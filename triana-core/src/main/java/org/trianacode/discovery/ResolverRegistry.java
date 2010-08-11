package org.trianacode.discovery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Jul 24, 2010
 */

public class ResolverRegistry {

    private static Map<String, ToolMetadataResolver> resolvers = new HashMap<String, ToolMetadataResolver>();

    public static void registerResolver(ToolMetadataResolver resolver) {
        resolvers.put(resolver.getName(), resolver);
    }

    public static Collection<ToolMetadataResolver> getResolvers() {
        return Collections.unmodifiableCollection(resolvers.values());
    }

    public static ToolMetadataResolver getResolver(String name) {
        return resolvers.get(name);
    }

}