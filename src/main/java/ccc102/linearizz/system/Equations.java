package ccc102.linearizz.system;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import ccc102.linearizz.exceptions.EquationException;
import ccc102.linearizz.exceptions.LinearSystemException;
import ccc102.linearizz.tokens.*;
import ccc102.linearizz.Vector;
import ccc102.linearizz.Matrix;

public class Equations {
    // stores variables or refers to existing variable set
    private Variables variables;

    // true  -> automatically registers variables
    // false -> uses existing variable set
    private boolean autoAdd;

    // List<List<Token>> -> list of equation tokens
    // - List<Token>     -> list of tokens per equation
    private List<List<Token>> equationTokens;

    public Equations() {
        variables      = new Variables();
        autoAdd        = true;
        equationTokens = new ArrayList<>();
    }

    public Equations(Variables vars) {
        variables      = vars;
        autoAdd        = false;
        equationTokens = new ArrayList<>();
    }

    /// queries
    public Variables getVariables() {
        return variables;
    }

    public int count() {
        return equationTokens.size();
    }

    // clears all equations
    public void clear() {
        if (autoAdd)
            variables.clear();
        equationTokens.clear();
    }

    // add an equation
    public void add(String equation) throws EquationException {
        equationTokens.add(parseEquation(equation.trim()));
    }

    // add equations
    public void addAll(String[] c) throws EquationException {
        for (String equation : c)
            add(equation);
    }

    public void addAll(Collection<? extends String> c) throws EquationException {
        for (String equation : c)
            add(equation);
    }

    // extract values 
    public ExtractResult extractValues() throws LinearSystemException {
        // check for unknown and equation count
        final int unknownCount  = variables.count();
        final int equationCount = this.count();
        if (equationCount < unknownCount)
            throw new LinearSystemException(LinearSystemException.Kind.Underdetermined);
        else if (equationCount > unknownCount)
            throw new LinearSystemException(LinearSystemException.Kind.Overdetermined);

        // so expect it to produce square matrix from now on
        final int N = equationCount;

        // STEP 1: extract the coefficients and constants from the tokens
        // STEP 2: store coefficients and constants in Matrix and Vector
        Map<String, double[]> coeffTable = new HashMap<>();
        double[] constArray = new double[N];

        // insert variable names first with empty array
        for (String variableName : variables.getNameSet())
            coeffTable.put(variableName, new double[N]);

        int index = 0;
        for (List<Token> tokens : equationTokens) {
            // STEP 1: extract the coefficients and constants from the tokens
            boolean leftSide = true; // true if leftSide; false if rightSide
            for (Token token : tokens) {
                if (token instanceof VariableTermToken varToken) {
                    double[] arr = coeffTable.get(varToken.getVariableName());
                    int sign = leftSide ? 1 : -1;
                    arr[index] += sign * varToken.getCoefficient();
                } else if (token instanceof ConstantTermToken constToken) {
                    int sign = leftSide ? -1 : 1;
                    constArray[index] += sign * constToken.getValue();
                } else if (token instanceof EqualToken) {
                    leftSide = false;
                }
            }
            ++index;
        }
        // STEP 2: store coefficients and constants in Matrix and Vector
        double[][] coefficientsPerVariable = new double[N][];
        String[] variableNames = variables.getNames(); // already sorted by indexx
        for (index = 0; index < variableNames.length;  ++index)
            coefficientsPerVariable[index] = coeffTable.get(variableNames[index]);

        // STEP N: returns the extracted result
        return new ExtractResult(
            new Matrix(coefficientsPerVariable).transpose(),
            new Vector(constArray)
        );
    }

