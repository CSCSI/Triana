package org.trianacode.http;

import org.thinginitself.streamable.Streamable;
import org.trianacode.config.TrianaProperties;
import org.trianacode.taskgraph.tool.Toolbox;
import org.trianacode.taskgraph.tool.ToolboxTree;
import org.trianacode.velocity.Output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 20, 2010
 */

public class ToolboxesRenderer implements Renderer {

    public static String TOOLBOXES_DESCRIPTION_TEMPLATE = "toolboxes.description.template";


    private List<Toolbox> toolboxes;
    private String path;

    public void init(List<Toolbox> toolboxes, String path) {
        this.toolboxes = toolboxes;
        this.path = path;
        try {
            if (toolboxes.size() > 0) {
                Output.registerDefaults(toolboxes.get(0).getProperties());
                Output.registerTemplate(TOOLBOXES_DESCRIPTION_TEMPLATE, toolboxes.get(0).getProperties().getProperty(TrianaProperties.TOOLBOXES_DESCRIPTION_TEMPLATE_PROPERTY));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] getRenderTypes() {
        return new String[]{TOOLBOXES_DESCRIPTION_TEMPLATE};
    }

    public Streamable render(String type) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("path", path);
        ToolboxTree tree = createToolboxTree(toolboxes);
        String ss = treesToString(tree, new HtmlListSerializer());
        properties.put("toolboxes", ss);
        return Output.output(properties, type);
    }

    protected ToolboxTree createToolboxTree(List<Toolbox> toolboxes) {
        ToolboxTree tree = new ToolboxTree();
        for (int i = 0; i < toolboxes.size(); i++) {
            Toolbox toolbox = toolboxes.get(i);
            tree.addToolbox(toolbox);
        }
        return tree;
    }

    protected String treesToString(ToolboxTree tree, TreeSerializer ser) {
        Iterator<ToolboxTree.TreeNode> it = tree.iterator();
        StringBuilder sb = new StringBuilder();
        int depth = 0;
        ToolboxTree.TreeNode last = null;
        sb.append(ser.begin());
        while (it.hasNext()) {
            ToolboxTree.TreeNode node = it.next();
            int newDepth = node.getDepth();
            int diff = newDepth - depth;
            sb.append(space(newDepth));
            if (diff < 0) {
                if (last != null && last.isLeaf()) {
                    sb.append(ser.endLeaf());
                    sb.append(space(newDepth));
                }
                for (int i = diff; i < 0; i++) {
                    sb.append(ser.endBranch());
                    sb.append(space(newDepth - i));
                }
            } else if (diff == 0) {
                if (last != null) {
                    if (!last.isLeaf()) {
                        sb.append(ser.endBranch());
                    } else {
                        sb.append(ser.endLeaf());
                    }
                }
            }
            sb.append(space(newDepth));
            sb.append(ser.startNode(node));
            depth = newDepth;
            last = node;
        }
        sb.append(ser.endLeaf());
        for (int i = depth; i > 0; i--) {
            sb.append(space(i));
            sb.append(ser.endBranch());
        }
        sb.append(ser.end());
        String ret = sb.toString();
        return ret;
    }

    private String space(int depth) {
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < depth; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }


}
