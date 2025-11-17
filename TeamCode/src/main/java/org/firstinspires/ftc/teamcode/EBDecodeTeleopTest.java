package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * This OpMode executes a POV Game style Teleop for a direct drive robot
 * The code is structured as a LinearOpMode
 *
 * Controls for Gamepad 1:
 *   Left Stick:     Move forward & backward, strafe left & right
 *   Left Trigger:   Turn counterclockwise
 *   Right Trigger:  Turn clockwise
 *   A:              Fast Drive Mode
 *   B:              Slow Drive Mode
 *
 * Controls for Gamepad 2:
 *   Left Bumper:    Reverse intake
 *   Right Bumper:   Intake
 *   Right Trigger:  Shoot
 *   A:              Long Shot Mode
 *   B:              Short Shot Mode
 *   X:              Move Paddle for Shooting (tap)
 *   Y:              Move Paddle for Sorting (tap)
 *
 */

@TeleOp(group="EBDecodeTest")
@Disabled
public class EBDecodeTeleopTest extends LinearOpMode {
    /* Declare OpMode members:
     *   4 Drive Motors
     *   1 Color Sorter Motor
     *   1 Shooter Motor
     *   2 Intake Servos
     */
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftRearDrive   = null;
    private DcMotor rightRearDrive = null;
    private DcMotor sorter = null;
    private DcMotor shooter = null;  // TODO: Try DcMotorEx for shooter to get setVelocity method
    private CRServo lowerIntake = null;
    private CRServo upperIntake = null;

    private static final double DRIVE_HIGH_POWER = 1.0;
    private static final double DRIVE_LOW_POWER = 0.4;
    private static double SORTER_POWER = 0.4;
    private static final double SHOOTER_HIGH_POWER = 0.95;
    private static final double SHOOTER_LOW_POWER = 0.8;
    private static double SHOOTER_VELOCITY = 1000;
    private static final double INTAKE_POWER = 0.9;
    private static final int STUTTER_PERIOD = 160;  // milliseconds
    private static final int STUTTER_PAUSE_DURATION = 120;  // milliseconds
    private static final int LOOP_PERIOD = 20;  // milliseconds

    private ElapsedTime shooterWarmupTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    private boolean fastDriveMode = true;
    private boolean longShotMode = true;
    private boolean isIntaking = false;
    private boolean isOuttaking = false;
    private double frontLeftPower, frontRightPower, rearLeftPower, rearRightPower;
    private double shooterPower;

