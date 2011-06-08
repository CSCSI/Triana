package org.trianacode.pegasus.gui.models;

import org.trianacode.gui.hci.color.ColorTable;
import org.trianacode.gui.hci.color.RegisterableColorModel;
import org.trianacode.gui.hci.color.TrianaColorConstants;
import org.trianacode.taskgraph.tool.Tool;

import java.awt.*;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 14, 2010
 */
public class DaxColorModel implements RegisterableColorModel {

    public static final String DAX_COLOR_MODEL = "DAX Tools";
    public static final Color DAX_FILE_COLOR = Color.cyan.darker();
    public static final Color DAX_JOB_COLOR = new Color(255, 102, 102);

    @Override
    public String getModelName() {
        return DAX_COLOR_MODEL;
    }

    @Override
    public String[] getColorNames() {
        return new String[]{DaxFileComponentModel.DAX_FILE_RENDERING_HINT,
                DaxJobComponentModel.DAX_JOB_RENDERING_HINT};
    }

    @Override
    public String[] getElementNames() {
        return new String[]{TrianaColorConstants.TOOL_ELEMENT};
    }

    @Override
    public Color getColor(String element, Tool tool) {
        if (element.equals(TrianaColorConstants.TOOL_ELEMENT)) {
            if (tool.isRenderingHint(DaxFileComponentModel.DAX_FILE_RENDERING_HINT)) {
                return ColorTable.instance().getColor(this, DaxFileComponentModel.DAX_FILE_RENDERING_HINT);
            } else if (tool.isRenderingHint(DaxJobComponentModel.DAX_JOB_RENDERING_HINT)) {
                return ColorTable.instance().getColor(this, DaxJobComponentModel.DAX_JOB_RENDERING_HINT);
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

    @Override
    public String[] getRegistrationNames() {
        return new String[]{DaxFileComponentModel.DAX_FILE_RENDERING_HINT,
                DaxJobComponentModel.DAX_JOB_RENDERING_HINT};
    }

    @Override
    public Color getDefaultColorForRegistrationName(String name) {
        if (DaxFileComponentModel.DAX_FILE_RENDERING_HINT.equals(name)) {
            return DAX_FILE_COLOR;
        } else if (DaxJobComponentModel.DAX_JOB_RENDERING_HINT.equals(name)) {
            return DAX_JOB_COLOR;
        }
        return null;
    }


}