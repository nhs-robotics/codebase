package codebase.actions;

import codebase.hardware.Motor;

public class SetMotorPowerAction implements Action {

    private final Motor motor;
    private final double power;

    public SetMotorPowerAction(Motor motor, double power) {
        this.motor = motor;
        this.power = power;
    }

    @Override
    public void init() {}

    @Override
    public boolean isComplete() {
        return motor.getPower() == power;
    }

    @Override
    public void loop() {
        motor.setPower(power);
    }
}
