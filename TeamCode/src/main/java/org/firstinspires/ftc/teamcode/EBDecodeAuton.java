package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(group="EBDecodeTest")
public class EBDecodeAuton extends LinearOpMode {
    public DcMotor leftDrive = null;
    public DcMotor rightDrive = null;
    public DcMotor leftDriveBack = null;
    public DcMotor rightDriveBack = null;
    public DcMotor sorter = null;
    public DcMotor shooter = null;
    public CRServo lowerIntake = null;
    public CRServo upperIntake = null;

    public static double SORTER_SORTING_POWER = -0.3;
    public static double SORTER_SHOOTING_POWER = -0.25;
    private static final double SHOOTER_HIGH_VELOCITY = 1800;
    private static final double SHOOTER_LOW_VELOCITY = 1400;
    public static double INTAKE_POWER = 0.9;
    public static double INTAKE_LOW_POWER = 0.7;
    public static final int STUTTER_PERIOD = 360;  // milliseconds
    public static final int STUTTER_PAUSE_DURATION = 120;  // milliseconds
    public static final int LOOP_PERIOD = 20;  // milliseconds
    public static final int SHOOTER_DURATION = 8000;  // milliseconds
    public static final double SORTER_TICKS = 384.5;

    public ElapsedTime shooterWarmupTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    public ElapsedTime shooterTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
    public ElapsedTime autonTimer = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);

    public double targetShooterVelocity = 0;
    public double currentShooterVelocity = 0;
    public boolean shooterVelocityInRange = false;
    public int shotCount = 0;
    public int sorterTargetPosition = 0;

    @Override
    public void runOpMode() {
        initHardware();

        // Wait for the game to start (driver presses START)
        waitForStart();

        // Start timer counting up toward 30 seconds
        autonTimer.reset();

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
        shooter.setDirection(DcMotor.Direction.FORWARD);
        sorter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sorter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sorter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        lowerIntake = hardwareMap.get(CRServo.class, "lowerIntake");
        upperIntake = hardwareMap.get(CRServo.class, "upperIntake");
        lowerIntake.setDirection(DcMotor.Direction.FORWARD);
        upperIntake.setDirection(DcMotor.Direction.REVERSE);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");
        telemetry.update();
    }

    public void intake(boolean active, boolean isShooting) {
        if (active) {
            if (isShooting) {
                lowerIntake.setPower(INTAKE_POWER);
                upperIntake.setPower(INTAKE_LOW_POWER);
            } else {
                lowerIntake.setPower(INTAKE_POWER);
                upperIntake.setPower(INTAKE_POWER);
            }
        } else {
            lowerIntake.setPower(0);
            upperIntake.setPower(0);
        }
    }

    public void intake(boolean active) {
        intake(active, false);
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


    public void resetSorter() {
        double position = sorter.getCurrentPosition();
        double positionMod = Math.abs(position % SORTER_TICKS);
        if (positionMod < SORTER_TICKS / 8 || positionMod > 7 * SORTER_TICKS / 8) {
            // close enough!
            sorterTargetPosition = 0;
            return;
        }

        // Calculate the next multiple of SORTER_TICKS
        double targetPosition = position + SORTER_TICKS;
        targetPosition = (Math.floor(Math.abs(targetPosition) / SORTER_TICKS)
                * SORTER_TICKS
                * (targetPosition < 0 ? -1 : 1));
        sorter.setPower(0);
        sorterTargetPosition = (int) Math.round(targetPosition);
        sorter.setTargetPosition(sorterTargetPosition);
        sorter.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        sorter.setTargetPosition(sorterTargetPosition);
        sorter.setPower(SORTER_SHOOTING_POWER);
    }

    public void warmupShooter(boolean longShot) {
        double newTargetShooterVelocity = (longShot ? SHOOTER_HIGH_VELOCITY : SHOOTER_LOW_VELOCITY);
        if (newTargetShooterVelocity != targetShooterVelocity) {
            shooterWarmupTimer.reset();
        }
        targetShooterVelocity = newTargetShooterVelocity;
        ((DcMotorEx)shooter).setVelocity(targetShooterVelocity);
    }

    public void shoot(boolean longShot) {
        shoot(longShot, SHOOTER_DURATION);
    }

    public void shoot(boolean longShot, int shooterDuration) {
        warmupShooter(longShot);

        // Make sure sorter is turned off and ready to run
        sorter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        sorter.setPower(0);

        shotCount = 0;
        shooterTimer.reset();
        while (shooterTimer.milliseconds() < shooterDuration && shotCount < 3) {
            currentShooterVelocity = ((DcMotorEx)shooter).getVelocity();

            // Wait until shooter velocity is very close to target velocity
            if (0.95 * targetShooterVelocity < currentShooterVelocity
                    && currentShooterVelocity < 1.00 * targetShooterVelocity) {
                shooterVelocityInRange = true;
            }

            // If shooter velocity later falls out of tolerance, pause the sorter and let the
            // shooter warm back up
            if (currentShooterVelocity < 0.95 * targetShooterVelocity) {
                if (shooterVelocityInRange) {
                    shotCount++;
                    shooterWarmupTimer.reset();
                }
                shooterVelocityInRange = false;
            }

            // Power up the sorter motor only after shooter reaches target velocity
            // OR button has been held for 3+ seconds
            if (shooterVelocityInRange || shooterWarmupTimer.milliseconds() > 3000) {
                intake(true, true);
                int time = (int) (System.currentTimeMillis() % STUTTER_PERIOD);
                if (time < STUTTER_PAUSE_DURATION) {
                    sorter.setPower(0);
                } else {
                    sorter.setPower(SORTER_SHOOTING_POWER);
                }
            } else {
                intake(false);
                sorter.setPower(0);
            }

            updateTelemetry();
            sleep(LOOP_PERIOD);
        }

        intake(false);
        targetShooterVelocity = 0;
        shooterVelocityInRange = false;
        shooterWarmupTimer.reset();
        shooter.setPower(0);
        sorter.setPower(0);

        // Try to move sorter paddle back to the "back" position
        resetSorter();
    }

    public void updateTelemetry() {
    }
}
