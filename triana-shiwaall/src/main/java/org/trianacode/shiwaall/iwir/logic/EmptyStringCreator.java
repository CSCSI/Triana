package org.trianacode.shiwaall.iwir.logic;

import org.trianacode.taskgraph.Unit;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 13/07/2011
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */

public class EmptyStringCreator extends Unit {

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#getInputTypes()
     */
    @Override
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#getOutputTypes()
     */
    @Override
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#process()
     */
    public void process() {

        if (this.getInputNodeCount() > 0) {
            Object input = this.getInputAtNode(0);
            System.out.println("Got an input : " + input.toString());
        }

        ArrayList<String> outputs = new ArrayList<String>();
        outputs.add("Text 1");
        outputs.add("Text 2");

        System.out.println("Output a list");

        this.output(outputs);
    }
}
