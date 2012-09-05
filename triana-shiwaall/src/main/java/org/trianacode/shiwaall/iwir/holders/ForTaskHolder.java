package org.trianacode.shiwaall.iwir.holders;

import org.trianacode.shiwaall.iwir.factory.models.IWIRControlComponentModel;
import org.trianacode.shiwaall.iwir.logic.AbstractLoop;
import org.trianacode.shiwaall.iwir.logic.Condition;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.imp.RenderingHintImp;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class ForTaskHolder extends AbstractLoop {

    /** The condition. */
    private CountCondition condition = new CountCondition(0);

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.logic.AbstractLoop#getCondition()
     */
    @Override
    protected Condition getCondition() {
        return condition;
    }

    /* (non-Javadoc)
     * @see org.trianacode.shiwaall.iwir.logic.AbstractLoop#init()
     */
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

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#parameterUpdate(java.lang.String, java.lang.Object)
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("count")) {
            if (value instanceof String) {
                condition.setCount(Integer.parseInt((String) value));
            } else if (value instanceof Number) {
                condition.setCount(((Number) value).intValue());
            }
        }

    }

    /**
     * The Class CountCondition.
     */
    private static class CountCondition implements Condition {

        /** The total. */
        private int total = 0;
        
        /** The count. */
        private int count = 0;

        /**
         * Instantiates a new count condition.
         *
         * @param count the count
         */
        private CountCondition(int count) {
            this.count = count;
        }

        /**
         * Sets the count.
         *
         * @param count the new count
         */
        public void setCount(int count) {
            this.count = count;
        }


        /* (non-Javadoc)
         * @see org.trianacode.shiwaall.iwir.logic.Condition#iterate(int, java.lang.Object[])
         */
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
