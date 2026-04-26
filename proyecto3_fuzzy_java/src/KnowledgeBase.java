import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base de conocimiento del sistema difuso.
 * Almacena las reglas y permite visualizarlas en formato texto.
 */
public class KnowledgeBase {
    private final List<FuzzyRule> rules;

    public KnowledgeBase() {
        this.rules = new ArrayList<>();
    }

    public void addRule(FuzzyRule rule) {
        rules.add(rule);
    }

    public List<FuzzyRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /** Visualizacion textual exigida por la fase 2 del proyecto. */
    public String describe() {
        StringBuilder sb = new StringBuilder();
        sb.append("Base de conocimiento - Reglas cargadas:\n");
        for (int i = 0; i < rules.size(); i++) {
            sb.append(String.format("  R%d: %s%n", i + 1, rules.get(i)));
        }
        return sb.toString();
    }
}
