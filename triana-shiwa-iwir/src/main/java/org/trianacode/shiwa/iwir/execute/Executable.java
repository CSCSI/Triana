package org.trianacode.shiwa.iwir.execute;

/**
 * Created by IntelliJ IDEA.
 * User: ian
 * Date: 24/06/2011
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class Executable implements ExecutableInterface {
    public void run() {
        System.out.println("Running with no inputs or outputs");

    }

    public void run(Object[] inputs) {
        System.out.println("Running with inputs");
        for (int i = 0; i < inputs.length; i++) {
            Object input = inputs[i];
            System.out.println(input);
        }
    }

    public void run(Object[] inputs, Object[] outputs) {
        System.out.println("Running with inputs, producing outputs");
        for (int i = 0; i < outputs.length; i++) {
            String output = "";

            for (int j = 0; j < inputs.length; j++) {
                Object input = inputs[j];
                System.out.println(input);
                output += input.toString();
            }

            outputs[i] = output;
        }
    }
}
