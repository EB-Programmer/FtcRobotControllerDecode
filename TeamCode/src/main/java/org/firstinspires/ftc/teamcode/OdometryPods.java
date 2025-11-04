/*   MIT License
 *   Copyright (c) [2024] [Base 10 Assets, LLC]
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:

 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.

 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.robotcore.external.ExportToBlocks;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.List;

public class OdometryPods extends BlocksOpModeCompanion {

    private static GoBildaPinpointDriver.DeviceStatus deviceStatus;
    private static int loopTime;
    private static int xEncoder;
    private static int yEncoder;
    private static double posX;
    private static double posY;
    private static double posH;
    private static double velX;
    private static double velY;
    private static double velH;



    public static double FourBarOdometryPod(DistanceUnit unit) {
        return unit.fromMm(19.89436789);
    }


    public static double SwingarmOdometryPod(DistanceUnit unit) {
        return unit.fromMm(13.26291192);
    }


    public static void update() {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            odo.update();
            deviceStatus = odo.getDeviceStatus();
            loopTime = odo.getLoopTime();
            xEncoder = odo.getEncoderX();
            yEncoder = odo.getEncoderY();
            posX = odo.getPosX();
            posY = odo.getPosY();
            posH = odo.getHeading();
            velX = odo.getVelX();
            velY = odo.getVelY();
            velH = odo.getHeadingVelocity();
        }
    }


    public static void reverseEncoders(boolean xEncoder, boolean yEncoder) {
        GoBildaPinpointDriver.EncoderDirection xEn = null;
        GoBildaPinpointDriver.EncoderDirection yEn = null;

        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            if (xEncoder) {
                xEn = GoBildaPinpointDriver.EncoderDirection.REVERSED;
            } else if (!xEncoder) {
                xEn = GoBildaPinpointDriver.EncoderDirection.FORWARD;
            }

            if (yEncoder) {
                yEn = GoBildaPinpointDriver.EncoderDirection.REVERSED;
            } else if (!yEncoder) {
                yEn = GoBildaPinpointDriver.EncoderDirection.FORWARD;
            }
            odo.setEncoderDirections(xEn, yEn);
        }
    }


    public static void encoderResolution(double res, DistanceUnit unit) {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            odo.setEncoderResolution(unit.toMm(res));
        }
    }


    public static void offsets(DistanceUnit unit, double xOffset, double yOffset) {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            odo.setOffsets(unit.toMm(xOffset), unit.toMm(yOffset));
        }
    }


    public static void resetPosAndIMU() {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            odo.resetPosAndIMU();
        }
    }


    public static void recalibrateIMU() {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            odo.recalibrateIMU();
        }
    }


    public static void yawScalar(double yawScalar) {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            odo.setYawScalar(yawScalar);
        }
    }


    public static void position(DistanceUnit distanceUnit, double x, double y, AngleUnit angleUnit, double h) {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            odo.setPosition(new Pose2D(distanceUnit, x, y, angleUnit, h));
        }
    }


    public static int deviceVersion() {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            return odo.getDeviceVersion();
        } else {
            return 0;
        }
    }


    public static float yawScalar() {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            return odo.getYawScalar();
        } else {
            return 0;
        }
    }


    public static String deviceStatus() {
        if (deviceStatus == GoBildaPinpointDriver.DeviceStatus.READY) {
            return "READY";
        } else if (deviceStatus == GoBildaPinpointDriver.DeviceStatus.NOT_READY) {
            return "NOT_READY";
        } else if (deviceStatus == GoBildaPinpointDriver.DeviceStatus.CALIBRATING) {
            return "CALIBRATING";
        } else if (deviceStatus == GoBildaPinpointDriver.DeviceStatus.FAULT_NO_PODS_DETECTED) {
            return "FAULT_NO_PODS_DETECTED";
        } else if (deviceStatus == GoBildaPinpointDriver.DeviceStatus.FAULT_X_POD_NOT_DETECTED) {
            return "FAULT_X_POD_NOT_DETECTED";
        } else if (deviceStatus == GoBildaPinpointDriver.DeviceStatus.FAULT_Y_POD_NOT_DETECTED) {
            return "FAULT_Y_POD_NOT_DETECTED";
        } else {
            return "PINPOINT_NOT_DETECTED";
        }
    }


    public static int loopTime() {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            return odo.getLoopTime();
        } else {
            return 0;
        }
    }


    public static double frequency() {
        List<GoBildaPinpointDriver> pinpoints;
        pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
        if (!pinpoints.isEmpty()) {
            GoBildaPinpointDriver odo = pinpoints.get(0);
            return odo.getFrequency();
        } else {
            return 0;
        }
    }


    public static double xPosition(DistanceUnit unit) {
        return unit.fromMm(posX);
    }


    public static double yPosition(DistanceUnit unit) {
        return unit.fromMm(posY);
    }


    public static double orientation(AngleUnit unit) {
        return unit.fromRadians(posH);
    }


    public static double xVelocity(DistanceUnit unit) {
        return unit.fromMm(velX);
    }


    public static double yVelocity(DistanceUnit unit) {
        return unit.fromMm(velY);
    }


    public static double headingVelocity(AngleUnit unit) {
        return unit.fromRadians(velH);
    }


    public static int xEncoder() {
        return xEncoder;
    }


    public static int yEncoder() {
        return yEncoder;
    }

}
