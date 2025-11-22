package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

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
public class EBDecodeTeleopShooterTest extends LinearOpMode {
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftRearDrive   = null;
    private DcMotor rightRearDrive = null;
    private DcMotor sorter = null;
    private DcMotor shooter = null;
    private CRServo lowerIntake = null;
    private CRServo upperIntake = null;

    private static final int LOOP_PERIOD = 20;  // milliseconds

    private ElapsedTime shooterWarmupTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    private boolean longShotMode = true;
    private double targetShooterVelocity = 0;
    private double currentShooterVelocity = 0;
    private boolean shooterVelocityInRange = false;

    @Override
    public void runOpMode() {
        // Initialize motors and servos
        initHardware();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            shootWithStutter();
            updateTelemetry();

            sleep(LOOP_PERIOD);
        }
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
        sorter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lowerIntake = hardwareMap.get(CRServo.class, "lowerIntake");
        upperIntake = hardwareMap.get(CRServo.class, "upperIntake");
        lowerIntake.setDirection(DcMotor.Direction.FORWARD);
        upperIntake.setDirection(DcMotor.Direction.REVERSE);

        // Initialize webcam and April Tag processor
        /*aprilTag = new AprilTagProcessor.Builder().build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "webcam"));
        builder.addProcessor(aprilTag);
        visionPortal = builder.build();*/

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press START.");
        telemetry.update();
    }

    public void shootWithStutter() {
        shooter.setPower(1.);
    }

    public void updateTelemetry() {
        // Send telemetry message with current state
        //telemetry.addData("Motif ID", motifID);
        telemetry.addData("Long Shot Mode", longShotMode);
        telemetry.addData("Shooter Warmup Timer", (int)shooterWarmupTimer.milliseconds());
        telemetry.addData("Shooter Velocity In Range", shooterVelocityInRange);
        telemetry.addData("Current Shooter Velocity", currentShooterVelocity);
        telemetry.addData("Target Shooter Velocity", targetShooterVelocity);


        telemetry.update();
    }
}
