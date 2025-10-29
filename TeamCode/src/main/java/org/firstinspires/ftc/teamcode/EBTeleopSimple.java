/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/*
 * This OpMode executes a POV Game style Teleop for a direct drive robot
 * The code is structured as a LinearOpMode
 *
 * In this mode the left stick moves the robot FWD and back, the Right stick turns left and right.
 * It raises and lowers the arm using the Gamepad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 */

@TeleOp
public class EBTeleopSimple extends LinearOpMode {

    /* Declare OpMode members.
    *
    *    4 Drive Motors
    *    1 Color Sorter Motor
    *    1 Shooter Motor
    *    2 Intake Servos
    *
    * */
    public DcMotor leftFrontDrive   = null;
    public DcMotor rightFrontDrive = null;
    public DcMotor leftRearDrive   = null;
    public DcMotor rightRearDrive = null;
    private DcMotor sorter = null;
    private DcMotor shooter = null;
    private CRServo frontIntake = null;
    private CRServo backIntake = null;

    public static final double MID_SERVO   =  0.5 ;
    public static final double CLAW_SPEED  = 0.02 ;                 // sets rate to move servo
    public static final double ARM_UP_POWER    =  0.45 ;
    public static final double ARM_DOWN_POWER  = -0.45 ;

    @Override
    public void runOpMode() {
        // Define and Initialize Motors
        leftFrontDrive = hardwareMap.get(DcMotor.class, "Front_left");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "Front_right");
        leftRearDrive = hardwareMap.get(DcMotor.class, "Back_left");
        rightRearDrive = hardwareMap.get(DcMotor.class, "Back_right");

        sorter = hardwareMap.get(DcMotor.class, "ColorSorter");
        shooter = hardwareMap.get(DcMotor.class, "Shooter");

        frontIntake = hardwareMap.get(CRServo.class, "FrontIntake");
        backIntake = hardwareMap.get(CRServo.class, "BackIntake");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftRearDrive.setDirection(DcMotor.Direction.FORWARD);
        rightRearDrive.setDirection(DcMotor.Direction.REVERSE);

        // If there are encoders connected, switch to RUN_USING_ENCODER mode for greater accuracy
        // leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        frontIntake.setDirection(DcMotorSimple.Direction.FORWARD);
        backIntake.setDirection(DcMotorSimple.Direction.REVERSE);

        // Define and initialize ALL installed servos.
        //leftClaw  = hardwareMap.get(Servo.class, "left_hand");
        //rightClaw = hardwareMap.get(Servo.class, "right_hand");
        //leftClaw.setPosition(MID_SERVO);
        //rightClaw.setPosition(MID_SERVO);

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press START.");    //
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Run wheels in POV mode (note: The joystick goes negative when pushed forward, so negate it)
            // In this mode the Left stick moves the robot fwd and back, the Right stick turns left and right.
            // This way it's also easy to just drive straight, or just turn.
            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn  =  gamepad1.right_stick_x;

            // Combine drive and turn for blended motion.
            double frontLeftPower = drive + strafe + turn;
            double frontRightPower = drive - strafe - turn;
            double rearLeftPower = drive - strafe + turn;
            double rearRightPower = drive + strafe - turn;

            // Normalize the values so neither exceed +/- 1.0
            double maxPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            maxPower = Math.max(Math.abs(maxPower), Math.abs(rearLeftPower));
            maxPower = Math.max(Math.abs(maxPower), Math.abs(rearRightPower));

            if (maxPower > 1.0)
            {
                frontLeftPower /= maxPower;
                frontRightPower /= maxPower;
                rearLeftPower /= maxPower;
                rearRightPower /= maxPower;
            }

            // Output the safe vales to the motor drives.
            leftFrontDrive.setPower(frontLeftPower);
            rightFrontDrive.setPower(frontRightPower);
            leftRearDrive.setPower(rearLeftPower);
            rightRearDrive.setPower(rearRightPower);

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

            shoot();
            sortColors();
            intake();

            // Send telemetry message to signify robot running;
            //telemetry.addData("claw",  "Offset = %.2f", clawOffset);
            //telemetry.addData("left",  "%.2f", left);
            //telemetry.addData("right", "%.2f", right);
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Front Right Power", frontRightPower);
            telemetry.addData("Rear Left Power", rearLeftPower);
            telemetry.addData("Rear Right Power", rearRightPower);
            telemetry.update();

            // Pace this loop so jaw action is reasonable speed.
            sleep(50);
        }
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
