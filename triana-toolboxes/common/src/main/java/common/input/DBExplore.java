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
package common.input;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.trianacode.taskgraph.NodeException;
import org.trianacode.taskgraph.Task;
import org.trianacode.taskgraph.Unit;
import triana.types.SampleSet;
import triana.types.util.StringVector;


/**
 * A tool for accessing data stored in an external database
 *
 * @author David Churches
 * @version $Revision $
 */


public class DBExplore extends Unit {

    // parameter data type definitions
    private String username;
    private String password;
    private String hostname;
    private String database;
    private String table;
    private String fields;
    private String criteria = "";
    private String finalQuery = "";
    private String generalQuery = "";
    private SampleSet input;
    private int time1;
    private int time2;
    int numberOfCols;
    int numberOfRows;
    StringVector columnHeaders;
    String originalCriteria = "";
    String originalGeneralQuery = "";
    String[] outputString;
    private int counter = 0;
    private int numberOfSamples = 0;
    private String columnName;


    // a flag indicating whether a connection is being/has been established
    private boolean connect = false;


    /*
     * Called whenever there is data for the unit to process
     */

    public void process() throws Exception {

        if (getInputNodeCount() == 1) {
            originalGeneralQuery = generalQuery;
            originalCriteria = criteria;
            input = (SampleSet) getInputAtNode(0);
            time1 = (int) input.data[0];
            time2 = (int) input.data[1];
            System.out.println("time1 in DBExplore= " + time1);
            System.out.println("time2 in DBExplore= " + time2);

            String paramstr = (String) getParameter("fieldnames");
            String fieldNames = new String();
            while (paramstr.indexOf('\0') > -1) {
                fieldNames = (paramstr.substring(0, paramstr.indexOf('\0')));
                paramstr = paramstr.substring(paramstr.indexOf('\0') + 1);
                if (fieldNames.substring(0, 3).equalsIgnoreCase("gps")) {
                    columnName = fieldNames;
                    System.out.println("time column set to " + columnName);
                }
            }
            if (generalQuery.equals("") || generalQuery.charAt(0) == '%') {
                if (criteria.equals("") || criteria.charAt(0) == '%') {
                    criteria = columnName + " between " + String.valueOf(time1) + " and " + String.valueOf(time2);
                } else {
                    //System.out.println("criteria to start with is " + criteria);
                    criteria = criteria + " and " + columnName + " between " + String.valueOf(time1) + " and " + String
                            .valueOf(time2);

                }
            } else {
                //System.out.println("generalQuery to start with is " + generalQuery);
                String[] findWhere = generalQuery.split(" ");
                boolean foundWhere = false;
                for (int i = 0; i < findWhere.length; i++) {
                    if (findWhere[i].equals("where")) {
                        foundWhere = true;
                    }
                }
                if (foundWhere) {
                    generalQuery = generalQuery + " and " + columnName + " between " + String.valueOf(time1) + " and "
                            + String.valueOf(time2);
                } else {
                    generalQuery = generalQuery + " where " + columnName + " between " + String.valueOf(time1) + " and "
                            + String.valueOf(time2);
                }
            }
            if (generalQuery.equals("") || generalQuery.charAt(0) == '%') {
                finalQuery = "select " + fields + " from " + table + " where " + criteria;
            } else {
                finalQuery = generalQuery;
            }
        }

        try {
            //Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception E) {
            System.err.println("Unable to load driver.");
            E.printStackTrace();
        }

        try {
            Connection C = DriverManager.getConnection("jdbc:mysql://" + hostname + ":3306/"
                    + database + "?user=" + username + "&password=" + password);
            java.sql.Statement Stmt = C.createStatement();

            System.out.println("query in unit = " + finalQuery);
            setParameter("finalQuery", finalQuery);
            ResultSet RS = Stmt.executeQuery(finalQuery);

            generalQuery = originalGeneralQuery;
            criteria = originalCriteria;

            ResultSetMetaData rsmd = RS.getMetaData();
            int numberOfCols = rsmd.getColumnCount();

            setNumberOfNodes(numberOfCols);

            int i = 0;

            RS.last();
            numberOfSamples = RS.getRow();
            System.out.println("There are " + numberOfSamples + " samples in the SampleSet");

            for (int j = 1; j <= numberOfCols; ++j) {

                if (getTask().getDataOutputNode(j - 1).isConnected()) {
                    if ((rsmd.getColumnType(j) == java.sql.Types.VARCHAR) || (rsmd.getColumnType(j)
                            == java.sql.Types.CHAR)) {
                        System.out.println("VARCHAR or CHAR");
                        outputString = new java.lang.String[numberOfSamples];
                        i = 0;
                        RS.beforeFirst();
                        while (RS.next()) {
                            outputString[i++] = RS.getString(j);
                        }
                        if (numberOfSamples > 0) {
                            outputAtNode(j - 1, outputString);
                        }
                    } else {
                        SampleSet output = new SampleSet(128, numberOfSamples);
                        output.setDependentLabels(0, rsmd.getColumnLabel(j));

                        i = 0;
                        RS.beforeFirst();
                        while (RS.next()) {
                            output.data[i++] = RS.getDouble(j);
                        }
                        if (numberOfSamples > 0) {
                            outputAtNode(j - 1, output);
                        }
                    }
                }
            }
            C.close();
        } catch (SQLException E) {
            System.out.println("SQLException: " + E.getMessage());
            System.out.println("SQLState: " + E.getSQLState());
            System.out.println("VendorError: " + E.getErrorCode());
        }


        if (getInputNodeCount() == 1) {
            criteria = originalCriteria;
        }
        if (getInputNodeCount() == 1) {
            generalQuery = originalGeneralQuery;
        }


    }


