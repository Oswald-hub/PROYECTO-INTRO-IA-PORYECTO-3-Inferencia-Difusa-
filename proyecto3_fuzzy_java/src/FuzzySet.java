/**
 * Representa un conjunto difuso asociado a una variable linguistica.
 * Guarda su ultimo grado de pertenencia calculado durante la fuzzificacion.
 */
public abstract class FuzzySet {
    protected final String name;
    protected double lastMembership;

    public FuzzySet(String name) {
        this.name = name;
        this.lastMembership = 0.0;
    }

    public String getName() {
        return name;
    }

    public double getLastMembership() {
        return lastMembership;
    }

    public void setLastMembership(double lastMembership) {
        this.lastMembership = clamp01(lastMembership);
    }

    /**
     * Calcula el grado de pertenencia de x al conjunto difuso.
     */
    public abstract double membership(double x);

    /**
     * Calcula y guarda internamente el grado de pertenencia.
     */
    public double fuzzify(double x) {
        this.lastMembership = clamp01(membership(x));
        return this.lastMembership;
    }

    protected double clamp01(double value) {
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }

    public abstract String describe();
}
