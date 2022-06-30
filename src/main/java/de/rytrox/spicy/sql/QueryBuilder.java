package de.rytrox.spicy.sql;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public record QueryBuilder(DataSource source, String query, Object... args) {

    @NotNull
    private PreparedStatement prepareStatement(@NotNull Connection connection, @NotNull String statement, Object... args) throws SQLException {
        //Do not close this Statement here!!
        PreparedStatement ps = connection.prepareStatement(statement);
        if(args != null)
            for(int i = 0; i < args.length; i++) ps.setString(i + 1, args[i].toString());
        return ps;
    }

    @NotNull
    private <T> List<T> convertResultSetToEntities(@NotNull Class<T> targetClass,
                                                   @NotNull ResultSet resultSet) throws SQLException {
        List<T> entities = new LinkedList<>();
        try {
            Constructor<T> constructor = targetClass.getConstructor(ResultSet.class);

            while(resultSet.next()) {
                entities.add(constructor.newInstance(resultSet));
            }
        } catch (NoSuchMethodException e) {
            // try to convert it manually if it's just one column
            if(resultSet.getMetaData().getColumnCount() == 1) {
                while(resultSet.next()) {
                    try {
                        entities.add(resultSet.getObject(1, targetClass));
                    } catch(SQLFeatureNotSupportedException exception) {
                        // SQLite is a pain...
                        entities.add((T) resultSet.getObject(1));
                    }
                }
            } else throw new RuntimeException("Cannot convert ResultSet into Class " + targetClass.getName() +
                    ". Be sure the constructor " + targetClass.getName() + "(ResultSet) exist and has accessor public.", e);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Cannot convert ResultSet into Class " + targetClass.getName() +
                    ". Be sure the constructor " + targetClass.getName() + "(ResultSet) exist and has accessor public.", e);
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
        CompletableFuture<List<T>> future = CompletableFuture.supplyAsync(() -> {
            try(Connection connection = source.getConnection();
                PreparedStatement statement = prepareStatement(connection, query, args)) {

                return convertResultSetToEntities(targetClass, statement.executeQuery());
            } catch (SQLException exception) {
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
        try(Connection connection = source.getConnection();
            PreparedStatement statement = prepareStatement(connection, query, args)) {

            return new SyncQueryResult<>(convertResultSetToEntities(targetClass, statement.executeQuery()));
        } catch (SQLException exception) {
            throw new IllegalStateException("Cannot execute SQL-Statement. Inner exception was: " + exception, exception);
        }
    }

    /**
     * Executes any MySQL-Statement <br>
     * This method runs asynchronously.
     *
     * @return a CompletableFuture that returns the result if the statement succeed
     */
    @Contract(" -> new")
    public @NotNull CompletableFuture<Boolean> execute() {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = source.getConnection();
                PreparedStatement statement = prepareStatement(connection, query, args)) {

                return statement.execute();
            } catch (SQLException exception) {
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
    public boolean executeSync() {
        try(Connection connection = source.getConnection();
            PreparedStatement statement = prepareStatement(connection, query, args)) {

            return statement.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not execute SQL-Statement. Inner exception was: " + e);
        }
    }


    /**
     * Executes a default CRUD-Statement. <br>
     * This method runs asynchronously.
     */
    @Contract(" -> new")
    public @NotNull CompletableFuture<Integer> executeUpdate() {
        return executeUpdate(false);
    }

    /**
     * Executes a default CRUD-Statement. <br>
     * This method runs asynchronously.
     */
    public @NotNull CompletableFuture<Integer> executeUpdate(boolean returnAutoGeneratedValue) {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = source.getConnection();
                PreparedStatement statement = returnAutoGeneratedValue ?
                        prepareStatement(connection, query, args, Statement.RETURN_GENERATED_KEYS) :
                        prepareStatement(connection, query, args)) {

                if(returnAutoGeneratedValue) {
                    statement.executeUpdate();

                    ResultSet resultSet = statement.getGeneratedKeys();
                    return resultSet.getInt(1);
                } else return statement.executeUpdate();
            } catch (SQLException exception) {
                throw new CompletionException(exception);
            }
        });
    }

    public int executeUpdateSync() {
        return executeUpdateSync(false);
    }

    /**
     * Executes a default INSERT-Statement. <br>
     * If you are using a generated key in your table, this method will return this key.
     *
     * @return the generated key of your table. Otherwise, ignore it
     */
    public int executeUpdateSync(boolean returnAutoGeneratedValue) {
        try(Connection connection = source.getConnection();
            PreparedStatement statement = returnAutoGeneratedValue ?
                    prepareStatement(connection, query, args, Statement.RETURN_GENERATED_KEYS) :
                    prepareStatement(connection, query, args)) {

            if(returnAutoGeneratedValue) {
                statement.executeUpdate();

                ResultSet resultSet = statement.getGeneratedKeys();
                return resultSet.getInt(1);
            } else return statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not execute SQL-Statement. Inner exception was: " + e);
        }
    }
}
