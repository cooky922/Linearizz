package ccc102.linearizz.gui;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.CompoundBorder;

public class ZCardPanel extends JPanel {
    public ZCardPanel(String title, Border baseBorder) {
        this(title, baseBorder, false);
    }

    public ZCardPanel(String title, Border baseBorder, boolean addOuterPadding) {
        super();
        TitledBorder innerBorder = new TitledBorder(baseBorder, title);
        innerBorder.setTitleFont(ZScheme.titleFont);
        innerBorder.setTitleColor(ZScheme.colorGray);
        Border border;
        if (addOuterPadding)
            border = new CompoundBorder(new EmptyBorder(10, 10, 10, 10), innerBorder);
        else 
            border = innerBorder;
        setBorder(border);
    }
}