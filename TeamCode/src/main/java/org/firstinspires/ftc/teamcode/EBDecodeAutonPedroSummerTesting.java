package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(group="EBDecodeTest")
public class EBDecodeAutonPedroSummerTesting extends EBDecodeAuton {
    public Follower follower;

    public PathChain pathChain;

    public int pathState;

    @Override
    public void auton() {
        pathState = 0;
        follower = Constants.createFollower(hardwareMap);
        pathChain = getPathChain();
        follower.setStartingPose(getPath(0).getPose(0));

        while (pathState <= pathChain.size() || follower.isBusy()) {
            follower.update();
            autonomousPathUpdate();
            updateTelemetry();
        }
    }

    @Override
    public void updateTelemetry() {
        telemetry.addData("Path State", pathState);
        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading", follower.getPose().getHeading());
        telemetry.addData("Follower isBusy", follower.isBusy());
        telemetry.update();
    }

    public PathChain getPathChain() {
        return null;
    }

    public void pathStateAction(int state) {
        // Override this method to customize actions between each path
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

    public Path getPath(int idx) {
        Path path = pathChain.getPath(idx);
        if (shouldFlipPath()) {
            Pose startPose = path.getPose(0);
            Pose endPose = path.getPose(1);
            double startHeading = path.getHeadingGoal(0);
            double endHeading = path.getHeadingGoal(1);
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
                    .build()
                    .getPath(0);
        } else {
            return path;
        }
    }

    public void autonomousPathUpdate() {
        if (!follower.isBusy() && pathState <= pathChain.size()) {
            // Check if OpMode wants to do something special when we've reached this pathState
            // pathState == 0:    at the initial location
            // pathState == 1:    at end of Path1
            // ...
            // pathState == size: at end of final Path
            pathStateAction(pathState);

            // Start next PathChain segment if necessary
            if (pathState < pathChain.size()) {
                follower.followPath(new PathChain(getPath(pathState)), 1.0, true);
            }

            pathState += 1;
        }
    }
}
