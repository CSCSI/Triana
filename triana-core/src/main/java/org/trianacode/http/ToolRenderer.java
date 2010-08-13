package org.trianacode.http;

import org.trianacode.taskgraph.tool.Tool;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public interface ToolRenderer extends Renderer {

    String TOOL_DESCRIPTION_TEMPLATE = "tool.description.template";
    String TOOL_CREATE_INSTANCE_TEMPLATE = "tool.create.instance.template";
    String TOOL_INSTANCE_TEMPLATE = "tool.instance.template";
    String TOOL_PARAMETER_TEMPLATE = "tool.parameter.template";
    String TOOL_COMPLETED_TEMPLATE = "tool.completed.template";

    public void init(Tool tool, String path);


}