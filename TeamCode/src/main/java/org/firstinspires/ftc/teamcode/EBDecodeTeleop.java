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

    private static final double FAST_DRIVE_SPEED_LIMIT = 0.4;
    private static final double NORMAL_DRIVE_SPEED_LIMIT = 0.2;
    private static final double SORTER_SORTING_POWER = -0.2;
    private static final double SORTER_SHOOTING_POWER = 0.2;
    private static final double SHOOTER_POWER = 0.1;
    private static final double INTAKE_POWER = 0.75;

    private boolean fastMode = true;
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
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftRearDrive.setDirection(DcMotor.Direction.REVERSE);
        rightRearDrive.setDirection(DcMotor.Direction.FORWARD);

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
        // Check if FastMode is being toggled on or off
        if (gamepad1.a) {
            fastMode = true;
        } else if (gamepad1.b) {
            fastMode = false;
        }

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

        // Normalize the values so neither exceed +/- speedLimit
        double speedLimit = (fastMode ? FAST_DRIVE_SPEED_LIMIT : NORMAL_DRIVE_SPEED_LIMIT);
        double maxPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        maxPower = Math.max(Math.abs(maxPower), Math.abs(rearLeftPower));
        maxPower = Math.max(Math.abs(maxPower), Math.abs(rearRightPower));
        if (maxPower > speedLimit) {
            frontLeftPower /= maxPower;
            frontRightPower /= maxPower;
            rearLeftPower /= maxPower;
            rearRightPower /= maxPower;
            frontLeftPower *= speedLimit;
            frontRightPower *= speedLimit;
            rearLeftPower *= speedLimit;
            rearRightPower *= speedLimit;
        }

        // Output the safe vales to the motor drives
        leftFrontDrive.setPower(frontLeftPower);
        rightFrontDrive.setPower(frontRightPower);
        leftRearDrive.setPower(rearLeftPower);
        rightRearDrive.setPower(rearRightPower);
    }

    public void shoot() {
        if(gamepad2.right_bumper && !gamepad2.y){
            shooter.setPower(SHOOTER_POWER);
            sorter.setPower(SORTER_SHOOTING_POWER);
        } else if  (gamepad2.y == gamepad2.right_bumper) {
            shooter.setPower(0);
            sorter.setPower(0);
        }
    }

    public void sortColors() {
        if(gamepad2.y && !gamepad2.right_bumper){
            sorter.setPower(SORTER_SORTING_POWER);
        } else if (gamepad2.y == gamepad2.right_bumper) {
            sorter.setPower(0);
        }
    }

    public void intake() {
        if (gamepad2.a){
            lowerIntake.setPower(INTAKE_POWER);
            upperIntake.setPower(INTAKE_POWER);
        } else{
            lowerIntake.setPower(0);
            upperIntake.setPower(0);
        }
    }

    public void updateTelemetry() {
        // Send telemetry message to signify robot running;
        //telemetry.addData("claw",  "Offset = %.2f", clawOffset);
        //telemetry.addData("left",  "%.2f", left);
        //telemetry.addData("right", "%.2f", right);
        telemetry.addData("Gamepad1 A", gamepad1.a);
        telemetry.addData("Gamepad2 A", gamepad2.a);
        telemetry.addData("Gamepad2 Y", gamepad2.y);
        telemetry.addData("Gamepad2 Right Bumper", gamepad2.right_bumper);
        telemetry.addData("Gamepad1 Left Stick X", gamepad1.left_stick_x);
        telemetry.addData("Gamepad1 Left Stick Y", gamepad1.left_stick_y);
        telemetry.addData("Front Left Power", frontLeftPower);
        telemetry.addData("Front Right Power", frontRightPower);
        telemetry.addData("Rear Left Power", rearLeftPower);
        telemetry.addData("Rear Right Power", rearRightPower);
        telemetry.addData("Fast Mode", fastMode);
        telemetry.update();
    }
}
