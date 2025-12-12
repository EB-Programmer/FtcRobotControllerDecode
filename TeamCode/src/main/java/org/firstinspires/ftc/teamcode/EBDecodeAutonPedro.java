package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.List;

@Autonomous(group="EBDecodeTest")
public class EBDecodeAutonPedro extends EBDecodeAuton {
    public Follower follower;
    public List<PathChain> pathList;
    public int pathState;

    public final int LEAVE_TIMEOUT = 28000;

    @Override
    public void auton() {
        follower = Constants.createFollower(hardwareMap);
        pathList = getPathList();
        follower.setStartingPose(getPathChain(0).getPath(0).getPose(0));

        while (pathState <= pathList.size() || follower.isBusy()) {
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
        telemetry.addData("Shot Count", shotCount);
        telemetry.addData("Follower isBusy", follower.isBusy());
        telemetry.addData("Sorter Position", sorter.getCurrentPosition());
        telemetry.addData("Sorter Position Mod", sorter.getCurrentPosition() % SORTER_TICKS);
        telemetry.addData("Sorter Target Position", sorterTargetPosition);
        telemetry.addData("Sorter isBusy", sorter.isBusy());
        telemetry.addData("Shooter Velocity", ((DcMotorEx)shooter).getVelocity());
        telemetry.addData("Shooter Velocity in Range", shooterVelocityInRange);
        telemetry.update();
    }

    public boolean needToLeave() {
        return timeUntilLeave() < 0;
    }

    public double timeUntilLeave() {
        return LEAVE_TIMEOUT - autonTimer.milliseconds();
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
        // If we are running out of time and we haven't started "leaving" yet, create a new path
        // to get us from our current pose to our planned final pose. Increment pathState.
        if (needToLeave() && pathState < pathList.size()) {
            follower.breakFollowing();
            pathState = pathList.size() - 1;
            PathChain lastPathChain = getPathChain(pathState);
            Pose endPose = lastPathChain.getPose(new PathChain.PathT(0, 1));
            double endHeading = lastPathChain.getHeadingGoal(new PathChain.PathT(0, 1));
            PathChain emergencyPathChain = follower
                    .pathBuilder()
                    .addPath(new BezierLine(follower.getPose(), endPose))
                    .setLinearHeadingInterpolation(follower.getHeading(), endHeading)
                    .build();
            follower.followPath(emergencyPathChain, 1.0, true);
            pathState += 1;
        }

        if (!follower.isBusy() && pathState <= pathList.size()) {
            // Check if OpMode wants to do something special when we've reached this pathState
            // pathState == 0:    at the initial location
            // pathState == 1:    at end of Path1
            // ...
            // pathState == size: at end of final Path
            pathStateAction(pathState);

            // Start next PathChain segment if necessary
            if (pathState < pathList.size()) {
                double maxPower = 1.0;
                PathChain pc = getPathChain(pathState);
                if (Math.round(pc.getPath(0).getHeadingGoal(0)) % 180 == 0
                    && Math.round(pc.getPath(0).getHeadingGoal(1)) % 180 == 0) {
                    // Move slower while picking up artifacts
                    maxPower = 0.85;
                } else if (pathState == 0) {
                    maxPower = 0.75;
                } else if (pathState == 3 || pathState == 7) {
                    // Move slower to line up shots
                    maxPower = 0.85;
                }
                follower.followPath(getPathChain(pathState), maxPower, true);
            }

            pathState += 1;
        }
    }

    @Override
    public void shoot(boolean longShot) {
        int duration = (int)Math.min(SHOOTER_DURATION, timeUntilLeave());
        super.shoot(longShot, duration);
    }

    public void correctHeading(int timeoutMs) {
        // TODO: not sure if this is working so just skipping most calls for now
        if (timeoutMs <= 300) {
            return;
        }

        // Pedro follower should try to "hold" the final position if you call update after
        // isBusy is no longer true
        for (int i = 0; i < timeoutMs / LOOP_PERIOD; i++) {
            // Don't waste time straightening out if we need to be "leaving"
            if (needToLeave() && pathState < pathList.size()) {
                break;
            }

            follower.update();
            sleep(LOOP_PERIOD);
        }
    }
}
