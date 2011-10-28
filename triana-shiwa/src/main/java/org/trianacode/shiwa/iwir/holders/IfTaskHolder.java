package org.trianacode.shiwa.iwir.holders;

import org.shiwa.fgi.iwir.IfTask;
import org.trianacode.shiwa.iwir.factory.AbstractTaskHolder;
import org.trianacode.shiwa.iwir.factory.models.IWIRControlComponentModel;
import org.trianacode.taskgraph.Node;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.RenderingHintImp;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class IfTaskHolder extends AbstractTaskHolder {

    Vector<Node> thenNodes = new Vector<Node>();
    Vector<Node> elseNodes = new Vector<Node>();


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

    public void init() {
        super.init();
        defineParameter("count", "0", Unit.USER_ACCESSIBLE);
        setGUIBuilderV2Info("Loops $title count IntScroller 0 100 0");
        getTask().addRenderingHint(
                new RenderingHintImp(
                        IWIRControlComponentModel.IWIR_CONTROL_RENDERING_HINT, false
                )
        );
    }

    public void addIfNode(Node node) {
        thenNodes.add(node);
    }

    public void addElseNode(Node node) {
        elseNodes.add(node);
    }
}
