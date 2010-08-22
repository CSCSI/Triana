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


import java.awt.event.ActionEvent;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.OldUnit;
import triana.types.TrianaType;
import triana.types.util.Str;

/**
 * A TypeExport unit to export in triana's ASCII format
 *
 * @author Ian Taylor
 * @version 2.0 25 August 2000
 */
public class TypeExport extends OldUnit {

    String fileName = "none";

    /**
     * The UnitPanel for TypeExport
     */
    TypeExportPanel myPanel;

    /**
     * ********************************************* ** USER CODE of TypeExport goes here    ***
     * *********************************************
     */
    public void process() {
        TrianaType data = getInputNode(0);

        if (myPanel.sendNextPacket((Object) data) == -1) {
            stop();
            return;
        }

        myPanel.numberSaved.setText(String.valueOf(Str.strToInt(
                myPanel.numberSaved.getText()) + 1));
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Exports data to a text file with header information";
    }

    /**
     * Initialses information specific to TypeExport.
     */
    public void init() {
        super.init();

        setResizableInputs(false);
        setResizableOutputs(false);

        myPanel = new TypeExportPanel();
        myPanel.setObject(this);
    }

    /**
     * Reset's TypeExport
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves TypeExport's parameters to the parameter file.
     */
    public void saveParameters() {
        saveParameter("file",
                FileUtils.convertToVirtualName(fileName));
    }

    /**
     * Loads TypeExport's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        fileName = FileUtils.convertFromVirtualName(value);
    }

    public void updateWidgetFor(String name) {
        myPanel.fileName.setText(
                FileUtils.convertFromVirtualName(fileName));
        myPanel.openFile();
    }

    /**
     * @return a string containing the names of the types allowed to be input to TypeExport, each separated by a white
     *         space.
     */
    public String inputTypes() {
        return "AsciiComm";
    }

    /**
     * @return a string containing the names of the types output from TypeExport, each separated by a white space.
     */
    public String outputTypes() {
        return "none";
    }

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "Output.html";
    }


    /**
     * @return TypeExport's parameter window sp that Triana can move and display it.
     */
    public UnitPanel getParameterPanel() {
        return myPanel;
    }


    public void cleanUp() {
        myPanel.closeFile();
        super.cleanUp();
    }

    /**
     * Captures the events thrown out by TypeExportPanel.
     */
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);   // we need this
    }
}

















