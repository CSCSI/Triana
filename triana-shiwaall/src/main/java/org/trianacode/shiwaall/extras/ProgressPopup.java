package org.trianacode.shiwaall.extras;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// TODO: Auto-generated Javadoc
/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Jan 12, 2011
 * Time: 4:27:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressPopup extends JDialog {
    
    /** The text area. */
    JTextArea textArea;
    
    /** The bar. */
    JProgressBar bar;
    
    /** The bit size. */
    int bitSize;
    
    /** The progress. */
    int progress = 0;
    
    /** The done. */
    boolean done = false;

    /**
     * Instantiates a new progress popup.
     *
     * @param title the title
     * @param numberOfTasks the number of tasks
     */
    public ProgressPopup(String title, int numberOfTasks) {

        Window parentWindow = this.getOwner();
        if (parentWindow != null) {
            this.setLocationRelativeTo(parentWindow);

            bitSize = (int) Math.ceil(100 / numberOfTasks);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            textArea = new JTextArea(15, 40);
            textArea.setLineWrap(true);
            JScrollPane scroll = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            DefaultCaret c = (DefaultCaret) textArea.getCaret();
            c.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            bar = new JProgressBar(0, 100);
            bar.setSize(50, 200);

            JButton close = new JButton("Close");
            close.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    done = true;
                    finish();
                }
            });

            mainPanel.add(bar, BorderLayout.NORTH);
            mainPanel.add(scroll, BorderLayout.CENTER);
            mainPanel.add(close, BorderLayout.SOUTH);

            addTextNoProgress("Begin");

            mainPanel.setSize(200, 100);
            this.add(mainPanel);
            this.setTitle(title);
            this.pack();
            this.setVisible(true);
        }
    }

    /**
     * Sets the unsure time.
     */
    public void setUnsureTime() {
        bar.setIndeterminate(true);
    }

    /**
     * Adds the text.
     *
     * @param text the text
     */
    public void addText(String text) {
        textArea.append(text + "\n");
        addBit();
    }

    /**
     * Adds the text no progress.
     *
     * @param text the text
     */
    public void addTextNoProgress(String text) {
        textArea.append(text + "\n");
    }

    /**
     * Adds the bit.
     */
    private void addBit() {
        bar.setIndeterminate(false);
        progress += bitSize;
        bar.setValue(progress);
        if (progress == 100) {
            this.dispose();
        }
    }

    /**
     * Finish.
     */
    public void finish() {
        bar.setValue(100);
        int wait = 0;
        while (!done && wait < 30) {
            wait++;
            addText("Done. Closing in " + (30 - wait) + " seconds.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        this.dispose();
    }
}
