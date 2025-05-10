package ccc102.linearizz.system;

import ccc102.linearizz.exceptions.LinearSystemException;
import ccc102.linearizz.Vector;
import ccc102.linearizz.Matrix;

public class Solver {
    // solve a system of linear equations in a form Ax = b
    // - where A is the matrix of coefficients of unknowns
    // - where x is the vector of unknowns
    // - where b is the vector of constant terms 
    // let C = [A | b]
    // - where C is the augmented matrix such that
    //   C's last column is vector b
    // ----------------------------------------------
    // process of solving the system:
    // 1. reduce matrix C to row-echelon form using Gaussian Elimination Method
    // 2. expect C to represent a triangular system of equations
    // 3. use back substitution to solve values of unknowns
    // ----------------------------------------------
    // returns vector x where each element is computed from the algorithm
    public static Vector solve(Matrix A, Vector b) throws LinearSystemException {
        // check for undetermined/overdetermined systems
        final int unknownCount  = A.getColumnSize(); 
        final int equationCount = A.getRowSize();
        if (equationCount < unknownCount)
            throw new LinearSystemException(LinearSystemException.Kind.Underdetermined);
        else if (equationCount > unknownCount)
            throw new LinearSystemException(LinearSystemException.Kind.Overdetermined);
        // safe if unknownCount == equationCount
        Matrix C = Matrix.createAugmented(A, b);
        C.toReducedRowEchelonForm();
        return solveTriangular(C);
    }

    private static Vector solveTriangular(Matrix C) throws LinearSystemException {
        Vector result = new Vector(C.getRowSize());
        final int lastColIndex = C.getColumnSize() - 1;
        for (int i = result.getSize() - 1; i >= 0; --i) {
            // check for inconsistent rows -> leading to inconsistent system (no solution)
            boolean allZero = true;
            for (int j = 0; j < lastColIndex; ++j) {
                if (C.getEntry(i, j) != 0) {
                    allZero = false;
                    break;
                }
            }
            if (allZero) {
                if (C.getEntry(i, lastColIndex) == 0)
                    throw new LinearSystemException(LinearSystemException.Kind.DependentInfinite);
                else 
                    throw new LinearSystemException(LinearSystemException.Kind.Inconsistent);
            }
            double sum = 0;
            for (int j = i; j < result.getSize() - 1; ++j) {
                sum += C.getEntry(i, j + 1) * result.getElement(j + 1);
            }
            double value = (C.getEntry(i, lastColIndex) - sum) / C.getEntry(i, i);
            result.setElement(i, value);
        }
        return result;

        // behind the scenes [example for 3x3]:
        // a00x0 + a01x1 + a02x2 + a03x3 = b0
        //         a11x1 + a12x2 + a13x3 = b1
        //                 a22x2 + a23x3 = b2
        //                         a33x3 = b3
        // ->
        // i = 3 | a33 * x3 = b3 - ( 0 )
        // i = 2 | a22 * x2 = b2 - (                      a23 * x3)
        // i = 1 | a11 * x1 = b1 - (           a12 * x2 + a13 * x3)
        // i = 0 | a00 * x0 = b0 - (a01 * x1 + a02 * x2 + a03 * x3)
    }
}
