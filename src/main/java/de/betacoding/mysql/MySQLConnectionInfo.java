package de.betacoding.mysql;

import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.Map;

public record MySQLConnectionInfo(@NotNull String protocol, @NotNull InetSocketAddress address, @NotNull String databaseName, @NotNull String user, @NotNull Map<MySQLConnectionProperty<?>, Object> properties) {
    public <T> T getProperty(@NotNull MySQLConnectionProperty<T> property) {
        var object = this.properties.get(property);
        return object == null ? property.getDefaultValue() : property.getType().cast(object);
    }

    @NotNull
    public String getConnectionURL() {
        StringBuilder builder = new StringBuilder(this.protocol + "//" + this.address.getHostName() + ":" + this.address.getPort() + "/" + this.databaseName);
        boolean propertiesInit = false;
        for (Map.Entry<MySQLConnectionProperty<?>, Object> property : properties.entrySet()) {
            if (!propertiesInit) {
                builder.append("?");
                propertiesInit = true;
            } else builder.append("&");

            builder.append(property.getKey().getName()).append("=").append(property.getValue().toString());
        }
        return builder.toString();
    }
}
