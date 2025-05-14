package ccc102.linearizz.gui;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class ZTabbedPaneUI extends BasicTabbedPaneUI {

    private int hoverTabIndex = -1;

    @Override
    public void installDefaults() {
        super.installDefaults();
        tabPane.setFont(ZScheme.labelFont);
        tabPane.setForeground(ZScheme.colorLightGray.darker());
        tabPane.setBackground(ZScheme.colorLightGray);
        // remove default borders/gaps
        contentBorderInsets = new Insets(0, 0, 0, 0);
        tabAreaInsets      = new Insets(4, 4, 0, 4);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
    }

    @Override 
    public void installListeners() {
        super.installListeners();
        // listener for mouse movement over the tab area
        tabPane.addMouseMotionListener(new MouseMotionAdapter() {
            @Override 
            public void mouseMoved(MouseEvent e) {
                int idx = tabForCoordinate(tabPane, e.getX(), e.getY());
                if (idx != hoverTabIndex) { 
                    int old = hoverTabIndex;
                    repaintTab(old); // hover exit
                    hoverTabIndex = idx;
                    repaintTab(hoverTabIndex); // hover enter
                }
            }
        });
        // also clear rollover when mouse exits
        tabPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                int old = hoverTabIndex;
                hoverTabIndex = -1;
                repaintTab(old);
            }
        });
    }

    @Override
    public int calculateTabHeight(int placement, int index, int fontHeight) {
        // add height
        return fontHeight + 16;
    }

    private void repaintTab(int tabIndex) {
        if (tabIndex < 0)
            return;
        Rectangle r = getTabBounds(tabPane, tabIndex);
        tabPane.repaint(r.x, r.y, r.width, r.height);
    }

    @Override
    public void paintText(Graphics g, int tabPlacement,
                            Font font, FontMetrics metrics, int tabIndex,
                            String title, Rectangle textRect,
                            boolean isSelected) {
        final int yOffset = (isSelected || tabIndex == hoverTabIndex) ? 0 : -3;
        Rectangle newTextRect = new Rectangle(textRect);
        newTextRect.y += yOffset;
        if (isSelected)
            // tabPane.setForegroundAt(tabIndex, Color.WHITE);
            tabPane.setForegroundAt(tabIndex, ZScheme.colorGray);
        else
            tabPane.setForegroundAt(tabIndex, ZScheme.colorLightGray.darker());
        super.paintText(g, tabPlacement, font, metrics, 
                        tabIndex, title, newTextRect, isSelected);
    }

    @Override
    public void paintTabBackground(Graphics g,
                                      int tabPlacement,
                                      int tabIndex,
                                      int x, int y, int w, int h,
                                      boolean isSelected) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // paint only if selected
        if (isSelected) {
            g2.setColor(tabPane.getBackground());
            g2.fillRoundRect(x, y, w, h, 4, 4);
        }

        // draw a little bottom line for a final touch
        // paint only if either selected or hover over it
        if (isSelected || tabIndex == hoverTabIndex) {
            g2.setColor(tabPane.getForegroundAt(tabIndex).brighter());
            g2.fillRoundRect(x + w / 4, y + h - 2, w / 2, 2, 0, 0);
        }
    }


    @Override
    public void paintTabBorder(Graphics g, int tabPlacement,
                               int tabIndex,
                               int x, int y, int w, int h,
                               boolean isSelected) {
        // no border
    }

    @Override
    public void paintFocusIndicator(Graphics g, int tabPlacement,
                                    Rectangle[] rects, int tabIndex,
                                    Rectangle iconRect, Rectangle textRect,
                                    boolean isSelected) {
        // no focus ring
    }
}