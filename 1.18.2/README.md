![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

# Spicy – Improve Spigot-Development
Spicy is an Utils-Library for Spigot-Developer to improve their Spigot-Plugins.

# Installation
To install and use Spicy in Maven, you simply need to include our Nexus-Repository and add the Library.

Currently, Spicy uses Java 17 and Spigot 1.18.2, so make sure to install that first.
```xml
<repositories>
    ...
    <repository>
        <id>rytrox</id>
        <url>https://nexus.rytrox.de/repository/maven-public/</url>
    </repository>
</repositories>

<dependencies>
    ...
    <dependency>
        <groupId>de.rytrox</groupId>
        <artifactId>spicy</artifactId>
        <version>1.0_1182</version>
    </dependency>
</dependencies>
```

If you are using NMS-Remapped you can use Spicy's remapping-features as well by changing your SpecialSource Configuration like this:
```xml
<plugins>
    ...
    <plugin>
        <groupId>net.md-5</groupId>
        <artifactId>specialsource-maven-plugin</artifactId>
        <version>1.2.3</version>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>remap</goal>
                </goals>
                <id>remap-obf</id>
                <configuration>
                    <srgIn>org.spigotmc:minecraft-server:1.18.2-R0.1-SNAPSHOT:txt:maps-mojang</srgIn>
                    <reverse>true</reverse>
                    <remappedDependencies>
                        <remappedDependency>
                            org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT:jar:remapped-mojang
                        </remappedDependency>
                        <remappedDependency>
                            de.rytrox:spicy:1.0_1182:jar
                        </remappedDependency>
                    </remappedDependencies>
                    <remappedArtifactAttached>true</remappedArtifactAttached>
                    <remappedClassifierName>remapped-obf</remappedClassifierName>
                </configuration>
            </execution>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>remap</goal>
                </goals>
                <id>remap-spigot</id>
                <configuration>
                    <inputFile>${project.build.directory}/${project.artifactId}-${project.version}-remapped-obf.jar</inputFile>
                    <srgIn>org.spigotmc:minecraft-server:1.18.2-R0.1-SNAPSHOT:csrg:maps-spigot</srgIn>
                    <remappedDependencies>org.spigotmc:spigot:1.18.2-R0.1-SNAPSHOT:jar:remapped-obf</remappedDependencies>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

<b>ATTENTION</b> <br>
If you want to use IntelliSense with Language Injection you need to clone this repository and include it in your `Project Structure`
You need to import this project as a Maven-Project.

If you want JetBrains to support this feature, <a href="https://youtrack.jetbrains.com/issue/IDEA-205659/Please-support-language-injection-for-external-libraries">click here!</a>

# Features
# 1. SQL ORM-like Library
Spicy includes an SQL-Module that allows to map tables and rows into Java-Object.
This feature can be used synchronously and asynchronously.

## 1.1 Select data

### 1.1.1 Create a database
To create a new database connection you simply have to create a MySQL / SQLite Object that is provided by Spicy.

```java
private MySQL relationalDatabase = new MySQL("hostname", 3306, "database", "username", "password");
private SQLite fileDatabase = new SQLite(Paths.get("path", "to", "your", "file.db").toFile());
```
You can also implement your own Driver based on your database.

Field `source` is a protected `DataSource` member of SQL.
It is used to create connections to your database

Here is an example for a PostgreSQL Driver:

```java


import java.sql.DriverManager;
import java.sql.SQLException;

import org.postgresql.Driver;
import org.postgresql.jdbc3.Jdbc3SimpleDataSource;

public class PostgreSQL extends SQL {

    static {
        try {
            // Make sure that you register your driver once
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PostgreSQL(String host, int port, String database, String username, String password) {
        // create your DataSource Implementation 
        this.source = new Jdbc3SimpleDataSource();
        this.source.setServerName(host);
        this.source.setDatabaseName(database);
        this.source.setPortNumber(port);
        this.source.setUser(username);
        this.source.setPassword(password);
    }
}
```

### 1.1.2 Select single values from your database
After creating a database you can select and access your database. 
You can select and map single rows into Java-Classes asynchronously or synchronously

Example: <br> 
Let's assume that our table is `Foo(id INTEGER NOT NULL, name VARCHAR(100))`. 
You can access this table asynchronously by using the database class
```java
database.prepare("SELECT name FROM Foo WHERE id = ?", 13)
        .query(String.class)
        .subscribe((names) -> {
            // Type of names is List<String>
            // This block will be executed asynchronously
        });
```

Alternatively, you can also select single data synchronously:
```java
List<String> names = database.prepare("SELECT name FROM Foo WHERE id = ?", 13)
        .querySync(String.class)
        .get();
```

### 1.1.3 Select entire custom Java-Objects from your database
You can also get entire custom Java-Objects from your database.
To access this feature, your custom class requires a constructor with ResultSet as only argument.

Example: <br>
Let's assume that our table is `Foo(id INTEGER NOT NULL, name VARCHAR(100))`.

First, you need to create a class Foo that has the required constructor Foo(ResultSet):

```java
import java.sql.ResultSet;

public class Foo {

    private final Integer id;
    private String name;

    /**
     * This constructor is required.
     * 
     * @param row the resultset. You need to assume that the index is on the correct row. 
     *            Ignore the row index
     */
    public Foo(ResultSet row) throws SQLException {
        this.id = row.getInt("id");
        this.name = row.getString("name");
    }
    
    ...
}
```

After that, you can select and map it from your database asynchronously:
```java
database.prepare("SELECT id, name FROM Foo")
        .query(Foo.class)
        .subscribe((foos) -> {
            // foos is List<Foo>
            // This codeblock will be executed asynchronously
        });
```

Alternatively, you can select and map it synchronously:
```java
List<Foo> foos = database.prepare("SELECT id, name FROM Foo")
        .querySync(Foo.class)
        .get();
```

### 1.1.4 Mapping Selects Into other 
If you select something from your database, and you want to change that before you continue (like Java-Streams do),
you can also map your rows or data into other classes.

This works for both single row data and custom Objects

Example:
Let's assume that our table is `Foo(id INTEGER NOT NULL, name VARCHAR(100))`. <br>
For mapping data asynchronously you can do:
```java
database.prepare("SELECT id FROM Foo")
        .query(Integer.class)
        .map((id) -> String.valueOf(id))
        .subscribe((ids) -> {
            // ids is now a List<String>
            // This codeblock will be executed asynchronously
        });
```
or if you want to map it synchronously:
```java
List<String> ids = database.prepare("SELECT id FROM Foo")
        .querySync(Integer.class)
        .map((id) -> String.valueOf(id))
        .get();
```

## 1.2 CRUD-Operation
CRUD Operations (Create, Remove, Update, Delete) can be executed with the library as well

### 1.2.1 Simple CRUD-Operation
To access and use CRUD features of your database you can use the methods provided by the QueryBuilder.

Example: <br>
Let's assume that our table is `Foo(id INTEGER NOT NULL, name VARCHAR(100))`. <br>
To execute a CRUD-Operation asynchronously, you can do:
```java
// CRUD data asynchronously
database.prepare("DELETE FROM Foo WHERE id = ?", 12)
        .executeUpdate();
```
or alternatively, you can modify it synchronously:
```java
database.prepare("DELETE FROM Foo WHERE id = ?", 12)
        .executeUpdateSync();
```

### 1.2.2 Chained Async CRUD-Operations
Sometimes you want to chain CRUD-Operations to be sure that they are executed in the correct order.
Since database operations should be async, you don't want to open a new thread. 
Luckily, <a href="https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html">CompletableFutures</a>
can chain async operations. Spicy uses this feature as well.

Example: <br>
Let's assume that our table is `Foo(id INTEGER NOT NULL, name VARCHAR(100))`. <br>
To chain operations we simply need to do:
```java
datasource.prepare("INSERT INTO Foo(id, name) VALUES (404, 'Not Found')")
        .executeUpdate()
        .thenRun(() -> {
            datasource.prepare("SELECT * FROM Foo WHERE id = 404")
                .query(Developer.class)
                .subscribe((res) -> {
                    // Both codeblocks will be executed asynchronously
                });
        });
```

## 1.3 Chaining multiple non-query statements
Sometimes, when you want to execute multiple statements in a row, 
you can chain the `prepare` method and executes multiple statements.

Those statements will be executed one after another in the order you chain them.

Example: <br>
Let's assume that our table is `Foo(id INTEGER NOT NULL, name VARCHAR(100))`. <br>
To chain statements we simply need to do:
```java
datasource.prepare("INSERT INTO Foo(id, name) VALUES (?, ?)", 101, "Bar") // Insert Bar with id 101
        .prepare("UPDATE FOO SET name = ? WHERE id = ?", "Updated", 101) // Change name of Bar to "Updated"
        .executeUpdate(); // execute in full async mode
```

<b> ATTENTION: </b> <br>
You cannot chain Query-Statements (SELECT), since SELECT works different from CREATE, UPDATE and DELETE

# 2. Colored Logging
With the ColoredLogger-Module you can use Minecraft-ColorCodes into your Plugin-Logger.
As you know, every plugin has its own logger. By using the Colored-Logger Module you can activate a Module,
that replaces Minecraft-ColorCodes with ANSI-Colors that can be read by the console. 

To activate it you simply have to do this inside your Main-Class:

```java
import de.rytrox.spicy.log.ColoredLogger;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    ...

    @Override
    public void onEnable() {
        ColoredLogger.enableColoredLogging('&', getLogger(), "&8[&6Your Prefix&8]");
 
        ...
    }
    
    ...
}
```
Now when you log something like `plugin.getLogger().log(Level.INFO, "&7Tell a &astory&7.");` it looks like 
"<span style="color: #555555">[</span><span style="color: #FFAA00">Your Plugin</span><span style="color: #555555">]</span> <span style="color: #AAAAAA">Tell a </span><span style="color: #55FF55">story</span><span style="color: #AAAAAA">.</span>"

# 3. Configurations
## 3.1 Creating custom configurations
If you created local default configurations inside your `src/main/resources` folder, and you want to copy this into your plugins folder, you can use the ConfigCreator.
The ConfigCreator is a class, that copies files from your jar into the plugins-folder or create new empty files. 

### 3.1.1 Copy custom file from jar into plugin folder
The ConfigCreator can copy default configurations into plugins folder.
If the file already exists inside the plugin folder, this method will do nothing.

Be sure to ignore the `src/main/resources` path inside your code, since all content inside this folder will be put in the root level of your jar

Usage:

```java

import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigCreator creator = new ConfigCreator(getDataFolder());

        // load src/main/resources/config.yml inside jar into /config.yml inside the plugins folder
        creator.copyDefaultFile(Paths.get("config.yml"));
    }
}
```

### 3.1.2 Create empty file
You can also create empty configurations.

Usage:

```java

