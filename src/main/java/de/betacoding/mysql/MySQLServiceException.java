package de.betacoding.mysql;

public class MySQLServiceException extends Exception {
    public MySQLServiceException() {
    }
    public MySQLServiceException(String message) {
        super(message);
    }
    public MySQLServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    public MySQLServiceException(Throwable cause) {
        super(cause);
    }
}