    /// Helpers .................
    // parses and tokenizes equation (assumed it is already trimmed) into stream of tokens
    // -------------------------------------
    // Equation Grammar
    // -------------------------------------
    // <equation>
    // | <expr> '=' <expr>
    //
    // <expr>
    // | <term> <term (>= 0) ...>
    //
    // <term>
    // | <constant-term>
    // | <variable-term>
    //
    // <constant-term>
    // | ('+' | '-') ? <number>
    //
    // <variable-term>
    // | ('+' | '-') ? <variable-name>
    // | ('+' | '-') ? <number> <variable-name>
    // 
    // <number>
    // [TODO: add fraction notation]
    // | <digit (>= 1) ...> '.' ?
    // | <digit (>= 0) ...> '.' <digit (>= 1) ...>
    //
    // <variable-name>
    // | <letter> <digit (>= 0) ...>
    private List<Token> parseEquation(String eq) throws EquationException {
        List<Token> tokens = new ArrayList<>();
        Variables newVariables = new Variables();

        int i = 0;
        final int len = eq.length();

        boolean sawEqual = false, sawFirstTerm = false;

        while (i < len) {
            // ignore if whitespace characters
            while (i < len && Character.isWhitespace(eq.charAt(i)))
                ++i;

            // exit if it has reached the end
            if (i >= len)
                break;

            char ch = eq.charAt(i);

            // encounters '='
            if (ch == '=') {
                if (!sawEqual) {
                    if (tokens.isEmpty())
                        throw new EquationException(EquationException.Kind.NoExprBeforeEqual);
                    sawEqual = true;
                } else {
                    throw new EquationException(EquationException.Kind.MoreEqual);
                }
                tokens.add(new EqualToken());
                sawFirstTerm = false;
                ++i;
                continue;
            }

            // encounters '+' or '-'
            // - optional sign in every first term
            // - requires sign for other terms
            int sign = 1;
            if (ch == '+' || ch == '-') {
                sign = (ch == '-') ? -1 : 1;
                ++i;
                // skip any space after sign
                while (i < len && Character.isWhitespace(eq.charAt(i)))
                    ++i;
            } else if (sawFirstTerm) {
                throw new EquationException(EquationException.Kind.UnseparatedTerms);
            }

            // try to parse a number
            double num = 0;
            int numStartIdx = i;
            boolean sawDigit = false, sawDot = false;
            while (i < len) {
                char d = eq.charAt(i);
                if (Character.isDigit(d)) {
                    sawDigit = true;
                    ++i;
                } else if (d == '.' && !sawDot) {
                    sawDot = true;
                    ++i;
                } else {
                    break;
                }
            }
            if (sawDigit) {
                String numText = eq.substring(numStartIdx, i);
                num = Double.parseDouble(numText);
            }

            // skip whitespaces before checking for variable name
            while (i < len && Character.isWhitespace(eq.charAt(i)))
                ++i;

            // check for variable name
            if (i < len && Character.isLetter(eq.charAt(i))) {
                int nameStartIdx = i;
                ++i;
                if (i < len && Character.isLetter(eq.charAt(i)))
                    throw new EquationException(EquationException.Kind.InvalidVariableName);
                while (i < len && Character.isDigit(eq.charAt(i)))
                    ++i;
                String name = eq.substring(nameStartIdx, i);

                // if autoAdd = false, throw if it doesn't contain the key
                // if autoAdd = true, add the variable name to the set
                if (autoAdd) {
                    if (!variables.containsName(name) || !newVariables.containsName(name))
                        newVariables.add(name);
                } else {
                    if (!variables.containsName(name))
                        throw new EquationException(EquationException.Kind.NoVariableFound);
                }
                double coeff = sawDigit ? num * sign : 1.0 * sign;
                tokens.add(new VariableTermToken(coeff, name));
                sawFirstTerm = true;
            } else {
                // no variable, so it must be a constant term
                if (!sawDigit)
                    throw new EquationException(EquationException.Kind.InvalidCharacter);
                tokens.add(new ConstantTermToken(num * sign));
                sawFirstTerm = true;
            }
        }

        if (!sawEqual)
            throw new EquationException(EquationException.Kind.NoEqual);
        else if (tokens.get(tokens.size() - 1) instanceof EqualToken)
            throw new EquationException(EquationException.Kind.NoExprAfterEqual);

        if (autoAdd)
            variables.addAll(newVariables);

        return tokens;
    }
}
