package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp

public class MultiTest1 extends LinearOpMode {
    private Blinker control_Hub;
    private DcMotor front_left;
    private DcMotor front_right;
    private DcMotor back_left;
    private DcMotor back_right;

    @Override
    public void runOpMode() {
        control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        front_left = hardwareMap.get(DcMotor.class, "front_left");
        front_right = hardwareMap.get(DcMotor.class, "front_right");
        back_left = hardwareMap.get(DcMotor.class,"back_left" );
        back_right = hardwareMap.get(DcMotor.class, "back_right");


        front_left.setDirection(DcMotor.Direction.REVERSE);
        back_left.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double x =  this.gamepad1.left_stick_x;
            double y = - this.gamepad1.left_stick_y;
            double x2 = this.gamepad1.right_stick_x;

            front_right.setPower(y-x-x2);
            front_left.setPower(y+x+x2);
            back_right.setPower(y+x-x2);
            back_left.setPower(y-x+x2);



            telemetry.addData("Status", "Running");
            telemetry.update();


        }
    }
}
