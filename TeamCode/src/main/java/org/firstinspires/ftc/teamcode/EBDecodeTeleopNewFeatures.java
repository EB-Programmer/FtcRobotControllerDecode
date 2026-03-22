package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.Locale;

/*
 * This OpMode tests new robot features and hardware
 *
 * Hardware Required:
 *   colorSensor1
 *   ledIndicator
 *   webcam
 *
 * Controls for Gamepad 1:
 *   A:              Change LED indicator color
 *   X:              Toggle April Tag processor on/off
 *   DPad Up:        Adjust color sensor gain up
 *   DPad Down:      Adjust color sensor gain down
 *
 * Controls for Gamepad 2:
 *
 */

@TeleOp(group="EBDecode")
public class EBDecodeTeleopNewFeatures extends LinearOpMode {
    private AprilTagProcessor aprilTagProcessor = null;
    private VisionPortal visionPortal = null;
    private NormalizedColorSensor colorSensor1 = null;
    private Servo ledIndicator = null;

    private static final int LOOP_PERIOD = 20;  // milliseconds
    private static final double[] INDICATOR_COLOR_VALUES = {0.0, 0.277, 0.333, 0.388, 0.444, 0.5, 0.555, 0.611, 0.666, 0.722, 1.0};

    private AprilTagDetection aprilTagDetection = null;
    private int indicatorColorIdx = 0;
    private float colorSensorGain = 2.0f;
    private NormalizedRGBA colorData1 = null;
    private double colorDist1 = -1.0;


    @Override
    public void runOpMode() {
        // Initialize motors and servos
        initHardware();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            updateAprilTag();
            updateIndicator();
            updateTelemetry();

            sleep(LOOP_PERIOD);
        }

        termHardware();
    }

    public void initHardware() {
        colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        if (colorSensor1 instanceof SwitchableLight) {
            ((SwitchableLight)colorSensor1).enableLight(true);
        }

        ledIndicator = hardwareMap.get(Servo.class, "ledIndicator");

        // Initialize webcam and April Tag processor
        aprilTagProcessor = new AprilTagProcessor.Builder().build();
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "webcam"));
        builder.addProcessor(aprilTagProcessor);
        visionPortal = builder.build();

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press START.");
        telemetry.update();
    }

    public void termHardware() {
        visionPortal.close();
    }

    public void updateIndicator() {
        if (gamepad1.aWasPressed()) {
            indicatorColorIdx += 1;
            indicatorColorIdx = indicatorColorIdx % INDICATOR_COLOR_VALUES.length;
            ledIndicator.setPosition(INDICATOR_COLOR_VALUES[indicatorColorIdx]);
        }
    }

    public void updateColorSensor() {
        if (gamepad1.dpadUpWasPressed()) {
            // Only increase the gain by a small amount, since this loop will occur multiple times per second.
            colorSensorGain += 0.01f;
        } else if (gamepad1.dpadDownWasPressed() && colorSensorGain > 1) { // A gain of less than 1 will make the values smaller, which is not helpful.
            colorSensorGain -= 0.01f;
        }

        colorSensor1.setGain(colorSensorGain);
        colorData1 = colorSensor1.getNormalizedColors();

        if (colorSensor1 instanceof DistanceSensor) {
            colorDist1 = ((DistanceSensor) colorSensor1).getDistance(DistanceUnit.CM);
        }
    }

    public void updateAprilTag() {
        // Toggle April Tag processor
        if (gamepad1.xWasPressed()) {
            visionPortal.setProcessorEnabled(aprilTagProcessor, !visionPortal.getProcessorEnabled(aprilTagProcessor));
        }

        aprilTagDetection = null;
        List<AprilTagDetection> currentDetections = aprilTagProcessor.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            if (20 <= detection.id && detection.id <= 24) {
                aprilTagDetection = detection;
                break;
            }
        }
    }

    public void updateTelemetry() {
        telemetry.addLine("Gamepad1 Controls:");
        telemetry.addLine("  A:        Change LED indicator color");
        telemetry.addLine("  X:        Toggle April Tag processor on/off");
        telemetry.addLine("  Dpad Up:  Increase Color Sensor Gain");
        telemetry.addLine("  Dpad Up:  Decrease Color Sensor Gain");
        telemetry.addLine();

        String aprilTagDesc = "None";
        if (aprilTagDetection != null) {
            if (aprilTagDetection.id == 20) {
                aprilTagDesc = "BLUE GOAL";
            } else if (aprilTagDetection.id == 24) {
                aprilTagDesc = "RED GOAL";
            } else {
                aprilTagDesc = "OBELISK";
            }
            aprilTagDesc += String.format(
                    Locale.US, ": %.1f degrees, %.1f feet away",
                    aprilTagDetection.ftcPose.bearing, aprilTagDetection.ftcPose.range / 12);
        }

        telemetry.addData("April Tag Processor Running", visionPortal.getProcessorEnabled(aprilTagProcessor));
        telemetry.addData("April Tag", aprilTagDesc);
        telemetry.addData("Color Sensor Gain", "%.3f", colorSensorGain);
        telemetry.addLine()
                .addData("Red", "%.3f", colorData1.red)
                .addData("Green", "%.3f", colorData1.green)
                .addData("Blue", "%.3f", colorData1.blue);
        telemetry.addData("Proximity (cm)", "%.3f", colorDist1);
        telemetry.addData("LED Indicator Value", "%.3f", INDICATOR_COLOR_VALUES[indicatorColorIdx]);

        telemetry.update();
    }
}
