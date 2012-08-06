package org.trianacode.shiwaall.dax.op;

/**
 * take all files into n jobs
 * 
 *
 * @author Andrew Harrison
 * @version 1.0.0 Jul 17, 2010
 */

public class Gather implements Op {

    private int jobNumber;

    public Gather() {
        this(1);
    }

    public Gather(int sinkNumber) {
        this.jobNumber = sinkNumber;
    }
}
