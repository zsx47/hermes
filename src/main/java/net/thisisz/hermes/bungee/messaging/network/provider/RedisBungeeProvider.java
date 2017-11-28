package net.thisisz.hermes.bungee.messaging.network.provider;

import com.google.gson.Gson;
import com.imaginarycode.minecraft.redisbungee.events.PlayerJoinedNetworkEvent;
import com.imaginarycode.minecraft.redisbungee.events.PlayerLeftNetworkEvent;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.LoadPlayerThenCallback;
import net.thisisz.hermes.bungee.messaging.MessagingController;
import net.thisisz.hermes.bungee.messaging.network.asynctask.common.DisplayLoginNotification;
import net.thisisz.hermes.bungee.messaging.network.asynctask.common.DisplayLogoutNotification;
import net.thisisz.hermes.bungee.messaging.network.asynctask.redis.DisplayNetworkErrorMessage;
import net.thisisz.hermes.bungee.messaging.network.asynctask.redis.DisplayNetworkMessage;
import net.thisisz.hermes.bungee.messaging.network.asynctask.redis.DisplayNetworkNotification;
import net.thisisz.hermes.bungee.messaging.network.asynctask.redis.RemoteUpdateNickname;
import net.thisisz.hermes.bungee.messaging.network.object.*;

import java.util.UUID;

public class RedisBungeeProvider implements NetworkProvider, net.md_5.bungee.api.plugin.Listener {

    private MessagingController controller;

