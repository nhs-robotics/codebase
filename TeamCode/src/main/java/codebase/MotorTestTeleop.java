package codebase;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import codebase.gamepad.Gamepad;
import codebase.hardware.Motor;

@TeleOp(name="Motor Test Teleop")
public class MotorTestTeleop extends OpMode {
    private Gamepad gamepad;
    private final List<Motor> motors = new ArrayList<>();


    private Telemetry.Item currentMotorIndexTelemetry;
    private int currentMotorIndex = 0;

    private Telemetry.Item motorModeTelemetry;
    private boolean velocityMode = false;

    private Telemetry.Item motorPowerOrVelocityTargetTelemetry;
    private double targetVelocityTicksPerSecond = 0;

    @Override
    public void init() {
        for (DcMotorEx motor : hardwareMap.getAll(DcMotorEx.class)) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motors.add(new Motor(motor));
        }

        if (motors.isEmpty()) {
            throw new IllegalStateException("No motors found :(");
        }

        gamepad = new Gamepad(gamepad1);

        gamepad.xButton.onPress(() -> currentMotorIndex = (currentMotorIndex - 1 + motors.size()) % motors.size());

        gamepad.bButton.onPress(() -> currentMotorIndex = (currentMotorIndex + 1) % motors.size());
        gamepad.yButton.onPress(() -> velocityMode = !velocityMode);

        gamepad.dpadLeft.onPress(() -> targetVelocityTicksPerSecond -= 50);
        gamepad.dpadRight.onPress(() -> targetVelocityTicksPerSecond += 50);


        telemetry.addLine("--- Motor Test Controls ---");
        telemetry.addLine("X/B: prev/next motor");
        telemetry.addLine("Y: toggle Power/Velocity mode");
        telemetry.addLine("DPad L/R: velocity target -/+50");
        telemetry.addLine("Right Stick Y: set power (If in Power mode)");
        telemetry.addLine("---------------------------");

        currentMotorIndexTelemetry = telemetry.addData("Current Motor", getSelectedMotorOutput());
        motorModeTelemetry = telemetry.addData("Mode", velocityMode ? "Velocity" : "Power");
        motorPowerOrVelocityTargetTelemetry = telemetry.addData("", getCurrentPowerOrVelocityTargetOutput());
    }

    private String getSelectedMotorOutput() {
        return currentMotorIndex + ": " + hardwareMap.getNamesOf(motors.get(currentMotorIndex).getMotor()).iterator().next();
    }
    private String getCurrentPowerOrVelocityTargetOutput() {
        if (velocityMode) {
            return String.format(Locale.US, "Target Velocity: %.2f, Current Velocity: %.2f", targetVelocityTicksPerSecond, motors.get(currentMotorIndex).getVelocity());
        } else {
            return String.format(Locale.US, "Current Power: %.2f", motors.get(currentMotorIndex).getPower());
        }
    }

    @Override
    public void loop() {
        gamepad.loop();
        currentMotorIndexTelemetry.setValue(getSelectedMotorOutput());
        motorModeTelemetry.setValue("Mode", velocityMode ? "Velocity" : "Power");

        if (velocityMode) {
            motors.get(currentMotorIndex).setVelocity(targetVelocityTicksPerSecond);
        } else {
            motors.get(currentMotorIndex).setPower(gamepad.rightJoystick.getY());
        }
    }
}
