package de.betacoding.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLLogger {
    public static final String PREFIX = "[MySQL] ";

    private final Logger logger;
    private final boolean systemLogger;

    protected MySQLLogger() {
        this.logger = null;
        this.systemLogger = true;
    }
    protected MySQLLogger(@Nullable Logger logger) {
        this.logger = logger;
        this.systemLogger = false;
    }

    public void info(@NotNull String message) {
        if (this.systemLogger) {
            System.out.println(message);
            return;
        }
        if (this.logger == null) return;
        this.logger.info(PREFIX + message);
    }
    public void warning(@NotNull String message) {
        if (this.systemLogger) {
            System.out.println(message);
            return;
        }
        if (this.logger == null) return;
        this.logger.warning(PREFIX + message);
    }
    public void severe(@NotNull String message) {
        if (this.systemLogger) {
            System.err.println(message);
            return;
        }
        if (this.logger == null) return;
        this.logger.severe(PREFIX + message);
    }
    public void severe(@NotNull String message, @NotNull Throwable throwable) {
        if (this.systemLogger) {
            System.err.println(message);
            return;
        }
        if (this.logger == null) return;
        this.logger.log(Level.SEVERE, PREFIX + message, throwable);
    }
}
