package org.trianacode.shiwa.iwir.holders;

import org.trianacode.shiwa.iwir.factory.AbstractTaskHolder;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 09/03/2011
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class AtomicTaskHolder extends AbstractTaskHolder {

    @Override
    public void process() throws Exception {

        if (getInputNodeCount() > 0) {
            Object[] inputs = new Object[getInputNodeCount()];
            for (int i = 0; i < getInputNodeCount(); i++) {
                inputs[i] = getInputAtNode(i);
            }
            Object[] outputs = new Object[getOutputNodeCount()];
            getExecutable().run(inputs, outputs);

            for (int j = 0; j < outputs.length; j++) {
                outputAtNode(j, outputs[j]);
            }

        } else {
            getExecutable().run();
        }
    }

}
