/*
 * The University of Wales, Cardiff Triana Project Software License (Based
 * on the Apache Software License Version 1.1)
 *
 * Copyright (c) 2007 University of Wales, Cardiff. All rights reserved.
 *
 * Redistribution and use of the software in source and binary forms, with
 * or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any,
 *    must include the following acknowledgment: "This product includes
 *    software developed by the University of Wales, Cardiff for the Triana
 *    Project (http://www.trianacode.org)." Alternately, this
 *    acknowledgment may appear in the software itself, if and wherever
 *    such third-party acknowledgments normally appear.
 *
 * 4. The names "Triana" and "University of Wales, Cardiff" must not be
 *    used to endorse or promote products derived from this software
 *    without prior written permission. For written permission, please
 *    contact triana@trianacode.org.
 *
 * 5. Products derived from this software may not be called "Triana," nor
 *    may Triana appear in their name, without prior written permission of
 *    the University of Wales, Cardiff.
 *
 * 6. This software may not be sold, used or incorporated into any product
 *    for sale to third parties.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN
 * NO EVENT SHALL UNIVERSITY OF WALES, CARDIFF OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------------------------------------------------------------
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Triana Project. For more information on the
 * Triana Project, please see. http://www.trianacode.org.
 *
 * This license is based on the BSD license as adopted by the Apache
 * Foundation and is governed by the laws of England and Wales.
 *
 */

package triana.types.audio.gsm.decoder;

//    $Id: GSMDecoder.java 981 2003-05-29 13:59:10Z spxmss $

//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Library General Public
//    License as published by the Free Software Foundation; either
//    version 2 of the License, or (at your option) any later version.

//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Library General Public License for more details.

//    You should have received a copy of the GNU Library General Public
//    License along with this library; if not, write to the Free
//    Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.


//  This software is a port of the GSM Library provided by
//  Jutta Degener (jutta@cs.tu-berlin.de) and
//  Carsten Bormann (cabo@cs.tu-berlin.de),
//  Technische Universitaet Berlin

import triana.types.audio.gsm.encoder.Gsm_Def;


public final class GSMDecoder {

    private static final byte GSM_MAGIC = 0x0d;

    private static final int[] FAC = {18431, 20479, 22527, 24575,
            26623, 28671, 30719, 32767};

    private static final int[] QLB = {3277, 11469, 21299, 32767};

    private static final int MIN_WORD = -32767 - 1;
    private static final int MAX_WORD = 32767;

    private int dp0[] = new int[280];

    private int u[] = new int[8];
    private int LARpp[][] = new int[2][8];
    private int j;

    private int nrp;
    private int v[] = new int[9];
    private int msr;

    // took these out of decode() for efficiency!

    int d_LARc[] = new int[8];
    int d_Nc[] = new int[4];
    int d_Mc[] = new int[4];
    int d_bc[] = new int[4];
    int d_xmaxc[] = new int[4];
    int d_xmc[] = new int[13 * 4];

    // took these out of decoder() for efficiency!

    int dec_erp[] = new int[40];
    int dec_wt[] = new int[160];

    // also this out of shortTermSynthesisFilter as it was being created on each iteration

    int s_allocated[] = new int[160];

    // also this out of RPCDecoding as it was being created on each iteration

    int rp_xMp[] = new int[13];

    // also this out of xmaxcToExpAndMant as it was being created on each iteration

    int result[] = new int[2];

    public void GSM() {
        nrp = 40;
    }

    byte[] curframe = new byte[Gsm_Def.FRAME_SIZE];
    int[] cursamples;

    /**
     * Takes an array of GSM encoded data returns a short array containing the decoded samples. The GSM array may
     * contain many frames.
     */
    public final short[] process(byte[] data) {
        int numberOfFrames = data.length / Gsm_Def.FRAME_SIZE;
        int off = 0;
        int froff = 0;
        int j, i, k;

        short[] sampl = new short[numberOfFrames * Gsm_Def.FRAME_SAMPLE_SIZE];

        for (i = 0; i < numberOfFrames; ++i) {
            for (j = 0; j < Gsm_Def.FRAME_SIZE; ++j) {
                curframe[j] = data[j + froff];
            }
            cursamples = decode(curframe);
            for (k = 0; k < Gsm_Def.FRAME_SAMPLE_SIZE; ++k) {
                sampl[k + off] = (short) cursamples[k];
            }
            off += Gsm_Def.FRAME_SAMPLE_SIZE;
            froff += Gsm_Def.FRAME_SIZE;
        }
        return sampl;
    }

