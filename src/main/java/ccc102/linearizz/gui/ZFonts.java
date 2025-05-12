package ccc102.linearizz.gui;

import java.io.InputStream;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class ZFonts {
    // predefined fonts 
    public static Font titleFont;
    public static Font labelFont;

    // implementation details
    private static Font[] fonts;
    private static InputStream[] fontStreams;

    static {
        final String[] fileNames = {
            "/fonts/minecraft-ten.ttf",
            "/fonts/minecraft-seven.ttf"
        };
        final int fontCount = fileNames.length;
        fonts = new Font[fontCount];
        fontStreams = new InputStream[fontCount];
        try {
            for (int i = 0; i < fontCount; ++i) {
                fontStreams[i] = ZMainFrame.entryPointClass.getResourceAsStream(fileNames[i]);
                fonts[i]       = Font.createFont(Font.TRUETYPE_FONT, fontStreams[i]);
            }
            GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (Font font : fonts)
                genv.registerFont(font);
            titleFont = fonts[0];
            labelFont = fonts[1];
        } catch (Exception e) {
            // DIDN'T HANDLE FONT LOST AND NO ENTRY POINT CLASS
            titleFont = new Font("Arial", Font.BOLD, 16);
            labelFont = new Font("Arial", Font.PLAIN, 12);
        }
    }
}