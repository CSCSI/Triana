package org.trianacode.gui.panels;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 28, 2010
 */
public class NormalizedField extends JTextField {

    private int maxLength = 100;
    private char[] allowed = new char[0];
    private boolean toLower = true;

    public NormalizedField() {
        this(25, 500, new char[0], true);
    }

    public NormalizedField(int size) {
        this(size, 500, new char[0], true);
    }

    public NormalizedField(boolean toLower) {
        this(25, 500, new char[0], toLower);
    }

    public NormalizedField(int size, boolean toLower) {
        this(25, 500, new char[0], toLower);
    }

    public NormalizedField(int size, char[] allowed) {
        this(size, 500, allowed, true);
    }

    public NormalizedField(int size, int maxLength, char[] allowed) {
        this(size, maxLength, allowed, true);
    }

    public NormalizedField(int size, int maxLength, char[] allowed, boolean toLower) {
        super(size);
        this.maxLength = maxLength;
        this.allowed = allowed;
        this.toLower = toLower;
    }

    // override this method to return an instance
    // of NormDocument (see below).

    public Document createDefaultModel() {
        return new NormDocument();
    }


    class NormDocument extends PlainDocument {

        // override this method to catch bad input before it gets to the
        // textarea.

        public void insertString(int offset, String s, AttributeSet as)
                throws BadLocationException {
            if (s == null) {
                return;
            }
            if (toLower) {
                s = s.toLowerCase();
            }
            final char[] chars = s.toCharArray();
            for (int x = 0; x < chars.length; x++) {

                final char c = chars[x];
                if ((c >= 'a') && (c <= 'z')) continue; // lowercase
                if (!toLower) {
                    if ((c >= 'A') && (c <= 'Z')) continue; // uppercase
                }
                if ((c >= '0') && (c <= '9')) continue; // numeric
                boolean match = false;
                for (char aChar : allowed) {
                    if (c == aChar) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    return;
                }
            }
            String newContent = getText(0, offset) + s +
                    getText(offset, (getLength() - offset));
            if (newContent.length() > maxLength)
                return;

            super.insertString(offset, s, as);
        }
    }
}
