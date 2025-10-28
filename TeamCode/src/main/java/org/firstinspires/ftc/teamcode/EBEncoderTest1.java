package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class EBEncoderTest1 extends LinearOpMode {
    DcMotor encoder1, encoder2;

    @Override
    public void runOpMode() throws InterruptedException {
        encoder1 = hardwareMap.get(DcMotor.class, "Back_right");  // your current "forwardEncoder"
        encoder2 = hardwareMap.get(DcMotor.class, "Front_left");  // your current "strafeEncoder"

        telemetry.addLine("Ready!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Encoder1 (Back_right)", encoder1.getCurrentPosition());
            telemetry.addData("Encoder2 (Front_left)", encoder2.getCurrentPosition());
            telemetry.update();
        }
    }
}
