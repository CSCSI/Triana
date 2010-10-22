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
 * Computes a planetary atmosphere
 *
 * @author B F Schutz
 * @version $Revision: 1.5 $
 * @created 19 May 2003
 * @date $Date: 2003/11/19 20:22:19 $ modified by $Author: schutz $
 * @todo
 */


public class Atmosphere extends Unit {

    // parameter data type definitions

    /*
      planetName is a String given by the user, which will be used as a name
      for the output graph.
    */
    private String planetName;

    /*
      gAccel is the value of the acceleration of gravity at the surface of
      the planet (bottom of the atmosphere), in SI units (m/s^2). Given by
      the user in the user interface window.
    */
    private double gAccel;

    /*
      mu is the mean molecular weight of the atmospheric gas, which is defined
      as the average mass, in units of the proton mass, of all the atoms,
      molecules, ions, electrons, etc that move freely in the atmosphere.
      It is given by the user in the user interface window.
    */
    private double mu;

    /*
      p0 is the pressure of the atmosphere at its base (the surface of the
      planet), in pascals. Given by the user in the user interface window.
    */
    private double p0;

    /*
      temperatureArray is a String that contains the temperature as a
      function of altitude. This is given by the user in the user
      interface window, in the form (h0,T0) (h1,T1) (h2,T2) ..., where
      h is in meters and T in kelvin. The program converts this into
      two double arrays h and T.
    */
    private String temperatureArray;

    /*
      outputType is a String which governs what kind of data will be
      output. All data is output as a Curve with x-values being the
      altitude and y-values being one of three choices: pressure,
      density, or temperature. The user chooses one of these three
      in the user interface window.
    */
    private String outputType;

    /*
      Define double arrays h, T that hold the numerical values of
      the altitude at which the temperature is given (h[]) and the
      values of the temperature at those altitudes (T[]). These
      arrays are re-defined and given values each time the parameter
      temperatureArray is set.
      Define the int measurements to hold the length of these arrays.
    */
    private double[] h, T;
    private int measurements;

    /*
      Two constants: k is Boltzmann's constant and mp is the mass of
      the proton, both in SI units.
    */
    private double k = 1.38e-23;
    private double mp = 1.67e-27;

    /*
      Three constants used for the computation of the temperature at
      the top of the atmosphere, above the highest altitude where the
      temperature has been measured.
      - reachedTop is a boolean that is set to false each time
        process() is called, and is then used by the method getTemp().
        See the comments in getTemp() below for details.
      - power and beta are the constants in the formula for the
        temperature in the upper region, T = beta * p^(power), where
        p is the pressure at that height and where "^" indicates raising
        to a power. We fix the value of power here, but the value of
        beta must be computed during the calculation. See the comments
        in getTemp() below for details.
    */
    private boolean reachedTop = false;
    private double power = 0.5;
    private double beta;

    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        /*
          Define variables needed for the calculation:
          - q is a combination of constants in the ideal gas law, used often.
          - rho0 is the density at the bottom of the atmosphere.
          - scale is the scale-height of the atmosphere, roughly the distance
            over which the pressure will fall by a factor of 2.
          - dh is the size of the step in altitude that the program will make.
          - arrays alt (height), p (pressure), rho (density), and Temp (temperature)
            hold the values of the associated physical quantities at the
            successive altitude steps. The arrays are initially given 1000
            elements. The choice of altitude step dh is designed to
            ensure that the top of the atmosphere (where p = 0) is reached
            in fewer than 1000 steps. Then give the values of the first
            elements of the arrays.
          - lastStep is an int that will hold the value of the array index
            associated with the top of the atmosphere. Set it to zero and
            use it as a test of whether the top has been reached (see below).
          - j is a loop counter.
        */
        double q = mp * mu / k;
        double rho0 = p0 * q / T[0]; //use ideal gas law to get density
        double scale = p0 / gAccel / rho0;
        double dh = scale / 200.;
        double[] alt = new double[1000];
        double[] p = new double[1000];
        double[] rho = new double[1000];
        double[] Temp = new double[1000];
        alt[0] = 0;
        p[0] = p0;
        Temp[0] = T[0];
        rho[0] = rho0;
        int lastStep = 0;
        int j;

        /*
          Set reachedTop to false at the beginning of the computation.
        */
        reachedTop = false;

