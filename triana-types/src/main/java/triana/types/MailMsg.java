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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import triana.types.util.Str;
import triana.types.util.StringSplitter;
import triana.types.util.StringVector;

/**
 * MailMsg is a subclass of Document which stores the contents of a single email message. The message is stored with
 * headers if it is incoming, and with MIME attachments. The type provides methods for examining relevant information,
 * such as the date and sender of the message. Messages can be locked when created, so that they are createTool-only.
 * For non-locked messages, there are messages for setting the date, sender, etc, and for setting the text. There are
 * also methods for interpreting the date for a wide variety of possible input formats; for parsing email names into
 * email addresses and personal names; and for locating and testing the integrity of MIME attachments.
 * <p/>
 * The first version 1.x provides very limited inspection of header data. Only the date, from, to, cc, bcc, subject, and
 * message-id fields are available as parameter data. There is no facility yet for inserting a MIME attachment or
 * dealing with flags for whether messages have been createTool or replied to, their priority, threads etc.
 *
 * @author Bernard Schutz
 * @version $Revision: 4048 $
 */
public class MailMsg extends Document implements AsciiComm {

    /*
    * Metadata consists of Strings containing information from a normal email
    * header, followed by information useful in Triana networks.
    */

    /**
     * Contains the date of the message as a String
     */
    private String date = "";

    /**
     * Contains the sender of the message as a String
     */
    private String sender = "";

    /**
     * Contains the recipient of the message as a String
     */
    private String recipient = "";

    /**
     * Contains the recipient(s) of copies of the message as a String
     */
    private String copiesTo = "";

    /**
     * Contains the recipient(s) of blind copies of the message as a String
     */
    private String blindCopiesTo = "";

    /**
     * Contains the subject of the message as a String
     */
    private String subject = "";

    /**
     * Contains the ID of the message as a String
     */
    private String ID = "";

    /**
     * Character index at which the message body starts, after the header is finished. If there is no body in the
     * message, then this is -1.
     */
    private int body = 0;

    /**
     * Character index at which the MIME attachment starts, after the body text is finished. If there is no MIME
     * attachment in the message, then this is -1.
     */
    private int mime = -1;

    /**
     * Boolean variable which is <i>true</i> if the data is createTool-only. If this is <i>true</i>, then methods that
     * modify the data will not work.
     */
    private boolean readOnly = false;

    /**
     * Mailbox folder (file) from which the mail message was createTool.
     */
    private FileName fromFolder = null;

    /**
     * Directory which is the root of the mail directories, so that if mail folders are written to a different root, the
     * entire mail tree will be duplicated.
     */
    private FileName mailRoot = null;

    /**
     * List of mailbox folders (files) into which the mail message should be stored. This enables multiple copies to be
     * stored for sensible grouping of messages.
     */
    private StringVector toFolders = new StringVector(3);


    /**
     * Marks message for deletion. If <i>true</i>, units that write messages to files should ignore <i>toFolders</i> and
     * write this to the deletion file. Similarly, units that perform filtering (create entries in <i>toFolders</i>)
     * should ignore the message if it is marked for deletion.
     */
    private boolean delete;

    /**
     * Marks messages as damaged. If <i>true</i>, units that write messages to files may ignore <i>toFolders</i> and
     * write this to a special file for inspection.
     */
    private boolean damaged;

    /**
     * The local end-of-line character.
     */
    private String eol = System.getProperty("line.separator");

    /**
     * Needed for umlaut replacement as used in some email names that confuse searches for quotation marks.
     */
    private static String umlaut = (new StringBuffer(2)).append('\\').append('"').toString();


    /**
     * Constructs an empty MailMsg.
     */
    public MailMsg() {
        super();
    }

    /**
     * Constructs a MailMsg from an input mail message, complete with header and MIME attachments, and the name of the
     * file from which it was createTool. It is locked to createTool-only: it cannot be modified. Various header data
     * are createTool and set, but it is not inspected for consistency of its MIME attachments.
     *
     * @param message The email message, with its header
     */
    public MailMsg(String message, FileName file) {
        this();
        if ((message == null) || (message.length() == 0)) {
            setText("");
            setSubject("empty message");
        } else {
            setText(message);
            setFromFolder(file);
            setBodyLocation(findHeaderEnd(message));
            if (body == -1) {  //can't find separation between header and rest of message
                setSubject("message not in standard format");
            } else {
                StringSplitter header = new StringSplitter(message.substring(0, body),
                        "newline"); // the last element is an empty string ( the final blank line)
                setSender(getHeaderField(header, "From: "));
                setDate(getHeaderField(header, "Date: "));
                setRecipient(getHeaderField(header, "To: "));
                setCopiesTo(getHeaderField(header, "Cc: "));
                setBlindCopiesTo(getHeaderField(header, "Bcc: "));
                setSubject(getHeaderField(header, "Subject: "));
                setID(getHeaderField(header, "Message-Id: "));
            }
            checkMIME();
        }
        readOnly = true;
    }


