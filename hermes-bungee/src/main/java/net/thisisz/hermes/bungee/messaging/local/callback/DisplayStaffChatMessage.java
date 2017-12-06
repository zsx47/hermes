package net.thisisz.hermes.bungee.messaging.local.callback;

import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.HermesChat;

import java.util.UUID;

public class DisplayStaffChatMessage implements Callback {

    private String message, server;
    private UUID sender;

    public DisplayStaffChatMessage(UUID sender, String server, String message) {
        this.sender = sender;
        this.server = server;
        this.message = message;
    }

    @Override
    public void run() {
        getPlugin().getMessagingController().displayStaffChatMessage(sender, server, message);
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

}
