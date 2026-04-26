/**
 * Conjunto difuso trapezoidal definido por cuatro puntos: a, b, c y d.
 * [a,b] es la subida, [b,c] es la meseta con pertenencia 1, [c,d] es la bajada.
 * Permite hombros cuando a == b o c == d.
 */
public class TrapezoidalFuzzySet extends FuzzySet {
    private final double a;
    private final double b;
    private final double c;
    private final double d;

    public TrapezoidalFuzzySet(String name, double a, double b, double c, double d) {
        super(name);
        if (!(a <= b && b <= c && c <= d)) {
            throw new IllegalArgumentException("En trapezoidal se requiere a <= b <= c <= d");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double membership(double x) {
        if (x < a || x > d) return 0.0;

        // Zona plana superior.
        if (x >= b && x <= c) return 1.0;

        // Subida. Si a == b, se trata como hombro izquierdo.
        if (x >= a && x < b) {
            if (b == a) return 1.0;
            return (x - a) / (b - a);
        }

        // Bajada. Si c == d, se trata como hombro derecho.
        if (x > c && x <= d) {
            if (d == c) return 1.0;
            return (d - x) / (d - c);
        }

        return 0.0;
    }

    @Override
    public String describe() {
        return String.format("%s TRAPEZOIDAL(%.2f, %.2f, %.2f, %.2f) ultimo_mu=%.4f", name, a, b, c, d, lastMembership);
    }
}
