package gftgu;

/*
This work is licensed under the Creative Commons Attribution-NonCommercial-ShareAlike License.
To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/1.0/
or send a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305, USA.
*/

import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.Curve;


/**
 * Simulates the orbit of a planet around a central mass in Newtonian gravity
 *
 * @author B F Schutz
 * @version $Revision $
 * @created 03 Apr 2003
 * @date $Date: 2007/01/23 12:36:53 $ modified by $Author: spxmss $
 * @todo
 */


public class Orbit extends Unit {

    /*
      xInit is the initial value of the x-coordinate of the planet,
      in meters. Similarly, yInit is the initial value of the
      y-coordinate. These are given default values but their values for
      any run are set by the user in the parameter window.
    */
    private double xInit;
    private double yInit;

    /*
      vInit and uInit are the initial values of the x- and y-
      velocities, respectively, in meters per second. These
      are given default values but their values for any run
      are set by the user in the parameter window.
    */
    private double vInit;
    private double uInit;

    /*
      M is the mass (in kg) of the object creating the gravitational field
      in which the orbit is computed. The default value is the mass of the
      Sun, but it is set by the user in the parameter window.
    */
    private double M;

    /*
      dt is the time-step in seconds. It has a default value but it can
      be set by the user in the parameter window.
    */
    private double dt;

    /*
      maxSteps is the maximum number of steps in the calculation. This is
      used to ensure that the calculation will stop even if initial values
      are chosen so that the projectile goes far away. It is given
      a default value but it can be set by the user in the parameter window.
    */
    private int maxSteps;


    /*
      eps1 sets the accuracy of the time-step. If computed quantities
      change by a larger fraction than this in a time-step, the time-step
      will be cut in half, repeatedly if necessary. Its value for any run
      is set by the user in the parameter window.
    */
    private double eps1;

    /*
      eps2 sets the accuracy of the predictor-corrector step. Averaging
      over the most recent time-step is iterated until it changes by
      less than this relative amount. Its value for any run is set by
      the user in the parameter window.
    */
    private double eps2;

    /*
      outputType regulates the data that is to be output from the program.
      The computation produces many kinds of data: positions, velocities,
      energies. In order to make them accessible, the user can select a
      value for this String, and the unit will output the required data.
      First-time programmers can safely ignore these output issues, which
      add some length to the program, although in a straightforward way.
      Here are the choices and the data that they produce:
      - "orbit (X,Y)" is the default choice and produces the orbit of the
        planet drawn in the X-Y plane of the orbit. The unit outputs this
        data from a single output node, which should be connected to the
        graphing unit.
      - "velocity space (V,U)" produces a curve in what physicists call
        velocity space, a graph whose axes are the x- and y-components of
        the velocity. Since a planet on a closed orbit also comes back to
        the same velocity after one orbit, the graph of this curve will
        be closed for such an orbit. The unit outputs this data from a
        single output node, which should be connected to the graphing unit.
      - "position vs. time (X,t) and (Y,t)" produces two curves, one giving
        the value of the X-coordinate (vertical axis of the graph) against
        the time along the orbit (horizontal axis) and the second giving the
        Y-coordinate against time. To produce this data the unit automatically
        changes the number of its output nodes to two as soon as the user
        selects this option in the user interface window; the first output
        node produces a curve of (X,t) and the second output node produces
        (Y,t). The user should connect both nodes to the grapher to
        see both curves at once. Alternatively, if only one is connected to
        the grapher then only that particular coordinate will be displayed.
        To connect two inputs to the grapher the user must use the grapher
        unit's node window to set the number of input nodes to two.
      - "velocity vs. time (V,t) and (U,t)" This does the same as the
        previous choice except that it produces the x- and y-components of
        the velocity (V and U) as functions of time instead of the
        coordinate positions. Again the unit changes its number of output
        nodes to two, and the user must change the grapher's input nodes to
        two as well.
      - "energy vs time" This produces three curves: the potential energy,
        the kinetic energy, and the total energy, all as functions of time.
        The unit changes itself to three output nodes and the data are output
        in the order given in the previous sentence. To see all three at
        once, as in the figure in the text, modify the number of input nodes
        of the grapher to three and connect them all.
    */
    private String outputType;

    /*
      This variable is for internal use and is not set by the user.
    */
    private Task task;