    /*
     * Searches the message header for the required information. It
     * finds a section of the header that begins with the given String
     * starting on a new line. It returns the remainder of the line
     * and any further lines that begin with whitespace. The header is
     * searched for the field up to three times: once for the given
     * string, then for the string converted to upper case, and finally
     * for the string converted to lower case. If none of the searches
     * succeeds, then the method returns an empty string (not null).
     */

    private String getHeaderField(StringVector header, String field) {
        //System.out.println("Get header for " + field );

        String fieldUpper = field.toUpperCase();
        String fieldLower = field.toLowerCase();
        String line;
        String data = "";
        boolean found = false;
        int startLine = 0;
        int headLines = header.size() - 1; // ignore final blank line of input
        int fieldLength = field.length();
        for (int j = 0; j < headLines; j++) {
            line = header.at(j);
            if (line.startsWith(field) || line.startsWith(fieldLower) || line.startsWith(fieldUpper)) {
                startLine = j;
                data = line.substring(fieldLength) + " ";
                found = true;
                break;
            }
        }
        if (!found) {
            return "";
        }

        int endLine = startLine + 1;
        if (endLine == headLines) {
            return data;
        }

        while (Character.isWhitespace(header.at(endLine).charAt(0))) {
            endLine++;
            if (endLine == headLines) {
                endLine--;
                break;
            }
        }

        for (int k = startLine + 1; k < endLine; k++) {
            data += header.at(k) + " ";
        }
        return data;

    }

    /*
     * Finds the end of the header at a double eol.
     */

    private int findHeaderEnd(String message) {
        int doubleEOL = message.indexOf(eol + eol);
        if (doubleEOL == -1) {
            return -1;
        }
        return doubleEOL + 2 * eol.length();
    }

    /*
     * Interpret a date contained somewhere in the input String
     *
     * Returns the date or null if the string cannot be parsed
     */

    public Date parseDate(SimpleDateFormat s) {
        String dateLine = getDate();
        int len = dateLine.length();
        ParsePosition pos;
        if ((dateLine == null) || (dateLine.length() == 0)) {
            return null;
        }
        int parsePos = 0;
        Date msgDate = null;
        while ((msgDate == null) && parsePos < len) {
            pos = new ParsePosition(parsePos++);
            msgDate = s.parse(dateLine, pos);
        }
        return msgDate;
    }


    /**
     * Returns the sender of the message.
     *
     * @return String The sender as contained in the message
     */
    public String getSender() {
        return sender;
    }

    /**
     * Returns the sender of the message as a StringVector with one element.
     *
     * @return StringVector The sender as contained in the message
     */
    public StringVector getSenderList() {
        return addressList(getSender());
    }

    /**
     * Sets the sender of the message. This only works if the message is not createTool-only.
     *
     * @param newSender The sender to be set into the message
     */
    public void setSender(String newSender) {
        if (!readOnly) {
            sender = newSender;
        }
    }

    /**
     * Returns the date of the message.
     *
     * @return String The date as contained in the message
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the message. This only works if the message is not createTool-only.
     *
     * @param newDate The date to be set into the message
     */
    public void setDate(String newDate) {
        if (!readOnly) {
            date = newDate;
        }
    }

    /**
     * Returns the recipient(s) of the message as a String containing all recipients, just as given in the email
     * message.
     *
     * @return String The recipient(s) as contained in the message
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Returns the recipient addresses in a StringVector, one recipient per element. This method separates the
     * recipients String into separate addresses by dividing it at commas that are not contained inside quotation marks
     * or brackets.
     *
     * @return StringVector The individual addresses as a list
     */
    public StringVector getRecipientList() {
        return addressList(getRecipient());
    }


    /**
     * Sets the recipient(s) of the message. This only works if the message is not createTool-only.
     *
     * @param newRecipient The recipient(s) to be set into the message
     */
    public void setRecipient(String newRecipient) {
        if (!readOnly) {
            recipient = newRecipient;
        }
    }

    /**
     * Returns the carbon-copy recipient(s) of the message as a String containing all such recipients just as contained
     * in the email message.
     *
     * @return String The carbon-copy recipient(s) as contained in the message
     */
    public String getCopiesTo() {
        return copiesTo;
    }

    /**
     * Returns the carbon-copy recipient addressess in a StringVector, one recipient per element. This method separates
     * the recipients String into separate addresses by dividing it at commas that are not contained inside quotation
     * marks or brackets.
     *
     * @return StringVector The individual addresses as a list
     */
    public StringVector getCopiesToList() {
        return addressList(getCopiesTo());
    }

    /**
     * Sets the carbon-copy recipient(s) of the message. This only works if the message is not createTool-only.
     *
     * @param newCopiesTo The carbon-copy recipient(s) to be set into the message
     */
    public void setCopiesTo(String newCopiesTo) {
        if (!readOnly) {
            copiesTo = newCopiesTo;
        }
    }

    /**
     * Returns the blind (hidden) carbon-copy recipient(s) of the message as a String containing all such recipients,
     * exactly as in the email message.
     *
     * @return String The blind carbon-copy recipient(s) as contained in the message
     */
    public String getBlindCopiesTo() {
        return blindCopiesTo;
    }

