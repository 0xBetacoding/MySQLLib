package de.betacoding.util;

import de.betacoding.mysql.MySQLServiceException;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws MySQLServiceException;
}
