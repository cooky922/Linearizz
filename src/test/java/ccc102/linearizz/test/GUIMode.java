package ccc102.linearizz.test;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class GUIMode extends JFrame {
    // fixed attributes
    private static Color colorGray = new Color(0x252525);
    private static Font titleFont  = CustomFontFactory.getMinecraftTitleFont()
                                                      .deriveFont(Font.BOLD, 15);
    private static Font labelFont  = CustomFontFactory.getMinecraftLabelFont()
                                                      .deriveFont(Font.PLAIN, 12);
    private static LineBorder baseBorder = new LineBorder(colorGray, 3, true);

    /// Within Variable Panel
    // selects either manual or automatic variable register
    private JComboBox<String> registerModeBox;
    private JTextField variableInputField;
    private JButton addVariableButton;
    private JPanel variableListPanel;
    private JScrollPane variableScrollPane;
    private JButton clearVariableButton;

    /// Within Equation Panel
    private JPanel equationListPanel;
    private JScrollPane equationScrollPane;
    private JButton addEquationButton;
    private JButton clearEquationButton;

    /// Solve Button
    private JButton solveButton;

    /// Within Output Panel
    private JTextArea outputArea;

    public GUIMode() {
        super("Linearizz App");
        setSize(350, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon(GUIMode.class.getResource("/app-icon.png")).getImage());

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
        solveButton = buildSolveButton();
        c.gridy = 2;
        c.ipady = 25;
        contentPane.add(solveButton, c);

        // output panel
        c.gridy = 3;
        c.ipady = 100;
        c.anchor = GridBagConstraints.PAGE_END;
        contentPane.add(buildOutputPanel(), c);
    }

    // build once
    private JButton buildSolveButton() {
        JButton button = new JButton("Solve");
        button.setFont(titleFont.deriveFont(13));
        button.setForeground(new Color(0x0e6626));
        button.addActionListener(e -> {
            // TODO
        });
        button.setBorder(baseBorder);
        return button;
    }

    // build once
    private JPanel buildVariablePanel() {
        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder(baseBorder, "Variables");
        border.setTitleFont(titleFont);
        panel.setBorder(border);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        // VARIABLE PANEL
        // | 
        // | TOP ROW
        // | | registerModeBox [JComboBox]
        // | 
        registerModeBox = new JComboBox<>(new String[] {"Manual Register", "Automatic Register"});
        registerModeBox.setFont(labelFont);
        registerModeBox.setForeground(colorGray);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.PAGE_START;
        panel.add(registerModeBox, gbc);

        // | ENTER NAME
        // | | enterNameHeading [JLabel]
        JLabel enterNameHeading = new JLabel("Enter Name");
        enterNameHeading.setFont(labelFont);
        enterNameHeading.setForeground(colorGray);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(enterNameHeading, gbc);

        // | MIDDLE ROW
        // | | variableInputField [JTextField] [3/4 width]
        // | | addVariableButton [JButton] [1/4 width]
        variableInputField = new JTextField();
        variableInputField.setFont(labelFont);
        variableInputField.setForeground(colorGray);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(variableInputField, gbc);

        addVariableButton = new JButton("Add");
        addVariableButton.setFont(labelFont);
        addVariableButton.setForeground(colorGray);
        addVariableButton.addActionListener(e -> {
            String varName = variableInputField.getText().trim();
            if (!varName.isEmpty()) {
                JLabel varLabel = new JLabel(varName);
                varLabel.setFont(labelFont);
                // varLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                variableListPanel.add(varLabel);
                // scroll to recently added
                JScrollBar scrollBar = variableScrollPane.getHorizontalScrollBar();
                scrollBar.setValue(scrollBar.getMaximum());
                variableListPanel.revalidate();
                variableInputField.setText("");
                variableScrollPane.revalidate();
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(addVariableButton, gbc);

        // | VARIABLE LIST
        // | | listHeading [JLabel]
        JLabel listHeading = new JLabel("List");
        listHeading.setFont(labelFont);
        listHeading.setForeground(colorGray);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(listHeading, gbc);

        // | BOTTOM ROW
        // | | variableListPanel [JPanel] [3/4 width]
        // | | clearVariableButton [JButton] [1/4 width]
        variableListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        variableScrollPane = new JScrollPane(variableListPanel,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        // make it invisible to make it "less" annoying
        variableScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.weightx = 0.8;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(variableScrollPane, gbc);

        clearVariableButton = new JButton("Clear");
        clearVariableButton.setFont(labelFont);
        clearVariableButton.setForeground(colorGray);
        clearVariableButton.addActionListener(e -> {
            variableListPanel.removeAll();
            variableListPanel.revalidate();
            variableListPanel.repaint();
        });
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(clearVariableButton, gbc);

        return panel;
    }

    // build once
    private JPanel buildEquationPanel() {
        JPanel panel = new JPanel();
        TitledBorder border = new TitledBorder(baseBorder, "Equations");
        border.setTitleFont(titleFont);
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
        addEquationButton = new JButton("Add");
        addEquationButton.setFont(labelFont);
        addEquationButton.setForeground(colorGray);
        addEquationButton.addActionListener(e -> {
            addEquationField();
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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_END;
        panel.add(addEquationButton, gbc);

        clearEquationButton = new JButton("Clear");
        clearEquationButton.setFont(labelFont);
        clearEquationButton.setForeground(colorGray);
        clearEquationButton.addActionListener(e -> {
            equationListPanel.removeAll();
            addEquationField();
            equationListPanel.revalidate();
            equationListPanel.repaint();
        });
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        panel.add(clearEquationButton, gbc);
        return panel;
    }

    private void addEquationField() {
        JPanel row = new JPanel(new BorderLayout());
        row.setMinimumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JTextField equationField = new JTextField();
        equationField.setFont(labelFont);
        equationField.setForeground(colorGray);
        equationField.setMinimumSize(equationField.getPreferredSize());
        equationField.setMaximumSize(equationField.getPreferredSize());
        JButton removeButton = new JButton("Remove");
        Component strut = Box.createVerticalStrut(5);
        removeButton.setFont(labelFont);
        removeButton.addActionListener(e -> {
            equationListPanel.remove(row);
            equationListPanel.remove(strut);
            equationListPanel.revalidate();
            equationListPanel.repaint();
        });
        row.add(equationField, BorderLayout.CENTER);
        row.add(removeButton, BorderLayout.EAST);
        equationListPanel.add(row);
        equationListPanel.add(strut);
        equationListPanel.revalidate();
    }

    // build once
    private JPanel buildOutputPanel() {
        // ====== OUTPUT PANEL ======
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.PAGE_AXIS));
        TitledBorder outputBorder = new TitledBorder(baseBorder, "Output");
        outputBorder.setTitleFont(titleFont);
        outputPanel.setBorder(outputBorder);

        // OUTPUT PANEL <== OUTPUT TEXT
        outputArea = new JTextArea(5, 50);
        outputArea.setText("Output will be displayed here");
        outputArea.setEditable(false);
        outputArea.setFont(labelFont);
        outputArea.setForeground(colorGray);
        JScrollPane outputScroll = new JScrollPane(outputArea);

        outputPanel.add(outputScroll);

        return outputPanel;
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

class CustomFontFactory {
    // private static File[] fontFiles;
    private static InputStream[] fontStreams;
    private static Font[] fonts;

    private static String[] fontFileNames = {
        "/fonts/minecraft-ten.ttf",
        "/fonts/minecraft-seven.ttf"
    };

    static {
        try {
            final int fontCount = fontFileNames.length;
            fontStreams = new InputStream[fontCount];
            fonts = new Font[fontCount];
            for (int i = 0; i < fontCount; ++i) {
                fontStreams[i] = GUIMode.class.getResourceAsStream(fontFileNames[i]);
                fonts[i]       = Font.createFont(Font.TRUETYPE_FONT, fontStreams[i]);
            }
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (Font font : fonts)
                genv.registerFont(font);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static Font getMinecraftTitleFont() {
        return fonts[0];
    }

    public static Font getMinecraftLabelFont() {
        return fonts[1];
    }
}
