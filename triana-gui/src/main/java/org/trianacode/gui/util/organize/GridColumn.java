package org.trianacode.gui.util.organize;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Harrison
 * @version 1.0.0 Nov 23, 2010
 */
public class GridColumn {

    private int x;
    private List<Gridbox> boxes = new ArrayList<Gridbox>();
    private double maxHeight = 1;

    public GridColumn(int x) {
        this.x = x;
    }

    public double getColumnWidth() {
        double max = 0;
        if (boxes.size() == 0) {
            return max;
        }
        for (Gridbox box : boxes) {
            if (box.getWidth() > max) {
                max = box.getWidth();
            }
        }
        return 1 + max;
    }

    public int getX() {
        return x;
    }

    public int getLength() {
        return boxes.size();
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public void addBox(int i, Gridbox box) {
        if (i == 0) {
            boxes.add(box);
        }
        if (i >= boxes.size()) {
            for (int j = boxes.size(); j < i; j++) {
                boxes.add(new Gridbox(x, j, null));
            }
        }
        boxes.add(i, box);
        if (box.getHeight() > maxHeight) {
            maxHeight = box.getHeight();
        }

    }

    public Gridbox getBox(int i) {
        if (boxes.size() - 1 >= i) {
            return boxes.get(i);
        }
        return null;

    }
}
