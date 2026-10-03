package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/*
    To modify this Pedro Pathing Auton OpMode:
      - Customize a new path at https://visualizer.pedropathing.com/
      - Click the "</>" button and copy the generated code ("Class Only")
      - Replace the whole "public static class Paths" block below with the new code
 */
@Autonomous(group="EBBiobuzz")
public class EBBiobuzzAutonPath extends EBDecodeAutonPedroSummerTesting {
    @Override
    public PathChain getPathChain() {
        return (new Paths(follower)).MainChain;
    }

    @Override
    public void pathStateAction(int state) {
        // state 0: start position
        // state 1..6: end of Path 1..6
    }

    public static class Paths {
        public PathChain MainChain;

        public Paths(Follower follower) {
            MainChain = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(56.000, 8.000),
                                    new Pose(56.237, 28.639)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
                    .addPath(
                            new BezierLine(
                                    new Pose(56.237, 28.639),
                                    new Pose(10.991, 45.926)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
                    .addPath(
                            new BezierLine(
                                    new Pose(10.991, 45.926),
                                    new Pose(56.213, 28.578)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(90))
                    .addPath(
                            new BezierLine(
                                    new Pose(56.213, 28.578),
                                    new Pose(8.253, 8.081)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
                    .addPath(
                            new BezierLine(
                                    new Pose(8.253, 8.081),
                                    new Pose(57.091, 28.425)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(90))
                    .addPath(
                            new BezierLine(
                                    new Pose(57.091, 28.425),
                                    new Pose(8.333, 95.000)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
                    .build();
        }
    }
}
