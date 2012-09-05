package org.trianacode.shiwaall.iwir.holders;

import org.shiwa.fgi.iwir.IfTask;
import org.trianacode.shiwaall.iwir.factory.AbstractTaskHolder;
import org.trianacode.shiwaall.iwir.factory.BasicIWIRPanel;
import org.trianacode.shiwaall.iwir.factory.models.IWIRControlComponentModel;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.imp.RenderingHintImp;

import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class IfTaskHolder extends AbstractTaskHolder {

    /** The then nodes. */
    Vector<Node> thenNodes = new Vector<Node>();
    
    /** The else nodes. */
    Vector<Node> elseNodes = new Vector<Node>();
    
    /** The readable condition. */
    private String readableCondition = "";


    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#process()
     */
    @Override
    public void process() throws Exception {
        Object data = getInputAtNode(0);

        IfTask ifTask = (IfTask) getIWIRTask();
        boolean then = ifTask.getCondition().eval();

        if (then) {
            for (Node node : thenNodes) {
                outputAtNode(node.getAbsoluteNodeIndex(), data);
            }
        } else {
            for (Node node : elseNodes) {
                outputAtNode(node.getAbsoluteNodeIndex(), data);
            }

        }
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.factory.AbstractTaskHolder#init()
     */
    public void init() {
        super.init();
//        defineParameter("count", "0", Unit.USER_ACCESSIBLE);
//        setGUIBuilderV2Info("Loops $title count IntScroller 0 100 0");
        getTask().addRenderingHint(
                new RenderingHintImp(
                        IWIRControlComponentModel.IWIR_CONTROL_RENDERING_HINT, false
                )
        );
        setParameterPanelClass(BasicIWIRPanel.class.getCanonicalName());
        setParameterPanelInstantiate(ON_USER_ACCESS);
    }

    /**
     * Adds the if node.
     *
     * @param node the node
     */
    public void addIfNode(Node node) {
        thenNodes.add(node);
    }

    /**
     * Adds the else node.
     *
     * @param node the node
     */
    public void addElseNode(Node node) {
        elseNodes.add(node);
    }

    /**
     * Condition satisfied.
     *
     * @return true, if successful
     */
    public boolean conditionSatisfied() {
        return ((IfTask) getIWIRTask()).getCondition().eval();
    }

    /**
     * Sets the readable condition.
     *
     * @param readableCondition the new readable condition
     */
    public void setReadableCondition(String readableCondition) {
        setParameter(BasicIWIRPanel.CONDITION, readableCondition);
    }
}
