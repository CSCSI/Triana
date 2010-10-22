package gftgu;

/*
This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/1.0/ 
or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA. 
*/

import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.Curve;


/**
 * Compute a random walk
 *
 * @author B F Schutz
 * @version $Revision: 1.5 $
 * @created 23 May 2003
 * @date $Date: 2003/11/19 20:22:19 $ modified by $Author: schutz $
 * @todo
 */


public class Random extends Unit {

    // parameter data type definitions

    /*
      nTrials is the number of times each random walk is performed. The
      distance achieved by the walk will be averaged over this number of
      walks. This is set by the user in the user interface window.
    */
    private int nTrials;

    /*
      maxSteps is the maximum number of steps in any of the random walks.
      The program will perform walks of this length and shorter ones to
      find the trend in the distance as a function of the number of steps.
      This is set by the user in the user interface window.
    */
    private int maxSteps;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        /*
          Variables that we need each time the code is executed.
          - nWalks is the number of lengths of random walks that we will
            experiment with. The largest length is given by maxSteps.
            Smaller walks are obtained by dividing the number of steps
            of the previous walk by 2, rounding down to an integer
            if necessary. Thus, if maxSteps is 9 then there will be a
            walk of 4 steps and one of 2 as well. (We do not perform
            walks of length 1!). To find nWalks we take the logarithm
            of maxSteps to the base 2 and round it down to the nearest
            integer. The compuation below uses Java's logarithm method,
            which computes the natural logarithm. To get the logarithm
            to the base 2, we divide by the natural logarithm of 2.
          - arrays avgDist and lengths hold the data that will be
            output. For each length of walk, array lengths holds the
            length (number of steps), and array avgDist holds the
            average distance achieved by all nTrials walks of that number
            of steps.
          - j and k are integers needed for loops.
          - nSteps will be used to keep track of the number of steps
            in the current type of walk.
          - x, y, and z will accumulate the distances moved in the three
            directions.
          - dx, dy, and dz will hold the distances moved in a single step.
          - stepSize will be used to find the average step size.
        */
        int nWalks = (int) Math.floor(Math.log(maxSteps) / Math.log(2.));
        double[] avgDist = new double[nWalks];
        double[] lengths = new double[nWalks];
        int j, k, m;
        int nSteps = maxSteps;
        double x, y, z, dx, dy, dz;
        double stepSize;

        /*
          Now start the main loop over the types of walks. Each type
          has a fixed number of steps. Within this loop we will perform
          each type of walk nTrials times and take the average of the
          resulting distances. This loop runs backwards, because we
          start with the maximum number of steps and keep reducing it
          unitl we get to walks with just 2 or 3 steps.
        */
        for (j = nWalks - 1; j >= 0; j--) {
            lengths[j] = nSteps;
            /*
              Here we begin the independent trials for this number of
              steps. Each trial begins at the origin. Use stepSize to
              accumulate the total of the step-lengths and later
              divide it by nSteps to get an average step-length.
            */
            for (k = 0; k < nTrials; k++) {
                x = 0.;
                y = 0.;
                z = 0.;
                stepSize = 0;
                /*
                  We perform the random walk here. We generate step-lengths
                  in each direction using the Java random-number generator,
                  Math.random(), which returns a pseudo-random number
                  uniformly distributed between 0 and 1. We multiply it by 2
                  and subtract 1 to get a number uniformly distributed
                  between -1 and 1.
                */
                for (m = 0; m < nSteps; m++) {
                    dx = 2 * Math.random() - 1;
                    dy = 2 * Math.random() - 1;
                    dz = 2 * Math.random() - 1;
                    stepSize += Math.sqrt(dx * dx + dy * dy + dz * dz);
                    x += dx;
                    y += dy;
                    z += dz;
                }
                /*
                  Compute the scaled distance achieved by the walk by taking
                  the total distance from the origin and dividing by the
                  average step-length, which is stepSize/nSteps. Use the
                  array avgDist[j] to accumulate the sum of these scaled
                  distances.
                */
                avgDist[j] += Math.sqrt(x * x + y * y + z * z) / (stepSize / nSteps);
            }
            /*
              Divide the accumulated total distance by the number of trials
              to get the average distance in units of the step-length
            */
            avgDist[j] /= nTrials;
            nSteps /= 2; // integer division leaves no remainder
            if (nSteps <= 1) break;
        }

        Curve out = new Curve(lengths, avgDist);
        out.setTitle("Random walk");
        out.setIndependentLabels(0, "number of steps");
        out.setDependentLabels(0, "average net distance");
        output(out);

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
        setParameterUpdatePolicy(Task.PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Compute a random walk");
        setHelpFileLocation("Random.html");

        // Define initial value and type of parameters
        defineParameter("nTrials", "100", USER_ACCESSIBLE);
        defineParameter("maxSteps", "4096", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Give the maximum number of steps in a walk: $title maxSteps TextField 4096\n";
        guilines += "Give the number of times each walk is performed: $title nTrials TextField 100\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        nTrials = new Integer((String) getParameter("nTrials")).intValue();
        maxSteps = new Integer((String) getParameter("maxSteps")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Random (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("nTrials"))
            nTrials = new Integer((String) value).intValue();

        if (paramname.equals("maxSteps"))
            maxSteps = new Integer((String) value).intValue();
    }


    /**
     * @return an array of the input types for Random
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for Random
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
    }

}
