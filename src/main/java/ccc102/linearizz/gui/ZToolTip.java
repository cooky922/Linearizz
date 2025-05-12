package ccc102.linearizz.gui;

import javax.swing.JComponent;
import javax.swing.JToolTip;

public class ZToolTip extends JToolTip {
    public ZToolTip(JComponent c) {
        super();
        setComponent(c);
        setFont(ZScheme.labelFont);
    }
}
