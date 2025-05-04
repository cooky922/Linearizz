package ccc102.linearizz.tokens;

public class ConstantTermToken extends Token {
    private final double value;

    public ConstantTermToken(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ConstTerm(" + value + ")";
    }
}
