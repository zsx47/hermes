package net.thisisz.hermes.bungee.messaging.local.callback;

import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.HermesChat;

import java.util.UUID;

public class DisplayLogoutNotification implements Callback {

    private UUID player;

    public DisplayLogoutNotification(UUID player) {
        this.player = player;
    }

    @Override
    public void run() {
        getPlugin().getMessagingController().displayLogoutNotification(player);
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

}
