package CCC102.FinalProject;

public class LinearSystemException extends RuntimeException {
    private Kind kind;

    public LinearSystemException(Kind kind) {
        super(createMessageFromKind(kind));
        this.kind = kind;
    }

    public static enum Kind {
        Inconsistent,      // the system has no solutions
        DependentInfinite, // the system has infinitely many solutions
        Underdetermined,   // #eq < #unknown, the system may have infinitely many solutions
        Overdetermined     // #eq > #unkowns, the system may have no solutions
    }

    private static String createMessageFromKind(Kind kind) {
        switch (kind) {
            case Kind.Inconsistent:
                return "the system has no solutions";
            case Kind.DependentInfinite:
                return "the system has infinitely many solutions";
            case Kind.Underdetermined:
                return "the system may have infinitely many solutions (underdetermined)";
            case Kind.Overdetermined:
                return "the system may have no solutions (overdetermined)";
            default:
                return "unknown error";
        }
    }

    public Kind getKind() {
        return kind;
    }
}