package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
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
        pathList = Arrays.asList(pathObj.Path1, pathObj.Path2);  // TODO: update with all names
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

    public static class Paths {
        public PathChain Path1;
        public PathChain Path2;
        public Paths(Follower follower) {
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
}
