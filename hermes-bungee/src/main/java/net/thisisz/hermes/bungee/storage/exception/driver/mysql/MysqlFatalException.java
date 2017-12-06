package net.thisisz.hermes.bungee.storage.exception.driver.mysql;

import net.thisisz.hermes.bungee.storage.exception.driver.GenericDriverException;

public class MysqlFatalException extends GenericDriverException {

    private Exception exception;

    public MysqlFatalException(String message) {
        super(message);
    }

    public MysqlFatalException(String message, Exception withException) {
        super(message);
        this.exception = withException;
    }

    public Exception getException() {
        return exception;
    }

}
