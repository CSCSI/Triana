package signalproc.algorithms;


import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: spxrpd Date: 02-Jun-2003 Time: 10:36:31 To change this template use Options | File
 * Templates.
 */
public class FFTWLibrary {

    jfftw.complex.Plan complexPlan;
    jfftw.complex.nd.Plan complexNdPlan;
    jfftw.real.Plan realPlan;
    jfftw.real.nd.Plan realNdPlan;

    public FFTWLibrary() {
    }
//Craig change all ESTIMATE to READONLY

    /**
     * @param dim
     * @param forward - perform a forward transform or not
     * @return
     */
    public jfftw.real.Plan realPlan(int dim, boolean forward, int type) {
        jfftw_real_Plan();

        if (forward) {
            realPlan.setPlan(dim, jfftw.Plan.FORWARD, type);

        } else {
            realPlan.setPlan(dim, jfftw.Plan.BACKWARD, type);
        }
        return realPlan;
    }

    public jfftw.real.Plan realPlanFW(int dim, int type) {
        jfftw_real_Plan();
        realPlan.setPlan(dim, jfftw.Plan.FORWARD, type);
        return realPlan;
    }

    public jfftw.real.Plan realPlanREV(int rdim, int idim, int type) {
        jfftw_real_Plan();
        if (isOdd(rdim)) {
            realPlan.setPlan(rdim + idim - 2, jfftw.Plan.FORWARD, type);
        } else {
            realPlan.setPlan(rdim + idim - 1, jfftw.Plan.FORWARD, jfftw.Plan.ESTIMATE);
        }
        return realPlan;
    }

    public ArrayList realTransformFW(double[] data) {
        double[] dbl = realPlan.transform(data);
        //return separate(dbl);
        //return separate2(dbl,false);
        return separate3(dbl);
    }

    public ArrayList realTransformREV(double[] real, double[] imag) {
        double[] data = merge4(real, imag);
        double[] dbl = realPlan.transform(data);
        ArrayList al = new ArrayList();
        al.add(dbl);
        al.add(new double[dbl.length]);
        return al;
    }

    public ArrayList realTransform(double[] data, boolean forward) {
        //if its fw then the output is complex, if its bw then its real

        double[] dbl = realPlan.transform(data);
        /*ArrayList al = new ArrayList();
        al.add(dbl);
        al.add(new double[dbl.length]);*/
        //return al;

        if (forward) {
            //return separate(dbl);
            //return separate2(dbl,false);
            return separate3(dbl);
        } else {
            ArrayList al = new ArrayList();
            al.add(dbl);
            al.add(new double[dbl.length]);
            return al;
        }
    }

    public ArrayList realTransform(double[] real, double[] imag, boolean forward) {
        //if its fw then the output is complex, if its bw then its real

        double[] data = FFTWLibrary.merge(real, imag);

        double[] dbl = realPlan.transform(data);
        /*ArrayList al = new ArrayList();
        al.add(dbl);
        al.add(new double[dbl.length]);*/
        //return al;

        if (forward) {
            return separate(dbl);
        } else {
            ArrayList al = new ArrayList();
            al.add(dbl);
            al.add(new double[dbl.length]);
            return al;
        }
    }

    public void jfftw_real_Plan() {

        realPlan = new jfftw.real.Plan();
        //Class rp = loadClass(toolboxPath, "jfftw.real.Plan");
        //realPlan = (jfftw.real.Plan) instanciateNativeLibrary(rp);

    }

    public jfftw.real.nd.Plan realNdPlan(int[] dim, boolean forward, int type) {
        jfftw_real_n_Plan();

        if (forward) {
            realNdPlan.setPlan(dim, jfftw.Plan.FORWARD, type);
        } else {
            realNdPlan.setPlan(dim, jfftw.Plan.BACKWARD, type);
        }

        return realNdPlan;

    }

    public ArrayList realNdTransform(double[] data, boolean forward) {

        /*String out1 = new String();
        for (int i=0; i<data.length; i++) {
            out1 = out1.concat(data[i]+"\n");
        }
        FileUtils.writeToFile("/home/spxrpd/fftw_raw_pre_output.txt",out1);*/

        double[] dbl = realNdPlan.transform(data);

        /*String out = new String();
        for (int i=0; i<dbl.length; i++) {
            out = out.concat(dbl[i]+"\n");
        }
        FileUtils.writeToFile("/home/spxrpd/fftw_raw_output.txt",out);*/

        //if its fw then the output is complex, if its bw then its real

        //if (forward) {
        //    return FFTWLibrary.separate(dbl);
        //} else {
        ArrayList al = new ArrayList();
        al.add(dbl);
        al.add(new double[dbl.length]);
        return al;
        //}


    }

