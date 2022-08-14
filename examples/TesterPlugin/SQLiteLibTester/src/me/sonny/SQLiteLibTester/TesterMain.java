package me.sonny.SQLiteLibTester;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import me.sonny.SQLiteLib.SQLite;
import me.sonny.SQLiteLib.SQLite.Cursor;
import me.sonny.SQLiteLib.SQLiteLib;

public class TesterMain extends JavaPlugin {

	public static SQLiteLib sqliteLib;
	
	@Override
	public void onEnable() {
		sqliteLib = SQLiteLib.getSQLiteLib();
		
		// VVVV You can also use the code below. They are almost the same. VVVV
		// sqliteLib = (SQLiteLib) Bukkit.getPluginManager().getPlugin("SQLiteLib");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("sqltester")) {
			if (args.length == 0) {
				sender.sendMessage("/sqltester 0 - Creates a new DB file mydb.db inside plugins folder.");
				sender.sendMessage("/sqltester 1 - Creates a new Table inside the new DB and inserts few values (execute after test 0).");
				sender.sendMessage("/sqltester 2 - Writes an Image file to BLOB data (execute after test 1).");
				sender.sendMessage("/sqltester 3 - Display items in the table (execute after test 1).");
				
			}
			
			if (args.length == 1) {
				// 0
				// Creates a new DB file mydb.db inside plugins folder.
				if (args[0].equalsIgnoreCase("0")) {
					// Create a new SQLite DB using createDB(dbName).
					SQLite db = sqliteLib.createDB("MyDB");
					
					db.connect( "plugins/mydb.db" );
						
					return true;
				}
				
				// 1
				// Creates a new Table inside the new DB and inserts few values
				if (args[0].equalsIgnoreCase("1")) {
					// You can always use DB(dbName) to get the instance of the created DB.
					SQLite db = sqliteLib.DB("MyDB");
					
					String statement = """
										CREATE TABLE IF NOT EXISTS PERSON (
											PID INTEGER PRIMARY KEY NOT NULL,
											NAME VARCHAR(20) NOT NULL,
											AGE INTEGER NOT NULL,
											PROFILE BLOB)
									   """;
					
					// Create a new table Person(PID, NAME, AGE, PROFILE).
					if (db.execute(statement)) {
						// Insert 8 items. Note that PROFILE is nullable.
						db.execute( "INSERT OR REPLACE INTO PERSON VALUES (0, 'Sonny', 13, NULL)" );
						db.execute( "INSERT OR REPLACE INTO PERSON VALUES (1, 'Annie', 23, NULL)" );
						db.execute( "INSERT OR REPLACE INTO PERSON VALUES (2, 'Tonny', 25, NULL)" );
						db.execute( "INSERT OR REPLACE INTO PERSON VALUES (4, 'Johnny', 42, NULL)" );
						db.execute( "INSERT OR REPLACE INTO PERSON VALUES (5, 'Ronny', 17, NULL)" );
						db.execute( "INSERT OR REPLACE INTO PERSON VALUES (6, 'Kenny', 31, NULL)" );
						db.execute( "INSERT OR REPLACE INTO PERSON VALUES (7, 'Timmy', 10, NULL)" );
					}
					
					return true;
				}
				
				// 2
				// Writes an Image file to BLOB data. This is a temporary solution.
				//
				// Make sure to put cat.jpg file at the root of your server (where your bukkit jar file is located).
				//
				if (args[0].equalsIgnoreCase("2")) {
					try {
						// You can always use DB(dbName) to get the instance of the created DB.
						SQLite db = sqliteLib.DB("MyDB");
						
						// Get Connection instance. This is different from connect() method.
						Connection conn = db.connection();
						
						// Get PreparedStatement instance.
						PreparedStatement ps = conn.prepareStatement( "UPDATE PERSON SET PROFILE = ? WHERE NAME = 'Sonny'" );
						
						// convert the image file to bytes array.
						byte[] cat = sqliteLib.fileToBytes("cat.jpg");
						
						// write bytes array into the PreparedStatement. Somehow the index starts at 1.
						ps.setBytes(1, cat);
						
						// Execute and close the statement
						ps.executeUpdate();
						ps.close();
					} catch (SQLException e) {
						sender.sendMessage(e.getClass().getName() + ": " + e.getMessage());
					}
					
					return true;
				}
				
				// Display items in the table
				if (args[0].equalsIgnoreCase("3")) {
					SQLite db = sqliteLib.DB("MyDB");
					
					if (db.execute("SELECT PID, NAME, AGE FROM PERSON")) {
						Cursor cur = db.cursor();
						
						while (cur.next()) {
							sender.sendMessage("Person ID: " + cur.getInt("PID") + ", Name: " + cur.getString("NAME") + ", Age: " + cur.getInt("AGE"));
						}
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
}
