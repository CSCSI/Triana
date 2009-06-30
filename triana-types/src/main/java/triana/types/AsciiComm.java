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
package triana.types;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * AsciiCommype (AsciiComm Type) outlines a set of functions which have to be
 * implemented for each Triana Type which wants to be able to
 * communicate with other programs via streams e.g. the Client and
 * Server classes use this to send data out of Triana via socket etc.
 * Also, each type which implements this interface can be used to output
 * and input their data by using the ATImport (Ascii Type importer)
 * and ATExport (Ascii Type exporter) modules.
 * </p><p>
 * All the functions here use ASCII (UniCode) transfer as opposed to
 * binary. This is useful when incompatabilities between various computer
 * platforms cause problems when using binary transfer (i.e. little and big endian).
 * </p><p>
 * Note that we ONLY now inplement ASCII transfer because Java has
 * incorporated the Serializable interface which does the same
 * as the old Binary transfer.  This will be implemented for
 * each type.
 *
 * @see TrianaType
 * @see GraphType
 * @see VectorType
 * @see Spectrum
 * @see Document
 *
 * @author      Ian Taylor
 * @created     20 August 2000
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public interface AsciiComm {

    /**
     * Used when Triana types want to be able to receive
     * ASCII data from the output of other programs.  This is used to
     * implement socket and to run other executables, written in C etc.
     * With ASCII you don'y have to worry about
     * ENDIANness as the conversions are all done via text. This is
     * obviously slower than the binary version since you have to format
     * the input and output within the another program.
     */
    public void inputFromStream(BufferedReader dis) throws IOException;

    /**
     * Used when Triana types want to be able to send their
     * data via an output stream to other programs using strings.  This
     * can be used to implement socket and to run other executables,
     * written in C etc. With ASCII you don't have to worry about
     * ENDIANness as the conversions are all done via text. This is
     * obviously slower than the binary version since you have to format
     * the input and output within the another program.
     */
    public void outputToStream(PrintWriter dos) throws IOException;
}















