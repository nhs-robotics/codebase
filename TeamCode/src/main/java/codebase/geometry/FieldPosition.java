package codebase.geometry;


import androidx.annotation.NonNull;

public class FieldPosition extends Point {

    /**
     * The direction of the field position defined as radians counterclockwise from directly to the right of the field
     */
    public double direction;

    public FieldPosition(double x, double y, double direction) {
        super(x, y);
        this.direction = direction;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("(%s, %s), facing %s", this.x, this.y, this.direction);
    }
}