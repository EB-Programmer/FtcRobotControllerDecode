package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.Arrays;
import java.util.List;


@Autonomous(group="EBDecodeTest")
public class EBDecodeAutonPedroPathTest extends EBDecodeAuton {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    private Follower follower;
    private Paths pathObj;
    private List<PathChain> pathList;
    private int pathState;

    @Override
    public void auton() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        follower = Constants.createFollower(hardwareMap);
        pathObj = new Paths(follower);
        pathList = Arrays.asList(  // TODO: update with all path member names
                pathObj.Path1,
                pathObj.Path2,
                pathObj.Path3,
                pathObj.Path4,
                pathObj.Path5
        );
        follower.setStartingPose(pathList.get(0).getPath(0).getPose(0));

        while (pathState < pathList.size() || follower.isBusy()) {
            follower.update();
            autonomousPathUpdate();

            panelsTelemetry.debug("Path State", pathState);
            panelsTelemetry.debug("X", follower.getPose().getX());
            panelsTelemetry.debug("Y", follower.getPose().getY());
            panelsTelemetry.debug("Heading", follower.getPose().getHeading());
            panelsTelemetry.update(telemetry);

            telemetry.addData("Path State", pathState);
            telemetry.addData("X", follower.getPose().getX());
            telemetry.addData("Y", follower.getPose().getY());
            telemetry.addData("Heading", follower.getPose().getHeading());
            telemetry.update();
        }
    }

    public void autonomousPathUpdate() {
        if (pathState < pathList.size()) {
            if (!follower.isBusy()) {
                sleep(1000);
                follower.followPath(pathList.get(pathState));
                pathState += 1;
            }
        }
    }

    // Blue Far: Pick up 3, shoot
    public static class PathsBlueFarDemo {
        public PathChain Path1;
        public PathChain Path2;
        public PathsBlueFarDemo(Follower follower) {
            Path1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(56.000, 8.000),
                                    new Pose(87.184, 37.420),
                                    new Pose(14.498, 36.049)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
                    .build();
            Path2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierCurve(
                                    new Pose(14.498, 36.049),
                                    new Pose(66.416, 38.008),
                                    new Pose(71.902, 23.314)
                            )
                    )
                    .setTangentHeadingInterpolation()
                    .setReversed()
                    .build();
        }
    }

    // Blue Near: Read obelisk, shoot, pick up 3, shoot
    public static class Paths {

        public PathChain Path1;
        public PathChain Path2;
        public PathChain Path3;
        public PathChain Path4;
        public PathChain Path5;

        public Paths(Follower follower) {
            Path1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(18.024, 119.314), new Pose(47.804, 95.608))
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(54))
                    .build();

            Path2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(47.804, 95.608), new Pose(53.600, 89.700))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(54), Math.toRadians(133))
                    .build();

            Path3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(53.600, 89.700), new Pose(45.257, 84.245))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(133), Math.toRadians(180))
                    .build();

            Path4 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(45.257, 84.245), new Pose(15.086, 84.049))
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Path5 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(15.086, 84.049), new Pose(53.600, 89.700))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(133))
                    .build();
        }
    }
}
