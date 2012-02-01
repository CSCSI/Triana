package org.trianacode.shiwa.test;

import org.trianacode.taskgraph.Unit;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 23/09/2011
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */

public class InOut extends Unit {

    public void process() throws Exception {
        Object input = getInputAtNode(0);
        System.out.println(input);
        output(input);
    }

    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);
        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        setParameterUpdatePolicy(PROCESS_UPDATE);
    }

    public void reset() {
    }

    public void dispose() {
    }

    public void parameterUpdate(String paramname, Object value) {
    }

    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }
}