    public void jfftw_real_n_Plan() {

        realNdPlan = new jfftw.real.nd.Plan();

    }

    public jfftw.complex.Plan complexPlan(int dim, boolean forward, int type) {
        jfftw_complex_Plan();
        if (forward) {
            complexPlan.setPlan(dim, jfftw.Plan.FORWARD, type);
        } else {
            complexPlan.setPlan(dim, jfftw.Plan.BACKWARD, type);
        }

        return complexPlan;

    }

    public ArrayList complexTransform(double[] real, double[] imag) {
        //if its fw then the output is complex, if its bw then its complex
        double[] dbl = complexPlan.transform(merge(real, imag));
        return separate(dbl);
    }

    public void jfftw_complex_Plan() {

        complexPlan = new jfftw.complex.Plan();
    }

    public jfftw.complex.nd.Plan complexNdPlan(int[] dim, boolean forward, int type) {
        jfftw_complex_n_Plan();
        if (forward) {
            complexNdPlan.setPlan(dim, jfftw.Plan.FORWARD, type);
        } else {
            complexNdPlan.setPlan(dim, jfftw.Plan.BACKWARD, type);
        }

        return complexNdPlan;

    }

    public ArrayList complexNdFWTransform(double[] real, double[] imag) {
        //if its fw then the output is complex, if its bw then its complex
        double[] dbl = complexNdPlan.transform(merge(real, imag));
        return separate(dbl);
    }

    public void jfftw_complex_n_Plan() {

        complexNdPlan = new jfftw.complex.nd.Plan();

    }

    //merge real and imag array into fftw double array with the in one array

    public static double[] merge(double[] real, double[] imag) {
        int length = real.length + imag.length;
        double[] dbl = new double[length];
        //double[] dblrev = new double[imag.length];
        /*for (int i=0; i<imag.length; i++) {
            dblrev[i] = imag[imag.length-1-i];
        }*/
        System.out.println("Real Length : " + real.length);
        System.out.println("Imag Length : " + imag.length);
        for (int i = 0; i < real.length; i++) {
            dbl[2 * i] = real[i];
            dbl[/*length-i-1*/(2 * i) + 1] = imag[i];
        }
        return dbl;
    }

    //merge real and imag array into fftw double array with the in one array

    public static double[] merge2(double[] real, double[] imag) {

        boolean odd = true;

        if (odd) {
            int length = real.length + imag.length - 2;
            double[] dbl = new double[length];
            //double[] dblrev = new double[imag.length];
            /*for (int i=0; i<imag.length; i++) {
                dblrev[i] = imag[imag.length-1-i];
            }*/

            System.out.println("Imag Length : " + imag.length);
            /*for (int i=0; i<imag.length; i++ ) {
                System.out.println(imag[i]);
            }*/

            double[] imag2 = reverseCutOffEnds(imag);

            System.out.println("Real Length : " + real.length);
            /*for (int i=0; i<real.length; i++ ) {
                System.out.println(real[i]);
            }*/
            System.out.println("Imag2 Length : " + imag2.length);
            /*for (int i=0; i<imag2.length; i++ ) {
                System.out.println(imag2[i]);
            }*/

            System.arraycopy(real, 0, dbl, 0, real.length);
            System.arraycopy(imag2, 0, dbl, real.length, imag2.length);

            return dbl;
        } else {
            int length = real.length + imag.length - 1;
            double[] dbl = new double[length];
            //double[] dblrev = new double[imag.length];
            /*for (int i=0; i<imag.length; i++) {
                dblrev[i] = imag[imag.length-1-i];
            }*/

            System.out.println("Imag Length : " + imag.length);
            /*for (int i=0; i<imag.length; i++ ) {
                System.out.println(imag[i]);
            }*/

            double[] imag2 = reverseCutOffBeg(imag);

            System.out.println("Real Length : " + real.length);
            /*for (int i=0; i<real.length; i++ ) {
                System.out.println(real[i]);
            }*/
            System.out.println("Imag2 Length : " + imag2.length);
            /*for (int i=0; i<imag2.length; i++ ) {
                System.out.println(imag2[i]);
            }*/

            System.arraycopy(real, 0, dbl, 0, real.length);
            System.arraycopy(imag2, 0, dbl, real.length, imag2.length);

            return dbl;
        }
    }

