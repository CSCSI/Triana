package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.TextFieldParameter;
import org.trianacode.taskgraph.annotation.Tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    DaxSettingObject dso = new DaxSettingObject();

    @TextFieldParameter
    private String executable = "";
    @TextFieldParameter
    private String optionsString = "";
    @TextFieldParameter
    private String search_for = "";

    @TextFieldParameter
    private String input = "";


    @Process(gather=true)
    public DaxSettingObject process(List in){

        dso.clear();

        java.lang.Process process;
        BufferedReader errorreader;
        BufferedReader inreader;
        String str;
        boolean errors = false;
        String errLog = "";

        List<String> options = new ArrayList<String>();
        String[] optionsStrings = optionsString.split(" ");
        for(int i = 0; i < optionsStrings.length; i++){
            options.add(optionsStrings[i]);
        }

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
                checkForData(str);
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

        dso.addFullOutput(out.toString());

        return dso;

    }

    private void checkForData(String s){
        if(s.contains(search_for)){
            log("Found : " + search_for);
            String found = s.substring(search_for.length());
            log("Adding : " + found);
            dso.addObject("files", found);
        }else{
            log("String : " + s + " does not contain : " + search_for);
        }
    }

    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }
}

//
//
//    private String processName = "process.name";
//    private String processOptions = "process.options";
//    private String processInput = "process.input";
//
//    public String getExecutable() {
//        return executable;
//    }
//
//    public void setExecutable(String executable) {
//        this.executable = executable;
//    }
//
//    public List<String> getOptions() {
//        return options;
//    }
//
//    public void setOptions(List<String> options) {
//        this.options = options;
//    }
//
//    public String getInput() {
//        return input;
//    }
//
//    public void setInput(String input) {
//        this.input = input;
//    }
//
//    @Override
//    public String[] getInputTypes() {
//        return new String[]{"java.lang.String", "java.util.List", "java.lang.String"};
//    }
//
//    @Override
//    public String[] getOutputTypes() {
//        return new String[]{"java.lang.String"};
//    }
//    public static void main(String[] args) throws Exception {
//        ExecUnit u = new ExecUnit();
//        u.setExecutable("ls");
//        u.setOptions(Arrays.asList("-l", "/Users/scmabh"));
//      //  u.process();
//
//    }