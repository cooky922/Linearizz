package ccc102.linearizz.test;

import ccc102.linearizz.*;
import ccc102.linearizz.exceptions.LinearSystemException;
import java.util.Scanner;

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
        test4();
    }

    private static void test2() {
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

    private static void test3() {
        Matrix m = new Matrix(new double[][] {
            {1, -2, 0, 4},
            {4, 0, 3, 1},
            {2, 1, 0, -3},
            {5, 1, 1, 2}
        });
        System.out.println(m.getDeterminant());
    }

    private static void test4() {
        Scanner scan = new Scanner(System.in);

        // enter variable names:
        System.out.print("Enter variable names: ");
        String[] names = scan.nextLine().split("\\s+");
        LinearSystemEnvironment env = new LinearSystemEnvironment();
        env.registerVariableNames(names);

        // enter equations:
        while (true) {
            System.out.print("Enter equation: ");
            String equation = scan.nextLine();
            if (equation.equals(".exit"))
                break;
            env.parseEquation(equation);
        }

    }
}