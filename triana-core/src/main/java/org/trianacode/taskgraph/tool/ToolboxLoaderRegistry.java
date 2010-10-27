package org.trianacode.taskgraph.tool;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 26, 2010
 */
public class ToolboxLoaderRegistry {

    private ToolboxLoaderRegistry() {
    }

    private static Map<String, ToolboxLoader> loaders = new HashMap<String, ToolboxLoader>();

    public static void registerLoader(ToolboxLoader loader) {
        if (loader.getType() != null) {
            loaders.put(loader.getType(), loader);
        }
    }

    public static Collection<ToolboxLoader> getLoaders() {
        return Collections.unmodifiableCollection(loaders.values());
    }

    public static ToolboxLoader getLoader(String type) {
        return loaders.get(type);
    }
}