        /*
          Do the calculation as long as the top has not been reached.
        */
        while (lastStep == 0) {
            /*
              Do calculation step by step, using the equation of hydrostatic
              equilibrium (in the second line of the loop).
            */
            for (j = 1; j < 1000; j++) {
                alt[j] = alt[j - 1] + dh;
                p[j] = p[j - 1] - gAccel * rho[j - 1] * dh;
                if (p[j] < 0) {
                    lastStep = j; //stop when the pressure goes negative
                    break;
                }
                Temp[j] = getTemp(alt[j], p[j]);
                rho[j] = p[j] * q / Temp[j];  //ideal gas law
            }
            /*
              If we reach this point and lastStep is still zero, then we have
              used 1000 steps and not yet reached the top. We must start the
              loop again with a larger step dh so that we can reach the top in
              1000 steps. The next line of the code resets the value of dh, and
              then when we reach the end-bracket of the "while"-loop the test
              in the loop will evaluate to true and the "for"-loop will be
              done again with this step-size.
              If we reach this point and lastStep is no longer zero, then we
              have finished the calculation. The next step (changing dh) will be
              executed but we will leave the "while"-loop and so the new value
              of dh will not be used.
            */
            dh *= 2.;
        }

        /*
          Now prepare output arrays depending on what output data type has
          been selected by the user. The arrays are only long enough to
          contain the number of points to the top of the atmosphere. Since
          the value of the variable lastStep is the step where the pressure
          first went negative, if we create arrays of length lastStep then
          this value will be excluded, since such arrays start at index 0
          and finish at index lastStep-1. We attach to each output Curve
          a title (which will appear on the graph legend), and we attach
          to the first output Curve the axis labels.
        */

        double[] finalH = new double[lastStep];
        Curve outData = null;
        String unitLabel = "";

        if (outputType.equals("Pressure")) {
            double[] finalP = new double[lastStep];
            for (j = 0; j < lastStep; j++) {
                finalH[j] = alt[j];
                finalP[j] = p[j];
            }
            outData = new Curve(finalH, finalP);
            unitLabel = " (Pa)";
        } else if (outputType.equals("Density")) {
            double[] finalRho = new double[lastStep];
            for (j = 0; j < lastStep; j++) {
                finalH[j] = alt[j];
                finalRho[j] = rho[j];
            }
            outData = new Curve(finalH, finalRho);
            unitLabel = " (kg/m^3)";
        } else if (outputType.equals("Temperature")) {
            double[] finalT = new double[lastStep];
            for (j = 0; j < lastStep; j++) {
                finalH[j] = alt[j];
                finalT[j] = Temp[j];
            }
            outData = new Curve(finalH, finalT);
            unitLabel = " (K)";
        }
        outData.setTitle(outputType + " for the atmosphere of " + planetName);
        outData.setIndependentLabels(0, "altitude (m)");
        outData.setDependentLabels(0, outputType + unitLabel);

