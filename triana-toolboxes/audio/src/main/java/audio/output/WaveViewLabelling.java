package audio.output;

public class WaveViewLabelling {

    /**
     * loose_label: demonstrate loose labeling of data range from min to max. (tight method is similar)
     */
    public static double[] label(double min, double max, int ticks) throws Exception {
        String str, temp;
        int nfrac;
        double d;                           /* tick mark spacing */
        double graphmin, graphmax;          /* graph range min and max */
        double range, x;

        //      System.out.println("min " + min + " : max " + max + " : ticks " + ticks);
        /* we expect min!=max */

        range = (double) nicenum(max - min, false);

        d = (double) nicenum(range / ticks, true);
        graphmin = (double) Math.floor(min / d) * d;
        graphmax = (double) Math.ceil(max / d) * d;

        // nfrac = (int)Math.max(-Math.floor(log10(d)), 0);   /* # of fractional digits to show */

//        System.out.println("NFrac " + nfrac);    


        //    sprintf(str, "%%.%df", nfrac);      /* simplest axis labels */

        //  printf("graphmin=%g graphmax=%g increment=%g\n", graphmin, graphmax, d);

        //    System.out.println("AFTER : Max = " + graphmax + " Min = " + graphmin + " D = " + d);

        int size = (int) Math.abs(((graphmax + .5 * d - graphmin) / d)) + 1;

        //      System.out.println("Size = " + size);

        double labs[] = new double[size];

        double halfd = (double) (d / (double) 2.0);

        //  System.out.println("halfd " + halfd);

        int i = 0;
        for (x = graphmin; x <= ((graphmax + halfd) * 10.0); x += d * 10) {
            labs[i] = x / (double) 10.0;
            ++i;
        }

//        System.out.println("Number of Ticks requested = " + ticks + " number got " + labs.length);
        return labs;
    }

    /**
     * nicenum: find a "nice" number approximately equal to x. Round the number if round=1, take ceiling if round=0
     */

    static double nicenum(double x, boolean round) throws Exception {
        int expv;                           /* exponent of x */
        double f;                           /* fractional part of x */
        double nf;                          /* nice, rounded fraction */

        expv = (int) Math.floor(log10(x));
        f = x / Math.pow(10., expv);              /* between 1 and 10 */
        if (round) {
            if (f < 1.5) {
                nf = 1.;
            } else if (f < 3.) {
                nf = 2.;
            } else if (f < 7.) {
                nf = 5.;
            } else {
                nf = 10.;
            }
        } else if (f <= 1.) {
            nf = 1.;
        } else if (f <= 2.) {
            nf = 2.;
        } else if (f <= 5.) {
            nf = 5.;
        } else {
            nf = 10.;
        }
        return nf * Math.pow(10., expv);
    }


    /**
     * returns the log to the base 10 of the number given to it. But if the number is zero then it returns log to the 10
     * of something very small and if the number is negative then it returns log to the base 10 of the absolute of the
     * number. What a safe log!
     */
    public static double log10(double n) throws Exception {
        if (n < 0.0) {
            n = -n;
        }

        if (n == 0.0) {
            return 0.0;
        }
        // n = Float.MIN_VALUE;

        return (Math.log(n) / Math.log(10));
    }

    public static void main(String args[]) {
        try {
            double[] labels = label(Double.parseDouble(args[0]),
                    Double.parseDouble(args[1]), Integer.parseInt(args[2]));
            for (int i = 0; i < labels.length; ++i) {
                System.out.println(labels[i]);
            }
        } catch (Exception e) {
        }
    }
}


