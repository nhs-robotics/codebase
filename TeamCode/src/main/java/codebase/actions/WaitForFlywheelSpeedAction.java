package codebase.actions;

import codebase.manipulators.FlywheelManipulator;

public class WaitForFlywheelSpeedAction implements Action {

    private final FlywheelManipulator flywheelManipulator;
    public WaitForFlywheelSpeedAction(FlywheelManipulator flywheelManipulator) {
        this.flywheelManipulator = flywheelManipulator;
    }

    @Override
    public void init() {}

    @Override
    public boolean isComplete() {
        return flywheelManipulator.isAtTargetVelocity();
    }

    @Override
    public void loop() {}
}
