package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="2025 Electric Bacon", group="Electric Bacon")

public class ElectricBacon2025 extends OpMode {

    //4 Drive Motors
    //1 Color Sorter Motor
    //1 Shooter Motor
    //2 Intake Servos

    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;
    private DcMotor colorSorter = null;
    private DcMotor shooter = null;

    private CRServo frontIntake = null;
    private CRServo backIntake = null;

    @Override
    public void init() {
        frontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "FrontRight");
        backLeft = hardwareMap.get(DcMotor.class, "BackLeft");
        backRight = hardwareMap.get(DcMotor.class, "BackRight");
        colorSorter = hardwareMap.get(DcMotor.class, "ColorSorter");
        shooter = hardwareMap.get(DcMotor.class, "Shooter");

        frontIntake = hardwareMap.get(CRServo.class, "FrontIntake");
        backIntake = hardwareMap.get(CRServo.class, "BackIntake");

        frontIntake.setDirection(DcMotorSimple.Direction.FORWARD);
        backIntake.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void init_loop() {

    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {
        drive();
        shoot();
        sortColors();
        intake();
    }

    public void drive() {

    }

    public void shoot() {
        if(gamepad2.right_bumper){
            shooter.setPower(.85);
        }
    }

    public void sortColors() {

    }

    public void intake() {
        if(gamepad2.a){
            frontIntake.setPower(0.5);
            backIntake.setPower(0.5);
        }
    }
}