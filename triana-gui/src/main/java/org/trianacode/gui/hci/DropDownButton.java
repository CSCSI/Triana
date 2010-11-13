package org.trianacode.gui.hci;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 12, 2010
 */
public class DropDownButton extends JButton {

    public static final String PROP_DROP_DOWN_MENU = "prop.drop.down.menu";

    private boolean mouseInButton = false;
    private boolean mouseInArrowArea = false;

    private PopupMenuListener menuListener;

    /**
     * Creates a new instance of MenuToggleButton
     */
    public DropDownButton(Icon icon, JPopupMenu popup) {
        super(icon);

        putClientProperty(PROP_DROP_DOWN_MENU, popup);


        addMouseListener(new MouseAdapter() {
            private boolean popupMenuOperation = false;

            public void mousePressed(MouseEvent e) {
                popupMenuOperation = false;
                JPopupMenu menu = getPopupMenu();
                if (menu != null && getModel() instanceof Model) {
                    Model model = (Model) getModel();
                    if (!model._isPressed()) {

                        model._press();
                        menu
                                .addPopupMenuListener(getMenuListener());
                        menu.show(DropDownButton.this, 0,
                                getHeight());
                        popupMenuOperation = true;

                    } else {
                        model._release();
                        menu.removePopupMenuListener(getMenuListener());
                        popupMenuOperation = true;
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                // If we done something with the popup menu, we should consume
                // the event, otherwise the button's action will be triggered.
                if (popupMenuOperation) {
                    popupMenuOperation = false;
                    e.consume();
                }
            }

            public void mouseEntered(MouseEvent e) {
                mouseInButton = true;
            }

            public void mouseExited(MouseEvent e) {
                mouseInButton = false;
            }
        });
        setModel(new Model());
    }

    private PopupMenuListener getMenuListener() {
        if (null == menuListener) {
            menuListener = new PopupMenuListener() {


                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                }

                public void popupMenuWillBecomeInvisible(
                        PopupMenuEvent e) {
                    // If inside the button let the button's mouse listener
                    // deal with the state. The popup menu will be hidden and
                    // we should not show it again.
                    if (!mouseInButton) {
                        if (getModel() instanceof Model) {
                            ((Model) getModel())._release();
                        }
                        JPopupMenu menu = getPopupMenu();
                        if (null != menu) {
                            menu.removePopupMenuListener(this);
                        }
                    }
                }

                public void popupMenuCanceled(PopupMenuEvent e) {
                }

            };
        }
        return menuListener;
    }


    JPopupMenu getPopupMenu() {
        Object menu = getClientProperty(PROP_DROP_DOWN_MENU);
        if (menu instanceof JPopupMenu) {
            return (JPopupMenu) menu;
        }
        return null;
    }


    private class Model extends DefaultButtonModel {
        private boolean _pressed = false;

        public void setPressed(boolean b) {
            if (mouseInArrowArea || _pressed)
                return;
            super.setPressed(b);
        }

        public void _press() {
            if ((isPressed()) || !isEnabled()) {
                return;
            }

            stateMask |= PRESSED + ARMED;

            fireStateChanged();
            _pressed = true;
        }

        public void _release() {
            _pressed = false;
            mouseInArrowArea = false;
            setArmed(false);
            setPressed(false);
            setRollover(false);
            setSelected(false);
        }

        public boolean _isPressed() {
            return _pressed;
        }

        protected void fireStateChanged() {
            if (_pressed)
                return;
            super.fireStateChanged();
        }

        @Override
        public void setArmed(boolean b) {
            if (_pressed)
                return;
            super.setArmed(b);
        }

        @Override
        public void setEnabled(boolean b) {
            if (_pressed)
                return;
            super.setEnabled(b);
        }

        @Override
        public void setSelected(boolean b) {
            if (_pressed)
                return;
            super.setSelected(b);
        }

        @Override
        public void setRollover(boolean b) {
            if (_pressed)
                return;
            super.setRollover(b);

        }

    }

}

