import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Motor de inferencia fuzzy tipo Mamdani.
 * Implementa:
 * 1) Fuzzificacion de entradas.
 * 2) Evaluacion de reglas con AND=min y OR=max.
 * 3) Implicacion Mamdani por recorte: min(activacion, mu_salida(x)).
 * 4) Agregacion de salidas por max.
 * 5) Defuzzificacion por centroide numerico.
 */
public class MamdaniInferenceEngine {
    private final FuzzySystem system;
    private final int centroidSamples;

    public MamdaniInferenceEngine(FuzzySystem system) {
        this(system, 1000);
    }

    public MamdaniInferenceEngine(FuzzySystem system, int centroidSamples) {
        if (centroidSamples <= 0) {
            throw new IllegalArgumentException("El numero de muestras para centroide debe ser positivo");
        }
        this.system = system;
        this.centroidSamples = centroidSamples;
    }

    public InferenceResult infer(Map<String, Double> crispInputs) {
        StringBuilder trace = new StringBuilder();
        trace.append("========== TRAZA DEL MOTOR MAMDANI ==========").append(System.lineSeparator());

        // 1. Fuzzificacion.
        trace.append("\n[FASE 1] Fuzzificacion de entradas\n");
        for (LinguisticVariable variable : system.getVariables()) {
            if (variable.isInput()) {
                if (!containsInput(crispInputs, variable.getName())) {
                    throw new IllegalArgumentException("Falta valor crisp para la entrada: " + variable.getName());
                }
                double value = getInputValue(crispInputs, variable.getName());
                trace.append(String.format("Entrada %s = %.4f%n", variable.getName(), value));
                Map<String, Double> memberships = variable.fuzzify(value);
                for (Map.Entry<String, Double> entry : memberships.entrySet()) {
                    trace.append(String.format("  mu_%s(%.4f) = %.4f%n", entry.getKey(), value, entry.getValue()));
                }
            }
        }

        // 2. Evaluacion de reglas y agregacion por conjunto de salida.
        trace.append("\n[FASE 2 y 3] Evaluacion de reglas e inferencia\n");
        Map<String, Double> aggregatedDegrees = new LinkedHashMap<>();
        int ruleNumber = 1;
        for (FuzzyRule rule : system.getKnowledgeBase().getRules()) {
            FuzzyCondition c1 = rule.getAntecedents().get(0);
            FuzzyCondition c2 = rule.getAntecedents().get(1);

            double mu1 = system.getVariable(c1.getVariableName()).membershipOf(c1.getSetName());
            double mu2 = system.getVariable(c2.getVariableName()).membershipOf(c2.getSetName());
            double activation = rule.getOperator().apply(mu1, mu2);

            String outputSet = rule.getConsequent().getSetName();
            double previous = aggregatedDegrees.getOrDefault(outputSet, 0.0);
            double aggregated = Math.max(previous, activation);
            aggregatedDegrees.put(outputSet, aggregated);

            trace.append(String.format("R%d: %s%n", ruleNumber, rule));
            trace.append(String.format("  antecedente 1 = %.4f, antecedente 2 = %.4f, operador=%s, activacion=%.4f%n",
                    mu1, mu2, rule.getOperator(), activation));
            trace.append(String.format("  salida %s: max(%.4f, %.4f) = %.4f%n", outputSet, previous, activation, aggregated));
            ruleNumber++;
        }

        // 4. Defuzzificacion por centroide.
        trace.append("\n[FASE 4] Defuzzificacion por centroide\n");
        LinguisticVariable outputVariable = system.getOutputVariable();
        double crispOutput = centroid(outputVariable, aggregatedDegrees, trace);
        trace.append(String.format("Resultado crisp final para %s = %.4f%n", outputVariable.getName(), crispOutput));
        trace.append("============================================\n");

        return new InferenceResult(crispOutput, aggregatedDegrees, trace.toString());
    }

    private double centroid(LinguisticVariable outputVariable, Map<String, Double> aggregatedDegrees, StringBuilder trace) {
        double min = outputVariable.getMin();
        double max = outputVariable.getMax();
        double step = (max - min) / centroidSamples;
        double numerator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i <= centroidSamples; i++) {
            double x = min + i * step;
            double aggregatedMu = 0.0;

            for (Map.Entry<String, Double> entry : aggregatedDegrees.entrySet()) {
                double activation = entry.getValue();
                FuzzySet outputSet = outputVariable.getSet(entry.getKey());
                double clippedMu = Math.min(activation, outputSet.membership(x));
                aggregatedMu = Math.max(aggregatedMu, clippedMu);
            }

            numerator += x * aggregatedMu;
            denominator += aggregatedMu;
        }

        trace.append(String.format("  Numerador centroide = %.6f%n", numerator));
        trace.append(String.format("  Denominador centroide = %.6f%n", denominator));

        if (denominator == 0.0) {
            trace.append("  Advertencia: ninguna regla se activo; no hay salida defuzzificada.\n");
            return Double.NaN;
        }
        return numerator / denominator;
    }

    private boolean containsInput(Map<String, Double> inputs, String variableName) {
        for (String key : inputs.keySet()) {
            if (key.trim().equalsIgnoreCase(variableName.trim())) return true;
        }
        return false;
    }

    private double getInputValue(Map<String, Double> inputs, String variableName) {
        for (Map.Entry<String, Double> entry : inputs.entrySet()) {
            if (entry.getKey().trim().equalsIgnoreCase(variableName.trim())) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("Falta entrada: " + variableName);
    }
}
