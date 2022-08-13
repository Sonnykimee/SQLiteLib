**example_01 (Java)**
```java
SQLiteLib sqliteLib = (SQLiteLib) Bukkit.getPluginManager().getPlugin("SQLiteLib");

if (sqliteLib != null) {
    SQLite db = sqliteLib.DB(); // Get default SQLite DB instance

    // Create a connection to a DB file that is located at "<root>/plugins/TriggerReactor/SavedData/DB/test.db".
    // The <root> is the location of your bukkit jar file.
    // If the file does not exist, the plugins will create a new DB file.
    db.connect( "plugins/TriggerReactor/SavedData/DB/test.db" );

    // Create a table
    db.execute( "CREATE TABLE IF NOT EXISTS CITIZENS (CIN INTEGER PRIMARY KEY NOT NULL, NAME VARCHAR(20) NOT NULL, CREDIT INTEGER NOT NULL)" );

    // Insert data
    db.execute( "INSERT OR REPLACE INTO CITIZENS VALUES (0, 'Sonny', 2500)" );
    db.execute( "INSERT OR REPLACE INTO CITIZENS VALUES (1, 'Tonny', 1500)" );
    db.execute( "INSERT OR REPLACE INTO CITIZENS VALUES (2, 'Johnny', 800)" );
    
    // Fetch data
    db.execute( "SELECT * FROM CITIZENS WHERE NAME = 'Sonny'" );
    List<List<Object>> sonny = db.fetch();
    int credit = (Integer) sonny.get(0).get(2);
    Bukkit.getConsoleSender().sendMessage("Credit: " + credit);

    // Use the Cursor class to fetch data
    db.execute( "SELECT * FROM CITIZENS" );

    Cursor cur = db.cursor(); // Get Cursor instance

    // Iterate
    while (cur.next()) {
        Bukkit.getConsoleSender().sendMessage("Name: " + cur.getString("NAME") + ", Credit: " + cur.getInt("CREDIT"));
    }

    db.close(); // Close DB
}
```
	
**example_01 (TriggerReactor)**
```java
sqliteLib = plugin("SQLiteLib") // Same as Bukkit.getPluginManager().getPlugin("SQLiteLib")

IF sqliteLib
    db = sqliteLib.DB() // Get default SQLite DB instance

    // Create a connection to a DB file that is located at "<root>/plugins/TriggerReactor/SavedData/DB/test.db".
    // The <root> is the location of your bukkit jar file.
    db.connect( "plugins/TriggerReactor/SavedData/DB/test.db" )

    // Create a table
    db.execute( "CREATE TABLE IF NOT EXISTS CITIZENS (CIN INTEGER PRIMARY KEY NOT NULL, NAME VARCHAR(20) NOT NULL, CREDIT INTEGER NOT NULL)" )

    // Insert data
    db.execute( "INSERT OR REPLACE INTO CITIZENS VALUES (0, 'Sonny', 2500)" )
    db.execute( "INSERT OR REPLACE INTO CITIZENS VALUES (1, 'Tonny', 1500)" )
    db.execute( "INSERT OR REPLACE INTO CITIZENS VALUES (2, 'Johnny', 800)" )
    
    // Fetch data
    db.execute( "SELECT * FROM CITIZENS WHERE NAME = 'Sonny'" )
    sonny = db.fetch()
    credit = sonny.get(0).get(2)
    #MESSAGE "Credit: " + credit

    // Use the Cursor class to fetch data
    db.execute( "SELECT * FROM CITIZENS" )

    cur = db.cursor() // Get Cursor instance

    // Iterate
    WHILE cur.next()
        #MESSAGE "Name: " + cur.getString("NAME") + ", Credit: " + cur.getInt("CREDIT")
    ENDWHILE

    db.close() // Close DB
ENDIF
```