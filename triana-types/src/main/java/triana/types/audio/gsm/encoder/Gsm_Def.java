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

package triana.audio.gsm.encoder;

public abstract class Gsm_Def {

    // Define the magic number for audio files
    public static final int AUDIO_FILE_MAGIC = 0x2e736e64;

    // The encoding key for type: 8-bit ISDN u-law
    public static final int AUDIO_FILE_ENCODING_MULAW_8 = 1;

    public static final short FRAME_SIZE = 33;
    public static final short FRAME_SAMPLE_SIZE = 160;

    public static final short MAX_FRAME_READ = 1000;

    public static final short MIN_WORD = -32768;
    public static final short MAX_WORD = 32767;

    public static final int MIN_LONGWORD = -2147483648;
    public static final int MAX_LONGWORD = 2147483647;

/*  Table 4.1  Quantization of the Log.-Area Ratios
 */
/* i                 1      2      3        4      5      6        7       8 */

    public static final short gsm_A[] =
            {20480, 20480, 20480, 20480, 13964, 15360, 8534, 9036};
    public static final short gsm_B[] =
            {0, 0, 2048, -2560, 94, -1792, -341, -1144};
    public static final short gsm_MIC[] =
            {-32, -32, -16, -16, -8, -8, -4, -4};
    public static final short gsm_MAC[] =
            {31, 31, 15, 15, 7, 7, 3, 3};

    /*  Table 4.2  Tabulation  of 1/A[1..8]
    */
    public static final short gsm_INVA[] =
            {13107, 13107, 13107, 13107, 19223, 17476, 31454, 29708};

/*   Table 4.3a  Decision level of the LTP gain quantizer
 */
    /*  bc                0         1         2          3                  */
    public static final short gsm_DLB[] =
            {6554, 16384, 26214, 32767};


/*   Table 4.3b   Quantization levels of the LTP gain quantizer
 */
    /* bc                 0          1        2          3                  */
    public static final short gsm_QLB[] =
            {3277, 11469, 21299, 32767};


/*   Table 4.4   Coefficients of the weighting filter
 */
    /* i                0      1   2    3   4      5      6     7   8   9    10  */
    public static final short gsm_H[] =
            {-134, -374, 0, 2054, 5741, 8192, 5741, 2054, 0, -374, -134};


/*   Table 4.5   Normalized inverse mantissa used to compute xM/xmax
 */
    /* i                  0      1      2      3      4      5     6        7  */
    public static final short gsm_NRFAC[] =
            {29128, 26215, 23832, 21846, 20165, 18725, 17476, 16384};


/*   Table 4.6   Normalized direct mantissa used to compute xM/xmax
 */
    /* i                  0      1       2      3      4      5      6      7   */
    public static final short gsm_FAC[] =
            {18431, 20479, 22527, 24575, 26623, 28671, 30719, 32767};
}