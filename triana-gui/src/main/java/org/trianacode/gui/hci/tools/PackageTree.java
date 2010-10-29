package org.trianacode.gui.hci.tools;

import org.trianacode.gui.hci.GUIEnv;
import org.trianacode.taskgraph.tool.Toolbox;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Oct 27, 2010
 */
public class PackageTree extends JDialog implements ActionListener {

    private JTree tree;
    private PackageTreeModel model;
    private JButton okBut;
    private JButton cancelBut;

    private String pkge = null;

    public PackageTree(Toolbox box) {
        super(GUIEnv.getApplicationFrame(), "Choose A Package", true);
        setResizable(false);
        this.model = new PackageTreeModel(box);

        init();
    }

    private void init() {
        this.tree = new JTree(model);
        DefaultTreeCellRenderer renderer =
                new DefaultTreeCellRenderer();
        renderer.setLeafIcon(renderer.getClosedIcon());
        tree.setCellRenderer(renderer);

        JPanel pane = new JPanel(new BorderLayout());
        JScrollPane scroll = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(300, 400));
        pane.add(scroll, BorderLayout.CENTER);
        pane.setBorder(new EmptyBorder(3, 3, 3, 3));
        getContentPane().add(pane, BorderLayout.CENTER);
        JPanel butPane = new JPanel(new BorderLayout());
        okBut = new JButton("OK");
        okBut.addActionListener(this);
        cancelBut = new JButton("Cancel");
        cancelBut.addActionListener(this);
        butPane.add(okBut, BorderLayout.WEST);
        butPane.add(cancelBut, BorderLayout.EAST);
        butPane.setBorder(new EmptyBorder(3, 3, 3, 3));
        getContentPane().add(butPane, BorderLayout.SOUTH);
        pack();

    }

    public String showPackages() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return pkge;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == cancelBut) {
            setVisible(false);
        } else if (src == okBut) {
            TreePath path = tree.getAnchorSelectionPath();
            Object[] comps = path.getPath();
            StringBuilder sb = new StringBuilder();
            int len = comps.length;
            if (len < 2) {
                setVisible(false);
                dispose();
                return;
            }
            for (int i = 1; i < comps.length - 1; i++) {
                Object comp = comps[i];
                sb.append(comp.toString()).append(".");
            }
            sb.append(comps[comps.length - 1]);
            this.pkge = sb.toString();
            setVisible(false);
            dispose();
        }
    }


}
