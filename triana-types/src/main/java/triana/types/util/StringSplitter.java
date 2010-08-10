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
package triana.types.util;


import java.util.Iterator;

/**
 * StringSplitter is a utility to divide strings into substrings (called tokens) by recognizing separators (called
 * delimiters).  It provides an Iterator called tokenizer that is a generalization of StringTokenizer.  Whereas
 * StringTokenizer allows only single-character delimiters, StringSplitter allows any string to be defined as a
 * delimiter, and it adds pre-defined delimiter sets called: whitespace, newline, word, paragraph, character, operator.
 * There can be any of a number of different delimiters.  Unlike StringTokenizer, the splitting is done when the object
 * is instantiated, so that the delimiters are fixed once and for all; it is not possible to change delimiter sets for
 * different parts of the string.
 * <p/>
 * Constructors are provided to allow delimiters to be specified as an array of strings, as a StringVector, as a single
 * string (whose characters are assumed to be delimiters, as in StringTokenizer), or as a keyword for some pre-defined
 * delimiter sets: whitespace (the default here and in StringTokenizer), newline, word (uses any punctuation mark or
 * whitespace as a delimiter), paragraph (splits on blank lines or newlines followed by a tab or space), character (no
 * delimiters: each character is a token), and operator (splits on arithmetic operators).
 * <p/>
 * A boolean argument allows the user to choose whether to include the delimiters as tokens.  The default is false: no
 * delimiters.
 * <p/>
 * Various extensions for returning the tokens in a convenient form are provided.  Although the Iterator is provided for
 * compatibility with StringTokenizer, tokens can be more flexibly accessed by using other methods that work by the
 * index of the token, since StringSplitter is an extension of the ArrayList class. Therefore all the methods of the
 * ArrayList class are available in StringSplitter.
 * <p/>
 * A test is also provided to see if the input string begins with a delimiter or not; this is useful if delimiter
 * strings are returned as tokens, since one may want to know if the odd-numbered tokens or the even ones are
 * delimiters.  Another test is provided to see if the input string ends in a delimiter.
 *
 * @author B F Schutz
 * @version $Revision: 4048 $
 */
public class StringSplitter extends StringVector {

    boolean startDelim = false;
    boolean endDelim = false;
    boolean tokensReturned = false;

    /**
     * Constructs an empty StringSpitter with fixed storage capacity and capacityIncrement.
     */
    public StringSplitter() {
        super(100);
    }

    /**
     * Constructs a StringSplitter by tokenizing the String argument on any of the delimiter strings in the String Array
     * argument.
     * <p/>
     * If the boolean argument is true, delimiter sequences will also be returned as tokens.
     *
     * @param str          the string to be split into tokens on whitespace
     * @param delims       the array of strings that act as delimiters between tokens
     * @param returnTokens a boolean to decide if delimiters are included as tokens
     */
    public StringSplitter(String str, String[] delims, boolean returnTokens) {
        super(100);
        splitString(str, delims, returnTokens);
    }

    void splitString(String str, String[] delims, boolean returnTokens) {

        if (str.length() == 0) {
            return;
        }

        int newDelimStart = 0;
        int lastDelimEnd = -1;
        int newTokenStart = 0;
        int lastTokenEnd = -1;
        int tryNewDelim, k;
        boolean lookForMoreDelims, lookForMoreString;
        int numberOfDelims = delims.length;
        int[] delimLength = new int[numberOfDelims];

        tokensReturned = returnTokens;

        for (k = 0; k < numberOfDelims; k++) {
            delimLength[k] = delims[k].length();
            if (str.indexOf(delims[k]) == 0) {
                startDelim = true;
            }
            if (str.endsWith(delims[k])) {
                endDelim = true;
            }
        }
        //      for (k=0; k< numberOfDelims; k++) System.out.println(delims[k]);

        lookForMoreString = true;
        while (lookForMoreString) {
            newDelimStart = str.indexOf(delims[0], lastDelimEnd + 1);
            for (k = 1; k < numberOfDelims; k++) {
                tryNewDelim = str.indexOf(delims[k], lastDelimEnd + 1);
                if ((tryNewDelim > -1) && ((newDelimStart == -1) || (tryNewDelim <= newDelimStart))) {
                    newDelimStart = tryNewDelim;
                }
            }
            if (newDelimStart == -1) {
                this.add(str.substring(lastDelimEnd + 1));
                //	  System.out.println("1: "+str.substring( lastDelimEnd + 1));
                lookForMoreString = false;
            } else {
                this.add(str.substring(lastDelimEnd + 1, newDelimStart));
                //	  System.out.println("2: "+str.substring( lastDelimEnd + 1, newDelimStart));
            }
            if (lookForMoreString) {
                lookForMoreDelims = true;
                lastTokenEnd = newDelimStart - 1;
                newTokenStart = newDelimStart;
                while (lookForMoreDelims) {
                    lookForMoreDelims = false;
                    for (k = 0; k < numberOfDelims; k++) {
                        if (str.startsWith(delims[k], newTokenStart)) {
                            newTokenStart += delimLength[k];
                            lookForMoreDelims = true;
                            break;
                        }
                    }
                }

                lastDelimEnd = newTokenStart - 1;
                if (returnTokens) {
                    this.add(str.substring(lastTokenEnd + 1, newTokenStart));
                    //	    System.out.println("3: "+str.substring( lastTokenEnd + 1, newTokenStart));
                }
            }
        }
    }


