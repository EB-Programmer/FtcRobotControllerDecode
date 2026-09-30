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
    Source path file: biobuzz1.pp
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
        // state 1: end of Path 1
    }

    public static class Paths {
        public PathChain MainChain;

        public Paths(Follower follower) {
            MainChain = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(56.000, 8.000),
                                    new Pose(56.000, 36.000)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
                    .build();
        }
    }
}
