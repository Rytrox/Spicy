package de.timeout.libs.sql;

import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class QueryBuilder {

    private final String query;
    private final DataSource source;
    private final Object[] args;

    public QueryBuilder(@NotNull DataSource source, @NotNull String query, Object... args) {
        this.query = query;
        this.args = args;
        this.source = source;
    }

    @NotNull
    private PreparedStatement prepareStatement(@NotNull Connection connection, @NotNull String statement, Object... args) throws SQLException {
        //Do not close this Statement here!!
        PreparedStatement ps = connection.prepareStatement(statement);
        if(args != null)
            for(int i = 0; i < args.length; i++) ps.setString(i +1, args[i].toString());
        return ps;
    }

    /**
     * Executes a SELECT-Statement which returns a table. <br>
     * This method runs asynchronously. <br>
     * The Bukkit API should run synchronously using a scheduler <br>
     *
     * @param targetClass the mapped EntityClass
     */
    public <T> FutureResult<T> query(Class<T> targetClass) {
        CompletableFuture<List<T>> future = CompletableFuture.supplyAsync(() -> {
            try(Connection connection = source.getConnection();
                PreparedStatement statement = prepareStatement(connection, query, args)) {

                Constructor<T> constructor = targetClass.getConstructor(ResultSet.class);
                ResultSet resultSet = statement.executeQuery();
                List<T> entities = new LinkedList<>();

                while(resultSet.next()) {
                    entities.add(constructor.newInstance(resultSet));
                }

                return entities;
            } catch (SQLException | ReflectiveOperationException exception) {
               throw new CompletionException(exception);
            }
        });

        return new QueryResult<>(future);
    }

    /**
     * Executes any MySQL-Statement <br>
     * This method runs asynchronously.
     *
     * @return a CompletableFuture that returns the result if the statement succeed
     */
    public CompletableFuture<Boolean> execute() {
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
     * Executes a default INSERT-Statement. <br>
     * This method runs asynchronously.
     */
    public CompletableFuture<Integer> insert() {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = source.getConnection();
                PreparedStatement statement = prepareStatement(connection, query, args, Statement.RETURN_GENERATED_KEYS)) {
                statement.executeUpdate();

                ResultSet resultSet = statement.getGeneratedKeys();
                return resultSet.getInt(1);
            } catch (SQLException exception) {
                throw new CompletionException(exception);
            }
        });
    }

    /**
     * Executes a MySQL-Statement like UPDATE, DELETE etc.<br>
     * This method runs asynchronously.
     *
     * @return a CompletableFuture that returns the result if the statement succeed
     */
    public CompletableFuture<Integer> update() {
        return CompletableFuture.supplyAsync(() -> {
            try(Connection connection = source.getConnection();
                PreparedStatement statement = prepareStatement(connection, query, args)) {

                return statement.executeUpdate();
            } catch (SQLException exception) {
                throw new CompletionException(exception);
            }
        });
    }
}
