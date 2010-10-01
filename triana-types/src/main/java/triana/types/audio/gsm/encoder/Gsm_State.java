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

package triana.types.audio.gsm.encoder;

public class Gsm_State {
    private short[] dp0;
    private short z1;        /* preprocessing,   Offset_com.     */
    private int L_z2;            /*                  Offset_com.     */
    private int mp;            /*                  Preemphasis     */

    private short[] u;        /* short_term.java                  */
    private short[][] LARpp;        /*                                  */
    private short j;        /*                                  */

    private short nrp;        /* long_term.java, synthesis        */
    private short[] v;        /* short_term.java, synthesis       */
    private short msr;        /* Gsm_Decoder.java, Postprocessing */

    public Gsm_State() {

        short Dp0[] = new short[280];
        short U[] = new short[8];
        short LARpp[][] = new short[2][8];
        short V[] = new short[9];

        this.setDp0(Dp0);
        this.setZ1((short) 0);
        this.setL_z2(0);
        this.setMp(0);
        this.setU(U);
        this.setLARpp(LARpp);
        this.setJ((short) 0);
        this.setNrp((short) 40);
        this.setV(V);
        this.setMsr((short) 0);
    }

    public void dump_Gsm_State() {
        int i, col;

        System.out.println("\ndp0[]: ");
        /*for(i = 0; i < dp0.length; ++i) {
            System.out.print("["+i+"] "+dp0[i]);
            if (i < dp0.length - 1)
                System.out.print(", ");
        }*/
        System.out.println("\nz1: " + z1);
        System.out.println("\nL_z2: " + L_z2);
        System.out.println("\nmp: " + mp);
        System.out.println("\nu[]: ");
        for (i = 0; i < u.length; ++i) {
            System.out.print("[" + i + "] " + u[i]);
            if (i < u.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.print("\n");
        System.out.println("\nLARpp[]: ");
        for (i = 0; i < 2; ++i) {
            for (col = 0; col < 8; ++col) {
                System.out.print("[" + i + "][" + col + "] " + LARpp[i][col]);
                System.out.print(", ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
        System.out.println("\nj: " + j);
        System.out.println("\nnrp: " + nrp);
        System.out.println("\nv[]: ");
        for (i = 0; i < v.length; ++i) {
            System.out.print("[" + i + "] " + v[i]);
            if (i < v.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.print("\n");
        System.out.println("\nmsr: " + msr);
        //System.out.println("\nverbose: " + verbose);
        //System.out.println("\nfast: " + fast);
    }

    public String toString() {
        String temp = new String("");
        return (new String("").valueOf(nrp));
    }

    public void setDp0(short[] lcl_arg0) {
        dp0 = lcl_arg0;
    }

    public void setDp0Indexed(int ix, short lcl_arg0) {
        dp0[ix] = lcl_arg0;
    }

    public short[] getDp0() {
        return dp0;
    }

    public short getDp0Indexed(int ix) {
        return dp0[ix];
    }


    public void setZ1(short lcl_arg0) {
        z1 = lcl_arg0;
    }

    public short getZ1() {
        return z1;
    }


    public void setL_z2(int lcl_arg0) {
        L_z2 = lcl_arg0;
    }

    public int getL_z2() {
        return L_z2;
    }


    public void setMp(int lcl_arg0) {
        mp = lcl_arg0;
    }

    public int getMp() {
        return mp;
    }


    public void setU(short[] lcl_arg0) {
        u = lcl_arg0;
    }

    public void setUIndexed(int ix, short lcl_arg0) {
        u[ix] = lcl_arg0;
    }

    public short[] getU() {
        return u;
    }

    public short getUIndexed(int ix) {
        return u[ix];
    }

    public void setLARpp(short[][] lcl_arg0) {
        LARpp = lcl_arg0;
    }

    public void setLARppIndexed(int ix, short[] lcl_arg0) {
        LARpp[ix] = lcl_arg0;
    }

    public short[][] getLARpp() {
        return LARpp;
    }

    public short[] getLARppIndexed(int ix) {
        return LARpp[ix];
    }

    public void setJ(short lcl_arg0) {
        j = lcl_arg0;
    }

    public short getJ() {
        return j;
    }


    public void setNrp(short lcl_arg0) {
        nrp = lcl_arg0;
    }

    public short getNrp() {
        return nrp;
    }


    public void setV(short[] lcl_arg0) {
        v = lcl_arg0;
    }

    public void setVIndexed(int ix, short lcl_arg0) {
        v[ix] = lcl_arg0;
    }

    public short[] getV() {
        return v;
    }

    public short getVIndexed(int ix) {
        return v[ix];
    }

    public void setMsr(short lcl_arg0) {
        msr = lcl_arg0;
    }

    public short getMsr() {
        return msr;
    }
}