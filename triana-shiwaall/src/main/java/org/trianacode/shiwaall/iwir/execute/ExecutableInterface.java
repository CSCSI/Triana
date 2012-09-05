package org.trianacode.shiwaall.iwir.execute;

import org.trianacode.taskgraph.Node;

import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 12/08/2011
 * Time: 10:08
 * To change this template use File | Settings | File Templates.
 */
public interface ExecutableInterface {
    
    /**
     * Run.
     */
    public void run();

    /**
     * Run.
     *
     * @param inputObjectAtNodeMap the input object at node map
     * @param outputs the outputs
     */
    public void run(HashMap<Node, Object> inputObjectAtNodeMap, Object[] outputs);
}