    public final int[] decode(byte c[]) { // throws InvalidGSMFrameException {

//    if (c.length!=33)
        //    throw new InvalidGSMFrameException();


        int i = 0;

//    if (((c[i]>>4) & 0xf) != GSM_MAGIC)
        //    throw new InvalidGSMFrameException();

        d_LARc[0] = ((c[i++] & 0xF) << 2);           /* 1 */
        d_LARc[0] |= ((c[i] >> 6) & 0x3);
        d_LARc[1] = (c[i++] & 0x3F);
        d_LARc[2] = ((c[i] >> 3) & 0x1F);
        d_LARc[3] = ((c[i++] & 0x7) << 2);
        d_LARc[3] |= ((c[i] >> 6) & 0x3);
        d_LARc[4] = ((c[i] >> 2) & 0xF);
        d_LARc[5] = ((c[i++] & 0x3) << 2);
        d_LARc[5] |= ((c[i] >> 6) & 0x3);
        d_LARc[6] = ((c[i] >> 3) & 0x7);
        d_LARc[7] = (c[i++] & 0x7);
        d_Nc[0] = ((c[i] >> 1) & 0x7F);
        d_bc[0] = ((c[i++] & 0x1) << 1);
        d_bc[0] |= ((c[i] >> 7) & 0x1);
        d_Mc[0] = ((c[i] >> 5) & 0x3);
        d_xmaxc[0] = ((c[i++] & 0x1F) << 1);
        d_xmaxc[0] |= ((c[i] >> 7) & 0x1);
        d_xmc[0] = ((c[i] >> 4) & 0x7);
        d_xmc[1] = ((c[i] >> 1) & 0x7);
        d_xmc[2] = ((c[i++] & 0x1) << 2);
        d_xmc[2] |= ((c[i] >> 6) & 0x3);
        d_xmc[3] = ((c[i] >> 3) & 0x7);
        d_xmc[4] = (c[i++] & 0x7);
        d_xmc[5] = ((c[i] >> 5) & 0x7);
        d_xmc[6] = ((c[i] >> 2) & 0x7);
        d_xmc[7] = ((c[i++] & 0x3) << 1);            /* 10 */
        d_xmc[7] |= ((c[i] >> 7) & 0x1);
        d_xmc[8] = ((c[i] >> 4) & 0x7);
        d_xmc[9] = ((c[i] >> 1) & 0x7);
        d_xmc[10] = ((c[i++] & 0x1) << 2);
        d_xmc[10] |= ((c[i] >> 6) & 0x3);
        d_xmc[11] = ((c[i] >> 3) & 0x7);
        d_xmc[12] = (c[i++] & 0x7);
        d_Nc[1] = ((c[i] >> 1) & 0x7F);
        d_bc[1] = ((c[i++] & 0x1) << 1);
        d_bc[1] |= ((c[i] >> 7) & 0x1);
        d_Mc[1] = ((c[i] >> 5) & 0x3);
        d_xmaxc[1] = ((c[i++] & 0x1F) << 1);
        d_xmaxc[1] |= ((c[i] >> 7) & 0x1);
        d_xmc[13] = ((c[i] >> 4) & 0x7);
        d_xmc[14] = ((c[i] >> 1) & 0x7);
        d_xmc[15] = ((c[i++] & 0x1) << 2);
        d_xmc[15] |= ((c[i] >> 6) & 0x3);
        d_xmc[16] = ((c[i] >> 3) & 0x7);
        d_xmc[17] = (c[i++] & 0x7);
        d_xmc[18] = ((c[i] >> 5) & 0x7);
        d_xmc[19] = ((c[i] >> 2) & 0x7);
        d_xmc[20] = ((c[i++] & 0x3) << 1);
        d_xmc[20] |= ((c[i] >> 7) & 0x1);
        d_xmc[21] = ((c[i] >> 4) & 0x7);
        d_xmc[22] = ((c[i] >> 1) & 0x7);
        d_xmc[23] = ((c[i++] & 0x1) << 2);
        d_xmc[23] |= ((c[i] >> 6) & 0x3);
        d_xmc[24] = ((c[i] >> 3) & 0x7);
        d_xmc[25] = (c[i++] & 0x7);
        d_Nc[2] = ((c[i] >> 1) & 0x7F);
        d_bc[2] = ((c[i++] & 0x1) << 1);             /* 20 */
        d_bc[2] |= ((c[i] >> 7) & 0x1);
        d_Mc[2] = ((c[i] >> 5) & 0x3);
        d_xmaxc[2] = ((c[i++] & 0x1F) << 1);
        d_xmaxc[2] |= ((c[i] >> 7) & 0x1);
        d_xmc[26] = ((c[i] >> 4) & 0x7);
        d_xmc[27] = ((c[i] >> 1) & 0x7);
        d_xmc[28] = ((c[i++] & 0x1) << 2);
        d_xmc[28] |= ((c[i] >> 6) & 0x3);
        d_xmc[29] = ((c[i] >> 3) & 0x7);
        d_xmc[30] = (c[i++] & 0x7);
        d_xmc[31] = ((c[i] >> 5) & 0x7);
        d_xmc[32] = ((c[i] >> 2) & 0x7);
        d_xmc[33] = ((c[i++] & 0x3) << 1);
        d_xmc[33] |= ((c[i] >> 7) & 0x1);
        d_xmc[34] = ((c[i] >> 4) & 0x7);
        d_xmc[35] = ((c[i] >> 1) & 0x7);
        d_xmc[36] = ((c[i++] & 0x1) << 2);
        d_xmc[36] |= ((c[i] >> 6) & 0x3);
        d_xmc[37] = ((c[i] >> 3) & 0x7);
        d_xmc[38] = (c[i++] & 0x7);
        d_Nc[3] = ((c[i] >> 1) & 0x7F);
        d_bc[3] = ((c[i++] & 0x1) << 1);
        d_bc[3] |= ((c[i] >> 7) & 0x1);
        d_Mc[3] = ((c[i] >> 5) & 0x3);
        d_xmaxc[3] = ((c[i++] & 0x1F) << 1);
        d_xmaxc[3] |= ((c[i] >> 7) & 0x1);
        d_xmc[39] = ((c[i] >> 4) & 0x7);
        d_xmc[40] = ((c[i] >> 1) & 0x7);
        d_xmc[41] = ((c[i++] & 0x1) << 2);
        d_xmc[41] |= ((c[i] >> 6) & 0x3);
        d_xmc[42] = ((c[i] >> 3) & 0x7);
        d_xmc[43] = (c[i++] & 0x7);                  /* 30  */
        d_xmc[44] = ((c[i] >> 5) & 0x7);
        d_xmc[45] = ((c[i] >> 2) & 0x7);
        d_xmc[46] = ((c[i++] & 0x3) << 1);
        d_xmc[46] |= ((c[i] >> 7) & 0x1);
        d_xmc[47] = ((c[i] >> 4) & 0x7);
        d_xmc[48] = ((c[i] >> 1) & 0x7);
        d_xmc[49] = ((c[i++] & 0x1) << 2);
        d_xmc[49] |= ((c[i] >> 6) & 0x3);
        d_xmc[50] = ((c[i] >> 3) & 0x7);
        d_xmc[51] = (c[i] & 0x7);                    /* 33 */

        return decoder(d_LARc, d_Nc, d_bc, d_Mc, d_xmaxc, d_xmc);
    }

