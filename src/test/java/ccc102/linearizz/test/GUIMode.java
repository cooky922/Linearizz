package ccc102.linearizz.test;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;

import ccc102.linearizz.gui.ZMainFrame;

public class GUIMode {

    public static void setWindowsLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        setWindowsLookAndFeel();
        SwingUtilities.invokeLater(ZMainFrame::new);
    }
}
