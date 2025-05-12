package ccc102.linearizz.gui;

// TODO: transfer all the content of GUIMode.java to this file

public class ZMainFrame {
    // set the application entrypoint class
    public static final Class<?> entryPointClass = getEntryPointClass();

    /// DO NOT TOUCH THIS
    // implementation details
    private static Class<?> getEntryPointClass() {
        try {
            return Class.forName("ccc102.linearizz.test.GUIMode");
        } catch (Exception e) {
            return null;
        }
    }

    // TODO:
    /*
    - frame.buildLayout()
        - frame.buildSolveButton()
            - frame.SolveEquations [implement ActionEvent]
        - frame.buildVariablePanel()
            - frame.RegisterModeChange [implement ActionEvent]
            - frame.AddVariable [implement ActionEvent]
            - frame.ClearVariable [implement ActionEvent]
        - frame.buildEquationPanel()
            - frame.addEquationField()
            - frame.AddEquation [implement ActionEvent]
            - frame.ClearEquations [implement ActionEvent]
            - frame.RemoveEquation [implement ActionEvent]
            - frame.HandleEquationFieldChange [implement DocumentListener]
            - frame.handleEquationField()
        - frame.buildOutputPanel()
    */
}