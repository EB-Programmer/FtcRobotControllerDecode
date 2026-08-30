package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

import java.util.ArrayList;
import java.util.List;

@Autonomous(group="EBDecodeTest")
public class EBDecodeAutonLimelightTest extends EBDecodeAutonSummer {
    public Limelight3A limelight;
    public double tx = 0, ty = 0, ta = 0;

    public int count = 0;

    public boolean pollenVisible = false;
    public boolean pollenTooClose = false;

    @Override
    public void auton() {
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // how many times/sec to request data
        limelight.start();            // must call this before reading results
        limelight.pipelineSwitch(3); // whatever slot your yellow-ball pipeline is in

        while (opModeIsActive()) {
            LLResult result = limelight.getLatestResult();

            pollenVisible = (result != null && result.isValid());
            pollenTooClose = (ta > 0.8);

            if (pollenVisible && !pollenTooClose) {
                count = 0;

                tx = result.getTx(); // horizontal offset, degrees
                ty = result.getTy(); // vertical offset, degrees
                ta = result.getTa(); // target area, % of image

                // Apply turnPower to your drivetrain
                drive(0.4,0, tx/50, 0.4);
            } else {
                count = count + 1;

                if (count >= 20 || pollenTooClose) {
                    tx = ty = ta = 0;
                    drive(0, 0, 0, 0);
                }
            }

            updateTelemetry();
            sleep(LOOP_PERIOD);
        }
    }

    @Override
    public void updateTelemetry() {
        telemetry.addData("tx", tx);
        telemetry.addData("ty", ty);
        telemetry.addData("ta", ta);
        telemetry.addData("count", count);
        telemetry.addData("pollenVisible", pollenVisible);
        telemetry.addData("pollenTooClose", pollenTooClose);
        telemetry.update();
    }
}
