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
package org.trianacode.gui.action.files;

import org.trianacode.gui.action.ActionDisplayOptions;
import org.trianacode.gui.desktop.DesktopView;
import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.gui.main.TaskGraphPanel;
import org.trianacode.gui.panels.IntegerField;
import org.trianacode.gui.panels.LabelledTextFieldPanel;
import org.trianacode.gui.panels.OptionPane;
import org.trianacode.gui.panels.TFileChooser;
import org.trianacode.taskgraph.TaskGraph;
import org.trianacode.util.TaskGraphPanelUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Action to handle workflow/taskgraph Exporting.
 *
 * @author Matthew Shields
 * @version $Revision: 4048 $
 */
public class ImageAction extends AbstractAction implements ActionDisplayOptions {

    private static int margin = 20;


    public ImageAction() {

        putValue(SHORT_DESCRIPTION, "Create Image");
        putValue(ACTION_COMMAND_KEY, "Create Image");
        putValue(SMALL_ICON, GUIEnv.getIcon("pic.png"));
        putValue(NAME, "Create Image");
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent event) {
        TaskGraphPanel taskgraphpanel = null;
        Component comp = null;
        DesktopView view = GUIEnv.getSelectedDesktopView();
        if (view != null) {
            taskgraphpanel = view.getTaskgraphPanel();
            if (taskgraphpanel != null) {
                TaskGraph tg = taskgraphpanel.getTaskGraph();
                if (tg == null || tg.getTasks(false).length == 0) {
                    JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                            "No Task Graph to Create an Image From", "Create Image", JOptionPane.ERROR_MESSAGE,
                            GUIEnv.getTrianaIcon());
                    return;
                }
                comp = taskgraphpanel.getContainer();
            } else {
                JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                        "Error: No Panel", "Create Image", JOptionPane.ERROR_MESSAGE,
                        GUIEnv.getTrianaIcon());
                return;
            }
        } else {
            JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                    "Error: No View", "Create Image", JOptionPane.ERROR_MESSAGE,
                    GUIEnv.getTrianaIcon());
            return;
        }
        if (comp != null && comp.isVisible()) {
            Dimension size = comp.getSize();
            BufferedImage image = new BufferedImage(size.width, size.height,
                    BufferedImage.TYPE_INT_RGB);
            Rectangle2D rect = TaskGraphPanelUtils.getBoundingBox(taskgraphpanel);
            ImagePanel p = new ImagePanel(comp, rect, image);
        } else {
            JOptionPane.showMessageDialog(GUIEnv.getApplicationFrame(),
                    "Error: No Visible Panel", "Create Image", JOptionPane.ERROR_MESSAGE,
                    GUIEnv.getTrianaIcon());
        }
    }


    public static void save(File output, double scale, String type) {
        TaskGraphPanel taskgraphpanel = null;
        Component comp = null;
        DesktopView view = GUIEnv.getSelectedDesktopView();
        if (view != null) {
            taskgraphpanel = view.getTaskgraphPanel();
            if (taskgraphpanel != null) {
                TaskGraph tg = taskgraphpanel.getTaskGraph();
                if (tg == null || tg.getTasks(false).length == 0) {
                    System.out.println("No taskgraph found.");
                    return;
                }
                comp = taskgraphpanel.getContainer();
            } else {
                System.out.println("No taskgraph panel found.");
                return;
            }
        } else {
            System.out.println("no taskgraph view found.");
            return;
        }
        if (comp != null && comp.isVisible()) {
            Dimension size = comp.getSize();
            BufferedImage image = new BufferedImage(size.width, size.height,
                    BufferedImage.TYPE_INT_RGB);
            Rectangle2D rect = TaskGraphPanelUtils.getBoundingBox(taskgraphpanel);
            save(output, comp, scale, rect, image, type, false);
        } else {
            System.out.println("no Component found.");
        }
    }

    private static void save(File f, Component comp, double scale, Rectangle2D rect, BufferedImage image, String type, boolean display) {
        if (scale == 0.0) {
            scale = 0.1;
        }
        String nm = f.getName();
        if (nm.indexOf(".") > -1) {
            nm = nm.substring(0, nm.indexOf(".") + 1) + type;
        } else {
            nm += ("." + type);
        }
        File out = new File(f.getParentFile(), nm);
        if (out.exists()) {
            int choice = JOptionPane.OK_OPTION;
            if (display) {
                choice = JOptionPane.showConfirmDialog(GUIEnv.getApplicationFrame(),
                        "Over write existing file '" + nm + "'?", "Over Write", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, GUIEnv.getTrianaIcon());
            }
            if (choice == JOptionPane.CANCEL_OPTION) {
                return;
            } else {
                String path = out.getAbsolutePath();
                out.delete();
                out = new File(path);
            }

        }
        comp.paint(image.getGraphics());
        AffineTransform tx = new AffineTransform();
        tx.scale(scale, scale);
        tx.translate(-(rect.getX() - (margin)), -(rect.getY() - (margin)));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage dest = new BufferedImage((int) ((rect.getWidth()) * scale) + (margin * 2), (int) ((rect.getHeight()) * scale) + (margin * 2),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = dest.getGraphics();
        Color c = g.getColor();
        g.setColor(comp.getBackground());
        g.fillRect(0, 0, dest.getWidth(), dest.getHeight());
        g.setColor(c);
        image = op.filter(image, dest);

        Iterator iter = ImageIO.getImageWritersByFormatName(type);
        if (iter == null || !iter.hasNext()) {
            if (display) {
                OptionPane.showError("No Image writers availabale for " + type, "Error", GUIEnv.getApplicationFrame());
            } else {
                System.out.println("ImageAction.save: No Image writers could be found for:" + type);
            }
        }
        ImageWriter writer = (ImageWriter) iter.next();

        ImageWriteParam iwp = writer.getDefaultWriteParam();
        if (writer.canWriteRasters()) {
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(1);
        }

        try {
            FileImageOutputStream fout = new FileImageOutputStream(out);
            writer.setOutput(fout);
            IIOImage im = new IIOImage(image, null, null);
            writer.write(null, im, iwp);
            writer.dispose();
            fout.flush();
            fout.close();
        } catch (IOException e) {
            if (display) {
                OptionPane.showError(e.getMessage(), "Error", GUIEnv.getApplicationFrame());
            } else {
                e.printStackTrace();
            }
        }
    }


    private class ImagePanel extends JDialog implements ChangeListener, ActionListener {

        private JRadioButton jpg = new JRadioButton("JPG (Better for scaled images)");
        private JRadioButton png = new JRadioButton("PNG (Better for unscaled images)");
        private JTextField widthField = new IntegerField();
        private JTextField heightField = new IntegerField();
        private JButton saveBut = new JButton("Save As");
        private Rectangle2D rect;
        private BufferedImage image;
        private int width;
        private int height;
        private double scale = 1.0;
        private Component comp;


        public ImagePanel(Component comp, Rectangle2D rect, BufferedImage image) {
            super(GUIEnv.getApplicationFrame(), "Create Image", true);
            setResizable(false);
            jpg.setSelected(false);
            png.setSelected(true);
            this.comp = comp;
            this.rect = rect;
            this.image = image;
            this.width = (int) rect.getWidth();
            this.height = (int) rect.getHeight();
            widthField.setText(width + "");
            heightField.setText(height + "");
            widthField.setEnabled(false);
            heightField.setEnabled(false);
            init();
        }

        /**
         * This method is called when the task is set for this panel. It is overridden to create the panel layout.
         */
        public void init() {
            JPanel main = new JPanel();
            main.setLayout(new BorderLayout());
            main.setBorder(new EmptyBorder(3, 3, 3, 3));

            JPanel radiopanel = new JPanel(new GridLayout(2, 1));
            radiopanel.setBorder(new EmptyBorder(3, 3, 3, 3));

            radiopanel.add(jpg);
            radiopanel.add(png);

            ButtonGroup group = new ButtonGroup();
            group.add(jpg);
            group.add(png);
            JPanel dims = new LabelledTextFieldPanel(new String[]{"Width", "Height"}, new JTextField[]{widthField, heightField});
            JSlider scale = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
            scale.setMajorTickSpacing(10);
            scale.setMinorTickSpacing(1);
            scale.setPaintTicks(true);
            scale.setPaintLabels(true);
            scale.addChangeListener(this);
            JPanel scalePanel = new JPanel();
            scalePanel.setLayout(new BoxLayout(scalePanel, BoxLayout.Y_AXIS));
            scalePanel.add(new JLabel("Scaling Percentage"));
            scalePanel.add(scale);
            JPanel sliderPane = new JPanel(new BorderLayout());
            sliderPane.add(scalePanel, BorderLayout.NORTH);
            sliderPane.add(dims, BorderLayout.CENTER);
            JPanel butPane = new JPanel();
            butPane.setLayout(new BoxLayout(butPane, BoxLayout.X_AXIS));
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    setVisible(false);
                    dispose();
                }
            });
            butPane.add(cancel);
            saveBut.addActionListener(this);
            butPane.add(Box.createHorizontalGlue());
            butPane.add(saveBut);
            sliderPane.setBorder(new EmptyBorder(3, 3, 3, 3));

            sliderPane.add(butPane, BorderLayout.SOUTH);
            main.add(radiopanel, BorderLayout.CENTER);
            main.add(sliderPane, BorderLayout.SOUTH);
            getContentPane().add(main);
            pack();
            setLocationRelativeTo(GUIEnv.getApplicationFrame());
            setVisible(true);

        }

        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int i = source.getValue();
                this.scale = (double) i / 100;
                widthField.setText(((int) (width * scale)) + "");
                heightField.setText(((int) (height * scale)) + "");

            }
        }

        private String getType() {
            if (this.jpg.isSelected()) {
                return "jpg";
            }
            return "png";
        }

        private double getScale() {
            return scale;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            TFileChooser chooser = new TFileChooser(System.getProperty("user.dir"));
            chooser.setDialogTitle("Save");
            chooser.setMultiSelectionEnabled(false);

            int result = chooser.showSaveDialog(GUIEnv.getApplicationFrame());

            if (result == TFileChooser.APPROVE_OPTION) {
                final File f = chooser.getSelectedFile();
                if (f != null) {
                    Thread thread = new Thread() {
                        public void run() {
                            save(f, comp, getScale(), rect, image, getType(), true);
                            setVisible(false);
                            dispose();
                        }
                    };

                    thread.setName("ImageActionThread");
                    thread.setPriority(Thread.NORM_PRIORITY);
                    thread.start();
                }
            }

        }
    }

}