    //merge real and imag array into fftw double array with the in one array

    public static double[] merge3(double[] real, double[] imag, boolean odd) {
        if (odd) {
            int length = real.length + imag.length - 2;
            double[] dbl = new double[length];
            //double[] dblrev = new double[imag.length];
            /*for (int i=0; i<imag.length; i++) {
                dblrev[i] = imag[imag.length-1-i];
            }*/

            System.out.println("Imag Length : " + imag.length);
            /*for (int i=0; i<imag.length; i++ ) {
                System.out.println(imag[i]);
            }*/

            double[] imag2 = reverseCutOffEnds(imag);

            System.out.println("Real Length : " + real.length);
            /*for (int i=0; i<real.length; i++ ) {
                System.out.println(real[i]);
            }*/
            System.out.println("Imag2 Length : " + imag2.length);
            /*for (int i=0; i<imag2.length; i++ ) {
                System.out.println(imag2[i]);
            }*/

            System.arraycopy(real, 0, dbl, 0, real.length);
            System.arraycopy(imag2, 0, dbl, real.length, imag2.length);

            return dbl;
        } else {
            int length = real.length + imag.length - 1;
            double[] dbl = new double[length];
            //double[] dblrev = new double[imag.length];
            /*for (int i=0; i<imag.length; i++) {
                dblrev[i] = imag[imag.length-1-i];
            }*/

            System.out.println("Imag Length : " + imag.length);
            /*for (int i=0; i<imag.length; i++ ) {
                System.out.println(imag[i]);
            }*/

            double[] imag2 = reverseCutOffBeg(imag);

            System.out.println("Real Length : " + real.length);
            /*for (int i=0; i<real.length; i++ ) {
                System.out.println(real[i]);
            }*/
            System.out.println("Imag2 Length : " + imag2.length);
            /*for (int i=0; i<imag2.length; i++ ) {
                System.out.println(imag2[i]);
            }*/

            System.arraycopy(real, 0, dbl, 0, real.length);
            System.arraycopy(imag2, 0, dbl, real.length, imag2.length);

            return dbl;
        }
    }

    public static boolean isOdd(int i) {
        double j = (double) i / 2;
        System.out.println("j : " + j);
        double k = Math.ceil(j);
        System.out.println("k : " + k);
        double m = k - j;
        System.out.println("m : " + m);
        if (m == 0) {
            System.out.println("***even : " + m);
            return false;
        }
        System.out.println("***odd : " + m);
        return true;
    }

    public static int mergeLength(double[] real, double[] imag) {
        if (imag[0] == 0 && imag[imag.length - 1] != 0) {
            return real.length + imag.length - 1;
        } else {
            return real.length + imag.length - 2;
        }
    }

    //merge real and complex data depending if odd or even input