    /*
      Called when the user presses the Start button.
    */

    public void process() throws Exception {

        /*
          Define and initialize the variables we will need. The position
          and velocity components are referred to an x-y coordinate system
          whose origin is at the central gravitating mass. We need the
          following variables for the calculation:
          - t is the time since the beginning of the orbit.
          - dt1 will be used as the "working" value of the time-step, which can
            be changed during the calculation. Using dt1 for the time-step allows
            us to keep dt as the original value, as specified by the user. Thus,
            dt1 is set equal to dt at the beginning of the calculation, but it may be
            reduced at any time-step, if accuracy requires it.
          - v and u are the x- and y-speed, given here their initial values.
          - x0 and y0 are variables that hold x- and y-coordinate values.
          - r is the distance of the point (x0, y0) from the central mass.
          - r3 is the cube of the radial distance.
          - kGravity is the constant GM in Newton's law of gravity, where G is
            Newton's gravitational constant.
          - ax0 and ay0 are the x-acceleration and y-acceleration, respectively,
            at the location (x0, y0).
          - xCoordinate and yCoordinate are used to store the values of
            x and y at each timestep. They are arrays of length maxSteps.
          - xVelocity and yVelocity are arrays that are used to store the values
            of the velocity components at each timestep.
          - potentialEnergy and kineticEnergy are arrays that are used to store
            the values of the potential and kinetic energy of the planet, taking
            its mass to equal 1. (The mass of the planet is not needed for the
            other calculations in this program, and since both energies are
            simply proportional to the mass, the energies for any particular
            planetary mass can be obtained by multiplying these values by
            the mass after they are output from the program.)
          - time is an array that is used to store the value of the time
            associated with the current position, as measured from the
            beginning of the orbit.
        */
        double t = 0;
        double dt1 = dt;
        double v = vInit;
        double u = uInit;
        double x0 = xInit;
        double y0 = yInit;
        double r = Math.sqrt(x0 * x0 + y0 * y0);
        double r3 = r * r * r;
        double kGravity = M * 6.6726e-11;
        double ax0 = -kGravity * x0 / r3;
        double ay0 = -kGravity * y0 / r3;
        double[] xCoordinate = new double[maxSteps];
        double[] yCoordinate = new double[maxSteps];
        double[] xVelocity = new double[maxSteps];
        double[] yVelocity = new double[maxSteps];
        double[] potentialEnergy = new double[maxSteps];
        double[] kineticEnergy = new double[maxSteps];
        double[] time = new double[maxSteps];
        xCoordinate[0] = x0;
        yCoordinate[0] = y0;

        /*
          Now define other variables that will be needed, but without giving
          initial values. They will be assigned values during the calculation.
          - x1 and y1 are temporary values of x and y that are needed during the
            calculation.
          - ax1 and ay1 are likewise temporary values of the x- and y-acceleration.
          - dx and dy are variables that hold part of the change in x and y that
            occurs during a time-step.
          - ddx0, ddy0, ddx1, and ddy1 are variables that hold other parts of
            the changes in x and y during a time-step. The reason for having both
            dx and ddx will be explained in comments on the calculation below.
          - dv and du are the changes in velocity that occur during a time-step.
          - testPrediction will hold a value that is used by the predictor-corrector
            steps to assess how accurately the calculation is proceeding.
          - angleNow holds the angular amount by which the planet has advanced in its
            orbit at the current time-step.
          - j and k are integers that will be used as loop counters.
        */
        double x1, y1, ax1, ay1, dv, du, dx, dy, ddx0, ddy0, ddx1, ddy1;
        double testPrediction, angleNow;
        int j, k;

        /*
          Finally, we introduce some variables that are used to determine when the
          trajectory completes a full orbit, so that the program can stop. This is
          not a simple job, if we want to be able to handle any starting position
          and any starting velocity. The idea is to determine from the initial
          position and velocity whether the trajectory will move in the clockwise
          or counterclockwise direction around the central mass. Having established
          that, then we will write code below that checks to see if the angular
          position of the trajectory has become larger (in the counterclockwise case)
          or smaller (clockwise) than the starting position. If so, then the program
          stops, since the original position has been passed. Of course, at the very
          start the orbit satisfies these conditions as well, so the test can only
          be applied after the orbit has gone at least half-way. The variables that
          are needed in order to perform these tests are as follows:
          - angleInitPos is the angle that a line from the origin to the initial
            position makes with the x-axis. Like all the other angles, it is computed
            from the initial data using the Math.atan2 function. This returns a
            value between -Pi and +Pi radians.
          - angleInitVel is similarly the angle that the initial velocity vector
            makes with the x-axis
          - anglediff is the angle between the position and velocity, which is
            used to decide whether the orbit will move in the clockwise or
            counter-clockwise direction. The comments just before its definition
            explain how we ensure that it is in the same range (-Pi, Pi) as the
            other angles.
          - counterclockwise is a boolean variable (true/false) which is true if
            the orbit moves in the counterclockwise direction, false otherwise
          - fullOrbit ia another boolean variable that will be set to true when
            the orbit returns to its starting point, so that the calculation can
            stop. Its initial value is set to false.
          - halfOrbit is a boolean variable that begins with the value false, and
            will be set to true when the orbit has gone half-way around.
        */
        double angleInitPos = Math.atan2(yInit, xInit);
        double angleInitVel = Math.atan2(uInit, vInit);

        /*
          Since the two initial angles will both be in the range (-Pi, Pi), their
          difference anglediff can be anywhere between (-2*Pi, 2*Pi). In order to
          put anglediff into the same range as the other angles, one can add or
          subtract 2*Pi to it without changing the actual location of the angle. So
          if the angle is larger than Pi, subtract 2*Pi to set it between Pi and -Pi;
          similarly if it is smaller than -Pi, add 2*Pi to set it between Pi and -Pi.
        */
        double anglediff = angleInitVel - angleInitPos;
        if (anglediff > Math.PI) anglediff -= 2 * Math.PI;
        else if (anglediff < -Math.PI) anglediff += 2 * Math.PI;
        boolean counterclockwise = (anglediff > 0);
        boolean fullOrbit = false;
        boolean halfOrbit = false;

        /*
          Now start the loop that computes the trajectory. The loop counter
          is j, which (as in EarthOrbit) starts at 1 and increases by 1 each
          step. The test for exiting from the loop will be either that the
          orbit has gone once around, or that the number of steps exceeds
          the maximum set by the user. This latter test is important because
          some orbits do not close: if the initial velocity is too large the
          trajectory simply goes off to larger and larger distances. The
          logical expression that provides the test is
                  !fullOrbit && ( j < maxSteps )
          Note the use of the logical negation operator !: !fullOrbit is true
          when fullOrbit is false, i.e. before the end of the orbit, so it
          allows the loop to continue.
        */
        for (j = 1; (!fullOrbit && (j < maxSteps)); j++) {

            /*
              - Set dv and du to the changes in x- and y-speeds that would occur
                during time dt1 if the acceleration were constant at (ax0, ay0).
              - Similarly set dx and dy to the changes in position that would
                occur if the velocity components v and u were constant during the
                time dt1.
              - Set ddx0 and ddy0 to the extra changes in x and y that occur because
                the velocity changes during the time dt1. The velocity change that
                is used is only dv/2 (or du/2, respectively) because the most
                accurate change in position comes from computing the average
                velocity during dt1. We separate the two position changes, dx and
                ddx0, because dx will be unchanged when we do the predictor-corrector
                below (the change in position due to the original speed is always
                there), while ddx0 will be modified when ax0 and hence dv is modified
                by the predictor-corrector.
              - Finally, set ddx1 and ddy1 to ddx0 and ddy0 initially. They will
                change when we enter the predictor-corrector code.
            */
            dv = ax0 * dt1;
            du = ay0 * dt1;
            dx = v * dt1;
            dy = u * dt1;
            ddx0 = dv / 2 * dt1;
            ddy0 = du / 2 * dt1;
            ddx1 = ddx0;
            ddy1 = ddy0;

            /*
              Now advance the position of the satellite by our initial estimates of
              the position changes, dx + ddx0 and dy + ddy0. Compute the radial
              distance of this new position and the acceleration there.
            */
            x1 = x0 + dx + ddx0;
            y1 = y0 + dy + ddy0;
            r = Math.sqrt(x1 * x1 + y1 * y1);
            r3 = r * r * r;
            ax1 = -kGravity * x1 / r3;
            ay1 = -kGravity * y1 / r3;

            /*
              Time-step check.
              This is the code to check whether the time-step is too large. The idea
              is to compare the changes in acceleration during the timestep with the
              acceleration itself. If the change is too large a fraction of the
              original value, then the step is likely to be too large, and the resulting
              position too inaccurate. The code below cuts the time-step dt1 in half
              and then goes back to the beginning of the loop. This is explained below.
              But first we explain the test itself.
              There is no unique test for this, nor does there need to be. If the time-step
              is cut in half the calculation will be more accurate, so generally in
              a test like this one tries to formulate the test just to make sure that
              some kind of inaccuracy is being measured. Here the test is to compute
              the absolute value of the change in the x-acceleration, ax1-ax0, and add
              that to the absolute value of the change in the y-acceleration, ay1-ay0,
              to get a measure of how big the change in acceleration is. This is then
              compared with the "original" acceleration, which is similarly measured
              by the sum of the absolute values of the components of the acceleration
              at the start of the time-steps, |ax0| + |ay0|. The comparison is
              simple: the user chooses the small number eps1, and if the changes
              are larger than eps1 times the original, then the time-step is changed.
              The test has the form of the logical comparison
                            change > eps * original
              where "change" and "original" are computed as above.
              The action that is taken is simple:
              - If the changes are too large, the time-step is cut in half (dt1 /= 2)
                and the loop index j is decreased by 1 (j--). Nothing else happens after
                this point in the loop: the rest of the code after this is inside the "else"
                clause that is executed if the change is small enough. So this pass
                through the loop ends after the statement "j--;". The reason for
                decreasing j is that the "for" statement automatically increases
                j each time, but we want j to remain the same, since we are re-doing
                the same time-step with a smaller value of dt1.
              - If the changes are sufficiently small, the "else" clause is executed
                instead. This keeps the value of dt1 the same. The "else" clause
                contains the predictor-corrector step that is described in the comments
                below.
            */
            if (Math.abs(ax1 - ax0) + Math.abs(ay1 - ay0) > eps1 * (Math.abs(ax0) + Math.abs(ay0))) {
                dt1 /= 2;
                j--;
            } else {

                /*
                  Predictor-corrector step.
                  Now that the time-step dt1 is fixed, we address the other new feature
                  of this program, which is to ensure that the position changes are
                  computed using the average velocity over the time dt1. This in turn
                  requires us to calculate the velocity change, also by averaging the
                  acceleration. But the acceleration is a function of position, so we
                  do not know how to average it until we find the final position. This
                  is a circular requirement, and cannot be solved in a single step.
                  However, it can be solved iteratively. That is, one can make a guess
                  and keep refining it.
                  The initial guess has already been made: we have computed values of
                  dx, dy, ddx0, and ddy0 from the data available at the beginning of
                  the current time-step. Recall that dx and dy depend only on the
                  velocity at the beginning of the time-step, but ddx0 and ddy0 depend
                  on the acceleration. So we will refine them, computing replacement
                  values ddx1 and ddy1 as we get better values for the acceleration at
                  the end of the time-step. The refinement is done in another loop, whose
                  counter is k below. Before enetering the loop, we define a
                  variable called testPrediction which stores a measure of how large
                  the initial guesses are, so that we can stop the iteration when the
                  refined values do not change by much.
                  The for loop is limited to at most 10 iterations. This is to prevent
                  it from getting stuck for some reason and never finishing. Ten
                  iterations should be sufficient for any reasonable problem.
                */
                testPrediction = Math.abs(ddx0) + Math.abs(ddy0);
                for (k = 0; k < 10; k++) {
                    /* compute dv and du by averaging the acceleration over dt1 */
                    dv = (ax0 + ax1) / 2 * dt1;
                    du = (ay0 + ay1) / 2 * dt1;
                    /* compute ddx1 and ddy1 by averaging the velocity change */
                    ddx1 = dv / 2 * dt1;
                    ddy1 = du / 2 * dt1;

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
                    if (Math.abs(ddx1 - ddx0) + Math.abs(ddy1 - ddy0) > eps2 * testPrediction) {
                        /* Re-define ddx0 and ddy0 to hold the values from the last iteration */
                        ddx0 = ddx1;
                        ddy0 = ddy1;
                        x1 = x0 + dx + ddx0;
                        y1 = y0 + dx + ddy0;
                        r = Math.sqrt(x1 * x1 + y1 * y1);
                        r3 = r * r * r;
                        ax1 = -kGravity * x1 / r3;
                        ay1 = -kGravity * y1 / r3;

                        /*
                          We now have the "best" acceleration values, using the most
                          recent estimates of the position at the end of the loop.
                          The next statement to be executed will be the first statement
                          of the "for" loop, finding better values of dv, du, ddx1, and
                          ddy1.
                        */
                    } else break;
                }

                /*
                  The iteration has finished, and we have sufficiently accurate
                  values of the position change in ddx1 and ddy1. Use them to get
                  final values of x and y at the end of the time-step dt1 and store
                  these into x0 and y0, ready for the next time-step. Compute all
                  the rest of the variables needed for the next time-step and for
                  possible data output.
                */
                t += dt1;
                x0 += dx + ddx1;
                y0 += dy + ddy1;
                ax0 = ax1;
                ay0 = ay1;
                v += dv;
                u += du;
                xCoordinate[j] = x0;
                yCoordinate[j] = y0;
                xVelocity[j] = v;
                yVelocity[j] = u;
                r = Math.sqrt(x0 * x0 + y0 * y0);
                potentialEnergy[j] = -kGravity / r;
                kineticEnergy[j] = 0.5 * (v * v + u * u);
                time[j] = t;

                /*
                  Now test to see if the orbit has closed, i.e. if we have gone around
                  the central mass once. We do this by computing the change in the
                  angular position of the orbit from its starting position, using the
                  same code for keeping the angular difference between -Pi and +Pi as
                  we used at the beginning of the program. This is stored in anglediff
                  as before. Once anglediff has been calculated, we enter the code
                  that tests for the completion of the orbit. It is based on an "if"
                  statement. The first part of the statement is executed if !halfOrbit is
                  "true", i.e. if at the previous step we were not yet half-way around
                  the central mass. The purpose of this part is to test the value of
                  anglediff to see if we have gone half-way by the present step. The
                  test depends on whether the orbit goes counterclockwise or not, so
                  this part of the overall "if" statement contains another "if". If
                  we are going counterclockwise, then in previous steps the value of
                  anglediff has been increasing. When it reaches Pi, we are half-way. It
                  will never exactly equal Pi, since our steps are not chosen to make
                  an integer number of divisions of the orbit, so we recognize that we
                  have gone half-way by allowing anglediff to get larger than Pi.
                  However, we know from the previous lines of code that when anglediff
                  is larger than Pi, we subtract 2*Pi, and therefore it becomes
                  negative. This is therefore the test: if anglediff is negative, we
                  know we have gone half-way, and we set the value of halfOrbit to true.
                  If the orbit were a clockwise orbit, then this is reversed: the
                  first time anglediff goes positive, we set halfOrbit to true.
                  If, however, we have already gone half-way by the time of the present
                  time-step, then the "else" clause of the overall "if" statement
                  will execute. This looks for the end of the orbit with the opposite
                  criterion to finding the half-way point. For a counterclockwise orbit,
                  when anglediff becomes positive again, we have passed through orbital
                  difference zero, which is where we started, so the orbit has finished.
                  In the clockwise case, we watch anglediff to see when it goes
                  negative. This finishes the orbit. We set the value of fullOrbit
                  to "true". This causes the overall loop around the orbit to finish.
                */
                angleNow = Math.atan2(y0, x0);
                anglediff = angleNow - angleInitPos;
                if (anglediff > Math.PI) anglediff -= 2 * Math.PI;
                else if (anglediff < -Math.PI) anglediff += 2 * Math.PI;
                if (!halfOrbit) {
                    if (counterclockwise) halfOrbit = (anglediff < 0);
                    else halfOrbit = (anglediff > 0);
                } else {
                    if (counterclockwise) fullOrbit = (anglediff > 0);
                    else fullOrbit = (anglediff < 0);
                }
            }
        }

        /*
          The orbit is finished. Now, as in previous programs, define arrays
          to contain the positions along the orbit with just the right size,
          so that no zeros are passed to the grapher. The value of j at this
          point is equal to the number of elements we need for the output arrays.
          But in this program, we must also check which output choice has been made and
          tailor the output to this choice. This includes, for some choices, multiple
          output nodes, as in EarthOrbit. First-time programmers can safely
          ignore this section.
        */

        if (outputType.equals("orbit (X,Y)")) {
            double[] finalX = new double[j];
            double[] finalY = new double[j];
            for (k = 0; k < j; k++) {
                finalX[k] = xCoordinate[k];
                finalY[k] = yCoordinate[k];
            }
            Curve out = new Curve(finalX, finalY);
            out.setTitle("Velocity of orbit");
            out.setIndependentLabels(0, "x (m)");
            out.setDependentLabels(0, "y (m)");
            output(out);
        } else if (outputType.equals("velocity space (V,U)")) {
            double[] finalV = new double[j];
            double[] finalU = new double[j];
            for (k = 0; k < j; k++) {
                finalV[k] = xVelocity[k];
                finalU[k] = yVelocity[k];
            }
            Curve out = new Curve(finalV, finalU);
            out.setTitle("Velocity of orbit");
            out.setIndependentLabels(0, "V (m/s)");
            out.setDependentLabels(0, "U (m/s)");
            output(out);
        } else if (outputType.equals("position vs. time (X,t) and (Y,t)")) {
            double[] finalX = new double[j];
            double[] finalY = new double[j];
            double[] finalT = new double[j];
            for (k = 0; k < j; k++) {
                finalX[k] = xCoordinate[k];
                finalY[k] = yCoordinate[k];
                finalT[k] = time[k];
            }
            Curve out0 = new Curve(finalT, finalX);
            out0.setTitle("x(t)");
            out0.setIndependentLabels(0, "t (s)");
            out0.setDependentLabels(0, "position (m)");
            Curve out1 = new Curve(finalT, finalY);
            out1.setTitle("y(t)");
            outputAtNode(0, out0);
            outputAtNode(1, out1);
        } else if (outputType.equals("velocity vs. time (V,t) and (U,t)")) {
            double[] finalV = new double[j];
            double[] finalU = new double[j];
            double[] finalT = new double[j];
            for (k = 0; k < j; k++) {
                finalV[k] = xVelocity[k];
                finalU[k] = yVelocity[k];
                finalT[k] = time[k];
            }
            Curve out0 = new Curve(finalT, finalV);
            out0.setTitle("V(t)");
            out0.setIndependentLabels(0, "t (s)");
            out0.setDependentLabels(0, "speed (m)");
            Curve out1 = new Curve(finalT, finalU);
            out1.setTitle("U(t)");
            outputAtNode(0, out0);
            outputAtNode(1, out1);
        } else if (outputType.equals("energy vs time")) {
            double[] finalP = new double[j];
            double[] finalK = new double[j];
            double[] finalE = new double[j];
            double[] finalT = new double[j];
            for (k = 0; k < j; k++) {
                finalP[k] = potentialEnergy[k];
                finalK[k] = kineticEnergy[k];
                finalE[k] = finalP[k] + finalK[k];
                finalT[k] = time[k];
            }
            Curve out0 = new Curve(finalT, finalP);
            out0.setTitle("Potential energy vs time");
            out0.setIndependentLabels(0, "t (s)");
            out0.setDependentLabels(0, "energy (J)");
            Curve out1 = new Curve(finalT, finalK);
            out1.setTitle("Kinetic energy vs time");
            Curve out2 = new Curve(finalT, finalE);
            out2.setTitle("Total energy vs time");
            outputAtNode(0, out0);
            outputAtNode(1, out1);
            outputAtNode(2, out2);
        }

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

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(3);

        task = getTask();

        // Initialise parameter update policy
        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Simulates the orbit of a planet around a central mass in Newtonian gravity");
        setHelpFileLocation("Orbit.html");

        // Initialise task parameters with default values (if not already initialised)
        if (!isParameter("xInit"))
            setParameter("xInit", "4.6e10");

        if (!isParameter("yInit"))
            setParameter("yInit", "0");

        if (!isParameter("vInit"))
            setParameter("vInit", "0");

        if (!isParameter("uInit"))
            setParameter("uInit", "59220.");

        if (!isParameter("M"))
            setParameter("M", "2e30");

        if (!isParameter("dt"))
            setParameter("dt", "1000");

        if (!isParameter("maxSteps"))
            setParameter("maxSteps", "10000");

        if (!isParameter("eps1"))
            setParameter("eps1", "0.05");

        if (!isParameter("eps2"))
            setParameter("eps2", "1e-4");

        if (!isParameter("outputType"))
            setParameter("outputType", "orbit (X,Y)");

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Give the initial x-coordinate of the trajectory, in meters $title xInit TextField 4.6e10\n";
        guilines += "Give the initial y-coordinate of the trajectory, in meters $title yInit TextField 0\n";
        guilines += "Give the initial x-velocity of the trajectory, in m/s $title vInit TextField 0\n";
        guilines += "Give the initial y-velocity of the trajectory, in m/s $title uInit TextField 59220.\n";
        guilines += "Give the mass of the object creating the gravitational field, in kg $title M TextField 2e30\n";
        guilines += "Give the time-step, in seconds $title dt TextField 1000\n";
        guilines += "Give the maximum number of steps in the calculation $title maxSteps TextField 10000\n";
        guilines += "Give the relative error that will force the time-step to be cut in half $title eps1 TextField 0.05\n";
        guilines += "Give the relative accuracy that the predictor-corrector aims at $title eps2 TextField 1e-4\n";
        guilines += "Select the data for output $title outputType Choice [orbit (X,Y)] [velocity space (V,U)] [position vs. time (X,t) and (Y,t)] [velocity vs. time (V,t) and (U,t)] [energy vs time]\n";
        setGUIBuilderV2Info(guilines);
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        xInit = new Double((String) getParameter("xInit")).doubleValue();
        yInit = new Double((String) getParameter("yInit")).doubleValue();
        vInit = new Double((String) getParameter("vInit")).doubleValue();
        uInit = new Double((String) getParameter("uInit")).doubleValue();
        M = new Double((String) getParameter("M")).doubleValue();
        dt = new Double((String) getParameter("dt")).doubleValue();
        maxSteps = new Integer((String) getParameter("maxSteps")).intValue();
        eps1 = new Double((String) getParameter("eps1")).doubleValue();
        eps2 = new Double((String) getParameter("eps2")).doubleValue();
        outputType = (String) getParameter("outputType");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Orbit (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("xInit"))
            xInit = new Double((String) value).doubleValue();

        if (paramname.equals("yInit"))
            yInit = new Double((String) value).doubleValue();

        if (paramname.equals("vInit"))
            vInit = new Double((String) value).doubleValue();

        if (paramname.equals("uInit"))
            uInit = new Double((String) value).doubleValue();

        if (paramname.equals("M"))
            M = new Double((String) value).doubleValue();

        if (paramname.equals("dt"))
            dt = new Double((String) value).doubleValue();

        if (paramname.equals("maxSteps"))
            maxSteps = new Integer((String) value).intValue();

        if (paramname.equals("eps1"))
            eps1 = new Double((String) value).doubleValue();

        if (paramname.equals("eps2"))
            eps2 = new Double((String) value).doubleValue();

        if (paramname.equals("outputType")) {
            outputType = (String) value;
            if (outputType.equals("orbit (X,Y)")) changeOutputNodes(1);
            else if (outputType.equals("velocity space (V,U)")) changeOutputNodes(1);
            else if (outputType.equals("position vs. time (X,t) and (Y,t)")) changeOutputNodes(2);
            else if (outputType.equals("velocity vs. time (V,t) and (U,t)")) changeOutputNodes(2);
            else if (outputType.equals("energy vs time")) changeOutputNodes(3);
        }
    }

    /*
      * Local method that changes the number of output nodes to the number given by its argument.
      * This is called when the parameter outputType is changed, to produce the right number of
      * output nodes for the requested information.
    */

    private void changeOutputNodes(int newNumber) {
        while (newNumber > task.getDataOutputNodeCount())
            try {
                task.addDataOutputNode();
            } catch (NodeException e) {
                e.printStackTrace();
            }

        while (newNumber < getTask().getDataOutputNodeCount())
            task.removeDataOutputNode(task.getDataOutputNode(task.getDataOutputNodeCount() - 1));
    }


    /**
     * @return an array of the input types for Orbit
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for Orbit
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
    }

}
