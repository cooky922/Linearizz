package ccc102.linearizz.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
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
        addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseEntered(MouseEvent e) {
                if (ZButton.this.isEnabled())
                    ZButton.this.setBackground(brighterColor(backColor, 1.1f));
            }

            @Override 
            public void mouseExited(MouseEvent e) {
                if (ZButton.this.isEnabled())
                    ZButton.this.setBackground(backColor);
            }
        });
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

    /// IMPLEMENTATION DETAILS
    private static Color brighterColor(Color c, double factor) {
        final int r = (int) Math.min(255, c.getRed() * factor);
        final int g = (int) Math.min(255, c.getGreen() * factor);
        final int b = (int) Math.min(255, c.getBlue() * factor);
        return new Color(r, g, b);
    }
}