package net.thisisz.hermes.bungee.messaging.network.object;

import java.util.UUID;

public class NetworkNicknameUpdate {

    private String user, newNickname;

    public NetworkNicknameUpdate(UUID user, String newNickname) {
        this.user = user.toString();
        this.newNickname = newNickname;
    }

    public UUID getUUID() {
        return UUID.fromString(user);
    }

    public String getNewNickname() {
        return newNickname;
    }


}
