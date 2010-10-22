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
 * Program to compute an orbit around the Earth.
 *
 * @author Bernard Schutz
 * @version $Revision $
 * @created 07 Apr 2003
 * @todo
 */


public class EarthOrbit extends Unit {

    // parameter data type definitions
    /*
      g is the acceleration of gravity near the surface of the Earth.
      In this program we do not take account of the fact that gravity 
      gets weaker as one goes further from the Earth, so the program 
      will only accurately simulate orbits that are near the surface.
    */
    private double g = 9.8;

    /*
      rEarth is the radius of the Earth in meters.
    */
    private double rEarth = 6378200;

    /*
      h0 is the initial height of the projectile above the surface of 
      the Earth, in meters. This is its starting position. Is value for 
      any run is set by the user in the parameter window.
    */
    private double h0;

    /* 
       vInit is the initial horizontal speed of the projectile, in meters 
       per second. Its default value is 7900, which is slightly smaller 
       than is required to achieve orbit. Use the parameter window to 
       set it to 7906 to get into orbit. 
    */
    private double vInit;

    /* 
       uInit is the initial vertical speed of the projectile, in meters 
       per second. Its value for any run is set by the user in the 
       parameter window.
    */
    private double uInit;

    /*
      dt is the time-step in seconds. Its value for any run is set by the 
      user in the parameter window.
    */
    private double dt;

    /*
      maxSteps is the maximum number of steps in the calculation. This is 
      used to ensure that the calculation will stop even if initial values
      are chosen so that the projectile goes far away. Its value for any 
      run is set by the user in the parameter window.
    */
    private int maxSteps;


    /*
      Called when the user presses the Start button.
    */

