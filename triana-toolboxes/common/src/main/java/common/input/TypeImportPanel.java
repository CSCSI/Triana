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


import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.gui.util.Env;
import org.trianacode.gui.windows.ErrorDialog;
import org.trianacode.gui.windows.QuestionWindow;
import org.trianacode.taskgraph.Unit;
import org.trianacode.taskgraph.util.FileUtils;
import triana.types.AsciiComm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A TypeImportPanel UnitPanel to import any datatype which supports the AsciiComm type interface.  There is code also
 * to do the binary version but I didn't finish it because I want to use Java's Serializable interface instead as it
 * does the same job!!
 *
 * @author Ian Taylor
 * @version 1.0 alpha 12 May 1997
 * @see UnitPanel
 */
public class TypeImportPanel extends UnitPanel implements ActionListener {

    boolean anAsciiType = false;
    boolean aBinaryType = false;

    public final static int BINARY = 0;
    public final static int ASCII = 1;

    int mode;

    BufferedReader br = null;
    DataInputStream ds = null;

    /**
     * The number of this particular type loaded in.
     */
    JTextField numberLoaded;

    JTextField offset;

    JTextField fileName;

    JLabel lab1, lab2, lab3;

    JButton chooseFile;
    JButton rewind;
    JButton ok;

    boolean beginning = true;

    /**
     * Creates a new TypeImportPanel for TypeImport.
     */
    public TypeImportPanel() {
    }

    public void setObject(Unit unit, int mode) {
        super.setObject(unit);
        this.mode = mode;
        createWidgets();
        layoutPanel();
    }

    public void createWidgets() {
        numberLoaded = new JTextField("0", 10);
        offset = new JTextField("0", 10);
        offset.addActionListener(this);
        fileName = new JTextField(Env.getString("None"), 10);
        fileName.addActionListener(this);

        lab1 = new JLabel(Env.getString("Offset"), JLabel.CENTER);
        lab2 = new JLabel(Env.getString("loadedSoFar"), JLabel.CENTER);
        lab3 = new JLabel(Env.getString("Filename"), JLabel.CENTER);

        rewind = new JButton(Env.getString("Rewind"));
        rewind.addActionListener(this);
        chooseFile = new JButton(Env.getString("Browse"));
        chooseFile.addActionListener(this);
    }


    /**
     * The layout of the RawToGen Panel.
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
        gb.setConstraints(lab1, c);
        add(lab1);

        c.fill = GridBagConstraints.NONE;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(offset, c);
        add(offset);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gb.setConstraints(lab2, c);
        add(lab2);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gb.setConstraints(numberLoaded, c);
        add(numberLoaded);

        gb.setConstraints(lab3, c);
        add(lab3);

        c.fill = GridBagConstraints.BOTH;
        gb.setConstraints(fileName, c);
        add(fileName);

        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 5, 5, 5);

        c.anchor = GridBagConstraints.WEST;
        c.gridwidth = 1;
        gb.setConstraints(chooseFile, c);
        add(chooseFile);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.anchor = GridBagConstraints.CENTER;
        gb.setConstraints(rewind, c);
        add(rewind);
    }

    /**
     * @return an instance of the object which is indicated in the file. i.e.e the type which is being loaded in. If the
     *         type doesn't exist or is not a AsciiComm or aBinaryComm then null is returned. We're just concered with
     *         Ascii for now.
     */
    public Object getType(boolean displayError) {
        String type = null;
        Class c = null;

        try {
            if (mode == BINARY) {
            }
            type = br.readLine();
        }
        catch (IOException ee) {
            return null;
        }

        if (type == null) {
            if (displayError) {
                QuestionWindow con = new QuestionWindow(null,
                        Env.getString("StartFromBeginning"));
                if (con.reply == con.YES) {
                    openFile();
                    return getNextPacket(displayError);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }


        try {
            c = Class.forName(type);
        }
        catch (ClassNotFoundException er) {
            ErrorDialog.show(null, Env.getString("InvalidFormat"));
            return null;
        }

        if (c == null) {
            return null;
        }

        Class interfaces[] = c.getInterfaces();
        anAsciiType = false;
        aBinaryType = false;

        for (int i = 0; i < interfaces.length; ++i) {
            if (interfaces[i].getName().equals("triana.types.AsciiComm")) {
                anAsciiType = true;
            }
        }

        if ((!anAsciiType) && (!aBinaryType)) {
            return null;
        }

        Object obj = null;

        try {
            obj = c.newInstance();
        }
        catch (InstantiationException e) {
            ErrorDialog.show(null, Env.getString("CouldntInstantiate") + " " + e.getMessage());
        }
        catch (IllegalAccessException e) {
            ErrorDialog.show(null, Env.getString("IllegalAccess") + " " + e.getMessage());
        }

        return obj;
    }


    /**
     * @return the next object read from the file.  This is an AsciiComm object
     */
    public Object getNextPacket(boolean displayError) {
        if ((br == null) && (ds == null)) {
            openFile();
            if ((br == null) && (ds == null)) {
                fileName.setText(Env.getString("None"));
                return null;
            }
        }

        Object type = getType(displayError);

        if (type == null) {
            return null;
        }

        if (anAsciiType) {
            if (mode == BINARY) {
                return null;
            }
            try {
                ((AsciiComm) type).inputFromStream(br);
            }
            catch (IOException ee) {
                return null;
            }
            return type;
        }


        if (aBinaryType) {
            //         if (mode == ASCII)
            //             return null;
//          ((BinaryComm)type).inputFromStream(ds);
//          return type;
        }

        return null;
    }

    public String getHelpFile() {
        return "Input.html";
    }

    /**
     * Closes the current file
     */
    public void closeFile() {
        try {
            if (mode == BINARY) {
                if (ds != null) {
                    ds.close();
                }
            } else {
                if (br != null) {
                    br.close();
                }
            }
        }
        catch (IOException o) {
            // no need to say anything!
        }
        br = null;
    }

    public void openFile() {
        closeFile();

        if (fileName.getText().equals(Env.getString("None"))) {
            return;
        }

        numberLoaded.setText("0");

        if (mode == BINARY) {
            try {
                ds = new DataInputStream(new FileInputStream(
                        fileName.getText()));
            }
            catch (IOException o) {
            }
        } else {
            try {
                br = FileUtils.createReader(fileName.getText());
            } catch (IOException except) {
            }
        }

        if ((br == null) && (ds == null)) {
            fileName.setText(Env.getString("None"));
            ErrorDialog.show(null, "No valid file chosen. Try again!");
        }

        updateParameter("file", fileName.getText());
        beginning = true;
    }


    /**
     * Checks for the next, previous and load buttons
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (e.getSource() == fileName) {
            openFile();
        }

        if (e.getSource() == offset) {
            updateParameter("offset", offset.getText());
        }

        if (command.equals(Env.getString("Rewind"))) {
            openFile();
        }

        if (command.equals(Env.getString("Browse"))) {
            /*int r = TFileDialog.showDialog(this, Env.getString("TypeImportDial"),
                                         TFileDialog.LOAD);
            if (r != TFileDialog.APPROVE) return;
            if ( TFileDialog.getPathAndFile() != null)
                fileName.setText( TFileDialog.getPathAndFile());
            openFile();  */
        }
    }
}















