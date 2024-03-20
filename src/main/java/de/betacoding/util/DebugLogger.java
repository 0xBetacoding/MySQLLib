package de.betacoding.util;

import org.jetbrains.annotations.NotNull;

public interface DebugLogger {
    void info(@NotNull String s);
    void warning(@NotNull String s);
    void severe(@NotNull String s);
    void severe(@NotNull String s, @NotNull Throwable throwable);

    DebugLogger SYS_LOGGER = new DebugLogger() {
        @Override
        public void info(@NotNull String s) {
            System.out.println(s);
        }

        @Override
        public void warning(@NotNull String s) {
            System.out.println(s);
        }

        @Override
        public void severe(@NotNull String s) {
            System.err.println(s);
        }

        @Override
        @SuppressWarnings("all")
        public void severe(@NotNull String s, @NotNull Throwable throwable) {
            System.err.println(s);
            throwable.printStackTrace();
        }
    };
}
