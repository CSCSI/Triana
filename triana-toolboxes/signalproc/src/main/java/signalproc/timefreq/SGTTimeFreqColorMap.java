package signalproc.timefreq;

import java.awt.Color;

import gov.noaa.pmel.sgt.ColorMap;
import gov.noaa.pmel.util.Range2D;
import triana.types.ImageMap;
import triana.types.image.PixelMap;

public class SGTTimeFreqColorMap extends ColorMap {

    ImageMap map;
    double[] data;

    public SGTTimeFreqColorMap(ImageMap map, double[] data) {
        this.map = map;
        this.data = data;
    }

    public ColorMap copy() {
        return new SGTTimeFreqColorMap((ImageMap) map.copyMe(), data);
    }

    public boolean equals(ColorMap cm) {
        if (this.equals(cm)) {
            return true;
        } else {
            return false;
        }
    }

    public Color getColor(double val) {
        int tmc = map.colorOf(val);
        PixelMap pm = map.getPixelMap();
        int rgb = pm.rgbOfTrianaColorMapColor(tmc);
        return new Color(rgb);
    }

    public Range2D getRange() {
        double low = Double.POSITIVE_INFINITY;
        double high = Double.NEGATIVE_INFINITY;


        for (int i = 0; i < data.length; i++) {

            low = Math.min(low, data[i]);
            high = Math.max(high, data[i]);

        }

        //System.out.println("low : "+low);
        //System.out.println("high : "+high);

        //low = Math.floor(low);
        //high = Math.ceil(high);

        //System.out.println("low : "+low);
        //System.out.println("high : "+high);

        //get the current user Range2D for the Transforms or ContourLevel
        //get the data
        //calculate the highest number and lowest number
        //round down the lowest
        //round up the highest
        return new Range2D(low, high);
    }
}
