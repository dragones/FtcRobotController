package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous

public class Auto_Mode extends LinearOpMode {
    private Blinker control_Hub;
    private DcMotor front_left;
    private DcMotor front_right;
    private DcMotor back_left;
    private DcMotor back_right;
    private DcMotor lift_motor;
    private WebcamName webcam;
    private Servo servo1;
    private static final String TFOD_MODEL_ASSET = "PowerPlay.tflite";
    // private static final String TFOD_MODEL_FILE  = "/sdcard/FIRST/tflitemodels/CustomTeamModel.tflite";


    private static final String[] LABELS = {
            "1 Bolt",
            "2 Bulb",
            "3 Panel"
    };
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

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
        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        front_right = hardwareMap.get(DcMotor.class, "front_right");
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        back_left = hardwareMap.get(DcMotor.class,"back_left" );
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        back_right = hardwareMap.get(DcMotor.class, "back_right");
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        servo1 = hardwareMap.get(Servo.class, "servo1");
        lift_motor = hardwareMap.get(DcMotor.class, "lift_motor");
        webcam = hardwareMap.get(WebcamName.class, "Webcam 1");

        front_left.setDirection(DcMotor.Direction.REVERSE);
        back_left.setDirection(DcMotor.Direction.REVERSE);
        double  MIN_POSITION = 0, MAX_POSITION = 1;

        int low_jun = 0;
        int med_jun = 0;
        int high_jun = 0;
        int encoder_tick = 538;
        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can increase the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1.0, 16.0/9.0);
        }

        // open servo
        double servo_position = 0;
        servo1.setPosition(servo_position);
       /*
        double servo_position = .4;
        servo1.setPosition(servo_position);
        telemetry.addData("servo_postion>", servo_position);
        telemetry.update();

        // wait to load cone
        sleep(2500);
        // close servo
        servo_position = .6;
        servo1.setPosition(servo_position);
        telemetry.addData("servo_postion>", servo_position);
        telemetry.update();
        */

        // raise arm slightly
        // raiseArm(-1,100);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        /** Wait for the game to begin */
        // Wait for the game to start (driver presses PLAY)
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();

        waitForStart();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");
            telemetry.update();

            //scan cone number
            String coneNumber = "";
            if (tfod != null) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Objects Detected", updatedRecognitions.size());

                    // step through the list of recognitions and display image position/size information for each one
                    // Note: "Image number" refers to the randomized image orientation/number
                    for (Recognition recognition : updatedRecognitions) {
                        double col = (recognition.getLeft() + recognition.getRight()) / 2;
                        double row = (recognition.getTop() + recognition.getBottom()) / 2;
                        double width = Math.abs(recognition.getRight() - recognition.getLeft());
                        double height = Math.abs(recognition.getTop() - recognition.getBottom());

                        telemetry.addData("", " ");
                        telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
                        telemetry.addData("- Position (Row/Col)", "%.0f / %.0f", row, col);
                        telemetry.addData("- Size (Width/Height)", "%.0f / %.0f", width, height);
                        coneNumber = recognition.getLabel();
                        telemetry.addData("codeNumber:", coneNumber);
                        if (coneNumber != null) {
                            break;
                        }
                    }
                    telemetry.update();
                }
            }

            // sleep(5000);
            //move((int) (10 * TICKSPERCENTIMETER), FORWARD);
            /*
            sleep(3000);
            //turn towards junction
            pivot_left (encoder_tick);
            //drop cone
            //raise arm to high junction
            raiseArm(HIGH_JUNCTION);
            */

            // move to correct zone
            if (coneNumber.contains("1")) {
                move((int)(1.1*SQUAREWIDTH * TICKSPERCENTIMETER), LEFT);
                move((int)(SQUAREWIDTH * TICKSPERCENTIMETER), FORWARD);
            }
            else if (coneNumber.contains("2")) {
                move((int)(SQUAREWIDTH * TICKSPERCENTIMETER), FORWARD);
            }
            else if (coneNumber.contains("3")) {
                move((int)(1.1*SQUAREWIDTH * TICKSPERCENTIMETER), RIGHT);
                move((int)(SQUAREWIDTH * TICKSPERCENTIMETER), FORWARD);
            }
        }

        if(tfod != null)
            tfod.shutdown();
    }

    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
    }
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.75f;
        tfodParameters.isModelTensorFlow2 = true;
        tfodParameters.inputSize = 300;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);

        // Use loadModelFromAsset() if the TF Model is built in as an asset by Android Studio
        // Use loadModelFromFile() if you have downloaded a custom team model to the Robot Controller's FLASH.
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
        // tfod.loadModelFromFile(TFOD_MODEL_FILE, LABELS);
    }
    private void move (int distance, int direction){
        front_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        front_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        back_left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

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

        front_right.setPower(0.3);
        front_left.setPower(0.3);
        back_right.setPower(0.3);
        back_left.setPower(0.3);

        while (opModeIsActive() &&
                //(timeout == -1 || runtime.seconds() < timeout) &&
                (front_left.isBusy() && front_right.isBusy() && back_left.isBusy() && back_right.isBusy())) {

            // Display it for the driver.
            //telemetry.addData("Path1", "Running to %7d : %7d : %7d : %7d",
            //        newLeft1Target, newLeft2Target, newRight1Target, newRight2Target);
            telemetry.addData("motors", "Running at %7d :%7d : %7d : %7d",
                    front_left.getCurrentPosition(),
                    front_right.getCurrentPosition(),
                    back_left.getCurrentPosition(),
                    back_right.getCurrentPosition());
            telemetry.update();
        }

        front_right.setPower(0.0);
        front_left.setPower(0.0);
        back_right.setPower(0.0);
        back_left.setPower(0.0);

        // pause between moves
        sleep(250);
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
        front_right.setTargetPosition(distance);
        front_left.setTargetPosition(distance);
        back_right.setTargetPosition(distance);
        back_left.setTargetPosition(distance);
    }

    private void right(int distance){
        front_right.setTargetPosition(-distance);
        front_left.setTargetPosition(distance);
        back_right.setTargetPosition(distance);
        back_left.setTargetPosition(-distance);
    }

    private void left(int distance){
        front_right.setTargetPosition(distance);
        front_left.setTargetPosition(-distance);
        back_right.setTargetPosition(-distance);
        back_left.setTargetPosition(distance);
    }

    private void backwards(int distance){
        front_right.setTargetPosition(-distance);
        front_left.setTargetPosition(-distance);
        back_right.setTargetPosition(-distance);
        back_left.setTargetPosition(-distance);
    }

    private void raiseArm(int junction, int distance){
        if (junction != -1){
            lift_motor.setTargetPosition(PowerRangerTeleOp.high_jun);
        }
        else{
            lift_motor.setTargetPosition(distance);
        }
        lift_motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // servo1.setPosition(PowerRangerTeleOp.MAX_POSITION);
    }

}