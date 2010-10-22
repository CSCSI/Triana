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
 * Computes the structure of a star
 *
 * @author B F Schutz
 * @version $Revision: 1.3 $
 * @created 23 May 2003
 * @date $Date: 2003/08/16 00:51:51 $ modified by $Author: schutz $
 * @todo
 */


public class Star extends Unit {

    // parameter data type definitions

    /*
      pC is the central pressure of the star, in pascals. It is given by
      the user in the user interface window.
    */
    private double pC;

    /*
      TC is the central temperature of the star, in kelvins. It is given by
      the user in the user interface window.
    */
    private double TC;

    /*
      mu is the mean molecular weight of the stellar gas, which is defined
      as the average mass, in units of the proton mass, of all the atoms,
      molecules, ions, electrons, etc that move freely in the star. We assume
      that this is constant through the star, which will not be true for
      older stars which have created heavy elements near their centers.
      It is given by the user in the user interface window.
    */
    private double mu;

    /*
      gamma is the polytropic exponent in the equation of state relating
      pressure and density: pressure is proportional to (density)^(gamma).
      It is given by the user in the user interface window.
    */
    private double gamma;

    /*
      outputType is a String which governs what kind of data will be
      output. All data is output as a Curve with x-values being the
      radial distance and y-values being one of four choices: pressure,
      density, temperature, or mass. In this case, "mass" means "mass
      interior to the given radius". The user chooses one of these four
      in the user interface window.
    */
    private String outputType;

