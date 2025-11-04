package codebase.sensors;

import com.qualcomm.robotcore.hardware.DcMotorEx;

public class MotorEncoder implements Encoder {
    private final DcMotorEx encoder;
    private final double ticksPerRotation;

    public MotorEncoder(DcMotorEx encoder, double ticksPerRotation) {
        this.encoder = encoder;
        this.ticksPerRotation = ticksPerRotation;
        this.encoder.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }

    public double getTicks() {
        return this.encoder.getCurrentPosition();
    }

    /**
     * Gets the encoder position in radians.
     * @return The position of the encoder in radians.
     */
    public double getPosition() {
        return (getTicks() / ticksPerRotation) * Math.PI * 2;
    }

    public void reset() {
        this.encoder.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        this.encoder.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }
}