    public final static void print(String name, int data[]) {
        System.out.print("[" + name + ":");
        for (int i = 0; i < data.length; i++) {
            System.out.print("" + data[i]);
            if (i < data.length - 1) {
                System.out.print(",");
            } else {
                System.out.println("]");
            }
        }
    }

    public final static void print(String name, int data) {
        System.out.println("[" + name + ":" + data + "]");
    }

    private final int[] decoder(int LARcr[],
                                int Ncr[],
                                int bcr[],
                                int Mcr[],
                                int xmaxcr[],
                                int xMcr[]) {
        int j, k;
        // drp is just dp0+120

        //print("LARcr",LARcr);
        //print("Ncr",Ncr);
        //print("bcr",bcr);
        //print("Mcr",Mcr);
        //print("xmaxcr",xmaxcr);
        //print("xMcr",xMcr);

        for (j = 0; j < 4; j++) {
            // find out what is done with xMcr
            RPEDecoding(xmaxcr[j], Mcr[j], xMcr, j * 13, dec_erp);

            //print("dec_erp",dec_erp);

            longTermSynthesisFiltering(Ncr[j], bcr[j], dec_erp, dp0);

            for (k = 0; k < 40; k++) {
                dec_wt[j * 40 + k] = dp0[120 + k];
            }
        }

        //print("LARcr",LARcr);

        //print("dec_wt",dec_wt);

        int s[] = shortTermSynthesisFilter(LARcr, dec_wt);

        //print("s",s);

        postprocessing(s);

        return s;

    }

