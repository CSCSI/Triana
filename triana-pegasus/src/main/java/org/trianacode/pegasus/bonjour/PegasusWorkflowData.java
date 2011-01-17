package org.trianacode.pegasus.bonjour;

import java.io.Serializable;
import java.util.Properties;

/**
 * Dataset containing the five files needed for pegasus represented as Strings.
 *
 * User: scmijt
 * Date: Jul 28, 2010
 * Time: 5:25:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class PegasusWorkflowData implements Serializable {

    private static long serialVersionUID = -1;
    
    private String dax, replicaCatalog, transformationCatalog, siteFile;
    Properties properties;


    public PegasusWorkflowData(String dax, String replicaCatalog, String transformationCatalog, String siteFile, Properties properties) {
        this.dax = dax;
        this.replicaCatalog = replicaCatalog;
        this.transformationCatalog = transformationCatalog;
        this.siteFile = siteFile;
        this.properties = properties;
    }

    public String getDax() {
        return dax;
    }

    public String getReplicaCatalog() {
        return replicaCatalog;
    }

    public String getTransformationCatalog() {
        return transformationCatalog;
    }

    public String getSiteFile() {
        return siteFile;
    }

    public Properties getProperties() {
        return properties;
    }

    public String toString() {
        return dax + ", " +
                replicaCatalog + ", " +
                transformationCatalog + ", " +
                properties + ", " +
                siteFile;
    }
}
