package net.thisisz.hermes.bungee.messaging.network.object;

import java.util.UUID;

public class NetworkLoginNotification {

    private String user;

    public NetworkLoginNotification(UUID user) {
        this.user = user.toString();
    }

    public UUID getUser() {
        return UUID.fromString(user);
    }
}
