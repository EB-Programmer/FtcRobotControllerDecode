package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


@Autonomous(group="EBDecode")
public class EBDecodeAutonBlueNear extends EBDecodeAuton {
    @Override
    public void auton() {
        // Back up, shoot, strafe right
        drive(-DRIVE_SPEED, 1000);
        shoot(0.75);
        strafe(DRIVE_SPEED, 1000, true);
    }
}
