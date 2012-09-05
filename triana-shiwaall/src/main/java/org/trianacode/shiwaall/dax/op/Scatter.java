package org.trianacode.shiwaall.dax.op;

// TODO: Auto-generated Javadoc
/**
 * scatter duplicate files across N jobs.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 17, 2010
 */

public class Scatter implements Op {

    /** The job number. */
    private int jobNumber = 1;

    /**
     * Instantiates a new scatter.
     *
     * @param jobNumber the job number
     */
    public Scatter(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    /**
     * Instantiates a new scatter.
     */
    public Scatter() {
        this(1);
    }
}
