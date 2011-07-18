package org.trianacode.shiwa.iwir;

import org.trianacode.taskgraph.Unit;

/**
 * @author Andrew Harrison
 * @version 1.0.0 25/06/2011
 */
public class ForLoop extends AbstractLoop {

    private CountCondition condition = new CountCondition(0);

    @Override
    protected Condition getCondition() {
        return condition;
    }

    public void init() {
        super.init();
        defineParameter("count", "0", Unit.USER_ACCESSIBLE);
        setGUIBuilderV2Info("Loops $title count IntScroller 0 100 0");
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
