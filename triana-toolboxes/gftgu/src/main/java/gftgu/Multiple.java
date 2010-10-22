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
 * Compute the gravitational interaction of any number of bodies
 *
 * @author B F Schutz
 * @version $Revision: 1.6 $
 * @created 19 Jul 2003
 * @date $Date: 2007/01/23 12:36:53 $ modified by $Author: spxmss $
 * @todo
 */
public class Multiple extends Unit {

    // parameter data type definitions

    /*
      nBodies is the value given in the parameter window for the
      number of bodies whose gravitational interaction is
      being computed.
      The variables massLength, posLength, and velLength are set when
      the parameters are read; they give the number of items in each of
      the parameter input Strings typed by the user. They should all
      equal nBodies or there is an inconsistency.
      */
    private int nBodies;
    private int massLength, posLength, velLength;

    /*
      masses is a String defined by the user in the parameter window,
      which holds the masses of all the bodies, in solar masses. It
      consists of a sequence of numbers separated by spaces.
      m is an array that holds the values of the masses, after they
      are extracted from the String masses.
      */
    private String masses;
    private double[] m;

    /*
      initPos is a String defined by the user in the parameter window,
      which holds the initial positions of all the bodies. It consists
      of a sequence of pairs of the form (x,y), one for each body. The
      pairs can be separated by spaces or any other symbol, or nothing
      at all. Spaces within the pair are also allowed. The user should
      ensure that the number of pairs equals nBodies.
      xInit and yInit are arrays that hold the initial x-positions and
      y-positions of all the bodies, after they are extracted from initPos.
      */
    private String initPos;
    private double[] xInit, yInit, zInit;


    /*
      initVel is a String defined by the user in the parameter window,
      which holds the initial velocities of all the bodies. It consists
      of a sequence of pairs of the form (v,u), one for each body. The
      pairs can be separated by spaces or any other symbol, or nothing
      at all. Spaces within the pair are also allowed. The user should
      ensure that the number of pairs equals nBodies.
      vInit and uInit are arrays that hold the initial x-component of
      the velocity and the initial y-component, for all bodies, after
      they are extracted from initPos.
      */
    private String initVel;
    private double[] vInit, uInit, wInit;

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
      outSteps fixes the number of time-steps between output events.
      This allows the program to run for a long time without using up
      memory storing all steps at once.
      outputType allows the user to choose to output a view of the
      trajectories of the bodies since the previous output, or just
      output the current positions of the bodies. If outSteps is
      chosen to be small, then choosing to view the current positions
      will produce a kind of video of the simulation.
    */
    private int outSteps;
    private String outputType;

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
    * Called whenever there is data for the unit to process
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
          - A, B are integers that will always serve as indices that refer to the
            different bodies. Thus, m[A] is the mass of body A, in solar masses.
          - dt1 will be used as the "working" value of the time-step, which can
            be changed during the calculation. Using dt1 for the time-step allows
            us to keep dt as the original value, as specified by the user. Thus,
            dt1 is set equal to dt at the beginning of the calculation, but it may be
            reduced at any time-step, if accuracy requires it.
          - x, y and z are arrays containing the x-, y-, and z-coordinates (respectively)
            of the positions of the bodies, given here their initial values.
          - v, u, and w are arrys containing the x-, y-, and z-components (respectively)
            of the velocities of the bodies, given here their initial values.
          - ax, ay, and az are arrays containing the x-, y-, and z-components (respectively)
            of the accelerations of the bodies. Their initial values are computed in the
            double "for" loop described below.
        */
        double t = 0;
        double dt1 = dt;
        int stepsSinceOutput = 1;
        int A, B;
        double[] x = new double[nBodies];
        double[] y = new double[nBodies];
        double[] z = new double[nBodies];
        double[] v = new double[nBodies];
        double[] u = new double[nBodies];
        double[] w = new double[nBodies];
        for (A = 0; A < nBodies; A++) {
            x[A] = xInit[A];
            y[A] = yInit[A];
            z[A] = zInit[A];
            v[A] = vInit[A];
            u[A] = uInit[A];
            w[A] = wInit[A];
        }
        double[] ax = new double[nBodies];
        double[] ay = new double[nBodies];
        double[] az = new double[nBodies];

