package gftgu;

/*
This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/1.0/ 
or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA. 
*/

import org.trianacode.taskgraph.Unit;
import triana.types.Curve;


/**
 * Simulation of Mercury affected by a nearby massive planet
 *
 * @author B F Schutz
 * @version $Revision: 1.5 $
 * @created 18 Jul 2003
 * @date $Date: 2003/11/19 20:22:19 $ modified by $Author: schutz $
 * @todo
 */
public class MercPert extends Unit {

    // parameter data type definitions

    /*
      mSun is the mass of the central body of the solar system, in solar
      masses. This is set by the user in the parameter window, where it has
      the default value is 1.0, the same as our Sun.
      mPlanet is the mass of the massive orbiting planet, which with the
      central body forms a binary system within which Mercury will orbit.
      This mass is also given in solar masses, and it is set by the user
      in the parameter window. It has a default value of 0.1, which is about
      100 times the mass of Jupiter.
      */
    private double mSun;
    private double mPlanet;

    /*
      binarySeparation is the distance between the central body and the
      planet, both of which are taken to follow circular orbits. They
      begin at t=0 with a separation just along the x-direction. The
      separation is in meters and is set by the user in the parameter
      window.
      */
    private double binarySeparation;


    /*
      initPosMerc is the String used by the program to allow users
      to input the initial position of "Mercury" in the parameter
      window. The String is processed to obtain the initial x-
      and y-positions, which are stored in xInitMerc and yInitMerc.
      All positions are given in meters.
    */
    private String initPosMerc;
    private double xInitMerc;
    private double yInitMerc;

    /*
    initVelMerc is the String used by the program to allow users
    to input the initial velocity of "Mercury" in the parameter
    window. The String is processed to obtain the initial x-
    and y-velocity components, which are stored in vInitMerc
    and uInitMerc. All velocities are given in m/s.
    */
    private String initVelMerc;
    private double vInitMerc;
    private double uInitMerc;

    /*
      dt is the time-step in seconds. It is set by the user in the
      parameter window.
    */
    private double dt;

    /*
      maxSteps is the maximum number of steps in the calculation.
      This is used to ensure that the calculation will stop even
      if initial values are chosen so that "Mercury" is expelled
      from the Solar System. It is set by the user in the parameter
      window.
    */
    private int maxSteps;

    /*
      eps1 sets the accuracy of the time-step. If computed quantities
      change by a larger fraction than this in a time-step, the time-step
      will be cut in half, repeatedly if necessary. It is set by the user
      in the parameter window.
    */
    private double eps1;

    /*
      eps2 sets the accuracy of the predictor-corrector step. Averaging
      over the most recent time-step is iterated until it changes by
      less than this relative amount. It is set by the user in the
      parameter window.
    */
    private double eps2;

    /*
      kGravity is Newton's gravitational constant times the mass of the Sun.
      It is used internally and not set by the user.
    */
    private double kGravity = 1.327e20;


    /*
      Called when the user presses the Start button.
    */

