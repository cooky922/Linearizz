package ccc102.linearizz.exceptions;

public class EquationException extends RuntimeException {
    private Kind kind;

    public EquationException(Kind kind) {
        super(createMessageFromKind(kind));
        this.kind = kind;
    }

    public static enum Kind {
        NoExprBeforeEqual,   // no expression before equal
        NoExprAfterEqual,    // no expression after equal
        NoEqual,             // no equal in equation
        MoreEqual,           // there is another equal
        UnseparatedTerms,    // terms are not separated by '+' or '-'
        InvalidCharacter,    // invalid character
        InvalidVariableName, // invalid variable name
        NoVariableFound,     // no variable found
        NoNumberAfterSlash,  // no number after '/'
        DivisionByZero,      // division by 0
        EmptyString          // empty string
    }

    private static String createMessageFromKind(Kind kind) {
        switch (kind) {
            case Kind.NoExprBeforeEqual:
                return "the equation doesn't have an expression before '='";
            case Kind.NoExprAfterEqual:
                return "the equation doesn't have an expression after '='";
            case Kind.NoEqual:
                return "the equation doesn't have '='";
            case Kind.MoreEqual:
                return "the equation has more than one '='";
            case Kind.UnseparatedTerms:
                return "the equation has unseparated terms, "
                     + "each non-first term must be separated by '+' or '-'";
            case Kind.InvalidCharacter:
                return "the equation contains an invalid character";
            case Kind.InvalidVariableName:
                return "the equation contains invalid variable name, "
                     + "the name must only have one leading letter";
            case Kind.NoVariableFound:
                return "the equation contains a variable that doesn't exist";
            case Kind.NoNumberAfterSlash:
                return "the equation contains '/' but not followed by any number";
            case Kind.DivisionByZero:
                return "the equation contains division by zero";
            case Kind.EmptyString:
                return "the equation is empty";
            default:
                return "unknown error";
        }
    }

    public Kind getKind() {
        return kind;
    }
}
