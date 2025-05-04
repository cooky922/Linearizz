package ccc102.linearizz.tokens;

public class VariableTermToken extends Token {
    private final double coeff;
    private final String name;

    public VariableTermToken(double coeff, String name) {
        this.coeff = coeff;
        this.name  = name;
    }

    public double getCoefficient() {
        return coeff;
    }

    public String getVariableName() {
        return name;
    }

    @Override
    public String toString() {
        return "VarTerm(" + coeff + ", '" + name + "')";
    }
}