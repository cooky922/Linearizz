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

    // only parse, not add yet
    public void validateEquation(String equation) throws EquationException {
        parseEquation(equation.trim(), false);
    }

    // add an equation
    public void add(String equation) throws EquationException {
        equationTokens.add(parseEquation(equation.trim(), true));
    }

    // add equations
    public void addAll(String[] c) throws EquationException {
        for (String equation : c)
            add(equation);
    }

    public void addAll(Iterable<? extends String> c) throws EquationException {
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
    // | ('+' | '-') ? <number-atom> <variable-name>
    // | ('+' | '-') ? <variable-name> '/' <number-atom>
    // | ('+' | '-') ? <number-atom> <variable-name> '/' <number-atom>
    // 
    // <number>
    // | <number-atom> ('/' <number-atom>) ?

    // <number-atom>
    // | <digit (>= 1) ...> '.' ?
    // | <digit (>= 0) ...> '.' <digit (>= 1) ...>

    //
    // <variable-name>
    // | <letter> <digit (>= 0) ...>
    private List<Token> parseEquation(String eq, boolean savesVariables) throws EquationException {
        if (eq.isEmpty())
            throw new EquationException(EquationException.Kind.EmptyString);
        return new EquationParser(eq, savesVariables).collectTokens();
    }

    // non-static class serve as state machine
    // for parsing an equation
    private class EquationParser {
        private final String eq;
        private final boolean savesVariables;
        private final Variables newVariables = new Variables();

        // state variables
        private List<Token> tokens = new ArrayList<>();
        private int i = 0;
        private int len;
        private boolean sawEqual = false;
        private boolean sawFirstTerm = false;
        private boolean sawDigit = false;
        private boolean sawDot   = false;
        private boolean sawSign  = false;

        public EquationParser(String eq, boolean savesVariables) {
            this.eq = eq;
            this.savesVariables = savesVariables;
            this.len = eq.length();
        }

        // can only be called once
        public List<Token> collectTokens() {
            for (;;) {
                skipWhitespace();
                if (!hasNextChar())
                    break;
                nextToken();
            }
            finalizeParse();
            return tokens;
        }

        /// IMPLEMENTATION DETAILS
        private boolean hasNextChar() {
            return i < len;
        }

        private void skipWhitespace() {
            while (i < len && Character.isWhitespace(eq.charAt(i)))
                ++i;
        }

        private char currentChar() {
            return eq.charAt(i);
        }

        private void nextChar() {
            ++i;
        }

        private void nextToken() {
            char ch = currentChar();
            if (ch == '=') {
                parseEqual(); // creates '=' token
                return; // done for this token
            }
            int sign = parseSign();
            if (!hasNextChar()) {
                if (sawSign)
                    throw new EquationException(EquationException.Kind.NoTermAfterSign);
                return;
            }
            double num = 0;
            // if it encounters a number atom
            ch = currentChar();
            if (Character.isDigit(ch) || ch == '.')
                num = parseNumberAtom();
            skipWhitespace();
            // if it contains any character left
            if (hasNextChar()) {
                // <number> ? <variable> , ...
                // - constructs variable term token
                if (Character.isLetter(currentChar())) {
                    String name = parseVariableName();
                    if (autoAdd) {
                        if (!newVariables.containsName(name) || !variables.containsName(name))
                            newVariables.addIfNew(name);
                    } else {
                        if (!variables.containsName(name))
                            throw new EquationException(EquationException.Kind.NoVariableFound);
                    }
                    // 'num' will serve as coefficient
                    double coeff = sawDigit ? num * sign : 1.0 * sign;
                    double denom = 1.0;
                    skipWhitespace();
                    if (hasNextChar() && currentChar() == '/') {
                        // expects a number after '/' or else error
                        nextChar();
                        skipWhitespace();
                        if (!hasNextChar() || (!Character.isDigit(currentChar()) && currentChar() != '.'))
                            throw new EquationException(EquationException.Kind.NoNumberAfterSlash);
                        sawDigit = false;
                        sawDot = false;
                        denom = parseNumberAtom();
                        if (denom == 0)
                            throw new EquationException(EquationException.Kind.DivisionByZero);
                        coeff /= denom;
                    }
                    tokens.add(new VariableTermToken(coeff, name));
                }
                // <number>, ...
                // - constructs constant term token
                else if (sawDigit) {
                    double denom = 1.0;
                    // saw '/' operator
                    if (currentChar() == '/') {
                        // expects a number after '/' or else error
                        nextChar();
                        skipWhitespace();
                        if (!hasNextChar() || (!Character.isDigit(currentChar()) && currentChar() != '.'))
                            throw new EquationException(EquationException.Kind.NoNumberAfterSlash);
                        sawDigit = false;
                        sawDot = false;
                        denom = parseNumberAtom();
                        if (denom == 0)
                            throw new EquationException(EquationException.Kind.DivisionByZero);
                        num /= denom;
                    }
                    tokens.add(new ConstantTermToken(num * sign));
                }
                // error: invalid character
                else {
                    throw new EquationException(EquationException.Kind.InvalidCharacter);
                }
            } else {
                // no characters left and didn't see any digit
                if (!sawDigit)
                    throw new EquationException(EquationException.Kind.InvalidCharacter);
                tokens.add(new ConstantTermToken(num * sign));
            }
            sawFirstTerm = true;
            sawDigit = false;
            sawDot = false;
            sawSign = false;
        }

        private void parseEqual() {
            if (!sawEqual) {
                if (tokens.isEmpty())
                    throw new EquationException(EquationException.Kind.NoExprBeforeEqual);
                sawEqual = true;
            } else {
                throw new EquationException(EquationException.Kind.MoreEqual);
            }
            tokens.add(new EqualToken());
            // resets sawFirstTerm
            sawFirstTerm = false;
            nextChar(); // consumes '='
        }

        private int parseSign() {
            // encounters '+' or '-'
            // - optional sign in every first term
            // - requires sign for other terms
            char ch = currentChar();
            int sign = 1;
            if (ch == '+' || ch == '-') {
                sign = (ch == '-') ? - 1 : 1;
                nextChar(); // consumes '+' or '-'
                sawSign = true;
                skipWhitespace();
            } else if (sawFirstTerm) {
                throw new EquationException(EquationException.Kind.UnseparatedTerms);
            }
            return sign;
        }

        private double parseNumberAtom() {
            double num = 0;
            int numStartIdx = i;
            while (hasNextChar()) {
                char ch = currentChar();
                if (Character.isDigit(ch)) {
                    sawDigit = true;
                    nextChar();
                } else if (ch == '.' && !sawDot) {
                    sawDot = true;
                    nextChar();
                } else {
                    break;
                }
            }
            if (sawDigit) {
                String numText = eq.substring(numStartIdx, i);
                num = Double.parseDouble(numText);
            }
            return num;
        }

        private String parseVariableName() {
            int nameStartIdx = i;
            nextChar();
            // it encounters a second letter [error]
            if (hasNextChar() && Character.isLetter(currentChar()))
                throw new EquationException(EquationException.Kind.InvalidVariableName);
            while (hasNextChar() && Character.isDigit(currentChar()))
                nextChar();
            return eq.substring(nameStartIdx, i);
        }

        private void finalizeParse() {
            if (!sawEqual)
                throw new EquationException(EquationException.Kind.NoEqual);
            else if (tokens.get(tokens.size() - 1) instanceof EqualToken)
                throw new EquationException(EquationException.Kind.NoExprAfterEqual);
            if (autoAdd && savesVariables)
                variables.addAllIfNew(newVariables);
        }
    }
}
