package org.firstinspires.ftc.teamcode;

import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Arrays;
import java.util.List;

@Autonomous(group="EBDecode")
public class EBDecodeAutonPedroBlueFar3 extends EBDecodeAutonPedroBlueNear9 {
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
                //paths.Path7,  // end intake 2 -> shoot
                paths.Path8  // shoot -> LEAVE (near opponent loading zone)
        );
    }

    @Override
    public void pathStateAction(int state) {
        if (state == 0) {
            // Start position: start warming up shooter
            warmupShooter(true);
        } else if (state == 1) {
            // Shooting position: fix heading, shoot, reset sorter
            correctHeading();
            shootWithStutter(true);
            resetSorter();
        } else if (state == 2) {
            // Final position: near opponent loading zone
            correctHeading();
        }
    }
}
