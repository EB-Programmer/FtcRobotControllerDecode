package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

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
 *   Right Trigger:  Shoot (hold)
 *   A:              Long Shot Mode
 *   B:              Short Shot Mode
 *   Y:              Sort (hold)
 *
 */

@TeleOp(group="EBDecode")
public class EBDecodeTeleop extends LinearOpMode {
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftRearDrive   = null;
    private DcMotor rightRearDrive = null;
    private DcMotor sorter = null;
    private DcMotor shooter = null;
    private CRServo lowerIntake = null;
    private CRServo upperIntake = null;
    private AprilTagProcessor aprilTag = null;
    private VisionPortal visionPortal = null;

    private static final double DRIVE_HIGH_POWER = 1.0;
    private static final double DRIVE_LOW_POWER = 0.4;
    private static final double SORTER_SORTING_POWER = -0.3;
    private static double SORTER_SHOOTING_POWER = 0.25;
    private static double SHOOTER_HIGH_VELOCITY = 1850;
    private static double SHOOTER_LOW_VELOCITY = 1450;
    private static final double INTAKE_POWER = 0.8;
    private static double INTAKE_LOW_POWER = 0.7;
    private static final int STUTTER_PERIOD = 360;  // milliseconds
    private static final int STUTTER_PAUSE_DURATION = 120;  // milliseconds
    private static final int LOOP_PERIOD = 20;  // milliseconds

    private ElapsedTime shooterWarmupTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    private boolean fastDriveMode = true;
    private boolean longShotMode = true;
    private boolean isIntaking = false;
    private boolean isOuttaking = false;
    private double frontLeftPower, frontRightPower, rearLeftPower, rearRightPower;
    private double targetShooterVelocity = 0;
    private double currentShooterVelocity = 0;
    private boolean shooterVelocityInRange = false;
    private int motifID = 0;

