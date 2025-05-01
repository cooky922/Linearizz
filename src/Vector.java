package CCC102.FinalProject;

import java.util.Arrays;

public class Vector implements Cloneable {
    private final double[] data;

    public Vector(int size) {
        data = new double[size];
    }

    public Vector(double[] values) {
        this.data = values;
    }

    public static Vector createFromValues(double... values) {
        return new Vector(values);
    }

    public double getElement(int i) {
        return data[i];
    }

    public void setElement(int i, double val) {
        data[i] = val;
    }

    public int getSize() {
        return data.length;
    }

    public static Vector add(Vector v1, Vector v2) {
        if (v1.getSize() != v2.getSize())
            throw new VectorException("vector sizes unmatched");
        Vector result = new Vector(v1.getSize());
        for (int i = 0; i < v1.getSize(); ++i)
            result.setElement(i, v1.getElement(i) + v2.getElement(i));
        return result;
    }

    public void add(Vector other) {
        if (getSize() != other.getSize())
            throw new VectorException("vector sized unmatched");
        for (int i = 0; i < getSize(); ++i)
            data[i] += other.getElement(i);
    }

    public void scale(double a) {
        for (int i = 0; i < getSize(); ++i)
            data[i] *= a;
    }

    public double getMagnitude() {
        return Math.sqrt(Vector.dotProduct(this, this));
    }

    public static double dotProduct(Vector v1, Vector v2) {
        if (v1.getSize() != v2.getSize())
            throw new VectorException("vector sizes unmatched");
        double sum = 0.0;
        for (int i = 0; i < v1.getSize(); ++i)
            sum += v1.getElement(i) * v2.getElement(i);
        return sum;
    }

    public static Vector crossProduct(Vector v1, Vector v2) {
        if (v1.getSize() != 3 || v2.getSize() != 3)
            throw new VectorException("vector sizes are neither 3");
        Vector result = new Vector(3);
        result.setElement(0, v1.getElement(1) * v2.getElement(2)
                           - v1.getElement(2) * v2.getElement(1));
        result.setElement(1, v1.getElement(2) * v2.getElement(0)
                           - v1.getElement(0) * v2.getElement(2));
        result.setElement(2, v1.getElement(0) * v2.getElement(1)
                           - v1.getElement(1) * v2.getElement(0));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Vector(");
        boolean hasComma = false;
        for (int i = 0; i < data.length; ++i) {
            if (hasComma)
                s.append(", ");
            s.append(data[i]);
            hasComma = true;
        }
        s.append(")");
        return s.toString();
    }

    @Override 
    public Vector clone() {
        double[] clonedData = Arrays.copyOf(data, data.length);
        return new Vector(clonedData);
    }
}
