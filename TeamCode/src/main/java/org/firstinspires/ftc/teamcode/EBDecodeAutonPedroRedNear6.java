package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(group="EBDecode")
public class EBDecodeAutonPedroRedNear6 extends EBDecodeAutonPedroBlueNear6 {
    @Override
    public boolean shouldFlipPath() {
        // Flip parent class's paths from blue to red
        return true;
    }
}
