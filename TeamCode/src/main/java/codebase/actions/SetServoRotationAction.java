package codebase.actions;

import com.qualcomm.robotcore.hardware.Servo;

public class SetServoRotationAction implements Action {

    private final Servo servo;
    private final double position;

    /**
     * @param servo the servo to set the position of
     * @param position the target position in the range [0.0, 1.0]
     */
    public SetServoRotationAction(Servo servo, double position) {
        this.servo = servo;
        this.position = position;
    }

    @Override
    public void init() {}

    @Override
    public boolean isComplete() {
        return servo.getPosition() == position;
    }

    @Override
    public void loop() {
        servo.setPosition(position);
    }
}