import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigCreator creator = new ConfigCreator(getDataFolder());

        // Creates empty.yml inside plugins folder
        creator.createFile(Paths.get("empty.yml"));
    }
}
```

## 3.2 Json Configurations
Spicy provides a Bukkit configuration that can be saved to a JSON-File.

### 3.2.1 Loading a JSON-File
Spicy can load JSON by using the Constructor of JsonConfig. 
InputStreams, Strings and Files can be loaded by Spicy

Usage:

```java

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Load a JSON file
        JsonConfig fileConfig = new JsonConfig(new File(getDataFolder(), "config.json"));

        // Loads a JSON string
        JsonConfig stringConfig = new JsonConfig("{}");

        // Loads a JSON from InputStreams
        InputStream stream; // Let's assume that this is your InputStream that is initialized
        JsonConfig streamConfig = new JsonConfig(stream);
    }
}
```

### 3.2.2 Saving a JSON-File
JsonConfigs can be saved like any other Spigot Config by using the save Method

Usage:

```java

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    private JsonConfig config;

    @Override
    public void onEnable() {
        config = new JsonConfig(new File(getDataFolder(), "config.json"));
    }

    public void saveJsonConfig() {
        try {
            config.save(new File(getDataFolder(), "config.json"));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Cannot save config.json in data folder", e);
        }
    }
}
```

## 3.3 Yaml Configuration that uses UTF-8 standardformat
By default, Bukkit is using the normal Charset of your OS. 
On other machines that can be problematic if you're using special characters like 'ä', 'ö', 'ü', 'ß'.

### 3.3.1 Loading a UTF-File
Spicy can load in UTF-8 Charset by using the Constructor of UTFConfig.
InputStreams, Strings and Files can be loaded by Spicy

Usage:

```java

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Load a UTF file
        UTFConfig fileConfig = new UTFConfig(new File(getDataFolder(), "config.json"));

        // Loads a UTF string
        UTFConfig stringConfig = new UTFConfig("key: 'value'");

        // Loads a UTF from InputStreams
        InputStream stream; // Let's assume that this is your InputStream that is initialized
        UTFConfig streamConfig = new UTFConfig(stream);
    }
}
```

### 3.3.2 Saving a UTF-File
JsonConfigs can be saved like any other Spigot Config by using the save Method

Usage:

```java

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    private UTFConfig config;

    @Override
    public void onEnable() {
        config = new JsonConfig(new File(getDataFolder(), "config.yml"));
    }

    @Override
    public void saveConfig() {
        try {
            config.save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Cannot save config.yml in data folder", e);
        }
    }
}
```

## 3.4 NBT Configurations
If you are using the remapped version, you can use a configuration that binds NBT-Data into a valid configuration.
This increases readability and modification of NBT-Data

### 3.4.1 Creating a NBT-Configuration
NBT Configurations can be loaded or created.

Usage:

```java

