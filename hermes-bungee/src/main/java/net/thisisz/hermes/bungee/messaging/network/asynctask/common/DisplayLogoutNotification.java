package net.thisisz.hermes.bungee.messaging.network.asynctask.common;

import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.messaging.network.provider.NetworkProvider;

import java.util.UUID;

public class DisplayLogoutNotification implements Runnable, Callback {

    private NetworkProvider provider;
    private UUID player;

    public DisplayLogoutNotification(NetworkProvider provider, UUID player) {
        this.provider = provider;
        this.player = player;
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void run() {
        getPlugin().getMessagingController().displayLogoutNotification(player);
    }

}
