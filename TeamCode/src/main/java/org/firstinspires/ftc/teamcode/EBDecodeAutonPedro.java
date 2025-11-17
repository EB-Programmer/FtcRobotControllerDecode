package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.List;

@Autonomous(group="EBDecodeTest")
public class EBDecodeAutonPedro extends EBDecodeAuton {
    public Follower follower;
    public List<PathChain> pathList;
    public int pathState;

    @Override
    public void auton() {
        follower = Constants.createFollower(hardwareMap);
        pathList = getPathList();
        follower.setStartingPose(pathList.get(0).getPath(0).getPose(0));
        intake(true);

        while (pathState < pathList.size() || follower.isBusy()) {
            follower.update();
            autonomousPathUpdate();

            telemetry.addData("Path State", pathState);
            telemetry.addData("X", follower.getPose().getX());
            telemetry.addData("Y", follower.getPose().getY());
            telemetry.addData("Heading", follower.getPose().getHeading());
            telemetry.update();
        }
    }

    public List<PathChain> getPathList() {
        return new ArrayList<PathChain>();
    }

    public void autonomousPathUpdate() {
        if (pathState < pathList.size()) {
            if (!follower.isBusy()) {
                sleep(1000);
                follower.followPath(pathList.get(pathState));
                pathState += 1;
            }
        } else {
            intake(false);
        }
    }
}
