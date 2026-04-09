package codebase;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import com.qualcomm.robotcore.hardware.ServoImpl;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;
import java.util.Locale;

import codebase.gamepad.Gamepad;

@TeleOp(name="Servo Test Teleop")
public class ServoTestTeleop extends OpMode {
    private Gamepad gamepad;
    private List<ServoImpl> servos;


    private Telemetry.Item currentServoIndexTelemetry;
    private Telemetry.Item currentServoPositionTelemetry;

    private int currentServoIndex = 0;

    private double currentServoPosition = 0;

    @Override
    public void init() {
        for (ServoImpl servo : hardwareMap.getAll(ServoImpl.class)) {
            servos.add(servo);
        }

        if (servos.isEmpty()) {
            throw new IllegalStateException("No servos found :(");
        }

        gamepad.xButton.onPress(() -> currentServoIndex = (currentServoIndex - 1 + servos.size()) % servos.size());
        gamepad.bButton.onPress(() -> currentServoIndex = (currentServoIndex + 1) % servos.size());

        gamepad.rightTrigger.onPress(() -> {
            updateServoPosition(0.1);
        });

        gamepad.leftTrigger.onPress(() -> {
            updateServoPosition(-0.1);
        });

        gamepad.rightBumper.onPress(() -> {
            updateServoPosition(0.01);
        });

        gamepad.leftBumper.onPress(() -> {
            updateServoPosition(-0.01);
        });

        telemetry.addLine("--- Servo Test Controls ---");
        telemetry.addLine("X/B: prev/next servo");
        telemetry.addLine("Left/Right Trigger: -/+ position by 0.01");
        telemetry.addLine("Left/Right Bumper: -/+ position by 0.1");
        telemetry.addLine("---------------------------");

        currentServoIndexTelemetry = telemetry.addData("Current Index", currentServoIndex);
        currentServoPositionTelemetry = telemetry.addData("Current Position", currentServoPosition);

    }

    private void updateServoPosition(double increaseBy) {
        currentServoPosition = Math.max(0, Math.min(1, currentServoPosition + increaseBy));
    }

    @Override
    public void loop() {
        gamepad.loop();
        currentServoIndexTelemetry.setValue(currentServoIndex);
        currentServoPositionTelemetry.setValue(currentServoPosition);
    }
}
