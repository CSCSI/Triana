package org.trianacode.enactment;

import org.trianacode.taskgraph.tool.Tool;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 17/08/2011
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */

//Discoverable from Exec, and can step in to run the workflow if extra processing is needed before execution.
//it is up to the ExecutionService to do execEngine.execute(tool, data) if required.
public interface ExecutionService {

    public String getServiceName();

    public String getLongOption();

    public String getShortOption();

    public String getDescription();

    public void execute(Exec execEngine, String workflow, Tool tool, String data, String[] args) throws Exception;
}
