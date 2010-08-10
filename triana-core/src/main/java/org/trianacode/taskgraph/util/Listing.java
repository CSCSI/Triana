/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.taskgraph.util;

import java.io.File;
import java.util.Vector;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 */

public class Listing {
    public static int DIR = 1;
    public static int FILE = 2;

    Vector listing = null;

    String matchMemory = null;
    Vector lastMatch = null;

    /**
     * creates a listing with a 10 elements
     */
    public Listing() {
        listing = new Vector(10);
    }

    /**
     * creates a listing with a specified number of elements.
     */
    public Listing(int i) {
        listing = new Vector(i);
    }


    /**
     * @return the number of files/directories in this lsiting.
     */
    public int size() {
        return listing.size();
    }

    /**
     * adds an element to the list.
     */
    public void addElement(Object fileOrListing) {
        listing.addElement(fileOrListing);
    }


    /**
     * A listing with all of the files removed, just the directory structure remains. Use one of the toString methods to
     * have formatted output.
     */
    public Listing justDirStructure() {
        return recurseAndEdit(DIR, null);
    }

    /**
     * A listing with all of the directories removed.
     */
    public Listing justFileList() {
        return recurseAndEdit(FILE, null);
    }

    /**
     * A listing with all of the files removed, just the directory structure remains. Use one of the toString methods to
     * have formatted output.
     */
    public Listing justDirStructure(String search) {
        return recurseAndEdit(DIR, search);
    }

    /**
     * A listing with all of the directories removed.
     */
    public Listing justFileList(String search) {
        return recurseAndEdit(FILE, search);
    }

    /**
     * A listing with all of the directories removed.
     */
    public Listing pickOutMatching(String search) {
        return recurseAndEdit(0, search);
    }


    /**
     * Finds the first available file i.e. non Listing = directory in this Listing. Recursion again!
     */
    public String findAFile() {
        String s = null;

        for (int i = 0; i < size(); ++i) {
            if (!(elementAt(i) instanceof Listing)) {
                return elementAt(i).toString();
            } else if ((s = ((Listing) elementAt(i)).findAFile()) != null) {
                return s;
            }
        }
        return null;
    }

    /**
     * Recurses this list and returns and editted version i.e. a refined search on specific file name matches or
     * Listings with directory occurances removed.
     */
    public Listing recurseAndEdit(int mode, String s) {
        Object o;
        Listing l = new Listing(10);
        int i;
        String st;

        if (((st = findAFile()) != null) && (st.indexOf("://") != -1)) {
            // for internet stuff
            if ((mode & DIR) > 0) {
                for (i = 0; i < size(); ++i) {
                    o = elementAt(i);
                    if (o instanceof Listing) {
                        l.addElement(((Listing) o).recurseAndEdit(mode, s));
                    } else if (FileUtils.isDirectory(o.toString())) {
                        l.addElement(o);
                    }
                }
            } else if ((mode & FILE) > 0) {
                for (i = 0; i < size(); ++i) {
                    o = elementAt(i);
                    if (o instanceof Listing) {
                        l.addElement(((Listing) o).recurseAndEdit(mode, s));
                    } else if (!FileUtils.isDirectory(o.toString())) {
                        l.addElement(o);
                    }
                }
            } else {
                for (i = 0; i < size(); ++i) {
                    o = elementAt(i);
                    if (o instanceof Listing) {
                        l.addElement(((Listing) o).recurseAndEdit(mode, s));
                    } else if (matched(o.toString(), s)) {
                        l.addElement(o);
                    }
                }
            }
            return l;
        }

        if ((mode & DIR) > 0) {
            for (i = 0; i < size(); ++i) {
                o = elementAt(i);
                if ((o instanceof File) && (((File) o).isDirectory())) {
                    l.addElement(o);
                } else if (o instanceof Listing) {
                    l.addElement(((Listing) o).recurseAndEdit(mode, s));
                }
            }
        } else if ((mode & FILE) > 0) {
            for (i = 0; i < size(); ++i) {
                o = elementAt(i);
                if ((o instanceof File) && (((File) o).isFile())) {
                    l.addElement(o);
                } else if (o instanceof Listing) {
                    l.addElement(((Listing) o).recurseAndEdit(mode, s));
                }
            }
        } else if (s != null) {
            for (i = 0; i < size(); ++i) {
                o = elementAt(i);

                if ((o instanceof File) && (matched(((File) o).getName(), s))) {
                    l.addElement(o);
                } else if (o instanceof Listing) {
                    l.addElement(((Listing) o).recurseAndEdit(mode, s));
                }
            }
        }
        return l;
    }