    public void process() throws Exception {
        /*
          Define and initialize the variables we will need. The position
          and velocity components are referred to an x-y coordinate system
          whose origin is at the center of mass of the two main bodies. The
          word Sun in a variable name refers to the more massive central
          body (the "Sun"); Planet refers to the massive planet ("Jupiter"); and
          Merc refers to the small planet ("Mercury") whose orbit in the
          gravitational field of the other two bodies we wish to compute.
          We need the following variables for the calculation:
          - t is the time since the beginning of the orbit.
          - dt1 will be used as the "working" value of the time-step, which can
            be changed during the calculation. Using dt1 for the time-step allows
            us to keep dt as the original value, as specified by the user. Thus,
            dt1 is set equal to dt at the beginning of the calculation, but it may be
            reduced at any time-step, if accuracy requires it.
          - omega is the orbital angular velocity of the binary system consisting
            of the Sun and the massive planet.
          - rSun and rPlanet are the orbital radii of the Sun and the massive planet.
          - xSun and ySun are the coordinates of the position of the Sun, given
            here initial values that place the Sun to the left of the origin.
          - vSun and uSun are the components of the velocity of the Sun, given
            here initial values so that the Sun is moving downwards (negative-y
            direction).
          - xPlanet and yPlanet are the coordinates of the position of the massive
            planet, given here initial values that place the planet to the right
            of the origin.
          - xMerc and yMerc are the coordinates of the position of Mercury relative
            to the center of mass of the two massive bodies, given here initial
            values that correspond to the initial position data given in the parameter
            window (which are Mercury's position relative to the Sun).
          - vMerc and uMerc are the components of the velocity of Mercury relative
            to the center of mass of the two massive bodies, given here initial
            values that correspond to the initial velocity data given in the parameter
            window (which are Mercury's velocity relative to the Sun).
          - xMerc0 and yMerc0 are variables that hold temporary x- and y-coordinate
            values for Mercury.
          - xMercSun and yMercSun are the components of the displacement vector from
            the Sun to Mercury.
          - xMercPlanet and yMercPlanet are the components of the displacement vector
            from the massive planet to Mercury.
          - rMercSun is the distance between Mercury and the Sun.
          - rMercPlanet is the distance between Mercury and the massive planet.
          - rMercSun3 is the cube of the distance between Mercury and the Sun.
          - rMercPlanet3 is the cube of the distance between Mercury and the massive
            planet.
          - axMerc0 and ayMerc0 are the x-acceleration and y-acceleration, respectively, of
            Mercury at the location (xmerc0, yMerc0).
          - xCoordinateSun and yCoordinateSun are used to store the values of x and y of
            the Sun at each timestep. They are arrays of length maxSteps.
          - xCoordinatePlanet and yCoordinatePlanet are used to store the values of x and y of
            the massive planet at each timestep. They are arrays of length maxSteps.
          - xCoordinateMerc and yCoordinateMerc are used to store the values of x and y of
            Mercury at each timestep. They are arrays of length maxSteps.
        */
        double t = 0;
        double dt1 = dt;
        double omega = Math.sqrt(kGravity * (mSun + mPlanet) / Math.pow(binarySeparation, 3));
        double rSun = mPlanet / (mSun + mPlanet) * binarySeparation;
        double xSun = -rSun;
        double ySun = 0.0;
        double vSun = 0.0;
        double uSun = -omega * xSun;
        double rPlanet = binarySeparation - rSun;
        double xPlanet = rPlanet;
        double yPlanet = 0.0;
        double xMerc = xInitMerc + xSun;
        double yMerc = yInitMerc + ySun;
        double vMerc = vInitMerc + vSun;
        double uMerc = uInitMerc + uSun;
        double xMerc0 = xMerc;
        double yMerc0 = yMerc;
        double xMercSun = xMerc - xSun;
        double yMercSun = yMerc - ySun;
        double xMercPlanet = xMerc - xPlanet;
        double yMercPlanet = yMerc - yPlanet;
        double rMercSun = Math.sqrt(xMercSun * xMercSun + yMercSun * yMercSun);
        double rMercPlanet = Math.sqrt(xMercPlanet * xMercPlanet + yMercPlanet * yMercPlanet);
        double rMercSun3 = Math.pow(rMercSun, 3);
        double rMercPlanet3 = Math.pow(rMercPlanet, 3);
        double axMerc0 = -kGravity * (mSun * xMercSun / rMercSun3 + mPlanet * xMercPlanet / rMercPlanet3);
        double ayMerc0 = -kGravity * (mSun * yMercSun / rMercSun3 + mPlanet * yMercPlanet / rMercPlanet3);
        double[] xCoordinateSun = new double[maxSteps];
        double[] yCoordinateSun = new double[maxSteps];
        double[] xCoordinatePlanet = new double[maxSteps];
        double[] yCoordinatePlanet = new double[maxSteps];
        double[] xCoordinateMerc = new double[maxSteps];
        double[] yCoordinateMerc = new double[maxSteps];
        xCoordinateSun[0] = xSun;
        yCoordinateSun[0] = ySun;
        xCoordinatePlanet[0] = xPlanet;
        yCoordinatePlanet[0] = yPlanet;
        xCoordinateMerc[0] = xMerc;
        yCoordinateMerc[0] = yMerc;

        /*
          Now define other variables that will be needed, but without giving
          initial values. They will be assigned values during the calculation.
          - t1 is a temporary value of the time, used to compute postions of the
            binary bodies when needed.
          - xMerc1 and yMerc1 are temporary values of x and y for Mercury that are
            needed during the calculation.
          - axMerc1 and ayMerc1 are likewise temporary values of the acceleration.
          - dxMerc and dyMerc are variables that hold part of the changes in
            x and y for Mercury that occur during a time-step.
          - ddxMerc0, ddxMerc1, ddyMerc0 and ddyMerc1 are variables that hold other parts of
            the changes in x and y of Mercury during a time-step. The reason for having
            both dxMerc and ddxMerc will be explained in comments on the calculation below.
          - dvMerc and duMerc are the changes in velocity components of Mercury that occur
            during a time-step.
          - xMercSun1, yMercSun1, xMercPlanet1, and yMercPlanet1 are temporary values that
            hold the separations of Mercury from the Sun and the massive planet, respectively.
          - testPrediction will hold a value that is used by the predictor-corrector
            steps to assess how accurately the calculation is proceeding.
          - c1 and s1 are temporary variables used to make the computation of the positions
            of the Sun and the massive planet more efficient.
          - j and k are integers that will be used as loop counters.
        */
        double t1, xMerc1, yMerc1, axMerc1, ayMerc1, dvMerc, duMerc;
        double dxMerc, dyMerc, ddxMerc0, ddyMerc0, ddxMerc1, ddyMerc1;
        double xMercSun1, yMercSun1, xMercPlanet1, yMercPlanet1;
        double testPrediction;
        double c1, s1;
        int j, k;

        /*
          Now start the loop that computes the two orbits. The loop counter
          is j, which (as in Orbit) starts at 1 and increases by 1 each
          step. The test for exiting from the loop will be that the number
          of steps exceeds  the maximum set by the user.
        */
        for (j = 1; j < maxSteps; j++) {

            /*
              - Set dvMerc and duMerc to the changes in x- and y-speeds that would occur
                during time dt1 if the acceleration were constant at (axMerc0, ayMerc0).
              - Similarly set dxMerc and dyMerc to the changes in position that would
                occur if the velocity components vMerc and uMerc were constant during the
                time dt1.
              - Set ddxMerc0 and ddyMerc0 to the extra changes in x and y that occur because
                Mercury's velocity changes during the time dt1. The velocity change that
                is used is only dvMerc/2 (or duMerc/2, respectively) because the most
                accurate change in position comes from computing the average
                velocity during dt1. We separate the two position changes, dxMerc and
                ddxMerc0, because dxMerc will be unchanged when we do the predictor-corrector
                below (the change in position due to the original speed is always
                there), while ddxMerc0 will be modified when axMerc0 and hence dvMerc is modified
                by the predictor-corrector.
              - Finally, set ddxMerc1 and ddyMerc1 to ddxMerc0 and ddyMerc0 initially. They will
                change when we enter the predictor-corrector code.
            */
            t1 = t + dt1;
            dvMerc = axMerc0 * dt1;
            duMerc = ayMerc0 * dt1;
            dxMerc = vMerc * dt1;
            dyMerc = uMerc * dt1;
            ddxMerc0 = dvMerc / 2 * dt1;
            ddyMerc0 = duMerc / 2 * dt1;
            ddxMerc1 = ddxMerc0;
            ddyMerc1 = ddyMerc0;

            /*
              Now advance the position of Mercury by our initial estimates of the
              position changes, dxMerc + ddxMerc0 and dyMerc + ddyMerc0. Then
              compute the new distances of Mercury from the binary bodies and the
              resulting acceleration at this position. Use the positions of the
              binary bodies at the time t1.
            */
            xMerc1 = xMerc0 + dxMerc + ddxMerc0;
            yMerc1 = yMerc0 + dyMerc + ddyMerc0;
            c1 = Math.cos(t1 * omega);
            s1 = Math.sin(t1 * omega);
            xMercSun1 = xMerc1 + rSun * c1;
            yMercSun1 = yMerc1 + rSun * s1;
            rMercSun = Math.sqrt(xMercSun1 * xMercSun1 + yMercSun1 * yMercSun1);
            rMercSun3 = Math.pow(rMercSun, 3);
            xMercPlanet1 = xMerc1 - rPlanet * c1;
            yMercPlanet1 = yMerc1 - rPlanet * s1;
            rMercPlanet = Math.sqrt(xMercPlanet1 * xMercPlanet1 + yMercPlanet1 * yMercPlanet1);
            rMercPlanet3 = Math.pow(rMercPlanet, 3);
            axMerc1 = -kGravity * (mSun * xMercSun1 / rMercSun3 + mPlanet * xMercPlanet1 / rMercPlanet3);
            ayMerc1 = -kGravity * (mSun * yMercSun1 / rMercSun3 + mPlanet * yMercPlanet1 / rMercPlanet3);

            /*
              Time-step check.
              This is the code to check whether the time-step is too large. The idea
              is to compare the changes in acceleration of Mercury during the timestep
              with the acceleration of Mercury itself. If the change is too
              large a fraction of the original value, then the step is likely to be
              too large, and the resulting position too inaccurate. The code below cuts
              the time-step dt1 in half and then goes back to the beginning of the loop.
              This is explained more fully in the program Orbit.
            */
            if (Math.abs(axMerc1 - axMerc0) + Math.abs(ayMerc1 - ayMerc0) > eps1 * (Math.abs(axMerc0) + Math.abs(ayMerc0))) {
                dt1 /= 2;
                j--;
            } else {

                /*
                  Predictor-corrector step. This is explained in program Orbit.
                */
                testPrediction = Math.abs(ddxMerc0) + Math.abs(ddyMerc0);
                for (k = 0; k < 10; k++) {
                    /* compute dvMerc and duMerc by averaging the acceleration over dt1 */
                    dvMerc = (axMerc0 + axMerc1) / 2 * dt1;
                    duMerc = (ayMerc0 + ayMerc1) / 2 * dt1;
                    /* compute ddxMerc1 and ddyMerc1 by averaging the velocity change */
                    ddxMerc1 = dvMerc / 2 * dt1;
                    ddyMerc1 = duMerc / 2 * dt1;

                    /*
                      Test the change in ddx and ddy since the last iteration.
                      If it is more than a fraction eps2 of the original, then
                      ddx and ddy have to be re-computed by finding the acceleration
                      at the refined position.
                      If the change is small enough, then the "else:" clause is
                      executed, which exits from the for loop using the statement
                      "break". This finishes the iteration and goes on to wrap up
                      the calculation.
                    */
                    if (Math.abs(ddxMerc1 - ddxMerc0) + Math.abs(ddyMerc1 - ddyMerc0) > eps2 * testPrediction) {
                        /*
                          Re-define ddxMerc0 and ddyMerc0 to hold the values
                          from the last iteration
                        */
                        ddxMerc0 = ddxMerc1;
                        ddyMerc0 = ddyMerc1;
                        xMerc1 = xMerc0 + dxMerc + ddxMerc0;
                        yMerc1 = yMerc0 + dyMerc + ddyMerc0;
                        c1 = Math.cos(t1 * omega);
                        s1 = Math.sin(t1 * omega);
                        xMercSun1 = xMerc1 + rSun * c1;
                        yMercSun1 = yMerc1 + rSun * s1;
                        rMercSun = Math.sqrt(xMercSun1 * xMercSun1 + yMercSun1 * yMercSun1);
                        rMercSun3 = Math.pow(rMercSun, 3);
                        xMercPlanet1 = xMerc1 - rPlanet * c1;
                        yMercPlanet1 = yMerc1 - rPlanet * s1;
                        rMercPlanet = Math.sqrt(xMercPlanet1 * xMercPlanet1 + yMercPlanet1 * yMercPlanet1);
                        rMercPlanet3 = Math.pow(rMercPlanet, 3);
                        axMerc1 = -kGravity * (mSun * xMercSun1 / rMercSun3 + mPlanet * xMercPlanet1 / rMercPlanet3);
                        ayMerc1 = -kGravity * (mSun * yMercSun1 / rMercSun3 + mPlanet * yMercPlanet1 / rMercPlanet3);

                        /*
                          We now have the "best" acceleration values, using the most
                          recent estimates of the position at the end of the loop.
                          The next statement to be executed will be the first statement
                          of the "for" loop, finding better values of dvMerc, duMerc, ddxMerc1,
                          and ddyMerc1.
                        */
                    } else break;
                }

                /*
                  The iteration has finished, and we have sufficiently accurate
                  values of the position change in ddxMerc1 and ddyMerc1.
                  Use them to get final values of xMerc and yMerc at the end of
                  the time-step dt1 and store these into xMerc0 and yMerc0,
                  respectively, ready for the next time-step. Compute all the
                  rest of the variables needed for the next time-step and for
                  possible data output.
                */
                t += dt1;
                xMerc0 += dxMerc + ddxMerc1;
                yMerc0 += dyMerc + ddyMerc1;
                axMerc0 = axMerc1;
                ayMerc0 = ayMerc1;
                vMerc += dvMerc;
                uMerc += duMerc;
                xCoordinateMerc[j] = xMerc0;
                yCoordinateMerc[j] = yMerc0;
                c1 = Math.cos(t * omega);
                s1 = Math.sin(t * omega);
                xCoordinateSun[j] = -rSun * c1;
                yCoordinateSun[j] = -rSun * s1;
                xCoordinatePlanet[j] = rPlanet * c1;
                yCoordinatePlanet[j] = rPlanet * s1;

            }
        }

        /*
          The orbit is finished. Since in this program the loop always goes
          to the maximum number of steps, we do not have to define smaller
          arrays to output.
          In previous programs we have mainly used the Triana method "output()"
          for producing output from a unit. This works only if the unit has
          just one output data set. In all the output cases here, we require
          more than one data set to be output, so (as in the program Orbit) we
          use the more elaborate method "outputAtNode()", which allows us to
          specify which node will output which data. The node numbering
          starts with 0. We also make sure that the axis labels and the
          titles of the graphs are correctly given.
        */

        Curve out0 = new Curve(xCoordinateMerc, yCoordinateMerc);
        out0.setTitle("Mercury");
        out0.setIndependentLabels(0, "x (m)");
        out0.setDependentLabels(0, "y (m)");
        Curve out1 = new Curve(xCoordinateSun, yCoordinateSun);
        out1.setTitle("Sun");
        Curve out2 = new Curve(xCoordinatePlanet, yCoordinatePlanet);
        out2.setTitle("Jupiter");
        outputAtNode(0, out0);
        outputAtNode(1, out1);
        outputAtNode(2, out2);

    }
    /*
      End of program and user code. The remaining code takes care of
      Triana system matters.
    */


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

