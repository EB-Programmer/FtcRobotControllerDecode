package org.firstinspires.ftc.teamcode;

import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Arrays;
import java.util.List;

@Autonomous(group="EBDecode")
public class EBDecodeAutonPedroBlueNear6 extends EBDecodeAutonPedroBlueNear9 {
    @Override
    public List<PathChain> getPathList() {
        Paths paths = new Paths(follower);
        return Arrays.asList(
                paths.Path1,  // start -> shoot
                paths.Path2,  // shoot -> start intake 1
                paths.Path3,  // start intake 1 -> end intake 1
                paths.Path4,  // end intake 1 -> shoot
                //paths.Path5,  // shoot -> start intake 2
                //paths.Path6,  // start intake 2 -> end intake 2
                //paths.Path7,  // end intake 2 -> backup after intake 2
                //paths.Path8,  // backup after intake2 -> shoot
                paths.Path9   // shoot -> LEAVE (near gate)
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
            // Lined up for intake: fix heading, turn on intake
            correctHeading(100);
            intake(true);
        } else if (state == 3) {
            // Post-intake: start warming up shooter, pause briefly to finish intake
            warmupShooter(false);
            correctHeading(500);
        } else if (state == 4) {
            // Shooting position: fix heading, shoot, reset sorter
            correctHeading(200);
            shoot(false);
        } else if (state == 5) {
            // Final position: ready to open gate
            correctHeading(500);
        }
    }
}
