package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.Arrays;
import java.util.List;


/*
  Big question: Are we hitting the holdingPosition condition in the follower.update() function even after isBusy is false:
    https://github.com/Pedro-Pathing/PedroPathing/blob/v2.0.1/core/src/main/java/com/pedropathing/follower/Follower.java#L404

  I don't see why calling one of the follower.turn() functions would make this work any better. That just seems to keep
  isBusy and isTurning set to true until complete, but the behavior of both should be the same as long as follower.update()
  continues to be called.

  We want to test with start and end poses that are equivalent, very similar, and very different.
 */


@Autonomous(group="EBDecodeTest")
public class EBDecodeAutonPedroTest extends EBDecodeAutonPedro {
    public static final double START_HEADING = Math.toRadians(90);
    public static final double END_HEADING = Math.toRadians(0);

    public static final Pose START_POSE = new Pose(25, 75);
    public static final Pose END_POSE = new Pose(30, 75);  // TODO: also try START = END

    private final ElapsedTime correctHeadingTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);


    @Override
    public void correctHeading(int timeoutMs) {
        // Eventually we'd want to break out once heading error and velocity are both very low.
        correctHeadingTimer.reset();
        while (correctHeadingTimer.milliseconds() < timeoutMs) {
            follower.update();
            updateTelemetry();
        }
    }

    @Override
    public void updateTelemetry() {
        telemetry.addData("Path State", pathState);
        telemetry.addData("X Error (in)", "%.3f", Math.abs(END_POSE.getX() - follower.getPose().getX()));
        telemetry.addData("Y Error (in)", "%.3f", Math.abs(END_POSE.getY() - follower.getPose().getY()));
        telemetry.addData("Heading Error (rad)", "%.3f", Math.abs(END_HEADING - follower.getPose().getHeading()));
        telemetry.addData("Correction Timer (s)", "%.3f", Math.abs(correctHeadingTimer.seconds()));

        telemetry.addData("Follower isBusy", follower.isBusy());
        telemetry.addData("Follower isTurning", follower.isTurning());
        telemetry.addData("Follower Heading Error (rad)", "%.3f", Math.abs(follower.getHeadingError()));
        telemetry.addData("Follower Path T-Value", "%.3f", follower.getClosestPose().getTValue());

        telemetry.update();
    }

    @Override
    public boolean needToLeave() {
        return false;
    }

    @Override
    public List<PathChain> getPathList() {
        Paths paths = new Paths(follower);
        // TODO: Keep this updated with the names of each PathChain in class Paths below
        return Arrays.asList(
                paths.Path1  // start -> end
        );
    }

    @Override
    public void pathStateAction(int state) {
        if (state == 0) {
            // Start position
        } else if (state == 1) {
            // Final position
            correctHeading(5000);
        }
    }

    public static class Paths {
        public PathChain Path1;

        public Paths(Follower follower) {
            Path1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(START_POSE, END_POSE))
                    .setLinearHeadingInterpolation(START_HEADING, END_HEADING)
                    .build();
        }
    }
}