    /**
     * matches the file to the given matching string.  The matching String can contain waitForWilcards etc and this
     * unction will match accordingly.
     */
    public boolean matched(String item, String matchStr) {
        Vector matchList = new Vector(10);
        int j = 0;
        boolean waitForWildcard = false;
        String s;

        if ((matchMemory != null) && (matchMemory.equals(matchStr))) {
            matchList = lastMatch;
        } else {
            for (int i = 0; i < matchStr.length(); ++i) {
                if ((matchStr.substring(i, i + 1).equals("*")) ||
                        (matchStr.substring(i, i + 1).equals("?"))) {
                    if (waitForWildcard) {
                        matchList.addElement(matchStr.substring(j, i));
                        waitForWildcard = false;
                    }
                    matchList.addElement(matchStr.substring(i, i + 1));
                } else {
                    if (!waitForWildcard) {
                        j = i;
                        waitForWildcard = true;
                    }
                }
            }
        }

        if (waitForWildcard) {
            matchList.addElement(matchStr.substring(j));
        }

        j = 0;
        int i = 0;

        if (matchList.size() == 0) {
            return false;
        }

        String st = (String) matchList.elementAt(i);

        do {
            s = (String) matchList.elementAt(i);

            // System.out.println("Matching " + item + " with " + s + " and j = " + j);
            if (s.equals("?")) {
                ++j; // must move at least one
            } else if (!s.equals("*")) {
                int t = j;
                j = item.indexOf(s, j);
                if ((st.equals("?")) && (j != t)) {
                    return false;
                }
                if (j != -1) {
                    j = j + s.length();
                }
            }

            ++i;
            st = s;
        }
        while ((i < matchList.size()) && (j != -1) && (j < item.length()));


        // System.out.println("s = " + s + " and j = " + j + " len = " + item.length());

        if (j == -1) {
            return false;
        } else {
            if (s.equals("*")) {
                return true;
            }

            if ((s.equals("?")) && (j != item.length())) {
                return false;
            }

            if (j == item.length()) {
                return true;
            } else {
                return false;
            }
        }
    }


    /**
     * @return the <i>i</i>th element within the Listing. This could be a File or a Listing.
     */
    public Object elementAt(int i) {
        return listing.elementAt(i);
    }


    /**
     * @return a listing as a set of Strings. If longFormat is true then the format of the listing will be in long
     *         format (i.e. with file details etc).
     */
    public String[] toStrings(boolean longFormat) {
        if (!longFormat) {
            return convertToStrings();
        } else {
            return null;
        }
    }

    /**
     * Recurses through the Listing and returns an array of strings containing each element within the Listing.
     */
    public String[] convertToStrings() {
        Vector filelist = new Vector(10);
        Object el;

        for (int i = 0; i < size(); ++i) {
            el = elementAt(i);
            if ((el instanceof File) || (el instanceof String)) {
                filelist.addElement(el.toString());
            } else { // must be a Listing
                String[] lowerl = ((Listing) el).convertToStrings();
                for (int j = 0; j < lowerl.length; ++j) {
                    filelist.addElement(lowerl[j].toString());
                }
            }
        }

        String[] st = new String[filelist.size()];

        for (int i = 0; i < filelist.size(); ++i) {
            st[i] = (String) filelist.elementAt(i);
        }
        return st;
    }


    /**
     * @return a listing as a string with carriage returns between each item. If longFormat is true then the format of
     *         the listing will be in long format (i.e. with file details etc).
     */
    public String toString(boolean longFormat) {
        String[] s = toStrings(longFormat);
        String ret = "";
        for (int i = 0; i < s.length; ++i) {
            ret = ret + s[i] + "\n";
        }
        return ret;
    }


    /**
     * @return a listing as a strings not in long format.
     */
    public String toString() {
        return toString(false);
    }
}


