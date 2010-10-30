package org.trianacode.http;

import org.trianacode.taskgraph.tool.ToolboxTree;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 30, 2010
 */
public interface TreeSerializer {

    public String begin();

    public String end();

    public String endBranch();

    public String endLeaf();

    public String startNode(ToolboxTree.TreeNode node);

}
