package net.thisisz.hermes.bungee.messaging.network.provider;

import net.thisisz.hermes.bungee.HermesChat;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

//Providers for network wide communication.
public interface NetworkProvider {

    void sendChatMessage(UUID sender, String server, String message);

    void sendNewUserNotification(UUID to, String message);

    void sendNewUserErrorMessage(UUID to, String message);

    void sendNicknameUpdate(UUID uuid, String nickname);

    void sendUserVanishStatus(UUID uuid, boolean status);

    void sendStaffChatMessage(UUID sender, String server, String message);

    void sendPrivateMessage(UUID sender, UUID uuid, String message);

    CompletableFuture<Boolean> isMuted(UUID uuid);

    void setMuted(UUID uuid, Boolean muted);
}
