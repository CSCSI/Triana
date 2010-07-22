package org.trianacode.http;

import org.thinginitself.streamable.Streamable;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public interface Renderer {

    public static final String TOOL_DESCRIPTION_TEMPLATE = "tool.description.template";
    public static final String TOOL_PARAMETER_TEMPLATE = "tool.parameter.template";
    public static final String TOOL_COMPLETED_TEMPLATE = "tool.completed.template";

    public static final String TOOLBOX_TEMPLATE = "toolbox.template";


    public Streamable render();
}
