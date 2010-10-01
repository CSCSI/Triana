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

//    $Id: GSMDriver.java 981 2003-05-29 13:59:10Z spxmss $

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


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GSMDriver {

    public static void main(String argv[]) {

        GSMDecoder myDecoder = new GSMDecoder();

        byte input[] = new byte[33];
        int output[];

        FileInputStream fis = null;
        FileOutputStream fos = null;

        byte outBytes[] = new byte[320];

        if (argv.length < 2) {
            System.err.print("Usage: GSMDriver inputfile outputfile");
            System.exit(1);
        }

        try {
            fis = new FileInputStream(argv[0]);
            fos = new FileOutputStream(argv[1]);
        }
        catch (Exception e) {
            System.err.println("file not found, or can't open.");
            System.exit(2);
        }

        while (true) {

            try {
                if (fis.read(input) <= 0) {
                    break;
                }
            }
            catch (java.io.IOException e) {
                System.err.println("error reading input");
                break;
            }

            try {
                output = myDecoder.decode(input);

                for (int i = 0; i < output.length; i++) {
                    int index = i << 1;
                    outBytes[index] = (byte) (output[i] & 0x00ff);
                    outBytes[++index] = (byte) ((output[i] & 0xff00) >> 8);
                }

//	try {
                fos.write(outBytes);
            }
            catch (IOException e) {
                System.err.println("error writing output");
                break;
            }


//      } catch (InvalidGSMFrameException e) {
//	System.err.println("bad frame");
            // break;
            // }

        }

        try {
            fis.close();
            fos.close();
        }
        catch (IOException e) {
            System.err.println("error closing files.");
        }

    }

}
