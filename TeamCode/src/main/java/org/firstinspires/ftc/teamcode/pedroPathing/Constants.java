package org.firstinspires.ftc.teamcode.pedroPathing;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.ftc.localization.constants.OTOSConstants;
import com.pedropathing.ftc.localization.Encoder;


public class Constants {
    // TODO: Need to re-measure weight
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(10);

    // TODO: Update motor names? Adjust max power?
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(0.5)
            .rightFrontMotorName("Front_right")
            .rightRearMotorName("Back_right")
            .leftRearMotorName("Back_left")
            .leftFrontMotorName("Front_left")
            .leftFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .leftRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightRearMotorDirection(DcMotorSimple.Direction.REVERSE);

    // TODO: Use this block if we decide to only use the drive motor encoders
    /*public static DriveEncoderConstants localizerConstants = new DriveEncoderConstants()
            .rightFrontMotorName("Front_right")
            .rightRearMotorName("Back_right")
            .leftRearMotorName("Back_left")
            .leftFrontMotorName("Front_left")
            .leftFrontEncoderDirection(Encoder.FORWARD)
            .leftRearEncoderDirection(Encoder.FORWARD)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .rightRearEncoderDirection(Encoder.FORWARD)
            .robotLength(13.25 )
            .robotWidth(8.5);*/

    // TODO: Use this block if we decide to use the SparkFun OTOS
    /*public static OTOSConstants localizerConstants = new OTOSConstants()
            .hardwareMapName("otos")
            //The y axis is the left/right axis, and the x axis is the forward/backward axis.
            //Left is positive y and forward is positive x.
            //Facing forward is PI/2 radians or 90 degrees, and clockwise rotation is negative.
            //.linearScalar(25.61)
            .linearScalar(1150)
            .angularScalar(0.982)
            .offset(new SparkFunOTOS.Pose2D(-5.5, 0.25, 0))
            .linearUnit(DistanceUnit.INCH)
            .angleUnit(AngleUnit.RADIANS);*/

    // TODO: Use this block if we decide to use two dead wheels + built-in IMU
    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
            .forwardEncoder_HardwareMapName("Back_right")
            .strafeEncoder_HardwareMapName("Front_left")
            .forwardEncoderDirection(Encoder.FORWARD)
            .strafeEncoderDirection(Encoder.REVERSE)
            .forwardTicksToInches(0.002955)  // measured by forward tuner test
            .strafeTicksToInches(0.002955)  // measured by lateral tuner test
            .strafePodX(0)
            .forwardPodY(-3.25)
            .IMU_HardwareMapName("imu")
            .IMU_Orientation(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.LEFT
                    )
            );

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    // TODO: Should we try to do localization with two dead wheels + Pinpoint?
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                //.driveEncoderLocalizer(localizerConstants)
                //.OTOSLocalizer(localizerConstants)
                .twoWheelLocalizer(localizerConstants)
                .build();
    }
}
