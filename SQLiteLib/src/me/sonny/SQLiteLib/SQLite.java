package me.sonny.SQLiteLib;

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
	
	/*
	 * Stores current DB connection.
	 */
	private Connection conn;
	
	/*
	 *  Store fetched data
	 *  List<List<Object>>: Table column
	 *  List<Object>: Table row
	 */
	private List<List<Object>> data;
	
	// Test
	private Cursor cursor;
	
	/**
	 * @constructor
	 */
	// Constructor
	public SQLite() {
		data = new ArrayList<>();
		cursor = null;
	}
	
	/**
	 * Create a connection to a DB file.
	 * @param {String} dbFileName File location of the DB file starting at the root of your server folder. For example: sqlite.connect("plugins/Essentials/myDB/test.db");
	 * @return Represents whether the connection was successful.
	 */
	// No login
	public boolean connect(String dbFileName) {
		try {
			// Import JDBC
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			SQLiteLib.getPlugin().getLogger().log(Level.SEVERE, "Error: Failed to load JDBC! Message: " + e.getMessage());
		}
		
		try {
			// If previous DB connection is still open, then close it.
			if (conn != null && !conn.isClosed()) {
				close();
			}
			
			// Creates a new SQLite DB connection,
			// if the DB file does not exist, it will be automatically created.
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbFileName);
			
			SQLiteLib.getPlugin().getLogger().log(Level.INFO, "DB connection established: " + dbFileName);
			
			return true;
		} catch (SQLException e) {
			SQLiteLib.getPlugin().getLogger().log(Level.SEVERE, "Error: Could not open DB! Message: " + e.getMessage());
		}
		
		return false;
	}
	/**
	 * Create a connection to a DB file that is secured with username and password.
	 * @param {String} dbFileName File location of the DB file starting at the root of your server folder. For example: sqlite.connect("plugins/Essentials/myDB/test.db");
	 * @param {String} username DB username
	 * @param {String} password DB password
	 * @return Represents whether the connection was successful.
	 */
	// Login
	public boolean connect(String dbFileName, String username, String password) {
		try {
			// Import JDBC
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			SQLiteLib.getPlugin().getLogger().log(Level.SEVERE, "Error: Failed to load JDBC! Message: " + e.getMessage());
		}
		
		try {
			// If previous DB connection is still open, then close it.
			if (conn != null && !conn.isClosed()) {
				close();
			}
			
			// Creates a new SQLite DB connection,
			// if the DB file does not exist, it will be automatically created.
			conn = DriverManager.getConnection("jdbc:sqlite:" + dbFileName, username, password);
			
			SQLiteLib.getPlugin().getLogger().log(Level.INFO, "DB connection established: " + dbFileName);
			
			return true;
		} catch (SQLException e) {
			SQLiteLib.getPlugin().getLogger().log(Level.SEVERE, "Error: Could not open DB! Message: " + e.getMessage());
		}
		
		return false;
	}
	
	/**
	 * Execute given query statement. If the given query is a DQL command,
	 * then save the fetched data in both List<List<Object>> data and Cursor.
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
						columnNames.add(rsmd.getColumnName(i+1));
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
				
				SQLiteLib.getPlugin().getLogger().log(Level.INFO, "[SQLiteLib] Query statement executed!");
				
				return true;
			} else {
				SQLiteLib.getPlugin().getLogger().log(Level.SEVERE, "Error: DB Connection is closed or does not exist!");
			}
		} catch (SQLException e) {
			SQLiteLib.getPlugin().getLogger().log(Level.SEVERE, "Error: Could not execute the query statement! Message: " + e.getMessage());
		}
		
		return false;
	}
	
	/**
	 * Close current DB connection. Using this will make the system to allow deleting the DB file.
	 * @return Returns true if the connection was successfully closed. Otherwise false.
	 */
	public boolean close() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
				
				SQLiteLib.getPlugin().getLogger().log(Level.INFO, "DB connection closed!");
				
				return true;
			} else {
				SQLiteLib.getPlugin().getLogger().log(Level.SEVERE, "Error: DB Connection is already closed or does not exist!");
			}
		} catch (SQLException e) {
			SQLiteLib.getPlugin().getLogger().log(Level.SEVERE, "Error: Could not close the connection! Message: " + e.getMessage());
		}
		
		return false;
	}
	
	/**
	 * Getter for the current connection instance. You might not need this at all.
	 * @return Returns the instance of current connection.
	 */
	public Connection getConnection() {
		return conn;
	}
	
	/**
	 * Use fetch after executing DQL statement to get read data
	 * @return Returns currently read data
	 */
	public List<List<Object>> fetch() {
		return data;
	}
	
	/**
	 * 
	 * @param {int} index The index of the row (starts at 0).
	 * @return Returns a specified row of read data as an ArrayList of Objects
	 */
	public List<Object> fetchRow(int index) {
		return data.get(index);
	}
	
	public Cursor cursor() {
		return cursor;
	}
	
	/**
	 * (Not implemented yet)
	 * Cursor class provides an easy way to fetch data without having to parse Object every time.
	 * Cursor will also have a feature to get data using column name String instead of index number.
	 */
	public class Cursor {
		/*
		 *  Store fetched data
		 *  List<List<Object>>: Table column
		 *  List<Object>: Table row
		 */
		private List<List<Object>> data;
		
		/**
		 * Store column names
		 * List index = column index
		 * String = name of the column
		 */
		private List<String> columnNames;
		
		/**
		 * @constructor
		 * @param data Fetched data from execute
		 * @param columnNames List of column names of fetched data from execute 
		 */
		private Cursor(List<List<Object>> data, List<String> columnNames) {
			this.data = data;
			this.columnNames = columnNames;
		}
	}
}
