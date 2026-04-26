import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Cargador de archivos de texto para variables linguisticas y reglas difusas.
 *
 * Formato de variables:
 * VARIABLE;nombre;tipo;min;max
 * SET;nombreConjunto;TRIANGULAR;a;b;c
 * SET;nombreConjunto;TRAPEZOIDAL;a;b;c;d
 *
 * Formato de reglas:
 * IF temperatura IS Fresco AND presion IS Baja THEN accion IS PM
 */
public class FuzzySystemLoader {

    public FuzzySystem loadSystem(Path variablesPath, Path rulesPath) throws IOException {
        FuzzySystem system = loadVariables(variablesPath);
        KnowledgeBase knowledgeBase = loadRules(rulesPath);
        system.setKnowledgeBase(knowledgeBase);
        validateRules(system);
        return system;
    }

    public FuzzySystem loadVariables(Path variablesPath) throws IOException {
        FuzzySystem system = new FuzzySystem();
        LinguisticVariable currentVariable = null;

        try (BufferedReader reader = Files.newBufferedReader(variablesPath)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = clean(line);
                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                String tag = parts[0].trim().toUpperCase();

                if (tag.equals("VARIABLE")) {
                    if (parts.length != 5) {
                        throw new IllegalArgumentException("Linea " + lineNumber + ": VARIABLE requiere 5 campos");
                    }
                    String name = parts[1].trim();
                    VariableType type = VariableType.fromText(parts[2]);
                    double min = Double.parseDouble(parts[3].trim());
                    double max = Double.parseDouble(parts[4].trim());
                    currentVariable = new LinguisticVariable(name, type, min, max);
                    system.addVariable(currentVariable);
                } else if (tag.equals("SET")) {
                    if (currentVariable == null) {
                        throw new IllegalArgumentException("Linea " + lineNumber + ": SET sin VARIABLE previa");
                    }
                    FuzzySet set = parseSet(parts, lineNumber);
                    currentVariable.addSet(set);
                } else {
                    throw new IllegalArgumentException("Linea " + lineNumber + ": etiqueta no reconocida: " + tag);
                }
            }
        }
        return system;
    }

    public KnowledgeBase loadRules(Path rulesPath) throws IOException {
        KnowledgeBase knowledgeBase = new KnowledgeBase();

        try (BufferedReader reader = Files.newBufferedReader(rulesPath)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = clean(line);
                if (line.isEmpty()) continue;
                knowledgeBase.addRule(parseRule(line, lineNumber));
            }
        }
        return knowledgeBase;
    }

    private FuzzySet parseSet(String[] parts, int lineNumber) {
        if (parts.length < 5) {
            throw new IllegalArgumentException("Linea " + lineNumber + ": SET incompleto");
        }
        String name = parts[1].trim();
        String shape = parts[2].trim().toUpperCase();

        if (shape.equals("TRIANGULAR")) {
            if (parts.length != 6) {
                throw new IllegalArgumentException("Linea " + lineNumber + ": TRIANGULAR requiere a;b;c");
            }
            double a = Double.parseDouble(parts[3].trim());
            double b = Double.parseDouble(parts[4].trim());
            double c = Double.parseDouble(parts[5].trim());
            return new TriangularFuzzySet(name, a, b, c);
        }

        if (shape.equals("TRAPEZOIDAL")) {
            if (parts.length != 7) {
                throw new IllegalArgumentException("Linea " + lineNumber + ": TRAPEZOIDAL requiere a;b;c;d");
            }
            double a = Double.parseDouble(parts[3].trim());
            double b = Double.parseDouble(parts[4].trim());
            double c = Double.parseDouble(parts[5].trim());
            double d = Double.parseDouble(parts[6].trim());
            return new TrapezoidalFuzzySet(name, a, b, c, d);
        }

        throw new IllegalArgumentException("Linea " + lineNumber + ": tipo de conjunto no soportado: " + shape);
    }

    private FuzzyRule parseRule(String line, int lineNumber) {
        String[] tokens = line.trim().split("\\s+");
        // IF var1 IS set1 AND/OR var2 IS set2 THEN varOut IS setOut
        if (tokens.length != 12) {
            throw new IllegalArgumentException("Linea " + lineNumber + ": la regla debe tener el formato IF v1 IS c1 AND/OR v2 IS c2 THEN vOut IS cOut");
        }
        if (!tokens[0].equalsIgnoreCase("IF") || !tokens[2].equalsIgnoreCase("IS") ||
                !tokens[6].equalsIgnoreCase("IS") || !tokens[8].equalsIgnoreCase("THEN") ||
                !tokens[10].equalsIgnoreCase("IS")) {
            throw new IllegalArgumentException("Linea " + lineNumber + ": palabras reservadas invalidas en la regla");
        }

        String variable1 = tokens[1];
        String set1 = tokens[3];
        FuzzyOperator operator = FuzzyOperator.valueOf(tokens[4].toUpperCase());
        String variable2 = tokens[5];
        String set2 = tokens[7];
        String outputVariable = tokens[9];
        String outputSet = tokens[11];

        List<FuzzyCondition> antecedents = new ArrayList<>();
        antecedents.add(new FuzzyCondition(variable1, set1));
        antecedents.add(new FuzzyCondition(variable2, set2));
        FuzzyCondition consequent = new FuzzyCondition(outputVariable, outputSet);
        return new FuzzyRule(antecedents, operator, consequent);
    }

    private void validateRules(FuzzySystem system) {
        for (FuzzyRule rule : system.getKnowledgeBase().getRules()) {
            for (FuzzyCondition condition : rule.getAntecedents()) {
                LinguisticVariable variable = system.getVariable(condition.getVariableName());
                if (!variable.isInput()) {
                    throw new IllegalArgumentException("La variable de antecedente debe ser INPUT: " + condition.getVariableName());
                }
                variable.getSet(condition.getSetName());
            }

            FuzzyCondition consequent = rule.getConsequent();
            LinguisticVariable output = system.getVariable(consequent.getVariableName());
            if (!output.isOutput()) {
                throw new IllegalArgumentException("La variable consecuente debe ser OUTPUT: " + consequent.getVariableName());
            }
            output.getSet(consequent.getSetName());
        }
    }

    private String clean(String line) {
        int commentIndex = line.indexOf('#');
        if (commentIndex >= 0) {
            line = line.substring(0, commentIndex);
        }
        return line.trim();
    }
}
