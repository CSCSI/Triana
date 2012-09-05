package org.trianacode.shiwaall.test;

import org.trianacode.gui.panels.ParameterPanel;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

// TODO: Auto-generated Javadoc
/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 31/07/2012
 * Time: 17:02
 * To change this template use File | Settings | File Templates.
 */
public class InOutPanel extends ParameterPanel implements DropTargetListener, AWTEventListener {

    /** The x. */
    int x = 0;
    
    /** The y. */
    int y = 0;

    /** The toolkit. */
    Toolkit toolkit = Toolkit.getDefaultToolkit();

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#init()
     */
    @Override
    public void init() {
        setLayout(new BorderLayout());
//        this.addMouseMotionListener(this);
//        this.addMouseListener(this);
        toolkit.addAWTEventListener(this, AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK);
        new DropTarget(this, this);
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        graphics.draw3DRect(x, y, x + 20, y + 20, true);
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#reset()
     */
    @Override
    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see org.trianacode.gui.panels.ParameterPanel#dispose()
     */
    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see java.awt.event.AWTEventListener#eventDispatched(java.awt.AWTEvent)
     */
    @Override
    public void eventDispatched(AWTEvent event) {

        if(event instanceof MouseEvent){

            if(((MouseEvent) event).getComponent() == this){
                System.out.println(event.getID() + event.paramString());

                x = ((MouseEvent) event).getX();
                y = ((MouseEvent) event).getY();

                repaint();
            }

        }
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        System.out.printf("Drag enter");
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        System.out.printf("Drag over");
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.DropTargetDragEvent)
     */
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
     */
    @Override
    public void dragExit(DropTargetEvent dte) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
     */
    @Override
    public void drop(DropTargetDropEvent dtde) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
