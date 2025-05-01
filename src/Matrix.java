package CCC102.FinalProject;

public class Matrix implements Cloneable {
    private double[][] data;

    // constructors
    public Matrix(int rowSize, int colSize) {
        this.data = new double[rowSize][colSize];
    }

    public Matrix(double[][] data) {
        this.data = data;
    }

    public static Matrix createFromRows(double[]... args) {
        return new Matrix(args);
    }

    public static Matrix createFromColumns(double[]... args) {
        final int colSize = args[0].length;
        final int rowSize = args.length;
        double[][] data = new double[rowSize][colSize];
        for (int i = 0; i < rowSize; ++i) {
            for (int j = 0; j < colSize; ++j) {
                data[i][j] = args[j][i];
            }
        }
        return new Matrix(data);
    }

    public static Matrix createAugmented(Matrix m1, Matrix m2) {
        if (m1.getRowSize() != m2.getRowSize())
            throw new MatrixException("unsupported");
        Matrix result = new Matrix(m1.getRowSize(), m1.getColumnSize() + m2.getColumnSize());
        for (int i = 0; i < result.getRowSize(); ++i) {
            for (int j = 0; j < result.getColumnSize(); ++j) {
                if (j < m1.getColumnSize()) {
                    result.data[i][j] = m1.data[i][j];
                } else {
                    result.data[i][j] = m2.data[i][j - m1.getColumnSize()]; 
                }
            }
        }
        return result;
    }

    public static Matrix createAugmented(Matrix m, Vector v) {
        if (m.getRowSize() != v.getSize())
            throw new MatrixException("unsupported");
        Matrix result = new Matrix(m.getRowSize(), m.getColumnSize() + 1);
        for (int i = 0; i < result.getRowSize(); ++i) {
            for (int j = 0; j < result.getColumnSize(); ++j) {
                if (j < m.getColumnSize()) {
                    result.data[i][j] = m.data[i][j];
                } else {
                    result.data[i][j] = v.getElement(i); 
                }
            }
        }
        return result;
    }

    public static Matrix createIdentityMatrix(int n) {
        Matrix result = new Matrix(n, n);
        for (int i = 0; i < n; ++i) {
            result.data[i][i] = 1;
        }
        return result;
    }

    public static Matrix createDiagonalMatrix(double[] entries) {
        final int len = entries.length;
        Matrix result = new Matrix(len, len);
        for (int i = 0; i < len; ++i) {
            result.data[i][i] = entries[i];
        }
        return result;
    }

    // queries
    public int getRowSize() {
        return data.length;
    }

    public int getColumnSize() {
        return data[0].length;
    }

    public double getEntry(int i, int j) {
        return data[i][j];
    }

    public void setEntry(int i, int j, double val) {
        data[i][j] = val;
    }

    public double[] getRow(int i) {
        return (double[]) data[i].clone();
    }

    public Vector getRowAsVector(int i) {
        return new Vector(getRow(i));
    }

    public double[] getColumn(int j) {
        double[] result = new double[getColumnSize()];
        for (int i = 0; i < getColumnSize(); ++i)
            result[i] = data[i][j];
        return result;
    }

    public Vector getColumnAsVector(int j) {
        return new Vector(getColumn(j));
    }

    public boolean isSquareMatrix() {
        return getRowSize() == getColumnSize();
    }

    public boolean isRowMatrix() {
        return getRowSize() == 1;
    }

    public boolean isColumnMatrix() {
        return getColumnSize() == 1;
    }

    public boolean isUpperTriangularMatrix() {
        if (!isSquareMatrix())
            return false;
        for (int i = 0; i < getRowSize(); ++i) {
            for (int j = 0; j < getColumnSize(); ++j) {
                if (i > j && data[i][j] != 0)
                    return false;
            }
        }
        return true;
    }

    public boolean isLowerTriangularMatrix() {
        if (!isSquareMatrix())
            return false;
        for (int i = 0; i < getRowSize(); ++i) {
            for (int j = 0; j < getColumnSize(); ++j) {
                if (i < j && data[i][j] != 0)
                    return false;
            }
        }
        return true;
    }

    public boolean isTriangularMatrix() {
        return isLowerTriangularMatrix() || isUpperTriangularMatrix();
    }

    public boolean isDiagonalMatrix() {
        return isLowerTriangularMatrix() && isUpperTriangularMatrix();
    }

    @Override 
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Matrix(\n");
        boolean hasCommaOuter = false;
        boolean hasCommaInner = false;
        for (int i = 0; i < getRowSize(); ++i) {
            if (hasCommaOuter)
                s.append(",\n");
            s.append(" [");
            for (int j = 0; j < getColumnSize(); ++j) {
                if (hasCommaInner)
                    s.append(", ");
                s.append(data[i][j]);
                hasCommaInner = true;
            }
            hasCommaInner = false;
            s.append("]");
            hasCommaOuter = true;
        }
        s.append(")");
        return s.toString();
    }

    @Override 
    public Matrix clone() {
        double[][] clonedData = new double[getRowSize()][getColumnSize()];
        for (int i = 0; i < getRowSize(); ++i) {
            for (int j = 0; j < getColumnSize(); ++j) {
                clonedData[i][j] = data[i][j];
            }
        }
        return new Matrix(clonedData);
    }

    // matrix operations
    public static Matrix transpose(Matrix m) {
        Matrix result = new Matrix(m.getColumnSize(), m.getRowSize());
        for (int i = 0; i < result.getRowSize(); ++i) {
            for (int j = 0; j < result.getColumnSize(); ++j) {
                result.data[i][j] = m.data[j][i];
            }
        }
        return result;
    }

