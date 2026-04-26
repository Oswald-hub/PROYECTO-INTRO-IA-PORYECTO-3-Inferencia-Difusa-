import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Punto de entrada del Proyecto 3 - Inferencia Difusa.
 *
 * Uso recomendado:
 *   javac -d out src/*.java
 *   java -cp out Main data/variables.txt data/rules.txt temperatura=115 presion=70
 *
 * Si no se envian argumentos, se ejecuta el caso de prueba por defecto.
 */
public class Main {
    public static void main(String[] args) {
        try {
            Path variablesPath = args.length >= 1 ? Path.of(args[0]) : Path.of("data/variables.txt");
            Path rulesPath = args.length >= 2 ? Path.of(args[1]) : Path.of("data/rules.txt");

            Map<String, Double> crispInputs = new LinkedHashMap<>();
            if (args.length >= 3) {
                for (int i = 2; i < args.length; i++) {
                    String[] pair = args[i].split("=");
                    if (pair.length != 2) {
                        throw new IllegalArgumentException("Entrada invalida: " + args[i] + ". Use nombre=valor");
                    }
                    crispInputs.put(pair[0].trim(), Double.parseDouble(pair[1].trim()));
                }
            } else {
                // Caso de prueba similar al ejemplo de clase de la caldera.
                crispInputs.put("temperatura", 115.0);
                crispInputs.put("presion", 70.0);
            }

            FuzzySystemLoader loader = new FuzzySystemLoader();
            FuzzySystem system = loader.loadSystem(variablesPath, rulesPath);

            System.out.println("========== PROYECTO 3 - INFERENCIA DIFUSA ==========");
            System.out.println("Archivos cargados:");
            System.out.println("  Variables: " + variablesPath.toAbsolutePath());
            System.out.println("  Reglas:    " + rulesPath.toAbsolutePath());
            System.out.println();

            System.out.println(system.describeVariables());
            System.out.println(system.getKnowledgeBase().describe());

            MamdaniInferenceEngine engine = new MamdaniInferenceEngine(system);
            InferenceResult result = engine.infer(crispInputs);

            System.out.println(result.getTrace());
            System.out.println("Grados agregados de salida:");
            for (Map.Entry<String, Double> entry : result.getAggregatedOutputDegrees().entrySet()) {
                System.out.printf("  %s -> %.4f%n", entry.getKey(), entry.getValue());
            }
            System.out.printf("Salida final defuzzificada = %.4f%n", result.getCrispOutput());
        } catch (Exception ex) {
            System.err.println("Error ejecutando el sistema difuso: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
