package ccc102.linearizz.gui;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.AbstractButton;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class ZButtonUI extends BasicButtonUI {

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        AbstractButton button = (AbstractButton) c;
        button.setOpaque(false);
        button.setBorder(new EmptyBorder(5, 15, 5, 15));
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        paintBackground(g, b, b.getModel().isPressed() ? 2 : 0);
        super.paint(g, c);
    }

    @Override 
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
        final int yOffset = b.getModel().isPressed() ? 2 : 0;
        Point newLoc = textRect.getLocation();
        newLoc.translate(0, yOffset);
        final Rectangle newTextRect = new Rectangle(newLoc, textRect.getSize());
        super.paintText(g, b, newTextRect, text);
    }

    private void paintBackground(Graphics g, JComponent c, int yOffset) {
        Graphics2D g2 = (Graphics2D) g;
        Dimension size = c.getSize();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getBackground().darker());
        g2.fillRoundRect(0, yOffset, size.width, size.height - yOffset, 2, 2);
        g2.setColor(c.getBackground());
        g2.fillRoundRect(0, yOffset, size.width, size.height + yOffset - 4, 2, 2);
    }
}