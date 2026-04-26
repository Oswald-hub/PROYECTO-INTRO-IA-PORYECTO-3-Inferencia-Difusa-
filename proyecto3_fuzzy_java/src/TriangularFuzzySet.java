/**
 * Conjunto difuso triangular definido por tres puntos: a, b y c.
 * a: inicio con pertenencia 0
 * b: punto central con pertenencia 1
 * c: fin con pertenencia 0
 */
public class TriangularFuzzySet extends FuzzySet {
    private final double a;
    private final double b;
    private final double c;

    public TriangularFuzzySet(String name, double a, double b, double c) {
        super(name);
        if (!(a <= b && b <= c)) {
            throw new IllegalArgumentException("En triangular se requiere a <= b <= c");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double membership(double x) {
        if (x < a || x > c) return 0.0;
        if (x == b) return 1.0;

        // Hombro izquierdo: a == b
        if (a == b && x >= a && x <= b) return 1.0;

        // Hombro derecho: b == c
        if (b == c && x >= b && x <= c) return 1.0;

        if (x > a && x < b) {
            return (x - a) / (b - a);
        }
        if (x > b && x < c) {
            return (c - x) / (c - b);
        }
        return 0.0;
    }

    @Override
    public String describe() {
        return String.format("%s TRIANGULAR(%.2f, %.2f, %.2f) ultimo_mu=%.4f", name, a, b, c, lastMembership);
    }
}
