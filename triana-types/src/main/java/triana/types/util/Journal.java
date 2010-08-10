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


import java.util.LinkedList;

/**
 * Journal is a class that accumulates the history of processing on a TrianaType data object. It has a tree-like
 * structure containing the information about the present data set, and how it was created, plus references to Journals
 * of data sets that were used as input in the creation of the present set. Units add information to the Journal when
 * they create the data set and add references to other Journals when they output the set. </p><p> Journal is
 * implemented as a LinkedList. Its first element is a StringVector, and when used with a TrianaType data object this is
 * the StringVector description of the object. The elements after the first are themselves Journals that represent the
 * histories of input data sets. </p><p> Methods are provided for extending the lists, for writing the whole tree to a
 * String for output, and for re-constructing a Journal from a StringVector input.
 *
 * @author B F Schutz
 * @version $Revision: 4048 $
 * @see triana.types.TrianaType
 */
public class Journal extends LinkedList {


    /**
     * Constructs an empty Journal with fixed storage capacity and capacityIncrement.
     */
    public Journal() {
        super();
    }

    /**
     * Constructs a Journal whose first element is the given StringVector.
     *
     * @param information the information about the present object
     * @see StringVector
     */
    public Journal(StringVector information) {
        addFirst(information);
    }

    /**
     * Adds a Journal to the present Journal.
     *
     * @param inputData the Journal associated with an input data set
     */
    public void addJournal(Journal inputData) {
        addLast(inputData);
    }

    /**
     * Copies this Journal to a new Journal object, by value not by reference.
     */
    public Journal copyMe() {
        Journal copy = new Journal(((StringVector) this.getFirst()).copy());
        if (this.size() > 1) {
            for (int i = 1; i < this.size(); i++) {
                copy.addJournal(((Journal) this.get(i)).copyMe());
            }
        }
        return copy;
    }


    /**
     * Returns a String consisting of the contents of the first element of the present Journal and of the first elements
     * of all the Journals in the list, converted to Strings. The String is structured so that the Journals of previous
     * data sets are preceded with a tabbing String (which can be any String, not just <i>\t</i>) to set them apart from
     * the first, or highest-level, Journal. Each level of the tree further from the present Journal gets an extra
     * tabbing String prepended.
     *
     * @param tabbing the String that is to be prepended
     * @param level   the number of times that tabbing is prepended
     * @return String the report
     */
    public String report(String tabbing, int level) {
        //System.out.println("Entering report with tabbing = " + tabbing + ", level = " + String.valueOf(level) + ", size = " + String.valueOf( size() ) );
        String s = "";
        String prepend = "";
        for (int j = 0; j < level; j++) {
            prepend += tabbing;
        }
        StringVector description = (StringVector) getFirst();
        if (description == null) {
            return prepend + "<i><i><i>\n";
        }
        for (int i = 0; i < description.size(); ++i) {
            s += prepend + (String) description.get(i) + "\n";
        }
        if (size() <= 1) {
            return s + prepend + "<i><i><i>\n";
        }
        s += prepend + "Created from the following inputs: \n";
        for (int k = 1; k < size(); k++) {
            s += prepend + "Input " + String.valueOf(k - 1) + ":\n";
            if (get(k) instanceof Journal) {
                s += ((Journal) get(k)).report(tabbing, level + 1);
            } else {
                s += prepend + tabbing + "No input data.\n" + prepend + tabbing + "<i><i><i>\n";
            }
        }
        s += prepend + "<i><i><i>\n";
        return s;
    }

    /**
     * Reads an input StringVector and creates a Journal. This is the inverse of the previous method: a string written
     * by method <i>report</i> and converted to a StringVector by breaking it on lines will be converted by this method
     * back to the original Journal.
     *
     * @param report  A report created by method <i>report</i> and converted to a StringVector
     * @param tabbing the indentation character(s) in the input report
     * @return Journal the reconstructed Journal.
     * @see StringVector
     */
    public static Journal readReport(StringVector report, String tabbing) {

        String prepend = "";
        int tabLength = tabbing.length();
        int j;
        StringVector description = new StringVector(10);
        int line = 0;


        int tabpos = 0;
        if (report.first().endsWith("<i><i><i>")) {
            return null;
        }
        while (report.first().startsWith(tabbing, tabpos)) {
            tabpos += tabLength;
        }
        if (tabpos > 0) {
            prepend = report.first().substring(0, tabpos);
        }

        int lastLine = 0;
        while (!report.at(lastLine).equals(prepend + "<i><i><i>")) {
            lastLine++;
        } //last line of report
        //System.out.println(String.valueOf(ID) + ": LstLine = " + String.valueOf( lastLine ) );

        boolean moreInputs = false;
        for (j = 0; j <= lastLine; j++) {
            //System.out.println(String.valueOf(ID) + ": Point 1 with j = " + String.valueOf( j ) );
            if (report.at(j).indexOf("Created from the following inputs: ") != -1) {
                moreInputs = true;
                break;
            }
        }
        if (moreInputs) {
            line = j; // first line afer description
        } else {
            line = lastLine;
        }
        //System.out.println(String.valueOf(ID) + ": First line after description = " + String.valueOf( line ) + ", with moreInputs = " + String.valueOf( moreInputs ) );

        for (j = 0; j < line; j++) {
            description.add(report.at(j).substring(prepend.length()));
        }
        Journal journal = new Journal(description);
        //System.out.println(String.valueOf(ID) + ": Point 2 with line = " + String.valueOf(line) + " and lastLine = " + String.valueOf(lastLine) );
        if (report.at(line).endsWith("<i><i><i>")) {
            return journal;
        }

        j = line + 1; // skip "Created from ... " line
        while ((j < lastLine)) {
            j++; // skip "Input ... " line
            //System.out.println(String.valueOf(ID) + ": Point 3 with j = " + String.valueOf( j ) );
            if (report.at(j).endsWith("<i><i><i>")) {
                ;
            } else if (report.at(j).endsWith("No input data.")) {
                j += 2; // skip "<i><i><i>" and "Created from ... " line
                journal.add(null);
                //System.out.println(String.valueOf(ID) + ": Added null journal.");
            } else {
                description = new StringVector(10);
                //System.out.println(String.valueOf(ID) + ": Preparing new StringVector.");
                while (!report.at(j).equals(prepend + tabbing + "<i><i><i>")) {
                    description.add(report.at(j));
                    j++;
                }
                description.add(report.at(j++));
                journal.add(readReport(description, tabbing));
            }
        }
        return journal;
    }


}
















