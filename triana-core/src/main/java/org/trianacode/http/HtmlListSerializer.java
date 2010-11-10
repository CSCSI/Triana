package org.trianacode.http;

import org.trianacode.taskgraph.tool.Tool;
import org.trianacode.taskgraph.tool.ToolboxTree;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 30, 2010
 */
public class HtmlListSerializer implements TreeSerializer {

    @Override
    public String begin() {
        return "";
    }

    @Override
    public String end() {
        return "";
    }

    @Override
    public String endBranch(boolean toolbox) {
        return "</ul></li>";
    }

    @Override
    public String endLeaf() {
        return "</li>";
    }

    @Override
    public String startNode(ToolboxTree.TreeNode node) {
        boolean leaf = node.isLeaf();
        if (!leaf) {
            if (node.isToolbox()) {
                return "<li><a href=\"" + PathController.getInstance().getToolboxPath(node.getName()) + "\"><span class=\"folder\">" + node.getName() + "</span></a><ul>";
            } else {
                return "<li><span class=\"folder\">" + node.getName() + "</span><ul>";
            }
        } else {
            Tool tool = node.getTool();
            if (tool == null) {
                return "<li><span class=\"file\">" + node.getName() + "</span>";
            } else {
                return "<li><a href=\"" + PathController.getInstance().getToolPath(tool) + "\"><span class=\"file\">" + node.getName() + "</span></a>";
            }
        }
    }

    @Override
    public String startRoot(ToolboxTree.TreeNode node) {
        return startNode(node);
    }

    @Override
    public String endRoot() {
        return endBranch(false);
    }
}
