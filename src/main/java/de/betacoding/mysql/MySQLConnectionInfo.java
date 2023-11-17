package de.betacoding.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MySQLConnectionInfo {
    private final String protocol;
    private final String host;
    private final int port;
    private String databaseName;
    private final String user;
    private final Map<MySQLConnectionProperty<?>, Object> properties;

    public MySQLConnectionInfo(@NotNull String protocol, @NotNull String host, int port, @NotNull String databaseName, @NotNull String user, @NotNull Map<MySQLConnectionProperty<?>, Object> properties) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.properties = properties;
    }

    public @NotNull String getProtocol() {
        return this.protocol;
    }
    public @NotNull String getHost() {
        return this.host;
    }
    public int getPort() {
        return this.port;
    }
    public @Nullable String getDatabaseName() {
        return this.databaseName;
    }
    public @NotNull String getUser() {
        return this.user;
    }

    protected void setDatabaseName(@NotNull String databaseName) {
        this.databaseName = databaseName;
    }

    public <T> T getProperty(@NotNull MySQLConnectionProperty<T> property) {
        var object = this.properties.get(property);
        return object == null ? property.getDefaultValue() : property.getType().cast(object);
    }

    @NotNull
    public String getConnectionURL() {
        StringBuilder builder = new StringBuilder(this.protocol + "//" + this.host + ":" + this.port);

        if (this.databaseName != null) builder.append('/').append(this.databaseName);

        boolean propertiesInit = false;
        for (Map.Entry<MySQLConnectionProperty<?>, Object> property : this.properties.entrySet()) {
            if (!propertiesInit) {
                builder.append("?");
                propertiesInit = true;
            } else builder.append("&");

            builder.append(property.getKey().getName()).append("=").append(property.getValue().toString());
        }

        return builder.toString();
    }
}
