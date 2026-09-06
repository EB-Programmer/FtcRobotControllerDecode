package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

/*
    To modify this Pedro Pathing Auton OpMode:
      - Customize a new path at https://visualizer.pedropathing.com/
      - Click the "</>" button and copy the generated code ("Class Only")
      - Replace the whole "public static class Paths" block below with the new code
 */
@Autonomous(group="EBDecode")
@Disabled
public class EBDecodeAutonPedroSummerTestingPath1 extends EBDecodeAutonPedroSummerTesting {
    @Override
    public PathChain getPathChain() {
        return (new Paths(follower)).MainChain;
    }

    @Override
    public void pathStateAction(int state) {
        if (state == 0) {
            // Start position: start warming up shooter
        } else if (state == 1) {
            // Shooting position: fix heading, shoot, reset sorter
        } else if (state == 2) {
            // Lined up for intake: fix heading, turn on intake
        } else if (state == 3) {
            // Post-intake: start warming up shooter, pause briefly to finish intake
        } else if (state == 4) {
            // Shooting position: fix heading, shoot, reset sorter
        } else if (state == 5) {
            // Lined up for intake: fix heading, turn on intake
        } else if (state == 6) {
            // Post-intake: start warming up shooter, pause briefly to finish intake
        } else if (state == 7) {
            // Post-intake-backup: do nothing
        } else if (state == 8) {
            // Shooting position: fix heading, shoot
        } else if (state == 9) {
            // Final position: still aligned for long shot
        }
    }

    public static class Paths {
        public PathChain MainChain;

        public Paths(Follower follower) {
            MainChain = follower.pathBuilder()
                    .addPath(
                            new BezierLine(
                                    new Pose(64.581, 29.873),
                                    new Pose(42.035, 85.130)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
                    .addPath(
                            new BezierLine(
                                    new Pose(42.035, 85.130),
                                    new Pose(64.235, 112.321)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0))
                    .addPath(
                            new BezierLine(
                                    new Pose(64.235, 112.321),
                                    new Pose(109.460, 72.629)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(36))
                    .build();
        }
    }
}
