![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

# Spicy – Improve Spigot-Development
Spicy is an Utils-Library for Spigot-Developer to improve their Spigot-Plugins.

# Installation
To install and use Spicy in Maven, you simply have to download this repository and install it on your local 
Maven-Repository with `mvn clean install`.

Currently, Spicy uses Java 17 and Spigot 1.18.2, so make sure to install that first.

After the installation you can use it like any other Maven-Repository:
```xml
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

Here is an example for a PostgreSQL Driver:

```java
import de.rytrox.spicy.sql.SQL;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.postgresql.Driver;
import org.postgresql.jdbc3.Jdbc3SimpleDataSource;

public class PostgreSQL implements SQL {

    static {
        try {
            // Make sure that you register your driver once
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private final DataSource source;

    public PostgreSQL(String host, int port, String database, String username, String password) {
        // create your DataSource Implementation 
        source = new Jdbc3SimpleDataSource();
        source.setServerName(host);
        source.setDatabaseName(database);
        source.setPortNumber(port);
        source.setUser(username);
        source.setPassword(password);
    }

    @Override
    public @NotNull QueryBuilder prepare(@NotNull @Language("PostgreSQL") String statement, Object... args) {
        return new QueryBuilder(source, statement, args);
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
    public Foo(ResultSet row) {
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