        /*
          The next statements define variables that are used in the double "for"
          loop that starts on the third line, and whose purpose is to compute
          the initial accelerations of the bodies. This double loop is repeated
          in the program below wherever the accelerations have to be computed.
          Therefore we describe it in detail here.
          Here are the variables that we need. They are defined here and will be
          re-used with the same meanings wherever we need to compute the accelerations.
          - xAB, yAB, zAB are the x-, y-, and z-components (respectively) of the displacement
            vector from body B to body A.
          - rAB is the distance between body A and body B.
          - rAB3 is the cube of the distance between body A and body B.
          - kxAB, kyAB, and kzAB are variables that hold temporary values used in the
            calculation of the accelerations.
          Here is how the double loop works. The acceleration of each body depends on
          all the others but not on itself. The acceleration of body A is just the
          sum of the accelerations produced on it by all the other bodies. The
          acceleration produced on body A by body B is (as in previous programs
          like MercPert) the product of the gravitational constant G times the
          mass of B (not of A) times the displacement vector from A to B, divided
          by the cube of the total distance between A and B. Since we store the
          mass of the bodies in solar masses in the array m[], we have put the mass
          of the Sun into the constant kGravity = G * Msun. Thus, the x-acceleration
          produced on A by B is -kGravity*m[B]*xAB/rAB3. (The minus sign is needed
          because we have defined xAB to be the x-component of the displacement vector
          from B to A.)
          Not only does B accelerate A, but also A accelerates B. In fact, the
          x-acceleration produced on body B by body A is +kGravity*m[A]*xAB/rAB3. Here the
          sign is different because the acceleration points in the opposite direction.
          It follows that to compute all these accelerations efficiently, we should
          first compute the quantities kGravity*xAB/rAB3 (and similarly for y and z)
          for each PAIR of bodies (A,B), and then multiply them by the appropriate masses
          to get the contributions to the various accelerations. These contributions simply
          add up for each body to get the result.
          The double loop is designed to do the computations for each pair of bodies.
          The first index is A and it runs over all the bodies. But for each value of A,
          there is another loop with the index B that runs from A+1 to the maximum. This
          ensures that each pair (A,B) occurs only once in these loops, and that is where
          the first index is the smaller of the two. Thus, for three bodies with indices
          0, 1, and 2, the loops compute only for the values (0,1), (0,2), (1,2).
          For each pair the loop computes:
          - the components xAB, yAB, and zAB;
          - the distance rAB and its cube rAB3;
          - the quantity kxAB = kGravity*xAB/rAB3 that we noted above was common to the
            accelerations of both A and B, and similarly kyAB and kzAB; and
          - the x-, y-, and x-contributions to the acceleration of A by B and the
            corresponding contributions to the acceleration of B by A. Note that these
            are just added to the array variables holding the total accelerations: ax,
            ay, and az. When these variables were defined in the three lines just before
            this comment, Java automatically initialized their values to zero. So they
            go into the double loop with zero values, and come out with the sums of all
            the contributions.
        */
        double xAB, yAB, zAB, rAB, rAB3, kxAB, kyAB, kzAB;
        for (A = 0; A < nBodies; A++)
            for (B = A + 1; B < nBodies; B++) {
                xAB = x[A] - x[B];
                yAB = y[A] - y[B];
                zAB = z[A] - z[B];
                rAB = Math.sqrt(xAB * xAB + yAB * yAB + zAB * zAB);
                rAB3 = rAB * rAB * rAB;
                kxAB = -kGravity * xAB / rAB3;
                kyAB = -kGravity * yAB / rAB3;
                kzAB = -kGravity * zAB / rAB3;
                ax[A] += kxAB * m[B];
                ax[B] += -kxAB * m[A];
                ay[A] += kyAB * m[B];
                ay[B] += -kyAB * m[A];
                az[A] += kzAB * m[B];
                az[B] += -kzAB * m[A];
            }

