package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.annotation.TextFieldParameter;
import org.trianacode.taskgraph.annotation.Tool;
import org.trianacode.taskgraph.annotation.Process;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * todo - pipe input stream?
 * @author Andrew Harrison
 * @version 1.0.0 Jul 27, 2010
 */

@Tool
public class ExecUnit{

    @TextFieldParameter
    private String executable = "";
    @TextFieldParameter
    private String optionsString = "";

    private List<String> options;
    @TextFieldParameter
    private String input = "";

    private String processName = "process.name";
    private String processOptions = "process.options";
    private String processInput = "process.input";

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

//    @Override
//    public String[] getInputTypes() {
//        return new String[]{"java.lang.String", "java.util.List", "java.lang.String"};
//    }
//
//    @Override
//    public String[] getOutputTypes() {
//        return new String[]{"java.lang.String"};
//    }

    @Process(gather=true)
    public String process(List in){
        java.lang.Process process;
        BufferedReader errorreader;
        BufferedReader inreader;
        String str;
        boolean errors = false;
        String errLog = "";


        StringBuilder out = new StringBuilder();
        List commmandStrVector = new ArrayList();
        commmandStrVector.add(executable);
        commmandStrVector.addAll(options);

        StringBuilder buffer = new StringBuilder();
        for (Iterator iterator = commmandStrVector.iterator(); iterator.hasNext();) {
            buffer.append((String) iterator.next());
            buffer.append(" ");
        }
        log("ExecUnit.process invocation:" + buffer.toString());

        try {
            String[] cmdarray = (String[]) commmandStrVector.toArray(new String[commmandStrVector.size()]);

            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(cmdarray);  // execute command


            errorreader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((str = errorreader.readLine()) != null) {
                errors = true;
                errLog += str + "\n";
            }
            errorreader.close();

            inreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            str = "";
            while ((str = inreader.readLine()) != null) {
                out.append(str).append("\n");
            }
            inreader.close();

        } catch (Exception except) {
            except.printStackTrace();
        }

        if (!errors) {
            log("ExecUnit.process output:" + out.toString());
        } else {
            log("ExecUnit.process err:" + errLog);
        }

        return(out.toString());

    }

    public static void main(String[] args) throws Exception {
        ExecUnit u = new ExecUnit();
        u.setExecutable("ls");
        u.setOptions(Arrays.asList("-l", "/Users/scmabh"));
      //  u.process();

    }

        private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        //System.out.println(s);
    }
}