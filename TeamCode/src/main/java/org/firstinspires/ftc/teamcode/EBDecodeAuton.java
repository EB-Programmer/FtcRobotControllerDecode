package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/*
 * This OpMode illustrates the concept of driving a path based on time.
 * The code is structured as a LinearOpMode
 *
 * The code assumes that you do NOT have encoders on the wheels,
 *   otherwise you would use: RobotAutoDriveByEncoder;
 *
 *   The desired path in this example is:
 *   - Drive forward for 3 seconds
 *   - Spin right for 1.3 seconds
 *   - Drive Backward for 1 Second
 *
 *  The code is written in a simple form with no optimizations.
 *  However, there are several ways that this type of sequence could be streamlined,
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@Autonomous(group="EBDecode")
//@Disabled
public class EBDecodeAuton extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotor leftDriveBack = null;
    private DcMotor rightDriveBack = null;
    private DcMotor sorter = null;
    private DcMotor shooter = null;
    private CRServo lowerIntake = null;
    private CRServo upperIntake = null;
    private ElapsedTime runtime = new ElapsedTime();
    private static double SORTER_SORTING_POWER = -0.2;
    private static double SORTER_SHOOTING_POWER = 0.2;
    private static double SHOOTER_POWER = 0.3;
    private static double INTAKE_POWER = 0.75;
    private static final int STUTTER_PERIOD = 500;  // milliseconds
    private static final int STUTTER_PAUSE_DURATION = 250;  // milliseconds
    private static final int LOOP_PERIOD = 50; // milliseconds
    static final double FORWARD_SPEED = 0.5;
    static final double TURN_SPEED = 0.4;
    private int SHOOTER_DURATION = 5000;

    @Override
    public void runOpMode() {
        // Initialize the drive system variables.
        leftDrive = hardwareMap.get(DcMotor.class, "leftFrontDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightFrontDrive");
        leftDriveBack = hardwareMap.get(DcMotor.class, "leftRearDrive");
        rightDriveBack = hardwareMap.get(DcMotor.class, "rightRearDrive");

        sorter = hardwareMap.get(DcMotor.class, "sorter");
        shooter = hardwareMap.get(DcMotor.class, "shooter");

        lowerIntake = hardwareMap.get(CRServo.class, "lowerIntake");
        upperIntake = hardwareMap.get(CRServo.class, "upperIntake");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);
        leftDriveBack.setDirection(DcMotor.Direction.REVERSE);
        rightDriveBack.setDirection(DcMotor.Direction.FORWARD);

        lowerIntake.setDirection(DcMotor.Direction.FORWARD);
        upperIntake.setDirection(DcMotor.Direction.REVERSE);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // LEAVE
        Drive(FORWARD_SPEED, 500);

        while (opModeIsActive()) {
            sleep(50);
        }
    }

    private void Drive(double speed, long time) {
        leftDrive.setPower(speed);
        rightDrive.setPower(speed);
        leftDriveBack.setPower(speed);
        rightDriveBack.setPower(speed);
        runtime.reset();
        sleep(time);
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftDriveBack.setPower(0);
        rightDriveBack.setPower(0);
    }

    private void Turn(double speed, long time, boolean right) {
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
        runtime.reset();
        sleep(time);
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftDriveBack.setPower(0);
        rightDriveBack.setPower(0);
    }

    private void Shoot() {
        runtime.reset();
        while (runtime.milliseconds() < SHOOTER_DURATION) {
            shooter.setPower(SHOOTER_POWER);
            lowerIntake.setPower(INTAKE_POWER);
            upperIntake.setPower(INTAKE_POWER);
            int time = (int) (System.currentTimeMillis() % STUTTER_PERIOD);
            if (time < STUTTER_PAUSE_DURATION) {
                sorter.setPower(0);
            } else {
                sorter.setPower(SORTER_SHOOTING_POWER);
            }
            sleep(50);
        }

        shooter.setPower(0);
        sorter.setPower(0);
        lowerIntake.setPower(0);
        upperIntake.setPower(0);
    }
}