    @Override
    public void runOpMode() {
        // Initialize motors and servos
        initHardware();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            SORTER_POWER = tuneConstant(
                    "Tuning - Sorter Power", SORTER_POWER,
                    gamepad2.dpad_up, gamepad2.dpad_down, 0.01, 1.0);

            SHOOTER_VELOCITY = tuneConstant(
                    "Tuning - Shooter Velocity", SHOOTER_VELOCITY,
                    gamepad2.dpad_right, gamepad2.dpad_left, 10, 2000);

            drive();
            intake();
            shootWithStutter();
            //sortColors();
            testSorter();
            testSorterForShooting();
            updateTelemetry();

            //OdometryPods.update();
            sleep(LOOP_PERIOD);
        }
    }

    public double tuneConstant(String name, double value, boolean button_up, boolean button_down, double increment, double max_val) {
        if (button_up) {
            value = value + increment;
        } else if (button_down) {
            value = value - increment;
        }

        if (value > max_val) {
            value = max_val;
        } else if (value < 0) {
            value = 0;
        } else if (button_up || button_down) {
            sleep(50);
        }

        telemetry.addData(name, value);
        return value;
    }

    public void initHardware() {
        // Define and Initialize Motors
        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftRearDrive = hardwareMap.get(DcMotor.class, "leftRearDrive");
        rightRearDrive = hardwareMap.get(DcMotor.class, "rightRearDrive");
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftRearDrive.setDirection(DcMotor.Direction.REVERSE);
        rightRearDrive.setDirection(DcMotor.Direction.FORWARD);

        sorter = hardwareMap.get(DcMotor.class, "sorter");
        shooter = hardwareMap.get(DcMotor.class, "shooter");

        sorter.setDirection(DcMotor.Direction.FORWARD);
        shooter.setDirection(DcMotor.Direction.REVERSE);
        sorter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sorter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sorter.setTargetPosition(0);
        sorter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sorter.setPower(SORTER_POWER);

        lowerIntake = hardwareMap.get(CRServo.class, "lowerIntake");
        upperIntake = hardwareMap.get(CRServo.class, "upperIntake");
        lowerIntake.setDirection(DcMotor.Direction.FORWARD);
        upperIntake.setDirection(DcMotor.Direction.REVERSE);

        //OdometryPods.resetPosAndIMU();  // TODO: Add odometry offsets

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press START.");
        telemetry.update();
    }

    public void drive() {
        // Check if FastMode is being toggled on or off
        if (gamepad1.a) {
            fastDriveMode = true;
        } else if (gamepad1.b) {
            fastDriveMode = false;
        }

        // Run wheels in POV mode
        // The left stick moves the robot fwd/back and strafes left/right
        // The right stick turns the robot counterclockwise and clockwise
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn  =  gamepad1.right_trigger - gamepad1.left_trigger;

        // Combine drive, strafe, and turn for blended motion
        frontLeftPower = drive + strafe + turn;
        frontRightPower = drive - strafe - turn;
        rearLeftPower = drive - strafe + turn;
        rearRightPower = drive + strafe - turn;

        // Normalize the values so neither exceed +/- speedLimit
        double powerLimit = (fastDriveMode ? DRIVE_HIGH_POWER : DRIVE_LOW_POWER);
        double maxPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        maxPower = Math.max(Math.abs(maxPower), Math.abs(rearLeftPower));
        maxPower = Math.max(Math.abs(maxPower), Math.abs(rearRightPower));
        if (maxPower > powerLimit) {
            frontLeftPower /= maxPower;
            frontRightPower /= maxPower;
            rearLeftPower /= maxPower;
            rearRightPower /= maxPower;
            frontLeftPower *= powerLimit;
            frontRightPower *= powerLimit;
            rearLeftPower *= powerLimit;
            rearRightPower *= powerLimit;
        }

        // Output the safe vales to the motor drives
        leftFrontDrive.setPower(frontLeftPower);
        rightFrontDrive.setPower(frontRightPower);
        leftRearDrive.setPower(rearLeftPower);
        rightRearDrive.setPower(rearRightPower);
    }

    /*public void sortColors() {
        boolean isShooting = (gamepad2.right_trigger > 0.25);
        boolean isSorting = gamepad2.y;
        if (isSorting && !isShooting) {
            sorter.setPower(SORTER_SORTING_POWER);
        } else if (isShooting == isSorting) {
            sorter.setPower(0);
        }
    }*/

    // Counterclockwise = shooting = positive direction
    // X and Y do the same thing
    // positive direction 1/3 rotation twice, then backward 2/3 rotation
    public void testSorter() {
        // Tap Y to move sorter paddle 1/3 rotation clockwise
        // TODO: test various speeds
        // TODO: still need stutter?
        // TODO: works for shooting and sorting?
        // TODO: shoot method likely needs to cancel active sort if they conflict

        boolean isSorting = sorter.isBusy();
        boolean sortRequested = gamepad2.yWasPressed();
        if (sortRequested && !isSorting) {
            double currentPosition = sorter.getCurrentPosition();
            double targetPosition = currentPosition - (384.5 / 3);
            double targetIndex = Math.round(targetPosition / (384.5 / 3));
            targetPosition = targetIndex * (384.5 / 3);
            sorter.setPower(SORTER_POWER / 2);
            sorter.setTargetPosition((int) Math.round(targetPosition));
        } else if (isSorting) {
            int time = (int) (System.currentTimeMillis() % STUTTER_PERIOD);
            if (time < STUTTER_PAUSE_DURATION) {
                sorter.setPower(0);
            } else {
                sorter.setPower(SORTER_POWER / 2);
            }
        }
    }



    public void testSorterForShooting() {
        // Tap X to move sorter paddle 1/3 rotation counterclockwise

        boolean isSorting = sorter.isBusy();
        boolean sortRequested = gamepad2.xWasPressed();
        if (sortRequested && !isSorting) {
            double currentPosition = sorter.getCurrentPosition();
            double targetPosition = currentPosition + (384.5 / 3);
            double targetIndex = Math.round(targetPosition / (384.5 / 3));
            targetPosition = targetIndex * (384.5 / 3);
            sorter.setPower(SORTER_POWER);
            sorter.setTargetPosition((int) Math.round(targetPosition));
        } else if (isSorting) {
            int time = (int) (System.currentTimeMillis() % STUTTER_PERIOD);
            if (time < STUTTER_PAUSE_DURATION) {
                sorter.setPower(0);
            } else {
                sorter.setPower(SORTER_POWER);
            }
        }
    }




    public void shootWithStutter() {
        // Check if LongShotMode is being toggled on or off
        if (gamepad2.a) {
            longShotMode = true;
        } else if (gamepad2.b) {
            longShotMode = false;
        }

        boolean isShooting = (gamepad2.right_trigger > 0.25);
        //boolean isSorting = gamepad2.y;
        boolean isSorting = false;
        if (isShooting && !isSorting) {
            // Always power up the shooter motor if we are holding the shoot button
            shooterPower = (longShotMode ? SHOOTER_HIGH_POWER : SHOOTER_LOW_POWER);
            if (longShotMode) {
                shooter.setPower(shooterPower);
            } else {
                ((DcMotorEx) shooter).setVelocity(SHOOTER_VELOCITY);
            }
        } else if (isShooting == isSorting) {
            shooterPower = 0;
            shooter.setPower(shooterPower);
        }

        if (shooterPower == 0) {
            // Whenever the shooter power is set to 0, we'll need to reset our warmup timer
            shooterWarmupTimer.reset();
        }
    }

    public void intake() {
        //boolean isShooting = (gamepad2.right_trigger > 0.25);

        if (gamepad2.rightBumperWasPressed()){
            isIntaking = !isIntaking;
            if (isIntaking) {
                isOuttaking = false;
            }
        } else if (gamepad2.leftBumperWasPressed()){
            isOuttaking = !isOuttaking;
            if (isOuttaking) {
                isIntaking = false;
            }
        }

        if (isOuttaking && !isIntaking) {
            lowerIntake.setPower(-INTAKE_POWER);
            upperIntake.setPower(-INTAKE_POWER);
        } else if (isIntaking && !isOuttaking) {
            lowerIntake.setPower(INTAKE_POWER);
            upperIntake.setPower(INTAKE_POWER);
        } else {
            lowerIntake.setPower(0);
            upperIntake.setPower(0);
        }
    }

    public void updateTelemetry() {
        telemetry.addData("Fast Drive Mode", fastDriveMode);
        telemetry.addData("Long Shot Mode", longShotMode);
        telemetry.addData("Shooter Power", shooterPower);

        telemetry.addData("Sorter Current Position", sorter.getCurrentPosition());
        telemetry.addData("Sorter Target Position", sorter.getTargetPosition());
        telemetry.addData("Sorter Busy", sorter.isBusy());
        telemetry.addData("Sorter Is Braking",
                (sorter.getZeroPowerBehavior() == DcMotor.ZeroPowerBehavior.BRAKE));
        telemetry.addData("Motor Velocity", ((DcMotorEx)shooter).getVelocity());

        telemetry.update();
    }
}
