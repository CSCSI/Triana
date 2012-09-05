package org.trianacode.shiwaall.dax.op;

// TODO: Auto-generated Javadoc
/**
 * take all files into n jobs.
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 17, 2010
 */

public class Gather implements Op {

    /** The job number. */
    private int jobNumber;

    /**
     * Instantiates a new gather.
     */
    public Gather() {
        this(1);
    }

    /**
     * Instantiates a new gather.
     *
     * @param sinkNumber the sink number
     */
    public Gather(int sinkNumber) {
        this.jobNumber = sinkNumber;
    }
}
