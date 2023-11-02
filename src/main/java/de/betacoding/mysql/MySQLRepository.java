package de.betacoding.mysql;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MySQLRepository {
    private final MySQLConnector connector;
    private CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

    public MySQLRepository(@NotNull MySQLConnectionBuilder builder) {
        this.connector = builder.build();
    }

    public boolean isEstablished() {
        return this.future.thenApply($ -> this.connector.isEstablished()).join();
    }

    public @NotNull MySQLQueryService getService() {
        return this.connector.getService().orElseThrow(() -> new RuntimeException("MySQLService is not initiated: Connection is not established"));
    }

    public @NotNull CompletableFuture<Void> establish(final @NotNull String password) {
        return this.future = this.future.thenCompose($ -> {
            if (this.connector.isEstablished()) return CompletableFuture.completedFuture(null);
            return CompletableFuture.runAsync(() ->
                    this.connector.establish(password));
        });
    }
    public @NotNull CompletableFuture<Void> close() {
        return this.future = this.future.thenCompose($ -> {
            if (!this.connector.isEstablished()) return CompletableFuture.completedFuture(null);
            return CompletableFuture.runAsync(this.connector::close);
        });
    }
}
