package net.thisisz.hermes.bungee.messaging.network.provider;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.LoadPlayerThenCallback;
import net.thisisz.hermes.bungee.messaging.MessagingController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

//Network messaging provider that only uses a local provider of choice.
public class LocalOnlyProvider implements NetworkProvider, net.md_5.bungee.api.plugin.Listener {

    private MessagingController controller;

    public LocalOnlyProvider(MessagingController parent) {
        this.controller = parent;
        HermesChat.getPlugin().getProxy().getPluginManager().registerListener(HermesChat.getPlugin(), this);
    }
    
    private HermesChat getPlugin() {
    	return HermesChat.getPlugin();
    }

    @Override
    public void sendNicknameUpdate(UUID uuid, String nickname) {
    }

    @Override
    public void sendUserVanishStatus(UUID uuid, boolean status) {

    }

    @Override
    public void sendStaffChatMessage(UUID sender, String server, String message) {
        controller.displayStaffChatMessage(sender, server, message);
    }

    @Override
    public void sendPrivateMessage(UUID sender, UUID to, String message) {
        controller.displayPrivateMessage(sender, to, message);
    }

    @Override
    public CompletableFuture<Boolean> isMuted(UUID uuid) {
        return getPlugin().getStorageController().getUser(uuid).thenComposeAsync((User) -> User.isMuted());
    }

    @Override
    public void setMuted(UUID uuid, Boolean muted) {
        getPlugin().getStorageController().getUser(uuid).thenAcceptAsync((User) -> User.setMuted(muted));
    }

    @Override
    public void sendMeMessage(UUID uniqueId, String server, String message) {
        controller.displayMeMessage(uniqueId, server, message);
    }

    @Override
    public void sendChatMessage(UUID sender, String server, String message) {
    	controller.displayChatMessage(sender, server, message);
    }

    @Override
    public void sendNewUserNotification(UUID to, String message) {
    	controller.displayUserNotification(to, message);
    }

    @Override
    public void sendNewUserErrorMessage(UUID to, String message) {
    	controller.displayUserErrorMessage(to, message);
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent event) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), () -> controller.displayLogoutNotification(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent event) {
    	getPlugin().getProxy().getScheduler().runAsync(getPlugin(), () -> controller.displayLoginNotification(event.getPlayer().getUniqueId()));
    }

}