    public void process() throws Exception {

        /*
          Define and initialize the variables we will need. The position 
          and velocity components are referred to an x-y coordinate system
          whose origin is at the center of the Earth. The initial starting 
          position is taken to be on the y-axis, so that the initial value 
          of the y-position is the radius of the Earth added to the intial 
          height of the starting position, i.e. rEarth + h0. The initial 
          x-speed is the initial horizontal speed; the initial y-speed is
          the initial vertical speed. We need the following variables for 
          the calculation:
          - v0 and v1 are the x-speeds used in the loop; as in
            the program CannonTrajectory, we keep track of the speed  
            in the previous loop step separately from the current one.
            v1 is the current position, v0 the previous one.
          - u0 and u1 are the y-speeds analogous to v0 and v1.
          - x and y are the x-coordinate position and y-coordinate 
            position, respectively.
          - ax and ay are the x-acceleration and y-acceleration, respectively.
          - r is the radial distance of the current position from the center 
            of the Earth.
          - xCoordinate and yCoordinate are used to store the values of 
            x and y at each timestep. They are arrays of length maxSteps.
          - j is an integer counter for the loop steps.
        */
        double v0 = vInit;
        double v1 = v0;
        double u0 = uInit;
        double u1 = u0;
        double x = 0;
        double y = rEarth + h0;
        double ax, ay;
        double r = Math.sqrt(x * x + y * y);
        double[] xCoordinate = new double[maxSteps];
        double[] yCoordinate = new double[maxSteps];
        xCoordinate[0] = x;
        yCoordinate[0] = y;
        int j;

        /* 
          Now start the loop that computes the trajectory. The loop counter
          is j, which starts at 1 and increases by 1 each step. The test for 
          exiting from the loop will be either that the number of steps is
          too large or that the orbital radius r is less that the radius 
          of the Earth rEarth, so the condition for continuing the loop is 
                  ( r >= rEarth ) && ( j < maxSteps )
        */

        for (j = 1; ((r >= rEarth) && (j < maxSteps)); j++) {

            /*
              First calculate the components of the acceleration of gravity
              at the last computed position.
            */
            ax = -g * x / r;
            ay = -g * y / r;

            /* 
               The velocity components change according to the acceleration.
               As in program CannonTrajectory, we keep track of the previous 
               and current values of the velocity components separately.
            */
            v1 = v0 + ax * dt;
            u1 = u0 + ay * dt;

            /*
              The position components change according to the average of 
              the velocity during the last time-interval, as in 
              CannonTrajectory. Calculate the new radial distance from these.
              Store the data in the position arrays.
            */
            x = x + (v0 + v1) / 2 * dt;
            y = y + (u0 + u1) / 2 * dt;
            r = Math.sqrt(x * x + y * y);
            xCoordinate[j] = x;
            yCoordinate[j] = y;

            /*
              Now update the values of velocities to get ready for next
              time-step.
            */
            v0 = v1;
            u0 = u1;

        }
        /* 
          The closing bracket above is the end of the group of statements 
          that form the loop. The computer increases j here and tests to 
          see if it should do another step in the loop. If so it goes 
          back to the first statement after the opening bracket at the end 
          of the "for" statement above. If not it goes to the next statement.
        */

        /*
          We have now exited from the loop. That means that either the radius
          is inside the Earth (the projectile has hit the ground), or the
          loop has run through maxSteps steps.
          In the latter case, the user will see from the output that the
          trajectory is not ended. The whole thing can be run again
          with a larger choice of time-step or a larger value of maxSteps,
          but most likely it is better to choose different initial conditions
          so that the orbit behaves better.
          In order to display the resulting orbit in a way that shows the
          relationship of the orbit to the Earth, we draw the circle representing
          the Earth as well as drawing the orbit. The coordinates of the Earth
          are put into the arrays xEarth and yEarth and are also output. This
          means that the unit will have two output nodes that have to be
          connected to the grapher. The first node (numbered 0) has the shape
          of the Earth and the second (numbered 1) has the trajectory.
          The ouput data sets are defined as objects of type Curve, which is
          a Triana data type. The data are assigned to each Curve when it is
          created by the "new" statement. In addition, a graph title is added
          to each Curve and axis labels are added to the Curve for the Earth's
          shape. This helps the graphing unit to display the information
          intelligibly.
        */

        int k;
        double[] finalX = new double[j];
        double[] finalY = new double[j];

        for (k = 0; k < j; k++) {
            finalX[k] = xCoordinate[k];
            finalY[k] = yCoordinate[k];
        }

        Curve out1 = new Curve(finalX, finalY);
        out1.setTitle("Orbit of the projectile");

        double angleStep = Math.PI / 200;
        double[] xEarth = new double[400];
        double[] yEarth = new double[400];

        for (k = 0; k < 400; k++) {
            xEarth[k] = rEarth * Math.cos(angleStep * k);
            yEarth[k] = rEarth * Math.sin(angleStep * k);
        }
        xEarth[399] = xEarth[0];  // This ensures that the curve describing the
        yEarth[399] = yEarth[0];  // Earth is closed.

        Curve out0 = new Curve(xEarth, yEarth);
        out0.setTitle("Surface of the Earth");
        out0.setIndependentLabels(0, "horizontal distance (m)");
        out0.setDependentLabels(0, "vertical distance (m)");

        outputAtNode(0, out0);
        outputAtNode(1, out1);
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

        setDefaultOutputNodes(2);
        setMinimumOutputNodes(2);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(Task.PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Program to compute an orbit around the Earth.");
        setHelpFileLocation("EarthOrbit.html");

        // Initialise task parameters with default values (if not already initialised)
        if (!isParameter("h0"))
            setParameter("h0", "300");

        if (!isParameter("vInit"))
            setParameter("vInit", "7900");

        if (!isParameter("uInit"))
            setParameter("uInit", "0");

        if (!isParameter("dt"))
            setParameter("dt", "0.4");

        if (!isParameter("maxSteps"))
            setParameter("maxSteps", "15000");

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Give the initial height above the Earth's surface, in meters $title h0 TextField 300\n";
        guilines += "Give the initial horizontal speed, in meters per second $title vInit TextField 7900\n";
        guilines += "Give the initial vertical speed, in meters per second $title uInit TextField 0\n";
        guilines += "Give the time-step, in seconds $title dt TextField 0.4\n";
        guilines += "Give the maximum number of steps $title maxSteps TextField 15000\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        h0 = new Double((String) getParameter("h0")).doubleValue();
        vInit = new Double((String) getParameter("vInit")).doubleValue();
        uInit = new Double((String) getParameter("uInit")).doubleValue();
        dt = new Double((String) getParameter("dt")).doubleValue();
        maxSteps = new Integer((String) getParameter("maxSteps")).intValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up EarthOrbit (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("h0"))
            h0 = new Double((String) value).doubleValue();

        if (paramname.equals("vInit"))
            vInit = new Double((String) value).doubleValue();

        if (paramname.equals("uInit"))
            uInit = new Double((String) value).doubleValue();

        if (paramname.equals("dt"))
            dt = new Double((String) value).doubleValue();

        if (paramname.equals("maxSteps"))
            maxSteps = new Integer((String) value).intValue();
    }


    /**
     * @return an array of the input types for EarthOrbit
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for EarthOrbit
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
    }

}
