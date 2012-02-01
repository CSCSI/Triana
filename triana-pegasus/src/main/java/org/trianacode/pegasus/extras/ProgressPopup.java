package org.trianacode.pegasus.extras;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: Ian Harvey
 * Date: Jan 12, 2011
 * Time: 4:27:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressPopup extends JDialog {
    JTextArea textArea;
    JProgressBar bar;
    int bitSize;
    int progress = 0;
    boolean done = false;

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

    public void setUnsureTime() {
        bar.setIndeterminate(true);
    }

    public void addText(String text) {
        textArea.append(text + "\n");
        addBit();
    }

    public void addTextNoProgress(String text) {
        textArea.append(text + "\n");
    }

    private void addBit() {
        bar.setIndeterminate(false);
        progress += bitSize;
        bar.setValue(progress);
        if (progress == 100) {
            this.dispose();
        }
    }

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
