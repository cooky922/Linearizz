package ccc102.linearizz.test;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import ccc102.linearizz.*;
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
    private LinearSystemEnvironment env;

    public SolveCommand(Scanner scan) {
        this.scan = scan;
        env = new LinearSystemEnvironment();
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
        if (!env.hasRegisteredVariableNames()) {
            System.out.print("Enter variable names > ");
            String[] names = scan.nextLine().split("\\s+");
            try {
                env.registerVariableNames(names);
            } catch (Exception e) {
                System.out.println("[Error: " + e.getMessage() + "]");
                env.unregisterVariableNames();
                return true;
            }
        }
        // enter equations [guaranteed #equation = #unknown]
        final int equationCount = env.getVariableNames().size();
        String[] equations = new String[equationCount];

        System.out.println("Enter " + equationCount + " equations below: ");
        for (int i = 0; i < equationCount; ++i) {
            System.out.print("> ");
            equations[i] = scan.nextLine().trim();
            try {
                env.validateEquation(equations[i]);
            } catch (Exception e) {
                System.out.println("[Error: " + e.getMessage() + "]");
                --i;
                continue;
            }
        }
        // not needed to be handled since every equation has already been validated
        env.registerEquations(equations);

        // solve equations
        Matrix A = env.extractCoefficients();
        Vector b = env.extractConstants();
        try {
            Vector x = LinearSystem.solve(A, b);
            System.out.println("Output:");
            System.out.println("[The system has one solution]");
            System.out.print("[");
            String[] names = env.getOrderedVariableNames();
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
        env.unregisterEquations();

        // try again?
        do {
            System.out.print("Do you want to try again (Y/S/N)? > ");
            char prompt = Character.toUpperCase(scan.nextLine().trim().charAt(0));
            switch (prompt) {
                case 'Y':
                    env.unregisterVariableNames();
                    return true;
                // yes, and uses the same variable names
                case 'S':
                    return true;

                // no
                case 'N':
                    env.unregisterVariableNames();
                    return false;
                
                // invalid prompt
                default:
                    System.out.println("[Error: invalid prompt '" + prompt + "']");
            }
        } while (true);
    }

}

/*

Welcome to Linearizz: Terminal Mode 
------------------------------------------
This linearizz will help you solve system of linear equations

.quit  -> exit the terminal
.help  -> validity of the input
.about -> provide more information about the program
.solve -> solve the system of linear equations
.vars  -> build variable set [TODO]

-------- under .solve 
enter number of variables variable names >
[scanning variable names ...] (will not print)
[error: __] (will print, can try again)
enter N equations below:
> [...]
[scanning equation ...] (will not print)
[error: __] (will print, can try again)
[calculating the results ...]
output:
[the system has (no solutions|one solution|infinitely many solutions)]
[x = _, y = _, z = _] (will print if it has one solution)
do you want to try again (Y/S/N)?
(Y - yes)
(S - yes but with the same variable names)
(N - no, exiting .solve mode)
...
*/
