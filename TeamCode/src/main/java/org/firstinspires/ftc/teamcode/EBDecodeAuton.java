package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
    private DcMotor         leftDrive   = null;
    private DcMotor         rightDrive  = null;
    private DcMotor         leftDriveBack  = null;
    private DcMotor         rightDriveBack  = null;

    private ElapsedTime     runtime = new ElapsedTime();

    static final double     FORWARD_SPEED = 0.5;
    static final double     TURN_SPEED    = 0.4;

    @Override
    public void runOpMode() {
        // Initialize the drive system variables.
        leftDrive = hardwareMap.get(DcMotor.class, "Front_left");
        rightDrive = hardwareMap.get(DcMotor.class, "Front_right");
        leftDriveBack = hardwareMap.get(DcMotor.class, "Back_left");
        rightDriveBack = hardwareMap.get(DcMotor.class, "Back_right");
        //sorter = hardwareMap.get(DcMotor.class, "sorter");
        //shooter = hardwareMap.get(DcMotor.class, "shooter");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);
        leftDriveBack.setDirection(DcMotor.Direction.REVERSE);
        rightDriveBack.setDirection(DcMotor.Direction.FORWARD);

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

    private void Drive(double speed, long time){
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

    private void Turn(double speed, long time, boolean right){
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
}
