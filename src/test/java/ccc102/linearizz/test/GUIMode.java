package ccc102.linearizz.test;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import ccc102.linearizz.*;
import ccc102.linearizz.exceptions.LinearSystemException;
import ccc102.linearizz.system.*;
import ccc102.linearizz.gui.*;

// TODO: add shortcuts, optimizes the GUI

public class GUIMode extends JFrame {

    private static final int timerDelay = 1000;
    private static LineBorder baseBorder = new LineBorder(ZScheme.colorGray, 3, true);

    /// Within Variable Panel
    // selects either manual or automatic variable register
    private ZComboBox<String> registerModeBox;
    private ZTextField variableInputField;
    private ZButton addVariableButton;
    private JPanel variableListPanel;
    private JScrollPane variableScrollPane;
    private ZButton clearVariableButton;

    /// Within Equation Panel
    private JPanel equationListPanel;
    private JScrollPane equationScrollPane;
    private ZButton addEquationButton;
    private ZButton clearEquationButton;

    /// Solve Button
    private ZButton solveButton;

    /// Within Output Panel
    private JTextArea outputArea;

    /// Logics ....
    private boolean autoRegister = false;
    private Variables variables = new Variables();
    private Equations equations = new Equations(variables);
    private Map<ZTextField, FieldStatus> equationFields = new HashMap<>();

    public GUIMode() {
        super("Linearizz App");
        setSize(350, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ZAppIcon.getImage());
        buildLayout();
        setVisible(true);
    }

    public void buildLayout() {
        // frame's content pane
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 1;

        solveButton = buildSolveButton();

        // variable panel
        c.gridy = 0;
        c.ipady = 50;
        c.anchor = GridBagConstraints.PAGE_START;
        contentPane.add(buildVariablePanel(), c);

        // equation panel
        c.gridy = 1;
        c.ipady = 150;
        contentPane.add(buildEquationPanel(), c);

        // solve button
        c.gridy = 2;
        c.ipady = 25;
        c.insets = new Insets(5, 5, 5, 5);
        contentPane.add(solveButton, c);

        // output panel
        c.gridy = 3;
        c.ipady = 100;
        c.insets = new Insets(0, 0, 0, 0);
        c.anchor = GridBagConstraints.PAGE_END;
        contentPane.add(buildOutputPanel(), c);
    }

    // build once
    private ZButton buildSolveButton() {
        ZButton button = new ZButton("Solve", ZScheme.colorGreen, true);
        // disabled by default, only enable if all equations are solvable
        button.setDisabled();
        button.setFont(ZScheme.titleFont.deriveFont(14.0f));
        button.addActionListener(e -> {
            for (ZTextField equationField : equationFields.keySet())
                equations.add(equationField.getText().trim());
            try {
                ExtractResult extract = equations.extractValues();
                Matrix A = extract.getCoefficients();
                Vector b = extract.getConstants();
                Vector x = Solver.solve(A, b);
                // print
                StringBuilder builder = new StringBuilder();
                String[] names = equations.getVariables().getNames();
                for (int i = 0; i < names.length; ++i)
                    builder.append(names[i] + " = " + x.getElement(i) + "\n");
                outputArea.setText(builder.toString());
            } catch (LinearSystemException ex) {
                String outputString = "";
                switch (ex.getKind()) {
                    case LinearSystemException.Kind.Inconsistent:
                        outputString = "[this system has no solutions]";
                        break;
                    case LinearSystemException.Kind.DependentInfinite:
                        outputString = "[this system has infinitely many solutions]";
                        break;
                    case LinearSystemException.Kind.Underdetermined:
                        outputString = "[this system is underdetermined]\n"
                                     + "[it may have infinitely many solutions]";
                        break;
                    case LinearSystemException.Kind.Overdetermined:
                        outputString = "[this system is overdetermined]\n"
                                     + "[it may have no solutions]";
                        break;
                    default:
                }
                outputArea.setText(outputString);
            }
            equations.clear();
        });
        return button;
    }

