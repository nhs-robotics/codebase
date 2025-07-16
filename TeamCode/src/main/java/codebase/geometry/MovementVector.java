package codebase.geometry;

import java.io.Serializable;
import java.util.Objects;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.database.annotations.NotNull;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MovementVector implements Serializable {
    protected double vertical;
    protected double horizontal;
    protected double rotation;
    protected AngleUnit angleUnit;

    public MovementVector(double vertical, double horizontal, double rotation, AngleUnit angleUnit) {
        this.vertical = vertical;
        this.horizontal = horizontal;
        this.rotation = rotation;
        this.angleUnit = angleUnit;
    }

    public MovementVector(double vertical, double horizontal, double rotation) {
        this(vertical, horizontal, rotation, AngleUnit.RADIANS);
    }

    public MovementVector(double[] v, AngleUnit angleUnit) {
        this(v[0], v[1], v[2], angleUnit);
    }

    public MovementVector(@NotNull Vector3D v, AngleUnit angleUnit) {
        this(v.getX(), v.getY(), v.getZ(), angleUnit);
    }

    public double getVertical() {
        return vertical;
    }

    public double getHorizontal() {
        return horizontal;
    }

    public double getRotation() {
        return rotation;
    }

    public AngleUnit getAngleUnit() {
        return this.angleUnit;
    }

    public void setVertical(double vertical) {
        this.vertical = vertical;
    }

    public void setHorizontal(double horizontal) {
        this.horizontal = horizontal;
    }

    public void setRotation(double rotation, AngleUnit angleUnit) {
        this.rotation = rotation;
        this.angleUnit = angleUnit;
    }

    public MovementVector add(@NotNull MovementVector movementVector) {
        return new MovementVector(
                this.vertical + movementVector.vertical,
                this.horizontal + movementVector.horizontal,
                this.angleUnit.toDegrees(this.rotation) + movementVector.angleUnit.toDegrees(movementVector.rotation),
                AngleUnit.DEGREES
        );
    }

    public MovementVector subtract(@NotNull MovementVector movementVector) {
        return new MovementVector(
                this.vertical - movementVector.vertical,
                this.horizontal - movementVector.horizontal,
                this.angleUnit.toDegrees(this.rotation) - movementVector.angleUnit.toDegrees(movementVector.rotation),
                AngleUnit.DEGREES
        );
    }

    public MovementVector scalarMultiply(double magnitude) {
        return new MovementVector(
                this.vertical * magnitude,
                this.horizontal * magnitude,
                this.rotation * magnitude,
                this.angleUnit
        );
    }

    public MovementVector normalize() {
        double magnitude = this.getMagnitude();

        // Check for division by zero
        if (magnitude == 0) {
            throw new ArithmeticException("Cannot normalize a zero vector");
        }

        return new MovementVector(
                this.vertical / magnitude,
                this.horizontal / magnitude,
                this.rotation / magnitude,
                this.angleUnit
        );
    }

    public double getMagnitude() {
        return Math.sqrt(
                this.vertical * this.vertical + this.horizontal * this.horizontal + this.rotation * this.rotation
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MovementVector)) {
            return false;
        }

        MovementVector that = (MovementVector) o;

        return this.vertical == that.vertical && this.horizontal == that.horizontal && this.rotation == that.rotation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertical, horizontal, rotation);
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("{%f; %f; %f}", vertical, horizontal, rotation);
    }
}