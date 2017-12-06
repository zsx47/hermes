package net.thisisz.hermes.bungee.messaging.network.asynctask.common;

import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.messaging.network.provider.NetworkProvider;

import java.util.UUID;

public class DisplayLoginNotification implements Runnable, Callback {

    private UUID player;
    private boolean vjoin;

    public DisplayLoginNotification(UUID player) {
        this.player = player;
        this.vjoin = false;
    }

    public DisplayLoginNotification(UUID player, boolean vjoin) {
        this.player = player;
        this.vjoin = vjoin;
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void run() {
    	getPlugin().getMessagingController().displayLoginNotification(player, vjoin);
    }

}
