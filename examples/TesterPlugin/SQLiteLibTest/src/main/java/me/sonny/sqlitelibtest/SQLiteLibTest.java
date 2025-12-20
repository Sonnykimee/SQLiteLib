package me.sonny.sqlitelibtest;

import me.sonny.sqlitelib.Cursor;
import me.sonny.sqlitelib.PrepStatement;
import me.sonny.sqlitelib.SQLite;
import me.sonny.sqlitelib.SQLiteLib;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class SQLiteLibTest extends JavaPlugin {

    public static SQLiteLib sqliteLib;

    @Override
    public void onEnable() {
        sqliteLib = SQLiteLib.getSQLiteLib();

        // Alternatively, you can also use the code below:
        // sqliteLib = (SQLiteLib) Bukkit.getPluginManager().getPlugin("SQLiteLib");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("sqltest")) {
            if (args.length == 0) {
                sender.sendMessage("**Run '/sqltest 1' first before running any other commands!**");
                sender.sendMessage("/sqltest 1 - Creates a new DB file (mydb.db) at /plugins folder.");
                sender.sendMessage("/sqltest 2 - Creates a new table called 'Person' and inserts 6 items.");
                sender.sendMessage("/sqltest 3 - Uses a prepared statement to safely update a BLOB column.");
                sender.sendMessage("/sqltest 4 - Query and display all items from the PERSON table using Cursor.");
            }

            if (args.length == 1) {
                /*
                 * Test #1:
                 * 1. Creates a new database instance with the logical name "MYDB".
                 * 2. Establishes a connection to the database file located at "/plugins/mydb.db".
                 */
                if (args[0].equalsIgnoreCase("1")) {
                    // Create a new SQLite DB using createDB(dbName).
                    SQLite myDb = sqliteLib.createDB("MYDB");

                    myDb.connect( "plugins/mydb.db" );

                    sender.sendMessage("Created a new database file '/plugins/mydb.db' and established connection.");
                    return true;
                }

                /*
                 * Test #2:
                 * 1. Create a new table called "Person" with columns: id, username, and age.
                 * 2. Insert 6 hardcoded items into the table.
                 *
                 * WARNING:
                 * This example uses raw string concatenation for SQL queries which can be vulnerable to SQL injection.
                 */
                if (args[0].equalsIgnoreCase("2")) {
                    // You can always use DB(dbName) to get the instance of the created DB.
                    SQLite myDb = sqliteLib.DB("MYDB");

                    String statement = """
										CREATE TABLE IF NOT EXISTS PERSON (
											PID INTEGER PRIMARY KEY AUTOINCREMENT,
											NAME VARCHAR(20) NOT NULL,
											AGE INTEGER NOT NULL,
											PROFILE BLOB);
									   """;

                    // Create a new table Person(PID, NAME, AGE, PROFILE).
                    if (myDb.execute(statement)) {
                        // Add below items to the database.
                        myDb.execute( "INSERT OR REPLACE INTO PERSON (NAME, AGE, PROFILE) VALUES ('Sonny', 13, NULL);" );
                        myDb.execute( "INSERT OR REPLACE INTO PERSON (NAME, AGE, PROFILE) VALUES ('Annie', 23, NULL);" );
                        myDb.execute( "INSERT OR REPLACE INTO PERSON (NAME, AGE, PROFILE) VALUES ('Tonny', 25, NULL);" );
                        myDb.execute( "INSERT OR REPLACE INTO PERSON (NAME, AGE, PROFILE) VALUES ('Johnny', 42, NULL);" );
                        myDb.execute( "INSERT OR REPLACE INTO PERSON (NAME, AGE, PROFILE) VALUES ('Kenny', 31, NULL);" );
                        myDb.execute( "INSERT OR REPLACE INTO PERSON (NAME, AGE, PROFILE) VALUES ('Timmy', 10, NULL);" );

                        sender.sendMessage("Created a table 'Person' and inserted 6 items.");
                    }

                    return true;
                }

                /*
                 * Test #3:
                 * 1. Converts 'cat.jpg' from the server root directory to a byte array.
                 * 2. Uses a prepared statement to safely update a BLOB column (prevents SQL injection).
                 * 3. Updates the PROFILE column (BLOB type) for 'Sonny' in the PERSON table.
                 *
                 * NOTE:
                 * Place an image file named "cat.jpg" in the server's root directory.
                 * (same location as your server.jar / bukkit.jar)
                 */
                if (args[0].equalsIgnoreCase("3")) {
                    // You can always use DB(dbName) to get the instance of the created DB.
                    SQLite myDb = sqliteLib.DB("MYDB");

                    // convert the image file to bytes array.
                    byte[] catImage = sqliteLib.fileToBytes("cat.jpg");
                    int imageHash = Arrays.hashCode(catImage); // calculate hash

                    // Get PreparedStatement instance.
                    PrepStatement statement = myDb.createPrepStatement( "UPDATE PERSON SET PROFILE = ? WHERE NAME = 'Sonny'" );

                    // write bytes array into the PreparedStatement.
                    // The index starts from 1.
                    statement.setBytes(1, catImage);

                    myDb.prepare(statement);

                    myDb.executePrepStatement();

                    sender.sendMessage("Successfully stored 'cat.jpg' (hashcode: " +  imageHash + ") as BLOB in Sonny's PROFILE picture.");

                    return true;
                }

                /*
                 * Test #4:
                 * 1. Query and display all items from the PERSON table using Cursor.
                 */
                if (args[0].equalsIgnoreCase("4")) {
                    SQLite myDb = sqliteLib.DB("MYDB");

                    if (myDb.execute("SELECT PID, NAME, AGE, PROFILE FROM PERSON")) {
                        Cursor cur = myDb.cursor();

                        if (!cur.next()) {
                            sender.sendMessage("No records found in the PERSON table.");
                        } else {
                            sender.sendMessage("==== PERSON Table Contents ====");
                            do {
                                int pid = cur.getInt("PID");
                                String name = cur.getString("NAME");
                                int age = cur.getInt("AGE");
                                byte[] profileData = cur.getBytes("PROFILE");

                                int profileHash;
                                if (profileData != null) {
                                    profileHash = Arrays.hashCode(profileData);
                                } else {
                                    profileHash = 0;
                                }

                                sender.sendMessage(String.format("§aID: %d | Name: %s | Age: %d | Profile Hash: %d", pid, name, age, profileHash));
                            } while (cur.next());
                            sender.sendMessage("===============================");
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
