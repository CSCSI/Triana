package common.input;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.AsciiComm;
import triana.types.TrianaType;
import triana.types.util.Str;

/**
 * A TypeImport unit to import a TrianaType data type stored as Ascii output in a file.
 *
 * @author Ian Taylor
 * @author B F Schutz
 * @version 1.1 20 August 2000
 */
public class TypeImport extends Unit {

    int offset = 0;
    String file = "";

    /**
     * The UnitPanel for TypeImport
     */
    TypeImportPanel myPanel;

    /**
     * ********************************************* ** USER CODE of TypeImport goes here    ***
     * *********************************************
     */
    public void process() {
        AsciiComm data;

        data = (AsciiComm) myPanel.getNextPacket(true);

        if (data == null) { // The user does not want to continue from start
            //stop();  // stops the scheduler and hence this process!
            return;
        }

        //setOutputType(data.getClass());

        myPanel.numberLoaded.setText(String.valueOf(Str.strToInt(myPanel.numberLoaded.getText()) + 1));

        output((TrianaType) data);
    }


    /**
     * Initialses information specific to TypeImport.
     */
    public void init() {
        super.init();

        setDefaultInputNodes(1);
        setMinimumInputNodes(1);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        myPanel = new TypeImportPanel();
        myPanel.setObject(this, TypeImportPanel.ASCII);

//        try {
//            setOutputType(Class.forName("triana.types.SampleSet"));
//        }
//        catch (ClassNotFoundException cn) {
//        }
    }

    /**
     * Reset's TypeImport
     */
    public void reset() {
        super.reset();
        myPanel.openFile();
    }

//    public void saveParameters() {
//        saveParameter("offset", offset);
//        saveParameter("file",
//                FileUtils.convertToVirtualName(file));
//    }

    /**
     * Loads DeSerialize's parameters of from the parameter file.
     */
    public void setParameter(String name, String value) {
        if (name.equals("offset")) {
            offset = Str.strToInt(value);
        }

        if (name.equals("file")) {
            file = FileUtils.convertFromVirtualName(value);
        }
    }

    public void updateWidgetFor(String name) {
        if (name.equals("offset")) {
            myPanel.offset.setText(String.valueOf(offset));
        }

        if (name.equals("file")) {
            myPanel.fileName.setText(file);
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to TypeImport, each separated by a white
     *         space.
     */
    public String[] getInputTypes() {
        return new String[]{"triana.types.AsciiComm"};
    }

    /**
     * @return a string containing the names of the types output from Compare, each separated by a white space.
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.TrianaType"};
    }

    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Loads any Triana Type perviously saved by TypeExport";
    }

    /**
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "Input.html";
    }


    /**
     * @return TypeImport's parameter window sp that Triana can move and display it.
     */
    public UnitPanel getParameterPanel() {
        return myPanel;
    }


//    public void cleanUp() {
//        myPanel.closeFile();
//        super.cleanUp();
//    }
}

















