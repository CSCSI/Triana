package org.trianacode.shiwa.iwir.factory.models;

import org.trianacode.gui.hci.color.ColorTable;
import org.trianacode.gui.hci.color.RegisterableColorModel;
import org.trianacode.gui.hci.color.TrianaColorConstants;
import org.trianacode.taskgraph.tool.Tool;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 11/08/2011
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class IWIRColorModel implements RegisterableColorModel {

    public static final String IWIR_COLOR_MODEL = "IWIR Tools";
    public static final Color IWIR_CONTROL_COLOR = new Color(255, 206, 0);

    @Override
    public String[] getRegistrationNames() {
        return new String[]{IWIRControlComponentModel.IWIR_CONTROL_RENDERING_HINT};
    }

    @Override
    public Color getDefaultColorForRegistrationName(String name) {
        if (IWIRControlComponentModel.IWIR_CONTROL_RENDERING_HINT.equals(name)) {
            return IWIR_CONTROL_COLOR;
        }
        return null;
    }

    @Override
    public String getModelName() {
        return IWIR_COLOR_MODEL;
    }

    @Override
    public String[] getColorNames() {
        return new String[]{IWIRControlComponentModel.IWIR_CONTROL_RENDERING_HINT};
    }

    @Override
    public String[] getElementNames() {
        return new String[]{TrianaColorConstants.TOOL_ELEMENT};
    }

    @Override
    public Color getColor(String element, Tool tool) {
        if (element.equals(TrianaColorConstants.TOOL_ELEMENT)) {
            if (tool.isRenderingHint(IWIRControlComponentModel.IWIR_CONTROL_RENDERING_HINT)) {
                return ColorTable.instance().getColor(this, IWIRControlComponentModel.IWIR_CONTROL_RENDERING_HINT);
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
