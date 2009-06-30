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

package org.trianacode.gui.action;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Formats a KeyStroke suitable for display on UI
 *
 * @author  Matthew Shields
 * @created Jun 24, 2003: 3:26:32 PM
 * @version $Revision: 4048 $
 * @date    $Date: 2007-10-08 16:38:22 +0100 (Mon, 08 Oct 2007) $ modified by $Author: spxmss $
 */
public class FormatKeyStroke {
    public static String keyStroke2String(KeyStroke key) {
        StringBuffer s = new StringBuffer(50);
        int m = key.getModifiers();

        if ((m & (InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK)) != 0) {
            s.append("ctrl+");
        }
        if ((m & (InputEvent.META_DOWN_MASK | InputEvent.META_MASK)) != 0) {
            s.append("meta+");
        }
        if ((m & (InputEvent.ALT_DOWN_MASK | InputEvent.ALT_MASK)) != 0) {
            s.append("alt+");
        }
        if ((m & (InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK)) != 0) {
            s.append("shift+");
        }

        s.append(getKeyText(key.getKeyCode()) + " ");
        return s.toString();
    }

    public static String getKeyText(int keyCode) {
        if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9 ||
                keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
            return String.valueOf((char) keyCode);
        }

        switch (keyCode) {
            case KeyEvent.VK_COMMA:
                return ",";
            case KeyEvent.VK_PERIOD:
                return ".";
            case KeyEvent.VK_SLASH:
                return "\\";
            case KeyEvent.VK_SEMICOLON:
                return ";";
            case KeyEvent.VK_EQUALS:
                return "=";
            case KeyEvent.VK_OPEN_BRACKET:
                return "(";
            case KeyEvent.VK_BACK_SLASH:
                return "/";
            case KeyEvent.VK_CLOSE_BRACKET:
                return ")";

            case KeyEvent.VK_ENTER:
                return "ENTER";
            case KeyEvent.VK_BACK_SPACE:
                return "BACK_SPACE";
            case KeyEvent.VK_TAB:
                return "TAB";
            case KeyEvent.VK_CANCEL:
                return "CANCEL";
            case KeyEvent.VK_CLEAR:
                return "CLEAR";
            case KeyEvent.VK_SHIFT:
                return "SHIFT";
            case KeyEvent.VK_CONTROL:
                return "CONTROL";
            case KeyEvent.VK_ALT:
                return "ALT";
            case KeyEvent.VK_PAUSE:
                return "PAUSE";
            case KeyEvent.VK_CAPS_LOCK:
                return "CAPS_LOCK";
            case KeyEvent.VK_ESCAPE:
                return "ESCAPE";
            case KeyEvent.VK_SPACE:
                return "SPACE";
            case KeyEvent.VK_PAGE_UP:
                return "PAGE_UP";
            case KeyEvent.VK_PAGE_DOWN:
                return "PAGE_DOWN";
            case KeyEvent.VK_END:
                return "END";
            case KeyEvent.VK_HOME:
                return "HOME";
            case KeyEvent.VK_LEFT:
                return "LEFT";
            case KeyEvent.VK_UP:
                return "UP";
            case KeyEvent.VK_RIGHT:
                return "RIGHT";
            case KeyEvent.VK_DOWN:
                return "DOWN";

                // numpad numeric keys handled below
            case KeyEvent.VK_MULTIPLY:
                return "MULTIPLY";
            case KeyEvent.VK_ADD:
                return "ADD";
            case KeyEvent.VK_SEPARATOR:
                return "SEPARATOR";
            case KeyEvent.VK_SUBTRACT:
                return "SUBTRACT";
            case KeyEvent.VK_DECIMAL:
                return "DECIMAL";
            case KeyEvent.VK_DIVIDE:
                return "DIVIDE";
            case KeyEvent.VK_DELETE:
                return "DELETE";
            case KeyEvent.VK_NUM_LOCK:
                return "NUM_LOCK";
            case KeyEvent.VK_SCROLL_LOCK:
                return "SCROLL_LOCK";

            case KeyEvent.VK_F1:
                return "F1";
            case KeyEvent.VK_F2:
                return "F2";
            case KeyEvent.VK_F3:
                return "F3";
            case KeyEvent.VK_F4:
                return "F4";
            case KeyEvent.VK_F5:
                return "F5";
            case KeyEvent.VK_F6:
                return "F6";
            case KeyEvent.VK_F7:
                return "F7";
            case KeyEvent.VK_F8:
                return "F8";
            case KeyEvent.VK_F9:
                return "F9";
            case KeyEvent.VK_F10:
                return "F10";
            case KeyEvent.VK_F11:
                return "F11";
            case KeyEvent.VK_F12:
                return "F12";
            case KeyEvent.VK_F13:
                return "F13";
            case KeyEvent.VK_F14:
                return "F14";
            case KeyEvent.VK_F15:
                return "F15";
            case KeyEvent.VK_F16:
                return "F16";
            case KeyEvent.VK_F17:
                return "F17";
            case KeyEvent.VK_F18:
                return "F18";
            case KeyEvent.VK_F19:
                return "F19";
            case KeyEvent.VK_F20:
                return "F20";
            case KeyEvent.VK_F21:
                return "F21";
            case KeyEvent.VK_F22:
                return "F22";
            case KeyEvent.VK_F23:
                return "F23";
            case KeyEvent.VK_F24:
                return "F24";

            case KeyEvent.VK_PRINTSCREEN:
                return "PRINTSCREEN";
            case KeyEvent.VK_INSERT:
                return "INSERT";
            case KeyEvent.VK_HELP:
                return "HELP";
            case KeyEvent.VK_META:
                return "META";
            case KeyEvent.VK_BACK_QUOTE:
                return "BACK_QUOTE";
            case KeyEvent.VK_QUOTE:
                return "QUOTE";

            case KeyEvent.VK_KP_UP:
                return "KP_UP";
            case KeyEvent.VK_KP_DOWN:
                return "KP_DOWN";
            case KeyEvent.VK_KP_LEFT:
                return "KP_LEFT";
            case KeyEvent.VK_KP_RIGHT:
                return "KP_RIGHT";

            case KeyEvent.VK_DEAD_GRAVE:
                return "DEAD_GRAVE";
            case KeyEvent.VK_DEAD_ACUTE:
                return "DEAD_ACUTE";
            case KeyEvent.VK_DEAD_CIRCUMFLEX:
                return "DEAD_CIRCUMFLEX";
            case KeyEvent.VK_DEAD_TILDE:
                return "DEAD_TILDE";
            case KeyEvent.VK_DEAD_MACRON:
                return "DEAD_MACRON";
            case KeyEvent.VK_DEAD_BREVE:
                return "DEAD_BREVE";
            case KeyEvent.VK_DEAD_ABOVEDOT:
                return "DEAD_ABOVEDOT";
            case KeyEvent.VK_DEAD_DIAERESIS:
                return "DEAD_DIAERESIS";
            case KeyEvent.VK_DEAD_ABOVERING:
                return "DEAD_ABOVERING";
            case KeyEvent.VK_DEAD_DOUBLEACUTE:
                return "DEAD_DOUBLEACUTE";
            case KeyEvent.VK_DEAD_CARON:
                return "DEAD_CARON";
            case KeyEvent.VK_DEAD_CEDILLA:
                return "DEAD_CEDILLA";
            case KeyEvent.VK_DEAD_OGONEK:
                return "DEAD_OGONEK";
            case KeyEvent.VK_DEAD_IOTA:
                return "DEAD_IOTA";
            case KeyEvent.VK_DEAD_VOICED_SOUND:
                return "DEAD_VOICED_SOUND";
            case KeyEvent.VK_DEAD_SEMIVOICED_SOUND:
                return "DEAD_SEMIVOICED_SOUND";

            case KeyEvent.VK_AMPERSAND:
                return "AMPERSAND";
            case KeyEvent.VK_ASTERISK:
                return "ASTERISK";
            case KeyEvent.VK_QUOTEDBL:
                return "QUOTEDBL";
            case KeyEvent.VK_LESS:
                return "LESS";
            case KeyEvent.VK_GREATER:
                return "GREATER";
            case KeyEvent.VK_BRACELEFT:
                return "BRACELEFT";
            case KeyEvent.VK_BRACERIGHT:
                return "BRACERIGHT";
            case KeyEvent.VK_AT:
                return "AT";
            case KeyEvent.VK_COLON:
                return "COLON";
            case KeyEvent.VK_CIRCUMFLEX:
                return "CIRCUMFLEX";
            case KeyEvent.VK_DOLLAR:
                return "DOLLAR";
            case KeyEvent.VK_EURO_SIGN:
                return "EURO_SIGN";
            case KeyEvent.VK_EXCLAMATION_MARK:
                return "EXCLAMATION_MARK";
            case KeyEvent.VK_INVERTED_EXCLAMATION_MARK:
                return "INVERTED_EXCLAMATION_MARK";
            case KeyEvent.VK_LEFT_PARENTHESIS:
                return "LEFT_PARENTHESIS";
            case KeyEvent.VK_NUMBER_SIGN:
                return "NUMBER_SIGN";
            case KeyEvent.VK_MINUS:
                return "-";
            case KeyEvent.VK_PLUS:
                return "PLUS";
            case KeyEvent.VK_RIGHT_PARENTHESIS:
                return "RIGHT_PARENTHESIS";
            case KeyEvent.VK_UNDERSCORE:
                return "UNDERSCORE";

            case KeyEvent.VK_FINAL:
                return "FINAL";
            case KeyEvent.VK_CONVERT:
                return "CONVERT";
            case KeyEvent.VK_NONCONVERT:
                return "NONCONVERT";
            case KeyEvent.VK_ACCEPT:
                return "ACCEPT";
            case KeyEvent.VK_MODECHANGE:
                return "MODECHANGE";
            case KeyEvent.VK_KANA:
                return "KANA";
            case KeyEvent.VK_KANJI:
                return "KANJI";
            case KeyEvent.VK_ALPHANUMERIC:
                return "ALPHANUMERIC";
            case KeyEvent.VK_KATAKANA:
                return "KATAKANA";
            case KeyEvent.VK_HIRAGANA:
                return "HIRAGANA";
            case KeyEvent.VK_FULL_WIDTH:
                return "FULL_WIDTH";
            case KeyEvent.VK_HALF_WIDTH:
                return "HALF_WIDTH";
            case KeyEvent.VK_ROMAN_CHARACTERS:
                return "ROMAN_CHARACTERS";
            case KeyEvent.VK_ALL_CANDIDATES:
                return "ALL_CANDIDATES";
            case KeyEvent.VK_PREVIOUS_CANDIDATE:
                return "PREVIOUS_CANDIDATE";
            case KeyEvent.VK_CODE_INPUT:
                return "CODE_INPUT";
            case KeyEvent.VK_JAPANESE_KATAKANA:
                return "JAPANESE_KATAKANA";
            case KeyEvent.VK_JAPANESE_HIRAGANA:
                return "JAPANESE_HIRAGANA";
            case KeyEvent.VK_JAPANESE_ROMAN:
                return "JAPANESE_ROMAN";
            case KeyEvent.VK_KANA_LOCK:
                return "KANA_LOCK";
            case KeyEvent.VK_INPUT_METHOD_ON_OFF:
                return "INPUT_METHOD_ON_OFF";

            case KeyEvent.VK_AGAIN:
                return "AGAIN";
            case KeyEvent.VK_UNDO:
                return "UNDO";
            case KeyEvent.VK_COPY:
                return "COPY";
            case KeyEvent.VK_PASTE:
                return "PASTE";
            case KeyEvent.VK_CUT:
                return "CUT";
            case KeyEvent.VK_FIND:
                return "FIND";
            case KeyEvent.VK_PROPS:
                return "PROPS";
            case KeyEvent.VK_STOP:
                return "STOP";

            case KeyEvent.VK_COMPOSE:
                return "COMPOSE";
            case KeyEvent.VK_ALT_GRAPH:
                return "ALT_GRAPH";
        }

        if (keyCode >= KeyEvent.VK_NUMPAD0 && keyCode <= KeyEvent.VK_NUMPAD9) {
            char c = (char) (keyCode - KeyEvent.VK_NUMPAD0 + '0');
            return "NUMPAD" + c;
        }

        return "unknown(0x" + Integer.toString(keyCode, 16) + ")";
    }

}
