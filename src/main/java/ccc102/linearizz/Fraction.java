package ccc102.linearizz;

public class Fraction {
    private boolean isNegative; // true if negative, false if positive
    private int num; // numerator
    private int denom; // denominator

    // invariants:
    // num >= 0 && denom > 0

    public Fraction(int numerator, int denominator) {
        if (denominator == 0)
            throw ArithmeticException("division by 0");
        isNegative = (numerator * denominator) < 0;
        num = Math.abs(numerator);
        denom = Math.abs(denominator);
        // normalize
        int gcd = computeGCD(num, denom);
        num /= gcd;
        denom /= gcd;
    }

    public static Fraction fromInt(int value) {
        return new Fraction(value, 1);
    }

    public static Fraction fromDecimal(double decimal) {
        final double EPSILON = 1.0E-6;
        int sign = decimal < 0 ? -1 : 1; 
        decimal = Math.abs(decimal);

        int denominator = 1;
        while (Math.abs(decimal * denominator - Math.round(decimal * denominator)) > EPSILON)
            denominator++;
        int numerator = (int) Math.round(decimal * denominator);

        return new Fraction(sign * numerator, denominator);
    }

    public int getNumerator() {
        // get the signed numerator
        return isNegative ? -num : num;
    }

    public int getDenominator() {
        return denom;
    }

    @Override 
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (isNegative)
            builder.append("-");
        builder.append(num);
        if (denom != 1)
            builder.append("/" + denom);
        return builder.toString();
    }

    /// IMPLEMENTATION DETAILS
    private static int computeGCD(int a, int b) {
        return b == 0 ? a : computeGCD(b, a % b);
    }
}