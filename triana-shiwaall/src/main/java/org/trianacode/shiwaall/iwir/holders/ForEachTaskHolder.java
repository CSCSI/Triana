package org.trianacode.shiwaall.iwir.holders;

import org.trianacode.shiwaall.iwir.factory.models.IWIRControlComponentModel;
import org.trianacode.shiwaall.iwir.logic.AbstractLoop;
import org.trianacode.shiwaall.iwir.logic.Condition;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.RenderingHintImp;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class ForEachTaskHolder extends AbstractLoop {
    private CountCondition condition = new CountCondition(0);

    @Override
    protected Condition getCondition() {
        return condition;
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

    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("count")) {
            if (value instanceof String) {
                condition.setCount(Integer.parseInt((String) value));
            } else if (value instanceof Number) {
                condition.setCount(((Number) value).intValue());
            }
        }

    }

    private static class CountCondition implements Condition {

        private int total = 0;
        private int count = 0;

        private CountCondition(int count) {
            this.count = count;
        }

        public void setCount(int count) {
            this.count = count;
        }


        @Override
        public Object[] iterate(int current, Object[] data) {
            if (total >= count) {
                return null;
            }
            this.total++;
            return data;
        }
    }
}
