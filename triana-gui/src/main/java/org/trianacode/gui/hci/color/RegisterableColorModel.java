package org.trianacode.gui.hci.color;

import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 14, 2010
 */
public interface RegisterableColorModel extends ColorModel {

    public String[] getRegistrationNames();

    public Color getDefaultColorForRegistrationName(String name);

}
