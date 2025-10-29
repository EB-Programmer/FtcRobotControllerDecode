package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

/*
 * This OpMode executes a POV Game style Teleop for a direct drive robot
 * The code is structured as a LinearOpMode
 *
 * In this mode the left stick moves the robot FWD and back, the Right stick turns left and right.
 * It raises and lowers the arm using the Gamepad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 */

@TeleOp(group="EBDecode")
public class EBDecodeTeleop extends LinearOpMode {
    /* Declare OpMode members.
    *
    *    4 Drive Motors
    *    1 Color Sorter Motor
    *    1 Shooter Motor
    *    2 Intake Servos
    *
    * */
    private DcMotor leftFrontDrive   = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftRearDrive   = null;
    private DcMotor rightRearDrive = null;
    private DcMotor sorter = null;
    private DcMotor shooter = null;
    private CRServo lowerIntake = null;
    private CRServo upperIntake = null;

    // TODO: Update constants for DECODE use cases: intake/shooter/drive speeds and limits
    //private static final double MID_SERVO   =  0.5 ;
    //private static final double CLAW_SPEED  = 0.02 ;   // sets rate to move servo
    //private static final double ARM_UP_POWER    =  0.45 ;
    //private static final double ARM_DOWN_POWER  = -0.45 ;

    private double frontLeftPower, frontRightPower, rearLeftPower, rearRightPower;

    @Override
    public void runOpMode() {
        // Initialize motors and servos
        initHardware();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            drive();
            shoot();
            sortColors();
            intake();
            updateTelemetry();
            sleep(50);
        }
    }

    public void initHardware() {
        // Define and Initialize Motors
        // TODO: Update driver hub configuration to make names consistent with other hardware
        leftFrontDrive = hardwareMap.get(DcMotor.class, "Front_left");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "Front_right");
        leftRearDrive = hardwareMap.get(DcMotor.class, "Back_left");
        rightRearDrive = hardwareMap.get(DcMotor.class, "Back_right");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftRearDrive.setDirection(DcMotor.Direction.FORWARD);
        rightRearDrive.setDirection(DcMotor.Direction.REVERSE);

        sorter = hardwareMap.get(DcMotor.class, "Sorter");
        shooter = hardwareMap.get(DcMotor.class, "Shooter");

        lowerIntake = hardwareMap.get(CRServo.class, "LowerIntake");
        upperIntake = hardwareMap.get(CRServo.class, "UpperIntake");

        lowerIntake.setDirection(DcMotor.Direction.FORWARD);
        upperIntake.setDirection(DcMotor.Direction.REVERSE);

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press START.");    //
        telemetry.update();
    }

    public void drive() {
        // Run wheels in POV mode
        // The left stick moves the robot fwd/back and strafes left/right
        // The right stick turns the robot counterclockwise and clockwise
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn  =  gamepad1.right_stick_x;

        // Combine drive, strafe, and turn for blended motion
        frontLeftPower = drive + strafe + turn;
        frontRightPower = drive - strafe - turn;
        rearLeftPower = drive - strafe + turn;
        rearRightPower = drive + strafe - turn;

        // Normalize the values so neither exceed +/- 1.0
        // TODO: Should we enforce a maximum speed other than 1.0?
        double maxPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        maxPower = Math.max(Math.abs(maxPower), Math.abs(rearLeftPower));
        maxPower = Math.max(Math.abs(maxPower), Math.abs(rearRightPower));
        if (maxPower > 1.0) {
            frontLeftPower /= maxPower;
            frontRightPower /= maxPower;
            rearLeftPower /= maxPower;
            rearRightPower /= maxPower;
        }

        // Output the safe vales to the motor drives
        leftFrontDrive.setPower(frontLeftPower);
        rightFrontDrive.setPower(frontRightPower);
        leftRearDrive.setPower(rearLeftPower);
        rightRearDrive.setPower(rearRightPower);
    }

    public void shoot() {
        // TODO: If we want to ensure we are not rotating the robot when shooting,
        //       we may want to use a right thumb button e.g. A or B
        // TODO: What should we do if the shooting button is not pressed?
        // TODO: Should we allow simultaneous intake and shooting? Sorting and shooting?
        if(gamepad2.right_bumper){
            shooter.setPower(0.85);
        }
    }

    public void sortColors() {
        // TODO
        // Use gamepad left & right Bumpers to open and close the claw
        //if (gamepad1.right_bumper)
        //    clawOffset += CLAW_SPEED;
        //else if (gamepad1.left_bumper)
        //    clawOffset -= CLAW_SPEED;

        // Move both servos to new position.  Assume servos are mirror image of each other.
        //clawOffset = Range.clip(clawOffset, -0.5, 0.5);
        //leftClaw.setPosition(MID_SERVO + clawOffset);
        //rightClaw.setPosition(MID_SERVO - clawOffset);

        // Use gamepad buttons to move arm up (Y) and down (A)
        //if (gamepad1.y)
        //    leftArm.setPower(ARM_UP_POWER);
        //else if (gamepad1.a)
        //    leftArm.setPower(ARM_DOWN_POWER);
        //else
        //    leftArm.setPower(0.0);
    }

    public void intake() {
        // TODO: Do we ever need to use right stick (rotate robot) while running intake?
        //       If so, maybe change intake to use a bumper
        // TODO: What should we do if the intake button is not pressed?
        if(gamepad2.a){
            lowerIntake.setPower(0.5);
            upperIntake.setPower(0.5);
        }
    }

    public void updateTelemetry() {
        // Send telemetry message to signify robot running;
        //telemetry.addData("claw",  "Offset = %.2f", clawOffset);
        //telemetry.addData("left",  "%.2f", left);
        //telemetry.addData("right", "%.2f", right);
        telemetry.addData("Front Left Power", frontLeftPower);
        telemetry.addData("Front Right Power", frontRightPower);
        telemetry.addData("Rear Left Power", rearLeftPower);
        telemetry.addData("Rear Right Power", rearRightPower);
        telemetry.update();
    }
}