    public static double[] merge4(double[] real, double[] imag) {
        //if isOdd(real.length+imag.length)) {
        if (imag[0] == 0 && imag[imag.length - 1] != 0) {
            //add 0 to beg of complex data
            int length = real.length + imag.length - 1;
            double[] dbl = new double[length];
            //double[] dblrev = new double[imag.length];
            /*for (int i=0; i<imag.length; i++) {
                dblrev[i] = imag[imag.length-1-i];
            }*/

            System.out.println("Imag Length : " + imag.length);
            /*for (int i=0; i<imag.length; i++ ) {
                System.out.println(imag[i]);
            }*/

            double[] imag2 = reverseCutOffBeg(imag);

            System.out.println("Real Length : " + real.length);
            /*for (int i=0; i<real.length; i++ ) {
                System.out.println(real[i]);
            }*/
            System.out.println("Imag2 Length : " + imag2.length);
            /*for (int i=0; i<imag2.length; i++ ) {
                System.out.println(imag2[i]);
            }*/

            System.arraycopy(real, 0, dbl, 0, real.length);
            System.arraycopy(imag2, 0, dbl, real.length, imag2.length);

            return dbl;
        } else {
            //add 0 to beg and end of complex data
            int length = real.length + imag.length - 2;
            double[] dbl = new double[length];
            //double[] dblrev = new double[imag.length];
            /*for (int i=0; i<imag.length; i++) {
                dblrev[i] = imag[imag.length-1-i];
            }*/

            System.out.println("Imag Length : " + imag.length);
            /*for (int i=0; i<imag.length; i++ ) {
                System.out.println(imag[i]);
            }*/

            double[] imag2 = reverseCutOffEnds(imag);

            System.out.println("Real Length : " + real.length);
            /*for (int i=0; i<real.length; i++ ) {
                System.out.println(real[i]);
            }*/
            System.out.println("Imag2 Length : " + imag2.length);
            /*for (int i=0; i<imag2.length; i++ ) {
                System.out.println(imag2[i]);
            }*/

            System.arraycopy(real, 0, dbl, 0, real.length);
            System.arraycopy(imag2, 0, dbl, real.length, imag2.length);

            return dbl;
        }
    }

    public static double[] reverseCutOffEnds(double[] data) {
        int length = data.length;
        double[] out = new double[data.length - 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = data[length - i - 2];
        }
        return out;
    }

    public static double[] reverseCutOffBeg(double[] data) {
        int length = data.length;
        double[] out = new double[data.length - 1];
        for (int i = 0; i < out.length; i++) {
            out[i] = data[length - i - 1];
        }
        return out;
    }

    //separate the fftw complex double array into the separate real and imag components

    public static ArrayList separate(double[] data) {

        boolean odd = true;

        if (odd) {
            System.out.println("***Data to separate");
            /*for (int i=0; i<data.length; i++) {
                System.out.println(data[i]);
            }*/

            /*String out = new String();
            for (int i=0; i<data.length; i++) {
                out = out.concat(data[i]+"\n");
            }*/
            //FileUtils.writeToFile("/home/spxrpd/fftw_raw_output.txt",out);

            /*String out = new String();
           for (int i=0; i<data.length; i++) {
               out = out.concat(data[i]+"\n");
           }
           FileUtils.writeToFile("/home/spxrpd/fftw_raw_sep_output.txt",out); */
            ArrayList al = new ArrayList();
            int n = (int) Math.floor((data.length - 2) / 2);
            int length = data.length;
            //int n = data.length/2;
            System.out.println("***N*** : " + n);
            double[] dblreal = new double[n + 2];
            double[] dblimag = new double[n + 2];
            dblreal[0] = data[0];
            dblimag[0] = 0;
            for (int i = 1; i < n + 1; i++) {
                dblreal[i] = data[i];
                dblimag[i] = data[length - i];
            }
            dblreal[n + 1] = data[n + 1];
            dblimag[n + 1] = 0;
            //dblimag[n] = data[n];
            double[] dblrev = new double[dblimag.length];
            for (int i = 0; i < dblimag.length; i++) {
                dblrev[i] = dblimag[dblimag.length - 1 - i];
            }

            System.out.println("***real***");
            //for (int i=0; i<dblreal.length; i++) System.out.println(dblreal[i]);
            System.out.println("***imag***");
            //for (int j=0; j<dblimag.length; j++) System.out.println(dblimag[j]);

            al.add(dblreal);
            al.add(dblimag);
            return al;
        } else {
            System.out.println("***Data to separate");
            /*for (int i=0; i<data.length; i++) {
                System.out.println(data[i]);
            }*/

            /*String out = new String();
            for (int i=0; i<data.length; i++) {
                out = out.concat(data[i]+"\n");
            }*/
            //FileUtils.writeToFile("/home/spxrpd/fftw_raw_output.txt",out);

            /*String out = new String();
           for (int i=0; i<data.length; i++) {
               out = out.concat(data[i]+"\n");
           }
           FileUtils.writeToFile("/home/spxrpd/fftw_raw_sep_output.txt",out); */
            ArrayList al = new ArrayList();
            int n = (int) Math.floor((data.length - 1) / 2);
            int length = data.length;
            //int n = data.length/2;
            System.out.println("***N*** : " + n);
            double[] dblreal = new double[n + 1];
            double[] dblimag = new double[n + 1];
            dblreal[0] = data[0];
            dblimag[0] = 0;
            for (int i = 1; i < n + 1; i++) {
                dblreal[i] = data[i];
                dblimag[i] = data[length - i];
            }
            //dblimag[n] = data[n];
            double[] dblrev = new double[dblimag.length];
            for (int i = 0; i < dblimag.length; i++) {
                dblrev[i] = dblimag[dblimag.length - 1 - i];
            }

            System.out.println("***real***");
            //for (int i=0; i<dblreal.length; i++) System.out.println(dblreal[i]);
            System.out.println("***imag***");
            //for (int j=0; j<dblimag.length; j++) System.out.println(dblimag[j]);

            al.add(dblreal);
            al.add(dblimag);
            return al;
        }
    }


