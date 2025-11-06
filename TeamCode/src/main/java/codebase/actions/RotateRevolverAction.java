package codebase.actions;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

import codebase.hardware.Motor;

public class RotateRevolverAction extends DcMotorToPositionAction {

    private static Motor revolverMotor;
    private static final double MAX_ROTATIONAL_ERROR = (Math.PI / 180) / 2;

    private static final PIDCoefficients PID_COEFFICIENTS = new PIDCoefficients(0.002, 0, 0);

    /**
     * @param chamberNumber the chamber number to rotate to (0-2)
     * @param revolverMode either input or output, due to offset for outputting
     * @param rotationalSpeed speed multiplier for the rotation
     */
    public RotateRevolverAction(int chamberNumber, RevolverMode revolverMode, double rotationalSpeed) {
        super(revolverMotor, RotateRevolverAction.getRotationForChamber(chamberNumber, revolverMode), rotationalSpeed, MAX_ROTATIONAL_ERROR, PID_COEFFICIENTS);
    }

    /**
     * Get the rotation in radians to rotate to the target chamber
     * @param chamberNumber the chamber to rotate to (0-2)
     * @param revolverMode either input or output, due to offset for outputting
     * @return the rotation, in radians,
     */
    private static double getRotationForChamber(int chamberNumber, RevolverMode revolverMode) {
        return (chamberNumber / 3.0) * (Math.PI * 2) + (revolverMode == RevolverMode.OUTPUT ? Math.PI : 0);
    }

    public static void setRevolverMotor(Motor revolverMotor) {
        RotateRevolverAction.revolverMotor = revolverMotor;
    }

    public enum RevolverMode {
        OUTPUT,
        INPUT
    }
}
