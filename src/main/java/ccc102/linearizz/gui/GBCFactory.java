package ccc102.linearizz.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GBCFactory {
    private GridBagConstraints c;

    public GBCFactory() {
        this.c = new GridBagConstraints();
    }

    public GBCFactory(GridBagConstraints c) {
        this.c = c;
    }

    /// chained methods
    public GBCFactory setAnchor(int anchor) {
        c.anchor = anchor;
        return this;
    }

    public GBCFactory setFill(int fill) {
        c.fill = fill;
        return this;
    }

    public GBCFactory setGridSize(int w, int h) {
        c.gridwidth = w;
        c.gridheight = h;
        return this;
    }

    public GBCFactory setGridPos(int x, int y) {
        c.gridx = x;
        c.gridy = y;
        return this;
    }

    public GBCFactory setExternalPadding(Insets insets) {
        c.insets = insets;
        return this;
    }

    public GBCFactory setInternalPadding(int ipadx, int ipady) {
        c.ipadx = ipadx;
        c.ipady = ipady;
        return this;
    }

    public GBCFactory setWeights(double weightx, double weighty) {
        c.weightx = weightx;
        c.weighty = weighty;
        return this;
    }

    /// collectors
    public GridBagConstraints getGBC() {
        return c;
    }

    public GBCFactory clone() {
        return new GBCFactory((GridBagConstraints) c.clone());
    }
}