    /**
     * Constructs a StringSplitter by tokenizing the first String argument in a manner given by the second String
     * argument.  If the second argument is one of a number of special strings, then specific delimiters are assumed:
     * "whitespace" means split on any whitespace character; "paragraph" means look for "\n\n" (paragraphs separated by
     * a blank line), "\n\t" (paragraphs denoted by a tab indentation) or "\n " (paragraphs denoted by a newline and at
     * least one space before text begins); "word" means use whitespace and punctuation marks as delimiters (the
     * punctuation marks used are the single characters .,;:?!"`()[]{}<> and the two-character combinations "--"  and "'
     * ", so hyphenated or apostrophe'd words are not split); "character" means each character is a separate token; and
     * "operator" means split on arithmetic symbols "+", "-", "*", and "/".  If the second argument is not recognized,
     * then it is assumed to be a string of delimiter characters, and splitting is performed on any of its characters.
     * <p/>
     * The boolean argument determines whether sequences of delimiters are returned as tokens too.
     *
     * @param str          the string to be split into tokens on whitespace
     * @param instruction  the String that gives the rule for splitting
     * @param returnTokens a boolean to decide if delimiters are included as tokens
     */
    public StringSplitter(String str, String instruction, boolean returnTokens) {
        super(100);
        splitInstruction(str, instruction, returnTokens);
    }


    void splitInstruction(String str, String instruction, boolean returnTokens) {
        if (instruction.equalsIgnoreCase("word")) {
            String[] delims = new String[25];
            delims[0] = " ";
            delims[1] = "\t";
            delims[2] = "\n";
            delims[3] = "\r";
            delims[4] = ".";
            delims[5] = ",";
            delims[6] = "?";
            delims[7] = "!";
            delims[8] = "\"";
            delims[9] = "\'";
            delims[10] = "(";
            delims[11] = ")";
            delims[12] = "[";
            delims[13] = "]";
            delims[14] = "{";
            delims[15] = "}";
            delims[16] = "--";
            delims[17] = ";";
            delims[18] = ":";
            delims[19] = "<";
            delims[20] = ">";
            delims[21] = "`";
            delims[22] = " \'";
            delims[23] = "\\";
            delims[24] = "/";
            splitString(str, delims, returnTokens);
        } else if (instruction.equalsIgnoreCase("newline")) {
            String[] delims = new String[2];
            delims[0] = "\n\r";
            delims[1] = "\n";
            splitString(str, delims, returnTokens);
        } else if (instruction.equalsIgnoreCase("paragraph")) {
            String[] delims = new String[10];
            delims[0] = "\n\n";
            delims[1] = "\n\t";
            delims[2] = "\n        ";
            delims[3] = "\n       ";
            delims[4] = "\n      ";
            delims[5] = "\n     ";
            delims[6] = "\n    ";
            delims[7] = "\n   ";
            delims[8] = "\n  ";
            delims[9] = "\n ";
            splitString(str, delims, returnTokens);
        } else if (instruction.equalsIgnoreCase("operator")) {
            String[] delims = new String[4];
            delims[0] = "+";
            delims[1] = "-";
            delims[2] = "*";
            delims[3] = "/";
            splitString(str, delims, returnTokens);
        } else if (instruction.equalsIgnoreCase("whitespace")) {
            String[] delims = new String[4];
            delims[0] = "\n";
            delims[1] = "\t";
            delims[2] = "\r";
            delims[3] = " ";
            splitString(str, delims, returnTokens);
        } else if (instruction.equals("character")) {
            int inputLength = str.length();
            char[] inputChars = str.toCharArray();
            for (int j = 0; j < inputLength; j++) {
                this.add(String.valueOf(inputChars[j]));
            }
        } else {
            int numberOfDelims = instruction.length();
            String[] delims = new String[numberOfDelims];
            for (int k = 0; k < numberOfDelims; k++) {
                delims[k] = instruction.substring(k, k + 1);
            }
            splitString(str, delims, returnTokens);
        }
    }


