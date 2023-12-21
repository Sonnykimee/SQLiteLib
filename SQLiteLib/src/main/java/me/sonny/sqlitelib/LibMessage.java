package me.sonny.sqlitelib;

public final class LibMessage {
    public static final String PLUGIN_ENABLED = "SQLiteLib has been enabled!";
    public static final String PLUGIN_DISABLED = "SQLiteLib has been disabled. You will have to set up DB connections again!";
    public static final String PLUGIN_NOT_READY = "SQLiteLib is not ready yet!";
    public static final String NO_PERMISSIONS = "You do not have permission to execute this command.";
    public static final String CONNECTION_ESTABLISHED = "DB connection established!";
    public static final String CONNECTION_FAILURE = "Could not establish connection to the DB file.";
    public static final String NO_JDBC = "Could not load JDBC driver.";
    public static final String CONNECTION_CLOSE_SUCCESS = "DB has been successfully closed!";
    public static final String CONNECTION_CLOSE_FAILURE = "Failure to close the DB connection.";
    public static final String CONNECTION_CLOSE_FAILURE_NULL = "DB connection is already closed or null.";
    public static final String EXECUTE_SUCCESS = "Query statement was successfully executed!";
    public static final String EXECUTE_FAILURE = "Could not execute the query statement.";
    public static final String PREPARED_STATEMENT_FAILURE = "Could not get PreparedStatement. The DB connection may have lost.";
    public static final String STATEMENT_RESULTSET_CLOSE_FAILURE = "Could not close statement or result Set.";
    public static final String COLUMN_NOT_FOUND = "Could not find a column with the given name.";
    public static final String NOT_INTEGER = "The value is not an Integer.";
    public static final String NOT_LONG = "The value is not a Long.";
    public static final String NOT_FLOAT = "The value is not a Float.";
    public static final String NOT_DOUBLE = "The value is not a Double.";
    public static final String NOT_BOOLEAN = "The value is not a Boolean.";
    public static final String STATUS_CONNECTED = "DB connection is currently open!";
    public static final String STATUS_NO_CONNECTION = "No DB connection has been established or it is closed.";
    public static final String STATUS_CHECK_FAILURE = "Failure to check the DB status!";
    public static final String PARAMETER_ERROR = "The index parameter does not correspond to the parameter in the given query statement.";
}
