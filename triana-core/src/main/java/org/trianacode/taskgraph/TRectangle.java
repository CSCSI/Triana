/*
 * Copyright 2004 - 2007 University of Cardiff.
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

package org.trianacode.taskgraph;

/**
 * awt independent rectangle
 *
 * @author Andrew Harrison
 * @version $Revision:$
 * @created Feb 13, 2008: 8:17:35 PM
 * @date $Date:$ modified by $Author:$
 * Put your notes here...
 */

public class TRectangle {

    private double x = 0;
    private double y = 0;
    private double width = 0;
    private double height = 0;

    public TRectangle() {
    }

    public TRectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public TRectangle(TPoint origin, TDimension dimension) {
        this(origin.getX(), origin.getY(), dimension.getWidth(), dimension.getHeight());
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
