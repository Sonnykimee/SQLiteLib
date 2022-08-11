**Still setting up**

# SQLiteLib
![alt text](images/sqlitelib_logo.png "SQLiteLib Logo")
A library plugin that provides an easy way to use SQLite DB on Minecraft Servers.

I decided to write this plugin after finding out about SQLiteLib by pablo67340 (https://www.spigotmc.org/resources/triggerreactor-script-for-everything.40987/ https://www.spigotmc.org/resources/sqlitelib.46801/) which I believe now it's out of support. Also, this plugin imports SQLite JDBC 3.36.0.3 by xerial (https://github.com/xerial/sqlite-jdbc). I also studied Python JDBC to keep the use of plugin easy.

# How to Use
**NOTE:** Some examples are written in TriggerReactor script language since that was my main purpose of writing this plugin; to use SQLite with TriggerReactor. However, translating the script to Java should be very easy.

**What is TriggerReactor?** TriggerReactor is a powerful scripting engine that provides a convenient Minecraft server scripting environment. The engine provides amazing features such as importing Bukkit API methods and third-party plugin access. BTW, I am not paid to advertise this plugin.

# Future Updates
1. Implement a Cursor class that provides a better way to fetch data without parsing types, and allows using column names as parameter instead of column index numbers.
2. Image -> BLOB write support
3. Configuration to set default databases to connect when the plugin starts.
