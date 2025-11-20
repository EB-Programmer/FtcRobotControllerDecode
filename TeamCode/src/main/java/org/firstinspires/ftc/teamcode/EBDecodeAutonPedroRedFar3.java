package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(group="EBDecode")
public class EBDecodeAutonPedroRedFar3 extends EBDecodeAutonPedroBlueFar3 {
    @Override
    public boolean shouldFlipPath() {
        // Flip parent class's paths from blue to red
        return true;
    }
}
