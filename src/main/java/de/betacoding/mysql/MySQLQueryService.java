package de.betacoding.mysql;

import com.google.common.base.Preconditions;
import de.betacoding.util.ThrowableFunction;
import de.betacoding.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class MySQLQueryService {
    private final Connection connection;
    private final ExecutorService sequentialExecutor;

    public MySQLQueryService(@NotNull Connection connection) throws SQLException {
        this.connection = connection;
        this.connection.setAutoCommit(true);
        this.sequentialExecutor = Executors.newSingleThreadExecutor();
    }

    private <T> @NotNull Optional<T> _executeQuery(final boolean retry,
                                                   final @NotNull String sqlString,
                                                   final @NotNull ThrowableFunction<ResultSet, T> mapper,
                                                   final Object... values) throws SQLException {
        Preconditions.checkArgument(!this.connection.isClosed(), "SQLConnection is closed!");
        Preconditions.checkArgument(!sqlString.isEmpty() && !sqlString.isBlank(), "SQLString cannot be empty");

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sqlString)) {
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setObject(i + 1, values[i]);
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                T value = mapper.apply(resultSet);
                return Optional.ofNullable(value);
            } catch (Throwable exception) {
                if (!retry) throw new RuntimeException(exception);
                return this._executeQuery(false, sqlString, mapper, values);
            }
        }
    }

    public <T> @NotNull CompletableFuture<Optional<T>> executeQueryAsync(final boolean retry,
                                                                         final @NotNull String sqlString,
                                                                         final @NotNull ThrowableFunction<ResultSet, T> mapper,
                                                                         final Object... values) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this._executeQuery(retry, sqlString, mapper, values);
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }, this.sequentialExecutor);
    }
    public <T> @NotNull CompletableFuture<Optional<T>> executeQueryAsync(final @NotNull String sqlString,
                                                                         final @NotNull ThrowableFunction<ResultSet, T> mapper,
                                                                         final Object... values) {
        return this.executeQueryAsync(true, sqlString, mapper, values);
    }

    public <T> @NotNull Optional<T> executeQuery(final boolean retry,
                                                 final @NotNull String sqlString,
                                                 final @NotNull ThrowableFunction<ResultSet, T> mapper,
                                                 final Object... values) throws MySQLServiceException {
        AtomicReference<Throwable> caughtException = new AtomicReference<>();

        Optional<T> optionalResult = this.executeQueryAsync(retry, sqlString, mapper, values)
                .exceptionally(
                        exception -> {
                            caughtException.set(exception);
                            return Optional.empty();
                        }
                ).join();

        if (caughtException.get() != null) {
            Throwable exception = caughtException.get();
            if (exception instanceof MySQLServiceException serviceException) {
                throw serviceException;
            }
            throw new MySQLServiceException(exception);
        }

        return optionalResult;
    }
    public <T> @NotNull Optional<T> executeQuery(final @NotNull String sqlString,
                                                 final @NotNull ThrowableFunction<ResultSet, T> mapper,
                                                 final Object... values) throws MySQLServiceException {
        return this.executeQuery(true, sqlString, mapper, values);
    }


    private int _executeUpdate(final boolean retry,
                               final @NotNull String sqlString,
                               final Object... values) throws SQLException {
        Preconditions.checkArgument(!this.connection.isClosed(), "SQLConnection is closed!");
        Preconditions.checkArgument(!sqlString.isEmpty() && !sqlString.isBlank(), "SQLString cannot be empty");

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(sqlString)) {
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setObject(i + 1, values[i]);
            }

            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            if (!retry) throw new SQLException(exception);
            return this._executeUpdate(false, sqlString, values);
        }
    }

    public @NotNull CompletableFuture<Integer> executeUpdateAsync(final boolean retry,
                                                                  final @NotNull String sqlString,
                                                                  final Object... values) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this._executeUpdate(retry, sqlString, values);
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        }, this.sequentialExecutor);
    }
    public @NotNull CompletableFuture<Integer> executeUpdateAsync(final @NotNull String sqlString,
                                                                  final Object... values) {
        return this.executeUpdateAsync(true, sqlString, values);
    }

    public @NotNull Integer executeUpdate(final boolean retry,
                                          final @NotNull String sqlString,
                                          final Object... values) throws MySQLServiceException {
        AtomicReference<Throwable> caughtException = new AtomicReference<>();

        Integer optionalResult = this.executeUpdateAsync(retry, sqlString, values)
                .exceptionally(
                        exception -> {
                            caughtException.set(exception);
                            return -1;
                        }
                ).join();

        if (caughtException.get() != null) {
            Throwable exception = caughtException.get();
            if (exception instanceof MySQLServiceException serviceException) {
                throw serviceException;
            }
            throw new MySQLServiceException(exception);
        }

        return optionalResult;
    }
    public @NotNull Integer executeUpdate(final @NotNull String sqlString,
                                          final Object... values) throws MySQLServiceException {
        return this.executeUpdate(true, sqlString, values);
    }


    public CompletableFuture<Void> transmit(ThrowableRunnable transmission) {
        return CompletableFuture.runAsync(() -> {
            try {
                this.connection.setAutoCommit(false);

                transmission.run();

                this.connection.commit();
                this.connection.setAutoCommit(true);
            } catch (Exception exception) {
                try {
                    this.connection.rollback();
                    this.connection.setAutoCommit(true);
                } catch (SQLException rollbackException) {
                    throw new RuntimeException(rollbackException);
                }
            }
        }, this.sequentialExecutor);
    }

    /*
     * CompletableFuture<Optional<String>> nameQuery = executeQueryAsync(connection, sqlQuery,
     * resultSet -> {
     *    if (resultSet.next()) {
     *        return resultSet.getString("name");
     *    }
     *    return null;
     * }, values);
     *
     * nameQuery.thenAccept(name -> sout(name));
     *
     * nameQuery.exceptionally(exception -> throw exception);
     *
     * CompletableFuture.allOf(nameQuery, ...).join();
     */
}
