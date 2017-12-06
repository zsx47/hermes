package net.thisisz.hermes.bungee.messaging.network.asynctask.redis;

import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.messaging.network.object.NetworkMessage;
import net.thisisz.hermes.bungee.messaging.network.provider.RedisBungeeProvider;

import java.util.UUID;

public class DisplayNetworkMessage implements Runnable {

    private NetworkMessage message;
    private boolean staff;

    public DisplayNetworkMessage(NetworkMessage message, boolean staff) {
        this.message = message;
        this.staff = staff;
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void run() {
        if (staff) {
            getPlugin().getMessagingController().displayStaffChatMessage(UUID.fromString(message.getSender()), message.getServer(), message.getMessage());
        } else {
        	getPlugin().getMessagingController().displayChatMessage(UUID.fromString(message.getSender()), message.getServer(), message.getMessage());
        }
    }

}
