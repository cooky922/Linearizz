package ccc102.linearizz.gui;

import javax.swing.JLabel;

public class ZLabel extends JLabel {
    public ZLabel(String text) {
        super(text);
        setFont(ZScheme.labelFont);
        setForeground(ZScheme.colorGray);
    }
}
