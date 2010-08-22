package common.string;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import org.trianacode.gui.panels.ParameterPanel;
import org.trianacode.gui.panels.TFileChooser;
import org.trianacode.gui.windows.ErrorDialog;


/**
 * $POPUP_DESCRIPTION
 *
 * @author $AUTHOR
 * @version $Revision: 2921 $
 */

public class StringGenPanel extends ParameterPanel implements ActionListener, FocusListener, ItemListener {

    private JTextArea textarea = new JTextArea(10, 30);

    private JMenuItem open = new JMenuItem("Open...");
    private JMenuItem clear = new JMenuItem("Clear");
    private JCheckBoxMenuItem wordwrap = new JCheckBoxMenuItem("Word Wrap", false);


    /**
     * This method is called before the panel is displayed. It should initialise the panel layout.
     */
    public void init() {
        setLayout(new BorderLayout());

        JMenu filemenu = new JMenu("File");
        filemenu.add(open);
        filemenu.add(clear);
        open.addActionListener(this);
        clear.addActionListener(this);

        JMenu viewmenu = new JMenu("View");
        viewmenu.add(wordwrap);
        wordwrap.addItemListener(this);

        JMenuBar menu = new JMenuBar();
        menu.add(filemenu);
        menu.add(viewmenu);

        setMenuBar(menu);

        textarea.setLineWrap(false);
        textarea.setWrapStyleWord(true);
        textarea.addFocusListener(this);
        textarea.setText(getParameter("str").toString());

        JLabel label = new JLabel("Enter String:");
        label.setBorder(new EmptyBorder(0, 0, 3, 0));

        JScrollPane scroll = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel textpanel = new JPanel(new BorderLayout());
        textpanel.add(label, BorderLayout.NORTH);
        textpanel.add(scroll, BorderLayout.CENTER);

        add(textpanel, BorderLayout.CENTER);
    }


    /**
     * This method is called when cancel is clicked on the parameter window. It should synchronize the GUI components
     * with the task parameter values
     */
    public void reset() {
        textarea.setText(getParameter("str").toString());
    }


    /**
     * This method is called when a parameter in the task is updated. It should update the GUI in response to the
     * parameter update
     */
    public void parameterUpdate(String paramname, Object value) {
        if (paramname.equals("str")) {
            textarea.setText(value.toString());
        }
    }


    /**
     * This method is called when the panel is being disposed off. It should clean-up subwindows, open files etc.
     */
    public void dispose() {
        textarea.removeFocusListener(this);
    }


    /**
     * Opens a text file into the string gen
     */
    private void open() {
        TFileChooser chooser = new TFileChooser("Common.String.StringGen");
        chooser.setDialogTitle("Open...");
        chooser.setFileSelectionMode(TFileChooser.FILES_ONLY);

        int result = chooser.showOpenDialog(this);

        if (result == TFileChooser.APPROVE_OPTION) {
            String str = "";
            String line;
            boolean init = true;

            try {
                BufferedReader reader = new BufferedReader(new FileReader(chooser.getSelectedFile()));

                while ((line = reader.readLine()) != null) {
                    if (!init) {
                        str += "\n";
                    } else {
                        init = false;
                    }

                    str += line;
                }
            } catch (IOException except) {
                ErrorDialog.show("Error Reading File: " + chooser.getSelectedFile(), except);
            }

            textarea.setText(str);
            textarea.setCaretPosition(0);
        }
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == open) {
            open();
        } else if (event.getSource() == clear) {
            textarea.setText("");
        }
    }

    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == wordwrap) {
            textarea.setLineWrap(wordwrap.isSelected());
        }
    }


    public void focusGained(FocusEvent event) {
    }

    public void focusLost(FocusEvent event) {
        setParameter("str", textarea.getText());
    }

}
