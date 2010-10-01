package common.parameter;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JColorChooser;
import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.gui.windows.WindowButtonConstants;

/**
 * A ColorGenWindow UnitPanel to allow the editing of a color
 *
 * @author ian
 * @version 1.0 Final 15 Aug 2000
 * @see UnitPanel
 */
public class ColorGenWindow extends UnitPanel {

    JColorChooser colorChoice;
    public Color currentColour;


    /**
     * @return false so that parameter changes are not committed automatically
     */
    public boolean isAutoCommitByDefault() {
        return false;
    }

    /**
     * Overrides UnitPanel method to return WindowConstans.OK_CANCEl_APPLY_BUTTONS.
     */
    public byte getPreferredButtons() {
        return WindowButtonConstants.OK_CANCEL_APPLY_BUTTONS;
    }


    /**
     * Sets the unit to for ColorGen.
     */
    public void setObject(Object unit) {
        super.setObject(unit);

        colorChoice = new JColorChooser();

        layoutPanel();
        reset();
    }

    public void setColor(Color color) {
        colorChoice.setColor(color);
    }

    /**
     * Function to do the layout of the panel.
     */
    public void layoutPanel() {
        setLayout(new BorderLayout());
        add(colorChoice, BorderLayout.CENTER);
    }

    /**
     * Called when the ok button is clicked on the parameter window. Commits any parameter changes.
     */
    public void okClicked() {
        setParameter("color", ColorGen.toString(colorChoice.getColor()));
        super.okClicked();
    }

    /**
     * Called when the apply button is clicked on the parameter window. Commits any parameter changes.
     */
    public void applyClicked() {
        setParameter("color", ColorGen.toString(colorChoice.getColor()));
        super.applyClicked();
    }

    /**
     * Resets the components in the panel to those specified by the task.
     */
    public void reset() {
        super.reset();

        if ((getTask() != null) && (getTask().isParameterName("color"))) {
            setColor(ColorGen.strToColor((String) getTask().getParameter("color")));
        }

    }

}





