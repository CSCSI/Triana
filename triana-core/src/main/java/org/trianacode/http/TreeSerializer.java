package org.trianacode.http;

import org.trianacode.taskgraph.tool.ToolboxTree;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 30, 2010
 */
public interface TreeSerializer {

    /**
     * begin the serialzation
     *
     * @return
     */
    public String begin();

    /**
     * end the serialization
     *
     * @return
     */
    public String end();

    /**
     * end a branch of the tree.
     *
     * @param toolbox whether the branch being closed is a toolbox or a package
     * @return
     */
    public String endBranch(boolean toolbox);

    /**
     * end a leaf (tool) node.
     *
     * @return
     */
    public String endLeaf();

    /**
     * start a node. The node allows one to determine wehther it is a toolbox node, a package node or a tool (leaf)
     *
     * @param node
     * @return
     */
    public String startNode(ToolboxTree.TreeNode node);

    /**
     * begin the root of the tree. This is a place holder node as there may be many toolboxes
     *
     * @param node
     * @return
     */
    public String startRoot(ToolboxTree.TreeNode node);

    /**
     * end the root component.
     *
     * @return
     */
    public String endRoot();


}
