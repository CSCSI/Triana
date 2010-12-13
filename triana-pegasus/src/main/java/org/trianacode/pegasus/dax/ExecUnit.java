package org.trianacode.pegasus.dax;

import org.apache.commons.logging.Log;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.annotation.Parameter;
import org.trianacode.taskgraph.annotation.Process;
import org.trianacode.taskgraph.annotation.TextFieldParameter;
import org.trianacode.taskgraph.annotation.Tool;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
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

@Tool (panelClass = "org.trianacode.pegasus.dax.ExecUnitPanel")
public class ExecUnit{

    public static final String numberOfFiles = "Number of files";
    public static final String namesOfFiles = "Names of files";

    DaxSettingObject dso = new DaxSettingObject();

    @TextFieldParameter
    private String executable = "";
    @TextFieldParameter
    private String input_file = "";
    @TextFieldParameter
    private String executable_args = "";
    @TextFieldParameter
    private String search_for = "";
    @Parameter
    private String save_as = "";

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
        String[] optionsStrings = executable_args.split(" ");
        for(int i = 0; i < optionsStrings.length; i++){
            options.add(optionsStrings[i]);
        }

        StringBuilder out = new StringBuilder();
        List commmandStrVector = new ArrayList();
        if(!executable.equals("")){
            if(!error()){
                commmandStrVector.add(executable);
                if(!input_file.equals("")){
                    commmandStrVector.add(input_file);
                }
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
                    //        log("ExecUnit.process output:" + out.toString());
                } else {
                    log("ExecUnit.process err:" + errLog);
                }

                dso.addFullOutput(out.toString());
            }
        }
        return dso;

    }

    private boolean error(){
        if(input_file.equals("")){
            return false;
        }else{
            if( new File(input_file).exists()){
                return false;
            }else{
                JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(), "Input file (" + input_file +")for ExecUnit does not appear to exist.\n" +
                        ".dax file will be created, but may contain errors.", "Error", JOptionPane.WARNING_MESSAGE);
                return true;
            }
        }
    }

    private void checkForData(String s){
        if(s.contains(search_for)){
            log("Found : " + search_for);
            String found = s.substring(search_for.length());
            log("Adding : " + found);

            if(save_as.equals(ExecUnit.numberOfFiles)){
                dso.addObject(ExecUnit.numberOfFiles, found);
            }
            if(save_as.equals(ExecUnit.namesOfFiles)){
                String foundNames = dso.getFileNames();
                String names = "";
                if(foundNames.equals("")){
                    names = found;
                }
                else{
                    names = foundNames + ", " + found;
                }
                dso.addObject(ExecUnit.namesOfFiles, names);
            }
        }else{
            //         log("String : *" + s + "* does not contain : " + search_for);
        }
    }

    private void log(String s){
        Log log = Loggers.DEV_LOGGER;
        log.debug(s);
        System.out.println(s);
    }
}
