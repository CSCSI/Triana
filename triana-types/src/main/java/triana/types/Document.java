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


import triana.types.util.Str;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Document is a type which stores the contents
 * of a Document in a String (or just encapsulates any string) with an optional
 * file name. There are parameters for marking special lines and
 * special groups of characters, for search-and-replace units to work with.
 * In order to keep the line- and group-markers pointing to the same text
 * after the contents have been modified, use the methods <i>cutString</i>,
 * <i>insertString</i>, and <i>replaceString</i>. To replace the entire
 * document and wipe out markers, use <i>setText()</i>.
 *
 * The markers for groups of characters are held in a FIFO stack, each element
 * of which is an ArrayList of pairs of integers, marking regions of the
 * document. Each member of the stack contains regions that are refinements
 * of (<i>i.e.</i> contained within) the regions delineated in the previous
 * element. The pointer <i>charGroups</i> points to the most refined
 * level, the pointer <i>domain</i> points to the next most refined level.
 * Methos allow editing units to select regions, refine them, edit them,
 * retreat to the previous level of selection, etc.
 *
 * @author      Ian Taylor
 * @author      Bernard Schutz
 * @created     10 October 2001
 * @version     $Revision: 4048 $
 * @date        $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class Document extends TrianaType implements AsciiComm {

    /**
     * Contains the optional file name (FileName is a TrianaType
     * data type) under which this Document is stored.
     */
    private FileName fname = null;

    /** Holds a key to the Hashtable in TrianaType which will hold the Document.
     */
    private String doc = "Document";

    /**
     * Array of integers holding the line numbers of lines that have
     * been marked. Allows search, replace, and display units to refer
     * to specific lines.
     */
    private int[] lineNos = new int[1];

    /**
     * Stack of ArrayLists of pairs (int[2]'s) of integers holding the character ranges
     * that have been marked. The elements of this list should be ArrayLists.
     * Each ArrayList will contain int[2]'s. Each int[2] holds the indices
     * of the starting and ending characters of a single group of characters.
     * Allows search, replace, and display units to refer
     * to specific groups of characters.
     */
    private ArrayList charGroupStack = new ArrayList(1);

    /**
     * Pointer to the most recent entry in the stack <i>charGroupStack</i>,
     * which is regarded as the current list of selected character ranges.
     */
    private ArrayList charGroups;

    /**
     * Pointer to the second-most recent entry in the stack <i>charGroupStack</i>,
     * which is regarded as the domain from within which the regions defined in
     * <i>charGroups</i> were selected.
     */
    private ArrayList domain;

    /*
     * The end-of-line marker used by this method. As its value is
     * static, it does not need to be copied with other parameters in the
     * copy methods. Its value is platform-dependent.
     */
    private static String eol = System.getProperty("line.separator");

    /*
     * Use to get debugging output. Allows a variety of writes to System.out.
     * Need to recompile Document to change this value.
     */
    private boolean debug = false;


    /**
     * Creates a new empty Document, setting its state to <i>false</i>.
     */
    public Document() {
    }

    /**
     * Creates a new Document with the contents set to the specified String.
     *
     * @param contents the String containing the contents of the Document.
     */
    public Document(String contents) {
        setText(contents);
    }

    /**
     * Creates a new Document with the contents set to the specified String and
     * with a String representing the full path and name of the file
     * which the Document was taken from.
     *
     * @param contents the String containing the contents of the Document.
     * @param fileAndPath the String containing the full pathname of the file where the Document came from.
     */
    public Document(String contents, String fileAndPath) {
        setText(contents);
        fname = new FileName(fileAndPath);
    }

    /**
     * Creates a new Document with the contents set to the specified String and
     * with a String representing the full path and name of the file
     * which the Document was taken from.
     *
     * @param contents the String containing the contents of the Document.
     * @param file the FileName object containing the file where the Document came from.
     */
    public Document(String contents, FileName file) {
        setText(contents);
        fname = file;
    }

    /**
     * Creates a new Document with the contents set to the specified String,
     * with the given name of the file from which the Document was
     * taken, and with the given directory for the Document.
     *
     * @param contents The contents of the Document.
     * @param file The name of the file where the Document came from.
     * @param directory The name of the directory where the Document came from.
     */
    public Document(String contents, String file, String directory) {
        setText(contents);
        fname = new FileName(file, directory);
    }

    /**
     * @return String a reference to the contents of this Document
     */
    public String getText() {
        Object res = getFromContainer(doc);

        if (res == null)
            return "";
        return (String) res;
    }

    /**
     * Gets the whole stack of ArrayLists that denote character groups and their
     * refinements. Used for copying but not much else. There is no method to set this.
     *
     * @return ArrayList The stack of character group ArrayLists
     */
    public ArrayList getCharGroupStack() {
        return charGroupStack;
    }

    /**
     * Sets the given String to be the contents of the object and eliminates
     * any character group delimiters because they are unlikely to be
     * relevant to new text. If simple modifications of the text are
     * desired, preserving groups, use the methods <i>cutString</i>,
     * <i>insertString</i>, <i>replaceString</i>, and <i>add</i>. Initially only one
     * character group is defined, spanning the whole document. The
     * pointer domain is set to null since there is only one element on
     * the stack.
     *
     * @param newContents A string of the new contents
     */
    public void setText(String newContents) {
        charGroups = new ArrayList(10);
        if (newContents.length() > 0) {
            insertIntoContainer(doc, newContents);
            int[] wholeDoc = {0, newContents.length() - 1};
            charGroups.add(wholeDoc);
        }
        charGroupStack.add(charGroups);
        if (debug) System.out.println("Document.setText: added new level, total levels = " + String.valueOf(charGroupStack.size()) + "\nDocument.setText: here are the populations of the character group levels: ");
        if (debug) for ( int k = 0; k < charGroupStack.size(); k++ ) System.out.print(String.valueOf( ((ArrayList)charGroupStack.get(k)).size() ) + ", " );
        if (debug) System.out.print("\n");
        if (debug) System.out.println("Document.setText: added new level, total levels = " + String.valueOf(charGroupStack.size()) + "\nDocument.setText: here are the populations of the character group levels: ");
        if (debug) for ( int k = 0; k < charGroupStack.size(); k++ ) System.out.print(String.valueOf( ((ArrayList)charGroupStack.get(k)).size() ) + ", " );
        if (debug) System.out.print("\n");
        domain = null;
        updateObsoletePointers();
    }

    /**
     * Returns the substring denoted by the given character
     * positions, inclusive. This does not modify the document
     * in any way.
     *
     * @param start The location of the first character to be returned
     * @param end The location of the last character to be returned
     */
    public String getString(int start, int end) {
        return getText().substring(start, end + 1);
    }


    /**
     * Removes and returns the character String between the two given
     * index positions, including the two positions. Returns null if
     * either index is out of bounds. Readjusts all character group
     * locations and interesting line numbers after the given string,
     * so that they point to the same text as before. Any groups with
     * delimiters inside the region to be cut are eliminated.
     *
     * @param start First character of string to be cut
     * @param end Last character of the string to be cut
     * @return The string that has been cut
     */
    public String cutString(int start, int end) {
        if(debug) System.out.println("Document: cutting String in range (" + String.valueOf(start) + "," + String.valueOf(end) + ")");
        StringBuffer oldContents = new StringBuffer(getText());
        String cut = oldContents.substring(start, end + 1);
        int cutlength = end - start + 1;
        insertIntoContainer(doc, oldContents.delete(start, end + 1).toString());
        int[] delims = {0, getText().length() - 1};
        ((ArrayList) charGroupStack.get(charGroupStack.size() - 1)).set(0, delims);
        if (debug) System.out.println("Document.cutString: charGroupStack size = " + String.valueOf(charGroupStack.size() ) + ", root element ArrayList has size = " + String.valueOf( ((ArrayList)charGroupStack.get( charGroupStack.size() -1 )).size() ) );
        updateObsoletePointers();
        if (!existCharGroups()) return cut;
        if (debug) System.out.println("Document.cutString has character groups.");
        int nextGroup, startInGroup, endInGroup, nextOpening, nextClosing, lastGroupToRemove;
        for (int k = 0; k < charGroupStack.size() - 1; k++) { // iterate through all charGroups in stack except for basis one
            charGroups = (ArrayList) charGroupStack.get(k); // pretend that each stack element in turn is the "current" one
            if(debug) System.out.println("Document.cutString: current stack element is k = " + String.valueOf( k ) + ", contains the following groups:" );
            if(debug) System.out.println( listCharGroups(k) );
            startInGroup = containedInCharGroup(start);
            endInGroup = containedInCharGroup(end);
            if(debug) System.out.println("Document.cutString: string to be cut runs from " + String.valueOf( start ) + " to " + String.valueOf( end ) + ", and returns the following contained-in-group values -- for start, " + String.valueOf( startInGroup ) + ", and for end, " + String.valueOf( endInGroup ) );
            if ((startInGroup == endInGroup) && (startInGroup != -1)) { // cut is within a group
                delims = (int[]) charGroups.get(startInGroup);
                if(debug) System.out.println("Document.cutString: delims of group containing cut: ( " + String.valueOf( delims[0] ) + ", " + String.valueOf( delims[1] ) + ")" );
                if(debug) System.out.println("Document.cutString: before removal, current stack element is k = " + String.valueOf( k ) + ", delims[1] = " + String.valueOf( delims[1] ) + " and list contains the following groups:" );
                if(debug) System.out.println( listCharGroups(k) );
                removeCharGroup(startInGroup);
                if(debug) System.out.println("Document.cutString: after removal, current stack element is k = " + String.valueOf( k ) + ", delims[1] = " + String.valueOf( delims[1] ) + " and list contains the following groups:" );
                if(debug) System.out.println( listCharGroups(k) );
                if (cutlength > delims[1] - delims[0]) { // group was entirely removed
                    nextGroup = startInGroup;
                }
                else { // some part of group remains, restore it to list
                    delims[1] -= cutlength;
                    if(debug) System.out.println("Document.cutString: now the group's delims are ( " + String.valueOf( delims[0] ) + ", " + String.valueOf( delims[1] ) + "), and size of charGroups is " + String.valueOf( charGroups.size() ) );
                    if (startInGroup < charGroups.size())
                        charGroups.add(startInGroup, delims);
                    else
                        charGroups.add(delims);
                    nextGroup = startInGroup + 1;
                    if(debug) System.out.println("Document.cutString: after placing new delims into charGroups we have size = " + String.valueOf( charGroups.size() ) + " and nextGroup = " + String.valueOf( nextGroup ) );
                }
            }
            else {
                nextOpening = nextCharGroup(start);  // the first group starting after cut begins
                nextClosing = nextCharGroup(end); // the first group starting after cut ends
                lastGroupToRemove = (nextClosing == -1) ? charGroups.size() - 1 : nextClosing - 1;
                if (startInGroup == -1) { // cut starts outside of a group
                    if (endInGroup == -1) { // cut ends outside of a group
                        if (nextOpening == nextClosing) { // no group in cut
                            nextGroup = nextOpening;
                        }
                        else { // group(s) contained entirely within cut
                            removeCharGroups(nextOpening, lastGroupToRemove);
                            nextGroup = nextOpening; // remember that removeCharGroups() shifts groups down
                        }
                    }
                    else { // cut starts outside of group, ends within a group
                        removeCharGroups(nextOpening, endInGroup);
                        nextGroup = nextOpening; // remember that removeCharGroups() shifts groups down
                    }
                }
                else { // cut starts within a group
                    if (endInGroup == -1) { // cut starts within a group, ends outside a group
                        removeCharGroups(startInGroup, lastGroupToRemove);
                        nextGroup = startInGroup; // remember that removeCharGroups() shifts groups down
                    }
                    else { // cut starts within a group, ends within a different group
                        removeCharGroups(startInGroup, lastGroupToRemove);
                        nextGroup = startInGroup; // remember that removeCharGroups() shifts groups down
                    }
                }
            }
            if ((nextGroup >= 0) && (nextGroup < charGroups.size())) {
                if(debug) System.out.println("Document.cutString: calling shiftCharGroups with arguments " + String.valueOf( start - end - 1) + " and " + String.valueOf( nextGroup ) );
                shiftCharGroups(start - end - 1, nextGroup);
            }
            if(debug) System.out.println("Document.cutString: after cut, current stack element k = " + String.valueOf( k ) + ", contains the following groups:" );
            if(debug) System.out.println( listCharGroups(k) );
        }
        charGroups = (ArrayList) charGroupStack.get(0); // reset pointer to current charGroup ArrayList
        if (isLineNumber()) {
            int startLine = countLines(getText().substring(0, start));
            int linesCut = countLines(cut);
            int nextInteresting = nextLineNo(startLine);
            if (nextInteresting != -1) { // there are interesting lines after the cut begins
                int endInteresting = nextLineNo(startLine + linesCut);
                int lastInteresting = lineNos.length - 1;
                if (endInteresting == -1) {
                    deleteLineNumbers(nextInteresting, lastInteresting); // delete all lines above beginning of cut
                }
                else if (endInteresting > nextInteresting) {
                    deleteLineNumbers(nextInteresting, endInteresting - 1); // delete intervening lines
                }
                shiftLineNumbers(-linesCut, nextInteresting); // remember that deleteLineNumbers() shifts lines down the list
            }
        }
        return cut;
    }

    /**
     * Inserts the given character String, starting at the given character
     * index position.  Readjusts all character group
     * locations and interesting line numbers after the character position,
     * so that they point to the same text as before.
     *
     * @param insertion The character String to be inserted
     * @param location Character index of the location of the insertion
     */
    public void insertString(String insertion, int location) {
        if(debug) System.out.println("Document.insertString: inserting String of length " + String.valueOf(insertion.length()) + " at location " + String.valueOf(location) );
        if (location == getText().length()) {
            add(insertion);
            return;
        }
        int insertLength = insertion.length();
        StringBuffer working = new StringBuffer(getText().length() + insertLength);
        insertIntoContainer(doc, working.append(getText()).insert(location, insertion).toString());
        int[] delims = {0, getText().length() - 1};
        ((ArrayList) charGroupStack.get(charGroupStack.size() - 1)).set(0, delims);
        updateObsoletePointers();
        if (!existCharGroups()) return;
        int startInGroup, nextGroup;
        for (int k = 0; k < charGroupStack.size() - 1; k++) { // iterate through all charGroups in stack except for basis one
            charGroups = (ArrayList) charGroupStack.get(k); // pretend that each stack element in turn is the "current" one
            startInGroup = containedInCharGroup(location);
            if (startInGroup != -1) { // insertion is within an existing group
                ((int[]) charGroups.get(startInGroup))[1] += insertLength;
            }
            nextGroup = nextCharGroup(location);
            if ((nextGroup == startInGroup) && (nextGroup != -1)) nextGroup++;
            if (nextGroup != -1) shiftCharGroups(insertLength, nextGroup);
        }
        charGroups = (ArrayList) charGroupStack.get(0); // reset pointer to current charGroup ArrayList
        if (isLineNumber()) {
            int startLine = countLines(getText().substring(0, location));
            int nextInteresting = nextLineNo(startLine);
            if (nextInteresting > -1) {
                int linesInserted = countLines(insertion);
                shiftLineNumbers(linesInserted, nextInteresting);
            }
        }
    }

    /**
     * Appends the given text to the existing Document.
     *
     * @param insertion the String to be appended
     */
    public void add(String insertion) {
        int insertLength = insertion.length();
        int oldLength = getText().length();
        StringBuffer working = new StringBuffer(oldLength + insertLength);
        insertIntoContainer(doc, working.append(getText()).append(insertion).toString());
        updateObsoletePointers();
        ArrayList list;
        for (int k = 0; k < charGroupStack.size(); k++) { // iterate through all charGroups in stack including basis one
            if (debug) System.out.println("Document.add: value of k = " + String.valueOf(k) );
            list = (ArrayList) charGroupStack.get(k);
            if (debug) System.out.println("Document.add: is list null?" + String.valueOf( (list == null) ) );
            if (debug) System.out.println("Document.add: size of list = " + String.valueOf( list.size() ) );
            int[] last = (int[]) list.get(list.size() - 1);
            if (last[1] == oldLength - 1) last[1] += insertLength; // change delim if it marks the last character of the old text
        }
    }

    /**
     * Appends the given text to the Document and appends a further line break
     * afterwards.
     *
     * @param text the String to be appended
     */
    public void addLine(String text) {
        add(text + eol);
    }

    /**
     * Replaces the characters between the two given positions, inclusive,
     * by the given String and returns the String that was replaced.
     * Readjusts all character group
     * locations after the replacement, so that they point to the same
     * text as before. Any groups with delimiters inside the region to
     * be replaced are eliminated. Does this by calling the <i>cutString</i>
     * and <i>insertString</i> methods.
     *
     * @param start First character to be replaced
     * @param end Last character to be replaced
     * @param replacement The string that is to be inserted in place of the cut string
     * @return The portion of the document that was replaced
     */
    public String replaceString(int start, int end, String replacement) {
        if(debug) System.out.println("Document: replacing String in range (" + String.valueOf(start) + "," + String.valueOf(end) + ")");
        String cut = cutString(start, end);
        insertString(replacement, start);
        return cut;
    }

    /**
     * Returns a string representation of the complete path
     * and file name. Note that this method returns NULL if there is
     * no filename associated with this Document.
     *
     * @return String full path to file containing the Document
     */
    public String getFile() {
        if (fname != null)
            return fname.getFile();
        else
            return null;
    }


    /**
     * @return true if the file is a HTML file
     */
    public boolean isTextHTML() {
        String text = getText();

        if ((text.indexOf("<html>") != -1) || (text.indexOf("<HTML>") != -1))
            return true;
        else
            return false;
    }

    /**
     * @return the FileName Object
     */
    public FileName getFileRef() {
        return fname;
    }

    /**
     * Returns a String representation of the filename not including any path
     * information. Note that this method returns NULL if there is
     * no filename associated with this Document.
     *
     * @return String filename (without path) of file containing Document
     */
    public String getFileNoPath() {
        if (fname != null)
            return fname.getFileNoPath();
        else
            return null;
    }

    /**
     * Returns a reference to the path for this file without its filename.
     * Note that this method returns NULL if there is
     * no filename associated with this Document.
     *
     * @return String the path (without filename) of the file containing this Document
     */
    public String getPath() {
        if (fname != null)
            return fname.getPath();
        else
            return null;
    }

    /**
     * Sets an int[] array containing interesting line numbers within the file
     * which units can use
     * to display the file at the appropriate place(s). For example,
     * this function is called by the Grep unit.
     *
     * @param ln the set of line numbers of interest
     */
    public void setLineNumbers(int ln[]) {
        lineNos = ln;
    }

    /**
     * Gets the int[] array of line numbers currently held for this Document.
     * This is the array created by <i>setLineNumbers</i>, not a list of all the line
     * numbers.
     *
     * @return int[] the line number array
     */
    public int[] getLineNumbers() {
        return lineNos;
    }

    /**
     * Tests whether there are interesting line numbers for this document.
     *
     * @return boolean <i>True</i> if there are non-zero line number markers
     */
    public boolean isLineNumber() {
        return ((lineNos.length > 1) || (lineNos[0] > 0));
    }


    /**
     * Gets the <i>n</i>th interesting line number held for this document. If
     * there are not this many line numbers, returns -1.
     *
     * @param n The index into the line-number array
     * @return int The desired line number
     */
    public int getLineNumber(int n) {
        int l = -1;
        if (n < lineNos.length) l = lineNos[n];
        return l;
    }

    /**
     * Adds an interesting line number to the line number array in
     * the right order.
     *
     * @param n The line number to be added
     */
    public void addLineNumber(int n) {
        int l = nextLineNo(n);
        if (lineNos[l] == n) return;
        int oldLength = lineNos.length;
        int[] newNos = new int[oldLength + 1];
        if (l == -1) {
            System.arraycopy(lineNos, 0, newNos, 0, oldLength);
            newNos[oldLength + 1] = n;
        }
        else {
            if (l > 0) System.arraycopy(lineNos, 0, newNos, 0, l);
            newNos[l] = n;
            System.arraycopy(lineNos, l, newNos, l + 1, oldLength - l);
        }
        setLineNumbers(newNos);
    }

    /**
     * Deletes the line number at the given index location.
     *
     * @param l The index number of the line to be deleted
     */
    public void deleteLineNumber(int l) {
        int oldLength = lineNos.length;
        int newLength = oldLength - 1;
        int[] newNos = new int[newLength];
        if (l == 0)
            System.arraycopy(lineNos, 1, newNos, 0, newLength);
        else if (l == newLength)
            System.arraycopy(lineNos, 0, newNos, 0, newLength);
        else {
            System.arraycopy(lineNos, 0, newNos, 0, l);
            System.arraycopy(lineNos, l + 1, newNos, l, newLength - l);
        }
    }

    /**
     * Deletes the line number range defined by the given index locations.
     *
     * @param start The index number of the first line to be deleted
     * @param end The index number of the last line to be deleted
     */
    public void deleteLineNumbers(int start, int end) {
        if (start == end) {
            deleteLineNumber(start);
            return;
        }
        int oldLength = lineNos.length;
        end = (end < oldLength) ? end : oldLength;
        int linesOut = end - start + 1;
        if (linesOut >= oldLength) {
            lineNos = new int[1];
            return;
        }
        int newLength = oldLength - linesOut;
        int[] newNos = new int[newLength];
        if (start == 0)
            System.arraycopy(lineNos, end + 1, newNos, 0, newLength);
        else if (end == oldLength - 1)
            System.arraycopy(lineNos, 0, newNos, 0, newLength);
        else {
            System.arraycopy(lineNos, 0, newNos, 0, start + 1);
            System.arraycopy(lineNos, end + 1, newNos, start + 1, newLength - start - 1);
        }
    }

    /**
     * Counts the number of lines in the given String.
     *
     * @param s The String to be examined
     * @return The number of end-of-line markers in the string
     */
    public int countLines(String s) {
        int count = 0;
        int pos = -1;
        while ((pos = s.indexOf(eol, pos + 1)) != -1) count++;
        return count;
    }

    /**
     * Returns a String containing the interesting line numbers, each on a separate line.
     *
     * @return String The list of interesting line numbers
     */
    public String listLineNumbers() {
        StringBuffer list = new StringBuffer(2 * lineNos.length);
        if (isLineNumber()) {
            for (int l = 0; l < lineNos.length; l++) list.append(lineNos[l]).append(eol);
        }
        return list.toString();
    }

    /**
     * Returns a String containing the interesting line numbers and their lines,
     * separated by a line-separator (carriage-return).
     *
     * @return String The list of interesting line numbers
     */
    public String listLineNumbersAndLines() {
        StringBuffer list = new StringBuffer(2 * lineNos.length);
        if (isLineNumber()) {
            int eolLength = eol.length();
            int lastLine = 0;
            int loc1 = 0;
            int loc2 = 0;
            String t = getText();
            if (!t.endsWith(eol)) t += eol;
            for (int l = 0; l < lineNos.length; l++) {
                while (lastLine <= lineNos[l]) {
                    loc2 = t.indexOf(eol, loc2);
                    lastLine++;
                }
                loc1 = t.lastIndexOf(eol, loc2 - 1);
                if (loc1 == -1) loc1 = 0;
                loc2 += eolLength;
                list.append(lineNos[l]).append(": ").append(t.substring(loc1, loc2));
            }
        }
        return list.toString();
    }


    /**
     * Adds a given constant <i>shift</i> to the interesting line numbers for
     * this Document above the given index. This allows one to ensure that the
     * same lines are marked after lines are added to or removed from the text.
     *
     * @param shift The amount by which the line numbers are moved
     * @param index The first line number to be moved.
     */
    public void shiftLineNumbers(int shift, int index) {
        for (int l = index; l < lineNos.length; l++) lineNos[l] += shift;
    }

    /**
     * Returns the index of the next interesting line number held in the array
     * <i>lineNos</i> that is larger than the given line number,
     * or -1 if there is no interesting line number larger.
     *
     * @param n The line number where the search starts
     * @return The index of the first interesting line number above <i>n</i>
     */
    public int nextLineNo(int n) {
        int l = 0;
        while (lineNos[l] < n) {
            l++;
            if (l == lineNos.length) return -1;
        }
        return l;
    }


    /**
     * Gets the current ArrayList of the current character groups for this Document.
     * This is the ArrayList created by <i>setCharGroups</i>.
     *
     * @return ArrayList the set of integer arrays holding character ranges
     */
    public ArrayList getCharGroups() {
        return charGroups;
    }


    /**
     * Gets the integer array for the character group denoted by the given
     * index. Returns null if the index is out of bounds.
     *
     * @param index the index of the character group being sought
     */
    public int[] getCharGroup(int index) {
        if ((index < 0) || (index >= charGroups.size())) return null;
        return (int[]) charGroups.get(index);
    }

    /**
     * Returns an ArrayList of the delimiters of the character groups that
     * are entirely contained within the given index values. Groups that
     * start outside and finish inside or start inside and finish outside are
     * not included.
     *
     * @param start The index of the first character in the search interval
     * @param end The index of the final character in the search interval
     * @return Contains int[2] pairs of delimiters of the character groups in the search interval, null if there are none
     */
    public ArrayList getCharGroups(int start, int end) {

        int n = nextCharGroup(start);
        if (n == -1) return null;
        ArrayList setOfGroups = new ArrayList(2);
        while ((n < charGroups.size()) && (getCharGroup(n)[1] <= end)) setOfGroups.add(getCharGroup(n++));
        if (setOfGroups.size() == 0) return null;
        return setOfGroups;
    }


    /**
     * Gets an ArrayList of int[2]'s containing the indices of the first and
     * last characters in all parts of the document text that
     * are not contained in the current charGroups but are contained in the
     * current domain. This allows units to search the domain to create further
     * new charGroups at the same level as the current one. If the domain is
     * totally subdivided into groups, then the method returns null.
     *
     * @return ArrayList Contains int[2] pairs marking text in current domain not in current charGroups, or null
     */
    public ArrayList getCharGroupsComplement() {

        int[] comp = new int[2];
        int newListSize;
        if (domain == null)
            newListSize = 100;
        else
            newListSize = 2 * domain.size();
        ArrayList complement = new ArrayList(newListSize);
        if (!existCharGroups()) {
            comp[0] = 0;
            comp[1] = getText().length() - 1;
            complement.add(comp);
            return complement;
        }

        ArrayList groups;
        int domainIndex = 0;
        int[] dom, cg;
        int[] delimiters;
        int j, k, m;

        if (debug) if ( domain == null ) System.out.println("Document.getCharGroupsComplement: domain pointer is null");

        for (j = 0; j < domain.size(); j++) {
            dom = (int[]) domain.get(j);
            groups = getCharGroups(dom[0], dom[1]);
            if (groups == null) {
                delimiters = new int[2];
                delimiters[0] = dom[0];
                delimiters[1] = dom[1];
                complement.add( delimiters );
            }
            else {
                /* First fill an array with all delimiters of the domain
                 * and its contained groups in sequence
                 */
                delimiters = new int[2 * groups.size() + 2];
                delimiters[0] = dom[0];
                m = 1;
                for (k = 0; k < groups.size(); k++) {
                    cg = (int[]) groups.get(k);
                    delimiters[m++] = cg[0];
                    delimiters[m++] = cg[1];
                }
                delimiters[m] = dom[1]; // now m is the last index of the delimiters array
                /* The candidates for ranges in the complement are the
                 * successive index pairs, which indicate whether there
                 * are gaps between groups. If there are no gaps then
                 * eliminate these candidates by setting their index values
                 * to -1. If there are gaps, readjust the delimiters to point
                 * to the first and last elements of the gaps.
                 */
                if (delimiters[0] == delimiters[1]) { // if domain opener equals first group element, no gap
                    delimiters[0] = -1;
                    delimiters[1] = -1;
                }
                else
                    delimiters[1] -= 1; // first gap starts with domain opener, closes before first element of group
                k = 2;
                while (k < m - 2) { // if middle pairs differ by one, no gap
                    if (delimiters[k] + 1 == delimiters[k + 1]) {
                        delimiters[k] = -1;
                        delimiters[k + 1] = -1;
                    }
                    else { // for middle pairs, gap starts after last element of one group and ends before first element of the next
                        delimiters[k] += 1;
                        delimiters[k + 1] -= 1;
                    }
                    k += 2;
                }
                if (delimiters[m - 1] == delimiters[m]) { // if domain closer equals last group element, no gap
                    delimiters[m - 1] = -1;
                    delimiters[m] = -1;
                }
                else
                    delimiters[m - 1] += 1; // last gap starts after the last group and ends on the domain closer
                /* Now scan the delimiter array, skipping over -1's, place
                 * successive pairs into int[2] arrays, and store them into complement.
                 */
                k = 0;
                if(debug) System.out.println("Document: delimiters array has length " + String.valueOf( delimiters.length ) + " and the top value is m = " + String.valueOf( m ) );
                while (k < m) {
                    if(debug) System.out.println("Document: complement loop for k = " + String.valueOf( k ) );
                    try {
                        while (delimiters[k] == -1) k++;
                    }
                    catch (ArrayIndexOutOfBoundsException ex) {
                        break;
                    }
                    if (k < m) {
                        comp = new int[2];
                        comp[0] = delimiters[k++];
                    }
                    while (delimiters[k] == -1) k++;
                    comp[1] = delimiters[k++];
                    complement.add(comp);
                }
            }
        }

        if (complement.size() == 0) return null;
        return complement;
    }


    /**
     * Sets a new ArrayList into the stack of ArrayLists containing interesting character
     * groups and makes this new one the current one, making the previous one the domain.
     *
     * @param cg the set of integer arrays holding character ranges
     */
    public void addCharGroupsLayer(ArrayList cg) {
        charGroupStack.add(0, cg);
        charGroups = cg;
        if (charGroupStack.size() > 1)
            domain = (ArrayList) charGroupStack.get(1);
        else
            domain = null;
        if(debug) System.out.println("Document.addCharGroupsLayer: added new level, total levels = " + String.valueOf(charGroupStack.size()) + "\nDocument.addCharGroupsLayer: here are the populations of the character group levels: ");
        if (debug) for ( int k = 0; k < charGroupStack.size(); k++ ) System.out.print(String.valueOf( ((ArrayList)charGroupStack.get(k)).size() ) + ", " );
        if(debug) System.out.print("\n");
        if (debug) {
            if ( domain == null ) System.out.println("Document.addCharGroupsLayer: domain pointer is null");
            else System.out.println("Document.addCharGroupsLayer: domain pointer set to level 1");
        }
    }

    /**
     * Replaces the current ArrayList of interesting character
     * groups with this new one. If there is not layer apart from the
     * base layer, then this adds a new one.
     *
     * @param cg the set of integer arrays holding character ranges
     */
    public void setCharGroups(ArrayList cg) {
        if (charGroupStack.size() > 1) {
            charGroupStack.set(0, cg);
            charGroups = cg;
            if(debug) System.out.println("Document.setCharGroups: replaced current character Group. Ttotal levels = " + String.valueOf(charGroupStack.size()) + "\nDocument.setCharGroups: here are the populations of the character group levels: ");
            if (debug) for ( int k = 0; k < charGroupStack.size(); k++ ) System.out.print(String.valueOf( ((ArrayList)charGroupStack.get(k)).size() ) + ", " );
            if(debug) System.out.print("\n");
        }
        else {
            if(debug) System.out.println("Document.setCharGroups: called addCharGroupsLayer");
            addCharGroupsLayer(cg);
        }
        if (debug) if ( domain == null ) System.out.println("Document.setCharGroups: domain pointer is null");
    }


    /**
     * Tests whether there are non-trivial character group lists, not just the lowest
     * one, which includes all characters.
     *
     * @return boolean <i>True</i> if there is more than one layer of character group lists
     */
    public boolean existCharGroups() {
        return ((charGroupStack.size() > 1) && (charGroups.size() > 0));
    }


    /**
     * Sets a new character group into the ArrayList charGroups.
     * The group is delineated by the given start and end character positions.
     * It is placed in the ArrayList in the correct place to be in sequence with
     * other character groups as they appear in the text.
     * If the range overlaps with one or more existing groups, the old groups
     * are eliminated. This only affects character group pointers; it does not
     * change the document text string at all.
     *
     * @param start the integer beginning the range for this group
     * @param end the integer ending the range for this group
     */
    public void setCharGroup(int start, int end) {

        if (charGroupStack.size() == 1) {
            if(debug) System.out.println("Document.setCharGroup: called addCharGroupsLayer");
            addCharGroupsLayer(new ArrayList(1));
        }
        int[] delim = {start, end};
        if (!existCharGroups()) {
            charGroups.add(delim);
            return;
        }

        int startInGroup = containedInCharGroup(start);
        int endInGroup = containedInCharGroup(end, startInGroup);
        if ((startInGroup == endInGroup) && (startInGroup != -1)) { // new group is within a group
            charGroups.set(startInGroup, delim);
        }
        else {
            int nextOpening = nextCharGroup(start);
            int nextClosing = nextCharGroup(end);
            int lastGroupToRemove = (nextClosing == -1) ? charGroups.size() - 1 : nextClosing - 1;
            if (startInGroup == -1) { // new group starts outside of a group
                if (endInGroup == -1) { // new group ends outside of a group
                    if (nextOpening == nextClosing) { // no group inside new group
                        if (nextOpening == -1)
                            charGroups.add(delim);
                        else
                            charGroups.add(nextOpening, delim); // remember that removeCharGroups() shifts groups down
                    }
                    else { // group(s) contained entirely within the new group
                        removeCharGroups(nextOpening, lastGroupToRemove);
                        if (nextOpening == -1)
                            charGroups.add(delim);
                        else
                            charGroups.add(nextOpening, delim); // remember that removeCharGroups() shifts groups down
                    }
                }
                else { // new group starts outside of a group, ends within a group
                    removeCharGroups(nextOpening, endInGroup);
                    if (nextOpening == -1)
                        charGroups.add(delim);
                    else
                        charGroups.add(nextOpening, delim); // remember that removeCharGroups() shifts groups down
                }
            }
            else { // new group starts within a group
                if (endInGroup == -1) { // new group starts within a group, ends outside a group
                    removeCharGroups(startInGroup, lastGroupToRemove);
                    if (startInGroup == -1)
                        charGroups.add(delim);
                    else
                        charGroups.add(startInGroup, delim); // remember that removeCharGroups() shifts groups down
                }
                else { // new group starts within a group, ends within a different group
                    removeCharGroups(startInGroup, lastGroupToRemove);
                    if (startInGroup == -1)
                        charGroups.add(delim);
                    else
                        charGroups.add(startInGroup, delim); // remember that removeCharGroups() shifts groups down
                }
            }
        }
    }


    /**
     * Deletes the entry for the character group denoted by the given
     * index.
     * This only affects character group pointers; it does not
     * change the document text string at all.
     *
     * @param index the index of the character group being deleted
     */
    public void removeCharGroup(int index) {
        if ((index >= 0) && (index < charGroups.size())) charGroups.remove(index);
    }

    /**
     * Deletes the int[2] arrays for all the character groups with
     * index values between the two given values, inclusive.
     * This only affects character group pointers; it does not
     * change the document text string at all.
     *
     * @param start the index of the first character group being deleted
     * @param finish the index of the final character group being deleted
     */
    public void removeCharGroups(int start, int finish) {
        int lastGroup = charGroups.size() - 1;
        if (lastGroup == -1) return;
        if (finish > lastGroup) finish = lastGroup;
        if (start < 0) return;
        // method remove shifts elements to the left, so its argument is not incremented
        for (int g = start; g <= finish; g++) charGroups.remove(start);
    }

    /**
     * Removes the current ArrayList of character groups, sets the next one in the
     * stack equal to the current one, resets the domain pointer to the next one beyond that.
     * Will not remove the last character group, which spans the whole document; in this
     * case the method returns null.
     *
     * @return ArrayList The removed ArrayList
     */
    public ArrayList deleteCharGroups() {
        if (charGroupStack.size() == 1) return null;
        ArrayList removed = (ArrayList) charGroupStack.remove(0);
        if(debug) System.out.println("Document.deleteCharGroups: deleted current level, total levels now = " + String.valueOf(charGroupStack.size()) + "\nDocument.deleteCharGroups: here are the populations of the character group levels: ");
        if (debug) for ( int k = 0; k < charGroupStack.size(); k++ ) System.out.print(String.valueOf( ((ArrayList)charGroupStack.get(k)).size() ) + ", " );
        if(debug) System.out.print("\n");
        charGroups = (ArrayList) charGroupStack.get(0);
        if (charGroupStack.size() > 1)
            domain = (ArrayList) charGroupStack.get(1);
        else
            domain = null;
        return removed;
    }

    /**
     * Returns the pointer to the domain, which is the set of character groups
     * just above the current set in the list. The model assumes that the current
     * set are obtained by refinement of the ones pointed to by <i>domain</i>. Thus,
     * an editing unit creating selections at the current level will search only
     * within selections in the domain. There is no method to set this pointer.
     * That happens automatically when the stack of character group sets changes.
     *
     * @return ArrayList The list referenced by the domain pointer
     */
    public ArrayList getDomain() {
        return domain;
    }


    /**
     * Adds the given constant <i>shift</i> to the locations of both delimiters
     * of all the character groups above and including
     * the given character group.  This allows resetting of the locations of
     * the character groups after the document has changed length.
     * This only affects character group pointers; it does not
     * change the document text string at all.
     *
     * @param shift The amount to increment delimiters of character groups
     * @param firstGroup The index of the first group to be shifted
     */
    public void shiftCharGroups(int shift, int firstGroup) {
        if (firstGroup < 0) return;
        int[] delims;
        int size = charGroups.size();
        int g = firstGroup;
        while (g < size) {
            delims = (int[]) charGroups.get(g);
            delims[0] += shift;
            delims[1] += shift;
            g++;
        }
    }


    /**
     * Returns the index of the character group containing the given character
     * position, starting the search of character groups from the given group.
     * If the given character is not contained in a group in the search, returns -1.
     *
     * @param character The index in the document of the character being tested
     * @param firstGroup The index of the character group at which the search starts
     * @return The index of the character group containing the character, or -1 if there is none
     */
    public int containedInCharGroup(int character, int firstGroup) {
        int size = charGroups.size();
        int startGroup = (firstGroup < 0) ? 0 : firstGroup;
        if (size > startGroup) {
            int g = startGroup;
            while (character > ((int[]) charGroups.get(g))[1]) {
                g++;
                if (g == size) return -1;
            }
            if (character >= ((int[]) charGroups.get(g))[0]) return g;
        }
        return -1;
    }

    /**
     * Returns the index of the character group containing the given character
     * position. If the given character is not contained in a group in the search, returns -1.
     *
     * @param character The index in the document of the character being tested
     * @return The index of the character group containing the character, or -1 if there is none
     */
    public int containedInCharGroup(int character) {
        return containedInCharGroup(character, 0);
    }


    /**
     * Returns the index of the first character group that starts at or after the
     * given character position, starting the search of character groups from the given group.
     * If no character group is found in the search, returns -1. If the given character
     * is in a character group, the index of the next group is returned.
     *
     * @param character The index in the document of the character being tested
     * @param firstGroup The index of the character group at which the search starts
     * @return The index of the first character group starting after the character, or -1 if there is none
     */
    public int nextCharGroup(int character, int firstGroup) {
        int size = charGroups.size();
        int startGroup = (firstGroup < 0) ? 0 : firstGroup;
        if (size > startGroup) {
            int g = startGroup;
            while (character > ((int[]) charGroups.get(g))[0]) {
                g++;
                if (g == size) return -1;
            }
            return g;
        }
        return -1;
    }

    /**
     * Returns the index of the first character group that starts at or after the
     * given character position.
     * If no character group is found in the search, returns -1. If the given character
     * is in a character group, the index of the next group is returned.
     *
     * @param character The index in the document of the character being tested
     * @return The index of the first character group starting after the character, or -1 if there is none
     */
    public int nextCharGroup(int character) {
        return nextCharGroup(character, 0);
    }

    /**
     * Returns the index of the first character group that ends at or before the
     * given character position, starting the search of character groups backwards
     * from the given group.
     *
     * If no character group is found in the search, returns -1. If the given character
     * is at the end of a character group, the index of that group is returned. If the
     * given character is in a character group but is not the last character of the group,
     * the index of the next group before it is returned.
     *
     * @param character The index in the document of the character being tested
     * @param firstGroup The index of the character group at which the search starts
     * @return int The index of the first character group ending at or before the character, or -1 if there is none
     */
    public int prevCharGroup( int character, int firstGroup ) {
        int size = charGroups.size();
        int startGroup = ( firstGroup < 0 ) ? 0 : firstGroup;
        if ( size > startGroup ) {
            int g = startGroup;
            while ( character < ((int[])charGroups.get( g ))[1] ) {
                g--;
                if ( g == -1 ) return -1;
            }
            return g;
        }
        return -1;
    }

    /**
     * Returns the index of the first character group that ends at or before the
     * given character position.
     *
     * If no character group is found in the search, returns -1. If the given character
     * is at the end of a character group, the index of that group is returned. If the
     * given character is in a character group but is not the last character of the group,
     * the index of the next group before it is returned.
     *
     * @param character The index in the document of the character being tested
     * @return int The index of the first character group ending at or before the character, or -1 if there is none
     */
    public int prevCharGroup( int character ) {
        return prevCharGroup( character, charGroups.size() - 1 );
    }



    /**
     * Returns the index of the character group in the domain ArrayList which
     * contains the character group of the current charGroups ArrayList corresponding
     * to the given index.
     *
     * @param cg The index of the character group in the current list
     * @return The index of the character group in the domain list that contains the given group
     */
    public int domainOfCharGroup(int cg) {
        int character = ((int[]) charGroups.get(cg))[0];
        int size = domain.size();
        int startGroup = 0;
        if (size > startGroup) {
            int g = startGroup;
            while (character > ((int[]) domain.get(g))[1]) {
                g++;
                if (g == size) return -1;
            }
            if (character >= ((int[]) domain.get(g))[0]) return g;
        }
        return -1;
    }


    /**
     * Returns the substring defined by the character group of the given index.
     * This does not modify the string or the character group locations.
     *
     * @param group Index of the group to be returned
     * @return String The substring defined by the group
     */
    public String getGroupString(int group) {
        int[] delims = getCharGroup(group);
        if (delims == null) return null;
        return getString(delims[0], delims[1]);
    }

    /**
     * Returns a String containing the character group delimiters
     * for the element of the character group stack denoted by the
     * given index. The pairs are written all on one line.
     *
     * @param k The level in the character group stack to be written
     * @return The character group delimiters
     */
    public String listCharGroups(int k) {
        ArrayList cg = (ArrayList) charGroupStack.get(k);
        int n = cg.size();
        StringBuffer list = new StringBuffer(12 * n);
        int[] delims;
        for (int g = 0; g < n; g++) {
            delims = (int[]) cg.get(g);
            list.append("( ").append(delims[0]).append(", ").append(delims[1]).append(") ");
        }
        return list.append(eol).toString();
    }

    /**
     * Returns a String containing the character group delimiters
     * for the element of the character group stack at the current
     * level. The pairs are written all on one line.
     *
     * @return String The character group delimiters
     */
    public String listCharGroups() {
        return listCharGroups(0);
    }


    /**
     * Returns a String containing the character group delimiters
     * and their associated group Strings at the level given by
     * the integer argument. The groups are separated by blank lines.
     *
     * @param k The level in the character group stack to be written
     * @return The character group Strings and delimiters
     */
    public String listGroupStrings(int k) {
        ArrayList cg = (ArrayList) charGroupStack.get(k);
        int n = cg.size();
        StringBuffer list = new StringBuffer(100 * n);
        int[] delims;
        String t = getText();
        for (int g = 0; g < n; g++) {
            delims = (int[]) cg.get(g);
            list.append("( ").append(delims[0]).append(", ").append(delims[1]).append("): ");
            list.append(t.substring(delims[0], delims[1] + 1)).append(eol).append(eol);
        }
        return list.toString();
    }

    /**
     * Returns a String containing the character group
     * Strings at the level given by
     * the integer argument. The groups are separated by blank lines.
     *
     * @param k The level in the character group stack to be written
     * @return The character group Strings
     */
    public String listGroupStringsNoDelims( int k ) {
        ArrayList cg = (ArrayList)charGroupStack.get( k );
        int n = cg.size();
        StringBuffer list = new StringBuffer( 100 * n );
        int[] delims;
        String t = getText();
        for ( int g = 0; g < n; g++ ) {
            delims = (int[])cg.get( g );
            list.append( t.substring( delims[0], delims[1] + 1 ) ).append(eol).append(eol);
        }
        return list.toString();
    }

    /**
     * Returns a String containing the character group delimiters
     * and their associated group Strings at the current level. The
     * groups are separated by blank lines.
     *
     * @return String The character group delimiters
     */
    public String listGroupStrings() {
        return listGroupStrings(0);
    }

    /**
     * Removes and returns the character string denoted by the given
     * character group index. Readjusts all subsequent character group
     * locations after the cut so that they point to the same
     * text as before the removal.
     *
     * @param index Index of the character group to be cut
     * @return the string that has been cut
     */
    public String cutGroupString(int index) {
        int[] delim = (int[]) charGroups.get(index);
        if (delim == null) return null;
        return cutString(delim[0], delim[1]);
    }

    /**
     * Inserts the given character String at the given character
     * index position and marks it as a character group.  Readjusts all character group
     * locations after the character position, so that they point to the same
     * text as before the insertion. If the insertion occurs within a
     * character group, that group is eliminated and the insertion is
     * made into a new character group. If insertion into an existing
     * group is desired, use instead the method <i>insertString</i>.
     *
     * @param insertion The character String to be inserted
     * @param location Character index of the location of the insertion
     */
    public void insertStringAsCharGroup(String insertion, int location) {
        int ownGroup = containedInCharGroup(location);
        if (ownGroup != -1) removeCharGroup(ownGroup);
        insertString(insertion, location);
        setCharGroup(location, location + insertion.length() - 1);
    }

    /**
     * Replaces the character group with the given index
     * by the given String and returns the String that was removed.
     * Readjusts all character group locations after the replacement,
     * so that they point to the same text as before. The character
     * group with the given index is readjusted in size to delimit the
     * new replacement substring.
     *
     * @param index Index of the character group to be replaced
     * @param replacement The string that is to be inserted in place of the cut string
     * @return The portion of the document that was replaced
     */
    public String replaceGroupString(int index, String replacement) {
        if ((replacement == null) || (replacement.length() == 0)) return cutGroupString(index);
        if(debug) System.out.println("Document.replaceGroupString: replacing group with index " + String.valueOf(index) + " and delims (" + String.valueOf( getCharGroup(index)[0]) + "'" + String.valueOf( getCharGroup(index)[1]) + ")" );
        if(debug) System.out.println("Document.replaceGroupString: before replacement, list contains the following groups at current level:" );
        if(debug) System.out.println( listCharGroups(0) );
        int[] delims = (int[]) charGroups.get(index);
        String cut = replaceString(delims[0], delims[1], replacement);
        setCharGroup(delims[0], delims[0] + replacement.length() - 1);
        if(debug) System.out.println("Document.replaceGroupString: after replacement, list contains the following groups at current level:" );
        if(debug) System.out.println( listCharGroups(0) );
        return cut;
    }


    /**
     * Returns the filetype <i>i.e.</i> LOCAL (<i>i.e.</i> disk file) or HTTP, (on the
     * internet). See FileName for definitions of HTTP and LOCAL.
     *
     * If there is no file name attached to this Document then this returns
     * the FileName.INVALID flag.
     * @return int the filetype
     * @see triana.types.FileName
     */
    public int getFileType() {
        if (fname != null)
            return fname.getFileType();
        else
            return FileName.INVALID;
    }

    /**
     * Sets the FileName data object for this Document
     *
     * @param fn The full path to the given file
     */
    public void setFile(FileName fn) {
        fname = fn;
    }

    /**
     * Sets the directory and file name from an input string containing
     * the full path to the file.
     *
     * @param pn The full path to the given file
     */
    public void setFile(String pn) {
        if ((pn == null) || (pn.startsWith("null")))
            return;

        if (fname == null)
            fname = new FileName(pn);
        else
            fname.setFile(pn);
    }


    /**
     * @return int the length of this Document
     */
    public int length() {
        return getText().length();
    }

    /**
     * Finds the first occurence of the given String in the Document. It
     * returns the position of the string or -1 if it is not present.
     *
     * @param text The text to search for
     * @return the first position of the given string within the Document
     */
    public int findNext(String text) {
        return getText().indexOf(text);
    }

    /**
     * Finds the first occurence of the given String after the given
     * character position in the Document. It returns the position of the
     * string or -1 if it is not present.
     *
     * @param text The text to search for
     * @param start Index of first character of the search
     * @return the first position of the given string after start within the Document
     */
    public int findNext(String text, int start) {
        return getText().indexOf(text, start);
    }

    /**
     * Finds the first occurence of the given String within the given
     * character group in the Document. It returns the position of the
     * string or -1 if it is not present in the group.
     *
     * @param text The text to search for
     * @param group Index of character group of the search
     * @return the first position of the given string after start within the Document
     */
    public int findNextInGroup(String text, int group) {
        int[] groupDelim = getCharGroup(group);
        int beginning = groupDelim[0];
        int end = groupDelim[1] + 1;
        int location = getText().substring(beginning, end).indexOf(text);
        return (location == -1) ? -1 : location + beginning;
    }

    /**
     * Finds the first occurence of the given String within the given
     * character group after the given location in the Document.
     * It returns the position of the string or -1 if it is not present
     * in the group after the given location. If the index start is not
     * within the group, the method returns -2.
     *
     * @param text The text to search for
     * @param group Index of character group of the search
     * @param start Index of the first character of the search
     * @return the first position of the given string after start within the Document
     */
    public int findNextInGroup(String text, int group, int start) {
        int[] groupDelim = getCharGroup(group);
        int beginning = groupDelim[0];
        int end = groupDelim[1] + 1;
        if ((start < beginning) || (start >= end)) return -2;
        start -= beginning;
        int location = getText().substring(beginning, end).indexOf(text, start);
        return (location == -1) ? -1 : location + beginning;
    }


    /**
     * Removes the first occurence of the given String from the Document. It
     * returns the position of the string or -1 if it is not present.
     *
     * @param text The text to search for
     * @return the first position of the given string within the Document
     */
    public int remove(String text) {
        int pos = findNext(text);
        if (pos == -1) return pos;
        cutString(pos, pos + text.length() - 1);
        return pos;
    }

    /**
     * Removes the first occurence of the given String after the given
     * character location in the Document. It
     * returns the position of the string or -1 if it is not present.
     *
     * @param text The text to search for
     * @return the first position of the given string within the Document
     */
    public int remove(String text, int start) {
        int pos = findNext(text, start);
        if (pos == -1) return pos;
        cutString(pos, pos + text.length() - 1);
        return pos;
    }

    /**
     * Finds the first occurence of the given String <i>text</i> in the
     * Document and replaces it with the given String <i>newText</i>.
     * Returns -1 if the Document does not contain text.
     *
     * @param text The text to search for
     * @param newText The text to insert in place of text
     * @return the first position of the text within the Document
     */
    public int replaceNext(String text, String newText) {
        int pos = findNext(text);
        if (pos == -1) return pos;
        replaceString(pos, pos + text.length() - 1, newText);
        return pos;
    }

    /**
     * Finds the first occurence of the given String <i>text</i>
     * after the given character location in the
     * Document and replaces it with the given String <i>newText</i>.
     * Returns -1 if the Document does not contain text.
     *
     * @param text The text to search for
     * @param newText The text to insert in place of text
     * @return the first position of the text within the Document
     */
    public int replaceNext(String text, String newText, int start) {
        int pos = findNext(text, start);
        if (pos == -1) return pos;
        replaceString(pos, pos + text.length() - 1, newText);
        return pos;
    }

    /**
     * Replaces all occurences of the given String <i>text</i> with the
     * given String <i>newText</i>.
     *
     * @param text The text to search for
     * @param newText The text to insert in place of text
     * @return int The number of replacements that were made
     */
    public int replaceAll(String text, String newText) {
        int count = 0;
        int start = 0;
        while ((start = replaceNext(text, newText, start)) != -1) {
            ++count;
        }

        return count;
    }


    /**
     * Returns <i>true</i> if the specified object doc
     * is the same type and has the same contents as this object,
     * including the path name and file name. Does not test character group
     * pointers or line number lists.
     *
     * @return Boolean <i>true</i> if the argument is a Document with the same contents as this Document
     */
    public boolean equals(Object doc) {
        if (!(doc instanceof Document))
            return false;

        Document d = (Document) doc;

        if (!getText().equals(d.getText()))
            return false;

        if (fname != null)
            if (!fname.equals(d.getFile()))
                return false;

        return true;
    }

    /**
     * This is one of the most important methods of Triana data.
     * types. It returns a copy of the type invoking it. This <b>must</b>
     * be overridden for every derived data type derived. If not, the data
     * cannot be copied to be given to other units. Copying must be done by
     * value, not by reference.
     * </p><p>
     * To override, the programmer should not invoke the <i>super.copyMe</i> method.
     * Instead, create an object of the current type and call methods
     * <i>copyData</i> and <i>copyParameters</i>. If these have been written correctly,
     * then they will do the copying.  The code should read, for type YourType:
     * <PRE>
     *        YourType y = null;
     *        try {
     *            y = (YourType)getClass().newInstance();
     *	          y.copyData( this );
     *	          y.copyParameters( this );
     *            y.setLegend( this.getLegend() );
     *            }
     *        catch (IllegalAccessException ee) {
     *            System.out.println("Illegal Access: " + ee.getMessage());
     *            }
     *        catch (InstantiationException ee) {
     *            System.out.println("Couldn't be instantiated: " + ee.getMessage());
     *            }
     *        return y;
     * </PRE>
     * </p><p>
     * The copied object's data should be identical to the original. The
     * method here modifies only one item: a String indicating that the
     * object was created as a copy is added to the <i>description</i>
     * StringVector.
     *
     * @return TrianaType Copy by value of the current Object except for an
     updated <i>description</i>
     */
    public TrianaType copyMe() {
        Document d = null;
        try {
            d = (Document) getClass().newInstance();
            d.copyData(this);
            d.copyParameters(this);
        }
        catch (IllegalAccessException ee) {
            System.out.println("Illegal Access: " + ee.getMessage());
        }
        catch (InstantiationException ee) {
            System.out.println("Couldn't be instantiated: " + ee.getMessage());
        }
        return d;
    }

    /**
     * Copies the Document by value from the given source, not by reference.
     *
     * @param source The Document object being copied
     */
    public void copyData(TrianaType source) {
        setText(Str.copy(((Document) source).getText()));
    }


    /**
     * Copies modifiable parameters from the given source.
     *
     * @param source The Document object being copied
     */
    public void copyParameters(TrianaType source) {
        super.copyParameters( source );
        if (((Document) source).getFileRef() != null)
            setFile((FileName)(((Document) source).getFileRef().copyMe()));
        if (((Document) source).isLineNumber()) {
            int[] lines = ((Document) source).getLineNumbers();
            int[] newlines = new int[lines.length];
            for (int i = 0; i < lines.length; ++i)
                newlines[i] = lines[i];
            setLineNumbers(newlines);
        }
        ArrayList stack = ((Document) source).getCharGroupStack();
        if (stack.size() > 1) { // if only one element in stack, it is the base group set up when the text was set in CopyData()
            ArrayList groups, newgroups;
            int[] delims, newdelims;
            int k, g;
            for (k = stack.size() - 2; k >= 0; k--) { // go backwards through stack to use addCharGroupsLayer to construct new stack
                groups = (ArrayList) stack.get(k);
                newgroups = new ArrayList(groups.size());
                for (g = 0; g < groups.size(); ++g) {
                    delims = (int[]) groups.get(g);
                    newdelims = new int[2];
                    newdelims[0] = delims[0];
                    newdelims[1] = delims[1];
                    newgroups.add(newdelims);
                }
                addCharGroupsLayer(newgroups);
            }
        }
    }


    /**
     * Used when Triana types want to be able to
     * send ASCII data to other programs using strings.  This is used to
     * implement socket and to run other executables, written in C or
     * other languages. With ASCII you don't have to worry about
     * ENDIAN'ness as the conversions are all done via text. This is
     * obviously slower than binary communication since you have to format
     * the input and output within the other program.
     * </p><p>
     * This method must be overridden in every subclass that defines new
     * data or parameters. The overriding method should first call<<PRE>
     *      super.outputToStream(dos)
     * </PRE>to get output from superior classes, and then new parameters defined
     * for the current subclass must be output. Moreover, subclasses
     * that first dimension their data arrays must explicitly transfer
     * these data arrays.
     *
     * @param dos The data output stream
     */
    public void outputToStream(PrintWriter dos) throws IOException {
        dos.println(this.getFile());
        if (isLineNumber()) {
            dos.println("Line numbers marked:");
            dos.println(lineNos.length);
            dos.println(listLineNumbers());
        }
        else
            dos.println("No line numbers marked.");
        dos.println(charGroupStack.size());
        for (int k = 0; k < charGroupStack.size(); k++) {
            ArrayList cg = (ArrayList) charGroupStack.get(k);
            dos.println(k);
            dos.println(cg.size());
            dos.println(listCharGroups(k));
        }
        dos.println(this.getText().length());
        dos.println(this.getText());
    }


    /**
     * Used when Triana types want to be able to
     * receive ASCII data from the output of other programs.  This is used to
     * implement socket and to run other executables, written in C or
     * other languages. With ASCII you don't have to worry about
     * ENDIAN'ness as the conversions are all done via text. This is
     * obviously slower than binary communication since you have to format
     * the input and output within the other program.
     * </p><p>
     * This method must be overridden in every subclass that defines new
     * data or parameters. The overriding method should first call<PRE>
     *      super.inputFromStream(dis)
     * </PRE>to get input from superior classes, and then new parameters defined
     * for the current subclass must be input. Moreover, subclasses
     * that first dimension their data arrays must explicitly transfer
     * these data arrays.
     *
     * @param dis The data input stream
     */
    public void inputFromStream(BufferedReader dis) throws IOException {
        String line;

        setFile(dis.readLine());
        if (dis.readLine().equals("Line numbers marked:")) {
            int lines = Str.strToInt(dis.readLine());
            int[] lineNums = new int[lines];
            for (int i = 0; i < lineNums.length; ++i)
                lineNums[i] = Str.strToInt(dis.readLine());
            setLineNumbers(lineNums);
        }
        int stackLength = Str.strToInt(dis.readLine());
        for (int k = 0; k < stackLength; k++) {
            int groups = Str.strToInt(dis.readLine());
            int[] delims;
            String gp, nm;
            int s, e;
            ArrayList cG = new ArrayList(groups);
            for (int g = 0; g < groups; ++g) {
                gp = dis.readLine();
                delims = new int[2];
                s = gp.indexOf("(");
                e = gp.indexOf(",");
                nm = gp.substring(s, e).trim();
                delims[0] = Integer.parseInt(nm);
                s = e + 1;
                e = gp.indexOf(")");
                nm = gp.substring(s, e).trim();
                delims[1] = Integer.parseInt(nm);
                cG.add(delims);
            }
            charGroupStack.set(k, cG);
        }
        int chars = Str.strToInt(dis.readLine());
        StringBuffer doc = new StringBuffer(chars);
        while ((line = dis.readLine()) != null) doc.append(line).append(eol);
        setText(doc.toString());
    }

    /**
     * Used to make the new
     * types derived from TrianaType backward-compatible with
     * older types. It must be called by any method that
     * modifies data in <i>dataContainer</i> or in any other variable
     * that replaces a storage location used previously by any
     * type. It must be implemented (over-ridden) in any type
     * that re-defines storage or access methods to any
     * variable. The implementation should assign the
     * new variables to the obsolete ones, and ensure that
     * obsolete access methods retrieve data from the new
     * locations. Any over-riding method should finish
     * with the line<PRE>
     *       super.updateObsoletePointers;
     * </PRE>
     */
    protected void updateObsoletePointers() {
        super.updateObsoletePointers();
    }

    /**
     * Provides the toString method for completeness. Synonym for getText.
     *
     * @return String the contents of this Document
     */
    public String toString() {
        return getText();
    }


}
















