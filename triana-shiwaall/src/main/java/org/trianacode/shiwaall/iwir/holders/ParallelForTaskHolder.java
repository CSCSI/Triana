package org.trianacode.shiwaall.iwir.holders;

import org.trianacode.shiwaall.iwir.factory.AbstractTaskHolder;
import org.trianacode.shiwaall.iwir.factory.models.IWIRControlComponentModel;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.RenderingHintImp;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class ParallelForTaskHolder extends AbstractTaskHolder {

    @Override
    public void process() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
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
}
