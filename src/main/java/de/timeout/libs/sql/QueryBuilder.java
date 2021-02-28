package de.timeout.libs.sql;

import de.timeout.libs.log.ColoredLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

class QueryBuilder {

    private static final String EXCEPTION = "Unhandled exception while executing statement: ";

    private static final Executor THREAD_EXECUTOR = Executors.newFixedThreadPool(3);
    private static final Logger LOGGER = Logger.getLogger("MySQL-Connector");

    static {
        ColoredLogger.enableColoredLogging('&', LOGGER, "&8[&6MySQL&8]");
    }

    private final Connection connection;

    private final String query;
    private final Object[] args;

    public QueryBuilder(@NotNull Connection connection, @NotNull String query, Object... args) {
        this.connection = connection;
        this.query = query;
        this.args = args;
    }
    private PreparedStatement prepareStatement(@NotNull String statement, Object... args) throws SQLException {
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
     * @param result A function that is executed after the ResultSet is received.
     */
    public void query(@NotNull ThrowableConsumer<ResultSet> result) {
        THREAD_EXECUTOR.execute(() -> {
            try(PreparedStatement statement = prepareStatement(query, args);
                ResultSet set = statement.executeQuery()) {

                result.accept(set);
            } catch (SQLException exception) {
                LOGGER.log(Level.WARNING, EXCEPTION, exception);
            }
        });
    }

    /**
     * Executes any MySQL-Statement. <br>
     * This method runs asynchronously. <br>
     *
     */
    public void execute() {
        this.execute(null);
    }

    /**
     * Executes any MySQL-Statement <br>
     * This method runs asynchronously.
     *
     * @param result a function that is executed after the result bool is received
     */
    public void execute(@Nullable ThrowableConsumer<Boolean> result) {
        THREAD_EXECUTOR.execute(() -> {
            try(PreparedStatement statement = prepareStatement(query, args)) {
                boolean bool = statement.execute();

                if(result != null) result.accept(bool);
            } catch (SQLException exception) {
                LOGGER.log(Level.WARNING, EXCEPTION, exception);
            }
        });
    }

    /**
     * Executes a MySQL-Statement like INSERT, UPDATE, DELETE etc.<br>
     * This method runs asynchronously.
     *
     */
    public void update() {
        this.update(null);
    }

    /**
     * Executes a MySQL-Statement like INSERT, UPDATE, DELETE etc.<br>
     * This method runs asynchronously.
     *
     * @param result A function that is executed after the integer result is received.
     */
    public void update(@Nullable ThrowableConsumer<Integer> result) {
        THREAD_EXECUTOR.execute(() -> {
            try(PreparedStatement statement = prepareStatement(query, args)) {
                int updateValue = statement.executeUpdate();

                if(result != null) result.accept(updateValue);
            } catch (SQLException exception) {
                LOGGER.log(Level.WARNING, EXCEPTION, exception);
            }
        });
    }

    /**
     * Interface which allows Exception-Throwing while consuming
     * @param <T> the Type of the Consumer
     */
    @FunctionalInterface
    interface ThrowableConsumer<T> extends Consumer<T> {

        @Override
        default void accept(T t) {
            try {
                this.acceptWithThrows(t);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Unhandled exception while accepting consumer", e);
            }
        }

        void acceptWithThrows(T t) throws Exception;
    }
}
