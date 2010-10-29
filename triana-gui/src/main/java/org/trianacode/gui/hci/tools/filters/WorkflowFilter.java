package org.trianacode.gui.hci.tools.filters;

import org.trianacode.gui.hci.ToolFilter;
import org.trianacode.taskgraph.tool.Tool;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Oct 29, 2010
 * Time: 5:23:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class WorkflowFilter implements ToolFilter {
    @Override
    public String getName() {
        return "Workflows";
    }

    @Override
    public String getRoot() {
        return getName();
    }

    public String toString(){
        return getName();
    }

    @Override
    public String[] getFilteredPackage(Tool tool) {
        if (tool.getDefinitionType().equals(Tool.DEFINITION_TRIANA_XML)) {
            return new String[]{""};
        }
        return null;
    }

    @Override
    public void init() {
    }

    @Override
    public void dispose() {
    }
}
