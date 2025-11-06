package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.ServoImpl;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import codebase.Constants;
import codebase.actions.DcMotorToPositionAction;
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

    private Motor intakeMotor;

    private Telemetry.Item revolverPIDDisplay;

    private RotateRevolverAction revolverAction;

    @Override
    public void init() {
        fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"));
        fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"));
        bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"));
        br = new Motor(hardwareMap.get(DcMotorEx.class, "br"));

        revolverMotor = new Motor(hardwareMap.get(DcMotorEx.class, "revolverMotor"), Constants.MotorConstants.GOBILDA_5203_2402_TICKS_PER_ROTATION);

        launchMotor1 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor1"));
        launchMotor2 = new Motor(hardwareMap.get(DcMotorEx.class, "launchMotor2"));
        launchServo = hardwareMap.get(ServoImpl.class, "launchServo");
        launchServo.setPosition(1);

        intakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "intake"));

        gamepad = new Gamepad(gamepad1);
        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX);
        actionThread = new SimultaneousAction();

        RotateRevolverAction.setRevolverMotor(revolverMotor);
        LaunchAction.setLaunchActionMotors(launchServo, launchMotor1, launchMotor2);

//        gamepad.dpadLeft.onPress(() -> {
//            revolverAction = new RotateRevolverAction(0, getRevolverMode(), 1);
//            actionThread.add(revolverAction, true, true);
//        });
//
//        gamepad.dpadUp.onPress(() -> {
//            revolverAction = new RotateRevolverAction(1, getRevolverMode(), 1);
//            actionThread.add(revolverAction, true, true);
//        });
//
//        gamepad.dpadRight.onPress(() -> {
//            revolverAction = new RotateRevolverAction(2, getRevolverMode(), 1);
//            actionThread.add(revolverAction, true, true);
//        });

        gamepad.rightTrigger.onPress(() -> {
            actionThread.add(new LaunchAction(), true);
        });
//
//        gamepad.aButton.onPress(() -> {
//            actionThread.add(new DcMotorToPositionAction(revolverMotor, revolverMotor.getMotorEncoder().getPosition() + Math.PI / 10, 1, Math.PI / 180, new PIDCoefficients(-0.002, 0, 0)), true, true);
//        });
//
//        gamepad.bButton.onPress(() -> {
//            actionThread.add(new DcMotorToPositionAction(revolverMotor, revolverMotor.getMotorEncoder().getPosition() - Math.PI / 10, 1, Math.PI / 180, new PIDCoefficients(-0.002, 0, 0)), true, true);
//        });

        revolverPIDDisplay = telemetry.addData("revolverError", 0);
    }

    private RotateRevolverAction.RevolverMode getRevolverMode() {
        return (gamepad.leftBumper.isPressed() ? RotateRevolverAction.RevolverMode.INPUT : RotateRevolverAction.RevolverMode.OUTPUT);
    }

    @Override
    public void loop() {
        driver.setRelativePower(new MovementVector(gamepad.leftJoystick.getY(), gamepad.leftJoystick.getX(), gamepad.rightJoystick.getX()));
        gamepad.loop();
        actionThread.loop();

        if (revolverAction != null) {
            revolverPIDDisplay.setValue(revolverAction.getRotationalError() + ", " + revolverMotor.getMotorEncoder().getPosition());
        }

        revolverMotor.setPower(gamepad.dpadUp.isPressed() ? 0.03 : (gamepad.dpadDown.isPressed()) ? -0.03 : 0);

        intakeMotor.setPower(gamepad.leftTrigger.isPressed() ? -1 : 0);
    }
}
