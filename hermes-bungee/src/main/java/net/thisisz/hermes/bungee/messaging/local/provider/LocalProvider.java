package net.thisisz.hermes.bungee.messaging.local.provider;

import net.md_5.bungee.api.config.ServerInfo;
import net.thisisz.hermes.bungee.storage.CachedUser;

import java.util.UUID;

//Providers for communication on single bungee proxy level. Used by MessagingController as an interface for sending network wide messages to each local proxy or to only the local proxy with the LocalOnlyProvider network messaging provider.
public interface LocalProvider {

    void displayChatMessage(CachedUser player, ServerInfo server, String message);

    void displayStaffChatMessage(CachedUser player, ServerInfo server, String message);

    void displayUserErrorMessage(CachedUser to, String message);

    void displayUserNotification(CachedUser to, String message);

    void displayLoginNotification(CachedUser player, boolean vjoin);

    void displayLogoutNotification(CachedUser player, boolean vjoin);

    void displayPrivateMessage(CachedUser sender, CachedUser to, String message);

    void displayMeMessage(CachedUser user, ServerInfo serverInfo, String message);
}
