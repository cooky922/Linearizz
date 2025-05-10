package ccc102.linearizz.system;

import ccc102.linearizz.Vector;
import ccc102.linearizz.Matrix;

public class ExtractResult {
    private Matrix coeff;
    private Vector consts;

    public ExtractResult(Matrix coeff, Vector consts) {
        this.coeff = coeff;
        this.consts = consts;
    }

    public Matrix getCoefficients() {
        return coeff;
    }

    public Vector getConstants() {
        return consts;
    }
}