import de.rytrox.spicy.item.NBTItemStacks;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // creates an empty NBTConfig
        NBTConfig empty = new NBTConfig();

        // Loads an NBTConfig from an ItemStack
        NBTConfig loaded = new NBTConfig(NBTItemStacks.getNBTTagCompound(new ItemStack()));

        // Loads an NBTConfig from an uncompressed file
        NBTConfig uncompressed = NBTConfig.fromUncompressedFile(new File(getDataFolder(), "config.dat"));

        // Loads an NBTConfig from a compressed file
        NBTConfig compressed = NBTConfig.fromCompressedFile(new File(getDataFolder(), "config-compressed.dat"));
    }
}
```

### 3.4.2 Saving a NBT-Configuration
NBT-Configurations can be saved into a CompoundTag, a compressed or uncompressed file.

Usage:

```java

import net.minecraft.nbt.CompoundTag;

import java.io.File;
import java.io.IOException;

public class Example {

    private NBTConfig nbtConfig = new NBTConfig();

    public void saveToFileCompressed(File file) {
        try {
            nbtConfig.saveCompressed(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void saveToFileUncompressed(File file) {
        try {
            nbtConfig.saveUncompressed(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CompoundTag saveToTagCompound() {
        return nbtConfig.save();
    }
}
```

# 4 ItemStack Library
Spicy provides a lot of useful methods for ItemStacks

## 4.1 ItemStackBuilder
The ItemStackBuilder simplified the creation and modification of ItemStacks.
It uses a Builder-Pattern to create an ItemStack.

### 4.1.1 Vanilla ItemStackBuilder
When you are done, you can use the ItemStackBuilder#toItemStack Method to build

```java
import de.rytrox.spicy.item.ItemStackBuilder;
import net.minecraft.world.level.material.Material;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class Foo {

    public void createItemStack() {
        ItemStack itemStack = new ItemStackBuilder(Material.AMETHYST) // Creates a Builder with Material Amethyst
                .amount(2) // Set amount to 2
                .displayName(ChatColor.translateAlternateColorCodes('&', "&5Amethyst")) // Set the Displayname of the ItemStack
                .enchantment(Enchantment.ARROW_KNOCKBACK, 2) // Enchants the ItemStack
                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE) // Sets ItemFlag
                .lore("Lore line 1", "Lore Line 2") // Sets the Lore
                .modelData(3) // Sets the ItemModel Data
                .toItemStack(); // Build the ItemStack
    }
}
```

### 4.1.2 NBT ItemStackBuilder
If you want to write NBT-Data as well, you can use the NBTItemStackBuilder-Class

```java
import de.rytrox.spicy.item.NBTItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Foo {

    public void createItemStack() {
        ItemStack itemStack = new NBTItemStackBuilder(Material.AMETHYST_BLOCK)
                .amount(2) // Set amount to 2
                .displayName(ChatColor.translateAlternateColorCodes('&', "&5Amethyst")) // Set the Displayname of the ItemStack
                .enchantment(Enchantment.ARROW_KNOCKBACK, 2) // Enchants the ItemStack
                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE) // Sets ItemFlag
                .lore("Lore line 1", "Lore Line 2") // Sets the Lore
                .modelData(3) // Sets the ItemModel Data
                .withNBTData("key", "value") // set string "value" in key "key"
                .withNBTData("key1", 1) // set value 1 in key "key1" 
                .toItemStack(); // Build the ItemStack
    }
}
```


## 4.2 ItemStack Utilities
Spicy provides util functions for ItemStacks

### 4.2.1 Base64 Decoding and Encoding
ItemStacks can be converted to a Base64 String, if you want to save it inside a Database or a file.

```java
import de.rytrox.spicy.item.ItemStacks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Foo {

    public void decodeAndEncodeBase64() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);

        // Creates a Base64 String of the ItemStack
        String base64Encoded = ItemStacks.encodeBase64(itemStack);
        
        ...
        
        // To decode an ItemStack
        ItemStack base64Decoded = ItemStacks.decodeBase64(base64Encoded);
    }
}
```

### 4.2.2 JSON Decoding and Encoding
Also, you can convert ItemStacks into JSON Strings instead

```java
import com.google.gson.JsonObject;
import de.rytrox.spicy.item.ItemStacks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Foo {

    public void decodeAndEncodeJSON() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);

        // Creates a Json String of the ItemStack
        JsonObject json = ItemStacks.encodeJson(itemStack);
        
        ...

        // To decode an ItemStack
        ItemStack jsonDecoded = ItemStacks.decodeJson(json);
    }
}
```

## 4.3 Get Customized Name
Minecraft provides a Default Name for ItemStacks and often developers thinks that ItemMeta#getDisplayName returns the default name.
Spicy has a built-in method to determinate the custom name.

First it checks if the ItemStack has a DisplayName and returns it. 
Secondly it uses the Localized Name or converts the ItemType to a String name

```java
import de.rytrox.spicy.item.ItemStacks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Foo {

    public void customizedName() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);

        String name = ItemStacks.getCustomizedName(itemStack);
    }
}
```

## 4.4 Bukkit-Copy of the ItemStack
CraftBukkit has a Method to create a Copy of an ItemStack. 
Sadly, you need to use Reflections to use it in multiple versions. 

Spicy has a Wrapper-Method to access this method regardless of Reflections

```java
import de.rytrox.spicy.item.ItemStacks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Foo {

    public void bukkitCopy() {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_SWORD);

        ItemStack copy = ItemStacks.asBukkitCopy(itemStack);
    }
}
```
