package org.trianacode.taskgraph.tool;

import java.util.*;

/**
 * Because Java doesn't have one...
 * generic toolbox tree
 *
 * @author Andrew Harrison
 * @version 1.0.0 Oct 29, 2010
 */
public class ToolboxTree {

    private TreeNode root;

    public ToolboxTree() {
        this.root = new TreeNode(null, "Toolboxes", false, 0);
    }

    public void addToolbox(Toolbox box) {
        TreeNode n = new TreeNode(root, box.getName(), true, 1);
        root.addChild(n);
        List<Tool> tools = box.getTools();
        for (Tool tool : tools) {
            addTool(n, tool);
        }
    }

    public void addTool(TreeNode box, Tool tool) {
        if (box == null || !box.isToolbox()) {
            return;
        }
        TreeNode t = box;
        String pkg = tool.getQualifiedToolName();
        String[] segs = pkg.split("\\.");
        for (int i = 0; i < segs.length; i++) {
            String seg = segs[i];
            t = insert(seg, t, i + 2);
        }
        t.setTool(tool);
    }

    private TreeNode insert(String name, TreeNode parent, int depth) {
        TreeNode newNode = new TreeNode(parent, name, depth);
        return parent.addChild(newNode);
    }

    /**
     * returns a depth first iterator
     *
     * @return
     */
    public Iterator<TreeNode> iterator() {
        return new ToolIterator(root);
    }

    public static class TreeNode {

        private TreeNode parent;
        private Map<String, TreeNode> children = new HashMap<String, TreeNode>();
        private String name;
        private int depth = 0;
        private Tool tool;
        private boolean toolbox;

        public TreeNode(TreeNode parent, String name, int depth) {
            this(parent, name, false, depth);
        }

        public TreeNode(TreeNode parent, String name, boolean toolbox, int depth) {
            this.parent = parent;
            this.name = name;
            this.depth = depth;
            this.toolbox = toolbox;
        }

        public TreeNode getParent() {
            return parent;
        }

        public Tool getTool() {
            return tool;
        }

        public void setTool(Tool tool) {
            this.tool = tool;
        }

        public List<TreeNode> getChildren() {
            return new ArrayList<TreeNode>(children.values());
        }

        public TreeNode addChild(TreeNode node) {
            TreeNode existing = children.get(node.getName());
            if (existing != null) {
                List<TreeNode> childs = node.getChildren();
                for (TreeNode child : childs) {
                    existing.addChild(child);
                }
                return existing;
            } else {
                children.put(node.getName(), node);
                return node;
            }
        }

        public String getName() {
            return name;
        }

        public boolean isRoot() {
            return parent == null;
        }

        public boolean isLeaf() {
            return children.size() == 0;
        }

        public boolean isToolbox() {
            return toolbox;
        }

        public int getDepth() {
            return depth;
        }

        public TreeNode getChild(String name) {
            return children.get(name);
        }

        public String toString() {
            return getName() + " children:" + getChildren();
        }
    }

    private static class ToolIterator implements Iterator<TreeNode> {

        private List<TreeNode> list = new ArrayList<TreeNode>();
        private Iterator<TreeNode> it;

        private ToolIterator(TreeNode root) {
            traverse(root, list);
            it = list.iterator();
        }

        private void traverse(TreeNode n, List<TreeNode> list) {
            list.add(n);
            List<TreeNode> children = n.getChildren();
            for (TreeNode child : children) {
                traverse(child, list);
            }
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public TreeNode next() {
            return it.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("removal is not supported by this iterator.");
        }
    }


}
