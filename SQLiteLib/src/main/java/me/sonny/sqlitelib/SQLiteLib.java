package me.sonny.sqlitelib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Arrays;
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

        databases.put("DEFAULT", defaultDb);

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[SQLiteLib] " + LibMessage.PLUGIN_ENABLED);
    }

    @Override
    public void onDisable() {
        // Close every DB inside the databases HashMap
        for(Map.Entry<String, SQLite> entry : databases.entrySet()) {
            entry.getValue().close();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[SQLiteLib] " + LibMessage.PLUGIN_DISABLED);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permissions
        if (!sender.hasPermission("sqlite.use")) {
            sender.sendMessage(ChatColor.RED + "[SQLiteLib] " + LibMessage.NO_PERMISSIONS);
            return false;
        }

        // Check command label
        if (!label.equalsIgnoreCase("sqlite")) {
            return false;
        }

        if (args.length == 0) {
            showCommands(sender);
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "close":
                    if (defaultDb.close()) {
                        if (sender instanceof Player) {
                            sender.sendMessage(LibMessage.CONNECTION_CLOSE_SUCCESS);
                        }
                    }
                    return true;
                case "check":
                    try {
                        if (defaultDb.connection() == null && defaultDb.connection().isClosed()) {
                            sender.sendMessage(LibMessage.STATUS_NO_CONNECTION);
                        } else {
                            sender.sendMessage(LibMessage.STATUS_CONNECTED);
                        }
                    } catch (SQLException e) {
                        sender.sendMessage("[SQLiteLib] " + LibMessage.STATUS_CHECK_FAILURE + ", Error Message: " + e.getMessage());
                    }
                    return true;
                default:
                    showCommands(sender);
                    return true;
            }
        } else {
            String statement = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            switch (args[0].toLowerCase()) {
                case "connect":
                    if (defaultDb.connect(statement)) {
                        if (sender instanceof Player) {
                            sender.sendMessage("[SQLiteLib] " + LibMessage.CONNECTION_ESTABLISHED + ", DB file location: " + statement);
                        }
                    } else {
                        sender.sendMessage("[SQLiteLib] " + LibMessage.CONNECTION_CLOSE_FAILURE + " See the console log for more information.");
                    }
                    return true;
                case "execute":
                    if (defaultDb.execute(statement)) {
                        if (sender instanceof Player) {
                            sender.sendMessage("[SQLiteLib] " + LibMessage.EXECUTE_SUCCESS);
                        }
                    } else {
                        sender.sendMessage("[SQLiteLib] " + LibMessage.EXECUTE_FAILURE + " See the console log for more information.");
                    }
                    return true;
                default:
                    showCommands(sender);
                    return true;
            }
        }

        return false;
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
     * Get the instance of this plugin. If the plugin was not set up, returns null.
     * This is the same action as: Bukkit.getPluginManager().getPlugin("SQLiteLib")
     *
     * @return The instance of this plugin (SQLiteLib).
     */
    public static SQLiteLib getSQLiteLib() {
        if (PLUGIN == null) {
            Bukkit.getConsoleSender().sendMessage("[SQLiteLib]" + LibMessage.PLUGIN_NOT_READY);
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
     * All DB names are saved as upper cases.
     *
     * If no DB matches, returns null.
     *
     * @param {String} dbName The name of the DB instance.
     * @return Returns a database that has the matching name.
     */
    public SQLite DB(String dbName) {
        dbName = dbName.toUpperCase();

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
        dbName = dbName.toUpperCase();

        // If there already is a DB with the given name, then close the previous DB connection.
        if (databases.containsKey(dbName)) {
            databases.get(dbName).close();
        }

        SQLite db = new SQLite();

        databases.put(dbName, db); // Store the SQLite DB instance inside the HashMap.

        return db;
    }
    public void removeDB(String dbName) {
        dbName = dbName.toUpperCase();

        // If there already is a DB with the given name, then close the DB connection, and remove it from the map.
        if (databases.containsKey(dbName)) {
            databases.get(dbName).close();
            databases.remove(dbName);
        } else {
            PLUGIN.getLogger().log(Level.SEVERE, "Error: Could not find a DB that matches the name!");
        }
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
    public byte[] fileToBytes(String fileName) {
        File file = new File(fileName);

        return fileToBytes(file);
    }
}
