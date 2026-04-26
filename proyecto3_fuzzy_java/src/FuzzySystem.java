import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contenedor principal de las variables linguisticas y la base de reglas.
 */
public class FuzzySystem {
    private final Map<String, LinguisticVariable> variablesByName;
    private KnowledgeBase knowledgeBase;

    public FuzzySystem() {
        this.variablesByName = new LinkedHashMap<>();
        this.knowledgeBase = new KnowledgeBase();
    }

    public void addVariable(LinguisticVariable variable) {
        String key = normalize(variable.getName());
        if (variablesByName.containsKey(key)) {
            throw new IllegalArgumentException("Variable repetida: " + variable.getName());
        }
        variablesByName.put(key, variable);
    }

    public LinguisticVariable getVariable(String name) {
        LinguisticVariable variable = variablesByName.get(normalize(name));
        if (variable == null) {
            throw new IllegalArgumentException("No existe la variable '" + name + "'");
        }
        return variable;
    }

    public Collection<LinguisticVariable> getVariables() {
        return variablesByName.values();
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public LinguisticVariable getOutputVariable() {
        for (LinguisticVariable variable : variablesByName.values()) {
            if (variable.isOutput()) return variable;
        }
        throw new IllegalStateException("El sistema no tiene variable de salida");
    }

    public String describeVariables() {
        StringBuilder sb = new StringBuilder();
        sb.append("Variables linguisticas cargadas:\n");
        for (LinguisticVariable variable : variablesByName.values()) {
            sb.append(variable.describe());
        }
        return sb.toString();
    }

    private String normalize(String text) {
        return text.trim().toLowerCase();
    }
}
