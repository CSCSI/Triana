package org.trianacode.shiwaall.iwir.holders;

import org.shiwa.fgi.iwir.WhileTask;
import org.trianacode.shiwaall.iwir.factory.AbstractTaskHolder;
import org.trianacode.shiwaall.iwir.factory.BasicIWIRPanel;
import org.trianacode.shiwaall.iwir.factory.models.IWIRControlComponentModel;
import org.trianacode.taskgraph.imp.RenderingHintImp;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class WhileTaskHolder extends AbstractTaskHolder {

    /** The readable condition. */
    private String readableCondition = "";

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#process()
     */
    @Override
    public void process() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
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
        setReadableCondition(((WhileTask) getIWIRTask()).getCondition().getConditionAsString());
        setParameterPanelClass(BasicIWIRPanel.class.getCanonicalName());
        setParameterPanelInstantiate(ON_USER_ACCESS);

    }

    /**
     * Condition satisfied.
     *
     * @return true, if successful
     */
    public boolean conditionSatisfied() {
        return ((WhileTask) getIWIRTask()).getCondition().eval();
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
