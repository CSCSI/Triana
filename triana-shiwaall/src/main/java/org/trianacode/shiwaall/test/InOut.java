package org.trianacode.shiwaall.test;

import org.trianacode.taskgraph.Unit;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 23/09/2011
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */

public class InOut extends Unit {

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#process()
     */
    public void process() throws Exception {
        Object input = getInputAtNode(0);
        System.out.println(input);
        output(input);
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#init()
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);
        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        setParameterUpdatePolicy(PROCESS_UPDATE);

        setParameterPanelClass(InOutPanel.class.getCanonicalName());
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#reset()
     */
    public void reset() {
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#dispose()
     */
    public void dispose() {
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#parameterUpdate(java.lang.String, java.lang.Object)
     */
    public void parameterUpdate(String paramname, Object value) {
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#getInputTypes()
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#getOutputTypes()
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }
}

