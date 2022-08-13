**Still setting up**

# SQLiteLib
<img src="images\sqlitelib_logo.png" width=50% height=50%>

SQLiteLib is a library plugin that provides an easy way to use the SQLite database on Minecraft servers.

Most Minecraft servers store their user data on a single YAML file or use MySQL as their DBMS. However, storing data on a YAML file is unsafe and performance inefficient, and MySQL might be a bit overwhelming to install and set up configurations. If you are a Minecraft server admin who is looking for a fast, light, and zero-configuration database, SQLite is a good solution. SQLiteLib is a Bukkit plugin that helps you to easily use SQLite databases on your Minecraft server.

I decided to write this plugin after finding out about SQLiteLib written by pablo67340 (https://github.com/pablo67340/SQLiteLib) which is now out of support. This plugin statically links SQLite JDBC Driver 3.39.2.0 written by xerial (https://github.com/xerial/sqlite-jdbc).

**Do I need to install SQLite before using this plugin?**

***No. You don't.***

Due to the compact, serveless architecture of SQLite, ***you actually don't even need to install SQLite*** before using this plugin. The JDBC driver inside the plugin handles SQLite DB files.

If you need a GUI SQLite database editor, you can try: [DB Browser for SQLite](https://sqlitebrowser.org)

# Usage
Some examples are written in TriggerReactor script language since that was my initial reason of writing this library. However, translating the script to Java language should be easy.

**What is TriggerReactor?** [TriggerReactor](https://www.spigotmc.org/resources/triggerreactor-script-for-everything.40987/) is a powerful scripting engine that provides a convenient Minecraft server scripting environment. The engine supports amazing features such as importing Bukkit API methods and third-party plugin access. BTW, I am not paid to advertise this plugin.

**Linking Your Plugin with SQLiteLib**
1. Download SQLiteLib.jar and put it in your **plugins** folder (just like how you add any other plugins to your server).
2. Add SQLiteLib.jar to your Java project build path.

3. Then, add `SQLiteLib` as a dependency to your plugin.yml file:
```yml
depend: [SQLiteLib]
```

**Example plugin.yml:**
```yml
main: me.sonny.SQLiteLibTester.TesterMain
name: SQLiteLibTester
version: 1.0.0
description: A tester plugin for SQLiteLib.
author: Sonny Kim
depend: [SQLiteLib]

commands:
  sqltest:
    description: SQLite testing commands.
```


**example_01 (Java)**
```java
SQLiteLib sqliteLib = (SQLiteLib) Bukkit.getPluginManager().getPlugin("SQLiteLib");

if (sqliteLib != null) {
    SQLite db = sqliteLib.DB(); // Get default SQLite DB instance

    // Create a connection to a DB file that is located at "<root>/plugins/TriggerReactor/SavedData/DB/test.db".
    // The <root> is the location of your bukkit jar file.
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

**example_01 (TR)**
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


# Future Updates
2. Image -> BLOB write support (partially implemented)
3. Configurations
