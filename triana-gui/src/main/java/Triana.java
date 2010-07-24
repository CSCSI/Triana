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

import org.trianacode.Bootstrap.Epicenter;
import org.trianacode.gui.hci.ApplicationFrame;

import java.util.logging.Logger;

/**
 * Main Launcher Class
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 * @created May 21, 2004: 7:06:33 PM
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class Triana {
    static Logger log = Logger.getLogger("Triana");

    static Epicenter epicenter;

    /**
     * the triana arguments
     */
    private static final String DEBUG_ARG = "-debug";
    private static final String NODEBUG_ARG = "-nodebug";
    private static final String HELP_ARG1 = "-help";
    private static final String HELP_ARG2 = "-?";

    /**
     * The main program for the ApplicationFrame class
     */
    public static void main(String[] args) throws Exception {

        epicenter = new Epicenter();       // Just a place to find things easier ....  Change as you feel fit.
        
        // ToDo: should have factory code to put OS specifics in
		/*String myOSName = Env.os();
        if (myOSName.equals("osx")) {
            UIManager.setLookAndFeel(QuaquaManager.getLookAndFeelClassName());
        }
		else if(myOSName.equals("Windows XP") || myOSName.equals("Windows 2000")
                || myOSName.equals("Windows NT") || myOSName.equals("windows") )

		{
			//XP Look and feel
			try
			{
			  UIManager.setLookAndFeel(
			  	"net.java.plaf.windows.WindowsLookAndFeel"
			  );
			}
			catch ( Exception e )
			{
				//this error is non critical
				e.printStackTrace();
			}
		}
		else
		{
			//assume linux / unix
			try
			{
				UIManager.setLookAndFeel(new MetouiaLookAndFeel());
			}
			catch(Exception e)
			{
				//this error is non critical
				e.printStackTrace();
			}
		}*/
        boolean starttriana = true;



        for (int count = 0; count < args.length; count++) {
            if (args[count].equals(HELP_ARG1) || args[count].equals(HELP_ARG2)) {
                printArgumentHelp();
                starttriana = false;
            }
        }

        if (starttriana) {
            ApplicationFrame.initTriana();
        }
        else
            System.exit(0);
    }

    /**
     * Outputs help on the Triana arguments to the command line
     */
    private static void printArgumentHelp() {

        int colwidth = 15;

        System.out.println("Usage: triana <options>");
        System.out.println("where possible options include:");
        System.out.println("     " + DEBUG_ARG + getPadStr(DEBUG_ARG, colwidth) + "Stream debug information to command line");
        System.out.println("     " + NODEBUG_ARG + getPadStr(NODEBUG_ARG, colwidth) + "Disable all debug output");
        System.out.println("     " + HELP_ARG1 + " " + HELP_ARG2 + getPadStr(HELP_ARG1 + " " + HELP_ARG2, colwidth) + "Show argument help");
    }

    /**
     * @return a column padding string for the argument help
     */
    private static String getPadStr(String option, int length) {
        return "                                              ".substring(0, length - option.length());
    }


}
