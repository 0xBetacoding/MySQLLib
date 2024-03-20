package de.betacoding.logger;

import org.jetbrains.annotations.NotNull;

public interface Logger {
    void info(@NotNull String s);
    void warning(@NotNull String s);
    void severe(@NotNull String s);
    void severe(@NotNull String s, @NotNull Throwable throwable);

    public static final Logger DEBUG_LOGGER = new Logger() {
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
        public void severe(@NotNull String s, @NotNull Throwable throwable) {
            System.err.println(s);
            throwable.printStackTrace();
        }
    };
}
