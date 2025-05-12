package ccc102.linearizz.gui;

import java.awt.Image;
import javax.swing.ImageIcon;

public class ZAppIcon {

    // returns the loaded app icon
    public static Image getImage() {
        return appIcon;
    }

    /// DO NOT TOUCH THIS
    // implementation details
    private static Image appIcon;
    static {
        appIcon = new ImageIcon(ZMainFrame.entryPointClass.getResource("/app-icon.png")).getImage();
    }
}