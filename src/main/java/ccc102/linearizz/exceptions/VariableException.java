package ccc102.linearizz.exceptions;

public class VariableException extends RuntimeException {
    private Kind kind;

    public VariableException(Kind kind) {
        super(createMessageFromKind(kind));
        this.kind = kind;
    }

    public static enum Kind {
        InvalidName,    // the variable name is invalid
        AlreadyExisted, // the variable has already existed
        EmptyString     // the variable name is empty
    }

    private static String createMessageFromKind(Kind kind) {
        switch (kind) {
            case Kind.InvalidName:
                return "the variable name is invalid";
            case Kind.AlreadyExisted:
                return "the variable has already existed";
            case Kind.EmptyString:
                return "the variable name is empty";
            default:
                return "unknown error";
        }
    }

    public Kind getKind() {
        return kind;
    }
}
