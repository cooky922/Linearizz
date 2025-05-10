package ccc102.linearizz.test;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import ccc102.linearizz.*;
import ccc102.linearizz.system.*;
import ccc102.linearizz.exceptions.LinearSystemException;

public class TerminalMode {

    public static void main(String[] args) {
        // initialization
        Scanner scan = new Scanner(System.in);
        Map<String, Command> mapCommands = new HashMap<>();
        boolean willContinue = true;
        mapCommands.put(".quit", new QuitCommand());
        mapCommands.put(".help", new HelpCommand());
        mapCommands.put(".about", new AboutCommand());
        mapCommands.put(".solve", new SolveCommand(scan));

        // welcome
        System.out.println("Welcome to Linearizz: Terminal Mode");
        System.out.println("------------------------------------------");
        System.out.println("This linearizz will help you solve system of linear equations");

        do {
            System.out.print("> ");
            String command = scan.nextLine().trim();
            if (!mapCommands.containsKey(command)) {
                System.out.println("[Error: the command you have entered does not exist]");
                continue;
            }
            willContinue = mapCommands.get(command).execute();
        } while (willContinue);
    }
}

abstract class Command {
    public abstract boolean execute();
}

class QuitCommand extends Command {
    public boolean execute() {
        System.out.println("[Thank you ...]");
        return false;
    }
}

class HelpCommand extends Command {
    public boolean execute() {
        System.out.println("[TODO ...]");
        return true;
    }
}

class AboutCommand extends Command {
    public boolean execute() {
        System.out.println("[TODO ...]");
        return true;
    }
}

class SolveCommand extends Command {
    private Scanner scan;
    private Variables variables;
    private Equations equations;

    public SolveCommand(Scanner scan) {
        this.scan = scan;
        variables = new Variables();
        equations = new Equations(variables);
    }

    public boolean execute() {
        boolean willRepeat = true;
        do {
            willRepeat = executeOne();
        } while (willRepeat);
        return true;
    }

    private boolean executeOne() {
        // enter variable names
        if (variables.isEmpty()) {
            System.out.print("Enter variable names > ");
            String[] names = scan.nextLine().split("\\s+");
            try {
                variables.addAll(names);
            } catch (Exception e) {
                System.out.println("[Error: " + e.getMessage() + "]");
                variables.clear();
                return true;
            }
        }
        // enter equations [guaranteed #equation = #unknown]
        final int equationCount = variables.count();
        String[] equationStrs = new String[equationCount];

        System.out.println("Enter " + equationCount + " equations below: ");
        for (int i = 0; i < equationCount; ++i) {
            System.out.print("> ");
            equationStrs[i] = scan.nextLine().trim();
            try {
                equations.add(equationStrs[i]);
            } catch (Exception e) {
                System.out.println("[Error: " + e.getMessage() + "]");
                --i;
                continue;
            }
        }
        ExtractResult extracts = equations.extractValues();
        // solve equations
        Matrix A = extracts.getCoefficients();
        Vector b = extracts.getConstants();
        try {
            Vector x = Solver.solve(A, b);
            System.out.println("Output:");
            System.out.println("[The system has one solution]");
            System.out.print("[");
            String[] names = variables.getNames();
            boolean printComma = false;
            for (int i = 0; i < equationCount; ++i) {
                if (printComma)
                    System.out.print(", ");
                System.out.print(names[i] + " = " + x.getElement(i));
                printComma = true;
            }
            System.out.println("]");
        } catch (LinearSystemException e) {
            switch (e.getKind()) {
                case LinearSystemException.Kind.Inconsistent:
                    System.out.println("[The system has no solutions]");
                    break;

                case LinearSystemException.Kind.DependentInfinite:
                    System.out.println("[The system has infinitely many solutions]");
                    break;
                
                default:;
            }
        }
        equations.clear();
        // try again?
        do {
            System.out.print("Do you want to try again (Y/S/N)? > ");
            char prompt = Character.toUpperCase(scan.nextLine().trim().charAt(0));
            switch (prompt) {
                case 'Y':
                    variables.clear();
                    return true;
                // yes, and uses the same variable names
                case 'S':
                    return true;

                // no
                case 'N':
                    variables.clear();
                    return false;
                
                // invalid prompt
                default:
                    System.out.println("[Error: invalid prompt '" + prompt + "']");
            }
        } while (true);
    }
}
