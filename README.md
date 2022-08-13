**Still setting up**

- [SQLiteLib](#sqlitelib)
  * [Usage](#usage)
    + [Linking Your Plugin Project with SQLiteLib](#linking-your-plugin-project-with-sqlitelib)
    + [Accessing SQLiteLib instance](#accessing-sqlitelib-instance)
    + [Accessing default DB and Creating a Connection](#accessing-default-db-and-creating-a-connection)
    + [Using more than one DB](#using-more-than-one-db)
    + [Executing a Statement](#executing-a-statement)
    + [Fetching Data from DB](#fetching-data-from-db)
      - [1. Using Cursor (recommended)](#1-using-cursor--recommended-)
      - [2. Using fetch()](#2-using-fetch--)
  * [Examples](#examples)
  * [Future Updates](#future-updates)

# SQLiteLib
<img src="images\sqlitelib_logo.png" width=50% height=50%>

SQLiteLib is a library plugin that provides an easy way to use the SQLite database on Minecraft servers.

Most Minecraft servers store their user data on a single YAML file or use MySQL as their DBMS. However, storing data on a YAML file is unsafe and performance inefficient, and MySQL might be a bit overwhelming to install and set up configurations. If you are a Minecraft server admin who is looking for a fast, light, and zero-configuration database, SQLite is a good solution. SQLiteLib is a Bukkit plugin that helps you to easily use SQLite databases on your Minecraft server.

I decided to write this plugin after finding out about SQLiteLib written by pablo67340 (https://github.com/pablo67340/SQLiteLib) which is now out of support. This plugin statically links SQLite JDBC Driver 3.39.2.0 written by xerial (https://github.com/xerial/sqlite-jdbc).

**Do I need to install SQLite before using this plugin?**

***No. You don't.***

Due to the compact, serveless architecture of SQLite, ***you actually don't even need to install SQLite*** before using this plugin. The JDBC driver inside the plugin handles SQLite DB files.

If you need a GUI SQLite database editor, you can try: [DB Browser for SQLite](https://sqlitebrowser.org)

## Usage
Some examples are written in TriggerReactor script language since that was my initial reason of writing this library. However, translating the script to Java language should be easy.

***What is TriggerReactor?*** [TriggerReactor](https://www.spigotmc.org/resources/triggerreactor-script-for-everything.40987/) is a powerful scripting engine that provides a convenient Minecraft server scripting environment. The engine supports amazing features such as importing Bukkit API methods and third-party plugin access (I'm not paid to advertise this plugin).

### Linking Your Plugin Project with SQLiteLib ###
1. Download **SQLiteLib.jar** and put it in your **plugins** folder (just like how you add any other plugins to your server).
2. Add **SQLiteLib.jar** to your Java project build path.
3. Then, add `SQLiteLib` as a dependency to your project's **plugin.yml** file:
```yml
depend: [SQLiteLib]
```

### Accessing SQLiteLib instance ###
Use the following code to access the library.
```java
public static SQLiteLib sqliteLib;

@Override
public void onEnable() {
    sqliteLib = SQLiteLib.getSQLiteLib();

    // VVVV You can also use the code below. They are almost the same. VVVV
    // sqliteLib = (SQLiteLib) Bukkit.getPluginManager().getPlugin("SQLiteLib");
}
```

### Accessing default DB and Creating a Connection ###
Once, your project is properly linked to SQLiteLib, you can access its classes and methods. Use the below code to access the default DB and create a connection to a DB file.
```java
if (sqliteLib != null) {
    SQLite db = sqliteLib.DB(); // Getting the default SQLite DB instance.
    
    // Create a connection to a DB file that is located at "<root>/plugins/TriggerReactor/SavedData/DB/test.db".
    // The <root> is the location of your bukkit jar file.
    // If the file does not exist, the plugins will create a new DB file.
    db.connect( "plugins/TriggerReactor/SavedData/DB/test.db" );
}
```
`DB()` method returns a default SQLite DB instance that is pre-initialized.

To create a connection to a DB file, use `connect(fileName)`. **fileName** parameter is the path of your DB file location from the root of your server folder (where the bukkit jar file is). In this example, the path is "plugins/TriggerReactor/SavedData/DB/test.db" from the root of my server folder. If the DB file does not exist, it will automatically create a new DB file. **Note that the plugin does not create a new folder, though. You must pass an existing directory.**

### Using more than one DB ###
You can create more than just the default DB using below code:
```java
// Create a new SQLite DB using createDB(dbName).
SQLite myNewDB = sqliteLib.createDB("MyNewDB");
					
// You can always use DB(dbName) to get the instance of the created DB.
myNewDB = sqliteLib.DB("MyNewDB");
					
myNewDB.connect( "plugins/TriggerReactor/SavedData/DB/mytest2.db" );
```
**dbName** parameter is a String, and you can give any name to it. However, be careful not to use a duplicating name, the new one will replace the pre-existing one.

### Executing a Statement ###
You can execute SQLite statements using `execute(statement)` method.

Below code creates a new DB Table **PERSON** that has **NAME** and **AGE** as its values, then inserts an item that has name Sonny, and 13 years old:
```java
if (db.execute( "CREATE TABLE IF NOT EXISTS PERSON (NAME TEXT, AGE INTEGER)" )) {
    // Successfully executed the statement
    db.execute( "INSERT OR REPLACE INTO PERSON VALUES ('Sonny', 13)" );
} else {
    // Failed to execute the statement
    // ...
}
```

### Fetching Data from DB ###
There are two ways to fetch data: Using `Cursor` class, or using `fetch()` method.

#### 1. Using Cursor (recommended) ####
`Cursor` provides a convenient way to get data without having to parse the data by yourself.

Here's an example of using a `Cursor`. **Let's assume that PERSON table has three items: (Sonny, 13), (Tonny, 25), and (Ronny, 31):**
```java
// Get all items from Person table.
if (db.execute( "SELECT * FROM PERSON" )) {
    Cursor cur = db.cursor(); // Get Cursor instance
    
    // Iterate through the read data
    while(cur.next()) {
    	// Recommended: use the field name of the column as parameter to get the stored value.
    	Bukkit.getConsoleSender().sendMessage("Name: " + cur.getString("NAME") + ", Age: " + cur.getInt("AGE"));
	
	// You can also use column index (starting at 0), but this is unsafe because the order of column is not always guaranteed.
	// Bukkit.getConsoleSender().sendMessage("Name: " + cur.getString(0) + ", Age: " + cur.getInt(1));
    }
}
```
<p>
Result:<br />
Name: Sonny, Age: 13<br />
Name: Tonny, Age: 25<br />
Name: Ronny, Age: 31<br />
</p>

If there is a next row, `next()` method moves the position of the pointer to the next row, and returns **true**. Otherwise **false**. The position starts at -1. You can reset the position of the pointer using `beforeFirst()` method which will move it back to -1.

Using `getInt(column name)`, you can get the the current item's that is stored in the specified column as an intger type. You can also use column index number as parameter, but unsafe since the order of column is not always guaranteed.

Cursor also contains `getFloat(column)`, `getDouble(column)`, `getString(column)`, and `getBool(column)` methods. **Note that there actually is no Boolean in SQLite. In SQLite, Boolean is an Integer. The program understands the keywords True and False but they are simply placeholders for 1 and 0.**

#### 2. Using fetch() ####
`fetch()` method returns the data as List<List<Object>>.

Since the data is an `Object` instance, you need to manually parse Object into a specific data type.

Here's an example of how to use `fetch()`.
```java
// Get all items from Person table.
db.execute( "SELECT * FROM PERSON" );

// fetch data
List<List<Object>> data = db.fetch();

// Loop through the data
for (int i=0; i=data.size(); i++) {
    String name = data.get(i).get(0).toString(); // Get NAME
    int age = (Integer) data.get(i).get(1); // Get AGE

    Bukkit.getConsoleSender().sendMessage(name + "'s age: " + age);
}
```
<p>
Result:<br />
Sonny's age: 13<br />
Tonny's age: 25<br />
Ronny's age: 31<br />
</p>

FYI, you should not assume that the data will always be ordered unless you specify. For instance, returning any of Sonny-Tonny-Ronny, Tonny-Sonny-Ronny, Ronny-Tonny-Sonny for "SELECT * FROM PERSON" statement is practically not wrong. Read about [ORDER BY](https://www.sqlitetutorial.net/sqlite-order-by/) SQL command to learn how to specify the order of the data.

You can also get a specific row using `fetchRow(index)`:
```java
db.execute( "SELECT * FROM PERSON WHERE NAME='Sonny'" );

<List<Object>> sonny = db.fetchRow(0);
```

## Examples ##
Basics: [example_01](/examples/example_01.md)

## Future Updates
1. Image -> BLOB write support (now partially implemented)
2. Configurations
3. Statement class (wrapper class of PreparedStatement) that allows users to use setString(index, value) method.
