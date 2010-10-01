package signalproc.algorithms;

import java.awt.event.ActionEvent;

import org.trianacode.taskgraph.Unit;
import triana.types.Histogram;
import triana.types.VectorType;


/**
 * A FreqHist unit to produce a Histogram from an input VectorType.  The Histogram records the number of input data
 * elements that lie between a series of values.  The unit takes values of the number of histogram bins, the width of
 * each bin, and the starting value for the lowest bin from the parameter window.  If any of these are set to "auto"
 * then defaults are calculated from the properties of the input data. The number of histogram values is always larger
 * than the number specified by the user, since an over-spill bin extending to postiive infinity and (if there are any
 * negative data values) another over-spill bin extending to negative infinity are added to catch outliers.  Even if a
 * given data set has no such outliers, the extra bins are added for the sake of uniformity; this ensures that
 * Histograms produced by this unit with a given set of user-specified parameters will be compatible with one another,
 * so that they can be added, multiplied, etc.
 * <p/>
 * Revision 1.1: replaced custom window with GUI builder
 *
 * @author B F Schutz
 * @version 1.1 16 Dec 2002
 */
public class FreqHist extends Unit {
    /**
     * The UnitWindow for FreqHist
     */
    //    TextFieldWindow myWindow;
    String numberOfBins = "auto";
    String binWidth = "auto";
    String lowestBinStartsAt = "auto";


    /**
     * ********************************************* ** USER CODE of FreqHist goes here    ***
     * *********************************************
     */
    public void process() {
        VectorType input;
        int nbins = 1;
        int j;
        int datasize = 1;
        double[] values = {0};
        double binwidth = 1.0;
        double startbin = 0.0;
        double max = 0.0;
        double min = 0.0;
        double x;
        input = (VectorType) getInputAtNode(0);
        datasize = input.size();
        values = input.getDataReal();
        max = values[0];
        min = max;
        for (int i = 1; i < datasize; i++) {
            if (values[i] < min) {
                min = values[i];
            } else if (values[i] > max) {
                max = values[i];
            }
        }
        if (lowestBinStartsAt.equals("auto")) {
            startbin = (min < 0) ? Math.floor((max > -min) ? -max : min) : 0.0;
        } else {
            startbin = (Double.valueOf(lowestBinStartsAt)).doubleValue();
        }
        if ((numberOfBins.equals("auto")) && (binWidth.equals("auto"))) {
            nbins = (datasize <= 50) ? 6 : 6 + (int) Math.round(6. * Math.log(datasize / 50.) / Math.log(2.));
            binwidth = (min < 0) ? -startbin / nbins * 2.0 : Math.ceil(max) / nbins;
        } else if (numberOfBins.equals("auto")) {
            binwidth = (Double.valueOf(binWidth)).doubleValue();
            nbins = (int) Math.ceil((max - startbin) / binwidth);
            if (nbins > datasize / 3.) {
                nbins = (datasize <= 50) ? 6 : 6 + (int) Math.round(6. * Math.log(datasize / 50.) / Math.log(2.));
            }
        } else if (binWidth.equals("auto")) {
            nbins = (Integer.valueOf(numberOfBins)).intValue();
            binwidth = (max - startbin) / nbins;
        } else {
            binwidth = (Double.valueOf(binWidth)).doubleValue();
            nbins = (Integer.valueOf(numberOfBins)).intValue();
        }
        int nSeparators = nbins + 1;
        int nValues = (min < 0) ? nSeparators + 1 : nSeparators;
        double[] delimiters = new double[nSeparators];
        double[] data = new double[nValues];
        double nextSep = startbin;
        for (int i = 0; i < nSeparators; i++) {
            delimiters[i] = nextSep;
            nextSep += binwidth;
        }
        if (nSeparators == nValues - 1) {
            for (int i = 0; i < datasize; i++) {
                x = values[i];
                j = 0;
                while ((j < nSeparators) && (x > delimiters[j])) {
                    j++;
                }
                data[j] = data[j] + 1.0;
            }
        } else {
            for (int i = 0; i < datasize; i++) {
                x = values[i];
                j = 1;
                while ((j < nSeparators) && (x > delimiters[j])) {
                    j++;
                }
                data[j - 1] = data[j - 1] + 1.0;
            }
        }
        Histogram histout = new Histogram("Amplitude", "Number", delimiters, data);
        output(histout);
    }


    /**
     * This returns a <b>brief!</b> description of what the unit does. The text here is shown in a pop up window when
     * the user puts the mouse over the unit icon for more than a second.
     */
    public String getPopUpDescription() {
        return "Produces a Histogram from an input VectorType";
    }


    /**
     * Initialses information specific to FreqHist.
     */
    public void init() {
        super.init();

//        setUseGUIBuilder(true);

        setMinimumInputNodes(1);
        setMaximumInputNodes(1);
        setDefaultInputNodes(1);
        setMinimumOutputNodes(1);
        setDefaultOutputNodes(1);

        String guilines = "";
        guilines += "Give number of bins in histogram $title numberOfBins TextField auto\n";
        guilines += "Give width of each bin $title binWidth TextField auto\n";
        guilines += "Give starting value of lowest bin $title lowestBinStartsAt TextField auto\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * @return the GUI information for this unit. It uses the addGUILine function to add lines to the GUI interface.
     *         Such lines must in the specified GUI text format.
     */
//    public void setGUIInformation() {
//        addGUILine("Give number of bins in histogram $title numberOfBins TextField auto");
//        addGUILine("Give width of each bin $title binWidth TextField auto");
//        addGUILine("Give starting value of lowest bin $title lowestBinStartsAt TextField auto");
//    }


    /**
     * Reset's FreqHist
     */
    public void reset() {
        super.reset();
    }

    /**
     * Saves ReplaceAll's parameters to the parameter file.
     */
//    public void saveParameters() {
//        saveParameter("numberOfBins", numberOfBins);
//        saveParameter("binWidth", binWidth);
//        saveParameter("lowestBinStartsAt", lowestBinStartsAt);
//    }

    /**
     * Used to set each of ReplaceAll's parameters.
     */
    public void parameterUpdate(String name, Object value) {
        if (name.equals("numberOfBins")) {
            numberOfBins = (String) value;
        }
        if (name.equals("binWidth")) {
            binWidth = (String) value;
        }
        if (name.equals("lowestBinStartsAt")) {
            lowestBinStartsAt = (String) value;
        }
    }

    /**
     * @return a string containing the names of the types allowed to be input to FreqHist, each separated by a white
     *         space.
     */
//    public String inputTypes() {
//        return "VectorType";
//    }
//
//    /**
//     * @return a string containing the names of the types output from FreqHist, each separated by a white space.
//     */
//    public String outputTypes() {
//        return "Histogram";
//    }

    public String[] getInputTypes() {
        return new String[]{"triana.types.VectorType"};
    }

    public String[] getOutputTypes() {
        return new String[]{"triana.types.Histogram"};
    }        

    /**
     *
     * @returns the location of the help file for this unit.  
     */
    public String getHelpFile() {
        return "FreqHist.html";
    }


    /*
     * @return FreqHist's parameter window so that Triana 
     * can move and display it.
     *
    public JFrame myParameterWindow() {
        return myWindow;
        }
    */

    /**
     * Captures the events thrown out by TextFieldWindow.
     */
//    public void actionPerformed(ActionEvent e) {
//        super.actionPerformed(e);   // we need this
//    }

}

















