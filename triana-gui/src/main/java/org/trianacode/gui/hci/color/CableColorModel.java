package org.trianacode.gui.hci.color;

import java.awt.Color;

import org.trianacode.taskgraph.Cable;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 5, 2010
 */

public interface CableColorModel extends ColorModel {

    public Color getColor(Cable cable);

}
