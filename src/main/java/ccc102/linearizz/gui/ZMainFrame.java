package ccc102.linearizz.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;

import ccc102.linearizz.*;
import ccc102.linearizz.exceptions.LinearSystemException;
import ccc102.linearizz.system.*;

public class ZMainFrame extends JFrame {

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

    /// default settings
    private static final int timerDelay = 1000;
    private static LineBorder baseBorder = new LineBorder(ZScheme.colorGray, 3, true);

    /// SETTINGS data members
    private ZComboBox<String> registerModeBox;
    private ZComboBox<String> outputModeBox;
    private boolean autoRegister = false;

    /// CALCULATOR data members
    // > variable panel
    private ZTextField variableInputField;
    private JPanel variableListPanel;
    private JScrollPane variableScrollPane;
    private ZButton addVariableButton;
    private ZButton clearVariableButton;

    // > equation panel
    private JPanel equationListPanel;
    private JScrollPane equationScrollPane;
    private ZButton addEquationButton;
    private ZButton clearEquationButton;
    
    // > solve button
    private ZButton solveButton;

    // > output panel
    private JTextArea outputArea;

    // > logics
    private Variables variables = new Variables();
    private Equations equations = new Equations(variables); // manual by default
    private Map<ZTextField, FieldStatus> equationFields = new HashMap<>();

    public ZMainFrame() {
        super("Linearizz App");
        setSize(350, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(ZImageIcon.getFrom("/app-icon.png").getImage());
        buildLayout();
        setVisible(true);
    }

    /// ======================
    /// LAYOUT METHODS
    /// ======================
    // .buildLayout()
    //  .buildCalculatorPanel()
    //   .buildVariablePanel()
    //   .buildEquationPanel()
    //    .addEquationField()
    //   .buildSolveButton()
    //   .buildOutputPanel()
    //  .buildSettingsPanel()
    //  .buildAboutPanel()
    public void buildLayout() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new ZTabbedPaneUI());
        tabbedPane.add("Calculator", buildCalculatorPanel());
        tabbedPane.add("Settings", buildSettingsPanel());
        tabbedPane.add("About", buildAboutPanel());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    // > about panel
    public JPanel buildAboutPanel() {
        JPanel panel = new ZCardPanel("About This", baseBorder, true);
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        // turn on gaps between components and container
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        Font smallFont = ZScheme.labelFont.deriveFont(11.0f);
        Font smallBoldFont = smallFont.deriveFont(Font.BOLD);

        // Labels and values
        JLabel descriptionLabel = new JLabel("Description");
        JTextArea description = new JTextArea(
            "This application is created to automate " +
            "the process of solving any system of linear equations " +
            "which are widely used in the field of " +
            "mathematics and computer science."
        );
        JLabel purposeLabel = new JLabel("Purpose");
        JLabel purpose = new JLabel("CCC102 Finals Project");

        JLabel versionLabel = new JLabel("Version");
        JLabel version = new JLabel("v0.2-beta");

        JLabel createdLabel = new JLabel("Created by");
        JLabel created = new JLabel(
            "<html>Desmond Gold Bongcawel<br>" +
            "Reymark Degamo<br>" +
            "Jericho Villadolid</html>"
        );

        JLabel githubLabel = new JLabel("GitHub");
        JLabel githubLink = new JLabel(
            "<html><a href=''>https://github.com/cooky922/Linearizz</a></html>"
        );
        githubLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        githubLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/cooky922/Linearizz"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JLabel catPicture = new JLabel(ZImageIcon.getFrom("/images/oia-uia.gif"));
        catPicture.setHorizontalAlignment(SwingConstants.CENTER);

        descriptionLabel.setFont(smallBoldFont);
        descriptionLabel.setForeground(ZScheme.colorGray.brighter());
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);
        description.setFocusable(false);
        description.setOpaque(false);
        description.setFont(smallFont);
        description.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        purposeLabel.setFont(smallBoldFont);
        purposeLabel.setForeground(ZScheme.colorGray.brighter());
        purpose.setFont(smallFont);
        versionLabel.setFont(smallBoldFont);
        versionLabel.setForeground(ZScheme.colorGray.brighter());
        version.setFont(smallFont);
        createdLabel.setFont(smallBoldFont);
        createdLabel.setForeground(ZScheme.colorGray.brighter());
        created.setFont(smallFont);
        githubLabel.setFont(smallBoldFont);
        githubLabel.setForeground(ZScheme.colorGray.brighter());
        githubLink.setFont(smallFont);

