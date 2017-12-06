package net.thisisz.hermes.bungee.messaging.network.asynctask.redis;

import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.messaging.network.object.NetworkNotification;
import net.thisisz.hermes.bungee.messaging.network.provider.RedisBungeeProvider;

import java.util.UUID;

public class DisplayNetworkNotification implements Runnable {

    private NetworkNotification message;

    public DisplayNetworkNotification(NetworkNotification message) {
        this.message = message;
    }
    
    private HermesChat getPlugin() {
    	return HermesChat.getPlugin();
    }

    @Override
    public void run() {
        if (getPlugin().getProxy().getPlayer(UUID.fromString(message.getReceiver())) != null) {
            getPlugin().getMessagingController().displayUserNotification(UUID.fromString(message.getReceiver()), message.getMessage());
        }
    }

}
