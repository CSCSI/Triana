package common.output;

import java.awt.event.ActionEvent;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.TrianaType;
import triana.types.util.Str;

/**
 * A TypeExport unit to export in triana's ASCII format
 *
 * @author Ian Taylor
 * @version 2.0 25 August 2000
 */
public class TypeExport extends Unit {

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
        TrianaType data = (TrianaType) getInputAtNode(0);

        if (myPanel.sendNextPacket((Object) data) == -1) {
            stop();
            return;
        }

        myPanel.numberSaved.setText(String.valueOf(Str.strToInt(myPanel.numberSaved.getText()) + 1));
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

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        myPanel = new TypeExportPanel();
        myPanel.setObject(this);
    }

    /**
     * Reset's TypeExport
     */
    public void reset() {
        super.reset();
    }

//    /**
//     * Saves TypeExport's parameters to the parameter file.
//     */
//    public void saveParameters() {
//        saveParameter("file", FileUtils.convertToVirtualName(fileName));
//    }

    /**
     * Loads TypeExport's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        fileName = FileUtils.convertFromVirtualName(value);
    }

    public void updateWidgetFor(String name) {
        myPanel.fileName.setText(FileUtils.convertFromVirtualName(fileName));
        myPanel.openFile();
    }

    /**
     * @return a string containing the names of the types allowed to be input to TypeExport, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types."};
    }

    /**
     * @return an array of the output types for this unit
     */
    public String[] getOutputTypes() {
        return new String[]{};
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


//    public void cleanUp() {
//        myPanel.closeFile();
//        super.cleanUp();
//    }

    /**
     * Captures the events thrown out by TypeExportPanel.
     */
    public void actionPerformed(ActionEvent e) {
    //    super.actionPerformed(e);   // we need this
    }
}

















