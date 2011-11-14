package org.trianacode.enactment.addon;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 07/11/2011
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public interface BundleAddon extends CLIaddon {

    public File getWorkflowFile(String bundlePath) throws IOException;

    public File getConfigFile(String bundlePath);

    public Object getWorkflowObject(String bundlePath);

}
