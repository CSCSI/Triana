package math.calculator;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */


import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;
import triana.types.util.Str;

/**
 * A MathCalcPanel UnitPanel to provide an interface for MathCalc which is an interface to Compute, written by Bernard
 * F. Schutz
 *
 * @author Ian Taylor
 * @version 1.0 8 Dec 1990
 * @see UnitPanel
 */
public class MathCalcPanel extends UnitPanel implements KeyListener, ItemListener {

    /**
     * A simple textfield to edit mathematical expression
     */
    JTextArea expression;

    /**
     * A checkbox to state whether the user wants a debug window or not.
     */
    JCheckBox debug;

    /**
     * A checkbox to state whether the user wants to optimise or not.
     */
    JCheckBox optimise;

    GridBagLayout gb;
    GridBagConstraints c;


    /**
     * A checkbox to state whether the user wants to show the optimised expression or not
     */
    JCheckBox showOptimisedExpression;

    /**
     * A simple textfield to show the optimised mathematical expression
     */
    JTextArea optimisedExpression;
    JScrollPane scrollText;
    JScrollPane optScrollText;

    JLabel lab1, lab2;

    /**
     * Creates a new MathCalcPanel for Compute.
     */
    public MathCalcPanel() {
        super();
    }

    public void setObject(Unit unit) {
        super.setObject(unit);
        createWidgets();
        layoutPanel();
    }

    public void createWidgets() {
        gb = new GridBagLayout();
        c = new GridBagConstraints();
        setLayout(gb);

        expression = new JTextArea(3, 10);
        expression.setLineWrap(true);
        expression.addKeyListener(this);

        scrollText = new JScrollPane(expression);

        debug = new JCheckBox("Show Debug Panel ? ", false);
        //    debug.addItemListener((ItemListener) getOCLUnit());
        debug.addItemListener(this);

        optimise = new JCheckBox("Optimise ?", true);
        optimise.addItemListener(this);

        showOptimisedExpression =
                new JCheckBox("Show The Optimised Expression ?", false);
        showOptimisedExpression.addItemListener(this);

        optimisedExpression = new JTextArea(3, 10);
        optimisedExpression.setEditable(false);
        optScrollText = new JScrollPane(optimisedExpression);

        lab1 = new JLabel("Type in your mathematical expression", JLabel.CENTER);
        lab2 = new JLabel("Optimised Expression", JLabel.CENTER);
    }

    /**
     * The layout of the RawToGen window.
     */
    public void layoutPanel() {
        removeAll();

        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 0, 0, 0);

        c.gridwidth = GridBagConstraints.RELATIVE;
        gb.setConstraints(optimise, c);
        add(optimise);

        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(debug, c);
        add(debug);

        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(showOptimisedExpression, c);
        add(showOptimisedExpression);

        gb.setConstraints(lab1, c);
        add(lab1);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(scrollText, c);
        add(scrollText);

        if (showOptimisedExpression.isSelected()) {
            gb.setConstraints(lab2, c);
            add(lab2);
            c.fill = GridBagConstraints.BOTH;
            gb.setConstraints(optScrollText, c);
            add(optScrollText);
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        String all = expression.getText();

        if (e.getKeyCode() != KeyEvent.VK_ENTER) {
            return;
        }

        String expressionText;

        if ((expressionText = expression.getText()).equals("")) {
            return;
        }

        // Have to manually insert a newline.
        if (e.isShiftDown()) {
            try {
                int caretPos = expression.getCaretPosition();

                expression.setText(expressionText.substring(0, caretPos) + "\n" +
                        expressionText.substring(caretPos));
                expression.setCaretPosition(caretPos + 1);
            } catch (Exception ex) {
            }
            return;
        }

        // Store the old cursor for later and set the busy/wait cursor
        Cursor oldCursor = getCursor();
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        // strips whitespace from end i.e. get rid of return chars
        expressionText = expressionText.trim();
        expression.setText(expressionText);

        String text = Str.replaceAll(expressionText, "\n", "");

        updateParameter("expression", text);

        setCursor(oldCursor);
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == debug) {
            updateParameter("debug", String.valueOf(debug.isSelected()));
        } else if (e.getSource() == showOptimisedExpression) {
            updateParameter("showOpt", String.valueOf(showOptimisedExpression.isSelected()));
            layoutPanel();
        } else if (e.getSource() == optimise) {
            updateParameter("optimise", String.valueOf(optimise.isSelected()));
        }
    }

    public String getHelpFile() {
        return "Compute.html";
    }
}















