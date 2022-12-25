package me.sonny.sqlitelib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SQLite {

    private Connection conn; // DB connection.

    private List<List<Object>> data;

    // Cursor provides a convenient way to get values from the read data.
    private Cursor cursor;

    // private String readyStatement;
    // private List<String> readyStatementPos;

    // Constructor
    public SQLite() {
        data = new ArrayList<>();
        cursor = null;
        // readyStatement = "";
        // readyStatementPos = new ArrayList<>();
    }

    /**
     * Creates a connection to a DB file (without a login process).
     * The file path starts at the root directory of your server (where the Bukkit jar file is located).
     *
     * For example: sqlite.connect("plugins/MyDatabase/test.db");
     *
     * If the SQLite DB file does not exist in the given path, then a new DB file will be created.
     *
     * @param fileName The path of the DB file (starting at the root directory of your server folder).
     *
     * @return Represents whether the connection was successful.
     */
    public boolean connect(String fileName) {
        try {
            Class.forName("org.sqlite.JDBC");

            try {
                // If a previous DB connection is already open, then close it.
                if (conn != null && !conn.isClosed()) {
                    close();
                }

                // Creates a new DB connection,
                // if the DB file does not exist, it will be automatically created.
                conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);

                SQLiteLib.PLUGIN.getLogger().log(Level.INFO, "DB connection established: " + fileName);

                return true;
            } catch (SQLException e) {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, "Could not open the DB! " + e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            // Failed to find or load the JDBC.
            // This will almost never happen since the JDBC is statically linked.
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, "Failed to load the JDBC! " + e.getClass().getName() + ": " + e.getMessage());
        }

        return false;
    }

    /**
     * Creates a connection to a DB file (with a login process).
     * The file path starts at the root directory of your server (where the Bukkit jar file is located).
     *
     * For example: sqlite.connect("plugins/MyDatabase/test.db");
     *
     * If the SQLite DB file does not exist in the given path, then a new DB file will be created.
     *
     * @param fileName The path of the DB file (starting at the root directory of your server folder).
     * @param username DB username.
     * @param password DB password.
     *
     * @return Represents whether the connection was successful.
     */
    public boolean connect(String fileName, String username, String password) {
        try {
            Class.forName("org.sqlite.JDBC");

            try {
                // If a previous DB connection is already open, then close it.
                if (conn != null && !conn.isClosed()) {
                    close();
                }

                // Creates a new DB connection,
                // if the DB file does not exist, it will be automatically created.
                conn = DriverManager.getConnection("jdbc:sqlite:" + fileName, username, password);

                SQLiteLib.PLUGIN.getLogger().log(Level.INFO, "DB connection established: " + fileName);

                return true;
            } catch (SQLException e) {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, "Could not open the DB! " + e.getClass().getName() + ": " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            // Failed to find or load the JDBC.
            // This will almost never happen since the JDBC is statically linked.
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, "Failed to load the JDBC! " + e.getClass().getName() + ": " + e.getMessage());
        }

        return false;
    }

    /**
     * Execute given query statement. If the given query is a DQL command,
     * then save the fetched data in both List<List<Object>> data and Cursor.
     *
     * @param {String} statement The query statement to execute
     * @return Returns true if the statement was successfully executed. Otherwise false.
     */
    public boolean execute(String statement) {
        data.clear(); // Empty previously fetched data

        PreparedStatement ps;
        ResultSet rs;
        ResultSetMetaData rsmd;

        List<List<Object>> cursorData = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();

        List<Object> row;

        try {
            if (conn != null && !conn.isClosed()) {
                ps = conn.prepareStatement(statement);

                if (ps.execute()) {
                    // DQL command; The first result is a ResultSet
                    rs = ps.getResultSet();
                    rsmd = rs.getMetaData();

                    int numCol = rsmd.getColumnCount(); // Number of values in one item

                    // Copy column names
                    for (int i=0; i<numCol; i++) {
                        columnNames.add(rsmd.getColumnName(i+1).toUpperCase());
                    }

                    // Copy data
                    while (rs.next()) {
                        row = new ArrayList<>(); // Empty row

                        for (int i=0; i<numCol; i++) {
                            row.add(rs.getObject(i+1)); // ResultSet column index starts at 1
                        }

                        data.add(row);
                        cursorData.add(row);
                    }

                    // Set up Cursor
                    cursor = new Cursor(cursorData, columnNames);
                } else {
                    // DDL or DML  command; The first result has no result
                }

                // Close prepare statement
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }

                SQLiteLib.PLUGIN.getLogger().log(Level.INFO, "Query statement executed!");

                return true;
            } else {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, "DB Connection is closed or does not exist!");
            }
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, "Error: Could not execute the query statement! " + e.getClass().getName() + ": " + e.getMessage());
        }

        return false;
    }

    /*
     * DO NOT USE YET
     */
    /**
     * Executes ready statement. Use ready()
     * @return Returns true if the statement was successfully executed. Otherwise false.
     */
	/*
	public boolean execute() {
		return execute(readyStatement);
	}
	*/

    /**
     * Prepare a statement to execute by using executeReady*()
     * @param statement
     */
	/*
	public void ready(String statement) {
		if (!statement.contains("?")) {
			readyStatement = statement;
			return;
		}

		int count = 0;
		int[] start = {};
		int[] end = {};

		for (int i=0; i<statement.length(); i++) {
			if (statement.charAt(i) == '\'') {
				if (start.length == end.length) {
					start[count] = i;
				} else if (start.length > end.length) {
					end[count] = i;
					count++;
				}
			}
		}

		for (int i=0; i<statement.length(); i++) {

		}
	}
	*/

    /**
     * Close current DB connection.
     *
     * This will release the DB file from "File in Use" status and allow to delete the DB file.
     *
     * @return true if the connection had been successfully closed. Otherwise false.
     */
    public boolean close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();

                SQLiteLib.PLUGIN.getLogger().log(Level.INFO, "DB connection closed!");

                return true;
            } else {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, "DB Connection is already closed or does not exist!");
            }
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, "Could not close the connection! " + e.getClass().getName() + ": " + e.getMessage());
        }

        return false;
    }

    /**
     * Get the DB connection instance.
     *
     * @return The DB connection instances.
     */
    public Connection connection() {
        return conn;
    }

    /**
     * Returns the read data after executing DQL statement in List<List<Object>>.
     *
     * @return Read data
     */
    public List<List<Object>> fetch() {
        return data;
    }

    /**
     * Returns a specified row of the read data in List<Object>.
     *
     * @param index The index number of the row (starts at 0).
     * @return The specified row of the read data.
     */
    public List<Object> fetchRow(int index) {
        return data.get(index);
    }

    /**
     * Get the Cursor instance of this DB.
     * @see Cursor
     *
     * @return A Cursor instance
     */
    public Cursor cursor() {
        return cursor;
    }


    /*
     * Cursor class provides an easy way to fetch data without having to parse Object every time.
     * Cursor will also have a feature to get data using column name String instead of index number.
     */
    public class Cursor {

        private int position;

        private List<List<Object>> data;

        private List<String> columnNames;

        private Cursor(List<List<Object>> data, List<String> columnNames) {
            this.data = data;
            this.columnNames = columnNames;
            position = -1;
        }

        /**
         * Returns the number of rows of fetched data
         * @return Returns the number of rows of fetched data
         */
        public int size() {
            return data.size();
        }

        /**
         * Move the position pivot to -1
         */
        public void beforeFirst() {
            position = -1;
        }

        /**
         * Move the position pivot to the size number of the fetched data
         */
        public void afterLast() {
            position = data.size();
        }

        /**
         * Move pivot to the next position.
         * @return If there is next, returns true. Otherwise false.
         */
        public boolean next() {
            if (data.size() > 0 && position < data.size()-1) {
                position++;
                return true;
            }

            return false;
        }

        /**
         * Move pivot to the previous position.
         * @return If there is previous, returns true. Otherwise false.
         */
        public boolean previous() {
            if (data.size() > 0 && position > 1) {
                position--;
                return true;
            }

            return false;
        }

        public boolean first() {
            if (data.size() > 0) {
                position = 0;
                return true;
            }

            return false;
        }

        public boolean last() {
            if (data.size() > 0) {
                position = data.size()-1;
                return true;
            }

            return false;
        }

        public Integer getInt(int column) {
            Integer value = (Integer) data.get(position).get(column);

            return value;
        }
        public Integer getInt(String column) {
            column = column.toUpperCase();
            Integer value = (Integer) data.get(position).get(columnNames.indexOf(column));

            return value;
        }

        public Float getFloat(int column) {
            Float value = (Float) data.get(position).get(column);

            return value;
        }
        public Float getFloat(String column) {
            column = column.toUpperCase();
            Float value = (Float) data.get(position).get(columnNames.indexOf(column));

            return value;
        }

        // Note that SQLite does not natively supports double-precision
        public Double getDouble(int column) {
            Double value = (Double) data.get(position).get(column);

            return value;
        }
        public Double getDouble(String column) {
            column = column.toUpperCase();
            Double value = (Double) data.get(position).get(columnNames.indexOf(column));

            return value;
        }

        // Note that SQLite does not natively supports Boolean.
        // In SQLite, Boolean is an integer. True is a substitute for 1 and False for 0.
        public Boolean getBool(int column) throws Exception {
            Integer value = (Integer) data.get(position).get(column);

            if (value == null) {
                return null;
            } else if (value == 0) {
                return false;
            } else if (value == 1) {
                return true;
            } else {
                throw new Exception("The value is not a Boolean!");
            }
        }
        public Boolean getBool(String column) throws Exception {
            column = column.toUpperCase();
            Integer value = (Integer) data.get(position).get(columnNames.indexOf(column));

            if (value == null) {
                return null;
            } else if (value == 0) {
                return false;
            } else if (value == 1) {
                return true;
            } else {
                throw new Exception("The value is not a Boolean!");
            }
        }

        public String getString(int column) {
            String value = data.get(position).get(column).toString();

            return value;
        }
        public String getString(String column) {
            column = column.toUpperCase();
            String value = data.get(position).get(columnNames.indexOf(column)).toString();

            return value;
        }
    }
}