    /**
     * Called when the unit is created. Initialises the unit's properties and parameters.
     */
    public void init() {
        super.init();

        // Initialise node properties
        setDefaultInputNodes(0);
        setMinimumInputNodes(0);
        setMaximumInputNodes(1);

        setDefaultOutputNodes(1);
        setMinimumOutputNodes(0);
        setMaximumOutputNodes(Integer.MAX_VALUE);

        // Initialise parameter update policy
        setParameterUpdatePolicy(Task.IMMEDIATE_UPDATE);

        // Initialise task parameters to default values (if not already initialised)
        Task task = getTask();

        defineParameter("username", "", USER_ACCESSIBLE);
        defineParameter("password", "", TRANSIENT);
        defineParameter("hostname", "", USER_ACCESSIBLE);
        defineParameter("database", "", USER_ACCESSIBLE);
        defineParameter("table", "", USER_ACCESSIBLE);
        defineParameter("connect", String.valueOf(false), TRANSIENT);
        defineParameter("dbnames", "", TRANSIENT);
        defineParameter("tbnames", "", TRANSIENT);
        defineParameter("fields", "", TRANSIENT);
        defineParameter("fieldnames", "", TRANSIENT);
        defineParameter("pressedCount", String.valueOf(false), TRANSIENT);
        defineParameter("criteria", "", USER_ACCESSIBLE);
        defineParameter("pressedQuery", String.valueOf(false), TRANSIENT);
        defineParameter("countResults", "0", TRANSIENT);
        defineParameter("generalQuery", "", USER_ACCESSIBLE);
        defineParameter("finalQuery", "", TRANSIENT);


        // Initialise parameter panel class
        setParameterPanelClass("common.input.DBExplorePanel");
    }

    /**
     * Called when the unit is reset.
     */
    public void reset() {
        // Set unit parameters to the values specified by the task definition
        Task task = getTask();
        //System.out.println("DBExplore inside reset():");
        username = (String) task.getParameter("username");
        //System.out.println("DBExplore got username:");
        password = (String) task.getParameter("password");
        //System.out.println("DBExplore got password:");
        hostname = (String) task.getParameter("hostname");
        //System.out.println("DBExplore got hostname:");
        database = (String) task.getParameter("database");
        //System.out.println("DBExplore got database:");
        table = (String) task.getParameter("table");
        //System.out.println("DBExplore got table:");
        fields = (String) task.getParameter("fields");
        //System.out.println("DBExplore got fields:");
        criteria = (String) task.getParameter("criteria");
        //System.out.println("DBExplore got criteria:");
        //System.out.println("DBExplore finished in reset():");
    }

    /**
     * Called when the unit is disposed of.
     */
    public void dispose() {
        // Insert code to clean-up DBExplore (e.g. close open files)
    }


