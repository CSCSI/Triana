package org.trianacode.enactment.addon;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 04/11/2011
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public interface CLIaddon {

    public String getServiceName();

    public String getLongOption();

    public String getShortOption();

    public String getDescription();

    public String toString();

    public String getUsageString();
}