    /**
     * Returns the blind (hidden) recipient addressess in a StringVector, one recipient per element. This method
     * separates the recipients String into separate addresses by dividing it at commas that are not contained inside
     * quotation marks or brackets.
     *
     * @return StringVector The individual addresses as a list
     */
    public StringVector getBlindCopiesToList() {
        return addressList(getBlindCopiesTo());
    }

    /**
     * Sets the blind (hidden) carbon-copy recipient(s) of the message. This only works if the message is not
     * createTool-only.
     *
     * @param newBlindCopiesTo The blind carbon-copy recipient(s) to be set into the message
     */
    public void setBlindCopiesTo(String newBlindCopiesTo) {
        if (!readOnly) {
            blindCopiesTo = newBlindCopiesTo;
        }
    }

    /**
     * Attempts to separate the input String into addresses of email recipients. It assumes that the input list consists
     * of such addresses separated by commas. The method ignores commas contained within quotation marks or brackets,
     * which could be used as part of a recipient's name. It is not confused by umlauts of the form given in the global
     * variable <i>umlaut</i>.
     *
     * @param addresses The input newline- and comma-separated list of addresses
     * @return The individual addresses as a List
     */
    public StringVector addressList(String addresses) {
        //System.out.println("MailMsg.addressList(): entered with addresses String = " + eol + addresses );
        String temp = Str.replaceAll(addresses, eol, " ");
        int quoteOpen, quoteEnd, bracketOpen, bracketEnd, comma;
        //System.out.println("MailMsg.addressList(): Umlaut represented by String " + umlaut);
        //System.out.println("MailMsg.addressList() finds umlaut at " + String.valueOf( temp.indexOf( umlaut ) ) );
        temp = Str.replaceAll(temp, umlaut, "##UMLAUT##");
        //System.out.println("MailMsg.addressList(): after initial replacements, the addresses String = " + eol + temp );
        quoteOpen = temp.indexOf('"');
        while (quoteOpen != -1) {
            quoteEnd = temp.indexOf('"', quoteOpen + 1);
            comma = temp.indexOf(',', quoteOpen);
            while ((comma > quoteOpen) && (comma < quoteEnd)) {
                temp = temp.substring(0, comma) + "##COMMA##" + temp.substring(comma + 1);
                quoteEnd += 8;
                comma = temp.indexOf(",", comma);
            }
            if (quoteEnd < temp.length() - 1) {
                quoteOpen = temp.indexOf('"', quoteEnd + 1);
            } else {
                quoteOpen = -1;
            }
        }
        bracketOpen = temp.indexOf('(');
        while (bracketOpen != -1) {
            bracketEnd = temp.indexOf(')', bracketOpen + 1);
            comma = temp.indexOf(',', bracketOpen);
            while ((comma > bracketOpen) && (comma < bracketEnd)) {
                temp = temp.substring(0, comma) + "##COMMA##" + temp.substring(comma + 1);
                bracketEnd += 8;
                comma = temp.indexOf(",", comma);
            }
            bracketOpen = temp.indexOf('(', bracketEnd);
        }
        //System.out.println("MailMsg.addressList(): after comma-in-quotes-and-brackets replacement plus eol removal in addresses String = " + eol + temp );
        StringVector addressVector = new StringSplitter(temp, ",");
        for (int j = 0; j < addressVector.size(); j++) {
            addressVector.setElementAt(
                    Str.replaceAll(Str.replaceAll(addressVector.at(j), "##UMLAUT##", umlaut), "##COMMA##", ","), j);
        }
        return addressVector;
    }

    /**
     * Returns the subject of the message.
     *
     * @return String The subject as contained in the message
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject of the message. This only works if the message is not createTool-only.
     *
     * @param newSubject The subject to be set into the message
     */
    public void setSubject(String newSubject) {
        if (!readOnly) {
            subject = newSubject;
        }
    }

    /**
     * Returns the ID of the message.
     *
     * @return String The ID as contained in the message
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets the ID of the message. This only works if the message is not createTool-only.
     *
     * @param newID The ID to be set into the message
     */
    public void setID(String newID) {
        if (!readOnly) {
            ID = newID;
        }
    }

