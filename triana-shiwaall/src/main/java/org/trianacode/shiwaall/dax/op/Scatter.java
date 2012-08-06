package org.trianacode.shiwaall.dax.op;

/**
 * scatter duplicate files across N jobs
 * @author Andrew Harrison
 * @version 1.0.0 Jul 17, 2010
 */

public class Scatter implements Op {

    private int jobNumber = 1;

    public Scatter(int jobNumber) {
        this.jobNumber = jobNumber;
    }

    public Scatter() {
        this(1);
    }
}
