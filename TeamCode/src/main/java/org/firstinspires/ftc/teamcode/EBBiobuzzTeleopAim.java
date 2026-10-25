package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

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
 *   Right Trigger:  Shoot (hold)
 *   Dpad Up/Down:   Increase / decrease shooter power
 *   Y:              Auto-aim at the HIVE AprilTags (hold). Overrides turning from gamepad 1
 *   B:              Toggle alliance (Red / Blue). Can be pressed during INIT
 *
 */

@TeleOp(group="EBBiobuzz")
public class EBBiobuzzTeleopAim extends LinearOpMode {
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftRearDrive   = null;
    private DcMotor rightRearDrive = null;
    private DcMotorEx shooter = null;
    private Limelight3A limelight = null;

    private static final double DRIVE_HIGH_POWER = 1.0;
    private static final double DRIVE_LOW_POWER = 0.4;
    private static double SHOOTER_POWER = 0.5;
    private static final double SHOOTER_TICKS_PER_REV = 384.5;
    private static final int LOOP_PERIOD = 20;  // milliseconds

    // AprilTag IDs on the HIVE CELLS: red 30-37, blue 38-45
    private static final int RED_TAG_MIN_ID = 30;
    private static final int RED_TAG_MAX_ID = 37;
    private static final int BLUE_TAG_MIN_ID = 38;
    private static final int BLUE_TAG_MAX_ID = 45;
    private static final int APRILTAG_PIPELINE = 4;  // Limelight pipeline slot
    private static final double AIM_KP = 0.02;  // turn power per degree of tx
    private static final double AIM_MAX_TURN = 0.5;
    private static final double AIM_TOLERANCE_DEG = 1.0;
    private static final long MAX_RESULT_STALENESS = 100;  // milliseconds

    private boolean fastDriveMode = true;
    private double frontLeftPower, frontRightPower, rearLeftPower, rearRightPower;
    private boolean redAlliance = true;
    private int aimTagCount = 0;
    private double aimTx = 0;
    private boolean aimOverridesTurn = false;
    private double aimTurn = 0;

    @Override
    public void runOpMode() {
        // Initialize motors
        initHardware();

        // Wait for the game to start (driver presses START). Alliance can be chosen meanwhile.
        while (!isStarted() && !isStopRequested()) {
            toggleAlliance();
            telemetry.addData("Alliance", redAlliance ? "RED" : "BLUE");
            telemetry.addData(">", "Press gamepad2 B to change alliance. Press START.");
            telemetry.update();
            sleep(LOOP_PERIOD);
        }
        if (isStopRequested()) {
            return;
        }

        limelight.start();  // must call this before reading results

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            toggleAlliance();
            updateAim();
            SHOOTER_POWER = tuneConstant(
                    "Shooter Power (Max)", SHOOTER_POWER,
                    gamepad2.dpadUpWasPressed(), gamepad2.dpadDownWasPressed(),
                    0.05, 1.0);

            drive();
            shoot();
            updateTelemetry();

            sleep(LOOP_PERIOD);
        }

        limelight.stop();
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
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftRearDrive.setDirection(DcMotor.Direction.REVERSE);
        rightRearDrive.setDirection(DcMotor.Direction.FORWARD);

        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setDirection(DcMotor.Direction.FORWARD);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);  // how many times/sec to request data
        limelight.pipelineSwitch(APRILTAG_PIPELINE);

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press START.");
        telemetry.update();
    }

    public void toggleAlliance() {
        if (gamepad2.bWasPressed()) {
            redAlliance = !redAlliance;
        }
    }

    public boolean isAllianceTag(int id) {
        if (redAlliance) {
            return RED_TAG_MIN_ID <= id && id <= RED_TAG_MAX_ID;
        } else {
            return BLUE_TAG_MIN_ID <= id && id <= BLUE_TAG_MAX_ID;
        }
    }

    public void updateAim() {
        // Average the horizontal angle to every visible tag from our alliance's HIVE. Which CELL is
        // facing up changes after each HIVE TIP, but the tags we can see are the ones to aim at.
        aimTagCount = 0;
        aimTx = 0;
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid() && result.getStaleness() < MAX_RESULT_STALENESS) {
            double txSum = 0;
            List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();
            for (LLResultTypes.FiducialResult tag : tags) {
                if (isAllianceTag(tag.getFiducialId())) {
                    txSum += tag.getTargetXDegrees();
                    aimTagCount++;
                }
            }
            if (aimTagCount > 0) {
                aimTx = txSum / aimTagCount;
            }
        }

        // Positive tx means the target is to the right, so turn clockwise (positive turn)
        aimOverridesTurn = gamepad2.y && aimTagCount > 0;
        if (!aimOverridesTurn || Math.abs(aimTx) < AIM_TOLERANCE_DEG) {
            aimTurn = 0;
        } else {
            aimTurn = Math.max(-AIM_MAX_TURN, Math.min(AIM_MAX_TURN, AIM_KP * aimTx));
        }
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
        if (aimOverridesTurn) {
            // Auto-aim replaces the driver's turn input
            turn = aimTurn;
        }

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
        telemetry.addData("Alliance", redAlliance ? "RED" : "BLUE");
        telemetry.addData("Aim Tags Visible", aimTagCount);
        telemetry.addData("Aim tx (deg)", aimTx);
        telemetry.addData("Aim Active", aimOverridesTurn);

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
