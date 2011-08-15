package org.trianacode.taskgraph.service;

import org.trianacode.taskgraph.Unit;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 08/08/2011
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
public class VariableDummyUnit extends Unit {

    @Override
    public String[] getInputTypes() {
        return new String[0];
    }

    @Override
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

    @Override
    public void process() throws Exception {
        Object configSizeObject = getParameter("configSize");
        if (configSizeObject != null) {
            int configSize = (Integer) configSizeObject;
            for (int i = 0; i < configSize; i++) {
                Object val = getParameter("var" + i);
                System.out.println("Output : " + val + " node : " + i);
                outputAtNode(i, val);
            }
        }
        output(getParameter("variable"));
    }
}

