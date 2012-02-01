package org.trianacode.shiwa.models;

import org.trianacode.gui.hci.color.ColorTable;
import org.trianacode.gui.hci.color.RegisterableColorModel;
import org.trianacode.gui.hci.color.TrianaColorConstants;
import org.trianacode.taskgraph.tool.Tool;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 17/08/2011
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class ShiwaColorModel implements RegisterableColorModel {
    public static final String SHIWA_Color_Model = "SHIWA Tools";
    public static final Color SHIWA_Config_Color = new Color(0, 180, 0);

    @Override
    public String[] getRegistrationNames() {
        return new String[]{ShiwaConfigComponentModel.ShiwaConfigRenderingHint};
    }

    @Override
    public Color getDefaultColorForRegistrationName(String name) {
        if (ShiwaConfigComponentModel.ShiwaConfigRenderingHint.equals(name)) {
            return SHIWA_Config_Color;
        }
        return null;
    }

    @Override
    public String getModelName() {
        return SHIWA_Color_Model;
    }

    @Override
    public String[] getColorNames() {
        return new String[]{ShiwaConfigComponentModel.ShiwaConfigRenderingHint};
    }

    @Override
    public String[] getElementNames() {
        return new String[]{TrianaColorConstants.TOOL_ELEMENT};
    }

    @Override
    public Color getColor(String element, Tool tool) {
        if (element.equals(TrianaColorConstants.TOOL_ELEMENT)) {
            if (tool.isRenderingHint(ShiwaConfigComponentModel.ShiwaConfigRenderingHint)) {
                return ColorTable.instance().getColor(this, ShiwaConfigComponentModel.ShiwaConfigRenderingHint);
            } else {
                return getColor(element);
            }
        } else {
            return getColor(element);
        }
    }

    @Override
    public Color getColor(String element) {
        if (element.equals(TrianaColorConstants.TOOL_ELEMENT)) {
            return ColorTable.instance().getColor(this, TrianaColorConstants.TOOL_COLOR);
        } else {
            return Color.black;
        }
    }
}
