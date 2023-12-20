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

    private Connection conn; // Database connection

    private List<List<Object>> data; // Fetched data from DB will be stored here first.

    // Cursor provides a convenient way to get values from the read data.
    private Cursor cursor;

    private ReadyStatement readyStatement;

    // Constructor
    public SQLite() {
        data = new ArrayList<>();
        cursor = null;
        readyStatement = null;
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
                // If there is a DB connection that is already open, then close.
                if (conn != null && !conn.isClosed()) {
                    close();
                }

                // Creates a new DB connection,
                // if the DB file does not exist, it will be automatically created.
                conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);

                SQLiteLib.PLUGIN.getLogger().log(Level.INFO, LibMessage.CONNECTION_ESTABLISHED + ", DB file location: " + fileName);

                return true;
            } catch (SQLException e) {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.CONNECTION_FAILURE + ", Error Message: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            // Failed to find or load the JDBC. This will almost never happen since the JDBC is statically linked.
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NO_JDBC + ", Error Message: " + e.getMessage());
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

                SQLiteLib.PLUGIN.getLogger().log(Level.INFO, LibMessage.CONNECTION_ESTABLISHED + ", DB file location: " + fileName);

                return true;
            } catch (SQLException e) {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.CONNECTION_FAILURE + ", Error Message: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            // Failed to find or load the JDBC. This will almost never happen since the JDBC is statically linked.
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.NO_JDBC + ", Error Message: " + e.getMessage());
        }

        return false;
    }

    private boolean execute(PreparedStatement ps) {
        data.clear(); // Empty previously fetched data

        ResultSet rs = null;
        ResultSetMetaData rsmd = null;

        List<List<Object>> cursorData = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();

        try {
            boolean isResultSet = ps.execute();

            if (isResultSet) {
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
                    List<Object> row = new ArrayList<>(); // Empty row

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

            // Send execute sucess message
            SQLiteLib.PLUGIN.getLogger().log(Level.INFO, LibMessage.EXECUTE_SUCCESS);

            return true;
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.EXECUTE_FAILURE + ", Error Message: " + e.getMessage());
        } finally {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            } catch (SQLException e) {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.STATEMENT_RESULTSET_CLOSE_FAILURE + ", Error Message: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Execute given query statement. If the given query is a DQL command,
     * then save the fetched data in both List<List<Object>> data and Cursor.
     *
     * @param {String} statement The query statement to execute
     * @return Returns true if the statement was successfully executed. Otherwise, false.
     */
    public boolean execute(String statement) {
        PreparedStatement ps = null;

        try {
            if (conn != null && !conn.isClosed()) {
                ps = conn.prepareStatement(statement);

                return execute(ps);
            } else {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.CONNECTION_CLOSE_FAILURE_NULL);
            }
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.EXECUTE_FAILURE + ", Error Message: " + e.getMessage());
        }

        return false;
    }

    /**
     * Returns a new ReadyStatement instance.
     *
     * This method is for the scripting plugins that cannot directly import classes inside a 3rd party plugin.
     *
     * @return ReadyStatement instance
     */
    public ReadyStatement readyStatement(String statement) {
        ReadyStatement readyStatement = null;

        try {
            if (conn != null && !conn.isClosed()) {
                PreparedStatement ps = conn.prepareStatement(statement);

                readyStatement = new ReadyStatement(ps);
            } else {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.CONNECTION_CLOSE_FAILURE_NULL);
            }
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.PREPARED_STATEMENT_FAILURE + ", Error Message: " + e.getMessage());
        }

        return readyStatement;
    }

    public void ready(ReadyStatement statement) {
        this.readyStatement = statement;
    }

	public boolean executeReadyStatement() {
		return execute(readyStatement.preparedStatement());
	}

    /**
     * Close current DB connection.
     *
     * This will release the DB file from "File in Use" status and allow to delete the DB file.
     *
     * @return true if the connection had been successfully closed. Otherwise, false.
     */
    public boolean close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                SQLiteLib.PLUGIN.getLogger().log(Level.INFO, LibMessage.CONNECTION_CLOSE_SUCCESS);

                return true;
            } else {
                SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.CONNECTION_CLOSE_FAILURE_NULL);
            }
        } catch (SQLException e) {
            SQLiteLib.PLUGIN.getLogger().log(Level.SEVERE, LibMessage.CONNECTION_CLOSE_FAILURE + ", Error Message: " + e.getMessage());
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
}
