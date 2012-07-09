package org.trianacode.taskgraph.service;

import org.trianacode.taskgraph.Unit;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
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
        } else {
            Object variableObject = getParameter("variable");
            if (variableObject != null) {
                output(variableObject);
            }
        }
    }

    @Override
    public void init() {
        String guiInfo = "";

//        Object configSizeObject = getParameter("configSize");
        if (isParameter("configSize")) {
            guiInfo += "Number of variables $title configSize TextField 1\n";
            int configSize = Integer.parseInt(String.valueOf(getParameter("configSize")));
            for (int i = 0; i < configSize; i++) {
                guiInfo += "Var " + i + " $title var" + i + " TextField 1\n";
            }
        } else {
//            System.out.println("no config size");
            if (isParameter("variable")) {
                guiInfo += "Single variable $title variable TextField 1\n";
            } else {
//                System.out.println("no variable");
            }
        }

        setGUIBuilderV2Info(guiInfo);
    }

}