    //separate the fftw complex double array into the separate real and imag components

    public static ArrayList separate2(double[] data, boolean odd) {

        if (odd) {
            System.out.println("***Data to separate");
            /*for (int i=0; i<data.length; i++) {
                System.out.println(data[i]);
            }*/

            /*String out = new String();
            for (int i=0; i<data.length; i++) {
                out = out.concat(data[i]+"\n");
            }*/
            //FileUtils.writeToFile("/home/spxrpd/fftw_raw_output.txt",out);

            /*String out = new String();
           for (int i=0; i<data.length; i++) {
               out = out.concat(data[i]+"\n");
           }
           FileUtils.writeToFile("/home/spxrpd/fftw_raw_sep_output.txt",out); */
            ArrayList al = new ArrayList();
            int n = (int) Math.floor((data.length - 2) / 2);
            int length = data.length;
            //int n = data.length/2;
            System.out.println("***N*** : " + n);
            double[] dblreal = new double[n + 2];
            double[] dblimag = new double[n + 2];
            dblreal[0] = data[0];
            dblimag[0] = 0;
            for (int i = 1; i < n + 1; i++) {
                dblreal[i] = data[i];
                dblimag[i] = data[length - i];
            }
            dblreal[n + 1] = data[n + 1];
            dblimag[n + 1] = 0;
            //dblimag[n] = data[n];
            double[] dblrev = new double[dblimag.length];
            for (int i = 0; i < dblimag.length; i++) {
                dblrev[i] = dblimag[dblimag.length - 1 - i];
            }

            /*System.out.println("***real***");
            for (int i=0; i<dblreal.length; i++) System.out.println(dblreal[i]);
            System.out.println("***imag***");
            for (int j=0; j<dblimag.length; j++) System.out.println(dblimag[j]);
            */

            al.add(dblreal);
            al.add(dblimag);
            return al;
        } else {
            System.out.println("***Data to separate");
            /*for (int i=0; i<data.length; i++) {
                System.out.println(data[i]);
            }*/

            /*String out = new String();
            for (int i=0; i<data.length; i++) {
                out = out.concat(data[i]+"\n");
            }*/
            //FileUtils.writeToFile("/home/spxrpd/fftw_raw_output.txt",out);

            /*String out = new String();
           for (int i=0; i<data.length; i++) {
               out = out.concat(data[i]+"\n");
           }
           FileUtils.writeToFile("/home/spxrpd/fftw_raw_sep_output.txt",out); */
            ArrayList al = new ArrayList();
            int n = (int) Math.floor((data.length - 1) / 2);
            int length = data.length;
            //int n = data.length/2;
            System.out.println("***N*** : " + n);
            double[] dblreal = new double[n + 1];
            double[] dblimag = new double[n + 1];
            dblreal[0] = data[0];
            dblimag[0] = 0;
            for (int i = 1; i < n + 1; i++) {
                dblreal[i] = data[i];
                dblimag[i] = data[length - i];
            }
            //dblimag[n] = data[n];
            double[] dblrev = new double[dblimag.length];
            for (int i = 0; i < dblimag.length; i++) {
                dblrev[i] = dblimag[dblimag.length - 1 - i];
            }

            /*System.out.println("***real***");
            for (int i=0; i<dblreal.length; i++) System.out.println(dblreal[i]);
            System.out.println("***imag***");
            for (int j=0; j<dblimag.length; j++) System.out.println(dblimag[j]);
            */

            al.add(dblreal);
            al.add(dblimag);
            return al;
        }
    }

