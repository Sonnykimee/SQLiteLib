package me.sonny.SQLiteLib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SQLiteLib extends JavaPlugin {
	
	public static SQLiteLib PLUGIN;
	
	private SQLite defaultDb; // The default SQLite DB.
	
	// HashMap to store multiple DBs with a distinctive name each.
	private Map<String, SQLite> databases;
	
	@Override
	public void onEnable() {
		PLUGIN = this;
		defaultDb = new SQLite();
		databases = new HashMap<>();
		
		databases.put("default", defaultDb);
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "SQLiteLib has been enabled!");
	}
	
	@Override
	public void onDisable() {
		// Close every DB inside the databases HashMap
		for(Map.Entry<String, SQLite> entry : databases.entrySet()) {
		    entry.getValue().close();
		}
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "SQLiteLib has been disabled! You will need to open DB connections again!");
	}
	
	/**
	 *  As of version 0.2.0, command can only access the default database.
	 *  
	 *  *The main purpose of this plugin (SQLiteLib) is to become a library API rather than an in-game DB editor.*
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("sqlite.use")) {
			//
			// "/sqlite" command
			//
			if (label.equalsIgnoreCase("sqlite")) {
				if (args.length == 0) {
					//
					// No argument
					//
					showCommands(sender);
					
				} else if (args.length == 1) {
					//
					// 1 argument
					//
					if (args[0].equalsIgnoreCase("close")) {
						if (defaultDb.close()) {
							if (sender instanceof Player) {
								sender.sendMessage("[SQLiteLib] DB connection closed!");
							}
						}
						return true;
					} else if (args[0].equalsIgnoreCase("check")) {
						try {
							if (defaultDb.connection() == null) {
								sender.sendMessage("[SQLiteLib] DB connection is NULL!");
							} else if (defaultDb.connection().isClosed()) {
								sender.sendMessage("[SQLiteLib] DB connection is closed!");
							} else {
								sender.sendMessage("[SQLiteLib] DB connection is open!");
							}
						} catch (SQLException e) {
							sender.sendMessage("[SQLiteLib] Error: Failed to check the DB status! Message: " + e.getMessage());
						}
						return true;
					}
					
				} else {
					//
					// More than 1 argument
					//
					if (args[0].equalsIgnoreCase("connect")) {
						String url = "";
						for (int i=1; i<args.length; i++) {
							url += args[i] + " ";
						}
						url.trim();
						
						if (defaultDb.connect(url)) {
							if (sender instanceof Player) {
								sender.sendMessage("[SQLiteLib] DB connection established: " + url);
							}
						}
						
						return true;
					} else if (args[0].equalsIgnoreCase("execute")) {
						String statement = "";
						
						for (int i=1; i<args.length; i++) {
							statement += args[i] + " ";
						}
						
						statement.trim(); // path name cannot contain spaces at the end of the name.
						
						if (defaultDb.execute(statement)) {
							if (sender instanceof Player) {
								sender.sendMessage("[SQLiteLib] Query statement executed!");
							}
						}
						
						return true;
					} else {
						//
						// No matching argument
						//
						showCommands(sender);
					}
				}
			}
		} else {
			//
			// No permission
			//
			sender.sendMessage(ChatColor.RED + "[SQLiteLib] You do not have permission to execute this command!");
		}
		
		return false;
	}
	
	/**
	 * Get the instance of this plugin. If the plugin was not set up, returns null.
	 * This is the same action as: Bukkit.getPluginManager().getPlugin("SQLiteLib")
	 * 
	 * @return The instance of this plugin (SQLiteLib).
	 */
	public static SQLiteLib getSQLiteLib() {
		if (PLUGIN == null) {
			Bukkit.getConsoleSender().sendMessage("[SQLiteLib] Plugin is not set up yet!");
			return null;
		}
		return PLUGIN;
	}
	
	/**
	 * Get the default SQLite DB instance.
	 * 
	 * @return The default DB instance.
	 */
	public SQLite DB() {
		return defaultDb;
	}
	
	/**
	 * Get the SQLite DB instance inside databases HashMap that has the given name.
	 * All DB names are saved as lower cases.
	 * @see createDB(dbName).
	 * 
	 * If no DB matches, returns null.
	 * 
	 * @param {String} dbName The name of the DB instance.
	 * @return Returns a database that has the matching name.
	 */
	public SQLite DB(String dbName) {
		dbName = dbName.toLowerCase();
		if (databases.containsKey(dbName)) {
			return databases.get(dbName);
		}
		
		// No DB with the given name was found.
		PLUGIN.getLogger().log(Level.SEVERE, "Error: Could not find a DB that matches the name!");
		return null;
	}
	
	/**
	 * Creates a new SQLite database instance in the databases HashMap that has the given name in lower cases.
	 * 
	 * The created database instanced will be stored inside databases HashMap.
	 * Use DB(dbName) method to get the DB instance.
	 * 
	 * @param dbName The given name for the new database instance.
	 * @return A newly created SQLite database instance.
	 */
	public SQLite createDB(String dbName) {
		dbName = dbName.toLowerCase();
		
		// If there already is a DB with the given name, then close the previous DB connection.
		if (databases.containsKey(dbName)) {
			databases.get(dbName).close(); 
		}
		
		SQLite db = new SQLite();
		
		databases.put(dbName, db); // Store the SQLite DB instance inside the HashMap.
		
		return db;
	}
	
	/**
	 * Displays "/sqlite" command help.
	 * 
	 * @param sender Sender of the command (either the console or a Player).
	 */
	private void showCommands(CommandSender sender) {
		// Commands
		sender.sendMessage(ChatColor.GRAY + "----[" + ChatColor.WHITE + " SQLiteLib Commands " + ChatColor.GRAY + "]----");
		sender.sendMessage(ChatColor.GOLD + "/sqlite connect <DB file location> " + ChatColor.YELLOW + "[username] [password]" + ChatColor.WHITE + " - Opens a DB connection.");
		sender.sendMessage(ChatColor.GOLD + "/sqlite execute <statement>" + ChatColor.WHITE + " - Execute the given query statement.");
		sender.sendMessage(ChatColor.GOLD + "/sqlite close" + ChatColor.WHITE + " - Close current database connection.");
		sender.sendMessage(ChatColor.GOLD + "/sqlite check" + ChatColor.WHITE + " - Check the status of current connection.");
		sender.sendMessage(ChatColor.GRAY + "---------------------------");
	}
	
	/**
	 * Converts the binary file located in the given path into a byte array.
	 * 
	 * If the file was not found, or inaccessible, returns null.
	 * 
	 * @param fileName The location of the file to convert.
	 * @return Byte array of the given file.
	 */
	public byte[] fileToBytes(String fileName) {
		byte[] result = null;
		File file = new File(fileName);
		
		if (file.exists()) {
			// If the file exists
			try {
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				
				// Read the file input stream and writes to the byte array output stream
				for (int len; (len = fis.read(buffer)) != -1;) {
					bos.write(buffer, 0, len);
		        }
				
				// Convert to byte array
				result = bos.toByteArray();
				
				// Close streams
				fis.close();
				bos.close();
			} catch (Exception e) {
				PLUGIN.getLogger().log(Level.SEVERE, "Error: " + e.getMessage());
			}
		} else {
			// Could not find or access the file
			PLUGIN.getLogger().log(Level.SEVERE, "Error: Could not find the file: " + fileName);
		}
		
		return result;
	}
	/**
	 * Converts the given binary file into a byte array.
	 * 
	 * If the file was not found, or inaccessible, returns null.
	 * 
	 * @param file The file to convert.
	 * @return Byte array of the given file.
	 */
	public byte[] fileToBytes(File file) {
		byte[] result = null;
		
		if (file.exists()) {
			// If the file exists
			try {
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				
				// Read the file input stream and writes to the byte array output stream
				for (int len; (len = fis.read(buffer)) != -1;) {
					bos.write(buffer, 0, len);
		        }
				
				// Convert to byte array
				result = bos.toByteArray();
				
				// Close streams
				fis.close();
				bos.close();
			} catch (Exception e) {
				PLUGIN.getLogger().log(Level.SEVERE, "Error: " + e.getMessage());
			}
		} else {
			// Could not find or access the file
			PLUGIN.getLogger().log(Level.SEVERE, "Error: Could not find the file: " + file.getName());
		}
		
		return result;
	}
}
