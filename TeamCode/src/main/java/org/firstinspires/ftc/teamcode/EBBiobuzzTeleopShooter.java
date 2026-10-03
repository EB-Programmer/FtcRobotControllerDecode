package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

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
 *   Right Trigger:  Shoot (hold)
 *   Dpad Up/Down:   Increase / decrease shooter power
 *
 */

@TeleOp(group="EBBiobuzz")
public class EBBiobuzzTeleopShooter extends LinearOpMode {
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftRearDrive   = null;
    private DcMotor rightRearDrive = null;
    private DcMotorEx shooter = null;

    private static final double DRIVE_HIGH_POWER = 1.0;
    private static final double DRIVE_LOW_POWER = 0.4;
    private static double SHOOTER_POWER = 0.5;
    private static final double SHOOTER_TICKS_PER_REV = 384.5;
    private static final int LOOP_PERIOD = 20;  // milliseconds

    private boolean fastDriveMode = true;
    private double frontLeftPower, frontRightPower, rearLeftPower, rearRightPower;

    @Override
    public void runOpMode() {
        // Initialize motors
        initHardware();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            SHOOTER_POWER = tuneConstant(
                    "Shooter Power (Max)", SHOOTER_POWER,
                    gamepad2.dpadUpWasPressed(), gamepad2.dpadDownWasPressed(),
                    0.05, 1.0);

            drive();
            shoot();
            updateTelemetry();

            sleep(LOOP_PERIOD);
        }
    }

    public double tuneConstant(String name, double value,
                               boolean buttonUp, boolean buttonDown,
                               double increment, double maxVal) {
        if (buttonUp) {
            value = value + increment;
        } else if (buttonDown) {
            value = value - increment;
        }

        // Round to avoid floating-point drift from repeated increments
        value = Math.round(value * 100) / 100.0;

        if (value > maxVal) {
            value = maxVal;
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

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setDirection(DcMotor.Direction.FORWARD);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

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
        // The left and right triggers turn the robot counterclockwise and clockwise
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

        // Normalize the values so neither exceed +/- powerLimit
        double maxPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(rearLeftPower));
        maxPower = Math.max(maxPower, Math.abs(rearRightPower));
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

        // Output the safe values to the motor drives
        leftFrontDrive.setPower(frontLeftPower);
        rightFrontDrive.setPower(frontRightPower);
        leftRearDrive.setPower(rearLeftPower);
        rightRearDrive.setPower(rearRightPower);
    }

    public void shoot() {
        if (gamepad2.right_trigger > 0.25) {
            shooter.setPower(SHOOTER_POWER);
        } else {
            shooter.setPower(0);
        }
    }

    public void updateTelemetry() {
        // Send telemetry message with current state
        telemetry.addData("Shooter Velocity (RPM)", shooter.getVelocity() * 60 / SHOOTER_TICKS_PER_REV);

        telemetry.addLine();
        telemetry.addData("Fast Drive Mode", fastDriveMode);

        telemetry.addLine();
        telemetry.addData("frontLeftPower", frontLeftPower);
        telemetry.addData("frontRightPower", frontRightPower);
        telemetry.addData("rearLeftPower", rearLeftPower);
        telemetry.addData("rearRightPower", rearRightPower);

        telemetry.addLine();
        telemetry.addData("gamepad1 LeftStick Y (-drive)", gamepad1.left_stick_y);
        telemetry.addData("gamepad1 LeftStick X (strafe)", gamepad1.left_stick_x);
        telemetry.addData("gamepad1 RightTrigger (turn)", gamepad1.right_trigger);
        telemetry.addData("gamepad1 LeftTrigger (-turn)", gamepad1.left_trigger);

        telemetry.update();
    }
}
