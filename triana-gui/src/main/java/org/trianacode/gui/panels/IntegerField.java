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
public class IntegerField extends JTextField {

    private int maxLength = 100;

    public IntegerField() {
        this(25, 500);
    }

    public IntegerField(int size) {
        this(size, 500);
    }


    public IntegerField(int size, int maxLength) {
        super(size);
        this.maxLength = maxLength;
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

            final char[] chars = s.toCharArray();
            for (int x = 0; x < chars.length; x++) {
                if (!Character.isDigit(chars[x])) {
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
