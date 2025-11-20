package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
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
public class EBDecodeAutonPedroBlueNear9 extends EBDecodeAutonPedro {
    public static final double START_HEADING = Math.toRadians(144);
    public static final double SHOOT_HEADING = Math.toRadians(129);
    public static final double INTAKE_HEADING = Math.toRadians(180);
    public static final double END_HEADING = Math.toRadians(0);

    public static final Pose START_POSE = new Pose(21.5, 123.2);
    public static final Pose SHOOT_POSE = new Pose(48, 96);
    public static final Pose PRE_INTAKE1_POSE = new Pose(53, 84);
    public static final Pose POST_INTAKE1_POSE = new Pose(15.5, 84);
    public static final Pose PRE_INTAKE2_POSE = new Pose(53, 60);
    public static final Pose POST_INTAKE2_POSE = new Pose(15.5, 60);
    public static final Pose END_POSE = new Pose(27, 76);

    @Override
    public List<PathChain> getPathList() {
        Paths paths = new Paths(follower);
        // TODO: Keep this updated with the names of each PathChain in class Paths below
        return Arrays.asList(
                paths.Path1,  // start -> shoot
                paths.Path2,  // shoot -> start intake 1
                paths.Path3,  // start intake 1 -> end intake 1
                paths.Path4,  // end intake 1 -> shoot
                paths.Path5,  // shoot -> start intake 2
                paths.Path6,  // start intake 2 -> end intake 2
                paths.Path7,  // end intake 2 -> shoot
                paths.Path8   // shoot -> LEAVE (near gate)
        );
    }

    @Override
    public void pathStateAction(int state) {
        if (state == 0) {
            // Start position: start warming up shooter
            warmupShooter(false);
        } else if (state == 1) {
            // Shooting position: fix heading, shoot, reset sorter
            correctHeading();
            shootWithStutter(false);
            resetSorter();
        } else if (state == 2) {
            // Lined up for intake: fix heading, turn on intake
            correctHeading();
            intake(true);
        } else if (state == 3) {
            // Post-intake: start warming up shooter, pause briefly to finish intake
            warmupShooter(false);
            sleep(250);
        } else if (state == 4) {
            // Shooting position: fix heading, shoot, reset sorter
            correctHeading();
            shootWithStutter(false);
            resetSorter();
        } else if (state == 5) {
            // Lined up for intake: fix heading, turn on intake
            correctHeading();
            intake(true);
        } else if (state == 6) {
            // Post-intake: start warming up shooter, pause briefly to finish intake
            warmupShooter(false);
            sleep(250);
        } else if (state == 7) {
            // Shooting position: fix heading, shoot
            correctHeading();
            shootWithStutter(false);
        } else if (state == 8) {
            // Final position: ready to open gate
            correctHeading();
        }
    }

    public static class Paths {
        public PathChain Path1;
        public PathChain Path2;
        public PathChain Path3;
        public PathChain Path4;
        public PathChain Path5;
        public PathChain Path6;
        public PathChain Path7;
        public PathChain Path8;

        public Paths(Follower follower) {
            Path1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(START_POSE, SHOOT_POSE))
                    .setLinearHeadingInterpolation(START_HEADING, SHOOT_HEADING)
                    .build();

            Path2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(SHOOT_POSE, PRE_INTAKE1_POSE))
                    .setLinearHeadingInterpolation(SHOOT_HEADING, INTAKE_HEADING)
                    .build();

            Path3 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(PRE_INTAKE1_POSE, POST_INTAKE1_POSE))
                    .setLinearHeadingInterpolation(INTAKE_HEADING, INTAKE_HEADING)
                    .build();

            Path4 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(POST_INTAKE1_POSE, SHOOT_POSE))
                    .setLinearHeadingInterpolation(INTAKE_HEADING, SHOOT_HEADING)
                    .build();

            Path5 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(SHOOT_POSE, PRE_INTAKE2_POSE))
                    .setLinearHeadingInterpolation(SHOOT_HEADING, INTAKE_HEADING)
                    .build();

            Path6 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(PRE_INTAKE2_POSE, POST_INTAKE2_POSE))
                    .setLinearHeadingInterpolation(INTAKE_HEADING, INTAKE_HEADING)
                    .build();

            Path7 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(POST_INTAKE2_POSE, SHOOT_POSE))
                    .setLinearHeadingInterpolation(INTAKE_HEADING, SHOOT_HEADING)
                    .build();

            Path8 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(SHOOT_POSE, END_POSE))
                    .setLinearHeadingInterpolation(SHOOT_HEADING, END_HEADING)
                    .build();
        }
    }
}
