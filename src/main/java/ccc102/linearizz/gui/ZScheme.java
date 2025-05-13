package ccc102.linearizz.gui;

import java.util.Map;
import java.util.HashMap;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;

public class ZScheme {
    // color schemes
    public static final Color colorGray = new Color(0x252525);
    public static final Color colorLightGray = new Color(0x959597).brighter();
    public static final Color colorGreen = new Color(0x39971d);
    public static final Color colorRed = new Color(0xce3635);
    public static final Color colorPurple = new Color(0x8857e5);
    public static final Color colorBlue = new Color(0x0e6ccd);
 
    // predefined fonts
    public static Font labelFont;
    public static Font titleFont;

    /// IMPLEMENTATION DETAILS
    static {
        labelFont = ZFonts.labelFont.deriveFont(Font.PLAIN, 12.0f);

        Map<TextAttribute, Object> moreTitleAttr = new HashMap<>();
        moreTitleAttr.put(TextAttribute.TRACKING, 0.1f);
        titleFont = ZFonts.titleFont.deriveFont(Font.BOLD, 15.0f)
                                    .deriveFont(moreTitleAttr);
    }
}