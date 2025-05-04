package ccc102.linearizz;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import ccc102.linearizz.exceptions.LinearSystemException;
import ccc102.linearizz.exceptions.LinearSystemEnvironmentException;
import ccc102.linearizz.tokens.*;

// how to use?
//  LinearSystemEnvironment env = new LinearSystemEnvironment();
//  env.registerVariableNames(variableNames);
//  env.registerEquations(equations);
//  Matrix A = env.extractCoefficients();
//  Vector b = env.extractConstants();
//  Vector x = LinearSystem.solve(A, b);
public class LinearSystemEnvironment {
    private Map<String, Integer> variableTable = new HashMap<>(); // mapped to its index
    private Matrix coefficients;
    private Vector constants;

    public Set<String> getVariableNames() {
        return variableTable.keySet();
    }

    public String[] getOrderedVariableNames() {
        String[] result = new String[variableTable.size()];
        for (Map.Entry<String, Integer> entry : variableTable.entrySet()) {
            result[entry.getValue().intValue()] = entry.getKey();
        }
        return result;
    }

    public void unregisterVariableNames() {
        variableTable.clear();
    }

    public boolean hasRegisteredVariableNames() {
        return !variableTable.isEmpty();
    }

    public void registerVariableNames(String[] variableNames) {
        // validate each variable name
        for (int i = 0; i < variableNames.length; ++i) {
            String name = variableNames[i];
            if (!isValidVariableName(name))
                throw new LinearSystemEnvironmentException("invalid variable name");
            if (variableTable.containsKey(name))
                throw new LinearSystemEnvironmentException("duplicated variable name");
            variableTable.put(name, i);
        }
    }

    public void unregisterEquations() {
        coefficients = null;
        constants = null;
    }

    // TODO: test only, will be removed soom
    public void parseEquation(String equation) {
        try {
            List<Token> tokens = tokenizeEquation(equation.trim());
            System.out.print("--- Tokens: [");
            boolean printComma = false;
            for (Token token : tokens) {
                if (printComma)
                    System.out.print(", ");
                System.out.print(token);
                printComma = true;
            }
            System.out.println("]");
        } catch (Exception e) {
            System.out.println("--- Error: " + e.getMessage());
        }
    }

    public void registerEquations(String[] equations) {
        // if unknown names are not yet registered
        if (!hasRegisteredVariableNames())
            throw new LinearSystemEnvironmentException("no unknown names registered yet");

        // check for unknown and equation count
        final int unknownCount = variableTable.size();
        final int equationCount = equations.length;
        if (equationCount < unknownCount)
            throw new LinearSystemException(LinearSystemException.Kind.Underdetermined);
        else if (equationCount > unknownCount)
            throw new LinearSystemException(LinearSystemException.Kind.Overdetermined);

        // so expect it to produce square matrix from now on
        final int N = equationCount;

        // parse each equation:
        // - step 1: tokenize equation, validate equations
        // - step 2: take the coefficients and constants from the tokens
        // - step 3: store coefficients and constants in Matrix and Vector

        Map<String, double[]> coeffTable = new HashMap<>();
        double[] constArray = new double[N];

        // insert variable names first with empty array
        for (String variableName : variableTable.keySet())
            coeffTable.put(variableName, new double[N]);

        int index = 0;
        for (String equation : equations) {
            // step 1: tokenize equation (assuming each has already been trimmed)
            List<Token> tokens = tokenizeEquation(equation);

            // step 2: take the coefficients and constants from the tokens
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
        // step 3: store coefficients and constants in Matrix and Vector
        double[][] coefficientsPerVariable = new double[N][];
        for (Map.Entry<String, Integer> variableEntry : variableTable.entrySet()) {
            index = variableEntry.getValue().intValue();
            coefficientsPerVariable[index] = coeffTable.get(variableEntry.getKey()); 
        }
        coefficients = new Matrix(coefficientsPerVariable).transpose();
        constants = new Vector(constArray);
    }

    public Matrix extractCoefficients() {
        return coefficients;
    }

    public Vector extractConstants() {
        return constants;
    }

    // TODO: optimize
    public void validateEquation(String eq) {
        tokenizeEquation(eq);
    }

    // tokenizes 'equation' (assumed it is already trimmed) into stream of tokens
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
    // | <digit (>= 1) ...> '.' ?
    // | <digit (>= 0) ...> '.' <digit (>= 1) ...>
    //
    // <variable-name>
    // | <letter> <digit (>= 0) ...>

    private List<Token> tokenizeEquation(String eq) {
        List<Token> tokens = new ArrayList<>();
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
                        throw new LinearSystemEnvironmentException("the equation does not have an expression before '='");
                    sawEqual = true;
                } else {
                    throw new LinearSystemEnvironmentException("the equation requires exactly one '='");
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
                throw new LinearSystemEnvironmentException("every non-first term must be separated by '+' or '-'");
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
                    throw new LinearSystemEnvironmentException("variable name must only contain 1 leading letter");
                while (i < len && Character.isDigit(eq.charAt(i)))
                    ++i;
                String name = eq.substring(nameStartIdx, i);
                if (!variableTable.containsKey(name))
                    throw new LinearSystemEnvironmentException("variable name '" + name + "' does not exist");
                double coeff = sawDigit ? num * sign : 1.0 * sign;
                tokens.add(new VariableTermToken(coeff, name));
                sawFirstTerm = true;
            } else {
                // no variable, so it must be a constant term
                if (!sawDigit)
                    throw new LinearSystemEnvironmentException("invalid character for an equation");
                tokens.add(new ConstantTermToken(num * sign));
                sawFirstTerm = true;
            }
        }

        if (!sawEqual)
            throw new LinearSystemEnvironmentException("the equation does not have '='");
        else if (tokens.get(tokens.size() - 1) instanceof EqualToken)
            throw new LinearSystemEnvironmentException("the equation does not have an expression after '='");

        return tokens;
    }

    // helpers
    private static boolean isValidVariableName(String name) {
        if (!Character.isLetter(name.charAt(0)))
            return false;
        for (int i = 1; i < name.length(); ++i)
            if (!Character.isDigit(name.charAt(i)))
                return false;
        return true;
    }
}