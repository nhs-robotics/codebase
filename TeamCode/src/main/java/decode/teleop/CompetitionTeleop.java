package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.ServoImpl;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.Constants;
import codebase.actions.LaunchAction;
import codebase.actions.RotateRevolverAction;
import codebase.actions.SetMotorPowerAction;
import codebase.actions.SimultaneousAction;
import codebase.gamepad.Gamepad;
import codebase.geometry.MovementVector;
import codebase.hardware.Motor;
import codebase.movement.mecanum.MecanumDriver;

@TeleOp(name="Competition Teleop")
public class CompetitionTeleop extends OpMode {

    private Gamepad gamepad;
    private MecanumDriver driver;
    private SimultaneousAction actionThread;

    private Motor fl;
    private Motor fr;
    private Motor bl;
    private Motor br;

    private Motor revolverMotor;

    private ServoImpl launchServo;
    private Motor launchMotor1;
    private Motor launchMotor2;

    private Telemetry.Item revolverPIDDisplay;

    private RotateRevolverAction revolverAction;

    @Override
    public void init() {
        fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"));
        fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"));
        bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"));
        br = new Motor(hardwareMap.get(DcMotorEx.class, "br"));

        revolverMotor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"));

        launchMotor1 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor1"));
        launchMotor2 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor2"));
        launchServo = hardwareMap.get(ServoImpl.class, "launchServo");

        launchServo.setPosition(0.5);

        gamepad = new Gamepad(gamepad1);
        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);
        actionThread = new SimultaneousAction();

        RotateRevolverAction.setRevolverMotor(revolverMotor);
        LaunchAction.setLaunchActionMotors(launchServo, launchMotor1, launchMotor2);

        gamepad.yButton.onPress(() -> {
            revolverAction = new RotateRevolverAction(0, RotateRevolverAction.RevolverMode.OUTPUT, 0.05);
            actionThread.add(revolverAction, true, true);

        });

        gamepad.bButton.onPress(() -> {
            revolverAction = new RotateRevolverAction(1, RotateRevolverAction.RevolverMode.OUTPUT, 0.05);
            actionThread.add(revolverAction, true, true);
        });

        gamepad.aButton.onPress(() -> {
            revolverAction = new RotateRevolverAction(2, RotateRevolverAction.RevolverMode.OUTPUT, 0.05);
            actionThread.add(revolverAction, true, true);
        });

        gamepad.rightTrigger.onPress(() -> {
            actionThread.add(new LaunchAction(), true);
        });

        revolverPIDDisplay = telemetry.addData("revolverError", 0);
    }

    @Override
    public void loop() {
        driver.setRelativePower(new MovementVector(gamepad.leftJoystick.getY(), gamepad.leftJoystick.getX(), gamepad.rightJoystick.getX()));
        gamepad.loop();
        actionThread.loop();

        if (revolverAction != null) {
            revolverPIDDisplay.setValue(revolverAction.getRotationalError());
        }
    }
}
