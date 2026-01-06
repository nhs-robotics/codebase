package codebase.actions;

import com.qualcomm.robotcore.hardware.PIDCoefficients;

import codebase.controllers.PIDController;
import codebase.geometry.Angles;
import codebase.geometry.FieldPosition;
import codebase.geometry.MovementVector;
import codebase.movement.mecanum.MecanumDriver;
import codebase.pathing.Localizer;

public class MoveToAction implements Action {

    private static final PIDCoefficients MOVEMENT_PID_COEFFICIENTS = new PIDCoefficients(1, 0, 0);
    private static final PIDCoefficients DIRECTION_PID_COEFFICIENTS = new PIDCoefficients(1, 0, 0);
    private final MecanumDriver driver;
    private final Localizer localizer;

    @ActionParameter
    private final FieldPosition destination;

    /**
     * The speed to move horizontally/vertically or some combination of the two in inches/sec
     */
    private final double movementSpeed;

    /**
     * The max rotational speed of the robot in radians/sec
     */
    private final double rotationalSpeed;

    private final double maxDistanceError;
    private final double maxRotationalError;

    private final PIDController xPID;
    private final PIDController yPID;
    private final PIDController directionPID;

    public MoveToAction(MecanumDriver driver, Localizer localizer, FieldPosition destination, double movementSpeed, double rotationalSpeed, double maxDistanceError, double maxRotationalError) {
        this.driver = driver;
        this.localizer = localizer;
        this.destination = destination;
        this.movementSpeed = movementSpeed;
        this.rotationalSpeed = rotationalSpeed;
        this.maxDistanceError = maxDistanceError;
        this.maxRotationalError = maxRotationalError;

        this.xPID = new PIDController(MOVEMENT_PID_COEFFICIENTS, () -> localizer.getCurrentPosition().x, () -> destination.x);
        this.yPID = new PIDController(MOVEMENT_PID_COEFFICIENTS, () -> localizer.getCurrentPosition().y, () -> destination.y);
        this.directionPID = new PIDController(
                DIRECTION_PID_COEFFICIENTS,
                () -> Angles.angleDifference(localizer.getCurrentPosition().direction, destination.direction)
        );
    }

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        double powerX = xPID.getPower();
        double powerY = yPID.getPower();
        double powerRotational = directionPID.getPower();

        MovementVector vector = new MovementVector(
                movementSpeed * powerY,
                movementSpeed * powerX,
                rotationalSpeed * powerRotational);

        this.driver.setAbsoluteVelocity(localizer.getCurrentPosition(), vector);
    }

    @Override
    public boolean isComplete() {
        double distanceError = Math.sqrt(Math.pow(localizer.getCurrentPosition().x - destination.x, 2) + Math.pow(localizer.getCurrentPosition().y - destination.y, 2));
        double rotationalError = Angles.angleDifference(localizer.getCurrentPosition().direction, destination.direction);

        return (distanceError <= maxDistanceError) && (rotationalError <= maxRotationalError);
    }
}
