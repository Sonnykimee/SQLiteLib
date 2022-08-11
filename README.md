**Still setting up**

# SQLiteLib
<img src="images\sqlitelib_logo.png" width=50% height=50%>

SQLiteLib is a library plugin that provides an easy way to use SQLite database on Minecraft servers.

I decided to write this plugin after studying SQLiteLib written by pablo67340 (https://github.com/pablo67340/SQLiteLib) which is now out of support. This plugin statically imports SQLite JDBC Driver 3.39.2.0 written by xerial (https://github.com/xerial/sqlite-jdbc) which is again, a fork of Zentus' SQLite JDBC Driver. I copied some of Python JayDeBeApi usage to make the usage as easy as possible.

# Usage
**NOTE:** Some examples are written in TriggerReactor script language since that was my initial reason of writing this library. However, translating the script to Java language should be easy.

**example_01.trg**

Below code is written in TriggerReactor script language.
```java
// IMPORT org.bukkit.Bukkit

sqliteLib = plugin("SQLiteLib") // Same as Bukkit.getPluginManager().getPlugin("SQLiteLib")

IF sqliteLib
    db = sqliteLib.DB() // Get default SQLite DB instance

    // Create a connection to a DB file that is located at "<root>/plugins/SavedData/DB/test.db".
    // The <root> is the location of your bukkit jar file.
    db.connect( "plugins/SavedData/DB/test.db" )

    // Create a table
    db.execute( "CREATE TABLE IF NOT EXISTS CITIZENS (CIN INTEGER PRIMARY KEY NOT NULL, NAME VARCHAR(20) NOT NULL, CREDIT INTEGER NOT NULL)" )

    // Insert data
    db.execute( "INSERT OR IGNORE INTO CITIZENS VALUES (0, 'Sonny', 2500)" )
    db.execute( "INSERT OR IGNORE INTO CITIZENS VALUES (1, 'Tonny', 1500)" )
    db.execute( "INSERT OR IGNORE INTO CITIZENS VALUES (2, 'Johnny', 800)" )
    
    // Fetch data
    db.execute( "SELECT * FROM CITIZENS WHERE NAME = 'Sonny'" )
    sonny = db.fetch()
    credit = sonny.get(0).get(2)
    #MESSAGE "Social Credit: " + credit

    // Use the Cursor class to fetch data
    db.execute( "SELECT * FROM CITIZENS" )

    cur = db.cursor() // Get Cursor instance

    // Iterate
    WHILE cur.next()
        #MESSAGE "Name: " + cur.getString("NAME") + ", Credit: " +  cur.getInt("CREDIT")
    ENDWHILE

    db.close() // Close DB
ENDIF
```

**What is TriggerReactor?** TriggerReactor is a powerful scripting engine that provides a convenient Minecraft server scripting environment. The engine provides amazing features such as importing Bukkit API methods and third-party plugin access. BTW, I am not paid to advertise this plugin.

# Future Updates
2. Image -> BLOB write support (partially implemented)
3. Configurations
