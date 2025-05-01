package CCC102.FinalProject;

public class Test {
    public static void main(String[] args) {
        // example:
        //  2x + y -  z = 8
        // -3x - y + 2z = -11
        // -2x + y + 2z = -3
        // will be turned into:
        // [ 2  1 -1 |   8]
        // [-3 -1  2 | -11]
        // [-2  1  2 |  -3]
        // goal:
        // ...
        Matrix A = new Matrix(new double[][] {
            {1, 2},
            {2, 4}
        });
        Vector b = new Vector(new double[] {
            5, 10
        });
        Matrix C = Matrix.createAugmented(A, b);
        try {
            Vector x = LinearSystem.solve(A, b);
            System.out.println("Input Matrix: " + C);
            System.out.println("Solved Unknowns: " + x);
        } catch (LinearSystemException e) {
            System.out.println("Input Matrix: " + C);
            System.out.println("Message: " + e.getMessage());
        }
    }
}