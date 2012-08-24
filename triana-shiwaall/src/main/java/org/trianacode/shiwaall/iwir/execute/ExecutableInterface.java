package org.trianacode.shiwaall.iwir.execute;

import org.trianacode.taskgraph.Node;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 12/08/2011
 * Time: 10:08
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutableInterface {
    public void run();

    public void run(HashMap<Node, Object> inputObjectAtNodeMap, Object[] outputs);
}