        /*
        - xCoordinate, yCoordinate, and zCoordinate are two-dimensional arrays that are
          used to store the values of x, y, and z of the different bodies at each timestep.
          Their first index is the body, their second the timestep. Thus, xCoordinate[5][2]
          will hold the value of the x-coordinate of body 5 at timestep 2.

          The size of the arrays depends on the output type chosen by the user. If the user
          selects to have trajectories output, then the second index takes as many values
          as will be output at one time, which is the value of the parameter outSteps. If
          the user selects to output current positions, then the second index has only a
          single value of 0, and the arrays will be filled only at the last step before
          output.

          Store the initial values of the position components in the appropriate elements
          of these arrays. If current positions are being output, output the initial
          positions.
        */
        double[][] xCoordinate, yCoordinate, zCoordinate;
        if (outputType.equals("trajectories")) {
            xCoordinate = new double[nBodies][outSteps];
            yCoordinate = new double[nBodies][outSteps];
            zCoordinate = new double[nBodies][outSteps];
        } else {
            xCoordinate = new double[nBodies][1];
            yCoordinate = new double[nBodies][1];
            zCoordinate = new double[nBodies][1];
        }
        for (A = 0; A < nBodies; A++) {
            xCoordinate[A][0] = xInit[A];
            yCoordinate[A][0] = yInit[A];
            zCoordinate[A][0] = zInit[A];
        }
        if (outputType.equals("current positions")) doOutput(xCoordinate, yCoordinate, zCoordinate);

        /*
          Now define other variables that will be needed, but without giving
          initial values. They will be assigned values during the calculation.
          - x1, y1, and z1 are temporary arrays holding values of x, y, and z
            (respectively) for each body that are needed during the calculation.
          - ax1, ay1, and az1 are likewise temporary arrays of the acceleration.
          - dv, du, and dw are arrays holding changes in the velocity components
            that occur during a timestep.
          - dx, dy, and dz are arrays that hold part of the changes in the position
            variables x, y, and z (respectively) for each body, that occur during a time-step.
          - ddx0, ddy0, ddz0, ddx1, ddy1, ddz1 are arrays that hold other parts of
            the changes in x, y, and z of the bodies during a time-step. The reason for having
            both dx and ddx will be explained in comments on the calculation below.
          - testPrediction is an array that will hold a value for each body that is used
            by the predictor-corrector steps to assess how accurately the calculation is proceeding.
          - j and k are integers that will be used as loop counters.
        */
        double[] x1 = new double[nBodies];
        double[] y1 = new double[nBodies];
        double[] z1 = new double[nBodies];
        double[] ax1 = new double[nBodies];
        double[] ay1 = new double[nBodies];
        double[] az1 = new double[nBodies];
        double[] dv = new double[nBodies];
        double[] du = new double[nBodies];
        double[] dw = new double[nBodies];
        double[] dx = new double[nBodies];
        double[] dy = new double[nBodies];
        double[] dz = new double[nBodies];
        double[] ddx0 = new double[nBodies];
        double[] ddy0 = new double[nBodies];
        double[] ddz0 = new double[nBodies];
        double[] ddx1 = new double[nBodies];
        double[] ddy1 = new double[nBodies];
        double[] ddz1 = new double[nBodies];
        double[] testPrediction = new double[nBodies];
        int j, k;