    public RedisBungeeProvider(MessagingController parent) {
        this.controller = parent;
        getPlugin().getRedisBungeeAPI().registerPubSubChannels("network-chat-messages");
        getPlugin().getRedisBungeeAPI().registerPubSubChannels("network-private-messages");
        getPlugin().getRedisBungeeAPI().registerPubSubChannels("network-notification");
        getPlugin().getRedisBungeeAPI().registerPubSubChannels("network-error-message");
        getPlugin().getRedisBungeeAPI().registerPubSubChannels("network-nickname-updates");
        getPlugin().getRedisBungeeAPI().registerPubSubChannels("network-login-notification");
        getPlugin().getRedisBungeeAPI().registerPubSubChannels("network-staff-chat-messages");
        getPlugin().getRedisBungeeAPI().registerPubSubChannels("network-user-vanish-status");
        getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), this);
    }

    public HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void sendNicknameUpdate(UUID uuid, String nickname) {
        NetworkNicknameUpdate obj = new NetworkNicknameUpdate(uuid, nickname);
        Gson gson = new Gson();
        getPlugin().getRedisBungeeAPI().sendChannelMessage("network-nickname-updates", gson.toJson(obj));
    }

    @Override
    public void sendLoginNotification(UUID uniqueId) {
        NetworkLoginNotification obj = new NetworkLoginNotification(uniqueId);
        Gson gson = new Gson();
        getPlugin().getRedisBungeeAPI().sendChannelMessage("network-login-notification", gson.toJson(obj));
    }

    @Override
    public void sendUserVanishStatus(UUID uuid, boolean status) {
        NetworkUserVanishStatus obj = new NetworkUserVanishStatus(uuid, status);
        Gson gson = new Gson();
        getPlugin().getRedisBungeeAPI().sendChannelMessage("network-user-vanish-status", gson.toJson(obj));
    }

    @Override
    public void sendStaffChatMessage(UUID sender, String server, String message) {
        NetworkMessage obj = new NetworkMessage(sender, server, message);
        Gson gson = new Gson();
        getPlugin().getRedisBungeeAPI().sendChannelMessage("network-staff-chat-messages", gson.toJson(obj));
    }

    @Override
    public void sendPrivateMessage(UUID sender, UUID to, String message) {
        if (getPlugin().getProxy().getPlayer(to) != null) {
            controller.displayPrivateMessage(sender, to, message);
        }
        NetworkPrivateMessage obj = new NetworkPrivateMessage(sender, to, message);
        Gson gson = new Gson();
        getPlugin().getRedisBungeeAPI().sendChannelMessage("network-private-messages", gson.toJson(obj));
    }

    @Override
    public void sendChatMessage(UUID sender, String server, String message) {
        NetworkMessage obj = new NetworkMessage(sender, server, message);
        Gson gson = new Gson();
        getPlugin().getRedisBungeeAPI().sendChannelMessage("network-chat-messages", gson.toJson(obj));
    }

    @Override
    public void sendNewUserNotification(UUID to, String message) {
        NetworkNotification obj = new NetworkNotification(to, message);
        Gson gson = new Gson();
        getPlugin().getRedisBungeeAPI().sendChannelMessage("network-notification", gson.toJson(obj));
    }

    @Override
    public void sendNewUserErrorMessage(UUID to, String message) {
        NetworkNotification obj = new NetworkNotification(to, message);
        Gson gson = new Gson();
        getPlugin().getRedisBungeeAPI().sendChannelMessage("network-error-notification", gson.toJson(obj));
    }

    @EventHandler
    public void onPlayerJoinedNetworkEvent(PlayerJoinedNetworkEvent event) {
        displayPlayerLogin(event.getUuid());
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        displayPlayerLogin(event.getPlayer().getUniqueId());
    }

    private void displayPlayerLogin(UUID player) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new LoadPlayerThenCallback(player, new DisplayLoginNotification(player)));    
    }
    

    @EventHandler
    public void onPlayerLeftNetworkEvent(PlayerLeftNetworkEvent event) {
        displayPlayerLogout(event.getUuid());
    }

    @EventHandler
    public void onPlayerLogout(PlayerDisconnectEvent event) {
        displayPlayerLogout(event.getPlayer().getUniqueId());
    }

    private void displayPlayerLogout(UUID player) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new DisplayLogoutNotification(this, player));
    }

    @EventHandler
    public void onPubSubMessageEvent(PubSubMessageEvent event) {
        Gson gson = new Gson();
        switch (event.getChannel()) {
            case "network-chat-messages":
                NetworkMessage networkMessage = gson.fromJson(event.getMessage(), NetworkMessage.class);
                getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new DisplayNetworkMessage(networkMessage, false));
                break;
            case "network-private-messages":
                NetworkPrivateMessage networkPrivateMessage = gson.fromJson(event.getMessage(), NetworkPrivateMessage.class);
                getPlugin().getProxy().getScheduler().runAsync(getPlugin(), () -> {
                    controller.displayPrivateMessage(networkPrivateMessage.getSender(), networkPrivateMessage.getTo(), networkPrivateMessage.getMessage());
                });
                break;
            case "network-notification":
                NetworkNotification networkNotification = gson.fromJson(event.getMessage(), NetworkNotification.class);
                getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new DisplayNetworkNotification(networkNotification));
                break;
            case "network-error-notification":
                NetworkErrorMessage networkErrorMessage = gson.fromJson(event.getMessage(), NetworkErrorMessage.class);
                getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new DisplayNetworkErrorMessage(networkErrorMessage));
                break;
            case "network-nickname-updates":
                NetworkNicknameUpdate networkNicknameUpdate = gson.fromJson(event.getMessage(), NetworkNicknameUpdate.class);
                getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new RemoteUpdateNickname(networkNicknameUpdate));
                break;
            case "network-login-notification":
                NetworkLoginNotification networkLoginNotification = gson.fromJson(event.getMessage(), NetworkLoginNotification.class);
                getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new DisplayLoginNotification(networkLoginNotification.getUser(), false));
                break;
            case "network-staff-chat-messages":
                NetworkMessage networkStaffMessage = gson.fromJson(event.getMessage(), NetworkMessage.class);
                getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new DisplayNetworkMessage(networkStaffMessage, true));
                break;
            default:
                break;
        }
    }

}