    /*
      Three constants: k is Boltzmann's constant; mp is the mass of
      the proton; and G is Newton's gravitational constant. Values of
      all three are given in SI units.
    */
    private double k = 1.38e-23;
    private double mp = 1.67e-27;
    private double G = 6.672e-11;


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {

        /*
          Define variables needed for the calculation:
          - q is a combination of constants in the ideal gas law, used often.
          - rhoC is the density at the center of the star.
          - gammaRecip is the reciprocal of gamma, 1/gamma.
          - D is the proportionality factor in the polytropic equation of state
            written to give the density as a function of the pressure,
            rho = D * (pressure)^(1/gamma). This is determined by demanding that
            the polytropic law give the same central density (depending on D and
            the central pressure) as the ideal gas law (depending on the central
            pressure and temperature). Thus, D is determined by the central values
            of the pressure and temperature, and by the exponent gamma.
          - scale is the scale-height of the pressure, roughly the distance
            over which the pressure will fall by a factor of 2.
          - dr is the size of the step in radius that the program will make.
          - arrays radius, p (pressure), rho (density), Temp (temperature),
            and mass (mass inside the radius value of the same index)
            hold the values of the associated physical quantities at the
            successive radial steps. The arrays are initially given 2000
            elements. The choice of radial step dr is designed to
            ensure that the surface of the star (where p = 0) is reached
            in fewer than 2000 steps. Then give the values of the first
            elements of the arrays.
          - lastStep is an int that will hold the value of the array index
            associated with the surface of the star. Set it to zero and
            use it as a test of whether the surface has been reached (see below).
          - j is a loop counter.
        */
        double q = mp * mu / k;
        double rhoC = pC * q / TC; //use ideal gas law to get density
        double gammaRecip = 1.0 / gamma;
        double D = rhoC / Math.pow(pC, gammaRecip);
        double scale = Math.sqrt(pC / G) / rhoC;
        double dr = scale / 400.;
        double[] radius = new double[2000];
        double[] p = new double[2000];
        double[] rho = new double[2000];
        double[] Temp = new double[2000];
        double[] mass = new double[2000];
        radius[0] = 0;
        p[0] = pC;
        Temp[0] = TC;
        rho[0] = rhoC;
        mass[0] = 0;
        int lastStep = 0;
        int j;


        /*
          Do the calculation as long as the top has not been reached.
        */
        while (lastStep == 0) {

            /*
              As described in the text, we cannot start the loop accurately with the
              first points. Instead we compute the values of pressure etc at the
              first non-zero radial step (radius[1] = dr) by the approximations given
              in the text.
            */
            radius[1] = dr;
            p[1] = pC;
            rho[1] = rhoC;
            mass[1] = 4.0 * Math.PI * dr * dr * dr * rhoC / 3.0;
            Temp[1] = q * pC / rhoC; // use ideal gas law to get temperature

            /*
              Do calculation step by step, using the equation of hydrostatic
              equilibrium (in the second line of the loop).
            */
            for (j = 2; j < 2000; j++) {
                radius[j] = radius[j - 1] + dr;
                p[j] = p[j - 1] - G * rho[j - 1] * mass[j - 1] * dr / (radius[j - 1] * radius[j - 1]);
                if (p[j] < 0) {
                    lastStep = j; //stop when the pressure goes negative
                    break;
                }
                mass[j] = mass[j - 1] + 4 * Math.PI * radius[j - 1] * radius[j - 1] * rho[j - 1] * dr;
                rho[j] = D * Math.pow(p[j], gammaRecip);  //polytropic equation of state
                Temp[j] = q * p[j] / rho[j]; // ideal gas law
            }
            /*
              If we reach this point and lastStep is still zero, then we have
              used 2000 steps and not yet reached the surface. We must start the
              loop again with a larger step dr so that we can reach the surface in
              2000 steps. The next line of the code resets the value of dr, and
              then when we reach the end-bracket of the "while"-loop the test
              in the loop will evaluate to true and the "for"-loop will be
              done again with this step-size.
              If we reach this point and lastStep is no longer zero, then we
              have finished the calculation. The next step (changing dr) will be
              executed but we will leave the "while"-loop and so the new value
              of dr will not be used.
            */
            dr *= 2.;
        }

        /*
          Now prepare output arrays depending on what output data type has
          been selected by the user. The arrays are only long enough to
          contain the number of points to the surface of the star. Since
          the value of the variable lastStep is the step where the pressure
          first went negative, if we create arrays of length lastStep then
          this value will be excluded, since such arrays start at index 0
          and finish at index lastStep-1.
        */

        double[] finalR = new double[lastStep];
        Curve outData = null;
        String unitLabel = "";

        if (outputType.equals("Pressure")) {
            double[] finalP = new double[lastStep];
            for (j = 0; j < lastStep; j++) {
                finalR[j] = radius[j];
                finalP[j] = p[j];
            }
            outData = new Curve(finalR, finalP);
            unitLabel = " (Pa)";
        } else if (outputType.equals("Density")) {
            double[] finalRho = new double[lastStep];
            for (j = 0; j < lastStep; j++) {
                finalR[j] = radius[j];
                finalRho[j] = rho[j];
            }
            outData = new Curve(finalR, finalRho);
            unitLabel = " (kg/m^3)";
        } else if (outputType.equals("Temperature")) {
            double[] finalT = new double[lastStep];
            for (j = 0; j < lastStep; j++) {
                finalR[j] = radius[j];
                finalT[j] = Temp[j];
            }
            outData = new Curve(finalR, finalT);
            unitLabel = " (K)";
        } else if (outputType.equals("Mass")) {
            double[] finalM = new double[lastStep];
            for (j = 0; j < lastStep; j++) {
                finalR[j] = radius[j];
                finalM[j] = mass[j];
            }
            outData = new Curve(finalR, finalM);
            unitLabel = " (kg)";
        }
        outData.setTitle(outputType);
        outData.setIndependentLabels(0, "altitude (m)");
        outData.setDependentLabels(0, outputType + unitLabel);

        output(outData);

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
        setPopUpDescription("Computes the structure of a star");
        setHelpFileLocation("Star.html");

        // Define initial value and type of parameters
        defineParameter("pC", "7.158E15", USER_ACCESSIBLE);
        defineParameter("TC", "2.263E7", USER_ACCESSIBLE);
        defineParameter("mu", "1.285", USER_ACCESSIBLE);
        defineParameter("gamma", "1.36", USER_ACCESSIBLE);
        defineParameter("outputType", "Pressure", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Give the polytropic exponent (gamma, not n): $title gamma TextField 1.36\n";
        guilines += "Give the central pressure (in Pa): $title pC TextField 7.158E15\n";
        guilines += "Give the central temperature (in K): $title TC TextField 2.263E7\n";
        guilines += "Give the mean molecular weight of the gas in the star: $title mu TextField 1.285\n";
        guilines += "Select the type of output data $title outputType Choice [Pressure] [Density] [Temperature] [Mass]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        pC = new Double((String) getParameter("pC")).doubleValue();
        TC = new Double((String) getParameter("TC")).doubleValue();
        mu = new Double((String) getParameter("mu")).doubleValue();
        gamma = new Double((String) getParameter("gamma")).doubleValue();
        outputType = (String) getParameter("outputType");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Star (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("pC"))
            pC = new Double((String) value).doubleValue();

        if (paramname.equals("TC"))
            TC = new Double((String) value).doubleValue();

        if (paramname.equals("mu"))
            mu = new Double((String) value).doubleValue();

        if (paramname.equals("gamma"))
            gamma = new Double((String) value).doubleValue();

        if (paramname.equals("outputType"))
            outputType = (String) value;
    }


    /**
     * @return an array of the input types for Star
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for Star
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
    }

}
