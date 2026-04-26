import java.util.LinkedHashMap;
import java.util.Map;

/** Resultado completo de una inferencia Mamdani. */
public class InferenceResult {
    private final double crispOutput;
    private final Map<String, Double> aggregatedOutputDegrees;
    private final String trace;

    public InferenceResult(double crispOutput, Map<String, Double> aggregatedOutputDegrees, String trace) {
        this.crispOutput = crispOutput;
        this.aggregatedOutputDegrees = new LinkedHashMap<>(aggregatedOutputDegrees);
        this.trace = trace;
    }

    public double getCrispOutput() {
        return crispOutput;
    }

    public Map<String, Double> getAggregatedOutputDegrees() {
        return aggregatedOutputDegrees;
    }

    public String getTrace() {
        return trace;
    }
}
