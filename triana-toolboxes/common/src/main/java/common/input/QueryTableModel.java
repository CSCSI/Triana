package common.input;

import javax.swing.table.AbstractTableModel;


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
 * @version 1.1 30 October 2003
 * @authorDavid Churches 
 */
public class QueryTableModel extends AbstractTableModel {

    String[][] data = {{"This", "is"}, {"a", "Test"}};
    String[] headers = {"Column", "Header"};

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return headers.length;
    }

    public Object getValueAt(int r, int c) {
        return data[r][c];
    }

    public String getColumnName(int c) {
        return headers[c];
    }

    public void setData(String[][] d, String[] h) {
        data = d;
        headers = h;
        fireTableStructureChanged();
    }

}
