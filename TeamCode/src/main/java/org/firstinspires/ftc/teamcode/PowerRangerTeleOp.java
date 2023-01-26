package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp

public class PowerRangerTeleOp extends LinearOpMode {
    private Blinker control_Hub;
    private DcMotor front_left;
    private DcMotor front_right;
    private DcMotor back_left;
    private DcMotor back_right;
    private DcMotor lift_motor;
    private Servo servo1;
    public static int max = -4200;
    public static int min = 0;
    public static int low_jun = -1300;
    public static int med_jun = -2600;
    public static int high_jun = -3800;
    public static double  MIN_POSITION = 0, MAX_POSITION = 1;


    @Override
    public void runOpMode() {
        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        front_left = hardwareMap.get(DcMotor.class, "front_left");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
        back_left = hardwareMap.get(DcMotor.class,"back_left" );
        back_right = hardwareMap.get(DcMotor.class, "back_right");
        servo1 = hardwareMap.get(Servo.class, "servo1");
        lift_motor = hardwareMap.get(DcMotor.class, "lift_motor");




        front_left.setDirection(DcMotor.Direction.REVERSE);
        back_left.setDirection(DcMotorSimple.Direction.REVERSE);

        // Wait for the game to start (driver presses PLAY)

        double servo_position = 0.5;
        servo1.setPosition (servo_position);
        telemetry.addData("Status", "Initialized");
        telemetry.addData("servoPosition> ", servo_position);
        telemetry.update();

        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double negative = 1;
            double x =  this.gamepad1.left_stick_x;
            double y = - this.gamepad1.left_stick_y;
            double x2 = this.gamepad1.right_stick_x;
            double cap = 0.45;

            // TODO: How to set power so have more fine grained control?
            front_right.setPower((y-x-x2)*cap);
            front_left.setPower((y+x+x2)*cap);
            back_right.setPower((y+x-x2)*cap);
            back_left.setPower((y-x+x2)*cap);

            if (gamepad1.right_trigger > 0.3 && servo_position > MIN_POSITION) servo_position += .01;

            if (gamepad1.left_trigger > 0.3 && servo_position < MAX_POSITION) servo_position -= .01;

            // set the servo position/power values as we have computed them.
            servo1.setPosition(Range.clip(servo_position, MIN_POSITION, MAX_POSITION));
            if (gamepad1.a) {
                lift_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift_motor.setTargetPosition(low_jun);
            }
            else if (gamepad1.b) {
                lift_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift_motor.setTargetPosition(med_jun);
            }
            else if (gamepad1.y) {
                lift_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift_motor.setTargetPosition(high_jun);
            }
            else if(gamepad1.x){
                lift_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift_motor.setTargetPosition(0);
            }
            if (gamepad1.dpad_up) {
                int newposition = lift_motor.getCurrentPosition() + 50;
                if(newposition>max){
                    newposition = max;
                }
                lift_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift_motor.setTargetPosition(newposition);

            }
            else if (gamepad1.dpad_down) {
                int newposition = lift_motor.getCurrentPosition()-50;
                if(newposition<min){
                    newposition = min;
                }
                lift_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                lift_motor.setTargetPosition(newposition);
            }
            lift_motor.setPower(0.7);


            //sets the lift motor to the heights of the junctions

            telemetry.addData("Status", "Running");
            telemetry.addData("lift position", lift_motor.getCurrentPosition());
            telemetry.addData("is at target", !lift_motor.isBusy());
            telemetry.update();


        }
    }
}