    //separetes the real and imag data, tests for odd and even data

    public static ArrayList separate3(double[] data) {
        if (!isOdd(data.length)) {
            //separate for odd data
            System.out.println("***Data to separate");
            /*for (int i=0; i<data.length; i++) {
                System.out.println(data[i]);
            }*/

            /*String out = new String();
            for (int i=0; i<data.length; i++) {
                out = out.concat(data[i]+"\n");
            }*/
            //FileUtils.writeToFile("/home/spxrpd/fftw_raw_output.txt",out);

            /*String out = new String();
           for (int i=0; i<data.length; i++) {
               out = out.concat(data[i]+"\n");
           }
           FileUtils.writeToFile("/home/spxrpd/fftw_raw_sep_output.txt",out); */
            ArrayList al = new ArrayList();
            int n = (int) Math.floor((data.length - 2) / 2);
            int length = data.length;
            //int n = data.length/2;
            System.out.println("***N*** : " + n);
            double[] dblreal = new double[n + 2];
            double[] dblimag = new double[n + 2];
            dblreal[0] = data[0];
            dblimag[0] = 0;
            for (int i = 1; i < n + 1; i++) {
                dblreal[i] = data[i];
                dblimag[i] = data[length - i];
            }
            dblreal[n + 1] = data[n + 1];
            dblimag[n + 1] = 0;
            //dblimag[n] = data[n];
            double[] dblrev = new double[dblimag.length];
            for (int i = 0; i < dblimag.length; i++) {
                dblrev[i] = dblimag[dblimag.length - 1 - i];
            }

            /*System.out.println("***real***");
            for (int i=0; i<dblreal.length; i++) System.out.println(dblreal[i]);
            System.out.println("***imag***");
            for (int j=0; j<dblimag.length; j++) System.out.println(dblimag[j]);
            */

            al.add(dblreal);
            al.add(dblimag);
            return al;
        } else {
            //separate for even data
            System.out.println("***Data to separate");
            /*for (int i=0; i<data.length; i++) {
                System.out.println(data[i]);
            }*/

            /*String out = new String();
            for (int i=0; i<data.length; i++) {
                out = out.concat(data[i]+"\n");
            }*/
            //FileUtils.writeToFile("/home/spxrpd/fftw_raw_output.txt",out);

            /*String out = new String();
           for (int i=0; i<data.length; i++) {
               out = out.concat(data[i]+"\n");
           }
           FileUtils.writeToFile("/home/spxrpd/fftw_raw_sep_output.txt",out); */
            ArrayList al = new ArrayList();
            int n = (int) Math.floor((data.length - 1) / 2);
            int length = data.length;
            //int n = data.length/2;
            System.out.println("***N*** : " + n);
            double[] dblreal = new double[n + 1];
            double[] dblimag = new double[n + 1];
            dblreal[0] = data[0];
            dblimag[0] = 0;
            for (int i = 1; i < n + 1; i++) {
                dblreal[i] = data[i];
                dblimag[i] = data[length - i];
            }
            //dblimag[n] = data[n];
            double[] dblrev = new double[dblimag.length];
            for (int i = 0; i < dblimag.length; i++) {
                dblrev[i] = dblimag[dblimag.length - 1 - i];
            }

            /*System.out.println("***real***");
            for (int i=0; i<dblreal.length; i++) System.out.println(dblreal[i]);
            System.out.println("***imag***");
            for (int j=0; j<dblimag.length; j++) System.out.println(dblimag[j]);
            */

            al.add(dblreal);
            al.add(dblimag);
            return al;
        }
    }

    public static double[] flattenArray(double[][] data) {
        double[] out = new double[data.length * data[0].length];
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(data[i], 0, out, i * data[0].length, data[0].length);
        }
        return out;
    }

    public static double[][] unflattenArray(double[] data, int col, int rows) {
        if (data.length != col * rows) {
            System.out.println("length of data doesn't match number of colums and rows");
            return null;
        }

        /*int count=0;
        double[][] out = new double[rows][col];
        for (int i=0; i<rows; i++) {
            for (int j=0; j<col; j++) {
                out[i][j] = data[count];
                count++;
            }
        }*/

        double[][] out = new double[rows][col];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data, i * col, out[i], (int) out[i][0], col);
        }


        return out;
    }

}
