package org.trianacode.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 24, 2010
 */

public class RendererRegistry {

    private static Map<String, Class<? extends ToolRenderer>> toolRenderers
            = new HashMap<String, Class<? extends ToolRenderer>>();
    private static Map<String, ToolboxRenderer> toolboxRenderers = new HashMap<String, ToolboxRenderer>();

    public static void registerToolRenderer(ToolRenderer renderer) {
        String[] types = renderer.getRenderTypes();
        for (String type : types) {
            toolRenderers.put(type, renderer.getClass());
        }
    }

    public static ToolRenderer getToolRenderer(String renderType) {
        Class<? extends ToolRenderer> tr = toolRenderers.get(renderType);
        if (tr != null) {
            try {
                return tr.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void registerToolboxRenderer(ToolboxRenderer renderer) {
        String[] types = renderer.getRenderTypes();
        for (String type : types) {
            toolboxRenderers.put(type, renderer);
        }
    }

    public static ToolboxRenderer getToolboxRenderer(String renderType) {
        return toolboxRenderers.get(renderType);
    }
}
