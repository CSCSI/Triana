package signalproc.converters;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Scanner;
import java.util.Vector;

import org.trianacode.taskgraph.Unit;
import triana.types.FileName;
import triana.types.VectorType;

/**
 * Unit to convert single (first) column CSV file or fileName (passed from previous unit) to a Triana VectorType.
 * If there are no input nodes the built-in browser is used. If there are input nodes, the built in browser is
 * ignored and either a custom Object array or multiple incoming FileNames (on multiple input nodes can be used as
 * input. If there are multiple FileName input nodes, the outputted VectorType corresponds to the same number output as
 * the input. Simples.
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

    private Vector<Double> csvValues = new Vector<Double>();

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        FileName input = null;
        int nodeCount = getInputNodeCount();
        FileName[] inputFileNames = new FileName[nodeCount];
        Object singleNodeInput = null;
        Object[] inputobjects = null;
        int inputNumber;

        // If there are no input nodes, use built in file browser
        if (nodeCount == 0){
                openInputFile(fileName);
        }

        // If there's just one input, find out if it's an object array or a fileName
        if (nodeCount == 1){

            singleNodeInput = getInputAtNode(0);

            // if the input is a filename, continue as normal
            if (singleNodeInput instanceof FileName) {
                    inputFileNames[0] = (FileName) singleNodeInput;
            }
            else if (singleNodeInput instanceof Object[]){
                inputobjects = (Object[]) singleNodeInput;

                int outputNode=0;
                // Print out the four strings, ignore last Stringbuffer
                for (Object string : inputobjects){
                    if (string instanceof String) { // its a CSV
                        System.out.println("Processing " + string);
                         String[] vals = ((String)string).split("\n");

                         double[] vectVals= new double[vals.length-1];

                         for (int i=1; i<vals.length; ++i ) {
                             vectVals[i-1] = (double)Integer.parseInt(vals[i]);
                         }

                        VectorType v = new VectorType(vectVals);
                        v.setIndependentLabels(0, vals[0] + " (Num)");
                        v.setDependentLabels(0, "Cluster Number");
                        outputAtNode(outputNode, v);
                        ++outputNode;
                        if (outputNode> this.getOutputNodeCount()) return;
   //                         throw new Exception("Too many inputs for output nodes - input count is " + outputNode);
                   }
                }
                return; // once output all inputs, return in this mode.
            }
        }

        // If there are more than one nodes, go through each node and assign filename to input array
        if (nodeCount > 1){
            for (int i = 0; i < nodeCount; ++i)
                inputFileNames[i] = (FileName) getInputAtNode(i);
        }

        if (input!=null)
            fileName = input.getFile();

        if (nodeCount == 0 || nodeCount == 1)
            inputNumber = 1;
        else
            inputNumber = nodeCount;

        // For each node
        for (int i = 0; i < inputNumber; ++i) {

            String colname = null;
            VectorType output;
            String[] colnames = null;

            VectorType[] outputs = null;

            // Essentially making sure that the input is 1 or more FileNames
            if (nodeCount > 0 && !(singleNodeInput instanceof Object[])){
                openInputFile(inputFileNames[i].getFile());
                colname = getColumnNameAndValues();

                System.out.println("colname = " + colname);

                Double[] csvVals = new Double[csvValues.size()];
                double[] csvValsFinal = new double[csvValues.size()];

                csvValues.copyInto(csvVals);

                //Converts from Double[] to double[], which is required by Vectortype
                for (int j = 0; j < csvVals.length; ++j){
                    csvValsFinal[j] = csvVals[j];
                    System.out.println("csvValsFinal = " + csvValsFinal[j]);
                }

                closeInputFile();

                output = new VectorType(csvValsFinal);
                output.setIndependentLabels(0, colname);
                output.setDependentLabels(0, "Patient Value");
                outputAtNode(i, output);
            }

            // Else If we're dealing with array of Objects...
            else if (nodeCount == 1 && (singleNodeInput instanceof Object[])){

                colnames = new String[inputobjects.length];

                // For each object (minus the last one which is a StringBuffer), assign the column name - needed for output
                for (int j = 0; j < (inputobjects.length)-1; ++j){
                    //System.out.println("inputobjects i = " + inputobjects[j]);
                    colnames[j] = getColumnNameAndValuesFromString(inputobjects[j].toString());
                    System.out.println("colnames = " + colnames[j]);
                }

                Double[] csvVals = new Double[csvValues.size()];
                double[] csvValsFinal = new double[csvValues.size()];

                csvValues.copyInto(csvVals);

                //Converts from Double[] to double[], which is required by Vectortype
                for (int j = 0; j < csvVals.length; ++j){
                    csvValsFinal[j] = csvVals[j];
                    //System.out.println("csvValsFinal = " + csvValsFinal[j]);
                }

                // Trying to show here that you need to output each of the '4' strings as a VectorType on a different node
                // so that String 1 is outputted to output node 0, and so on
                for (int j = 0; j < (inputobjects.length)-1; ++j){
                    outputs[j] = new VectorType(csvValsFinal);
                    outputs[j].setIndependentLabels(0, colnames[j]);
                    outputs[j].setDependentLabels(0, "Patient Value");
                    outputAtNode(i, outputs[j]);
                }
            }
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


    public String getColumnNameAndValuesFromString(String current) throws IOException{
        String str = "";
        String columnName = null;

        BufferedReader reader = new BufferedReader(new StringReader(current));
        int index = 0;

        try {
            while ((str = reader.readLine()) != null) {
                if (index == 0){
                    columnName = (str);
                }
                else{
                    csvValues.addElement((Double.parseDouble(str)));
                }
                ++index;
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return columnName;
    }


    public String getColumnNameAndValues() throws IOException {
        //System.out.println("getColumnNameAndValues method");
        String columnName = null;

		Scanner numScan = new Scanner(inf);

		String line;
        int index = 0;

		while (numScan.hasNext()) {
			line = numScan.nextLine();

            if (index == 0){
                    columnName = (line);
                }
            else{
                csvValues.addElement((Double.parseDouble(line)));
            }
            ++index;
			//System.out.println(line);
		}
        System.out.println("index size = " + index);
        return columnName;
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
        return new String[] {"FileName", "java.lang.Object"};
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