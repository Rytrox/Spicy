package de.rytrox.spicy.sql;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for SQL-Connector
 *
 */
@FunctionalInterface
public interface SQL {

    /**
     * Prepares the MySQL-Connection for a new Statement. <br>
     * This Method will return a {@link QueryBuilder} so you need to decide which MySQL-Command you want to use. <br>
     * <br>
     * Normally you need to do something like that: <br>
     * <br>
     * <pre>
     *     <code>
     * mysql.prepare("SELECT * FROM ExampleTable WHERE id = ?", this.id)
     *                 .query(ExampleEntity.class)
     *                 .subscribe(List<ExampleEntity> entities -> {
     *                      // Do something with your entities here!
     *                 });
     *     </code>
     * </pre>
     * <br>
     * This method is fully asynchronous! If you want to use the Bukkit-API you may need a scheduler to sync this thread.
     *
     * @param statement the raw statement
     * @param args any arguments if required
     * @return a builder which will be used for connections later
     */
    @NotNull
    QueryBuilder prepare(@NotNull @Language("SQL") String statement, @NotNull Object... args);
}
