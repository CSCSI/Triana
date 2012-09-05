package org.trianacode.shiwaall.gui.models;

import org.trianacode.gui.hci.color.ColorTable;
import org.trianacode.gui.hci.color.RegisterableColorModel;
import org.trianacode.gui.hci.color.TrianaColorConstants;
import org.trianacode.taskgraph.tool.Tool;

import java.awt.*;

// TODO: Auto-generated Javadoc
/**
 * The Class DaxColorModel.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Nov 14, 2010
 */
public class DaxColorModel implements RegisterableColorModel {

    /** The Constant DAX_COLOR_MODEL. */
    public static final String DAX_COLOR_MODEL = "DAX Tools";
    
    /** The Constant DAX_FILE_COLOR. */
    public static final Color DAX_FILE_COLOR = Color.cyan.darker();
    
    /** The Constant DAX_JOB_COLOR. */
    public static final Color DAX_JOB_COLOR = new Color(255, 102, 102);

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.color.ColorModel#getModelName()
     */
    @Override
    public String getModelName() {
        return DAX_COLOR_MODEL;
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.color.ColorModel#getColorNames()
     */
    @Override
    public String[] getColorNames() {
        return new String[]{DaxFileComponentModel.DAX_FILE_RENDERING_HINT,
                DaxJobComponentModel.DAX_JOB_RENDERING_HINT};
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.color.ColorModel#getElementNames()
     */
    @Override
    public String[] getElementNames() {
        return new String[]{TrianaColorConstants.TOOL_ELEMENT};
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.color.ColorModel#getColor(java.lang.String, org.trianacode.taskgraph.tool.Tool)
     */
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

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.color.ColorModel#getColor(java.lang.String)
     */
    @Override
    public Color getColor(String element) {
        if (element.equals(TrianaColorConstants.TOOL_ELEMENT)) {
            return ColorTable.instance().getColor(this, TrianaColorConstants.TOOL_COLOR);
        } else {
            return Color.black;
        }
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.color.RegisterableColorModel#getRegistrationNames()
     */
    @Override
    public String[] getRegistrationNames() {
        return new String[]{DaxFileComponentModel.DAX_FILE_RENDERING_HINT,
                DaxJobComponentModel.DAX_JOB_RENDERING_HINT};
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.hci.color.RegisterableColorModel#getDefaultColorForRegistrationName(java.lang.String)
     */
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