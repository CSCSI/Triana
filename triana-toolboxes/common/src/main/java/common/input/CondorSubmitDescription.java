package common.input;

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

import java.text.DecimalFormat;
import java.util.Random;

import org.trianacode.taskgraph.Unit;
import triana.types.Document;

/**
 * Create a Condor job submit description file
 *
 * @author Rui Zhu
 * @version $Revision: 2921 $
 */

public class CondorSubmitDescription extends Unit {


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {
        // Insert main algorithm for CondorSubmitDescription
        Object[][] desc = (Object[][]) getParameter("CondorSubmitDescription");

        Document doc = new Document();
        doc.setFile(System.getProperty("java.io.tmpdir")
                + System.getProperty("file.separator")
                + "triana-"
                + new DecimalFormat("00000").format(new Random().nextInt(100000))
                + ".condor");
        for (int i = 0; i < desc.length; i++) {
            if (desc[i][0].toString().startsWith("QUEUE")) {
                System.err.println(desc[i][0].toString() + " => `" + desc[i][1].toString() + "'");
                doc.addLine(desc[i][0].toString() + " " + desc[i][1].toString());
            } else {
                if (!desc[i][1].toString().equals("")) {
                    System.err.println(desc[i][0].toString() + " => `" + desc[i][1].toString() + "'");
                    doc.addLine(desc[i][0].toString() + " = " + desc[i][1].toString());
                }
            }
        }

        output(doc);
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(0);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(Unit.PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Create a Condor job submit description file");
        setHelpFileLocation("CondorSubmitDescription.html");

        // Initialise custom panel interface
        setParameterPanelClass("common.input.CondorSubmitDescriptionPanel");

        defineParameter("CondorSubmitDescription",
                new Object[][]{
                        {"universe", ""},
                        {"executable", ""},
                        {"requirements", ""},
                        {"rank", ""},
                        {"input", ""},
                        {"output", ""},
                        {"error", ""},
                        {"log", ""},
                        {"arguments", ""},
                        {"initialdir", ""},
                        {"notification", ""},
                        {"notify_user", ""},
                        {"QUEUE", ""},
                },
                Unit.INTERNAL);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up CondorSubmitDescription (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
    }


    /**
     * @return an array of the input types for CondorSubmitDescription
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for CondorSubmitDescription
     */
    public String[] getOutputTypes() {
        return new String[]{"Document"};
    }

}