    private final void RPEDecoding(int xmaxcr,
                                   int Mcr,
                                   int xMcr[],
                                   int xMcrOffset,
                                   int erp[]) {
        int expAndMant[];

        expAndMant = xmaxcToExpAndMant(xmaxcr);

        //System.out.println("[e&m:"+expAndMant[0]+","+expAndMant[1]+"]");

        APCMInverseQuantization(xMcr, xMcrOffset, expAndMant[0], expAndMant[1], rp_xMp);

        //print("xMp",rp_xMp);

        RPE_grid_positioning(Mcr, rp_xMp, erp);

    }

    private final int[] xmaxcToExpAndMant(int xmaxc) {
        int exp, mant;

        exp = 0;
        if (xmaxc > 15) {
            exp = ((xmaxc >> 3) - 1);
        }
        mant = (xmaxc - (exp << 3));

        if (mant == 0) {
            exp = -4;
            mant = 7;
        } else {
            while (mant <= 7) {
                mant = (mant << 1 | 1);
                exp--;
            }
            mant -= 8;
        }

        //assert(exp>=-4 && exp <= 6);
        //assert(mant>=0 && mant<=7);

        result[0] = exp;
        result[1] = mant;

        return result;

    }

    //private void assert(boolean test) {
    //  if (!test) {
    //    System.out.println("assertion error");
    //  }
    //}

    private final void APCMInverseQuantization(int xMc[],
                                               int xMcOffset,
                                               int exp,
                                               int mant,
                                               int xMp[]) {
        int i, p;
        int temp, temp1, temp2, temp3;
        int ltmp;

        //assert(mant >0 && mant <= 7 );

        temp1 = FAC[mant];
        temp2 = sub(6, exp);
        temp3 = asl(1, sub(temp2, 1));

        //System.out.println("temp1="+temp1);
        //System.out.println("temp2="+temp2);
        //System.out.println("temp3="+temp3);

        p = 0;

        for (i = 13; i-- > 0;) {
            //assert(xMc[xMcOffset] <= 7 && xMc[xMcOffset] >= 0);

            temp = ((xMc[xMcOffset++] << 1) - 7);

            //System.out.println("s1:temp="+temp);

            //assert(temp<=7 && temp >= -7);

            temp = (temp << 12);//&0xffff;

            //System.out.println("s2:temp="+temp);

            temp = mult_r(temp1, temp);

            //System.out.println("s3:temp="+temp);

            temp = add(temp, temp3);

            //System.out.println("s4:temp="+temp);

            xMp[p++] = asr(temp, temp2);
        }
    }

    private final static int saturate(int x) {
        return (x < MIN_WORD ? MIN_WORD : (x > MAX_WORD ? MAX_WORD : x));
    }

    private final static int sub(int a, int b) {
        int diff = a - b;
        return saturate(diff);
    }

    private final static int add(int a, int b) {
        int sum = a + b;
        return saturate(sum);
    }

    private final static int asl(int a, int n) {
        if (n >= 16) {
            return 0;
        }
        if (n <= -16) {
            return (a < 0 ? -1 : 0);
        }
        if (n < 0) {
            return asr(a, -n);
        }
        return (a << n);
    }

    private final static int asr(int a, int n) {
        if (n >= 16) {
            return (a < 0 ? -1 : 0);
        }
        if (n <= -16) {
            return 0;
        }
        if (n < 0) {
            return (a << -n);
        }//&0xffff;
        return (a >> n);
    }

