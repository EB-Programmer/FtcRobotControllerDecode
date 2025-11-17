package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Arrays;
import java.util.List;

@Autonomous(group="EBDecode")
public class EBDecodeAutonPedroBlueNear extends EBDecodeAutonPedro {
    @Override
    public List<PathChain> getPathList() {
        Paths paths = new Paths(follower);
        return Arrays.asList(
                paths.Path1,
                paths.Path2,
                paths.Path3,
                paths.Path4,
                paths.Path5
        );
    }

    // Blue Near: Read obelisk, shoot, pick up 3, shoot
    public static class Paths {
        public PathChain Path1;
        public PathChain Path2;
        public PathChain Path3;
        public PathChain Path4;
        public PathChain Path5;

        public Paths(Follower follower) {
            Path1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(19.000, 118.900), new Pose(48.000, 95.600))
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(54))
                    .build();

            Path2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(48.000, 95.600), new Pose(53.600, 89.700))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(54), Math.toRadians(131))
                    .build();

            Path3 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(53.600, 89.700), new Pose(48.000, 84.000))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(131), Math.toRadians(180))
                    .build();

            Path4 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(48.000, 84.000), new Pose(15.500, 84.000))
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .build();

            Path5 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(15.500, 84.000), new Pose(53.600, 89.700))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(131))
                    .build();
        }
    }
}
