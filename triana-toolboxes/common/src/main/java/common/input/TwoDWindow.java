package common.input;

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


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.trianacode.gui.panels.UnitPanel;
import triana.types.OldUnit;

/**
 * A TwoDWindow UnitWindow to output a 2D type within VectorType.
 *
 * @author Ian Taylor
 * @version 2.0 11 May 2000
 */
public class TwoDWindow extends UnitPanel implements ActionListener, DocumentListener {

    /**
     * A simple textArea to edit the 2-D data
     */
    JTextArea data;

    /**
     * A JTextField for the X JLabel of the 2-D data
     */
    JTextField xlabel;

    /**
     * A JTextField for the Y JLabel of the 2-D data
     */
    JTextField ylabel;

    JLabel lab1, lab2, lab3;

    JButton ok;
    JButton load;

    /**
     * Creates a new VectorTypeWindow for Generate2D.
     */
    public TwoDWindow() {
        super();
    }

    public void setObject(OldUnit unit) {
        super.setObject(unit);
        createWidgets();
        layoutPanel();
    }

    public void createWidgets() {
        data = new JTextArea("", 15, 20);
        data.getDocument().addDocumentListener(this);

        xlabel = new JTextField("X Data", 20);
        xlabel.addActionListener(this);
        ylabel = new JTextField("Y Data", 20);
        ylabel.addActionListener(this);

        lab1 = new JLabel("Enter 2-D Data in the form :- ", JLabel.CENTER);
        lab2 = new JLabel("X Y <return>", JLabel.CENTER);
        lab3 = new JLabel("X Y <return>", JLabel.CENTER);

        load = new JButton("Import .. ");
        load.addActionListener(this);
    }


    /**
     * The layout of the RawToGen window.
     */
    public void layoutPanel() {
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gb);

        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 0, 0, 0);
        c.gridwidth = GridBagConstraints.RELATIVE;

        JLabel xlab = new JLabel("X Label", JLabel.LEFT);
        gb.setConstraints(xlab, c);
        add(xlab);

        c.gridwidth = GridBagConstraints.REMAINDER;

        gb.setConstraints(xlabel, c);
        add(xlabel);

        c.gridwidth = GridBagConstraints.RELATIVE;

        JLabel ylab = new JLabel("Y Label", JLabel.LEFT);
        gb.setConstraints(ylab, c);
        add(ylab);

        c.gridwidth = GridBagConstraints.REMAINDER;

        gb.setConstraints(ylabel, c);
        add(ylabel);

        gb.setConstraints(lab1, c);
        add(lab1);

        c.insets = new Insets(0, 0, 0, 0); // clump writing together

        gb.setConstraints(lab2, c);
        add(lab2);

        gb.setConstraints(lab3, c);
        add(lab3);

        c.insets = new Insets(10, 0, 0, 0);

        JScrollPane jp = new JScrollPane(data);
        gb.setConstraints(jp, c);
        add(jp);

        c.insets = new Insets(5, 0, 5, 0);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(load, c);
        add(load);
    }

    public String getHelpFile() {
        return "Generate2D.html";
    }

    public void insertUpdate(DocumentEvent d) {
        updateParameter("data", data.getText());
    }

    public void removeUpdate(DocumentEvent d) {
        updateParameter("data", data.getText());
    }

    public void changedUpdate(DocumentEvent d) {
    }

    /**
     * Checks for the OK button
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (e.getSource() == xlabel) {
            updateParameter("xlabel", xlabel.getText());
        } else if (e.getSource() == ylabel) {
            updateParameter("ylabel", ylabel.getText());
        } else if (command.equals("OK")) {
            setVisible(false);
        } else if (e.getSource() == load) {
/*            TFileDialog.showDialog(this,
                    "Choose a File To Import the Data From",
                                         TFileDialog.LOAD);
            if (TFileDialog.getPathAndFile() != null) {
                String inp = FileUtils.readFile(TFileDialog.getPathAndFile());
                
                if (inp.length() < 32000)
                    data.setText(inp);
                else
                    data.setText("Loaded OK but data too Large to \ndisplay in text area!");
                updateParameter("data", inp);
                }                            */
        }
    }

}



