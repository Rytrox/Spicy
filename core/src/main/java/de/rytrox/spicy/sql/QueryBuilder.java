package de.rytrox.spicy.sql;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryBuilder {

    private static final Logger logger = Logger.getLogger("SQL");

    private final DataSource source;
    private final Queue<StatementWrapper> statements = new LinkedBlockingQueue<>();

    public QueryBuilder(@NotNull DataSource source, @NotNull String query, Object... args) {
        this.source = source;

        this.statements.add(new StatementWrapper(query, args));
    }

    @NotNull
    private <T> List<T> convertResultSetToEntities(@NotNull Class<T> targetClass,
                                                   @NotNull ResultSet resultSet) throws SQLException {
        List<T> entities = new LinkedList<>();
        try {
            while(resultSet.next()) {
                entities.add(ConstructorUtils.invokeConstructor(targetClass, resultSet));
            }
        } catch (NoSuchMethodException e) {
            // try to convert it manually if it's just one column
            if(resultSet.getMetaData().getColumnCount() == 1) {
                do { // do while, because pointer was already moved in try-block
                    try {
                        entities.add(resultSet.getObject(1, targetClass));
                    } catch(SQLFeatureNotSupportedException exception) {
                        // SQLite is a pain...
                        entities.add((T) resultSet.getObject(1));
                    }
                } while(resultSet.next());
            } else {
                logger.log(Level.SEVERE,
                        String.format(
                                "Cannot convert ResultSet into Class %s. Be sure the constructor %s(ResultSet) exist and has public accessor.",
                                targetClass.getName(),
                                targetClass.getName()
                        ), e);

                throw new SQLException(e);
            }
        } catch (ReflectiveOperationException e) {
            logger.log(Level.SEVERE,
                    String.format(
                            "Cannot convert ResultSet into Class %s. Be sure the constructor %s(ResultSet) exist and has public accessor.",
                            targetClass.getName(),
                            targetClass.getName()
                    ), e);

            throw new SQLException(e);
        }

        return entities;
    }

    /**
     * Executes a SELECT-Statement which returns a table. <br>
     * This method runs asynchronously. <br>
     * The Bukkit API should run synchronously using a scheduler <br>
     *
     * @param targetClass the mapped EntityClass
     */
    @NotNull
    public <T> AsyncQueryResult<T> query(Class<T> targetClass) {
        CompletableFuture<SyncQueryResult<T>> future = CompletableFuture.supplyAsync(() -> {
            try {
                return queryWithException(targetClass);
            } catch (SQLException exception) {
                logger.log(Level.WARNING, "SQLException", exception);

                throw new CompletionException(exception);
            }
        });

        return new AsyncQueryResult<>(future);
    }

    /**
     * Executes a SELECT-Statement which returns a table. <br>
     * This method runs sync with Bukkit-API and is safe to use.
     *
     * @param targetClass the mapped Entity Class
     */
    @Contract("_ -> new")
    public <T> @NotNull SyncQueryResult<T> querySync(Class<T> targetClass) {
        try {
            return queryWithException(targetClass);
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot execute SQL-Statement. Inner exception was: ", exception);
        }
    }

    @Contract("_ -> new")
    private <T> @NotNull SyncQueryResult<T> queryWithException(Class<T> targetClass) throws SQLException {
        if(this.statements.size() > 1)
            throw new IllegalStateException("Queries doesn't support Statement-Chaining");

        try(Connection connection = source.getConnection();
            PreparedStatement statement = this.statements.remove().build(connection)) {

            return new SyncQueryResult<>(convertResultSetToEntities(targetClass, statement.executeQuery()));
        }
    }

    /**
     * Executes any MySQL-Statement <br>
     * This method runs asynchronously.
     *
     * @return a CompletableFuture that returns the result if the statement succeed
     */
    @Contract(" -> new")
    public @NotNull CompletableFuture<List<Boolean>> execute() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return executeWithException();
            } catch (SQLException exception) {
                logger.log(Level.WARNING, "SQLException", exception);

                throw new CompletionException(exception);
            }
        });
    }

    /**
     * Executes any SQL-Statement <br>
     * This method runs sync with the Bukkit-API
     *
     * @return true if the statement was successfully executed, false otherwise
     */
    public List<Boolean> executeSync() {
        try {
            return executeWithException();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not execute SQL-Statement. Inner exception was: " + e);
        }
    }

    private @NotNull List<Boolean> executeWithException() throws SQLException {
        try(Connection connection = source.getConnection()) {
            List<Boolean> results = Lists.newArrayListWithExpectedSize(this.statements.size());

            for(StatementWrapper wrapper : this.statements) {
                try(PreparedStatement statement = wrapper.build(connection)) {
                    results.add(statement.execute());
                }
            }

            return results;
        }
    }


    /**
     * Executes a default CRUD-Statement. <br>
     * This method runs asynchronously.
     */
    @Contract(" -> new")
    public @NotNull CompletableFuture<List<Integer>> executeUpdate() {
        return executeUpdate(false);
    }

    /**
     * Executes a default CRUD-Statement. <br>
     * This method runs asynchronously.
     */
    public @NotNull CompletableFuture<List<Integer>> executeUpdate(boolean returnAutoGeneratedValue) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return executeUpdateWithException(returnAutoGeneratedValue);
            } catch (SQLException exception) {
                logger.log(Level.WARNING, "SQLException", exception);

                throw new CompletionException(exception);
            }
        });
    }

    public List<Integer> executeUpdateSync() {
        return executeUpdateSync(false);
    }

    /**
     * Executes a default INSERT-Statement. <br>
     * If you are using a generated key in your table, this method will return this key.
     *
     * @return the generated key of your table. Otherwise, ignore it
     */
    public List<Integer> executeUpdateSync(boolean returnAutoGeneratedValue) {
        try {
            return executeUpdateWithException(returnAutoGeneratedValue);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not execute SQL-Statement. Inner exception was: " + e);
        }
    }

    private @NotNull List<Integer> executeUpdateWithException(boolean returnAutoGeneratedValue) throws SQLException {
        try(Connection connection = source.getConnection()) {
            List<Integer> results = Lists.newArrayListWithExpectedSize(this.statements.size());

            for(StatementWrapper wrapper : this.statements) {
                try(PreparedStatement statement = wrapper.build(connection, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    if(returnAutoGeneratedValue) {
                        statement.executeUpdate();

                        ResultSet resultSet = statement.getGeneratedKeys();
                        results.add(resultSet.getInt(1));
                    } else results.add(statement.executeUpdate());
                }
            }

            return results;
        }
    }

    public QueryBuilder prepare(@NotNull @Language("SQL") String query, Object... args) {
        this.statements.add(new StatementWrapper(query, args));

        return this;
    }
}
