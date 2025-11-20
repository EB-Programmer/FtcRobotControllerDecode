package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(group="EBDecode")
public class EBDecodeAutonPedroRedNear3 extends EBDecodeAutonPedroBlueNear3 {
    @Override
    public boolean shouldFlipPath() {
        // Flip parent class's paths from blue to red
        return true;
    }
}
