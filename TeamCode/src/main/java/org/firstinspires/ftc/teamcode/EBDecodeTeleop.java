package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
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
 *   X:              Toggle sorter zero power behavior
 *   Y:              Sort
 *
 */

@TeleOp(group="EBDecode")
public class EBDecodeTeleop extends LinearOpMode {
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

    private static final double DRIVE_HIGH_POWER = 0.9;
    private static final double DRIVE_LOW_POWER = 0.6;
    private static final double SORTER_SORTING_POWER = -0.1;
    private static final double SORTER_SHOOTING_POWER = 0.4;
    private static final double SHOOTER_HIGH_POWER = 0.95;
    private static final double SHOOTER_LOW_POWER = 0.8;
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
            drive();
            intake();
            shootWithStutter();
            sortColors();
            updateTelemetry();

            //OdometryPods.update();
            sleep(LOOP_PERIOD);
        }
    }
/*
    public double tuneConstant(String name, double value, boolean button_up, boolean button_down) {
        if (button_up) {
            value = value + 0.01;
        } else if (button_down) {
            value = value - 0.01;
        }

        if (value > 1) {
            value = 1;
        } else if (value < 0) {
            value = 0;
        } else if (button_up || button_down) {
            sleep(50);
        }

        telemetry.addData(name, value);
        return value;
    }
*/
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

    public void sortColors() {
        if (gamepad2.xWasPressed()) {
            if (sorter.getZeroPowerBehavior() == DcMotor.ZeroPowerBehavior.BRAKE) {
                sorter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            } else {
                sorter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
        }

        boolean isShooting = (gamepad2.right_trigger > 0.25);
        boolean isSorting = gamepad2.y;
        if (isSorting && !isShooting) {
            sorter.setPower(SORTER_SORTING_POWER);
        } else if (isShooting == isSorting) {
            sorter.setPower(0);
        }
    }

    /*public void shootWithStutter() {
        boolean isShooting = (gamepad2.right_trigger > 0.25);
        if (gamepad2.b){
            //TODO 2 states for moving and not moving
            if (!sorterIsTicking) {
                sorterIsTicking = true;
                sorterIsWaiting = false;
            }
            if (sorterTickTimer.time() > 0.2) {
                // Move!
                sorterTickTimer.reset();
                int position = sorter.getCurrentPosition();
                sorter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                sorter.setTargetPosition((int) (position+384.5/30) % 384);
                sorter.setPower(0.5); // TODO: faster
            }

        } else {
            // TODO
        }
    }*/

    public void shootWithStutter() {
        // Check if LongShotMode is being toggled on or off
        if (gamepad2.a) {
            longShotMode = true;
        } else if (gamepad2.b) {
            longShotMode = false;
        }

        boolean isShooting = (gamepad2.right_trigger > 0.25);
        boolean isSorting = gamepad2.y;
        if (isShooting && !isSorting) {
            //isIntaking = false;
            // Always power up the shooter motor if we are holding the shoot button
            shooterPower = (longShotMode ? SHOOTER_HIGH_POWER : SHOOTER_LOW_POWER);
            shooter.setPower(shooterPower);

            // Power up the sorter motor only if the button has been held for 1+ seconds
            if (shooterWarmupTimer.milliseconds() > 1000) {
                int time = (int) (System.currentTimeMillis() % STUTTER_PERIOD);
                if (time < STUTTER_PAUSE_DURATION) {
                    sorter.setPower(0);
                } else {
                    sorter.setPower(SORTER_SHOOTING_POWER);
                }
            }
        } else if (isShooting == isSorting) {
            shooterPower = 0;
            sorter.setPower(0);
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
        // Send telemetry message with current state
        telemetry.addData("Gamepad1 Left Stick X", gamepad1.left_stick_x);
        telemetry.addData("Gamepad1 Left Stick Y", gamepad1.left_stick_y);
        telemetry.addData("Gamepad1 Right Stick X", gamepad1.right_stick_x);

        telemetry.addData("Gamepad2 Left Trigger", gamepad2.left_trigger);
        telemetry.addData("Gamepad2 Right Trigger", gamepad2.right_trigger);

        telemetry.addData("Front Left Power", frontLeftPower);
        telemetry.addData("Front Right Power", frontRightPower);
        telemetry.addData("Rear Left Power", rearLeftPower);
        telemetry.addData("Rear Right Power", rearRightPower);

        telemetry.addData("Fast Drive Mode", fastDriveMode);
        telemetry.addData("Long Shot Mode", longShotMode);
        telemetry.addData("Shooter Power", shooterPower);
        telemetry.addData("Sorter Is Braking",
                (sorter.getZeroPowerBehavior() == DcMotor.ZeroPowerBehavior.BRAKE));

        telemetry.update();
    }
}
