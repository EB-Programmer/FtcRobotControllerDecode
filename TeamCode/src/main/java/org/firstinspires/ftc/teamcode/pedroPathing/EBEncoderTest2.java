package org.firstinspires.ftc.teamcode.pedroPathing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(group="EBDecodeTest")
public class EBEncoderTest2 extends LinearOpMode {

    // Replace these names with your actual hardware map names
    private static final String FORWARD_ENCODER_NAME = "Back_right";  // current forward encoder
    private static final String STRAFE_ENCODER_NAME = "Front_left";   // current strafe encoder
    private static final String IMU_NAME = "imu";                     // your IMU name in hardwareMap

    private DcMotor forwardEncoder;
    private DcMotor strafeEncoder;
    private IMU imu;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize encoders
        forwardEncoder = hardwareMap.get(DcMotor.class, FORWARD_ENCODER_NAME);
        strafeEncoder = hardwareMap.get(DcMotor.class, STRAFE_ENCODER_NAME);

        // Initialize IMU with your current hub mounting orientation
        imu = hardwareMap.get(IMU.class, IMU_NAME);
        RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT // change to RIGHT if USB points right when viewed from front
        );
        imu.initialize(new IMU.Parameters(orientation));

        // Reset encoders
        forwardEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        strafeEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        forwardEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        strafeEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addLine("🚦 Encoder + IMU Test Ready");
        telemetry.addLine("Move the robot and watch telemetry:");
        telemetry.addLine(" - Move forward → Forward encoder increases (X+)");
        telemetry.addLine(" - Strafe left → Strafe encoder increases (Y+)");
        telemetry.addLine(" - Rotate CCW → Heading increases (deg)");
        telemetry.addLine();
        telemetry.addLine("Press A to reset encoders and heading");
        telemetry.update();

        waitForStart();
        imu.resetYaw();

        while (opModeIsActive()) {
            int forwardPos = forwardEncoder.getCurrentPosition();
            int strafePos = strafeEncoder.getCurrentPosition();
            double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);

            telemetry.addData("Forward Encoder (" + FORWARD_ENCODER_NAME + ")", forwardPos);
            telemetry.addData("Strafe Encoder (" + STRAFE_ENCODER_NAME + ")", strafePos);
            telemetry.addData("Heading (° CCW+)", "%.1f", heading);
            telemetry.addLine();
            telemetry.addLine("✔ Forward → Forward encoder should increase");
            telemetry.addLine("✔ Strafe Left → Strafe encoder should increase");
            telemetry.addLine("✔ Rotate CCW → Heading should increase");
            telemetry.addLine("✖ If reversed, adjust directions in Constants.java or IMU orientation");
            telemetry.update();

            // Optional: Press A to reset encoders and IMU heading
            if (gamepad1.a) {
                forwardEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                strafeEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                forwardEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                strafeEncoder.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                imu.resetYaw();
            }
        }
    }
}