    private final static int mult_r(int a, int b) {
        if (b == MIN_WORD && a == MIN_WORD) {
            return MAX_WORD;
        } else {
            int prod = a * b + 16384;
            //prod >>= 15;
            return saturate(prod >> 15);//&0xffff;
            //return (prod & 0xffff);
        }
    }

    private final void longTermSynthesisFiltering(int Ncr,
                                                  int bcr,
                                                  int erp[],
                                                  int dp0[]) {
        int ltmp;
        int k;
        int brp, drpp, Nr;

        Nr = Ncr < 40 || Ncr > 120 ? nrp : Ncr;
        nrp = Nr;

        brp = QLB[bcr];

        for (k = 0; k <= 39; k++) {
            drpp = mult_r(brp, dp0[120 + (k - Nr)]);
            dp0[120 + k] = add(erp[k], drpp);
        }

        for (k = 0; k <= 119; k++) {
            dp0[k] = dp0[40 + k];
        }

    }

    private final int[] shortTermSynthesisFilter(int LARcr[],
                                                 int wt[]) {


        //print("wt",wt);

        int LARpp_j[] = LARpp[j];
        int LARpp_j_1[] = LARpp[j ^= 1];

        int LARp[] = new int[8];

        decodingOfTheCodedLogAreaRatios(LARcr, LARpp_j);

        //print("LARpp_j",LARpp_j);

        Coefficients_0_12(LARpp_j_1, LARpp_j, LARp);
        LARp_to_rp(LARp);
        shortTermSynthesisFiltering(LARp, 13, wt, s_allocated, 0);

        Coefficients_13_26(LARpp_j_1, LARpp_j, LARp);
        LARp_to_rp(LARp);
        shortTermSynthesisFiltering(LARp, 14, wt, s_allocated, 13);

        Coefficients_27_39(LARpp_j_1, LARpp_j, LARp);
        LARp_to_rp(LARp);
        shortTermSynthesisFiltering(LARp, 13, wt, s_allocated, 27);

        Coefficients_40_159(LARpp_j, LARp);
        LARp_to_rp(LARp);
        shortTermSynthesisFiltering(LARp, 120, wt, s_allocated, 40);

        return s_allocated;
    }

    public final static void decodingOfTheCodedLogAreaRatios(int LARc[],
                                                             int LARpp[]) {
        int temp1;
        int ltmp;

        // STEP(      0,  -32,  13107 );

        temp1 = (add(LARc[0], -32) << 10);
        //temp1 = (sub(temp1, 0));
        temp1 = (mult_r(13107, temp1));
        LARpp[0] = (add(temp1, temp1));

        //         STEP(      0,  -32,  13107 );

        temp1 = (add(LARc[1], -32) << 10);
        //temp1 = (sub(temp1, 0));
        temp1 = (mult_r(13107, temp1));
        LARpp[1] = (add(temp1, temp1));

        //         STEP(   2048,  -16,  13107 );

        temp1 = (add(LARc[2], -16) << 10);
        temp1 = (sub(temp1, 4096));
        temp1 = (mult_r(13107, temp1));
        LARpp[2] = (add(temp1, temp1));

        //         STEP(  -2560,  -16,  13107 );

        temp1 = (add(LARc[3], (-16)) << 10);
        temp1 = (sub(temp1, -5120));
        temp1 = (mult_r(13107, temp1));
        LARpp[3] = (add(temp1, temp1));

        //         STEP(     94,   -8,  19223 );

        temp1 = (add(LARc[4], -8) << 10);
        temp1 = (sub(temp1, 188));
        temp1 = (mult_r(19223, temp1));
        LARpp[4] = (add(temp1, temp1));

        //         STEP(  -1792,   -8,  17476 );

        temp1 = (add(LARc[5], (-8)) << 10);
        temp1 = (sub(temp1, -3584));
        temp1 = (mult_r(17476, temp1));
        LARpp[5] = (add(temp1, temp1));

        //         STEP(   -341,   -4,  31454 );

        temp1 = (add(LARc[6], (-4)) << 10);
        temp1 = (sub(temp1, -682));
        temp1 = (mult_r(31454, temp1));
        LARpp[6] = (add(temp1, temp1));

        //         STEP(  -1144,   -4,  29708 );

        temp1 = (add(LARc[7], -4) << 10);
        temp1 = (sub(temp1, -2288));
        temp1 = (mult_r(29708, temp1));
        LARpp[7] = (add(temp1, temp1));

    }

