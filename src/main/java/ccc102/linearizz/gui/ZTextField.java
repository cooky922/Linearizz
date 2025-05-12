package ccc102.linearizz.gui;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.CompoundBorder;

public class ZTextField extends JTextField {
    private static final Color backNormalColor = ZScheme.colorLightGray;
    private static final Color backErrorColor  = ZScheme.colorRed.brighter();
    private static final Color foreNormalColor = ZScheme.colorGray;
    private static final Color foreErrorColor  = Color.WHITE;

    private static Border normalBorder = new CompoundBorder(
        new MatteBorder(0, 0, 2, 0, backNormalColor.darker()),
        new EmptyBorder(4, 4, 4, 4)
    );

    private static Border errorBorder = new CompoundBorder(
        new MatteBorder(0, 0, 2, 0, backErrorColor.darker()),
        new EmptyBorder(4, 4, 4, 4)
    );

    private boolean isNormal = false;

    public ZTextField() {
        setFont(ZScheme.labelFont);
        setNormalMode();
    }

    public void setNormalMode() {
        if (!isNormal) {
            setForeground(foreNormalColor);
            setBackground(backNormalColor);
            setBorder(normalBorder);
            isNormal = true;
        }
    }

    public void setErrorMode() {
        if (isNormal) {
            setForeground(foreErrorColor);
            setBackground(backErrorColor);
            setBorder(errorBorder);
            isNormal = false;
        }
    }

    @Override 
    public JToolTip createToolTip() {
        return new ZToolTip(this);
    }
}