        setDefaultOutputNodes(3);
        setMinimumOutputNodes(3);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Simulation of Mercury affected by a nearby massive planet");
        setHelpFileLocation("MercPert.html");

        // Define initial value and type of parameters
        defineParameter("mSun", "1.0", USER_ACCESSIBLE);
        defineParameter("mPlanet", "0.1", USER_ACCESSIBLE);
        defineParameter("binarySeparation", "1.0e11", USER_ACCESSIBLE);
        defineParameter("initPosMerc", "4.5e10 0.0", USER_ACCESSIBLE);
        defineParameter("initVelMerc", "0.0 59220.0", USER_ACCESSIBLE);
        defineParameter("dt", "2000", USER_ACCESSIBLE);
        defineParameter("maxSteps", "400000", USER_ACCESSIBLE);
        defineParameter("eps1", "0.05", USER_ACCESSIBLE);
        defineParameter("eps2", "1e-4", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Give the mass of central body (the Sun) in solar masses $title mSun TextField 1.0\n";
        guilines += "Give the mass of massive planet (Jupiter), in solar masses $title mPlanet TextField 0.1\n";
        guilines += "Give the separation of central body and planet, in meters $title binarySeparation TextField 1.0e11\n";
        guilines += "Give the initial position of Mercury relative to the Sun, in meters (just give x and y) $title initPosMerc TextField 4.5e10 0.0\n";
        guilines += "Give the initial velocity of Mercury relative to the Sun, in m/s (just give v and u) $title initVelMerc TextField 0.0 59220.0\n";
        guilines += "Give the time-step, in seconds $title dt TextField 2000\n";
        guilines += "Give the maximum number of steps in the calculation $title maxSteps TextField 400000\n";
        guilines += "Give the relative error that will force the time-step to be cut in half $title eps1 TextField 0.05\n";
        guilines += "Give the relative accuracy that the predictor-corrector aims at $title eps2 TextField 1e-4\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values
     * specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        mSun = new Double((String) getParameter("mSun")).doubleValue();
        mPlanet = new Double((String) getParameter("mPlanet")).doubleValue();
        binarySeparation = new Double((String) getParameter("binarySeparation")).doubleValue();
        initPosMerc = (String) getParameter("initPosMerc");
        initVelMerc = (String) getParameter("initVelMerc");
        dt = new Double((String) getParameter("dt")).doubleValue();
        maxSteps = new Integer((String) getParameter("maxSteps")).intValue();
        eps1 = new Double((String) getParameter("eps1")).doubleValue();
        eps2 = new Double((String) getParameter("eps2")).doubleValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up MercPert (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("mSun"))
            mSun = new Double((String) value).doubleValue();

        if (paramname.equals("mPlanet"))
            mPlanet = new Double((String) value).doubleValue();

        if (paramname.equals("binarySeparation"))
            binarySeparation = new Double((String) value).doubleValue();

        if (paramname.equals("initPosMerc")) {
            initPosMerc = (String) value;
            decodeParamString(initPosMerc, "position");
        }

        if (paramname.equals("initVelMerc")) {
            initVelMerc = (String) value;
            decodeParamString(initVelMerc, "velocity");
        }

        if (paramname.equals("dt"))
            dt = new Double((String) value).doubleValue();

        if (paramname.equals("maxSteps"))
            maxSteps = new Integer((String) value).intValue();

        if (paramname.equals("eps1"))
            eps1 = new Double((String) value).doubleValue();

        if (paramname.equals("eps2"))
            eps2 = new Double((String) value).doubleValue();
    }


    /*
      Local method to convert input Strings for position and velocity
      information into double values in the appropriate variables.
    */

    private void decodeParamString(String inputValue, String type) {
        String[] parts = inputValue.trim().split("\\s+");
        if (type.equals("position")) {
            xInitMerc = Double.parseDouble(parts[0]);
            yInitMerc = Double.parseDouble(parts[1]);
        } else if (type.equals("velocity")) {
            vInitMerc = Double.parseDouble(parts[0]);
            uInitMerc = Double.parseDouble(parts[1]);
        }
    }

    /**
     * @return an array of the input types for MercPert
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for MercPert
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
    }

}