    @Override
    public void runOpMode() {
        // Initialize motors and servos
        initHardware();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            /*if(longShotMode) {
                SHOOTER_HIGH_VELOCITY = tuneConstant(
                        "Tuner: Shooter High Velocity", SHOOTER_HIGH_VELOCITY,
                        gamepad2.dpadUpWasPressed(), gamepad2.dpadDownWasPressed(),
                        25, 3000);
            } else {
                SHOOTER_LOW_VELOCITY = tuneConstant(
                        "Tuner: Shooter Low Velocity", SHOOTER_LOW_VELOCITY,
                        gamepad2.dpadUpWasPressed(), gamepad2.dpadDownWasPressed(),
                        25, 3000);
            }*/

            SORTER_SHOOTING_POWER = tuneConstant(
                    "Tuner: Sorter Shooting Power", SORTER_SHOOTING_POWER,
                    gamepad2.dpadUpWasPressed(), gamepad2.dpadDownWasPressed(),
                    0.01, 1.0);

            INTAKE_LOW_POWER = tuneConstant(
                    "Tuner: Intake Shooting Power", INTAKE_LOW_POWER,
                    gamepad2.dpadRightWasPressed(), gamepad2.dpadLeftWasPressed(),
                    0.01, 1.0);

            drive();
            intake();
            shootWithStutter();
            sortColors();
            identifyMotif();
            updateTelemetry();

            sleep(LOOP_PERIOD);
        }
    }

    public double tuneConstant(String name, double value,
                               boolean button_up, boolean button_down,
                               double increment, double max_val) {
        if (button_up) {
            value = value + increment;
        } else if (button_down) {
            value = value - increment;
        }

        if (value > max_val) {
            value = max_val;
        } else if (value < 0) {
            value = 0;
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
        shooter.setDirection(DcMotor.Direction.FORWARD);
        //shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        sorter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lowerIntake = hardwareMap.get(CRServo.class, "lowerIntake");
        upperIntake = hardwareMap.get(CRServo.class, "upperIntake");
        lowerIntake.setDirection(DcMotor.Direction.FORWARD);
        upperIntake.setDirection(DcMotor.Direction.REVERSE);

        // Initialize webcam and April Tag processor
        aprilTag = new AprilTagProcessor.Builder().build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "webcam"));
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();

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

        double powerLimit = (fastDriveMode ? DRIVE_HIGH_POWER : DRIVE_LOW_POWER);

        // If turning during Low Power Mode: decrease max speed even more for fine-tune aiming
        if (!fastDriveMode && drive == 0 && strafe == 0) {
            powerLimit = powerLimit / 2;
        }

        // Normalize the values so neither exceed +/- speedLimit
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

    public void identifyMotif() {
        if (motifID == 0) {
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                if (21 <= detection.id && detection.id <= 23) {
                    motifID = detection.id;
                    visionPortal.stopStreaming();
                    break;
                }
            }
        }
    }

    public void sortColors() {
        boolean isShooting = (gamepad2.right_trigger > 0.25);
        boolean isSorting = gamepad2.y;
        if (isSorting && !isShooting) {
            sorter.setPower(SORTER_SORTING_POWER);
        } else if (isShooting == isSorting) {
            sorter.setPower(0);
        }
    }

    public void shootWithStutter() {
        // Check if LongShotMode is being toggled on or off
        if (gamepad2.a) {
            longShotMode = true;
        } else if (gamepad2.b) {
            longShotMode = false;
        }

        currentShooterVelocity = ((DcMotorEx)shooter).getVelocity();

        boolean isShooting = (gamepad2.right_trigger > 0.25);
        boolean isSorting = gamepad2.y;

        if (isShooting && !isSorting) {
            // Always power up the shooter motor if we are holding the shoot button
            targetShooterVelocity = (longShotMode ? SHOOTER_HIGH_VELOCITY : SHOOTER_LOW_VELOCITY);
            ((DcMotorEx)shooter).setVelocity(targetShooterVelocity);

            // Wait until shooter velocity is very close to target velocity
            if (0.95 * targetShooterVelocity < currentShooterVelocity
                && currentShooterVelocity < 1.00 * targetShooterVelocity) {
                shooterVelocityInRange = true;
            }

            // If shooter velocity later falls out of tolerance, pause the sorter and let the
            // shooter warm back up
            if (currentShooterVelocity < 0.95 * targetShooterVelocity) {
                if (shooterVelocityInRange) {
                    shooterWarmupTimer.reset();
                }
                shooterVelocityInRange = false;
            }

            // Power up the sorter motor only after shooter reaches target velocity
            // OR button has been held for 3+ seconds
            if (shooterVelocityInRange || shooterWarmupTimer.milliseconds() > 3000) {
                int time = (int) (System.currentTimeMillis() % STUTTER_PERIOD);
                if (time < STUTTER_PAUSE_DURATION) {
                    sorter.setPower(0);
                } else {
                    sorter.setPower(SORTER_SHOOTING_POWER);
                }
            } else {
                sorter.setPower(0);
            }
        } else {
            targetShooterVelocity = 0;
            shooterVelocityInRange = false;
            shooterWarmupTimer.reset();
            shooter.setPower(0);
        }

        // Only force sorter off if we are not shooting and also not sorting
        // (if both are set to true, we do neither)
        if (isShooting == isSorting) {
            sorter.setPower(0);
        }
    }

    public void intake() {
        boolean isShooting = (gamepad2.right_trigger > 0.25);

        if (gamepad2.rightBumperWasPressed()){
            // Toggle intake
            isIntaking = !isIntaking;
            isOuttaking = false;
        } else if (gamepad2.leftBumperWasPressed()){
            // Toggle outtake
            isOuttaking = !isOuttaking;
            isIntaking = false;
        }

        if (isOuttaking) {
            lowerIntake.setPower(-INTAKE_POWER);
            upperIntake.setPower(-INTAKE_POWER);
        } else if (isShooting) {
            lowerIntake.setPower(INTAKE_POWER);
            upperIntake.setPower(INTAKE_LOW_POWER);
        } else if (isIntaking) {
            lowerIntake.setPower(INTAKE_POWER);
            upperIntake.setPower(INTAKE_POWER);
        } else {
            lowerIntake.setPower(0);
            upperIntake.setPower(0);
        }
    }

    public void updateTelemetry() {
        // Send telemetry message with current state
        telemetry.addData("Motif ID", motifID);
        telemetry.addData("Fast Drive Mode", fastDriveMode);
        telemetry.addData("Long Shot Mode", longShotMode);
        //telemetry.addData("Shooter Warmup Timer", (int)shooterWarmupTimer.milliseconds());
        //telemetry.addData("Shooter Velocity In Range", shooterVelocityInRange);
        telemetry.addData("Current Shooter Velocity", currentShooterVelocity);
        telemetry.addData("Target Shooter Velocity", targetShooterVelocity);

        telemetry.update();
    }
}
