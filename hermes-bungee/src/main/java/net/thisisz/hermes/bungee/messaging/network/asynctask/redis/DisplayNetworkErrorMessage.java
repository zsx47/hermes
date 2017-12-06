package net.thisisz.hermes.bungee.messaging.network.asynctask.redis;

import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.messaging.network.object.NetworkErrorMessage;
import net.thisisz.hermes.bungee.messaging.network.provider.RedisBungeeProvider;

import java.util.UUID;

public class DisplayNetworkErrorMessage implements Runnable {

    private NetworkErrorMessage message;

    public DisplayNetworkErrorMessage(NetworkErrorMessage message) {
        this.message = message;
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void run() {
        if (getPlugin().getProxy().getPlayer(UUID.fromString(message.getReceiver())) != null) {
        	getPlugin().getMessagingController().displayUserErrorMessage(UUID.fromString(message.getReceiver()), message.getMessage());
        }
    }

}
