package ccc102.linearizz.gui;

import java.awt.Image;
import javax.swing.ImageIcon;

public class ZImageIcon {

    public static ImageIcon getFrom(String path) {
        return new ImageIcon(ZMainFrame.entryPointClass.getResource(path));
    }

}