package net.thisisz.hermes.bungee.messaging.local.callback;

import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.HermesChat;

import java.util.UUID;

public class DisplayUserErrorMessage implements Callback {

    private String message;
    private UUID to;

    public DisplayUserErrorMessage(UUID to, String message) {
        this.to = to;
        this.message = message;
    }

    @Override
    public void run() {
        getPlugin().getMessagingController().displayUserErrorMessage(to, message);
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }
    
}