        /*
          Now start the loop that computes the two orbits. The loop counter
          is j, which (as in Orbit) starts at 1 and increases by 1 each
          step. The test for exiting from the loop will be that the number
          of steps exceeds the maximum set by the user.
        */
        for (j = 1; j < maxSteps; j++) {

            /*
              For each body, labeled by the index A;
              - Set dv[A], du[A], and dw[A] to the changes in x-, y-, and z-speeds
                that would occur during time dt1 if the acceleration were constant at
                (ax[A], ay[A], az[A]).
              - Similarly set dx[A], dy[A], and dz[A] to the changes in position that would
                occur if the velocity components v[A], u[A], and w[A] were constant during the
                time dt1.
              - Set ddx0[A], ddy0[A], and ddz0[A] to the extra changes in x, y, and z that occur
                because body A's velocity changes during the time dt1. The velocity change that
                is used is only (dv[A]/2, du[A]/2, dw[A]/2) because the most accurate change
                in position comes from computing the average velocity during dt1. We separate
                the two position changes, dx[A] and ddx0[A], because dx[A] will be unchanged
                when we do the predictor-corrector below (the change in position due to the
                original speed is always there), while ddx0[A] will be modified when ax[A] and
                hence dv[A] is modified by the predictor-corrector. Similar remarks apply,
                of course, to the y- and z-directions.
              - Set ddx1[A], ddy1[A], and ddyz1[A] to ddx0[A], ddy0[A], and ddz0[A]
                initially. They will change when we enter the predictor-corrector code.
              - Finally advance the positions of the bodies by our initial estimates of the
                position changes, for example dx[A] + ddx0[A], and store that in x1[A].
            */
            for (A = 0; A < nBodies; A++) {
                dv[A] = ax[A] * dt1;
                du[A] = ay[A] * dt1;
                dw[A] = az[A] * dt1;
                dx[A] = v[A] * dt1;
                dy[A] = u[A] * dt1;
                dz[A] = w[A] * dt1;
                ddx0[A] = dv[A] / 2 * dt1;
                ddy0[A] = du[A] / 2 * dt1;
                ddz0[A] = dw[A] / 2 * dt1;
                ddx1[A] = ddx0[A];
                ddy1[A] = ddy0[A];
                ddz1[A] = ddz0[A];
                x1[A] = x[A] + dx[A] + ddx0[A];
                y1[A] = y[A] + dy[A] + ddy0[A];
                z1[A] = z[A] + dz[A] + ddz0[A];
                ax1[A] = 0.0;
                ay1[A] = 0.0;
                az1[A] = 0.0;
            }

            /*
              Now compute the new distances of the bodies from one another and the
              resulting acceleration at the first-guess positions of the bodies at
              the time t1. Store the acceleration components in the arrays ax1, ay1,
              and az1. This is a repeat of the loop used to compute the initial
              accelerations, above.
            */
            for (A = 0; A < nBodies; A++)
                for (B = A + 1; B < nBodies; B++) {
                    xAB = x1[A] - x1[B];
                    yAB = y1[A] - y1[B];
                    zAB = z1[A] - z1[B];
                    rAB = Math.sqrt(xAB * xAB + yAB * yAB + zAB * zAB);
                    rAB3 = rAB * rAB * rAB;
                    kxAB = -kGravity * xAB / rAB3;
                    kyAB = -kGravity * yAB / rAB3;
                    kzAB = -kGravity * zAB / rAB3;
                    ax1[A] += kxAB * m[B];
                    ax1[B] += -kxAB * m[A];
                    ay1[A] += kyAB * m[B];
                    ay1[B] += -kyAB * m[A];
                    az1[A] += kzAB * m[B];
                    az1[B] += -kzAB * m[A];
                }

            /*
              Time-step check.
              This is the code to check whether the time-step is too large. The idea
              is to compare the changes in acceleration of each body during the timestep
              with the acceleration of the body itself. If the change for any body is too
              large a fraction of the original value, then the step is likely to be
              too large, and the resulting position too inaccurate. The code below cuts
              the time-step dt1 in half and then goes back to the beginning of the loop.
              The boolean (true-false) variable tooLarge is used to keep track of the result
              of testing all the different bodies. If any one body fails the test, then
              tooLarge is set to "true" and the calculation is repeated. If the loop over
              the bodies exits with tooLarge still equal to "false" then we go on to the
              predictor-corrector. Notice that the loop over the bodies has a more complicated
              test for repeating: it repeats only if A is less than nBodies (ie there is another
              body to test) AND if tooLarge is equal to "false" (so that !tooLarge is equal
              to "true"). Once any body fails the test and tooLarge is set to "true", there is
              no point in testing the remaining bodies, so the loop exits. The single statement
              inside the loop sets the value of tooLarge equal to a boolean expression: the
              statement inside the outer () is a comparison, so it evalues either to "true" or
              to "false", and this value is assigned to tooLarge.
            */
            boolean tooLarge = false;
            for (A = 0; (A < nBodies) && !tooLarge; A++) {
                tooLarge = (Math.abs(ax1[A] - ax[A]) + Math.abs(ay1[A] - ay[A]) + Math.abs(az1[A] - az[A]) > eps1 * (Math.abs(ax[A]) + Math.abs(ay[A])) + Math.abs(az[A]));
            }
            if (tooLarge) {
                dt1 /= 2;
                j--;
            } else {

                /*
                  Predictor-corrector step. This is explained in program Orbit.
                */
                for (A = 0; A < nBodies; A++) {
                    testPrediction[A] = Math.abs(ddx0[A]) + Math.abs(ddy0[A]) + Math.abs(ddz0[A]);
                }
                for (k = 0; k < 10; k++) {
                    for (A = 0; A < nBodies; A++) {
                        /* compute dv[A], du[A], and dw[A] by averaging the acceleration over dt1 */
                        dv[A] = (ax[A] + ax1[A]) / 2 * dt1;
                        du[A] = (ay[A] + ay1[A]) / 2 * dt1;
                        dw[A] = (az[A] + az1[A]) / 2 * dt1;
                        /* compute ddx1[A], ddy1[A], and ddz1[A] by averaging the velocity change */
                        ddx1[A] = dv[A] / 2 * dt1;
                        ddy1[A] = du[A] / 2 * dt1;
                        ddz1[A] = dw[A] / 2 * dt1;
                    }
                    /*
                      Test the changes in ddx, ddy, and ddz since the last iteration for each .
                      body A. If it is more than a fraction eps2 of the original, then
                      set the variable tooLarge to "true", as in the test for the time-step above.
                      Then, after all bodies are examined, if tooLarge is "true", the values of
                      ddx, ddy, and ddz for each body have to be re-computed by finding the acceleration
                      components at the refined positions.
                      On the other hand, if tooLarge is still false after examining all the bodies,
                      then the position change is small enough, and the "else:" clause is
                      executed, which exits from the for loop using the statement
                      "break". This finishes the iteration and goes on to wrap up
                      the calculation.
                    */
                    tooLarge = false;
                    for (A = 0; (A < nBodies) && !tooLarge; A++) {
                        tooLarge = (Math.abs(ddx1[A] - ddx0[A]) + Math.abs(ddy1[A] - ddy0[A]) + Math.abs(ddz1[A] - ddz0[A]) > eps2 * testPrediction[A]);
                    }
                    if (tooLarge) {
                        /*
                          Re-define ddx0[A], ddy0[A] and ddz0[A] to hold the values
                          from the last iteration. Then get the acceleration values again.
                        */
                        for (A = 0; A < nBodies; A++) {
                            ddx0[A] = ddx1[A];
                            ddy0[A] = ddy1[A];
                            ddz0[A] = ddz1[A];
                            x1[A] = x[A] + dx[A] + ddx0[A];
                            y1[A] = y[A] + dy[A] + ddy0[A];
                            z1[A] = z[A] + dz[A] + ddz0[A];
                            ax1[A] = 0.0;
                            ay1[A] = 0.0;
                            az1[A] = 0.0;
                        }
                        for (A = 0; A < nBodies; A++)
                            for (B = A + 1; B < nBodies; B++) {
                                xAB = x1[A] - x1[B];
                                yAB = y1[A] - y1[B];
                                zAB = z1[A] - z1[B];
                                rAB = Math.sqrt(xAB * xAB + yAB * yAB + zAB * zAB);
                                rAB3 = rAB * rAB * rAB;
                                kxAB = -kGravity * xAB / rAB3;
                                kyAB = -kGravity * yAB / rAB3;
                                kzAB = -kGravity * zAB / rAB3;
                                ax1[A] += kxAB * m[B];
                                ax1[B] += -kxAB * m[A];
                                ay1[A] += kyAB * m[B];
                                ay1[B] += -kyAB * m[A];
                                az1[A] += kzAB * m[B];
                                az1[B] += -kzAB * m[A];
                            }

                        /*
                          We now have the "best" acceleration values, using the most
                          recent estimates of the position at the end of the loop on k.
                          The next statement to be executed will be the first statement
                          of the "for" loop, finding better values of dv, du, dw, ddx1,
                          ddy1, and ddz1 for each body.
                        */
                    } else break;
                }

                /*
                  The iteration has finished, and we have sufficiently accurate
                  values of the position change in arrays ddx1, ddy1, and ddz1.
                  Use them to get final values of arrays x, y and z at the end of
                  the time-step dt1, ready for the next time-step. Compute all the
                  rest of the variables needed for the next time-step and for
                  data output.
                */
                t += dt1;
                for (A = 0; A < nBodies; A++) {
                    x[A] += dx[A] + ddx1[A];
                    y[A] += dy[A] + ddy1[A];
                    z[A] += dz[A] + ddz1[A];
                    ax[A] = ax1[A];
                    ay[A] = ay1[A];
                    az[A] = az1[A];
                    v[A] += dv[A];
                    u[A] += du[A];
                    w[A] += dw[A];
                }

                /*
                  Now fill the output arrays if appropriate.
                */
                if (outputType.equals("trajectories")) for (A = 0; A < nBodies; A++) {
                    xCoordinate[A][stepsSinceOutput] = x[A];
                    yCoordinate[A][stepsSinceOutput] = y[A];
                    zCoordinate[A][stepsSinceOutput] = z[A];
                }
                else if (stepsSinceOutput == outSteps - 1) for (A = 0; A < nBodies; A++) {
                    xCoordinate[A][0] = x[A];
                    yCoordinate[A][0] = y[A];
                    zCoordinate[A][0] = z[A];
                }
            }

            /*
              If the number of steps since the last output equals outSteps, then we do
              the output and reset the value of the counter stepsSinceOutput. If not,
              then increment the counter and go on to the next time-step.
            */
            if (stepsSinceOutput == outSteps - 1) {
                doOutput(xCoordinate, yCoordinate, zCoordinate);
                stepsSinceOutput = 0;
            } else stepsSinceOutput++;
        }

    }
    /*
      End of program and user code. The remaining code takes care of
      Triana system matters.
    */

    /*
      This method outputs the position data for
      all the bodies. See the remarks in MercPert about multiple output
      nodes. Here we output one Curve for each body, so we need the number of
      nodes to equal the number of bodies. The unit should have adjusted the
      number of nodes automatically when nBodies was defined.
      In order to understand the following statements, you need to understand
      the way Java stores arrays with more than one index. It treats them as
      collections of one-dimensional arrays. In the case of a 2D array, the first
      index says which one-dimensional array one is dealing with, and the second
      index runs along that array. In this case we are dealing with the array
      x[][]. Because the first index is the body index A and the second
      is the time-step index, this array is stored as nBodies separate one-dimensional
      arrays, each of length maxSteps. (If you are familiar with the computer language
      C, then you may already know about this: it is the same in C as in Java.)
      Now, we don't normally need to know about this, since when we write
      x[5][2] Java takes care of finding out where the value of this is located
      in the computer's memory. However, there is one circumstance where knowing how Java
      stores such arrays is useful. That is because Java permits us to refer to each of
      the one-dimensional arrays by its index. Thus, x[5] refers to the
      one-dimensional array of length either outSteps or 1 (depending on the output type
      that the user had chosen) that gives the values of the x-coordinates
      of body 5 at the appropriate time-steps. This makes copying from the internal
      storage (x,y,z) to the output arrays (xout, yout, zout) simple, using the
      efficient System.arraycopy utility supplied by Java.
    */

    private void doOutput(double[][] x, double[][] y, double[][] z) {
        Curve out;
        int len = x[0].length;
        double[] xout, yout, zout;
        for (int A = 0; A < nBodies; A++) {
            xout = new double[len];
            yout = new double[len];
            zout = new double[len];
            System.arraycopy(x[A], 0, xout, 0, len);
            System.arraycopy(y[A], 0, yout, 0, len);
            System.arraycopy(z[A], 0, zout, 0, len);
            out = new Curve(xout, yout, zout);
            out.setTitle("Body " + String.valueOf(A));
            if (A == 0) {  // the grapher reads the axis labels from the first input
                out.setIndependentLabels(0, "x (m)");
                out.setDependentLabels(0, "y (m)");
            }
            outputAtNode(A, out);
        }
    }

    /*
      Local method to convert input Strings for masses, positions and
      velocities into double values in the appropriate variables. If
      the information is for masses, then the parsing is similar to
      that in the same method of Binary. Otherwise, the method
      has to parse the sequence of brackets. It is a forgiving parser:
      it requires the "(..)" and the "," inside the round brackets,
      but it does not matter about spaces and about what is between
      one closing ")" and the next opening "(". It stores the
      number of sets of values it finds in the variables massLength,
      posLength and velLength; these should be checked against each
      other and the current value of nBodies for consistency.
    */

    private void decodeParamString(String inputValue, String type) {
        String[] parts, pieces;
        int len, j;
        if (type.equals("mass")) {
            parts = inputValue.trim().split("\\s+");
            len = parts.length;
            m = new double[len];
            for (j = 0; j < len; j++) m[j] = Double.parseDouble(parts[j]);
            massLength = len;
        } else {
            parts = inputValue.trim().split("\\)", -1); // -1 to ensure that last element not dropped.
            len = parts.length - 1;   // -1 because last element is whatever follows last ")"
            double[] firstValues = new double[len];
            double[] secondValues = new double[len];
            double[] thirdValues = new double[len];
            for (j = 0; j < len; j++) {
                pieces = parts[j].trim().split(",", -1);
                firstValues[j] = Double.parseDouble(pieces[0].substring((pieces[0].indexOf("(") + 1)));
                secondValues[j] = Double.parseDouble(pieces[1]);
                thirdValues[j] = Double.parseDouble(pieces[2]);
            }
            if (type.equals("position")) {
                xInit = firstValues;
                yInit = secondValues;
                zInit = thirdValues;
                posLength = len;
            } else if (type.equals("velocity")) {
                vInit = firstValues;
                uInit = secondValues;
                wInit = thirdValues;
                velLength = len;
            }
        }
    }


    /*
      This method adjusts the number of output nodes to equal nBodies. It is
      called each time any of the parameters nBodies, masses, initVel, or
      initPos is changed, since changing any of these can change nBodies via
      the previous method.
    */

    private void adjustOutputNodes() {
        Task task = getTask();
        while (nBodies > task.getDataOutputNodeCount())
            try {
                task.addDataOutputNode();
            } catch (NodeException e) {
                e.printStackTrace();
            }
        while (nBodies < task.getDataOutputNodeCount())
            task.removeDataOutputNode(task.getDataOutputNode(task.getDataOutputNodeCount() - 1));
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

        setDefaultOutputNodes(3);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(IMMEDIATE_UPDATE);

        // Initialise pop-up description and help file location
        setPopUpDescription("Compute the gravitational interaction of any number of bodies");
        setHelpFileLocation("Multiple.html");

        // Define initial value and type of parameters
        defineParameter("nBodies", "3", USER_ACCESSIBLE);
        defineParameter("masses", "3 2 1", USER_ACCESSIBLE);
        defineParameter("initPos", "(4.6e10, 0, 0) (-4.6e10, 0, 0) (0, 4.6e10, 0)", USER_ACCESSIBLE);
        defineParameter("initVel", "(2e3, 1.3e4, 0) (3.5e3, -1.95e4, 0) (-1.3e4, 0, 0)", USER_ACCESSIBLE);
        defineParameter("dt", "2000", USER_ACCESSIBLE);
        defineParameter("maxSteps", "15000", USER_ACCESSIBLE);
        defineParameter("outSteps", "15000", USER_ACCESSIBLE);
        defineParameter("outputType", "trajectories", USER_ACCESSIBLE);
        defineParameter("eps1", "0.05", USER_ACCESSIBLE);
        defineParameter("eps2", "1e-4", USER_ACCESSIBLE);

        // Initialise GUI builder interface
        String guilines = "";
        guilines += "Number of bodies $title nBodies TextField 3\n";
        guilines += "Give the masses of all the bodies, in solar masses, as: m1 m2 ..  $title masses TextField 3 2 1\n";
        guilines += "Give the initial 3D positions of all the bodies (in m), as: (x1, y1, z1) (x2, y2, z2) .. $title initPos TextField (4.6e10, 0) (-4.6e10, 0) (0, 4.6e10)\n";
        guilines += "Give the initial 3D velocities of all the bodies (in m/s) as: (v1, u1, w1) (v2, u2, w2) .. $title initVel TextField (2e3, 1.3e4) (3.5e3, -1.95e4) (-1.3e4, 0)\n";
        guilines += "Give the time-step, in seconds $title dt TextField 2000\n";
        guilines += "Give the maximum number of steps in the calculation $title maxSteps TextField 15000\n";
        guilines += "Give the number of steps between output of intermediate results $title outSteps TextField 15000\n";
        guilines += "Select the type of output $title outputType Choice [trajectories] [current positions]\n";
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
        nBodies = new Integer((String) getParameter("nBodies")).intValue();
        masses = (String) getParameter("masses");
        initPos = (String) getParameter("initPos");
        initVel = (String) getParameter("initVel");
        dt = new Double((String) getParameter("dt")).doubleValue();
        maxSteps = new Integer((String) getParameter("maxSteps")).intValue();
        eps1 = new Double((String) getParameter("eps1")).doubleValue();
        eps2 = new Double((String) getParameter("eps2")).doubleValue();
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up Multiple (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("nBodies")) {
            nBodies = new Integer((String) value).intValue();
            adjustOutputNodes();
        }

        if (paramname.equals("masses")) {
            masses = (String) value;
            decodeParamString(masses, "mass");
        }

        if (paramname.equals("initPos")) {
            initPos = (String) value;
            decodeParamString(initPos, "position");
        }

        if (paramname.equals("initVel")) {
            initVel = (String) value;
            decodeParamString(initVel, "velocity");
        }

        if (paramname.equals("dt"))
            dt = new Double((String) value).doubleValue();

        if (paramname.equals("maxSteps"))
            maxSteps = new Integer((String) value).intValue();

        if (paramname.equals("outSteps"))
            outSteps = new Integer((String) value).intValue();

        if (paramname.equals("outputType"))
            outputType = (String) value;

        if (paramname.equals("eps1"))
            eps1 = new Double((String) value).doubleValue();

        if (paramname.equals("eps2"))
            eps2 = new Double((String) value).doubleValue();

    }


    /**
     * @return an array of the input types for Multiple
     */
    public String[] getInputTypes() {
        return new String[]{};
    }

    /**
     * @return an array of the output types for Multiple
     */
    public String[] getOutputTypes() {
        return new String[]{"triana.types.Curve"};
    }

}
