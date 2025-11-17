package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;


@Autonomous(group="EBDecode")
@Disabled
public class EBDecodeAutonBlueFar extends EBDecodeAuton {
    @Override
    public void auton() {
        shoot(SHOOTER_HIGH_POWER);
        drive(DRIVE_SPEED, 1000);
    }
}
