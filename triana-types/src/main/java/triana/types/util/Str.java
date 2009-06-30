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


import java.awt.*;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * Str is a class which contains static functions which perform a
 * number of useful repetitive function in java. e.g. when converting from
 * a String to a float, you just simply type in :- </p><p>
 * <center>
 * myfloat = Str.strToFloat(str); <br>
 * instead of<br>
 * myfloat = Float.valueOf(str).floatValue();
 * </center></p>
 * <p> The latter, I find really annoying! The routines here therefore colate
 * a number of frequently performed procedures and provide shortcuts
 * to their operation. </p>
 *
 * Also, there are routines for counting the line numbers and
 * finding and replacing etc.
 *
 *
 * @author      Ian Taylor
 * @author      Bernard Schutz
 * @created     11 June 2001
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public final class Str {
    private static double lastDouble = 0.0;
    private static float lastFloat = (float) 0.0;
    private static int lastInt = 0;
    private static byte lastByte = 0;
    private static boolean lastBoolean = false;
    private static short lastShort = 0;
    private static long lastLong = 0;

    private static int count = 0;
    private static int pos = 0;

    /**
     * Copies a String, not by reference
     */
    public static String copy(String s) {
        if (s == null) return null;
        if (s == "") return "";
        StringBuffer sb = new StringBuffer(s);
        String s1 = new String(sb);
        return s1;
    }


    /**
     * Attempts to convert a String to a float
     */
    public static float strToFloat(String str) throws
            NumberFormatException, NullPointerException {
        if (!str.trim().equals(""))
            return (lastFloat = Float.valueOf(str).floatValue());
        else
            return lastFloat;
    }

    /**
     * Attempts to convert a String to a double
     */
    public static double strToDouble(String str) throws
            NumberFormatException, NullPointerException {
        if (!str.trim().equals(""))
            return (lastDouble = Double.valueOf(str).doubleValue());
        else
            return lastDouble;
    }

    /**
     * Attempts to convert a String to an int
     */
    public static int strToInt(String str) throws
            NumberFormatException, NullPointerException {
        if (!str.trim().equals(""))
            return (lastInt = Integer.valueOf(str).intValue());
        else
            return lastInt;
    }

    /**
     * Attempts to convert a String to a short
     */
    public static short strToShort(String str) throws
            NumberFormatException, NullPointerException {
        if (!str.trim().equals(""))
            return (lastShort = Short.valueOf(str).shortValue());
        else
            return lastShort;
    }

    /**
     * Attempts to convert a String to a long
     */
    public static long strToLong(String str) throws
            NumberFormatException, NullPointerException {
        if (!str.trim().equals(""))
            return (lastLong = Long.valueOf(str).longValue());
        else
            return lastLong;
    }

    /**
     * Attempts to convert a String to a byte
     */
    public static byte strToByte(String str) throws
            NumberFormatException, NullPointerException {
        if (!str.trim().equals(""))
            return (lastByte = Byte.valueOf(str).byteValue());
        else
            return lastByte;
    }

    /**
     * Attempts to convert a String to a boolean
     */
    public static boolean strToBoolean(String str) throws
            NumberFormatException, NullPointerException {
        if (str.trim().equals(""))
            return lastBoolean;
        if (str.trim().equals("true"))
            lastBoolean = true;
        else
            lastBoolean = false;
        return lastBoolean;
    }

    /**
     * @return a "19 Mar 1997" type format of the date. Gets rid of
     * the time etc.
     */
    public final static String niceDate(Date d) {
        StringVector items = new StringVector(10);  // 10 should be OK

        String line = d.toString();

        StringTokenizer st = new StringTokenizer(line);

        while (st.hasMoreTokens()) {
            items.addElement(st.nextToken());
        }

        return items.at(2) + " " + items.at(1) + " " +
                items.at(items.size() - 1);
    }

    /**
     * @return a "19 Mar 1997" type format of the date. Gets rid of
     * the time etc. Give new Date() as argument if the current time
     * and date is needed.
     */
    public final static String niceDateAndTime(Date d) {
        StringVector items = new StringVector(10);  // 10 should be OK

        String line = d.toString();

        StringTokenizer st = new StringTokenizer(line);

        while (st.hasMoreTokens()) {
            items.addElement(st.nextToken());
        }

        return items.at(0) + " " + items.at(2) + " " + items.at(1)
                + " " + items.at(5) + " " +
                items.at(3) + " (" + items.at(4) + ")";
    }

    /**
     * Finds the next occurence of the given string <i>text</i> from the
     * string <i>theString</i> and replaces it with the <i>newText</i>.
     * This should be used with caution: it uses a counter to keep
     * track of how many occurences there have been,  but this counter
     * will be advanced by any call to this method, even one that searches
     * a different String. It is safer to use the version with the position
     * argument and keep track of the position in the calling program.
     *
     * @param theString the String to scan
     * @param text the text to find within theString
     * @param newText the String to replace <i>text</i> with
     * @return the String after replacement
     */
    public static String replaceNext(String theString, String text,
                                     String newText) {
        pos = theString.indexOf(text, pos);
        if ((pos == -1) || (pos > theString.length()))
            return null;

        theString = theString.substring(0, pos) +
                theString.substring(pos + text.length());

        StringBuffer sb = new StringBuffer(theString);
        sb.insert(pos, newText);
        return sb.toString();
    }

    /**
     * Starting at the given position, this method
     * finds the next occurence of the given string <i>text</i> from the
     * string <i>theString</i> and replaces it with the <i>newText</i>.
     *
     * @param theString the String to scan
     * @param pos the starting index within the given string
     * @param text the text to find within theString
     * @param newText the String to replace <i>text</i> with
     * @return the String after replacement
     */
    public static String replaceNext(String theString, int pos, String text,
                                     String newText) {
        if (pos > theString.length() || (pos < 0)) return null;
        pos = theString.indexOf(text, pos);
        if (pos == -1) return null;

        StringBuffer sb = new StringBuffer(theString);
        sb.replace(pos, pos + text.length(), newText);
        return sb.toString();
    }

    /**
     * Inserts a String into a given String at a given position.
     * The insertion is made so that the first character of the inserted
     * String will be at the given position in the returned String.
     * If the given position is not within the initial String (negative or
     * too large) then the original String is return without modification.
     *
     * @param theString The String to be modified
     * @param pos The index at which the insertion is to be made
     * @param text The insertion String
     * @return The modified String
     */
    public static String insertString(String theString, int pos, String text) {
        if ((pos < 0) || (pos >= theString.length())) return theString;
        return (new StringBuffer(theString)).insert(pos, text).toString();
    }

    /**
     * Removes a substring from the given String and returns a String
     * consisting of the remaining parts concatenated.
     *
     * @param theString The String to be modified
     * @param from The index of the first character to be removed
     * @param to The index of the first character that remains after the removed String
     * @return The modified String
     */
    public String removeSubstring(String theString, int from, int to) {
        if ((to < from) || (from < 0) || (to > theString.length())) return theString;
        return (new StringBuffer(theString)).delete(from, to).toString();
    }


    /**
     * Replace all occurances of <i>text</i>  with <i>newText</i> from
     * <i>theString</i>.
     * @param theString the string to scan
     * @param text the text to find within theString
     * @param newText the string to replace <i>text</i> with
     * @return the new String after all the relevant text has been replaced
     */
    public static String replaceAll(String theString, String text,
                                    String newText) {
        StringBuffer buf = new StringBuffer(theString);
        int loc = 0;
        int oldLength = text.length();
        int newLength = newText.length();

        while ((loc = buf.toString().indexOf(text, loc)) != -1) {
            //System.out.println("Str.replaceAll() has buf = \n" + buf.toString() +  "    with location = " + String.valueOf( loc ) );
            buf = buf.replace(loc, loc + oldLength, newText);
            //System.out.println("Str.replaceAll() has buf = \n" + buf.toString() +  "    after the replacement.");
            loc += newLength;
            if (loc >= buf.length()) break;
        }

        return buf.toString();
    }

    /**
     * From the specified position in the String this function
     * returns the number of characters <b>excluding line feeds
     * </b> for this position. JTextAreas don't seem to use line
     * feeds for example.
     */
    public static int noLineChars(String str, int pos) {
        int curr = 0;
        int old = 0;
        int count = 0;
        String lineSep = "\n";
        String carrRet = "\r";
        int sepLen = lineSep.length();
        int retLen = carrRet.length();

        curr = str.indexOf(lineSep, old);

        while ((curr < pos) && (curr != -1)) {
            old = curr;
            curr = str.indexOf(lineSep, old);
            if (!str.substring(old, curr).trim().equals(""))
                count += sepLen;
            if (!str.substring(curr - retLen,
                               curr + sepLen).equals(carrRet + lineSep))
                count += retLen;
            curr += sepLen;
        }

        System.out.println("Offset by " + count);
        return pos - count;
    }

    /**
     * gets number of replacings made by the replaceAll function.
     */
    public static int howMany() {
        return count;
    }

    /**
     * Splits the text by items separated by white space
     */
    public static StringVector splitTextBySpace(String text) {
        return new StringSplitter(text, "whitespace", false);
    }

    /**
     * This function splits the text into a vector of lines
     * contained within that text i.e. splits the text on the
     * newline character.
     */
    public static StringVector splitText(String text) {
        return new StringSplitter(text, "newline", false);
    }

    /**
     * Prints a vector one element after another with a new line
     * between each element.
     */
    public static void printVector(Vector v, PrintWriter pw) {
        for (int i = 0; i < v.size(); ++i) {
            if (v.elementAt(i) instanceof URL) {
                URL url = (URL) v.elementAt(i);
                pw.println(url.getProtocol() + "://" + url.getHost() +
                           url.getFile() + " ");
            }
            else if (v.elementAt(i) instanceof ZipFile)
                pw.println(((ZipFile) v.elementAt(i)).getName() + " ");
            else
                pw.println(v.elementAt(i).toString() + " ");
        }
    }

    /**
     * For printing out the types and associated colours to the user
     * configuration file. Can be also used to print out any hashtable
     * objects in a :- </p>
     * <center> colorname actualcolor<line feed>
     * </center>
     * <p> format
     */
    public static void printHashtable(Hashtable v, PrintWriter pw) {
        int max = v.size() - 1;
        String s1, s2;
        Object el;

        Enumeration k = v.keys();
        Enumeration e = v.elements();

        for (int i = 0; i <= max; i++) {
            s1 = k.nextElement().toString();
            el = e.nextElement();
            if (el instanceof Color) {
                Color c = (Color) el;
                s2 = String.valueOf(c.getRed()) + " " +
                        String.valueOf(c.getGreen()) + " " +
                        String.valueOf(c.getBlue());
            }
            else
                s2 = el.toString();
            pw.print(s1 + " " + s2 + "\n");
        }
    }

    /**
     * For printing out the types and associated colours to the user
     * configuration file. Can be also used to print out any hashtable
     * objects in a :- </p>
     * <center> colorname actualcolor<line feed>
     * </center>
     * <p> format
     */
    public static void printTreeMap(TreeMap v, PrintWriter pw) {
        String s1, s2;
        Object el;

        Object k[] = v.keySet().toArray();

        for (int i = 0; i < k.length; i++) {
            s1 = (String) k[i];
            el = v.get(s1);
            if (el instanceof Color) {
                Color c = (Color) el;
                s2 = String.valueOf(c.getRed()) + " " +
                        String.valueOf(c.getGreen()) + " " +
                        String.valueOf(c.getBlue());
            }
            else
                s2 = el.toString();
            pw.print(s1 + " " + s2 + "\n");
        }
    }

}












