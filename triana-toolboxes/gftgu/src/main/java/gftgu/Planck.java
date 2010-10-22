package gftgu;

/*
This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/1.0/ 
or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA. 
*/

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.Const;


/**
 * Investigate the Planck black-body function
 *
 * @author B F Schutz
 * @version $Revision: 1.4 $
 * @created 23 May 2003
 * @date $Date: 2003/11/19 20:22:19 $ modified by $Author: schutz $
 * @todo
 */


public class Planck extends Unit {

    // parameter data type definitions

    /*
      nSteps is the number of divisions that the domain x of the Planck
      function will be divided into. It is chosen by the user in the
      user interface window.
    */
    private int nSteps;

    /*
      outputType determines what data will be output. The user chooses
      it in the user interface window in order to select between
      the location of the maximum of the function (for Wien's law) or
      the area under the Planck curve (for the Stefan-Boltzmann law).
    */
    private String outputType;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        /*
          Variables needed for the calculation:
          - x is the independent variable in the Planck function.
          - y is the value of the Planck function y=x^5/(e^x-1)
          - xMin is the beginning of the range of x.
          - XMax is the end of the range of x.
          - xLow is the value of x, below which we use the small-x
            approximation for the Planck function, as in the text.
          - xHigh is the value of x, above which we use the large-x
            approximation for the Planck function, as in the text.
          - dx is the step-size in x.
          - area will accumulate the area under the curve.
          - xPeak is the value of x where the Planck function reaches
            its maximum. It is set to zero at first and changed in
            the loop until the correct value is reached.
          - yPeak is the maximum value of the Planck function. It is
            set to zero at first and changed in the loop until the
            correct value is reached.
          - yLast is a variable that will hold the value of y at the
            lower of the two values of x in the intervals used for
            finding the area under the curve.
        */
        double x, y;
        double xMin = 0.01;
        double xMax = 100.0;
        double xLow = 0.05;
        double xHigh = 20.0;
        double dx = (xMax - xMin) / nSteps;
        double area = 0.0;
        double xPeak = 0.0;
        double yPeak = 0.0;
        double yLast = 0;


        /*
          Now we enter the for loop that runs across the domain x and
          searches for the maximum of the Planck function and calculates
          the area under the curve.
        */
        for (x = xMin; x <= xMax; x += dx) {
            /*
              Compute the value of the Planck function, using the
              approximations for small or large x as appropriate.
            */
            if (x < xLow) y = Math.pow(x, 4);
            else if (x > xHigh) y = Math.exp(5.0 * Math.log(x) - x);
            else y = Math.pow(x, 5) / (Math.exp(x) - 1);
            /*
              Find the maximum by testing each new value of y to
              see if is larger than the one already stored in
              yPeak. If it is larger, then yPeak is set equal to
              the new value and xPeak is set equal to x. In this
              way when the loop is finished xPeak contains the
              location of the maximum.
            */
            if (y > yPeak) {
                yPeak = y;
                xPeak = x;
            }
            /*
              To find the area, add in the area under the curve
              (as computed by the approximation described in the
              text) between the present value of x and the previous
              one. The value of y at the previous x has been stored
              in yLast. Note that the area required is under the
              curve x^3/(e^x-1), so we must divide by x^2.
            */
            y /= (x * x);
            area += 0.5 * (y + yLast) * dx;
            /*
              Now make sure yLast contains the current value of y,
              which will be the previous value at the next step.
            */
            yLast = y;
        }

        if (outputType.equals("area")) output(new Const(area));
        else if (outputType.equals("maximum")) output(new Const(xPeak));


    }


    /**
     * Called when the unit is created. Initialises the unit's properties and
     * parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(0);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(1);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Investigate the Planck black-body function");
        setHelpFileLocation("Planck.html");

        // Define initial value and type of parameters
        defineParameter("nSteps", "1000", USER_ACCESSIBLE);
        defineParameter("outputType", "maximum", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Give the number of steps in x to take: $title nSteps TextField 1000\n";
        guilines += "outputType $title outputType Choice [maximum] [area]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        nSteps = new Integer((String) getParameter("nSteps")).intValue();
        outputType = (String) getParameter("outputType");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Planck (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("nSteps"))
            nSteps = new Integer((String) value).intValue();

        if (paramname.equals("outputType"))
            outputType = (String) value;
    }


    /**
     * @return an array of the input types for Planck
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for Planck
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Const"};
    }

}
