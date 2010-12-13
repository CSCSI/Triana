package org.trianacode.pegasus.dax;

import org.trianacode.taskgraph.annotation.Tool;
import org.trianacode.taskgraph.annotation.Process;


import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: Nov 30, 2010
 * Time: 2:34:25 PM
 * To change this template use File | Settings | File Templates.
 */

@Tool(panelClass="org.trianacode.pegasus.dax.DaxToPegasusPanel")
public class DaxToPegasusUnit {

    @Process
    public void process(File file){

    }
}
