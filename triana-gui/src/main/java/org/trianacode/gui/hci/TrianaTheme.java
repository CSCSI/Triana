package org.trianacode.gui.hci;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Sep 26, 2010
 */
public class TrianaTheme extends DefaultMetalTheme {

    private final ColorUIResource primary1 = new ColorUIResource(102, 153, 153);
    private final ColorUIResource primary2 = new ColorUIResource(128, 192, 192);
    private final ColorUIResource primary3 = new ColorUIResource(159, 235, 235);

    private final FontUIResource plainFont = new FontUIResource("SansSerif", Font.PLAIN, 10);
    private final FontUIResource boldFont = new FontUIResource("SansSerif", Font.BOLD, 10);

    /**
     * Returns the name of the theme.
     *
     * @return the name of the theme.
     */
    public String getName() {
        return "Triana Theme";
    }

    /**
     * Returns the system text font.
     *
     * @return the system text font.
     */
    public FontUIResource getSystemTextFont() {
        return plainFont;
    }

    /**
     * Returns the user text font.
     *
     * @return the user text font.
     */
    public FontUIResource getUserTextFont() {
        return plainFont;
    }

    /**
     * Returns the window title font.
     *
     * @return the window title font.
     */
    public FontUIResource getWindowTitleFont() {
        return plainFont;
    }

    /**
     * Returns the sub text font.
     *
     * @return the sub text font.
     */
    public FontUIResource getSubTextFont() {
        return plainFont;
    }

    /**
     * Returns the menu text font.
     *
     * @return the menu text font.
     */
    public FontUIResource getMenuTextFont() {
        return plainFont;
    }


    /**
     * Returns the control text font.
     *
     * @return the control text font.
     */
    public FontUIResource getControlTextFont() {
        return plainFont;
    }

    //protected ColorUIResource getPrimary1() { return primary1; }
    //protected ColorUIResource getPrimary2() { return primary2; }
    //protected ColorUIResource getPrimary3() { return primary3; }

}