    private final static void Coefficients_0_12(int LARpp_j_1[],
                                                int LARpp_j[],
                                                int LARp[]) {
        int i;
        int ltmp;

        for (i = 0; i < 8; i++) {
            LARp[i] = add((LARpp_j_1[i] >> 2), (LARpp_j[i] >> 2));
            LARp[i] = add(LARp[i], (LARpp_j_1[i] >> 1));
        }
    }

    private final static void Coefficients_13_26(int LARpp_j_1[],
                                                 int LARpp_j[],
                                                 int LARp[]) {
        int i;
        int ltmp;

        for (i = 0; i < 8; i++) {
            LARp[i] = add((LARpp_j_1[i] >> 1), (LARpp_j[i] >> 1));
        }
    }

    private final static void Coefficients_27_39(int LARpp_j_1[],
                                                 int LARpp_j[],
                                                 int LARp[]) {
        int i;
        int ltmp;

        for (i = 0; i < 8; i++) {
            LARp[i] = add((LARpp_j_1[i] >> 2), (LARpp_j[i] >> 2));
            LARp[i] = add(LARp[i], (LARpp_j[i] >> 1));
        }
    }


    private final static void Coefficients_40_159(int LARpp_j[],
                                                  int LARp[]) {
        int i;
        int ltmp;

        for (i = 0; i < 8; i++) {
            LARp[i] = LARpp_j[i];
        }
    }


    private final static void LARp_to_rp(int LARp[]) {

        int i;
        int temp;

        for (i = 0; i < 8; i++) {
            if (LARp[i] < 0) {
                temp = ((LARp[i] == MIN_WORD) ? MAX_WORD : -LARp[i]);
                LARp[i] = (-((temp < 11059) ? temp << 1
                        : ((temp < 20070) ? temp + 11059
                                : add((temp >> 2), 26112))));
            } else {
                temp = LARp[i];
                LARp[i] = ((temp < 11059) ? temp << 1
                        : ((temp < 20070) ? temp + 11059
                                : add((temp >> 2), 26112)));
            }
        }
    }

    //      shortTermSynthesisFiltering(LARp,13,wt,s,0);

    private final void shortTermSynthesisFiltering(int rrp[],
                                                   int k,
                                                   int wt[],
                                                   int sr[],
                                                   int off) {
        int i;
        int sri, tmp1, tmp2;
        int woff = off;
        int soff = off;

        while (k-- > 0) {
            sri = wt[woff++];
            for (i = 8; i-- > 0;) {
                tmp1 = rrp[i];
                tmp2 = v[i];
                tmp2 = ((tmp1 == MIN_WORD && tmp2 == MIN_WORD
                        ? MAX_WORD
                        : saturate((tmp1 * tmp2 + 16384) >> 15)));
                sri = sub(sri, tmp2);

                tmp1 = ((tmp1 == MIN_WORD && sri == MIN_WORD
                        ? MAX_WORD
                        : saturate((tmp1 * sri + 16384) >> 15)));
                v[i + 1] = add(v[i], tmp1);
            }
            sr[soff++] = v[0] = sri;
        }
    }

    private final void postprocessing(int s[]) {
        int k, soff = 0;
        int tmp;
        for (k = 160; k-- > 0; soff++) {
            tmp = mult_r(msr, (28180));
            msr = add(s[soff], tmp);
            //s[soff]=(add(msr,msr) & 0xfff8);
            s[soff] = saturate(add(msr, msr) & ~0x7);
        }
    }

    private final static void RPE_grid_positioning(int Mc,
                                                   int xMp[],
                                                   int ep[]) {
        int i = 13;

        int epo = 0;
        int po = 0;

        switch (Mc) {
            case 3:
                ep[epo++] = 0;
            case 2:
                ep[epo++] = 0;
            case 1:
                ep[epo++] = 0;
            case 0:
                ep[epo++] = xMp[po++];
                i--;
        }
        ;

        do {
            ep[epo++] = 0;
            ep[epo++] = 0;
            ep[epo++] = xMp[po++];
        }
        while (--i > 0);

        while (++Mc < 4) {
            ep[epo++] = 0;
        }

    }

}




















