package org.trianacode.shiwa.iwir.execute;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: 24/06/2011
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
public class Executable implements ExecutableInterface {

    private String taskType;
    public static final String TASKTYPE = "taskType";

    public Executable(String taskType) {
        this.taskType = taskType;
    }

    public void run() {
        System.out.println("TaskType : " + taskType);
        System.out.println("Running with no inputs or outputs");
    }

    public void run(Object[] inputs) {
        System.out.println("TaskType : " + taskType);
        System.out.println("Running with inputs");
        for (int i = 0; i < inputs.length; i++) {
            Object input = inputs[i];
            System.out.println(input);
        }
    }

    public void run(Object[] inputs, Object[] outputs) {
        System.out.println("TaskType : " + taskType);
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

    public String getTaskType() {
        return taskType;
    }
}
