package de.betacoding.mysql;

import com.google.common.base.Preconditions;
import de.betacoding.util.ThrowableFunction;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MySQLQueryService {
    private final Connection connection;

    public MySQLQueryService(@NotNull Connection connection) {
        this.connection = connection;
    }

    public <T> @NotNull Optional<T> executeQuery(final boolean retry,
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
                return this.executeQuery(false, sqlString, mapper, values);
            }
        }
    }
    public <T> @NotNull Optional<T> executeQuery(final @NotNull String sqlString,
                                        final @NotNull ThrowableFunction<ResultSet, T> mapper,
                                        final Object... values) throws SQLException {
        return this.executeQuery(true, sqlString, mapper, values);
    }
    public <T> @NotNull CompletableFuture<Optional<T>> executeQueryAsync(final boolean retry,
                                                                final @NotNull String sqlString,
                                                                final @NotNull ThrowableFunction<ResultSet, T> mapper,
                                                                final Object... values) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.executeQuery(retry, sqlString, mapper, values);
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
    public <T> @NotNull CompletableFuture<Optional<T>> executeQueryAsync(final @NotNull String sqlString,
                                                                final @NotNull ThrowableFunction<ResultSet, T> mapper,
                                                                final Object... values) {
        return this.executeQueryAsync(true, sqlString, mapper, values);
    }


    private int executeUpdate(final boolean retry,
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
            return this.executeUpdate(false, sqlString, values);
        }
    }
    public int executeUpdate(final @NotNull String sqlString,
                             final Object... values) throws SQLException {
        return this.executeUpdate(true, sqlString, values);
    }
    public @NotNull CompletableFuture<Integer> executeUpdateAsync(final boolean retry,
                                                                  final @NotNull String sqlString,
                                                                  final Object... values) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.executeUpdate(retry, sqlString, values);
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        });
    }
    public @NotNull CompletableFuture<Integer> executeUpdateAsync(final @NotNull String sqlString,
                                                                  final Object... values) {
        return this.executeUpdateAsync(true, sqlString, values);
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
