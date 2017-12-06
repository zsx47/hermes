package net.thisisz.hermes.bungee.messaging;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.LoadPlayerThenCallback;
import net.thisisz.hermes.bungee.messaging.filter.FilterManager;
import net.thisisz.hermes.bungee.messaging.local.callback.DisplayChatMessage;
import net.thisisz.hermes.bungee.messaging.local.callback.DisplayLoginNotification;
import net.thisisz.hermes.bungee.messaging.local.callback.DisplayLogoutNotification;
import net.thisisz.hermes.bungee.messaging.local.callback.DisplayStaffChatMessage;
import net.thisisz.hermes.bungee.messaging.local.callback.DisplayUserErrorMessage;
import net.thisisz.hermes.bungee.messaging.local.callback.DisplayUserNotification;
import net.thisisz.hermes.bungee.messaging.local.provider.LocalBungeeProvider;
import net.thisisz.hermes.bungee.messaging.local.provider.LocalProvider;
import net.thisisz.hermes.bungee.messaging.network.provider.LocalOnlyProvider;
import net.thisisz.hermes.bungee.messaging.network.provider.NetworkProvider;
import net.thisisz.hermes.bungee.messaging.network.provider.RedisBungeeProvider;
import net.thisisz.hermes.bungee.storage.StorageController;

import java.util.UUID;

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
    	if (getStorageController().isLoaded(sender)) {
            localProvider.displayChatMessage(getStorageController().getCachedUser(sender), getPlugin().getProxy().getServerInfo(server), message);
        } else {
            //load user in async call then call this method again
            loadCachedUserThenCallback(sender, new DisplayChatMessage(sender, server, message));
        }
    }

    public void displayPrivateMessage(UUID sender, UUID to, String message) {
        if (getStorageController().isLoaded(sender)) {
            if (getStorageController().isLoaded(to)) {
                localProvider.displayPrivateMessage(sender, to, message);
            } else {
                loadCachedUserThenCallback(to, () -> displayPrivateMessage(sender, to, message));
            }
        } else {
            loadCachedUserThenCallback(sender, () -> displayPrivateMessage(sender, to, message));
        }
    }

    public void displayUserNotification(UUID to, String message) {
    	if (getStorageController().isLoaded(to)) {
    		localProvider.displayUserNotification(getStorageController().getCachedUser(to), message);
        } else {
            loadCachedUserThenCallback(to, new DisplayUserNotification(to, message));
        }
    }

    public void displayUserErrorMessage(UUID to, String message) {
    	if (getStorageController().isLoaded(to)) {
            localProvider.displayUserErrorMessage(getStorageController().getCachedUser(to), message);
        } else {
            loadCachedUserThenCallback(to, new DisplayUserErrorMessage(to, message));
        }
    }

    public void displayLoginNotification(UUID player, boolean vjoin) {
    	if (getStorageController().isLoaded(player)) {
    		localProvider.displayLoginNotification(getStorageController().getCachedUser(player), vjoin);
        } else {
            loadCachedUserThenCallback(player, new DisplayLoginNotification(player, vjoin));
        }
    }

    public void displayLogoutNotification(UUID player) {
    	if (getStorageController().isLoaded(player)) {
    		localProvider.displayLogoutNotification(getStorageController().getCachedUser(player));
        } else {
            loadCachedUserThenCallback(player, new DisplayLogoutNotification(player));
        }
    }
    
    public void displayStaffChatMessage(UUID sender, String server, String message) {
    	if (getStorageController().isLoaded(sender)) {
    		localProvider.displayStaffChatMessage(getStorageController().getCachedUser(sender), getPlugin().getProxy().getServerInfo(server), message);
        } else {
            loadCachedUserThenCallback(sender, new DisplayStaffChatMessage(sender, server, message));
        }
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

    public void sendLoginNotification(ProxiedPlayer commandSender) {
    	networkProvider.sendLoginNotification(commandSender.getUniqueId());
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
}
