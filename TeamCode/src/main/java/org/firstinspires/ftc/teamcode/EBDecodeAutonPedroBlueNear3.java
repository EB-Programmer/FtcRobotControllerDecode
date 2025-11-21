package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Arrays;
import java.util.List;

@Autonomous(group="EBDecode")
public class EBDecodeAutonPedroBlueNear3 extends EBDecodeAutonPedroBlueNear9 {
    @Override
    public List<PathChain> getPathList() {
        Paths paths = new Paths(follower);
        return Arrays.asList(
                paths.Path1,  // start -> shoot
                //paths.Path2,  // shoot -> start intake 1
                //paths.Path3,  // start intake 1 -> end intake 1
                //paths.Path4,  // end intake 1 -> shoot
                //paths.Path5,  // shoot -> start intake 2
                //paths.Path6,  // start intake 2 -> end intake 2
                //paths.Path7,  // end intake 2 -> backup after intake 2
                //paths.Path8,  // backup after intake2 -> shoot
                //paths.Path9   // shoot -> LEAVE (near gate)
                follower.pathBuilder()  // shoot -> LEAVE (inside triangle)
                        .addPath(
                                new BezierLine(
                                        paths.Path1.getPose(new PathChain.PathT(0, 1)),
                                        new Pose(54, 120)
                                )
                        )
                        .setLinearHeadingInterpolation(SHOOT_HEADING, SHOOT_HEADING)
                        .build()
        );
    }

    @Override
    public void pathStateAction(int state) {
        if (state == 0) {
            // Start position: start warming up shooter
            warmupShooter(false);
        } else if (state == 1) {
            // Shooting position: fix heading, shoot, reset sorter
            correctHeading(200);
            shoot(false);
        } else if (state == 2) {
            // Final position: do nothing
            correctHeading(500);
        }
    }
}
