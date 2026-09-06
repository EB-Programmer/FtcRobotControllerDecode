package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

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
 *   colorSensor0, colorSensor1, colorSensor2
 *   ledIndicator0, ledIndicator1
 *   webcam
 *
 * Controls for Gamepad 1:
 *   X:               Toggle April Tag processor on/off
 *   DPad Up/Down:    Adjust color sensor gain up/down
 *   DPad Right/Left: Adjust artifact distance threshold up/down
 *
 * Controls for Gamepad 2:
 *
 */

@TeleOp(group="EBDecodeTest")
@Disabled
public class EBDecodeTeleopNewFeatures extends LinearOpMode {
    private static final double COLOR_OFF = 0.0;
    private static final double COLOR_RED = 0.277;
    private static final double COLOR_ORANGE = 0.333;
    private static final double COLOR_YELLOW = 0.388;
    private static final double COLOR_SAGE = 0.444;
    private static final double COLOR_GREEN = 0.5;
    private static final double COLOR_AZURE = 0.555;
    private static final double COLOR_BLUE = 0.611;
    private static final double COLOR_INDIGO = 0.666;
    private static final double COLOR_VIOLET = 0.722;
    private static final double COLOR_WHITE = 1.0;
    private static final double[] COLOR_STATE = {COLOR_GREEN, COLOR_YELLOW, COLOR_ORANGE, COLOR_RED};
    private static /*final*/ double ARTIFACT_DIST_THRESHOLD_CM = 2.0;
    private static /*final*/ float COLOR_SENSOR_GAIN = 2.0f;
    private static final int ARTIFACT_PRESENT_DEBOUNCE_MS = 500;

    private AprilTagProcessor aprilTagProcessor = null;
    private VisionPortal visionPortal = null;
    private NormalizedColorSensor[] colorSensors = {null, null, null};
    private Servo[] ledIndicators = {null, null};

    private static final int LOOP_PERIOD = 20;  // milliseconds

    private AprilTagDetection aprilTagDetection = null;
    private NormalizedRGBA[] colorData = {null, null, null};
    private double[] colorDists = {-1.0, -1.0, -1.0};
    private boolean[] artifactPresent = {false, false, false};
    private boolean[] prevArtifactPresent = {false, false, false};
    private ElapsedTime[] artifactPresentTimer = {null, null, null};


    @Override
    public void runOpMode() {
        // Initialize motors and servos
        initHardware();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            updateConstants();
            updateAprilTag();
            updateColorSensors();
            updateArtifactState();
            updateIndicators();
            updateTelemetry();

            sleep(LOOP_PERIOD);
        }

        termHardware();
    }

    public void initHardware() {
        for (int i = 0; i < colorSensors.length; i++) {
            colorSensors[i] = hardwareMap.get(NormalizedColorSensor.class, "colorSensor" + i);
            if (colorSensors[i] instanceof SwitchableLight) {
                ((SwitchableLight) colorSensors[i]).enableLight(true);
            }
        }

        for (int i = 0; i < ledIndicators.length; i++) {
            ledIndicators[i] = hardwareMap.get(Servo.class, "ledIndicator" + i);
        }

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

    public void updateConstants() {
        if (gamepad1.dpadUpWasPressed()) {
            // Only increase the gain by a small amount, since this loop will occur multiple times per second.
            COLOR_SENSOR_GAIN += 0.01f;
        } else if (gamepad1.dpadDownWasPressed() && COLOR_SENSOR_GAIN > 1) {
            // A gain of less than 1 will make the values smaller, which is not helpful.
            COLOR_SENSOR_GAIN -= 0.01f;
        }

        if (gamepad1.dpadRightWasPressed()) {
            ARTIFACT_DIST_THRESHOLD_CM += 0.1;
        } else if (gamepad1.dpadLeftWasPressed() && ARTIFACT_DIST_THRESHOLD_CM > 0.15) {
            ARTIFACT_DIST_THRESHOLD_CM -= 0.1;
        }
    }

    public void updateIndicators() {
        int artifactCount = 0;
        for (int i = 0; i < artifactPresent.length; i++) {
            if (artifactPresent[i]) {
                artifactCount += 1;
            }
        }

        for (int i = 0; i < ledIndicators.length; i++) {
            if ((artifactCount == 3) && (System.currentTimeMillis() % 200 < 100)) {
                // Blink when all 3 artifacts are present
                ledIndicators[i].setPosition(COLOR_OFF);
            } else {
                ledIndicators[i].setPosition(COLOR_STATE[artifactCount]);
            }
        }
    }

    public void updateArtifactState() {
        for (int i = 0; i < colorSensors.length; i++) {
            boolean curArtifactPresent = (colorDists[i] < ARTIFACT_DIST_THRESHOLD_CM);
            if (curArtifactPresent != prevArtifactPresent[i]) {
                prevArtifactPresent[i] = curArtifactPresent;
                artifactPresentTimer[i].reset();
            }
            if (prevArtifactPresent[i] != artifactPresent[i]
                    && artifactPresentTimer[i].milliseconds() > ARTIFACT_PRESENT_DEBOUNCE_MS) {
                artifactPresent[i] = prevArtifactPresent[i];
            }
        }
    }

    public void updateColorSensors() {
        for (int i = 0; i < colorSensors.length; i++) {
            colorSensors[i].setGain(COLOR_SENSOR_GAIN);
            colorData[i] = colorSensors[i].getNormalizedColors();

            if (colorSensors[i] instanceof DistanceSensor) {
                colorDists[i] = ((DistanceSensor) colorSensors[i]).getDistance(DistanceUnit.CM);
            }
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
        telemetry.addLine("  X:               Toggle April Tag processor on/off");
        telemetry.addLine("  Dpad Up/Down:    Adjust Color Sensor Gain Up/Down");
        telemetry.addLine("  Dpad Right/Left: Adjust Artifact Dist Threshold Up/Down");
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
        telemetry.addData("Color Sensor Gain", "%.2f", COLOR_SENSOR_GAIN);
        telemetry.addData("Artifact Dist Threshold (cm)", "%.1f", ARTIFACT_DIST_THRESHOLD_CM);
        for (int i = 0; i < colorSensors.length; i++) {
            telemetry.addLine()
                    .addData("R", "%.1f", colorData[i].red)
                    .addData("G", "%.1f", colorData[i].green)
                    .addData("B", "%.1f", colorData[i].blue)
                    .addData("Dist", "%.1f", colorDists[i])
                    .addData("Present(Instant)", prevArtifactPresent[i])
                    .addData("Present(State)", artifactPresent[i]);
        }
        telemetry.update();
    }
}
