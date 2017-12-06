package net.thisisz.hermes.bungee.messaging.network.object;

import java.util.UUID;

public class NetworkUserVanishStatus {

    public String user;
    public boolean status;

    public NetworkUserVanishStatus(UUID user, boolean status) {
        this.user = user.toString();
        this.status = status;
    }

    public UUID getUser() {
        return UUID.fromString(user);
    }

    public boolean getStatus() {
        return status;
    }

}