    /**
     * Sets the filename of the directory that is the root of the mail file system from which this message came.
     *
     * @param fn The FileName of the directory that is the mail system root
     * @return boolean True if the input name is a directory, false if not
     */
    public boolean setMailRoot(FileName fn) {
        File input = new File(fn.getFile());
        if (input.isDirectory()) {
            mailRoot = fn;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the filename of the directory that is the root of the mail file system from which this message came.
     *
     * @return FileName of the directory of the root of the mail system
     */

    public FileName getMailRoot() {
        return mailRoot;
    }

    /**
     * Sets the filename of the folder (mailbox) from which the message was createTool.
     *
     * @param fn The file which contained the message when it was created
     */
    public void setFromFolder(FileName fn) {
        fromFolder = fn;
    }

    /**
     * Returns the filename of the folder (mailbox) from which the message was createTool.
     *
     * @return FileName The file which contained the message when it was created
     */
    public FileName getFromFolder() {
        return fromFolder;
    }

    /**
     * Sets the list of filenames of the folders (mailboxes) into which the message should be written.
     *
     * @param to The file into which the message should be written
     */
    public void setToFolders(StringVector to) {
        toFolders = to;
    }

    /**
     * Adds to the list <i>toFolders</i> a foldername (just a String ) of the folder (mailbox) into which the message
     * should be written.
     *
     * @param fn The file into which the message should be written
     */
    public void addToFolder(String fn) {
        toFolders.add(fn);
    }

    /**
     * Adds to the list <i>toFolders</i> a StringVector of foldernames of the folders (mailboxes) into which the message
     * should be written.
     *
     * @param fns The file into which the message should be written
     */
    public void addToFolders(StringVector fns) {
        toFolders.append(fns);
    }

    /**
     * Returns the list of filenames of the folders (mailboxes) into which the message should be written.
     *
     * @return LinkedList The list of files into which the message should be written
     */
    public StringVector getToFolders() {
        return toFolders;
    }

    /**
     * Returns a copy-by-value of the list of filenames of the folders (mailboxes) into which the message should be
     * written.
     *
     * @return LinkedList The list of files into which the message should be written
     */
    public StringVector copyToFolders() {
        return toFolders.copy();
    }

    /**
     * Returns <i>true</i> if the message has been marked for deletion.
     *
     * @return <i>True</i> if message is to be deleted
     */
    public boolean getDeletion() {
        return delete;
    }

    /**
     * Sets the boolean flag that deletes the message.
     *
     * @param del <i>True</i> if the message is to be deleted
     */
    public void setDeletion(boolean del) {
        delete = del;
    }

    /**
     * Returns <i>true</i> if the message has been marked as damaged.
     *
     * @return <i>True</i> if message is damaged
     */
    public boolean isDamaged() {
        return damaged;
    }

    /**
     * Sets the boolean flag that marks the message as damaged.
     *
     * @param dam <i>True</i> if the message is damaged
     */
    public void setDamaged(boolean dam) {
        damaged = dam;
    }


    /**
     * Returns the character index location where the message body begins after the header.
     *
     * @return int The start of the message
     */
    public int getBodyLocation() {
        return body;
    }

    /**
     * Sets the character index at which the message starts, after the header. This only works if the message is not
     * createTool-only.
     *
     * @param newBody The starting character of the message body
     */
    public void setBodyLocation(int newBody) {
        if (!readOnly) {
            body = newBody;
        }
    }

    /**
     * Returns the character index location where the MIME attachment begins after the main body text.
     *
     * @return int The start of the MIME attachment
     */
    public int getMIMELocation() {
        return mime;
    }

    /**
     * Sets the character index at which the MIME attachment starts, after the body text. This only works if the message
     * is not createTool-only.
     *
     * @param newMIME The starting character of the MIME attachment
     */
    public void setMIMELocation(int newMIME) {
        if (!readOnly) {
            mime = newMIME;
        }
    }

    /**
     * Returns the value of the lock parameter
     *
     * @return boolean True if message is createTool-only
     */
    public boolean getLock() {
        return readOnly;
    }

    /**
     * Unlocks the message so it can be edited.
     */
    public void unlock() {
        readOnly = false;
    }

    /**
     * Locks the message so it cannot be edited.
     */
    public void lock() {
        readOnly = true;
    }

    /**
     * Returns the message body, without the header, including any MIME attachments.
     *
     * @return String The message body without the header but with attachments
     */
    public String getBody() {
        if (body == -1) {
            return "";
        }
        return getText().substring(body);
    }

    /**
     * Returns the message body, without the header or MIME attachments.
     *
     * @return String The message body without the header or attachments
     */
    public String getBodyNoMIME() {
        if (body == -1) {
            return "";
        }
        if (mime == -1) {
            return getBody();
        }
        return getText().substring(body, mime);
    }

    /**
     * Returns the MIME attachment, as encoded in the message.
     *
     * @return String The encoded MIME attachment
     */
    public String getMIME() {
        if (mime == -1) {
            return "";
        }
        return getText().substring(mime);
    }

    /**
     * Checks MIME attachments to see if the delimiters are consistent.
     *
     * @return boolean True if the MIME attachments seem consistent
     */
    public boolean checkMIME() {
        String msg = getText();
        String boundary;
        int contentsLoc = msg.indexOf("Content-Type: ");
        if (contentsLoc == -1) {
            return true;
        }
        StringSplitter header = new StringSplitter(msg.substring(0, body), "newline");
        String contents = getHeaderField(header, "Content-Type: ");
        int bound = contents.indexOf("boundary=");
        if (bound == -1) {
            return true;
        }
        if (contents.charAt(bound + 9) == '"') {
            contentsLoc += bound + 10;
            boundary = contents.substring(bound + 10, contents.length() - 1);
        } else {
            contentsLoc += bound + 9;
            boundary = contents.substring(bound + 9);
        }
        //System.out.println("boundary string = " + boundary);
        return (goodMSection(msg, contentsLoc + boundary.length(), boundary) != -1);
    }

    /*
     * Recursively investigates message to see that MIME parts are all in
     * correct places, nested etc. Returns the index of the first character
     * after the end of the MIME section, provided the section is consistent.
     * If the end does not exist or if there are errors with nested sections,
     * this returns -1.
     *
     */

    private int goodMSection(String msg, int startIndex, String boundary) {
        String newBoundary, contents;
        int bound, currentPosition, opening, ending, contentsLoc, endPosition;
        boolean nesting;
        //System.out.println("Entered goodMSection" );
        opening = msg.indexOf("--" + boundary, startIndex);
        if (opening == -1) {
            //System.out.println("goodMSection return A");
            return -1;
        }
        currentPosition = opening + boundary.length() + 2;
        ending = msg.indexOf("--" + boundary + "--", currentPosition);
        if (ending == -1) {
            //System.out.println("goodMSection return C");
            return -1;
        } else {
            setMIMELocation(ending);
        }
        endPosition = ending + boundary.length() + 4;
        nesting = true;
        while (nesting) {
            contentsLoc = msg.indexOf("Content-Type: ", currentPosition);
            if ((contentsLoc != -1) && (contentsLoc < ending)) {
                contents = getHeaderField(new StringSplitter(msg.substring(currentPosition, body), "newline"),
                        "Content-Type: ");
                bound = contents.indexOf("boundary=");
                if (bound != -1) {
                    contentsLoc += bound + 10;
                    newBoundary = contents.substring(bound + 10, contents.length() - 1);
                    currentPosition = goodMSection(msg, contentsLoc + newBoundary.length(), newBoundary);
                    if (currentPosition == -1) {
                        //System.out.println("goodMSection return D");
                        return -1;
                    }
                } else {
                    nesting = false;
                }
            } else {
                nesting = false;
            }
        }
        //System.out.println("goodMSection return E");
        return endPosition;
    }

    /**
     * Parses a String containing a single recipient address into the email address (the part containing the @ symbol)
     * and the name of the person, if it exists. These parts are placed in this order into the two elements of the
     * StringVector that is returned. The input String can be an element of the StringVector returned by
     * <i>getRecipientList()</i>, for example. </p><p> The method finds the email address by looking for the first
     *
     * @param fullAddress The input String containing the full address
     * @return Contains two Strings, the first being the address and the second the interpreted name
     */
    public static StringVector parseAddress(String fullAddress) {
        String currentName = fullAddress;
        String tempName;
        String emailAddress = "";
        String personalName = "";
        try {
            int atLocation = currentName.indexOf("@");
            if (atLocation == -1) {    //not an email name
                emailAddress = "Error: not an email name";
                personalName = "";
                //System.out.println("MailMsg.parseAddress: checkpoint A");
            } else {
                /*
                 * Check first for stuff in <..> -- this is quicker than
                 * if we have to scan for the string containing @
                 */
                //System.out.println("MailMsg.parseAddress: checkpoint B");
                int openEmail = currentName.indexOf("<");
                int closeEmail;
                char testChar;
                if (openEmail >= 0) { //contains email address in <..>
                    //System.out.println("MailMsg.parseAddress: checkpoint C");
                    closeEmail = currentName.indexOf(">", openEmail);
                    emailAddress = currentName.substring(openEmail + 1, closeEmail).trim();
                    currentName = currentName.substring(0, openEmail).trim();
                } else {                  // no <..> construction
                    //System.out.println("MailMsg.parseAddress: checkpoint D");
                    openEmail = atLocation;
                    testChar = currentName.charAt(openEmail);
                    while (!Character.isWhitespace(testChar)) {
                        openEmail--;
                        if (openEmail < 0) {
                            break;
                        }
                        testChar = currentName.charAt(openEmail);
                    }
                    openEmail++;
                    closeEmail = atLocation;
                    testChar = currentName.charAt(closeEmail);
                    while ((testChar != ',') && !Character.isWhitespace(testChar)) {
                        closeEmail++;
                        if (closeEmail >= currentName.length()) {
                            break;
                        }
                        testChar = currentName.charAt(closeEmail);
                    }
                    closeEmail--;
                    if (closeEmail == currentName.length() - 1) {
                        //System.out.println("MailMsg.parseAddress: checkpoint E");
                        emailAddress = currentName.substring(openEmail).trim();
                        if (openEmail > 0) {
                            currentName = currentName.substring(0, openEmail - 1).trim();
                        } else {
                            currentName = "";
                        }
                    } else {
                        //System.out.println("MailMsg.parseAddress: checkpoint F");
                        emailAddress = currentName.substring(openEmail, closeEmail + 1);
                        if (closeEmail < currentName.length() - 2) {
                            //System.out.println("MailMsg.parseAddress: checkpoint G");
                            tempName = currentName.substring(closeEmail + 2).trim();
                            int openBracket, closeBracket;
                            if (tempName.length() != 0) {
                                //System.out.println("MailMsg.parseAddress: checkpoint H");
                                currentName = tempName;
                                openBracket = currentName.indexOf('(');
                                if (openBracket != -1) {
                                    //System.out.println("MailMsg.parseAddress: checkpoint I");
                                    closeBracket = currentName.indexOf(')');
                                    if (closeBracket == -1) {
                                        closeBracket = currentName.length();
                                    }
                                    currentName = currentName.substring(openBracket + 1, closeBracket);
                                }
                            }
                        } else {
                            currentName = currentName.substring(0, openEmail).trim();
                        }
                        //System.out.println("MailMsg.parseAddress: checkpoint J");
                    }
                }
                if (currentName.length() != 0) {
                    //System.out.println("MailMsg.parseAddress: checkpoint K");
                    currentName = Str.replaceAll(currentName, umlaut, "##UMLAUT##");
                    int openQuote, closeQuote;
                    openQuote = currentName.indexOf('"');
                    if (openQuote >= 0) {
                        //System.out.println("MailMsg.parseAddress: checkpoint L");
                        closeQuote = currentName.indexOf('"', openQuote + 1);
                        if (closeQuote == -1) {
                            currentName = currentName.substring(openQuote + 1);
                        } else {
                            currentName = currentName.substring(openQuote + 1, closeQuote);
                        }
                    }
                    openQuote = currentName.lastIndexOf("'");
                    while (openQuote >= 0) {
                        if (openQuote == currentName.length() - 1) {
                            currentName = currentName.substring(0, openQuote);
                        } else {
                            currentName = currentName.substring(0, openQuote) + currentName.substring(openQuote + 1);
                        }
                        openQuote = currentName.lastIndexOf("'", openQuote - 1);
                    }
                    currentName = Str.replaceAll(currentName, "##UMLAUT##", umlaut);
                }
                //System.out.println("MailMsg.parseAddress: checkpoint M");
                personalName = currentName;
            }
        }
        catch (IndexOutOfBoundsException ex) {
            //System.out.println("MailMsg.parseAddress: checkpoint N");
            emailAddress = "Error: not an email name";
            personalName = "";
        }
        //System.out.println("MailMsg.parseAddress: checkpoint O");
        StringVector answer = new StringVector(2);
        answer.add(emailAddress);
        answer.add(personalName);
        return answer;
    }

    /**
     * Creates a filename in a standard format from an input personal name. The personal name can have many standard
     * forms. The filename will be in the form LastnameFirstinitials with no spaces, and where "Lastname" includes name
     * parts like "von" or "de", while "Firstinitials" is made from all given names separated from the part of the input
     * name containing the elements of "Lastname". (Initials are included without punctuation.)
     * <p/>
     * This can be used to construct a filename automatically for email addresses from a given sender. The input
     * personal name could be taken from the output of <i>parseAddress</i>.
     *
     * @param nameIn Input personal name
     * @return filename Output file name in standard form
     */
    public static String filenameFromName(String nameIn) {
        String name = nameIn.trim();
        if (name.length() == 0) {
            return "";
        }
        if (!Character.isLetter(name.charAt(0))) {
            return "";
        }
        StringSplitter nameParts = new StringSplitter(name);
        int parts = nameParts.size();
        int j;
        String testPart;
        String firstInitials = "";
        String lastName = "";
        for (j = parts - 1; j >= 0; j--) {
            testPart = nameParts.at(j);
            if ((testPart.equalsIgnoreCase("dr")) || (testPart.equalsIgnoreCase("dr."))
                    || (testPart.equalsIgnoreCase("prof")) || (testPart.equalsIgnoreCase("prof."))
                    || (testPart.equalsIgnoreCase("phd")) || (testPart.equalsIgnoreCase("ph.d."))
                    || (testPart.equalsIgnoreCase("ma")) || (testPart.equalsIgnoreCase("m.a."))
                    || (testPart.equalsIgnoreCase("md")) || (testPart.equalsIgnoreCase("m.d."))
                    || (testPart.equalsIgnoreCase("mr")) || (testPart.equalsIgnoreCase("mr."))
                    || (testPart.equalsIgnoreCase("mrs")) || (testPart.equalsIgnoreCase("mrs."))
                    || (testPart.equalsIgnoreCase("frau")) || (testPart.equalsIgnoreCase("fr"))
                    || (testPart.equalsIgnoreCase("fr.")) || (testPart.equalsIgnoreCase("herr"))
                    || (testPart.equalsIgnoreCase("hr")) || (testPart.equalsIgnoreCase("hr."))) {
                nameParts.removeElementAt(j);
            } else {
                if (testPart.endsWith(".")) {
                    testPart = testPart.substring(0, testPart.length() - 1);
                    nameParts.setElementAt(testPart, j);
                }
            }
        }
        parts = nameParts.size();
        if (parts == 0) {
            return "";
        }
        if (parts == 1) {
            return nameParts.at(0);
        }
        name = nameParts.toAString();
        int commaLoc = name.indexOf(',');
        if (commaLoc == 0) { // handle comma at beginning of string
            name = name.substring(1);
            commaLoc = name.indexOf(',');
        }
        if (commaLoc == name.length() - 1) { // handle comma at end of string
            name = name.substring(0, commaLoc);
            commaLoc = -1;
        }
        if (commaLoc == -1) { // no comma, assume first name is first
            int lastNamePart = parts - 1; // assume last name is last word
            for (j = parts - 1; j >= 0; j--) { // check for key to last name
                testPart = nameParts.at(j);
                if ((testPart.equalsIgnoreCase("von")) || (testPart.equalsIgnoreCase("van"))
                        || (testPart.equalsIgnoreCase("der")) || (testPart.equalsIgnoreCase("den"))
                        || (testPart.equalsIgnoreCase("de")) || (testPart.equalsIgnoreCase("dos"))
                        || (testPart.equalsIgnoreCase("di")) || (testPart.equalsIgnoreCase("los"))
                        || (testPart.equalsIgnoreCase("la")) || (testPart.equalsIgnoreCase("ap"))) {
                    lastNamePart = j;
                }
            }
            for (j = 0; j < lastNamePart; j++) {
                firstInitials += nameParts.at(j).substring(0, 1).toUpperCase();
            }
            for (j = lastNamePart; j < parts; j++) {
                lastName += nameParts.at(j);
            }
        } else { // take first comma as break between lastname, firstname
            nameParts = new StringSplitter(name.substring(0, commaLoc).trim());
            for (j = 0; j < nameParts.size(); j++) {
                lastName += nameParts.at(j);
            }

            nameParts = new StringSplitter(name.substring(commaLoc + 1).trim());
            for (j = 0; j < nameParts.size(); j++) {
                firstInitials += nameParts.at(j).substring(0, 1).toUpperCase();
            }
        }
        return lastName + firstInitials;
    }


    /**
     * Returns <i>true</i> if the specified object doc is the same type and has the same contents as this object,
     * including the path name and file name. This tests all the auxilliary data, such as subject, etc, but it does not
     * test the deletion or damaged flags, nor does it test the source folder (<i>fromFolder</i>) or destination folders
     * (<i>toFoilders</i>). This allows it to compare messages that may have come from different locations or are
     * destined for different locations.
     *
     * @return Boolean <i>true</i> if the argument is a Document with the same contents as this Document
     */
    public boolean equals(Object doc) {

        if (!super.equals(doc)) {
            return false;
        }

        if (!(doc instanceof MailMsg)) {
            return false;
        }

        if (!getDate().equals(((MailMsg) doc).getDate())) {
            return false;
        }
        if (!getSender().equals(((MailMsg) doc).getSender())) {
            return false;
        }
        if (!getRecipient().equals(((MailMsg) doc).getRecipient())) {
            return false;
        }
        if (!getCopiesTo().equals(((MailMsg) doc).getCopiesTo())) {
            return false;
        }
        if (!getBlindCopiesTo().equals(((MailMsg) doc).getBlindCopiesTo())) {
            return false;
        }
        if (!getSubject().equals(((MailMsg) doc).getSubject())) {
            return false;
        }
        if (!getID().equals(((MailMsg) doc).getID())) {
            return false;
        }
        if (getBodyLocation() != ((MailMsg) doc).getBodyLocation()) {
            return false;
        }
        if (getLock() != ((MailMsg) doc).getLock()) {
            return false;
        }

        return true;
    }

    /**
     * This is one of the most important methods of Triana data. types. It returns a copy of the type invoking it. This
     * <b>must</b> be overridden for every derived data type derived. If not, the data cannot be copied to be given to
     * other units. Copying must be done by value, not by reference. </p><p> To override, the programmer should not
     * invoke the <i>super.copyMe</i> method. Instead, create an object of the current type and call methods
     * <i>copyData</i> and <i>copyParameters</i>. If these have been written correctly, then they will do the copying.
     * The code should createTool, for type YourType: <PRE> YourType y = null; try { y =
     * (YourType)getClass().newInstance(); y.copyData( this ); y.copyParameters( this ); y.setLegend( this.getLegend()
     * ); } catch (IllegalAccessException ee) { System.out.println("Illegal Access: " + ee.getMessage()); } catch
     * (InstantiationException ee) { System.out.println("Couldn't be instantiated: " + ee.getMessage()); } return y;
     * </PRE> </p><p> The copied object's data should be identical to the original. The method here modifies only one
     * item: a String indicating that the object was created as a copy is added to the <i>description</i> StringVector.
     *
     * @return TrianaType Copy by value of the current Object except for an updated <i>description</i>
     */
    public TrianaType copyMe() {
        MailMsg d = null;
        try {
            d = (MailMsg) getClass().newInstance();
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
     * Copies modifiable parameters from the given source.
     *
     * @param source The Document object being copied
     */
    public void copyParameters(TrianaType source) {

        super.copyParameters(source);

        boolean isLocked = ((MailMsg) source).getLock();
        unlock();
        setDate(((MailMsg) source).getDate());
        setSender(((MailMsg) source).getSender());
        setRecipient(((MailMsg) source).getRecipient());
        setCopiesTo(((MailMsg) source).getCopiesTo());
        setBlindCopiesTo(((MailMsg) source).getBlindCopiesTo());
        setSubject(((MailMsg) source).getSubject());
        setID(((MailMsg) source).getID());
        setBodyLocation(((MailMsg) source).getBodyLocation());
        readOnly = isLocked;

        setFromFolder(((MailMsg) source).getFromFolder());
        setToFolders(((MailMsg) source).copyToFolders());
        setDeletion(((MailMsg) source).getDeletion());
        setDamaged(((MailMsg) source).isDamaged());

    }


    /**
     * Used when Triana types want to be able to send ASCII data to other programs using strings.  This is used to
     * implement socket and to run other executables, written in C or other languages. With ASCII you don't have to
     * worry about ENDIAN'ness as the conversions are all done via text. This is obviously slower than binary
     * communication since you have to format the input and output within the other program. </p><p> This method must be
     * overridden in every subclass that defines new data or parameters. The overriding method should first call<PRE>
     * super.outputToStream(dos) </PRE>to get output from superior classes, and then new parameters defined for the
     * current subclass must be output. Moreover, subclasses that first dimension their data arrays must explicitly
     * transfer these data arrays.
     *
     * @param dos The data output stream
     */
    public void outputToStream(PrintWriter dos) throws IOException {
        super.outputToStream(dos);
        dos.println("Email Date: " + getDate());
        dos.println("Email From: " + getSender());
        dos.println("Email To: " + getRecipient());
        dos.println("Email Cc: " + getCopiesTo());
        dos.println("Email Bcc: " + getBlindCopiesTo());
        dos.println("Email Subject: " + getSubject());
        dos.println("Email Message-Id: " + getID());
        dos.println("Email Read-Only Lock: " + String.valueOf(getLock()));
        dos.println("Email From Folder: " + getFromFolder().getFile());
        dos.println("Email To Folders: " + getToFolders().toAString().trim());
        dos.println("Email Deletion Flag: " + String.valueOf(getDeletion()));
        dos.println("Email Damaged Flag: " + String.valueOf(isDamaged()));
    }


    /**
     * Used when Triana types want to be able to receive ASCII data from the output of other programs.  This is used to
     * implement socket and to run other executables, written in C or other languages. With ASCII you don't have to
     * worry about ENDIAN'ness as the conversions are all done via text. This is obviously slower than binary
     * communication since you have to format the input and output within the other program. </p><p> This method must be
     * overridden in every subclass that defines new data or parameters. The overriding method should first call<PRE>
     * super.inputFromStream(dis) </PRE>to get input from superior classes, and then new parameters defined for the
     * current subclass must be input. Moreover, subclasses that first dimension their data arrays must explicitly
     * transfer these data arrays.
     *
     * @param dis The data input stream
     */
    public void inputFromStream(BufferedReader dis) throws IOException {

        super.inputFromStream(dis);

        String line;
        String field;
        line = dis.readLine();
        field = line.substring(12);
        if (field.equals("null")) {
            setDate("");
        } else {
            setDate(field);
        }
        line = dis.readLine();
        field = line.substring(12);
        if (field.equals("null")) {
            setSender("");
        } else {
            setSender(field);
        }
        line = dis.readLine();
        field = line.substring(10);
        if (field.equals("null")) {
            setRecipient("");
        } else {
            setRecipient(field);
        }
        line = dis.readLine();
        field = line.substring(10);
        if (field.equals("null")) {
            setCopiesTo("");
        } else {
            setCopiesTo(field);
        }
        line = dis.readLine();
        field = line.substring(11);
        if (field.equals("null")) {
            setBlindCopiesTo("");
        } else {
            setBlindCopiesTo(field);
        }
        line = dis.readLine();
        field = line.substring(15);
        if (field.equals("null")) {
            setSubject("");
        } else {
            setSubject(field);
        }
        line = dis.readLine();
        field = line.substring(18);
        if (field.equals("null")) {
            setID("");
        } else {
            setID(field);
        }
        setBodyLocation(findHeaderEnd(getText()));
        line = dis.readLine();
        readOnly = Str.strToBoolean(line.substring(22));
        line = dis.readLine();
        field = line.substring(19);
        if (field.equals("null")) {
            setFromFolder(null);
        } else {
            setFromFolder(new FileName(field));
        }
        line = dis.readLine();
        field = line.substring(18);
        if (field.equals("null")) {
            setToFolders(new StringVector(3));
        } else {
            setToFolders(Str.splitTextBySpace(field));
        }
        line = dis.readLine();
        delete = Str.strToBoolean(line.substring(21));
        line = dis.readLine();
        damaged = Str.strToBoolean(line.substring(20));

    }

    public String toString() {
        return "triana.types.MailMsg{" +
                "date='" + date + "'\n" +
                ", sender='" + sender + "'\n" +
                ", recipient='" + recipient + "'\n" +
                ", copiesTo='" + copiesTo + "'\n" +
                ", blindCopiesTo='" + blindCopiesTo + "'\n" +
                ", subject='" + subject + "'\n" +
                ", ID='" + ID + "'" +
                ", body=" + body +
                ", readOnly=" + readOnly +
                ", fromFolder=" + fromFolder +
                ", toFolders=" + (toFolders == null ? null : "size:" + toFolders.size() + toFolders) +
                ", delete=" + delete +
                ", damaged=" + damaged +
                ", eol='" + eol + "'" +
                ", umlaut='" + umlaut + "'\n" +
                ", content='" + this.getBody() + "'\n" +
                "}";
    }


}
