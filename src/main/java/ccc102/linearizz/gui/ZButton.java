package ccc102.linearizz.gui;

import java.awt.Color;
import javax.swing.JButton;

public class ZButton extends JButton {
    private static final Color disabledColor = ZScheme.colorLightGray.darker();
    private final Color backColor;

    public ZButton(String name, Color backColor, boolean isTextTitle) {
        super(name);
        this.backColor = backColor;
        if (isTextTitle)
            setFont(ZScheme.titleFont);
        else
            setFont(ZScheme.labelFont);
        setForeground(Color.WHITE);
        setBackground(backColor);
        setUI(new ZButtonUI());
    }

    public ZButton(String name, Color backColor) {
        this(name, backColor, false);
    }

    public void setEnabled() {
        setEnabled(true);
        setBackground(backColor);
    }

    public void setDisabled() {
        setEnabled(false);
        setBackground(disabledColor);
    }
}