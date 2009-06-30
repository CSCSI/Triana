/*
 * Copyright 2004 - 2009 University of Cardiff.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trianacode.util;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Class Description Here...
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Jun 25, 2009: 5:11:26 PM
 * @date $Date:$ modified by $Author:$
 */

public class PrintUtilities implements Printable {

    private Component componentToBePrinted;
    private boolean scale;
    private boolean white;

    public static void printComponent(final Component comp, final boolean scale, final boolean white) {
        Thread thread = new Thread() {
            public void run() {
                new PrintUtilities(comp, scale, white).print();
            }
        };

        thread.setName("PrintThread");
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
    }

    public PrintUtilities(Component componentToBePrinted, boolean scale, boolean white) {
        this.componentToBePrinted = componentToBePrinted;
        this.scale = scale;
        this.white = white;
    }

    public void print() {
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);

        if (printJob.printDialog(aset))
            try {
                printJob.print();
            } catch (PrinterException pe) {
                System.out.println("Error printing: " + pe);
            }
    }

    public int print(Graphics graphics, PageFormat format, int pageIndex) {
        if (pageIndex > 0) {
            return (NO_SUCH_PAGE);
        } else {
            Color back = Color.white;

            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.translate(format.getImageableX(), format.getImageableY());

            if (scale) {
                double scalex = format.getImageableWidth() / (double) componentToBePrinted.getWidth();
                double scaley = format.getImageableHeight() / (double) componentToBePrinted.getHeight();
                double scale = Math.min(scalex, scaley);

                graphics2D.scale(scale, scale);
            }

            disableDoubleBuffering(componentToBePrinted);

            if (white) {
                back = componentToBePrinted.getBackground();
                componentToBePrinted.setBackground(Color.white);
            }

            componentToBePrinted.paint(graphics2D);

            if (white)
                componentToBePrinted.setBackground(back);

            enableDoubleBuffering(componentToBePrinted);
            return (PAGE_EXISTS);
        }
    }

    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}

