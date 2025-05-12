package ccc102.linearizz.gui;

import java.awt.Color;
import java.awt.Font;

public interface ZScheme {
    // color schemes
    Color colorGray = new Color(0x252525);
    Color colorLightGray = new Color(0x959597).brighter();
    Color colorGreen = new Color(0x39971d);
    Color colorRed = new Color(0xce3635);
    Color colorPurple = new Color(0x8857e5);
    Color colorBlue = new Color(0x0e6ccd);
    
    // predefined fonts
    Font titleFont = ZFonts.titleFont.deriveFont(Font.BOLD, 15.0f);
    Font labelFont = ZFonts.labelFont.deriveFont(Font.PLAIN, 12.0f);
}