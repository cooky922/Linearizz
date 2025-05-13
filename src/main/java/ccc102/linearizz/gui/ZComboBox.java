package ccc102.linearizz.gui;

import java.awt.Color;
import javax.swing.JComboBox;

public class ZComboBox<E> extends JComboBox<E> {

    public ZComboBox(E[] items, Color backColor) {
        super(items);
        setFont(ZScheme.labelFont);
        setForeground(ZScheme.colorGray);
        ZComboBoxUI.install(this, backColor);
    }

}