    public void transpose() {
        if (isSquareMatrix()) {
            // doesn't need to reallocate when the matrix is square
            for (int i = 0; i < getRowSize(); ++i) {
                for (int j = 0; j < getColumnSize(); ++j) {
                    if (i > j) {
                        // swap
                        double temp = data[i][j];
                        data[i][j]  = data[j][i];
                        data[j][i]  = temp;
                    }
                }
            }
        } else {
            // needs to reallocate to adjust the nested array
            final int oldRowSize = getRowSize();
            final int oldColSize = getColumnSize();
            final double[][] oldMatData = data;
            data = new double[oldColSize][oldRowSize];
            for (int i = 0; i < oldColSize; ++i) {
                for (int j = 0; j < oldRowSize; ++j)
                    data[i][j] = oldMatData[j][i];
            }
        }
    }

    public static Matrix add(Matrix m1, Matrix m2) {
        if (m1.getRowSize() != m2.getRowSize() ||
            m1.getColumnSize() != m2.getColumnSize())
            throw new MatrixException("matrix sized unmatched");
        Matrix result = new Matrix(m1.getRowSize(), m1.getColumnSize());
        for (int i = 0; i < m1.getRowSize(); ++i) {
            for (int j = 0; j < m1.getColumnSize(); ++j) {
                result.data[i][j] = m1.data[i][j] + m2.data[i][j];
            }
        }
        return result;
    }

    public void add(Matrix other) {
        if (getRowSize() != other.getRowSize() ||
            getColumnSize() != other.getColumnSize())
            throw new MatrixException("matrix sizes unmatched");
        for (int i = 0; i < getRowSize(); ++i) {
            for (int j = 0; j < getColumnSize(); ++j) {
                data[i][j] += other.data[i][j];
            }
        }
    }

    public static Matrix scale(Matrix m, double a) {
        Matrix result = new Matrix(m.getRowSize(), m.getColumnSize());
        for (int i = 0; i < m.getRowSize(); ++i) {
            for (int j = 0; j < m.getColumnSize(); ++j) {
                result.data[i][j] = m.data[i][j] * a;
            }
        }
        return result;
    }

    public void scale(double a) {
        for (int i = 0; i < getRowSize(); ++i) {
            for (int j = 0; j < getColumnSize(); ++j) {
                data[i][j] *= a;
            }
        }
    }

    public static Matrix multiply(Matrix m1, Matrix m2) {
        if  (m1.getColumnSize() != m2.getRowSize())
            throw new MatrixException("matrix sizes unsupported for multiplication");
        Matrix result = new Matrix(m1.getRowSize(), m2.getColumnSize());
        for (int i = 0; i < m1.getRowSize(); ++i) {
            for (int j = 0; j < m2.getColumnSize(); ++j) {
                for (int k = 0; k < m1.getColumnSize(); ++k) {
                    result.data[i][j] += m1.data[i][k] * m2.data[k][j];
                }
            }
        }
        return result;
    }

    public static Vector multiply(Matrix m, Vector v) {
        // treat vector as column matrix
        // ex:
        // [1 2] [5]
        // [3 4] [6]
        // [m x n] * [n x 1] = [m x 1]
        if (m.getColumnSize() != v.getSize())
            throw new MatrixException("matrix and vector sizes unsupported for multiplication");
        Vector result = new Vector(m.getRowSize());
        for (int i = 0; i < m.getRowSize(); ++i) {
            for (int j = 0; j < m.getColumnSize(); ++j) {
                result.setElement(i, result.getElement(i) + m.data[i][j] * v.getElement(j));
            }
        }
        return result;
    }

    // row operations
    public void rowOperationSwap(int r1, int r2) {
        // row[r1] <-> row[r2]
        if (r1 == r2)
            throw new MatrixException("row index: " + r1 + " and " + r2);
        for (int j = 0; j < getColumnSize(); ++j) {
            double temp = data[r1][j];
            data[r1][j] = data[r2][j];
            data[r2][j] = temp;
        }
    }

    public void rowOperationMultiply(int r, double scale) {
        // scale * row[r] -> row[r]
        if (scale == 0)
            throw new MatrixException("...");
        for (int j = 0; j < getColumnSize(); ++j) {
            data[r][j] = scale * data[r][j];
        }
    }

    public void rowOperationAdd(int r1, int r2, double scale) {
        // row[r1] + scale * row[r2] -> row[r1]
        if (r1 == r2)
            throw new MatrixException("...");
        for (int j = 0; j < getColumnSize(); ++j) {
            data[r1][j] = data[r1][j] + scale * data[r2][j];
        }
    }

    public void toRowEchelonForm() {
        int pivotRow = 0;
        int pivotCol = 0;
        for (; pivotRow < getRowSize() && pivotCol < getColumnSize(); ++pivotRow) {
            // locate pivot
            int i = pivotRow;
            while (i < getRowSize() && getEntry(i, pivotCol) == 0) {
                ++i;
            }
            if (i == getRowSize()) {
                ++pivotCol;
                --pivotRow;
                continue;
            }
            // swap current row with pivot row
            if (i != pivotRow) {
                rowOperationSwap(pivotRow, i);
            }
            double pivot = getEntry(pivotRow, pivotCol);
            if (pivot != 1) {
                rowOperationMultiply(pivotRow, 1 / pivot);
            }
            // eliminate entries below pivot (making its leading entries zero)
            for (int j = pivotRow + 1; j < getRowSize(); ++j) {
                double factor = getEntry(j, pivotCol);
                if (j != pivotRow && factor != 0) {
                    rowOperationAdd(j, pivotRow, -factor);
                }
            }
            // move to next column
            ++pivotCol;
        }
    }
}
