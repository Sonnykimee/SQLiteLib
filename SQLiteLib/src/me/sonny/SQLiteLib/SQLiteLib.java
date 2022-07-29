package me.sonny.SQLiteLib;

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
	
	private SQLite sqlite; // Use this as the default DB.
	
	private Map<String, SQLite> databases; // supports to use multiple DBs at the same time.
	
	private static SQLiteLib PLUGIN;
	
	@Override
	public void onEnable() {
		databases = new HashMap<>();
		sqlite = new SQLite();
		PLUGIN = this;
		
		Bukkit.getConsoleSender().sendMessage("[SQLiteLib] Plugin Enabled!");
	}
	
	@Override
	public void onDisable() {
		sqlite.close();
		
		Bukkit.getConsoleSender().sendMessage("[SQLiteLib] Plugin Disabled! You need to open databases again!");
	}
	
	/**
	 *  Commands can only access one database (as of 0.1.0).
	 *  Accessing database using commands is not recommended.
	 *  SQLiteLib was meant to be an API for other plugins and scripts.
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("sqlite.use")) {
			if (label.equalsIgnoreCase("sqlite")) {
				if (args.length == 0) {
					// No argument
					showCommands(sender);
				} else if (args.length == 1) {
					// One argument
					if (args[0].equalsIgnoreCase("close")) {
						if (sqlite.close()) {
							if (sender instanceof Player) {
								sender.sendMessage("[SQLiteLib] DB connection closed!");
							}
						}
						return true;
					} else if (args[0].equalsIgnoreCase("check")) {
						try {
							if (sqlite.getConnection() == null) {
								sender.sendMessage("[SQLiteLib] DB connection is NULL!");
							} else if (sqlite.getConnection().isClosed()) {
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
					// More than one argument
					if (args[0].equalsIgnoreCase("connect")) {
						String url = "";
						for (int i=1; i<args.length; i++) {
							url += args[i] + " ";
						}
						url.trim();
						
						if (sqlite.connect(url)) {
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
						statement.trim();
						
						if (sqlite.execute(statement)) {
							if (sender instanceof Player) {
								sender.sendMessage("[SQLiteLib] Query statement executed!");
							}
						}
						
						return true;
					} else {
						// No matching argument
						showCommands(sender);
					}
				}
			}
		}
		
		return false;
	}
	
	public static SQLiteLib getPlugin() {
		return PLUGIN;
	}
	
	public SQLite getDB() {
		return sqlite;
	}
	
	/**
	 * Returns a database that has the matching name, If there is not, returns null.
	 * @param {String} dbName Name of the database to get.
	 * @return Returns a database that has the matching name, If there is not, returns null.
	 */
	public SQLite getDB(String dbName) {
		if (databases.containsKey(dbName)) {
			return databases.get(dbName);
		}
		
		PLUGIN.getLogger().log(Level.SEVERE, "Error: Could not find a DB that matches the name!");
		
		return null;
	}
	
	/**
	 * Creates a new database that has given name.
	 * @param {String} dbName Set a name for the new database.
	 * @return Returns newly created database.
	 */
	public SQLite createDB(String dbName) {
		SQLite db = new SQLite();
		
		databases.put(dbName, db);
		
		return db;
	}
	
	private void showCommands(CommandSender sender) {
		// Commands
		sender.sendMessage(ChatColor.GRAY + "----[" + ChatColor.WHITE + " SQLiteLib Commands " + ChatColor.GRAY + "]----");
		sender.sendMessage(ChatColor.GOLD + "/sqlite connect <DB file location> " + ChatColor.YELLOW + "[username] [password]" + ChatColor.WHITE + " - Opens a DB connection.");
		sender.sendMessage(ChatColor.GOLD + "/sqlite execute <statement>" + ChatColor.WHITE + " - Execute the given query statement.");
		sender.sendMessage(ChatColor.GOLD + "/sqlite close" + ChatColor.WHITE + " - Close current database connection.");
		sender.sendMessage(ChatColor.GOLD + "/sqlite check" + ChatColor.WHITE + " - Check the status of current connection.");
		sender.sendMessage(ChatColor.GRAY + "---------------------------");
	}
}
