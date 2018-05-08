package net.thisisz.hermes.bungee.storage.driver;

import net.thisisz.hermes.bungee.storage.exception.driver.GenericDriverException;

import java.util.UUID;

public interface StorageDriver {

    void runDriverInit() throws GenericDriverException;

    String getNickname(UUID uuid) throws GenericDriverException;

    void setNickname(UUID uuid, String nickname) throws GenericDriverException;

    void logChat(UUID uuid, String message);

    void logPm(UUID sender, UUID receiver, String message);

}
