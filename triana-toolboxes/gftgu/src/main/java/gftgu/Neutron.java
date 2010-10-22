package gftgu;

/*
This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike License. 
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/1.0/ 
or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA. 
*/

import org.trianacode.taskgraph.Unit;
import triana.types.Curve;


/**
 * Computes the structure of a neutron star in relativity
 *
 * @author B F Schutz
 * @version $Revision: 1.3 $
 * @created 28 Jul 2003
 * @date $Date: 2003/08/16 00:51:51 $ modified by $Author: schutz $
 * @todo
 */
public class Neutron extends Unit {


    /*
      gamma is the polytropic exponent in the equation of state relating
      pressure and density: pressure is proportional to (density)^(gamma).
      It is given by the user in the user interface window.
    */
    private double gamma;

    /*
      pC is the central pressure of the star, in pascals. It is given by
      the user in the user interface window.
    */
    private double pC;

    /*
      K is the proportionality constant in the equation of state, relating
      pressure to density^gamma. It is given by the user in the user
      interface window.
    */
    private double K;

    /*
      outputType is a String which governs what kind of data will be
      output. All data is output as a Curve with x-values being the
      radial distance and y-values being one of three choices: pressure,
      density, or mass. In this case, "mass" means "relativistic mass
      interior to the given radius". The user chooses one of these four
      in the user interface window.
    */
    private String outputType;

    /*
      Two constants needed in the calculation, in SI units:
      - G is Newton's gravitational constant
      - c2 is the square of the speed of light.
    */
    private double G = 6.672e-11;
    private double c2 = 8.98755e16;


    /*
    * Called whenever there is data for the unit to process
    */

    public void process() throws Exception {

        /*
          Define variables needed for the calculation:
          - gammaRecip is the reciprocal of gamma, 1/gamma.
          - rhoC is the density at the center of the star.
          - scale is the scale-height of the pressure, roughly the distance
            over which the pressure will fall by a factor of 2.
          - dr is the size of the step in radius that the program will make.
          - arrays radius, p (pressure), rho (density), and mass (relativistic
            mass function inside the star) hold the values of the associated
            physical quantities at the successive radial steps. The arrays
            are initially given 2000 elements. The choice of radial step dr
            is designed to ensure that the surface of the star (where p = 0)
            is reached in fewer than 2000 steps. Then give the values of the
            first elements of the arrays.
          - lastStep is an int that will hold the value of the array index
            associated with the surface of the star. Set it to zero and
            use it as a test of whether the surface has been reached (see below).
          - j is a loop counter.
        */
        double gammaRecip = 1.0 / gamma;
        double rhoC = Math.pow(pC / K, gammaRecip);
        double scale = Math.sqrt(pC / G) / rhoC;
        double dr = scale / 400.;
        double[] radius = new double[2000];
        double[] p = new double[2000];
        double[] rho = new double[2000];
        double[] mass = new double[2000];
        radius[0] = 0;
        p[0] = pC;
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

            /*
              Do calculation step by step, using the equation of hydrostatic
              equilibrium (in the second line of the loop).
            */
            for (j = 2; j < 2000; j++) {
                radius[j] = radius[j - 1] + dr;
                p[j] = p[j - 1] - G * (rho[j - 1] + p[j - 1] / c2) * (mass[j - 1] + 4 * Math.PI * Math.pow(radius[j - 1], 3) / c2) * dr / (radius[j - 1] * (radius[j - 1] - 2 * G * mass[j - 1] / c2));
                if (p[j] < 0) {
                    lastStep = j; //stop when the pressure goes negative
                    break;
                }
                mass[j] = mass[j - 1] + 4 * Math.PI * radius[j - 1] * radius[j - 1] * rho[j - 1] * dr;
                rho[j] = Math.pow(p[j] / K, gammaRecip);  //polytropic equation of state
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
        setParameterUpdatePolicy(PROCESS_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Computes the structure of a neutron star in relativity");
        setHelpFileLocation("Neutron.html");

        // Define initial value and type of parameters
        defineParameter("gamma", "1.666667", USER_ACCESSIBLE);
        defineParameter("pC", "1.26e35", USER_ACCESSIBLE);
        defineParameter("K", "5.3802e3", USER_ACCESSIBLE);
        defineParameter("outputType", "Pressure", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Give the polytropic exponent (gamma, not n): $title gamma TextField 1.666667\n";
        guilines += "Give the central pressure (in Pa): $title pC TextField 1.26e35\n";
        guilines += "Give the proportionality constant in the equation of state: $title K TextField 5.3802e3\n";
        guilines += "Select the type of output data: $title outputType Choice [Pressure] [Density] [Mass]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset. Restores the unit's variables to values
     * specified by the parameters.
     */
    public void reset() {
        // Set unit variables to the values specified by the parameters
        gamma = new Double((String) getParameter("gamma")).doubleValue();
        pC = new Double((String) getParameter("pC")).doubleValue();
        K = new Double((String) getParameter("K")).doubleValue();
        outputType = (String) getParameter("outputType");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Neutron (e.g. close open files) 
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables 
        if (paramname.equals("gamma"))
            gamma = new Double((String) value).doubleValue();

        if (paramname.equals("pC"))
            pC = new Double((String) value).doubleValue();

        if (paramname.equals("K"))
            K = new Double((String) value).doubleValue();

        if (paramname.equals("outputType"))
            outputType = (String) value;
    }


    /**
     * @return an array of the input types for Neutron
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for Neutron
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
    }

}
