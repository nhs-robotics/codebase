package codebase.geometry;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class FieldPosition extends Point {

    /**
     * The direction of the field position defined as radians counterclockwise from directly to the right of the field
     */
    private double direction;

    public FieldPosition(double x, double y, double direction, AngleUnit angleUnit) {
        super(x, y);
        setDirection(direction, angleUnit);
    }

    public void setDirection(double direction, AngleUnit angleUnit) {
        this.direction = angleUnit.toRadians(direction);
    }

    public double getDirection(AngleUnit angleUnit) {
        return angleUnit.fromRadians(direction);
    }

    public Pose2D toPose2D() {
        return new Pose2D(DistanceUnit.INCH, this.getX(), this.getY(), AngleUnit.RADIANS, this.getDirection(AngleUnit.RADIANS));
    }
}