        /// LAYOUTS
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(
                    layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(descriptionLabel)
                            .addComponent(purposeLabel)
                            .addComponent(versionLabel)
                            .addComponent(createdLabel)
                            .addComponent(githubLabel)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(description)
                            .addComponent(purpose)
                            .addComponent(version)
                            .addComponent(created)
                            .addComponent(githubLink)
                        )
                )
                .addComponent(catPicture, GroupLayout.Alignment.CENTER)
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionLabel)
                    .addComponent(description, GroupLayout.PREFERRED_SIZE, 
                                               GroupLayout.DEFAULT_SIZE, 
                                               GroupLayout.PREFERRED_SIZE)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(purposeLabel)
                    .addComponent(purpose)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(versionLabel)
                    .addComponent(version)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(createdLabel)
                    .addComponent(created)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(githubLabel)
                    .addComponent(githubLink)
                )
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(catPicture)
                .addGap(0, 0, Short.MAX_VALUE)
        );

        return panel;
    }

    // > settings panel
    public JPanel buildSettingsPanel() {
        /// PANEL INITIALIZATION
        JPanel panel = new ZCardPanel("Settings", baseBorder, true);

        /// COMPONENTS
        // <regModeLabel>
        ZLabel registerModeLabel = new ZLabel("Variable Register Mode");
        registerModeLabel.setFont(registerModeLabel.getFont().deriveFont(11.0f));
        registerModeLabel.setBackground(ZScheme.colorLightGray.darker());

        // <regModeBox>
        registerModeBox = new ZComboBox<>(new String[] {"Manual", "Automatic"}, ZScheme.colorBlue);
        registerModeBox.addActionListener(e -> doSwitchRegisterMode());

        // <outModeLabel>
        ZLabel outputModeLabel = new ZLabel("Output Mode");
        outputModeLabel.setFont(outputModeLabel.getFont().deriveFont(11.0f));

        // <outModeBox>
        outputModeBox = new ZComboBox<>(new String[] {"Fraction", "Decimal"}, ZScheme.colorBlue);

        /// LAYOUT
        // <regModeLabel> <regModeBox>
        // <outModeLabel> <outModeBox>
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        final int fixedSize = GroupLayout.PREFERRED_SIZE;

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                  .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                          .addComponent(registerModeLabel, fixedSize, fixedSize, fixedSize)
                          .addComponent(outputModeLabel, fixedSize, fixedSize, fixedSize))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                  .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                          .addComponent(registerModeBox, 0, 50, Short.MAX_VALUE)
                          .addComponent(outputModeBox, 0, 50, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                  .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                          .addComponent(registerModeLabel)
                          .addComponent(registerModeBox))
                  .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                          .addComponent(outputModeLabel)
                          .addComponent(outputModeBox))
        );

        return panel;
    }

    // > calculator panel
    public JPanel buildCalculatorPanel() {
        /// PANEL INITIALIZATION
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        solveButton = buildSolveButton();

        /// LAYOUT
        panel.setLayout(new GridBagLayout());
        GBCFactory c = new GBCFactory().setFill(GridBagConstraints.HORIZONTAL)
                                       .setAnchor(GridBagConstraints.PAGE_START)
                                       .setWeights(1, 1);

        panel.add(buildVariablePanel(), c.clone()
                                         .setInternalPadding(0, 25)
                                         .setGridPos(0, 0)
                                         .getGBC());

        panel.add(buildEquationPanel(), c.clone()
                                         .setInternalPadding(0, 150)
                                         .setGridPos(0, 1)
                                         .getGBC());

        panel.add(solveButton, c.clone()
                                .setExternalPadding(new Insets(5, 5, 5, 5))
                                .setInternalPadding(0, 25)
                                .setGridPos(0, 2)
                                .getGBC());

        panel.add(buildOutputPanel(), c.clone()
                                       .setAnchor(GridBagConstraints.PAGE_END)
                                       .setInternalPadding(0, 100)
                                       .setGridPos(0, 3)
                                       .getGBC());

        return panel;
    }

    // > calculator panel > solve button
    private ZButton buildSolveButton() {
        ZButton button = new ZButton("Solve", ZScheme.colorGreen, true);
        // disabled by default, only enable if all equations are solvable
        button.setDisabled();
        button.setFont(ZScheme.titleFont.deriveFont(14.0f));
        button.addActionListener(e -> doSolve());
        return button;
    }

    // > calculator panel > variable panel
    private JPanel buildVariablePanel() {
        /// PANEL INITIALIZATION
        JPanel panel = new ZCardPanel("Variables", baseBorder);

        /// COMPONENTS
        // <enter-name-heading>
        ZLabel enterNameHeading = new ZLabel("Enter Name");

        // <variable-input-field>
        variableInputField = new ZTextField();

        // <add-button>
        addVariableButton = new ZButton("Add", ZScheme.colorGreen);
        addVariableButton.addActionListener(e -> doAddVariable());

        // <list-heading>
        ZLabel listHeading = new ZLabel("List");

        // <variable-scroll-pane>
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

        // <clear-button>
        clearVariableButton = new ZButton("Clear", ZScheme.colorPurple);
        clearVariableButton.addActionListener(e -> doClearVariable());

        /// LAYOUT
        // <enter-name-heading [L]>
        // <variable-input-field [TF]>  <add-button [B]> [3:1]
        // <list-heading [L]>
        // <variable-scroll-pane [SP]>  <clear-button [B]> [3:1]

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(false);

        final int fixedSize = GroupLayout.PREFERRED_SIZE;

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                  .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                          .addComponent(enterNameHeading, fixedSize, fixedSize, fixedSize)
                          .addComponent(variableInputField, 0, 400, Short.MAX_VALUE)
                          .addComponent(listHeading, fixedSize, fixedSize, fixedSize)
                          .addComponent(variableScrollPane, 0, 400, Short.MAX_VALUE)
                  )
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                          .addComponent(addVariableButton, fixedSize, 100, Short.MAX_VALUE)
                          .addComponent(clearVariableButton, fixedSize, 100, Short.MAX_VALUE)
                  )
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                  .addComponent(enterNameHeading)
                  .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                          .addComponent(variableInputField, 0, 30, Integer.MAX_VALUE)
                          .addComponent(addVariableButton, 0, 30, Integer.MAX_VALUE)
                  )
                  .addComponent(listHeading)
                  .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                          .addComponent(variableScrollPane, 0, 30, Integer.MAX_VALUE)
                          .addComponent(clearVariableButton, 0, 30, Integer.MAX_VALUE)
                  )
                  .addGap(0, 0, Short.MAX_VALUE)
        );

        return panel;
    }

    // > calculator panel > equation panel
    private JPanel buildEquationPanel() {
        /// PANEL INITIALIZATION
        JPanel panel = new ZCardPanel("Equations", baseBorder);

        /// COMPONENTS
        equationListPanel = new JPanel();
        equationListPanel.setLayout(new BoxLayout(equationListPanel, BoxLayout.Y_AXIS));
        equationScrollPane = new JScrollPane(equationListPanel,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        equationScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        equationScrollPane.getVerticalScrollBar()
                          .putClientProperty("JScrollBar.fastWheelScrolling", true);

        addEquationButton = new ZButton("Add", ZScheme.colorGreen);
        addEquationButton.setFont(ZScheme.labelFont.deriveFont(11.0f));
        // disable 'add' first
        // enable only when all equations are valid
        addEquationButton.setDisabled();
        addEquationButton.addActionListener(e -> doAddEquation());

        clearEquationButton = new ZButton("Clear", ZScheme.colorPurple);
        clearEquationButton.setFont(ZScheme.labelFont.deriveFont(11.0f));
        clearEquationButton.addActionListener(e -> doClearEquation());

        /// LAYOUT
        panel.setLayout(new GridBagLayout());
        panel.add(equationScrollPane, new GBCFactory()
                                      .setAnchor(GridBagConstraints.PAGE_START)
                                      .setFill(GridBagConstraints.BOTH)
                                      .setWeights(1.0, 0.8)
                                      .setGridSize(4, 3)
                                      .setGridPos(0, 0)
                                      .getGBC());

        panel.add(addEquationButton, new GBCFactory()
                                    .setAnchor(GridBagConstraints.PAGE_END)
                                    .setFill(GridBagConstraints.HORIZONTAL)
                                    .setWeights(0.5, 0.2)
                                    .setExternalPadding(new Insets(0, 0, 0, 2))
                                    .setGridSize(2, 1)
                                    .setGridPos(0, 3)
                                    .getGBC());

        panel.add(clearEquationButton, new GBCFactory()
                                      .setAnchor(GridBagConstraints.PAGE_END)
                                      .setFill(GridBagConstraints.HORIZONTAL)
                                      .setWeights(0.5, 0.2)
                                      .setExternalPadding(new Insets(0, 2, 0, 0))
                                      .setGridSize(2, 1)
                                      .setGridPos(2, 3)
                                      .getGBC());

        // add one field by default
        addEquationField();
            
        return panel;
    }

    // > calculator panel > equation panel > equation field
    private void addEquationField() {
        BorderLayout layout = new BorderLayout();
        layout.setHgap(5);
        JPanel row = new JPanel(layout);
        row.setMinimumSize(new Dimension(Integer.MAX_VALUE, 30));
        // row.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        Component rowGap = Box.createVerticalStrut(5);

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
        removeButton.addActionListener(e -> doRemoveEquation(equationField, row, rowGap));
        equationFields.put(equationField, new FieldStatus());
        updateSolveButton();

        row.add(equationField, BorderLayout.CENTER);
        row.add(removeButton, BorderLayout.EAST);
        equationListPanel.add(row);
        equationListPanel.add(rowGap);
        equationListPanel.revalidate();
    }

    // > calculator panel > output panel
    private JPanel buildOutputPanel() {
        /// PANEL INITIALIZATION
        JPanel panel = new ZCardPanel("Output", baseBorder);

        /// COMPONENTS
        outputArea = new JTextArea(5, 50);
        outputArea.setText("");
        outputArea.setEditable(false);
        outputArea.setFocusable(false);
        outputArea.setLineWrap(true);
        outputArea.setFont(ZScheme.labelFont);
        outputArea.setForeground(ZScheme.colorGray);
        JScrollPane outputScroll = new JScrollPane(outputArea);

        /// LAYOUT
        panel.add(outputScroll);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        return panel;
    }

    /// ======================
    /// LOGICS METHODS
    /// ======================
    /// >> settings
    // .doSwitchRegisterMode()
    //
    /// >> variables
    // .doAddVariable()
    // .doClearVariable()
    //
    /// >> equations
    // .doAddEquation()
    // .doClearEquation()
    // .doRemoveEquation()
    // .handleEquationField()
    //
    /// >> solve
    // .updateSolveButton()
    // .doSolve()

    private void doSwitchRegisterMode() {
        final int index = registerModeBox.getSelectedIndex();
        switch (index) {
            // note: the default is manual register
            // switch to manual register
            case 0:
                if (autoRegister) {
                    variableInputField.setEditable(true);
                    variableInputField.setFocusable(true);
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
                    variableInputField.setFocusable(false);
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
    }

    private void doAddVariable() {
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
    }

    private void doClearVariable() {
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
    }

    private void doAddEquation() {
        addEquationField();
        // disable the button
        addEquationButton.setDisabled();
        // scroll to recently added
        JScrollBar scrollBar = equationScrollPane.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
    }

    private void doClearEquation() {
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
    }

    private void doRemoveEquation(ZTextField equationField, JPanel thisRow, Component thisStrut) {
        FieldStatus thisStatus = equationFields.remove(equationField);
        if (thisStatus.timer != null)
            thisStatus.timer.stop();

        equationListPanel.remove(thisRow);
        equationListPanel.remove(thisStrut);
        equationListPanel.revalidate();
        equationListPanel.repaint();

        if (equationFields.isEmpty())
            addEquationField();

        updateSolveButton();
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

    private void doSolve() {
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
            for (int i = 0; i < names.length; ++i) {
                builder.append(names[i]);
                builder.append(" = ");
                // displays fraction instead of decimal
                if (outputModeBox.getSelectedIndex() == 0)
                    builder.append(Fraction.fromDecimal(x.getElement(i)));
                else 
                    builder.append(x.getElement(i));
                builder.append("\n");
            }
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
    }
}