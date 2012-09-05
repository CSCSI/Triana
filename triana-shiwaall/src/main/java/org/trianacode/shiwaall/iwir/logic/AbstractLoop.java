package org.trianacode.shiwaall.iwir.logic;

import org.trianacode.shiwaall.iwir.factory.AbstractTaskHolder;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractLoop.
 *
 * @author Andrew Harrison
 * @version 1.0.0 25/06/2011
 */
public abstract class AbstractLoop extends AbstractTaskHolder {

    // the number of wakeups received by this unit
    /** The wakeups. */
    private int wakeups = 0;

    /*
    * Called whenever there is data for the unit to process
    */
    /* (non-Javadoc)
     * @see org.trianacode.taskgraph.Unit#process()
     */
    public void process() throws Exception {
        wakeups++;
        Object[] data = getData();
        Condition condition = getCondition();

        data = condition.iterate(wakeups, data);
        if (data != null) {
            output(data);
        }
    }


    /**
     * Gets the data.
     *
     * @return an array of the input data on this iteration
     */
    private Object[] getData() {
        Object[] data = new Object[getInputNodeCount()];

        for (int count = 0; count < getInputNodeCount(); count++) {
            data[count] = getInputAtNode(count);
        }

        return data;
    }

    /**
     * Gets the condition.
     *
     * @return the condition
     */
    protected abstract Condition getCondition();


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(2);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(2);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setDefaultNodeRequirement(OPTIONAL);

        // Initialise pop-up description and help file location
        setPopUpDescription("A conditional looping unit");
        setHelpFileLocation("Loop.html");
    }

    /**
     * Called when the unit is reset.
     * Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
        wakeups = 0;
    }


    /**
     * Gets the input types.
     *
     * @return an array of the input types for Loop2
     */
    public String[] getInputTypes() {
        return new String[]{"java.lang.Object"};
    }

    /**
     * Gets the output types.
     *
     * @return an array of the output types for Loop2
     */
    public String[] getOutputTypes() {
        return new String[]{"java.lang.Object"};
    }

}
