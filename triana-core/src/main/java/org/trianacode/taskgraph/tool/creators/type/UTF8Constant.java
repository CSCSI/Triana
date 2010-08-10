/*
 * Copyright 2004 - 2005 University of Cardiff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.trianacode.taskgraph.tool.creators.type;

import java.util.ArrayList;

public class UTF8Constant {
    private String str;
    private final int index;
    private boolean unknownEntry = true;
    private String[] classes;

    public UTF8Constant(int index, String str) {
        this.str = str;
        this.index = index;
        unknownEntry = testString(str);
        classes = getStrings();
    }

    public int getIndex() {
        return index;
    }

    public boolean isUnknownEntry() {
        return unknownEntry;
    }

    public String[] getClasses() {
        return classes;
    }

    private boolean testString(String str) {
        if (str.startsWith("[") || str.startsWith("(")) {
            return false;
        }
        if (str.startsWith("L") && str.endsWith(";")) {
            return false;
        }
        return true;
    }

    private String[] getStrings() {
        if (unknownEntry) {
            return new String[]{str};
        }
        int open = str.indexOf("(");
        int close = str.indexOf(")");
        if (open == 0 && close > open) { // method
            ArrayList<String> list = new ArrayList<String>();
            String paramString = str.substring(open + 1, close);
            if (paramString.length() > 0) {
                String[] params = parseParams(paramString);
                for (int i = 0; i < params.length; i++) {
                    list.add(params[i]);
                }
            }
            String output = str.substring(close + 1);
            if (!(output.length() == 1 && "BCDFIJSZ".indexOf(output) > -1)) {
                output = trimString(output);
                if (output != null) {
                    list.add(output);
                }
            }
            return list.toArray(new String[list.size()]);
        } else {
            str = trimString(str);
            if (str != null) {
                return new String[]{str};
            } else {
                return new String[0];
            }
        }
    }


    private String[] parseParams(String params) {
        ArrayList<String> list = new ArrayList<String>();
        int pointer = 0;
        int offset = params.length();
        while (pointer < params.length()) {
            if (params.charAt(pointer) == 'L') {
                if (params.indexOf(";", pointer) != -1) {
                    offset = params.indexOf(";", pointer);
                }
                String s = params.substring(pointer + 1, offset);
                if (isValid(s)) {
                    list.add(s);
                }
                pointer = offset + 1;
            } else {
                pointer++;
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /*
    * gets rid of array symbols, 'L' and semicolon.
    * If the class is a primitive the method returns null
    */
    private String trimString(String str) {
        int pointer = 0;
        int offset = str.length();
        while (pointer < str.length()) {
            if (str.charAt(pointer) == 'L') {
                if (str.indexOf(";", pointer) != -1) {
                    offset = str.indexOf(";", pointer);
                    pointer++;
                }
                String s = str.substring(pointer, offset);
                if (isValid(s)) {
                    return s;
                } else {
                    return null;
                }
            }
            pointer++;
        }
        return null;
    }

    private boolean isValid(String s) {

        if (isJVMCap(s)) {
            return false;
        }
        boolean valid = true;
        boolean firstCharacter = true;
        for (int i = 0, n = s.length(); valid && i < n; i++) {
            char c = s.charAt(i);
            if (firstCharacter) {
                firstCharacter = false;
                valid = Character.isJavaIdentifierStart(c);
            } else {
                if (c == '/') {
                    firstCharacter = true;
                } else {
                    valid = Character.isJavaIdentifierPart(c);
                }
            }
        }
        return valid && !firstCharacter;
    }

    private boolean isJVMCap(String s) {

        if (s == null || s.length() != 1) {
            return false;
        }
        char c = s.charAt(0);
        return c == 'L' ||
                c == 'B' ||
                c == 'C' ||
                c == 'D' ||
                c == 'F' ||
                c == 'I' ||
                c == 'J' ||
                c == 'S' ||
                c == 'Z';
    }
}
