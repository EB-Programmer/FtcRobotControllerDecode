package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@Autonomous(group="EBDecodeTest")
public class EBDecodeAuton extends LinearOpMode {
    /* Declare OpMode members. */
    public DcMotor leftDrive = null;
    public DcMotor rightDrive = null;
    public DcMotor leftDriveBack = null;
    public DcMotor rightDriveBack = null;
    public DcMotor sorter = null;
    public DcMotor shooter = null;
    public CRServo lowerIntake = null;
    public CRServo upperIntake = null;

    public ElapsedTime runtime = new ElapsedTime();

    public static double SORTER_SORTING_POWER = -0.2;
    public static double SORTER_SHOOTING_POWER = 0.4;
    public static double SHOOTER_HIGH_POWER = 0.95;
    public static double SHOOTER_LOW_POWER = 0.8;
    public static double INTAKE_POWER = 0.9;
    public static final int STUTTER_PERIOD = 160;  // milliseconds
    public static final int STUTTER_PAUSE_DURATION = 120;  // milliseconds
    public static final int LOOP_PERIOD = 20;  // milliseconds
    public static final double DRIVE_SPEED = 0.4;
    public static final double TURN_SPEED = 0.25;
    public static final int SHOOTER_WARMUP_DURATION = 1000;  // milliseconds
    public static final int SHOOTER_DURATION = 10000;  // milliseconds

    @Override
    public void runOpMode() {
        initHardware();

        // Wait for the game to start (driver presses START)
        waitForStart();

        auton();

        waitForEnd();
    }

    public void auton() {
    }

    private void waitForEnd() {
        while (opModeIsActive()) {
            sleep(LOOP_PERIOD);
        }
    }

    public void initHardware() {
        // Initialize the drive system variables.
        leftDrive = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftDriveBack = hardwareMap.get(DcMotor.class, "leftRearDrive");
        rightDriveBack = hardwareMap.get(DcMotor.class, "rightRearDrive");
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);
        leftDriveBack.setDirection(DcMotor.Direction.REVERSE);
        rightDriveBack.setDirection(DcMotor.Direction.FORWARD);

        sorter = hardwareMap.get(DcMotor.class, "sorter");
        shooter = hardwareMap.get(DcMotor.class, "shooter");
        sorter.setDirection(DcMotor.Direction.FORWARD);
        shooter.setDirection(DcMotor.Direction.REVERSE);

        lowerIntake = hardwareMap.get(CRServo.class, "lowerIntake");
        upperIntake = hardwareMap.get(CRServo.class, "upperIntake");
        lowerIntake.setDirection(DcMotor.Direction.FORWARD);
        upperIntake.setDirection(DcMotor.Direction.REVERSE);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");
        telemetry.update();
    }

    public void drive(double speed, long time) {
        leftDrive.setPower(speed);
        rightDrive.setPower(speed);
        leftDriveBack.setPower(speed);
        rightDriveBack.setPower(speed);
        sleep(time);
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftDriveBack.setPower(0);
        rightDriveBack.setPower(0);
    }

    public void turn(double speed, long time, boolean right) {
        if (right) {
            leftDrive.setPower(speed);
            rightDrive.setPower(-speed);
            leftDriveBack.setPower(speed);
            rightDriveBack.setPower(-speed);
        } else {
            leftDrive.setPower(-speed);
            rightDrive.setPower(speed);
            leftDriveBack.setPower(-speed);
            rightDriveBack.setPower(speed);
        }
        sleep(time);
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftDriveBack.setPower(0);
        rightDriveBack.setPower(0);
    }

    public void strafe(double speed, long time, boolean right) {
        if (right) {
            leftDrive.setPower(speed);
            rightDrive.setPower(-speed);
            leftDriveBack.setPower(-speed);
            rightDriveBack.setPower(speed);
        } else {
            leftDrive.setPower(-speed);
            rightDrive.setPower(speed);
            leftDriveBack.setPower(speed);
            rightDriveBack.setPower(-speed);
        }
        sleep(time);
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftDriveBack.setPower(0);
        rightDriveBack.setPower(0);
    }

    public void shoot(double shooterPower) {
        // Let shooter motor warm up for 1 second before pushing artifacts into launcher
        shooter.setPower(shooterPower);
        sleep(SHOOTER_WARMUP_DURATION);

        // Run the sorter motor for 10 seconds to push all the artifacts into the launcher
        runtime.reset();
        while (runtime.milliseconds() < SHOOTER_DURATION) {
            //upperIntake.setPower(INTAKE_POWER);  // TODO: not sure if we want/need this
            int time = (int) (System.currentTimeMillis() % STUTTER_PERIOD);
            if (time < STUTTER_PAUSE_DURATION) {
                sorter.setPower(0);
            } else {
                sorter.setPower(SORTER_SHOOTING_POWER);
            }
            sleep(LOOP_PERIOD);
        }

        shooter.setPower(0);
        sorter.setPower(0);
        lowerIntake.setPower(0);
        upperIntake.setPower(0);
    }
}
