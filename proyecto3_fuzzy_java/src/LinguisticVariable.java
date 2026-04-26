import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa una variable linguistica fuzzy.
 * Contiene su universo de discurso y los conjuntos difusos que definen sus etiquetas.
 */
public class LinguisticVariable {
    private final String name;
    private final VariableType type;
    private final double min;
    private final double max;
    private final Map<String, FuzzySet> setsByName;

    public LinguisticVariable(String name, VariableType type, double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("El minimo debe ser menor que el maximo para la variable " + name);
        }
        this.name = name;
        this.type = type;
        this.min = min;
        this.max = max;
        this.setsByName = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public VariableType getType() {
        return type;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public void addSet(FuzzySet set) {
        String key = normalize(set.getName());
        if (setsByName.containsKey(key)) {
            throw new IllegalArgumentException("Conjunto repetido en " + name + ": " + set.getName());
        }
        setsByName.put(key, set);
    }

    public FuzzySet getSet(String setName) {
        FuzzySet set = setsByName.get(normalize(setName));
        if (set == null) {
            throw new IllegalArgumentException("No existe el conjunto '" + setName + "' en la variable '" + name + "'");
        }
        return set;
    }

    public List<FuzzySet> getSets() {
        return Collections.unmodifiableList(new ArrayList<>(setsByName.values()));
    }

    /**
     * Fuzzifica un valor crisp y guarda los grados de pertenencia en cada conjunto.
     */
    public Map<String, Double> fuzzify(double value) {
        Map<String, Double> result = new LinkedHashMap<>();
        for (FuzzySet set : setsByName.values()) {
            double mu = set.fuzzify(value);
            result.put(set.getName(), mu);
        }
        return result;
    }

    public double membershipOf(String setName) {
        return getSet(setName).getLastMembership();
    }

    public boolean isInput() {
        return type == VariableType.INPUT;
    }

    public boolean isOutput() {
        return type == VariableType.OUTPUT;
    }

    /**
     * Visualizacion textual exigida por la fase 1 del proyecto.
     */
    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Variable %s [%s] universo=[%.2f, %.2f]%n", name, type, min, max));
        for (FuzzySet set : setsByName.values()) {
            sb.append("  - ").append(set.describe()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    private String normalize(String text) {
        return text.trim().toLowerCase();
    }
}
