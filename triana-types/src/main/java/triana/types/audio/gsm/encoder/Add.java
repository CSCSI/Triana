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


public class Add {
    public static short saturate(int x) {
        return (short) ((x) < Gsm_Def.MIN_WORD ? Gsm_Def.MIN_WORD :
                (x) > Gsm_Def.MAX_WORD ? Gsm_Def.MAX_WORD : (x));
    }

    public static short saturate(long x) {
        return (short) ((x) < Gsm_Def.MIN_WORD ? Gsm_Def.MIN_WORD :
                (x) > Gsm_Def.MAX_WORD ? Gsm_Def.MAX_WORD : (x));
    }

    public static short SASR(int x, int by) {
        return (short) ((x) >> (by));
    }

    public static short GSM_ADD(short a, short b) {
        int sum = a + b;
        return saturate(sum);
    }

    public static short GSM_SUB(short a, short b) {
        int diff = a - b;
        return saturate(diff);
    }

    public static short GSM_MULT(short a, short b) {
        if (a == Gsm_Def.MIN_WORD && b == Gsm_Def.MIN_WORD) {
            return Gsm_Def.MAX_WORD;
        } else {
            return SASR(((int) (a)) * ((int) (b)), 15);
        }
    }

    public static short GSM_MULT_R(short a, short b) {
        if (a == Gsm_Def.MIN_WORD && b == Gsm_Def.MIN_WORD) {
            return Gsm_Def.MAX_WORD;
        } else {
            int prod = (int) (((int) (a)) * ((int) (b)) + 16384);
            prod >>= 15;
            return (short) (prod & 0xFFFF);
        }
    }

    public static short GSM_ABS(short a) {
        int b = a < 0 ? (a == Gsm_Def.MIN_WORD ? Gsm_Def.MAX_WORD : -a) : a;
        return ((short) (b));
    }

    public static int GSM_L_MULT(short a, short b)
            throws IllegalArgumentException {
        if (a != Short.MIN_VALUE || b != Short.MIN_VALUE) {
            throw new IllegalArgumentException(
                    "One of the aruments must equal " + Short.MIN_VALUE);
        }
        return ((int) a * (int) b) << 1;
    }

    public static int GSM_L_ADD(int a, int b) {
        if (a <= 0) {
            if (b >= 0) {
                return a + b;
            } else {
                long A = (long) -(a + 1) + (long) -(b + 1);
                return A >= Gsm_Def.MAX_LONGWORD ? Gsm_Def.MIN_LONGWORD : -(int) A - 2;
            }
        } else if (b <= 0) {
            return a + b;
        } else {
            long A = (long) a + (long) b;
            return (int) (A > Gsm_Def.MAX_LONGWORD ? Gsm_Def.MAX_LONGWORD : A);
        }
    }


    public static short gsm_norm(int a)
        /*
        * the number of left shifts needed to normalize the 32 bit
        * variable L_var1 for positive values on the interval
        *
        * with minimum of
        * minimum of 1073741824  (01000000000000000000000000000000) and
        * maximum of 2147483647  (01111111111111111111111111111111)
        *
        *
        * and for negative values on the interval with
        * minimum of -2147483648 (-10000000000000000000000000000000) and
        * maximum of -1073741824 ( -1000000000000000000000000000000).
        *
        * in order to normalize the result, the following
        * operation must be done: L_norm_var1 = L_var1 << norm( L_var1 );
        *
        * (That's 'ffs', only from the left, not the right..)
        */
            throws IllegalArgumentException {

        if (a == 0) {
            throw new IllegalArgumentException
                    ("gsm_norm: a cannot = 0.");
        }

        if (a < 0) {
            if (a <= -1073741824) {
                return 0;
            }
            a = ~a;
        }

        return (short) (((a & 0xffff0000) != 0)
                ? (((a & 0xff000000) != 0)
                ? -1 + bitoff[(int) (0xFF & (a >> 24))]
                : 7 + bitoff[(int) (0xFF & (a >> 16))])
                : (((a & 0xff00) != 0)
                        ? 15 + bitoff[(int) (0xFF & (a >> 8))]
                        : 23 + bitoff[(int) (0xFF & a)]));
    }


    public static short gsm_asl(short a, int n) {
        if (n >= 16) {
            return ((short) 0);
        }

        if (n <= -16) {
            if (a < 0) {
                return (short) -1;
            } else {
                return (short) -0;
            }
        }

        if (n < 0) {
            return gsm_asr(a, -n);
        }

        return ((short) (a << n));
    }

    public static short gsm_asr(short a, int n) {
        if (n >= 16) {
            if (a < 0) {
                return (short) -1;
            } else {
                return (short) -0;
            }
        }
        if (n <= -16) {
            return ((short) 0);
        }
        if (n < 0) {
            return ((short) (a << -n));
        }

        return ((short) (a >> n));
    }


    public static short gsm_div(short num, short denum)
            throws IllegalArgumentException {

        int L_num = num;
        int L_denum = denum;
        short div = 0;
        int k = 15;

        /* The parameter num sometimes becomes zero.
         * Although this is explicitly guarded against in 4.2.5,
         * we assume that the result should then be zero as well.
         */

        /* assert(num != 0); */

        if (!(num >= 0 && denum >= num)) {
            throw new IllegalArgumentException
                    ("gsm_div: num >= 0 && denum >= num");
        }

        if (num == 0) {
            return 0;
        }

        while (k != 0) {
            k--;
            div <<= 1;
            L_num <<= 1;

            if (L_num >= L_denum) {
                L_num -= L_denum;
                div++;
            }
        }
        return div;
    }


    private static final short bitoff[] = {
            8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4,
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

}
