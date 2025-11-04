package decode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ServoImpl;
import codebase.actions.SetServoRotationAction;
import codebase.actions.SimultaneousAction;
import codebase.gamepad.Gamepad;

@TeleOp(name="Servo Test Teleop")
public class ServoTestTeleop extends OpMode {
    private Gamepad gamepad;
    private SimultaneousAction actionThread;
    private ServoImpl launchServo;

    private double servoPosition = 0;

    @Override
    public void init() {
        launchServo = hardwareMap.get(ServoImpl.class, "launchServo");

        gamepad = new Gamepad(gamepad1);
        actionThread = new SimultaneousAction();

        gamepad.dpadRight.onPress(() -> {
            launchServo.setPosition(0.01);
            servoPosition += 0.01;
            updateServoPosition();
        });

        gamepad.dpadLeft.onPress(() -> {
            launchServo.setPosition(0.00);
            servoPosition -= 0.01;
            updateServoPosition();
        });

        gamepad.rightTrigger.onPress(() -> {
            servoPosition += 0.1;
            updateServoPosition();
        });

        gamepad.leftTrigger.onPress(() -> {
            servoPosition -= 0.1;
            updateServoPosition();
        });
    }

    private void updateServoPosition() {
        actionThread.add(
                new SetServoRotationAction(
                        launchServo,
                        servoPosition
                ),
                true,
                true
        );
    }

    @Override
    public void loop() {
        gamepad.loop();
        actionThread.loop();
    }
}