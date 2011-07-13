package org.trianacode.iwir;

import org.trianacode.taskgraph.Unit;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 13/07/2011
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */

public class EmptyStringCreator extends Unit {

    @Override
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    @Override
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

    public void process() {

        if (this.getInputNodeCount() > 0) {
            Object input = this.getInputAtNode(0);
            System.out.println("Got an input : " + input.toString());
        }
        System.out.println("Pointlessly passing a new String");

        this.output("Rubbish");
    }
}
