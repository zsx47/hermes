package net.thisisz.hermes.bungee.messaging.network.provider;

import net.thisisz.hermes.bungee.HermesChat;

import java.util.UUID;

//Providers for network wide communication.
public interface NetworkProvider {

    void sendChatMessage(UUID sender, String server, String message);

    void sendNewUserNotification(UUID to, String message);

    void sendNewUserErrorMessage(UUID to, String message);

    void sendNicknameUpdate(UUID uuid, String nickname);

    void sendLoginNotification(UUID uniqueId);

    void sendUserVanishStatus(UUID uuid, boolean status);

    void sendStaffChatMessage(UUID sender, String server, String message);

    void sendPrivateMessage(UUID sender, UUID uuid, String message);
}