    /**
     * Called a parameters is updated (e.g. by the GUI)
     */
    public void parameterUpdate(String paramname, Object value) {
        // Code to update local variables
        if (paramname.equals("username")) {
            username = (String) value;
            //System.out.println("DBExplore recieved username= " + username);
        }

        if (paramname.equals("password")) {
            password = (String) value;
            //System.out.println("DBExplore recieved password= " + password);
        }
        if (paramname.equals("hostname")) {
            hostname = (String) value;
            //System.out.println("DBExplore recieved hostname= " + hostname);
        }

        if (paramname.equals("database")) {
            database = (String) value;
            //System.out.println("DBExplore recieved database= " + database);
            if (!database.equals("") && !username.equals("") && !password.equals("") && !hostname.equals("")) {
                String[][] tbnamesarray = makeQuery(hostname, username, password, database, "show tables");
                String tbnames = "";
                for (int i = 0; i < tbnamesarray.length; ++i) {
                    tbnames += tbnamesarray[i][0];
                    tbnames += "\0";
                }
                getTask().setParameter("tbnames", tbnames);
            }
        }

        if (paramname.equals("table")) {
            table = (String) value;
            //System.out.println("DBExplore recieved table= " + table);
            if (!database.equals("") && !username.equals("") && !password.equals("") && !hostname.equals("")) {
                String[][] fieldnamesarray = makeQuery(hostname, username, password, database, "describe " + table);
                String fieldnames = "";
                for (int i = 0; i < fieldnamesarray.length; ++i) {
                    fieldnames += fieldnamesarray[i][0];
                    fieldnames += "\0";
                }
                getTask().setParameter("fieldnames", fieldnames);
            }
        }

        if (paramname.equals("connect")) {
            if (value.equals(String.valueOf(true))) {
                //System.out.println("DBExplore recieved connect= true");
                if (!hostname.equals("") && !username.equals("") && !password.equals("")) {
                    initialiseConnection();
                    String[][] dbnamesarray = makeQuery(hostname, username, password, "", "show databases");
                    String dbnames = "";
                    for (int i = 0; i < dbnamesarray.length; ++i) {
                        dbnames += dbnamesarray[i][0];
                        dbnames += "\0";
                    }
                    getTask().setParameter("dbnames", dbnames);
                }
            }
            getTask().setParameter("connect", String.valueOf(false));
        }
        if (paramname.equals("pressedCount")) {
            countResults();
        }
        if (paramname.equals("pressedQuery")) {
            //System.out.println("DBExplore recieved pressedQuery");

            if (!hostname.equals("") && !username.equals("") && !password.equals("") && !database.equals("")) {

                if (fields.equals("") && (generalQuery.equals("") || generalQuery.charAt(0) == '%')) {
                    System.out.println("No fields selected !!!");
                } else {

                    String[][] fieldnamesarray = makeQuery(hostname, username, password, database, finalQuery);

                    getTask().setParameter("numberOfCols", String.valueOf(fieldnamesarray[0].length));
                    getTask().setParameter("numberOfRows", String.valueOf(fieldnamesarray.length));

                    //System.out.println("Sending " + numberOfRows + " rows back to Panel");
                    String results;

                    for (int i = 0; i < fieldnamesarray.length; ++i) { /* the number of rows */
                        results = "";
                        for (int j = 0; j < fieldnamesarray[0].length; ++j) { /* the number of cols */
                            results += fieldnamesarray[i][j];
                            results += "\0";
                        }
                        getTask().setParameter("results", results);
                        getTask().setParameter("results", "empty");
                    }

                    setNumberOfNodes(fieldnamesarray[0].length);
                    if (numberOfRows > 0) {
                        getTask().setParameter("showTable", String.valueOf(counter++));
                    }

                }
            }
        }

        if (paramname.equals("fields")) {
            fields = (String) value;
            //System.out.println("DBExplore recieved fields= " + fields);

            if (generalQuery.equals("") || generalQuery.charAt(0) == '%') {
                if (criteria.equals("") || criteria.charAt(0) == '%') {
                    finalQuery = "select " + fields + " from " + table;
                } else {
                    finalQuery = "select " + fields + " from " + table + " where " + criteria;
                }
            }
        }
        if (paramname.equals("numberOfNodes")) {
            int nodeNumber = Integer.parseInt((String) value);
            setNumberOfNodes(nodeNumber);
        }

        if (paramname.equals("criteria")) {
            criteria = (String) value;
            //System.out.println("DBExplore recieved criteria= " + criteria);

            if (generalQuery.equals("") || generalQuery.charAt(0) == '%') {
                if (criteria.equals("") || criteria.charAt(0) == '%') {
                    finalQuery = "select " + fields + " from " + table;
                } else {
                    finalQuery = "select " + fields + " from " + table + " where " + criteria;
                }
            }
        }
        if (paramname.equals("generalQuery")) {
            generalQuery = (String) value;
            //System.out.println("DBExplore recieved generalQuery= " + generalQuery);
            //System.out.println("recieved generalQuery as " + generalQuery);
            if (generalQuery.equals("") || generalQuery.charAt(0) == '%') {
                if (criteria.equals("")) {
                    finalQuery = "select " + fields + " from " + table;
                } else {
                    finalQuery = "select " + fields + " from " + table + " where " + criteria;
                }
            } else {
                finalQuery = generalQuery;
            }
        }

    }

