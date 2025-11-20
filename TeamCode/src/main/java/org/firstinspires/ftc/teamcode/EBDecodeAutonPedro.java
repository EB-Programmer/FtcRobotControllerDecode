package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.List;

@Autonomous(group="EBDecodeTest")
public class EBDecodeAutonPedro extends EBDecodeAuton {
    public Follower follower;
    public List<PathChain> pathList;
    public int pathState;

    @Override
    public void auton() {
        follower = Constants.createFollower(hardwareMap);
        pathList = getPathList();
        follower.setStartingPose(pathList.get(0).getPath(0).getPose(0));

        while (pathState < pathList.size() || follower.isBusy()) {
            follower.update();
            autonomousPathUpdate();

            telemetry.addData("Path State", pathState);
            telemetry.addData("X", follower.getPose().getX());
            telemetry.addData("Y", follower.getPose().getY());
            telemetry.addData("Heading", follower.getPose().getHeading());
            telemetry.update();
        }
    }

    public List<PathChain> getPathList() {
        return new ArrayList<PathChain>();
    }

    public void pathStateAction(int state) {
        // Override this method to customize actions between each path
        if (state < pathList.size()) {
            intake(true);
        } else {
            intake(false);
        }
        sleep(500);
    }

    public boolean shouldFlipPath() {
        // Override this method for OpModes that want to flip all of its Paths red<->blue
        return false;
    }

    public double flipAngle(double a) {
        // Reflect angle across vertical line
        // e.g. 85 -> 95; -100 -> -80 (but in radians not degrees)
        double vertDiff = Math.abs(a) - Math.toRadians(90);
        double resAbs = Math.toRadians(90) + Math.abs(vertDiff) * (vertDiff < 0 ? 1 : -1);
        return resAbs * (a < 0 ? -1 : 1);
    }

    public PathChain getPathChain(int idx) {
        PathChain pathChain = pathList.get(idx);
        if (shouldFlipPath()) {
            Pose startPose = pathChain.getPose(new PathChain.PathT(0, 0));
            Pose endPose = pathChain.getPose(new PathChain.PathT(0, 1));
            double startHeading = pathChain.getHeadingGoal(new PathChain.PathT(0, 0));
            double endHeading = pathChain.getHeadingGoal(new PathChain.PathT(0, 1));
            return follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(144 - startPose.getX(), startPose.getY()),
                                    new Pose(144 - endPose.getX(), endPose.getY())
                            )
                    )
                    .setLinearHeadingInterpolation(
                            flipAngle(startHeading),
                            flipAngle(endHeading)
                    )
                    .build();
        } else {
            return pathChain;
        }
    }

    public void autonomousPathUpdate() {
        if (!follower.isBusy() && pathState <= pathList.size()) {
            pathStateAction(pathState);
            if (pathState < pathList.size()) {
                follower.followPath(getPathChain(pathState), 0.5, true);
            }
            pathState += 1;
        }
    }

    public void correctHeading() {
        if (pathState <= 0 || pathState > pathList.size()) {
            return;
        }
        PathChain pathChain = getPathChain(pathState - 1);
        double goalHeading = pathChain.endPoint().getHeading();
        Pose currentPose = follower.getPose();
        // TODO: exit early if current heading is close enough to goal heading?
        // TODO: OR break loop below when heading is within half a degree or so
        Pose goalPose = currentPose.setHeading(goalHeading);
        PathChain correctionPath = follower
                .pathBuilder()
                .addPath(new BezierLine(currentPose, goalPose))
                .setLinearHeadingInterpolation(currentPose.getHeading(), goalHeading)
                .build();
        follower.followPath(correctionPath, 0.3, true);
        while (follower.isBusy()) {
            sleep(LOOP_PERIOD);
        }
    }
}
