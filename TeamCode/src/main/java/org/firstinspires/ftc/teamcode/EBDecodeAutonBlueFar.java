package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


@Autonomous(group="EBDecode")
public class EBDecodeAutonBlueFar extends EBDecodeAuton {
    @Override
    public void auton() {
        shoot(SHOOTER_HIGH_POWER);
        drive(DRIVE_SPEED, 1000);
    }
}
