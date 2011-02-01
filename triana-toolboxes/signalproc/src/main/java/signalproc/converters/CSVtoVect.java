package signalproc.converters;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import org.trianacode.taskgraph.Unit;
import triana.types.FileName;
import triana.types.VectorType;

/**
 *
 *
 * @author     Eddie Al-Shakarchi
 * @created    31 Jan 2011
 * @version    $Revision: 2915 $
 * @date       $Date: 2006-07-25 14:39:49 +0000 (Tue, 25 Jul 2006) $ modified by $Author: spxmss $
 * @todo
 */
public class CSVtoVect extends Unit {

    // parameter data type definitions
    private String fileName;
    private FileInputStream inf = null;
    private BufferedReader din;
    String colname;

    Vector<Double> csvValues = new Vector<Double>();

    /*
     * Called whenever there is data for the unit to process
     */
    public void process() throws Exception {

        FileName input = null;
        int nodeCount = getInputNodeCount();
        //FileName input= (FileName) getInputAtNode(0);

        FileName[] inputFileNames = new FileName[nodeCount];

        if (nodeCount == 0){
                openInputFile(fileName);
        }
        // If there are one or more nodes, go through each node and assign filename to input array
        if (nodeCount > 0){
            for (int i = 0; i < nodeCount; ++i) {
                inputFileNames[i] = (FileName) getInputAtNode(i);
            }
        }

        if (input!=null)
            fileName = input.getFile();

        int inputNumber;

        if (nodeCount == 0 || nodeCount == 1){
            inputNumber = 1;
        } else {
            inputNumber = nodeCount;
        }

        for (int i = 0; i < inputNumber; ++i) {// For each node
            if (nodeCount > 0){
                openInputFile(inputFileNames[i].getFile());
            }
            VectorType output;

            String colnames[] = getColumnNameAndValues();
            colname = colnames[0];
            System.out.println("colname = " + colname);

            Double[] csvVals = new Double[csvValues.size()];
            double[] csvValsFinal = new double[csvValues.size()];

            csvValues.copyInto(csvVals);

            //Converts from Double[] to double[], which is required by Vectortype
            for (int j = 0; j < csvVals.length; j++){
                csvValsFinal[j] = csvVals[j];
            }
            for (int j = 0; j < csvVals.length; j++){
                System.out.println("csvValsFinal = " + csvValsFinal[j]);
            }

            closeInputFile();

            output = new VectorType(csvValsFinal);
            output.setIndependentLabels(0, colname);
            output.setDependentLabels(0, "Patient Value");
            outputAtNode(i, output);
        }
    }

    /**
     * Opens the file provide. If it opens then it returns the name of the file opened.
     *
     * @param inputFile
     * @return
     * @throws java.io.FileNotFoundException
     */
    public String openInputFile(String inputFile) throws FileNotFoundException {

        System.out.println("inputFile name = " + inputFile);
        inf = new FileInputStream(inputFile);

        din = new BufferedReader(new InputStreamReader(inf));
        return inputFile;
    }

    private void closeInputFile() {
        try{ inf.close();}
        catch(IOException e) {System.out.println("Input file close error!!!"+e);return;}
    }

    public String[] getColumnNameAndValues() throws IOException {
        //System.out.println("getColumnNameAndValues method");

        String inString = null;
        String[] stringArr;

        try {
            inString = din.readLine();
        } //catch(EOFException ee) {System.out.println("End of Input File Reached!!");return;}
        catch(IOException e) {
            System.out.println("File record read error!!");
            return null;
        }

        try {
            stringArr = inString.split(",");
            //for (int j = 0; j < myarray.length; j++){
            while ((din.readLine()) != null) {
                csvValues.addElement((Double.parseDouble(din.readLine())));
            }

        } catch(NullPointerException eee) {
            System.out.println("EOF Found!!!");
            return null;
        }
        return stringArr;
    }


    /**
     * Called when the unit is created. Initialises the unit's properties and
     * parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(1);
        setMinimumInputNodes(0);
        setMaximumInputNodes(Integer.MAX_VALUE);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy and output policy
        setParameterUpdatePolicy(PROCESS_UPDATE);
        setOutputPolicy(CLONE_MULTIPLE_OUTPUT);

        // Initialise pop-up description and help file location
        setPopUpDescription("");
        setHelpFileLocation("CSVtoVect.html");

        // Define initial value and type of parameters
        defineParameter("fileName", "", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Choose CSV File $title fileName File null *.csv\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values
     * specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        fileName = (String) getParameter("fileName");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up CSVtoVect (e.g. close open files)
    }

    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("fileName"))
            fileName = (String) value;
    }


    /**
     * @return an array of the types accepted by each input node. For node indexes
     * not covered the types specified by getInputTypes() are assumed.
     */
    public String [][] getNodeInputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types accepted by nodes not covered
     * by getNodeInputTypes().
     */
    public String [] getInputTypes() {
        return new String[] {"FileName"};
    }


    /**
     * @return an array of the types output by each output node. For node indexes
     * not covered the types specified by getOutputTypes() are assumed.
     */
    public String [][] getNodeOutputTypes() {
        return new String[0][0];
    }

    /**
     * @return an array of the input types output by nodes not covered
     * by getNodeOutputTypes().
     */
    public String [] getOutputTypes() {
        return new String[] {"VectorType"};
    }
}



