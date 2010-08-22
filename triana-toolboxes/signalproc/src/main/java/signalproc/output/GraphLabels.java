package signalproc.output;

import gov.noaa.pmel.sgt.LineAttribute;

/*
 * Copyright (c) 1995 onwards, University of Wales College of Cardiff
 *
 * Permission to use and modify this software and its documentation for
 * any purpose is hereby granted without fee provided a written agreement
 * exists between the recipients and the University.
 *
 * Further conditions of use are that (i) the above copyright notice and
 * this permission notice appear in all copies of the software and
 * related documentation, and (ii) the recipients of the software and
 * documentation undertake not to copy or redistribute the software and
 * documentation to any other party.
 *
 * THE SOFTWARE IS PROVIDED "AS-IS" AND WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR OTHERWISE, INCLUDING WITHOUT LIMITATION, ANY
 * WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF WALES COLLEGE OF CARDIFF BE LIABLE
 * FOR ANY SPECIAL, INCIDENTAL, INDIRECT OR CONSEQUENTIAL DAMAGES OF ANY
 * KIND, OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER OR NOT ADVISED OF THE POSSIBILITY OF DAMAGE, AND ON
 * ANY THEORY OF LIABILITY, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 */

/* 
 * GraphLabels class to hold information about a particular
 * graph.
 *
 * @version 1.1 23 July 2003
 * @authorDavid Churches 
 */

public class GraphLabels {

    private String title;
    private String xtitle;
    private String ytitle;
    private String lineKeyTitle;
    public static final String Graph_Labels_ClipIn_Tag = "GraphLabels";
    private int lineStyle; // 0=points, 1=line
    private int markType;  // see gov.noaa.pmel.sgt.PlotMark for definitions of mark types
    private double markSize;

// Default constructor

    public GraphLabels() {
        this.title = "";
        this.xtitle = "";
        this.ytitle = "";
        this.lineKeyTitle = null;
        this.lineStyle = LineAttribute.SOLID;
    }

// Constructor which assumes a line graph

    public GraphLabels(String t, String xt, String yt, String lkt) {
        this.title = t;
        this.xtitle = xt;
        this.ytitle = yt;
        this.lineKeyTitle = lkt;
        this.lineStyle = LineAttribute.SOLID;
    }

// Constructor which allows you to set the line style and therefore the mark type and size
// if a mark is chosen.

    public GraphLabels(String t, String xt, String yt, String lkt, int ls, int mt, double ms) {
        this.title = t;
        this.xtitle = xt;
        this.ytitle = yt;
        this.lineKeyTitle = lkt;
        this.lineStyle = ls;
        this.markType = mt;
        this.markSize = ms;
    }

    public String getTitle() {
        return title;
    }

    public String getXTitle() {
        return xtitle;
    }

    public String getYTitle() {
        return ytitle;
    }

    public String getLineKeyTitle() {
        return lineKeyTitle;
    }

    public int getLineStyle() {
        return lineStyle;
    }

    public int getMarkType() {
        return markType;
    }

    public double getMarkSize() {
        return markSize;
    }

}