    /**
     * Constructs a StringSplitter by tokenizing the specified input string on white space.  This is the default if the
     * second string-type argument is omitted.  Include the option to return delimiter strings as tokens.
     *
     * @param str          the string to be split into tokens on whitespace
     * @param returnTokens a boolean to decide if delimiters are included as tokens
     */
    public StringSplitter(String str, boolean returnTokens) {
        this(str, "whitespace", returnTokens);
    }

    /**
     * Constructs a StringSplitter by tokenizing the specified input string on white space.  This is the default if the
     * second string-type argument is omitted.  Since the boolean argument is also absent, assume no delimiters are
     * returned.
     *
     * @param str the string to be split into tokens on whitespace
     */
    public StringSplitter(String str) {
        this(str, "whitespace", false);
    }

    /**
     * Constructs a StringSplitter by tokenizing the String first argument on the delimiters given by the String array
     * second argument. Since the boolean argument is omitted, assume no delimiters are returned.
     *
     * @param str    the string to be split into tokens on whitespace
     * @param delims the array of strings that act as delimiters between tokens
     */
    public StringSplitter(String str, String[] delims) {
        this(str, delims, false);
    }

    /**
     * Constructs a StringSplitter by tokenizing the first String argument according to the instructions given by the
     * second String argument. Since the boolean argument is omitted, assume no delimiters are returned.
     *
     * @param str         the String to be split into tokens on whitespace
     * @param instruction the String that gives the rule for splitting
     */
    public StringSplitter(String str, String instruction) {
        this(str, instruction, false);
    }

    /**
     * Constructs a StringSplitter by tokenizing the String first argument on the delimiters given by the StringVector
     * second argument.
     * <p/>
     * The boolean argument determines whether sequences of delimiters are returned as tokens too.
     *
     * @param str          the string to be split into tokens on whitespace
     * @param delimVector  the array of strings that act as delimiters between tokens
     * @param returnTokens a boolean to decide if delimiters are included as tokens
     */
    public StringSplitter(String str, StringVector delimVector, boolean returnTokens) {
        super(100);
        splitStringVector(str, delimVector, returnTokens);
    }

    void splitStringVector(String str, StringVector delimVector, boolean returnTokens) {
        Object[] temp = new Object[delimVector.size()];
        delimVector.copyInto(temp);
        String[] delims = temp[0] instanceof String ? (String[]) temp : null;
        splitString(str, delims, returnTokens);
    }


    /**
     * Constructs a StringSplitter by tokenizing the String first argument on the delimiters given by the StringVector
     * second argument. Since the boolean argument is absent, the default of not returning delimiters is assumed.
     *
     * @param str         the string to be split into tokens on whitespace
     * @param delimVector the array of strings that act as delimiters between tokens
     */
    public StringSplitter(String str, StringVector delimVector) {
        this(str, delimVector, false);
    }

    /**
     * @return an Iterator that acts like StringTokenizer.
     */
    public final synchronized Iterator tokenizer() {
        return super.iterator();
    }


    /**
     * @return the number of tokens generated by splitting the string.
     */
    public final int countTokens() {
        return super.size();
    }

    /**
     * @return true if delimiters were returned as tokens.
     */
    public final boolean containsTokens() {
        return tokensReturned;
    }

    /**
     * @return true if the input string begins with a delimiter. (Returns answer independently of whether delimiters
     *         will be returned as tokens.)
     */
    public final boolean beginsWithDelimiter() {
        return startDelim;
    }

    /**
     * @return true if the input string ends with a delimiter. (Returns answer independently of whether delimiters will
     *         be returned as tokens.)
     */
    public final boolean endsWithDelimiter() {
        return endDelim;
    }

    /**
     * @return the first non-delimiter token (first token if delimiters are not made into tokens).
     */
    public final String firstNonDelimiterElement() {
        if (startDelim) {
            return at(1);
        }
        return first();
    }

    /**
     * @return the last non-delimiter token (last token if delimiters are not made into tokens).
     */
    public final String lastNonDelimiterElement() {
        if ((containsTokens()) && (endDelim)) {
            return at(countTokens() - 2);
        }
        return (String) last();
    }

    /**
     * @return the first non-delimiter token (first token if delimiters are not made into tokens).  Identical to
     *         firstNonDelimiterElement().
     */
    public final String firstNonDelimiterToken() {
        if (startDelim) {
            return at(1);
        }
        return first();
    }

    /**
     * @return the last non-delimiter token (last token if delimiters are not made into tokens).  Identical to
     *         lastNonDelimiterElement().
     */
    public final String lastNonDelimiterToken() {
        if ((containsTokens()) && (endDelim)) {
            return at(countTokens() - 2);
        }
        return (String) last();
    }

    /**
     * @return a String array representation of the tokens. /todo this causes a classcast exception
     */
    public final String[] toStringArray() {
        Object[] temp = new Object[size()];
        copyInto(temp);
        return (String[]) temp;
    }

}













