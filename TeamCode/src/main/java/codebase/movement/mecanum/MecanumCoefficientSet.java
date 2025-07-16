package codebase.movement.mecanum;

public class MecanumCoefficientSet {
    public double fr;
    public double fl;
    public double bl;
    public double br;

    public MecanumCoefficientSet(
            double fr,
            double fl,
            double bl,
            double br
    ) {
        this.fr = fr;
        this.fl = fl;
        this.bl = bl;
        this.br = br;
    }

    public MecanumCoefficientSet(double[] coefficient) {
        this.fr = coefficient[0];
        this.fl = coefficient[1];
        this.bl = coefficient[2];
        this.br = coefficient[3];
    }
}
