/** Condicion linguistica de una regla: variable IS conjunto. */
public class FuzzyCondition {
    private final String variableName;
    private final String setName;

    public FuzzyCondition(String variableName, String setName) {
        this.variableName = variableName;
        this.setName = setName;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getSetName() {
        return setName;
    }

    @Override
    public String toString() {
        return variableName + " IS " + setName;
    }
}
