package net.thisisz.hermes.bungee.messaging.local.callback;

import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.HermesChat;

import java.util.UUID;

public class DisplayUserNotification implements Callback {

    private String message;
    private UUID to;

    public DisplayUserNotification(UUID to, String message) {
        this.to = to;
        this.message = message;
    }

    @Override
    public void run() {
        getPlugin().getMessagingController().displayUserNotification(to, message);
    }


    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }
    
}
