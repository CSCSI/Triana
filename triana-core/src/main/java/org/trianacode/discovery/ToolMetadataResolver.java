package org.trianacode.discovery;

import java.util.List;

import org.trianacode.discovery.toolinfo.ToolMetadata;

/**
 * Resolver extension API. Resolvers will be asked to re-resolve periodically. They should therefore attempt to only
 * return new or changed metadata on subsequent calls to resolve(). Add your resolver by putting a
 * META-INF/services/org.trianacode.discovery.ToolMetadataResolver file in your jar or classpath containing the full
 * name of your resolver.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Aug 11, 2010
 */

public interface ToolMetadataResolver {

    public String getName();

    public List<ToolMetadata> resolve(String toolbox);

}
