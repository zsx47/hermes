package net.thisisz.hermes.bungee.messaging.local.callback;

import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.HermesChat;

import java.util.UUID;

public class DisplayLoginNotification implements Callback {

    private UUID player;
    private boolean vjoin;

    public DisplayLoginNotification(UUID player, boolean vjoin) {
        this.player = player;
        this.vjoin = vjoin;
    }

    @Override
    public void run() {
        getPlugin().getMessagingController().displayLoginNotification(player, vjoin);
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

}
