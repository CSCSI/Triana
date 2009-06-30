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
package org.trianacode.util;


import org.trianacode.gui.panels.ScrollingMessageFrame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * CompileUtil is a class which finds the java compiler and compiles
 * a java source file.  Its intended to be used for the Triana units.
 *
 * @author Ian Taylor
 * @version $Revision: 4048 $
 * @created 1 Dec 1999
 * @date $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public final class CompileUtil {

    /**
     * the file to be compiled
     */
    private String javaFile;

    /**
     * the class destination directory
     */
    private String destDir;

    /**
     * the source directory
     */
    private String sourcepath = "";

    /**
     * the compiler path (default = Env.getCompilerCommand())
     */
    private String compilerPath = Env.getCompilerCommand();

    /**
     * the compiler classpath (default = Env.getClasspath())
     */
    private String classpath = Env.getClasspath();

    /**
     * the additonal arguments for the compiler
     */
    private String arguments = "";

    /**
     * the main compiler window
     */
    private ScrollingMessageFrame compileScreen;

    /**
     * a flag indicating whether the compiler screen is enabled
     */
    private boolean screenenabled = true;
    private boolean appendOuput;


    /**
     * Default constructor
     */
    public CompileUtil() {
    }

    public CompileUtil(String destDir, boolean compilewindow, boolean appendOuput) {
        this.appendOuput = appendOuput;
        this.destDir = destDir;

        setCompilerScreenEnabled(compilewindow);
        initCompileScreen();
    }

    /**
     * Constructor
     *
     * @param javaFile      file to compile
     * @param destDir       destination directory for compiled class file
     * @param compilewindow if true show the compile window with output
     */
    public CompileUtil(String javaFile, String destDir, boolean compilewindow) {
        setJavaFile(javaFile);
        this.destDir = destDir;
        appendOuput = false;

        setCompilerScreenEnabled(compilewindow);
        initCompileScreen();

    }

    public String getJavaFile() {
        return javaFile;
    }

    public void setJavaFile(String javaFile) {
        this.javaFile = addJavaToFile(javaFile);
    }

    public String getDestDir() {
        return destDir;
    }

    public void setDestDir(String destDir) {
        this.destDir = destDir;
    }

    public String getSourcepath() {
        return sourcepath;
    }

    public void setSourcepath(String sourcepath) {
        this.sourcepath = sourcepath;
    }

    /**
     * Initialise compile screen
     */
    private void initCompileScreen() {
        if (isCompilerScreenEnabled()) {

            if (appendOuput)
                compileScreen = new ScrollingMessageFrame("Triana Compiling: All Tools");
            else
                compileScreen = new ScrollingMessageFrame("Triana Compiling: " + new File(javaFile).getName());
        }
    }

    /**
     * Sets compiler command
     */
    public void setCompilerLocation(String path) {
        compilerPath = path;
    }

    /**
     * @return the current compiler command
     */
    public String getCompilerLocation() {
        return compilerPath;
    }

    /**
     * Sets the compiler classpath
     */
    public void setCompilerClasspath(String classpath) {
        this.classpath = classpath;
    }

    /**
     * @return the compiler classpath
     */
    public String getCompilerClasspath() {
        return classpath;
    }

    /**
     * Sets the additional compiler arguments
     */
    public void setCompilerArguments(String arguments) {
        this.arguments = arguments;
    }

    /**
     * @return the additional compiler arguments
     */
    public String getCompilerArguments() {
        return arguments;
    }


    /**
     * @return the additional compiler arguments
     */
    public String getCompilerFile() {
        return arguments;
    }


    /**
     * Turns the compiler screen on/off
     */
    private void setCompilerScreenEnabled(boolean state) {
        screenenabled = state;
    }

    /**
     * @return true if the compiler screen is enabled
     */
    public boolean isCompilerScreenEnabled() {
        return screenenabled;
    }


    /**
     * Sets the size of the compiler screen size
     */
    public void setComplieScreenSize(int width, int height) {
        compileScreen.setScreenSize(width, height);
    }

    /**
     * shows/hides the compile screen
     */
    public void setCompileScreenVisible(boolean state) {
        if (isCompilerScreenEnabled())
            compileScreen.setVisible(state);
    }


    /**
     * outputs a line to the compile screen
     */
    public void printCompilerLine(String line) {
        compileScreen.println(line);
    }


    /**
     * Adds a ".java" extension to the file if it hasn't already got
     * one.
     */
    private final static String addJavaToFile(String javaFile) {
        if (!javaFile.endsWith(".java"))
            javaFile += ".java";

        return javaFile;
    }


    /**
     * Executes the current compiler compilerCommand
     */
    public void compile() throws CompilerException, FileNotFoundException {
        Process process;
        BufferedReader errorreader;
        String str;
        boolean errors = false;
        String errLog = "";
        Vector commmandStrVector = new Vector();
        commmandStrVector.add(getCompilerLocation());
        String args = getCompilerArguments();
        if (!args.equals("")) {
            StringTokenizer tok = new StringTokenizer(args);
            while (tok.hasMoreTokens()) ;
            commmandStrVector.add(tok.nextToken());
        }
        if (!sourcepath.equals("")) {
            commmandStrVector.add("-sourcepath");
            commmandStrVector.add(sourcepath);
        }
        commmandStrVector.add("-classpath");
        commmandStrVector.add(getCompilerClasspath());
        commmandStrVector.add(javaFile);
        commmandStrVector.add("-d");
        commmandStrVector.add(destDir);

        File cmdFile = new File(getCompilerLocation());
        if (cmdFile.isAbsolute() && !cmdFile.exists())
            throw (new FileNotFoundException("Java compiler not found: " + getCompilerLocation()));

        if (!new File(destDir).exists())
            new File(destDir).mkdirs();

        StringBuffer compilerStrBuff = new StringBuffer();
        for (Iterator iterator = commmandStrVector.iterator(); iterator.hasNext();) {
            compilerStrBuff.append((String) iterator.next());
            compilerStrBuff.append(" ");

        }
        printCompilerLine("Compiling: " + javaFile);
        if (!appendOuput) {
            printCompilerLine(compilerStrBuff.toString());
            printCompilerLine(Env.getString("compilerWait"));
        }
        try {
            String[] cmdarray = (String[]) commmandStrVector.toArray(new String[commmandStrVector.size()]);

            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(cmdarray);  // execute command

            errorreader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((str = errorreader.readLine()) != null) {
                if (!str.startsWith("Note:"))
                    errors = true;

                errLog += str + "\n";
            }

            errorreader.close();
        } catch (Exception except) {
            except.printStackTrace();
        }

        if (!errors) {
            printCompilerLine(errLog);
            printCompilerLine(Env.getString("compilerSuccess"));
        } else {
            if (!appendOuput)
                printCompilerLine(errLog);
            throw (new CompilerException(errLog));
        }

        printCompilerLine(Env.getString("compilerFinish"));
    }

}












