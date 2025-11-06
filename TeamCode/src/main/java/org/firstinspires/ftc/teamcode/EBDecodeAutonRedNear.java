package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


@Autonomous(group="EBDecode")
public class EBDecodeAutonRedNear extends EBDecodeAuton {
    @Override
    public void auton() {
        drive(-DRIVE_SPEED, 1000);
        shoot();
        strafe(DRIVE_SPEED, 500, true);
    }
}