        output(outData);
    }


    /*
      Method to compute the temperature at any height from the given
      measurements at specific altitudes. There are three cases:
      (1) The height is below the first measured altitude. In this
          case we simply use the temperature at the first measured
          altitude, i.e. we take the temperature to be constant from
          the ground up to the first measured value.
      (2) The height is between two measured altitudes. This is the
          normal case over most of the atmosphere. We find the
          temperature between two measurements by linear interpolation,
          which means that we draw a straight line on a graph of
          temperature versus altitude between the two measurements,
          and we use the temperature on the line at the actual
          required height.
      (3) The height is above the highest measured altitude. Here
          we cannot make the simple constant-temperature assumption
          of case (1), since an isothermal atmosphere goes on
          forever. Instead, we assume a temperature law of the form
          T = beta*pressure^(power), where beta and power are
          constants. We set the value of power = 0.5 in the
          initialization part of the code at the very beginning,
          but we do not know ahead of time what value of beta to
          use. This is determined by insisting that the temperature
          law join continuously onto the straight line between the
          last two measured points, and to do that we need to know
          the pressure at the height of the last measurement. We
          only know this as we are moving through the calculation,
          so the value of beta can only be computed at the step
          where we first reach the highest measured point. To keep
          track of when this happens, we use the boolean variable
          reachedTop, which is false at first. If the height is
          greater than or equal to that of the highest measurement,
          we test the value of reachedTop. If it is false, as it
          will be the first time we reach this altitude, then we
          compute beta from the local value of the pressure, and
          we then set reachedTop equal to true. This ensures that
          the next and subsequent times we reach past the
          altitude of the highest measurement, we will not
          re-compute beta. In both cases we then compute the
          temperature from our pressure law.
    */

    private double getTemp(double height, double pressure) {
        if (height <= h[0]) return T[0]; // return if height low
        if (height >= h[measurements - 1]) { // do if height high
            if (!reachedTop) {
                beta = T[measurements - 1] / Math.pow(pressure, power);
                reachedTop = true;
            }
            return beta * Math.pow(pressure, power); //return when high
        }
        int j = 1; // only reach this step if between measured heights
        while (height > h[j]) j++;
        return T[j - 1] + (T[j] - T[j - 1]) / (h[j] - h[j - 1]) * (height - h[j - 1]);
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
        setPopUpDescription("Computes a planetary atmosphere");
        setHelpFileLocation("Atmosphere.html");

        // Define initial value and type of parameters
        defineParameter("planetName", "Earth", USER_ACCESSIBLE);
        defineParameter("gAccel", "9.8", USER_ACCESSIBLE);
        defineParameter("mu", "29.0", USER_ACCESSIBLE);
        defineParameter("p0", "1.01e5", USER_ACCESSIBLE);
        defineParameter("temperatureArray", "(0,288) (2E3,275) (4E3,262) (6E3,249) (8E3,236) (1E4,223) (2E4,217) (4E4,250) (6E4,256) (8E4,181) (1E5,210) (1.4E5,714) (1.8E5,1156)", USER_ACCESSIBLE);
        // createTemperatures();
        defineParameter("outputType", "Pressure", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Give the name of the planet $title planetName TextField Earth\n";
        guilines += "Give the surface acceleration of gravity (meters per second per second) $title gAccel TextField 9.8\n";
        guilines += "Give the mean molecular weight of the atmospheric gas $title mu TextField 29.0\n";
        guilines += "Give the surface pressure in pascals $title p0 TextField 1.01e5\n";
        guilines += "Give the temperature function in the form (h0,T0) (h1,T1), ... $title temperatureArray TextField (0,288) (2E3,275) (4E3,262) (6E3,249) (8E3,236) (1E4,223) (2E4,217) (4E4,250) (6E4,256) (8E4,181) (1E5,210) (1.4E5,714) (1.8E5,1156)\n";
        guilines += "Select the type of output data $title outputType Choice [Pressure] [Density] [Temperature]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        planetName = (String) getParameter("planetName");
        gAccel = new Double((String) getParameter("gAccel")).doubleValue();
        mu = new Double((String) getParameter("mu")).doubleValue();
        p0 = new Double((String) getParameter("p0")).doubleValue();
        temperatureArray = (String) getParameter("temperatureArray");
        outputType = (String) getParameter("outputType");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Atmosphere (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("planetName"))
            planetName = (String) value;

        if (paramname.equals("gAccel"))
            gAccel = new Double((String) value).doubleValue();

        if (paramname.equals("mu"))
            mu = new Double((String) value).doubleValue();

        if (paramname.equals("p0"))
            p0 = new Double((String) value).doubleValue();

        if (paramname.equals("temperatureArray")) {
            temperatureArray = (String) value;
            createTemperatures();
        }

        if (paramname.equals("outputType"))
            outputType = (String) value;
    }

    /*
      In the following code we extract from the String temperatureArray
      the values of the altitude and temperature and place them in the
      arrays h[] and T[]. The job is simplified by using the Java
      String utility method split(), which divides a String into
      several Strings by splitting it at the given argument, and then
      puts the new Strings into a String array. Each of these pieces
      contains both h and T, which the remaining code separates from
      one another and converts their String representations into
      genuine double values using the Double utility method parseDouble().
      The method split() uses a device called a "regular expression" for
      its argument, which is a powerful pattern-forming language. To
      explain how it works is beyond our scope here.
    */

    private void createTemperatures() {
        temperatureArray = "(0,288) (2E3,275) (4E3,262) (6E3,249) (8E3,236) (1E4,223) (2E4,217) (4E4,250) (6E4,256) (8E4,181) (1E5,210) (1.4E5,714) (1.8E5,1156)";
        String[] pairs = temperatureArray.split("\\).*?\\(");  //divide at each ") ("
        measurements = pairs.length;
        pairs[0] = pairs[0].substring(1); // remove "(" from first substring
        int ln = pairs[measurements - 1].length();
        pairs[measurements - 1] = pairs[measurements - 1].substring(0, ln - 1); //remove ")" from last substring
        h = new double[measurements];
        T = new double[measurements];
        String tmp;
        int commaLocation;
        for (int j = 0; j < measurements; j++) {
            commaLocation = pairs[j].indexOf(',');
            tmp = pairs[j].substring(0, commaLocation);
            h[j] = Double.parseDouble(tmp);
            tmp = pairs[j].substring(commaLocation + 1);
            T[j] = Double.parseDouble(tmp);
        }
    }

    /**
     * @return an array of the input types for Atmosphere
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for Atmosphere
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
    }

}