    public void countResults() {
        String[][] countTheResults;
        String countQuery = new String();
        boolean performCount = false;
        if (hostname != "" && password != "" && username != "" && database != "") {
            if (generalQuery.equals("")) {
                if (table != "") {
                    performCount = true;
                    if (criteria == "") {
                        countQuery = "select count(*) from " + table;
                    } else {
                        countQuery = "select count(*) from " + table + " where " + criteria;
                    }
                } else {
                    System.out.println("No table selected for Count Command");
                }
            } else {
                performCount = true;
                String[] splitString = generalQuery.split("from");
                countQuery = "select count(*) from " + splitString[splitString.length - 1];
            }
        } else {
            System.out.println("Database access parameters not set");
        }
        if (performCount) {
            try {
                countTheResults = makeQuery(hostname, username, password, database, countQuery);
                setParameter("countResults", countTheResults[0][0]);
            } catch (Exception e) {
                System.out.println("Cannot perform count on this Query");
                setParameter("countResults", "Check Query");
            }
        }
    }


    public void setNumberOfNodes(int n) {
        Task task = getTask();

        try {
            while (task.getDataOutputNodeCount() < n) {
                task.addDataOutputNode();
            }
            while (task.getDataOutputNodeCount() > n) {
                task.removeDataOutputNode(task.getDataOutputNode(n));
            }
        } catch (NodeException except) {
            notifyError(except.getMessage());
        }
    }

    /**
     * Initialise database connection
     */
    public void initialiseConnection() {
        //System.out.println("inside initialiseConnection");

        try {
            //Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception E) {
            System.err.println("Unable to load driver.");
            E.printStackTrace();
        }

    }

    public String[][] makeQuery(String host, String user, String passwd, String dbase, String query) {

        String result = "";
        String[][] data;
        data = new String[1][1];
        if (user == null) {
        } else {
            try {
                Connection C = DriverManager.getConnection("jdbc:mysql://" + host + ":" + "3306"
                        + "/" + dbase + "?user=" + user + "&password=" + passwd);

                if (dbase.equals("")) {
                    C = DriverManager.getConnection("jdbc:mysql://" + host + ":" + "3306" + "/"
                            + "?user=" + user + "&password=" + passwd);
                }

                System.out.println("Making the following query: " + query);

                java.sql.Statement Stmt = C.createStatement();
                ResultSet RS = Stmt.executeQuery(query);

                System.out.println("query made and resultSet generated");

                ResultSetMetaData rsmd = RS.getMetaData();
                numberOfCols = rsmd.getColumnCount();

                RS.last();
                numberOfRows = RS.getRow();

                StringVector columnHeaders = new StringVector();

                for (int i = 1; i <= numberOfCols; ++i) {
                    columnHeaders.add((String) rsmd.getColumnName(i));
                }

                getTask().setParameter("headersVec", columnHeaders);

                System.out.println("There are " + numberOfRows + " rows in your query");

                if (numberOfRows > 100000) {
                    numberOfRows = 100000;
                    System.out.println("This has been truncated to " + numberOfRows + " rows");
                }

                data = new String[numberOfRows][numberOfCols];


                RS.beforeFirst();
                int i = 0;
                while (RS.next() && i < numberOfRows) {
                    for (int j = 1; j <= numberOfCols; ++j) {
                        data[i][j - 1] = RS.getString(j);
                    }
                    ++i;
                }

                if (numberOfRows == 0) {
                    data = new String[1][numberOfCols];
                    for (int k = 0; k < numberOfCols; ++k) {
                        data[0][k] = "";
                    }

                    System.out.println("in makeQuery, there are no rows!!!!");
                    return data;
                }


                C.close();
            } catch (SQLException E) {
                System.out.println("SQLException: " + E.getMessage());
                System.out.println("SQLState: " + E.getSQLState());
                System.out.println("VendorError: " + E.getErrorCode());
            }
        }
        return data;


    }


    /**
     * @return an array of the input types for DBExplore
     */
    public String[] getInputTypes() {
        return new String[]{"SampleSet"};
    }

    /**
     * @return an array of the output types for DBExplore
     */
    public String[] getOutputTypes() {
        return new String[]{"SampleSet", "java.lang.String[]"};
    }


    /**
     * @return a <b>brief!</b> description of what the unit does
     */
    public String getPopUpDescription() {
        return "A tool for accessing data stored in an external database";
    }

    /**
     * @returns the location of the help file for this unit.
     */
    public String getHelpFile() {
        return "DBExplore.html";
    }

}