    // build once
    private JPanel buildVariablePanel() {
        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder(baseBorder, "Variables");
        border.setTitleFont(ZScheme.titleFont);
        border.setTitleColor(ZScheme.colorGray);
        panel.setBorder(border);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // VARIABLE PANEL
        // | 
        // | TOP ROW
        // | | registerModeBox [ZComboBox]
        // | 
        registerModeBox = new ZComboBox<>(
            new String[] {"Manual Register", "Automatic Register"},
            ZScheme.colorBlue    
        );
        registerModeBox.addActionListener(e -> {
            final int index = registerModeBox.getSelectedIndex();
            switch (index) {
                // switch to manual register
                case 0:
                    if (autoRegister) {
                        variableInputField.setEditable(true);
                        addVariableButton.setEnabled();
                        clearVariableButton.setEnabled();
                        // transfer variables to this equations object
                        Equations fromEquations = new Equations();
                        for (Map.Entry<ZTextField, FieldStatus> entry : equationFields.entrySet()) {
                            if (entry.getValue().isValid)
                                fromEquations.add(entry.getKey().getText().trim());
                        }
                        variables = fromEquations.getVariables();
                        equations = new Equations(variables);
                        // show added variables on variable panel list
                        for (String name : variables.getNames()) {
                            ZLabel varLabel = new ZLabel(name);
                            variableListPanel.add(varLabel);
                        }
                        variableListPanel.revalidate();
                        autoRegister = false;
                    }
                    break;
                // switch to automatic register
                case 1:
                    if (!autoRegister) {
                        variableInputField.setEditable(false);
                        variableInputField.setText("");
                        addVariableButton.setDisabled();
                        clearVariableButton.doClick(); // calls variables.clear()
                        clearVariableButton.setDisabled();
                        equations = new Equations();
                        autoRegister = true;
                    }
                    break;
                default:;
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.PAGE_START;
        panel.add(registerModeBox, gbc);

        // | ENTER NAME
        // | | enterNameHeading [ZLabel]
        ZLabel enterNameHeading = new ZLabel("Enter Name");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(enterNameHeading, gbc);

        // | MIDDLE ROW
        // | | variableInputField [JTextField] [3/4 width]
        // | | addVariableButton [JButton] [1/4 width]
        variableInputField = new ZTextField();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(variableInputField, gbc);

        addVariableButton = new ZButton("Add", ZScheme.colorGreen);
        addVariableButton.addActionListener(e -> {
            String varName = variableInputField.getText().trim();
            try {
                variables.add(varName);
                ZLabel varLabel = new ZLabel(varName);
                variableListPanel.add(varLabel);
                // scroll to recently added
                JScrollBar scrollBar = variableScrollPane.getHorizontalScrollBar();
                scrollBar.setValue(scrollBar.getMaximum());
                variableListPanel.revalidate();
                variableInputField.setText("");
                variableInputField.setNormalMode();
                variableInputField.setToolTipText(null);
                variableScrollPane.revalidate();
            } catch (Exception ex) {
                variableInputField.setErrorMode();
                variableInputField.setToolTipText(ex.getMessage());
            }
            // try to update the equation fields status
            boolean hasOneEmptyField = 
                equationFields.size() == 1 &&
                equationFields.keySet().iterator().next().getText().isEmpty();

            if (!hasOneEmptyField)
                for (ZTextField equationField : equationFields.keySet())
                    handleEquationField(equationField, false);
        });
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(addVariableButton, gbc);

        // | VARIABLE LIST
        // | | listHeading [ZLabel]
        ZLabel listHeading = new ZLabel("List");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(listHeading, gbc);

        // | BOTTOM ROW
        // | | variableListPanel [JPanel] [3/4 width]
        // | | clearVariableButton [JButton] [1/4 width]
        variableListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        variableListPanel.setBackground(ZScheme.colorLightGray);
        variableScrollPane = new JScrollPane(variableListPanel,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        // make it invisible to make it "less" annoying
        variableScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        variableScrollPane.setBackground(ZScheme.colorLightGray);
        variableScrollPane.setBorder(new EmptyBorder(4, 4, 4, 4));

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(variableScrollPane, gbc);

        clearVariableButton = new ZButton("Clear", ZScheme.colorPurple);
        clearVariableButton.addActionListener(e -> {
            variables.clear();
            variableInputField.setText("");
            variableInputField.setToolTipText(null);
            variableInputField.setNormalMode();
            variableListPanel.removeAll();
            variableListPanel.revalidate();
            variableListPanel.repaint();
            // try to update the equation fields status
            boolean hasOneEmptyField = 
                equationFields.size() == 1 &&
                equationFields.keySet().iterator().next().getText().isEmpty();

            if (!autoRegister && !hasOneEmptyField)
                for (ZTextField equationField : equationFields.keySet())
                    handleEquationField(equationField, false);
        });
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(clearVariableButton, gbc);
        return panel;
    }

    // build once
    private JPanel buildEquationPanel() {
        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder(baseBorder, "Equations");
        border.setTitleFont(ZScheme.titleFont);
        border.setTitleColor(ZScheme.colorGray);
        panel.setBorder(border);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;

        // | FIELDS ROWS
        // | equationListPanel [JPanel]
        equationListPanel = new JPanel();
        equationListPanel.setLayout(new BoxLayout(equationListPanel, BoxLayout.Y_AXIS));
        equationScrollPane = new JScrollPane(equationListPanel,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        equationScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        equationScrollPane.getVerticalScrollBar()
                          .putClientProperty("JScrollBar.fastWheelScrolling", true);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.gridheight = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_START;
        panel.add(equationScrollPane, gbc);

        // add one field by default
        addEquationField();

        // | BOTTOM ROW
        // | | addEquationButton [JButton]
        // | | clearEquationButton [JButton]
        addEquationButton = new ZButton("Add", ZScheme.colorGreen);
        addEquationButton.setFont(ZScheme.labelFont.deriveFont(11.0f));
        // disable 'add' first
        // enable only when all equations are valid
        addEquationButton.setDisabled();
        addEquationButton.addActionListener(e -> {
            addEquationField();
            // disable the button
            addEquationButton.setDisabled();
            // scroll to recently added
            JScrollBar scrollBar = equationScrollPane.getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMaximum());
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.2;
        gbc.insets = new Insets(0, 0, 0, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_END;
        panel.add(addEquationButton, gbc);

        clearEquationButton = new ZButton("Clear", ZScheme.colorPurple);
        clearEquationButton.setFont(ZScheme.labelFont.deriveFont(11.0f));
        clearEquationButton.addActionListener(e -> {
            equationListPanel.removeAll();
            for (FieldStatus status : equationFields.values())
                if (status.timer != null)
                    status.timer.stop();
            equationFields.clear();
            // clear output area
            outputArea.setText("");
            addEquationField();
            // disable add button
            addEquationButton.setDisabled();
            equationListPanel.revalidate();
            equationListPanel.repaint();
        });
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 2, 0, 0);
        panel.add(clearEquationButton, gbc);
        return panel;
    }

    private void addEquationField() {
        BorderLayout layout = new BorderLayout();
        layout.setHgap(5);
        JPanel row = new JPanel(layout);
        row.setMinimumSize(new Dimension(Integer.MAX_VALUE, 30));
        // row.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        ZTextField equationField = new ZTextField();
        equationField.setMinimumSize(equationField.getPreferredSize());
        equationField.setMaximumSize(equationField.getPreferredSize());
        equationField.getDocument().addDocumentListener(new DocumentListener() {
            // listen to text changes in the field
            @Override public void insertUpdate(DocumentEvent e) {
                handleEquationField(equationField, true);
            }

            @Override public void removeUpdate(DocumentEvent e) {
                handleEquationField(equationField, true);
            }

            @Override public void changedUpdate(DocumentEvent e) {
                handleEquationField(equationField, true);
            }
        });

        ZButton removeButton = new ZButton("X", ZScheme.colorRed);
        Component strut = Box.createVerticalStrut(5);
        // remove one text field if remove button is clicked
        removeButton.addActionListener(e -> {
            FieldStatus thisStatus = equationFields.remove(equationField);
            if (thisStatus.timer != null)
                thisStatus.timer.stop();

            equationListPanel.remove(row);
            equationListPanel.remove(strut);
            equationListPanel.revalidate();
            equationListPanel.repaint();
            if (equationFields.isEmpty())
                addEquationField();

            updateSolveButton();
        });
        equationFields.put(equationField, new FieldStatus());
        updateSolveButton();
        row.add(equationField, BorderLayout.CENTER);
        row.add(removeButton, BorderLayout.EAST);
        equationListPanel.add(row);
        equationListPanel.add(strut);
        equationListPanel.revalidate();
    }

    private void handleEquationField(ZTextField textField, boolean hasDelay) {
        FieldStatus thisStatus = equationFields.get(textField);
        thisStatus.isValid = false;

        if (thisStatus.timer != null)
            thisStatus.timer.stop();

        Timer newTimer = new Timer(hasDelay ? timerDelay : 0, e -> {
            String text = textField.getText().trim();
            boolean isValid = false;
            String errorMessage = "";

            try {
                equations.validateEquation(text);
                isValid = true;
            } catch (Exception ex) {
                errorMessage = ex.getMessage();
            }

            if (isValid) {
                textField.setNormalMode();
                textField.setToolTipText(null);
                // enables 'add' equation button
                addEquationButton.setEnabled();
            } else {
                textField.setErrorMode();
                textField.setToolTipText(errorMessage);
                // disables 'add' equation button
                addEquationButton.setDisabled();
            }
            thisStatus.isValid = isValid;
            updateSolveButton();
        });
        newTimer.setRepeats(false);
        newTimer.start();
        // replace new timer
        thisStatus.timer = newTimer;
    }

    private void updateSolveButton() {
        boolean allValid = true;
        for (FieldStatus status : equationFields.values()) {
            if (!status.isValid) {
                allValid = false;
                break;
            }
        }
        if (allValid)
            solveButton.setEnabled();
        else 
            solveButton.setDisabled();
    }

    // build once
    private JPanel buildOutputPanel() {
        // ====== OUTPUT PANEL ======
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        TitledBorder border = new TitledBorder(baseBorder, "Output");
        border.setTitleFont(ZScheme.titleFont);
        border.setTitleColor(ZScheme.colorGray);
        panel.setBorder(border);

        // OUTPUT PANEL <== OUTPUT TEXT
        outputArea = new JTextArea(5, 50);
        outputArea.setText("");
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setFont(ZScheme.labelFont);
        outputArea.setForeground(ZScheme.colorGray);
        JScrollPane outputScroll = new JScrollPane(outputArea);

        panel.add(outputScroll);

        return panel;
    }

    public static void setWindowsLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        setWindowsLookAndFeel();
        SwingUtilities.invokeLater(GUIMode::new);
    }
}