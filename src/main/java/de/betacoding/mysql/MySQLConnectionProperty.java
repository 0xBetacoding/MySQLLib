package de.betacoding.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MySQLConnectionProperty<T> {
    public static final MySQLConnectionProperty<Boolean> AUTO_RECONNECT = new MySQLConnectionProperty<>("autoReconnect", Boolean.class, false);
    public static final MySQLConnectionProperty<String> CHARACTER_ENCODING = new MySQLConnectionProperty<>("characterEncoding", String.class, null);

    private final String name;
    private final Class<T> type;
    private final T defaultValue;

    private MySQLConnectionProperty(@NotNull String name, @NotNull Class<T> type, @Nullable T defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public @NotNull String getName() {
        return this.name;
    }
    public @NotNull Class<T> getType() {
        return this.type;
    }
    public @Nullable T getDefaultValue() {
        return this.defaultValue;
    }
}
