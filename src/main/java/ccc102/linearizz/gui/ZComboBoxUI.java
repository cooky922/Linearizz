package ccc102.linearizz.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class ZComboBoxUI extends BasicComboBoxUI {
    public static <E> void install(JComboBox<E> combo, Color backColor) {
        combo.setOpaque(true);
        combo.setBackground(backColor);
        combo.setForeground(Color.WHITE);
        combo.setBorder(new EmptyBorder(5, 5, 5, 5));
        combo.setUI(new ZComboBoxUI());
        combo.setRenderer(new ListCellRenderer(backColor));
    }

    @Override
    protected JButton createArrowButton() {
        JButton b = new JButton("V");
        b.setFont(ZScheme.labelFont);
        b.setBorder(null);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setForeground(Color.WHITE);
        return b;
    }

    @Override
    public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
        Graphics2D g2 = (Graphics2D) g;
        Dimension size = comboBox.getSize();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(comboBox.getBackground().darker());
        g2.fillRoundRect(0, 0, size.width, size.height, 4, 4);
        g2.setColor(comboBox.getBackground());
        g2.fillRoundRect(0, 0, size.width, size.height - 4, 4, 4);
    }

    private static class ListCellRenderer extends DefaultListCellRenderer {
        private Color backColor;

        public ListCellRenderer(Color backColor) {
            super();
            this.backColor = backColor;
        }

        @Override
        public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
        {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            if (isSelected) {
                label.setBackground(backColor.darker());
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }
            label.setBorder(new EmptyBorder(5, 5, 5, 5));
            return label;
        }
    }
}