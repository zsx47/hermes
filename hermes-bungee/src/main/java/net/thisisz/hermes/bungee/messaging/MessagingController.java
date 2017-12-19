package net.thisisz.hermes.bungee.messaging;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.LoadPlayerThenCallback;
import net.thisisz.hermes.bungee.messaging.filter.FilterManager;
import net.thisisz.hermes.bungee.messaging.local.provider.LocalBungeeProvider;
import net.thisisz.hermes.bungee.messaging.local.provider.LocalProvider;
import net.thisisz.hermes.bungee.messaging.network.provider.LocalOnlyProvider;
import net.thisisz.hermes.bungee.messaging.network.provider.NetworkProvider;
import net.thisisz.hermes.bungee.messaging.network.provider.RedisBungeeProvider;
import net.thisisz.hermes.bungee.storage.StorageController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MessagingController {

    private FilterManager filterManager;
    private NetworkProvider networkProvider;
    private LocalProvider localProvider;
    private static MessagingController instance;

    public MessagingController() {
    	instance = this;
        this.filterManager = new FilterManager(this);
        setNetworkProvider();
        setLocalProvider();
    }
    
    private void setNetworkProvider() {
        if (getPlugin().getRedisBungeeAPI() == null) {
            getPlugin().getProxy().getLogger().info("Using local only network message provider since no other api was found.");
            this.networkProvider = new LocalOnlyProvider(this);
        } else {
            this.networkProvider = new RedisBungeeProvider(this);
        }
    }
    
    private void setLocalProvider() {
        this.localProvider = new LocalBungeeProvider(this);
    }
    
    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }
    
    private StorageController getStorageController() {
    	return getPlugin().getStorageController();
    }
    
    private void loadCachedUserThenCallback(UUID uuid, Callback callback) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new LoadPlayerThenCallback(uuid, callback));
    }

    //Methods prefixed with display are returns from network controller system.
    public void displayChatMessage(UUID sender, String server, String message) {
    	getStorageController().getUser(sender).thenAcceptAsync(user -> localProvider.displayChatMessage(user, getPlugin().getProxy().getServerInfo(server), message));
    }

    public void displayPrivateMessage(UUID sender, UUID to, String message) {
        getStorageController().getUser(sender).thenAcceptAsync(senderUser -> {
            getStorageController().getUser(to).thenAcceptAsync(toUser -> {
                localProvider.displayPrivateMessage(senderUser, toUser, message);
            });
        });
    }

    public void displayUserNotification(UUID to, String message) {
    	getStorageController().getUser(to).thenAcceptAsync(toUser -> localProvider.displayUserNotification(toUser, message));
    }

    public void displayUserErrorMessage(UUID to, String message) {
        getStorageController().getUser(to).thenAcceptAsync(toUser -> localProvider.displayUserErrorMessage(toUser, message));
    }

    public void displayLoginNotification(UUID player) {
        getStorageController().getUser(player).thenAcceptAsync(user -> user.canVanish().thenAcceptAsync(canVanish -> localProvider.displayLoginNotification(user, canVanish.asBoolean())));
    }

    public void displayLogoutNotification(UUID player) {
        getStorageController().getUser(player).thenAcceptAsync(user -> user.canVanish().thenAcceptAsync(canVanish -> localProvider.displayLogoutNotification(user, canVanish.asBoolean())));
    }
    
    public void displayStaffChatMessage(UUID sender, String server, String message) {
    	getStorageController().getUser(sender).thenAcceptAsync(user -> localProvider.displayStaffChatMessage(user, getPlugin().getProxy().getServerInfo(server), message));
    }

    //Methods prefixed with send new are sent out to network provider, so that any information can be passed to other bungee proxies via non local only messaging provider i.e. redisbungee
    public void sendChatMessage(ProxiedPlayer sender, Server server, String message) {
        message = filterManager.filterMessage(message);
        networkProvider.sendChatMessage(sender.getUniqueId(), server.getInfo().getName(), message);
    }

    public void sendPrivateMessage(UUID sender, UUID to, String message) {
        networkProvider.sendPrivateMessage(sender, to, message);
    }

    public void sendNewErrorMessage(ProxiedPlayer to, String message) {
    	networkProvider.sendNewUserErrorMessage(to.getUniqueId(), message);
    }

    public void sendNewNotification(UUID to, String message) {
    	networkProvider.sendNewUserNotification(to, message);
    }

    public void sendNicknameUpdate(UUID uuid, String nickname) {
    	networkProvider.sendNicknameUpdate(uuid, nickname);
    }

    public void sendUserVanishStatus(UUID uuid, boolean status) {
    	networkProvider.sendUserVanishStatus(uuid, status);
    }

    public void sendStaffChatMessage(ProxiedPlayer sender, Server server, String message) {
    	networkProvider.sendStaffChatMessage(sender.getUniqueId(), server.getInfo().getName(), message);
    }
    
    static MessagingController getMessagingController() {
    	return instance;
    }

    public CompletableFuture<Boolean> isMuted(UUID uuid) {
        return networkProvider.isMuted(uuid);
    }

    public void setMuted(UUID uuid, Boolean muted) {
        networkProvider.setMuted(uuid, muted);
    }
}
