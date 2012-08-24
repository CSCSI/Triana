package org.trianacode.shiwaall.iwir.holders;

import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.shiwaall.iwir.factory.AbstractTaskHolder;
import org.trianacode.shiwaall.iwir.factory.BasicIWIRPanel;
import org.trianacode.taskgraph.Node;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class AtomicTaskHolder extends AbstractTaskHolder {

    private Executable executable;

    @Override
    public void init() {

        if(this.isParameter(Executable.EXECUTABLE)){
            executable = (Executable) getParameter(Executable.EXECUTABLE);
        }

        setParameterPanelClass(BasicIWIRPanel.class.getCanonicalName());
        setParameterPanelInstantiate(ON_TASK_INSTANTATION);
    }

    @Override
    public void process() throws Exception {

        if (getInputNodeCount() > 0) {
//            Object[] inputs = new Object[getInputNodeCount()];
            HashMap<Node, Object> inputObjectAtNodeMap = new HashMap<Node, Object>();
            for (int i = 0; i < getInputNodeCount(); i++) {
//                inputs[i] = getInputAtNode(i);
                Node node = getTask().getInputNode(i);
                Object data = getInputAtNode(i);
                inputObjectAtNodeMap.put(node, data);
            }

            for(Node inputNode : inputObjectAtNodeMap.keySet()){
                System.out.println(inputNode.getName() + " data " + inputObjectAtNodeMap.get(inputNode));
            }

            Object[] outputs = new Object[getOutputNodeCount()];
            getExecutable().run(inputObjectAtNodeMap, outputs);

            for (int j = 0; j < outputs.length; j++) {
                outputAtNode(j, outputs[j]);
            }

        } else {
            getExecutable().run();
        }
    }

    public Executable getExecutable() {
        return executable;
    }
}
