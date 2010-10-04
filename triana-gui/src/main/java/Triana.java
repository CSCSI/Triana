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


import org.apache.commons.logging.Log;
import org.trianacode.TrianaInstance;
import org.trianacode.config.Locations;
import org.trianacode.config.cl.ArgumentParsingException;
import org.trianacode.config.cl.OptionValues;
import org.trianacode.config.cl.OptionsHandler;
import org.trianacode.config.cl.TrianaOptions;
import org.trianacode.enactment.Exec;
import org.trianacode.enactment.logging.Loggers;
import org.trianacode.gui.hci.ApplicationFrame;

/**
 * Main Launcher Class
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class Triana {
    static Log log = Loggers.LOGGER;

    /**
     * the triana arguments
     */
    public static final String LOG_LEVEL = "l";
    public static final String ISOLATED_LOGGER = "i";
    public static final String WORKFLOW = "w";
    public static final String NOGUI = "n";

    /**
     * non gui arguments
     */

    /**
     * executes a workflow takes a workflow file name
     */
    public static final String EXECUTE = "e";
    /**
     * takes a running instance id. Used for interacting with a running taskgraph
     */
    public static final String UUID = "u";
    /**
     * combined with UUID, retrieves current status
     */
    public static final String SERVER = "s";

    public static final String HELP = "h";


    /**
     * The main program for Triana
     */
    public static void main(String[] args) throws Exception {

        String os = Locations.os();
        String usage = "./triana.sh";
        if (os.equals("windows")) {
            usage = "triana.bat";
        }

        OptionsHandler parser = new OptionsHandler(usage, TrianaOptions.TRIANA_OPTIONS);
        OptionValues vals = null;
        try {
            vals = parser.parse(args);
        } catch (ArgumentParsingException e) {
            System.out.println(e.getMessage());
            System.out.println(parser.usage());
            System.exit(0);
        }
        boolean help = vals.hasOption(HELP);
        if (help) {
            System.out.println(parser.usage());
            System.exit(0);
        }
        String logLevel = vals.getOptionValue(LOG_LEVEL);

        String logger = vals.getOptionValue(ISOLATED_LOGGER);
        if (logger != null) {
            Loggers.isolateLogger(logger, logLevel == null ? "INFO" : logLevel);
        } else {
            if (logLevel != null) {
                Loggers.setLogLevel(logLevel);
            }
        }
        boolean runNoGui = vals.hasOption(NOGUI);
        boolean pid = vals.hasOption(UUID);
        boolean exec = vals.hasOption(EXECUTE);
        boolean server = vals.hasOptionValue(SERVER);
        boolean workflow = vals.hasOptionValue(WORKFLOW);
        if (runNoGui) {
            if (!server) {
                if (pid || exec || workflow) {
                    Exec.exec(args);
                } else {
                    System.out.println("Non-gui mode combined with non-server mode requires either a uuid, or a workflow");
                    System.out.println(parser.usage());
                    System.exit(0);
                }
            } else {
                new TrianaInstance(args, null);
            }
        } else {
            ApplicationFrame.initTriana(args);
        }

    }


}
