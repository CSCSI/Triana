package common.output;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.trianacode.taskgraph.Unit;
import triana.types.Document;

/**
 * Unit Interfacing to Condor Utilities
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */


public class Condor extends Unit {

    private String submit_schedd_name;
    private boolean submit_verbose;
    private boolean submit_remote_schedd;
    private boolean submit_disable_permchk;
    private Object[][] submit_additional_commands;
    private String output_submit;

    private boolean queue_refresh;
    private String output_queue;

    private MyThread queue_thread;
    private MyThread status_thread;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {
        Document input = (Document) getInputAtNode(0);

        // Insert main algorithm for Condor
        submit_schedd_name = (String) getParameter("submit_schedd_name");
        submit_verbose = ((Boolean) getParameter("submit_verbose")).booleanValue();
        submit_remote_schedd = ((Boolean) getParameter("submit_remote_schedd")).booleanValue();
        submit_disable_permchk = ((Boolean) getParameter("submit_disable_permchk")).booleanValue();
        submit_additional_commands = (Object[][]) getParameter("submit_additional_commands");

        String cmd_submit = "condor_submit";
        if (submit_verbose) {
            cmd_submit += " -v";
        }
        if (!submit_schedd_name.equals("")) {
            if (!submit_remote_schedd) {
                cmd_submit += " -n " + submit_schedd_name;
            } else {
                cmd_submit += " -r " + submit_schedd_name;
            }
        }
        if (submit_disable_permchk) {
            cmd_submit += " -d";
        }
        if (submit_additional_commands.length > 1) {
            for (int i = 0; i < submit_additional_commands.length - 1; i++) {
                if (!submit_additional_commands[i][0].equals("")
                        && !submit_additional_commands[i][1].equals("")) {
                    cmd_submit += " -a \"" + submit_additional_commands[i][0] + " = "
                            + submit_additional_commands[i][1] + "\"";
                }
            }
        }

        try {
            System.err.println("run cmd: " + cmd_submit);
            Process process_submit = Runtime.getRuntime().exec(cmd_submit);
            PrintWriter feed = new PrintWriter(process_submit.getOutputStream(), true);
            System.err.println("submit starting");
            String tst = input.getText();
            System.err.println(tst);
            feed.println(tst);
            feed.close();
            System.err.println("submit got data");
            BufferedReader sink = new BufferedReader(new InputStreamReader(process_submit.getInputStream()));
            System.err.println("submit getting output");
            String sink_str = "", tmp_str;
            while ((tmp_str = sink.readLine()) != null) {
                sink_str += tmp_str + "\n";
            }
            //sink_str += "dummy output_submit" + Math.random() + "\n";
            setParameter("output_submit", sink_str);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(0);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(0);

        // Initialise parameter update policy
        setParameterUpdatePolicy(Unit.PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Unit Interfacing to Condor Utilities");
        setHelpFileLocation("Condor.html");

        // Initialise custom panel interface
        setParameterPanelClass("common.output.CondorPanel");

        defineParameter("submit_schedd_name", "", Unit.USER_ACCESSIBLE);
        defineParameter("submit_verbose", new Boolean(false), Unit.USER_ACCESSIBLE);
        defineParameter("submit_remote_schedd", new Boolean(false), Unit.USER_ACCESSIBLE);
        defineParameter("submit_disable_permchk", new Boolean(false), Unit.USER_ACCESSIBLE);
        defineParameter("submit_additional_commands", new Object[][]{{"", ""}}, Unit.USER_ACCESSIBLE);
        defineParameter("output_submit", "", Unit.INTERNAL);

        defineParameter("queue_refresh", new Boolean(false), Unit.INTERNAL);
        defineParameter("output_queue", "", Unit.INTERNAL);

        defineParameter("status_refresh", new Boolean(false), Unit.INTERNAL);
        defineParameter("output_status", "", Unit.INTERNAL);

        queue_thread = new MyThread("queue_refresh");
        queue_thread.start();

        status_thread = new MyThread("status_refresh");
        status_thread.start();
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values specified by the parameters.
     */
    public void reset() {
// 	submit_schedd_name = (String) getParameter("submit_schedd_name");
// 	submit_verbose = ((Boolean)getParameter("submit_verbose")).booleanValue();
// 	submit_remote_schedd = ((Boolean)getParameter("submit_remote_schedd")).booleanValue();
// 	submit_disable_permchk = ((Boolean)getParameter("submit_disable_permchk")).booleanValue();
// 	submit_additional_commands = (Object[][])getParameter("submit_additional_commands");
// 	output_submit = (String) getParameter("output_submit");
        setParameter("submit_schedd_name", "");
        setParameter("submit_verbose", new Boolean(false));
        setParameter("submit_remote_schedd", new Boolean(false));
        setParameter("submit_disable_permchk", new Boolean(false));
        setParameter("submit_additional_commands", new Object[][]{{"", ""}});
        setParameter("output_submit", "");

        setParameter("queue_refresh", new Boolean(false));
        setParameter("output_queue", "");
        setParameter("status_refresh", new Boolean(false));
        setParameter("output_status", "");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Condor (e.g. close open files)
        queue_thread.stopPlease();
        status_thread.stopPlease();
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        System.err.println("parameterUpdate (GUI -> task), paramname: " + paramname);
// 	if (paramname.equals("submit_schedd_name")) {
// 	    submit_schedd_name = (String)value;
// 	}
// 	if (paramname.equals("submit_verbose")) {
// 	    submit_verbose = ((Boolean)value).booleanValue();
// 	}
// 	if (paramname.equals("submit_remote_schedd")) {
// 	    submit_remote_schedd = ((Boolean)value).booleanValue();
// 	}
// 	if (paramname.equals("submit_disable_permchk")) {
// 	    submit_disable_permchk = ((Boolean)value).booleanValue();
// 	}
// 	if (paramname.equals("submit_additional_commands")) {
// 	    submit_additional_commands = (Object[][])value;
// 	}
// 	if (paramname.equals("output_submit")) {
// 	    output_submit = (String)value;
// 	}
    }


    /**
     * @return an array of the input types for Condor
     */
    public String[] getInputTypes() {
        return new String[]{"Document"};
    }

    /**
     * @return an array of the output types for Condor
     */
    public String[] getOutputTypes() {
        return new String[]{};
    }

    private final class MyThread extends Thread {
        boolean thread_continue = true;
        String kind;
        String cmd, param;

        MyThread(String kind) {
            this.kind = kind;
            if (kind.equals("queue_refresh")) {
                this.cmd = "condor_q";
                this.param = "output_queue";
            }
            if (kind.equals("status_refresh")) {
                this.cmd = "condor_status";
                this.param = "output_status";
            }
        }

        public void run() {
            while (thread_continue) {
                if (((Boolean) getParameter(kind)).booleanValue()) {
                    setParameter(kind, new Boolean(false)); // reset
                    try {
                        System.err.println("run cmd: " + cmd);
                        Process process = Runtime.getRuntime().exec(cmd);
                        BufferedReader sink = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String sink_str = "", tmp_str;
                        while ((tmp_str = sink.readLine()) != null) {
                            System.err.println("-----> " + tmp_str);
                            sink_str += tmp_str + "\n";
                        }
                        //sink_str += "dummy " + kind + Math.random() + "\n";
                        setParameter(param, sink_str);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                }
                yield();
            }
            System.err.println(kind + " exited");
        }

        public void stopPlease() {
            thread_continue = false;
        }
    }
}



