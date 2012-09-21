package org.trianacode.shiwaall.iwir.holders;

import org.trianacode.shiwaall.iwir.execute.Executable;
import org.trianacode.shiwaall.iwir.factory.AbstractTaskHolder;
import org.trianacode.shiwaall.iwir.factory.BasicIWIRPanel;
import org.trianacode.taskgraph.Node;

import java.io.File;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class AtomicTaskHolder extends AbstractTaskHolder{

    /** The executable. */
    private Executable executable;

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.factory.AbstractTaskHolder#init()
     */
    @Override
    public void init() {
        if(this.isParameter(Executable.EXECUTABLE)){
            executable = (Executable) getParameter(Executable.EXECUTABLE);
        } else {
            executable = new Executable("");
            setParameter(Executable.EXECUTABLE, executable);
        }

        setParameterPanelClass(BasicIWIRPanel.class.getCanonicalName());
        setParameterPanelInstantiate(ON_TASK_INSTANTATION);
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#process()
     */
    @Override
    public void process() throws Exception {
        System.out.println("");
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

            for(int j = 0; j < getOutputNodeCount(); j++) {
                Node node = getTask().getOutputNode(j);

                File outputFile = getExecutable().getOutputFileForNode(node);
                if(outputFile != null && outputFile.exists()){
                    System.out.println(node.getName() + " sends data to file " + outputFile.getAbsolutePath());
                    outputAtNode(j, outputFile.getAbsolutePath());
                } else {
                    System.out.println("Output file for node " + node.getName() + " does not exist");
                }
            }

        } else {
            getExecutable().run();
        }
    }

    /**
     * Gets the executable.
     *
     * @return the executable
     */
    public Executable getExecutable() {
        return executable;
    }

//    public String getTaskType(){
//        AbstractTask iwirTask = getIWIRTask();
//        if(iwirTask instanceof Task){
//            return ((Task) iwirTask).getTasktype();
//        }
//        return null;
//    }
}
