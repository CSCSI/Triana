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
 * Compute the trajectory of a cannonball.
 *
 * @author B F Schutz
 * @version $Revision $
 * @created 02 May 2003
 * @todo
 */


public class CannonTrajectory extends Unit {

    // parameter data type definitions

    /* 
       g is the acceleration of gravity in meters per second per second
    */

    private double g = 9.8;

    /*
      dt is the time-step in seconds. Its value for any run is set by 
      the user in the parameter window.
    */

    private double dt;

    /* 
       speed is the launch speed in meters per second. Its value for any 
       run is set by the user in the parameter window.
    */

    private double speed;

    /* 
       angle is the launch angle in degrees, measured from the horizontal.
       Its value for any run is set by the user in the parameter window.
    */

    private double angle;


    /*
      Called when the user presses the Start button.
    */

    public void process() throws Exception {

        /*
          Initialize the calculation:
          - Define horizontalDistance and verticalDistance to be arrays 
            holding the horizontal distance and height reached by the 
            cannonball at each time-step. Give them length 1000 to allow 
            for up to 1000 time-steps in the trajectory. 
          - Define x and h to be the variables used to store these distances 
            temporarily at each time-step of the calculation.
          - Convert the input angle to radians so we can use trig functions.
            Use the Java built-in value of pi, called Math.PI.
          - Define u and v to be the horizontal and vertical components 
            of the velocity; compute their values from the speed and 
            angle chosen by the user. Use the trig functions built into 
            Java, called Math.cos and Math.sin.
          - Introduce w, a variable that stores an intermediate value 
            of the vertical speed.
          - Set the initial values stored for the height
            and distance to zero.
          - Define an integer variable j to count steps.
        */

        double[] horizontalDistance = new double[1000];
        double[] verticalDistance = new double[1000];
        double x = 0;
        double h = 0;
        double theta = angle * Math.PI / 180.0;
        double u = speed * Math.cos(theta);
        double v = speed * Math.sin(theta);
        double w;
        horizontalDistance[0] = 0;
        verticalDistance[0] = 0;
        int j;

        /* 
          Now enter the loop that computes each time step in succession. 
          The variable j is a counter: it starts at 1 and increases 
          by one at each step of the loop. 
          There should be no more than 1000 time-steps, since that is 
          the size of the array we have defined to hold the data. But 
          the calculation should end when the height is negative, 
          meaning that the cannonball has returned to the ground and 
          would actually be below it if the ground were not there. 
          Therefore the condition for continuing the loop is 
                 ( h >= 0.0 ) && ( j < 1000 )
        */

        for (j = 1; ((h >= 0.0) && (j < 1000)); j++) {

            /* 
              At each step in the loop the value of x starts out as 
              the horizontal distance at the previous step. Increase 
              it by the distance traveled in time dt so that it now has
              the new value of the distance.
            */
            x = x + u * dt;

            /* 
              At each step in the loop the value of v starts out as 
              the vertical speed at the previous one. Since we want to 
              use the average of the vertical speeds at two time-steps, 
              keep v unchanged at first, and define w to be the speed 
              at the present time-step. Thus, w equals v diminished by 
              the downward acceleration.
            */
            w = v - g * dt;

            /* 
              Now follow the rule described in the text, that the 
              change in vertical height depends on the average speed 
              over the time-interval, in other words on the speed 
                             (w + v )/2.
              Multiply this speed by the interval of time dt and 
              increase the height by this amount. Note that the speeds 
              eventually will become negative because of the previous 
              line of code, so that eventually h will begin to decrease.
            */
            h = h + (w + v) / 2 * dt;

            /*
              Now store the calculated height and horizontal distance in 
              the arrays defined for them. This allows us to re-use h and 
              x for the new values at the next time-step without losing 
              the values we have computed for this time-step.
            */
            verticalDistance[j] = h;
            horizontalDistance[j] = x;

            /*
              Finally update the variable v so that it stores the vertical 
              speed for the present time-step. That way, at the next step 
              in the loop it will contain the "old" speed, as required 
              for the averaging of speeds described above.
            */
            v = w;

        }
        /* 
          The closing bracket above is the end of the group of statements 
          that form the loop. The computer increases j here and tests to 
          see if it should do another step in the loop. If so it goes 
          back to the first statement after the opening bracket at the end 
          of the "for" statement above. If not it goes to the next statement.
        */

        /*
          We have now exited from the loop. That means that either the height
          is negative (the cannonball has returned to the ground), or the
          loop has run through 1000 steps before the cannonball returned.
          In the latter case, the user will see from the output that the
          trajectory is not ended, and the whole thing should be run again
          with a larger choice of time-step.
          For the case where the cannonball returns before we run out of
          the allocated number of steps, the variable j is equal to one
          more than the number of steps, since it got increased at the end
          of the final loop step, after the last values were stored. So
          we define two new arrays of exactly the length needed to hold
          the data, copy the values into them, and then output the result
          so that it can be examined, printed, or graphed. (We have to go
          to the trouble of defining new arrays since, in Java, it is not
          possible to re-define the length of an array without losing the
          data stored in it.) The new arrays should have length j since
          they contain the initial values as well as the values at all the
          time-steps.
          The output is defined to be an object called a Curve, which is a
          data type defined in Triana. It contains not only the data but
          also the labels that can be used by the grapher. So we set here
          the title and the horizontal and vertical axis labels.
        */

        double[] finalHorizontal = new double[j];
        double[] finalVertical = new double[j];

        for (int k = 0; k < j; k++) {
            finalHorizontal[k] = horizontalDistance[k];
            finalVertical[k] = verticalDistance[k];
        }

        Curve out = new Curve(finalHorizontal, finalVertical);
        out.setTitle("Trajectory of projectile");
        out.setIndependentLabels(0, "horizontal distance (m)");
        out.setDependentLabels(0, "vertical distance (m)");
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
        setPopUpDescription("Compute the trajectory of a cannonball.");
        setHelpFileLocation("CannonTrajectory.html");

        // Initialise task parameters with default values (if not already initialised)
        if (!isParameter("speed"))
            setParameter("speed", "100");

        if (!isParameter("angle"))
            setParameter("angle", "45");

        if (!isParameter("dt"))
            setParameter("dt", "0.1");

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Give the initial speed of the projectile (in m/s) $title speed Scroller 0 200 100 false\n";
        guilines += "Give the angle the cannon's barrel makes with the ground (in degrees) $title angle Scroller 0 90 45 false\n";
        guilines += "Give the length of the time-step (in seconds) $title dt Scroller 0 1 0.1 false\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        speed = new Double((String) getParameter("speed")).doubleValue();
        angle = new Double((String) getParameter("angle")).doubleValue();
        dt = new Double((String) getParameter("dt")).doubleValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up CannonTrajectory (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("speed"))
            speed = new Double((String) value).doubleValue();

        if (paramname.equals("angle"))
            angle = new Double((String) value).doubleValue();

        if (paramname.equals("dt"))
            dt = new Double((String) value).doubleValue();
    }


    /**
     * @return an array of the input types for CannonTrajectory
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for CannonTrajectory
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
    }

}
