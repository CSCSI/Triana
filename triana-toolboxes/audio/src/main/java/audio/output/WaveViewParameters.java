package audio.output;

import org.trianacode.gui.panels.UnitPanel;
import org.trianacode.taskgraph.Unit;


/**
 * A WaveViewParameters UnitPanel to ..
 *
 * @author ian
 * @version 2.0 31 Dec 2000
 * @see UnitPanel
 */
public class WaveViewParameters extends UnitPanel {

    /**
     * Creates a new WaveViewParameters.
     */
    public WaveViewParameters() {
        super();
    }

    /**
     * Sets the triana unit for WaveView.
     */
    public void setObject(Unit unit) {
        super.setObject(unit);

        layoutPanel();
    }

    /**
     * Function to do the layout of the panel.
     */
    public void layoutPanel() {

    }
}





