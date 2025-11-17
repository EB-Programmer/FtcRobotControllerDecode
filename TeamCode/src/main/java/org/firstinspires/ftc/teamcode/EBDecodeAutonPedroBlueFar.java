package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Arrays;
import java.util.List;

@Autonomous(group="EBDecode")
public class EBDecodeAutonPedroBlueFar extends EBDecodeAutonPedro {
    @Override
    public List<PathChain> getPathList() {
        Paths paths = new Paths(follower);
        return Arrays.asList(
                paths.Path1,
                paths.Path2,
                paths.Path3,
                paths.Path4
        );
    }

    // Blue Near: Read obelisk(?) + shoot, pick up 3, shoot
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
