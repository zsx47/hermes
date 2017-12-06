package net.thisisz.hermes.bungee.storage.exception.driver.mysql;

import net.thisisz.hermes.bungee.storage.exception.driver.GenericDriverException;

public class MysqlConnectionException extends GenericDriverException {

    public MysqlConnectionException(String message) {
        super(message);
    }

}
