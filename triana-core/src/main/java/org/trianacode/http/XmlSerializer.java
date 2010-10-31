package org.trianacode.http;

import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolboxTree;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 30, 2010
 */
public class XmlSerializer implements TreeSerializer {

    @Override
    public String begin() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    }

    @Override
    public String end() {
        return "";
    }

    @Override
    public String endBranch(boolean toolbox) {
        if (toolbox) {
            return "</toolbox>";
        }
        return "</package>";
    }

    @Override
    public String endLeaf() {
        return "";
    }

    @Override
    public String startNode(ToolboxTree.TreeNode node) {

        boolean leaf = node.isLeaf();
        if (!leaf) {
            if (node.isToolbox()) {
                return "<toolbox name=\"" + node.getName() + "\">";
            }
            return "<package name=\"" + node.getName() + "\">";
        } else {
            Tool tool = node.getTool();
            if (tool == null) {
                return "<tool name=\"" + node.getName() + "\"/>";
            } else {
                return "<tool name=\"" + node.getName() + "\" href=\"" + PathController.getInstance().getToolPath(tool) + "\"/>";
            }
        }
    }

    @Override
    public String startRoot(ToolboxTree.TreeNode node) {
        return "<toolboxes>";
    }

    @Override
    public String endRoot() {
        return "</toolboxes>";
    }
}
