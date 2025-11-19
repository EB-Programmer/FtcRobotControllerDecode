package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Arrays;
import java.util.List;

/*
    To modify this Pedro Pathing Auton OpMode:
      - Customize a new path at https://visualizer.pedropathing.com/
      - Click the "</>" button and copy the generated code
      - Replace the whole "public static class Paths" block below with the new code
      - Update the return value of getPathList() below to list each Path member of the
        new code. E.g. if the new code has Path1..Path6, list each of these names inside
        of "return Arrays.asList()".
 */
@Autonomous(group="EBDecode")
public class EBDecodeAutonPedroBlueFar extends EBDecodeAutonPedro {
    @Override
    public List<PathChain> getPathList() {
        Paths paths = new Paths(follower);
        // TODO: Keep this updated with the names of each PathChain in class Paths below
        return Arrays.asList(
                paths.Path1,
                paths.Path2,
                paths.Path3,
                paths.Path4
        );
    }

    @Override
    public void pathStateAction(int state) {
        if (state == 0) {
            // nothing
        } else if (state == 1) {
            shoot(SHOOTER_HIGH_POWER);
            sleep(250);
        } else if (state == 2) {
            intake(true);
            sleep(250);
        } else if (state == 3) {
            sleep(250);
            intake(false);
        } else if (state == 4) {
            shoot(SHOOTER_HIGH_POWER);
        }
    }

    // Blue Far: Read obelisk(?) + shoot, pick up 3, shoot
    public static class Paths {
        public PathChain Path1;
        public PathChain Path2;
        public PathChain Path3;
        public PathChain Path4;

        public Paths(Follower follower) {
            Path1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(57.200, 8.600), new Pose(55.500, 19.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(111))
                    .build();

            Path2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(55.500, 19.000), new Pose(48.000, 36.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(111), Math.toRadians(180))
                    .build();

            Path3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(48.000, 36.000), new Pose(15.500, 36.000))
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Path4 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(15.500, 36.000), new Pose(55.500, 19.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(111))
                    .build();
        }
    }
}
