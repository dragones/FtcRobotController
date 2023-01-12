package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp

public class Auto_Mode extends LinearOpMode {
    private Blinker control_Hub;
    private DcMotor front_left;
    private DcMotor front_right;
    private DcMotor back_left;
    private DcMotor back_right;
    private DcMotor lift_motor;
    private Servo servo1;
    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int FORWARD = 3;
    private static final int BACKWARDS = 4;
    private static final int HIGH_JUNCTION = 5;
    private static final double TICKSPERCENTIMETER = 538/30.159;
    private static final double SQUAREWIDTH = 60.0;





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
        double  MIN_POSITION = 0, MAX_POSITION = 1;

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        int low_jun = 0;
        int med_jun = 0;
        int high_jun = 0;
        int encoder_tick = 538;

        waitForStart();

        // TODO: load cone
        double servo_position = .5;


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            int coneNumber = 1;

            //scan cone number
            //Move cone to high junction
            forward(2509);
            //raise arm to high junction
            raiseArm(HIGH_JUNCTION);
            //move to numbered cone
            //turn towards junction
            pivot_left (encoder_tick);
            //drop cone
            //move to correct zone
            if (coneNumber == 1){
                right((int)(SQUAREWIDTH * TICKSPERCENTIMETER));
                forward((int)(SQUAREWIDTH * TICKSPERCENTIMETER));
            }
            else if (coneNumber == 2){
                forward((int)(SQUAREWIDTH * TICKSPERCENTIMETER));

            }
            else if (coneNumber == 3){
                left((int)(SQUAREWIDTH * TICKSPERCENTIMETER));
                forward((int)(SQUAREWIDTH * TICKSPERCENTIMETER));

            }
            telemetry.addData("Status", "Running");
            telemetry.update();


        }
    }

    // TODO: code math
    private void move (int distance, int direction){
        //rot.=distance/C
        //move(rot.,direction)
        if (direction == LEFT){
            left(distance);
        }
        else if (direction == RIGHT){
            right(distance);
        }
        else if (direction == FORWARD){
            forward(distance);
        }
        else if (direction == BACKWARDS){
            backwards(distance);
        }
        front_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        front_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_right.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        back_left.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    private void pivot_right(int distance){
        front_right.setTargetPosition(distance);
        front_left.setTargetPosition(-distance);
        back_right.setTargetPosition(distance);
        back_left.setTargetPosition(-distance);
    }

    private void pivot_left(int distance){
        front_right.setTargetPosition(-distance);
        front_left.setTargetPosition(distance);
        back_right.setTargetPosition(-distance);
        back_left.setTargetPosition(distance);
    }

    private void forward(int distance){
        //loop x # of rotations
        front_right.setTargetPosition(distance);
        front_left.setTargetPosition(distance);
        back_right.setTargetPosition(distance);
        back_left.setTargetPosition(distance);

    }

    private void right(int distance){
        //loop x # of rotations
        front_right.setTargetPosition(-distance);
        front_left.setTargetPosition(distance);
        back_right.setTargetPosition(distance);
        back_left.setTargetPosition(-distance);
    }

    private void left(int distance){
        //loop x # of rotations
        front_right.setTargetPosition(distance);
        front_left.setTargetPosition(-distance);
        back_right.setTargetPosition(-distance);
        back_left.setTargetPosition(distance);

    }

    private void backwards(int distance){
        //loop x # of rotations
        front_right.setTargetPosition(-distance);
        front_left.setTargetPosition(-distance);
        back_right.setTargetPosition(-distance);
        back_left.setTargetPosition(-distance);
    }
    private void raiseArm(int junction){
        lift_motor.setTargetPosition(PowerRangerTeleOp.high_jun);
        lift_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        servo1.setPosition(PowerRangerTeleOp.MAX_POSITION);
    }

}