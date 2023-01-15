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

    // Constants ***********************************************************************************

    private static final int LEFT = 1;
    private static final int RIGHT = 2;
    private static final int FORWARD = 3;
    private static final int BACKWARDS = 4;
    private static final int HIGH_JUNCTION = 5;
    private static final double TICKSPERCENTIMETER = 538/30.159;
    private static final double SQUAREWIDTH = 60.0;

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "AdryQWj/////AAABmW9foMVP4EG7hViuB7ygplGIDTgHF0aBKjL+i2subJbCwEWFs0B/wn8gDiTf0E5M42NZkGB3DaBgy7oPb5Eo7uD0khYdAgLnZRQDNDsRLHiDXuOUdT5AASQjQwu27RHkyYkIpAf4ADLhoUp08EeuCtZmGye92TkfO7Od873aLthXkM3Sa4tAJD5pIAnrSfND5rs6DPkdIsRuyxcDIXkC3Hwptw3ksqXb2YABsvTY7JlKYU/sKGwnRwea9phu57lzch0APdXsTTJvR2ns+rRL7ENyACqVWjb3RY0L+5SJgvIY0T1hN6Ngi4yatobCzPkMHhKoVIe40RHAu+eIyy4QE0kCxZ9GwZd2YakEsxwkRnYn";

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

            // TODO: scan cone with webcam
            //scan cone number

            //Move cone to high junction
            forward(2509);
            //turn towards junction
            pivot_left (encoder_tick);
            //drop cone
            //raise arm to high junction
            raiseArm(HIGH_JUNCTION);

            //move to numbered cone
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