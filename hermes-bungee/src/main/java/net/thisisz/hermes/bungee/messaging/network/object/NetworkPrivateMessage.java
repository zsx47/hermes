package net.thisisz.hermes.bungee.messaging.network.object;

import java.util.UUID;

public class NetworkPrivateMessage {

    private String sender, to, message;

    public NetworkPrivateMessage(UUID sender, UUID to, String message) {
        this.sender = sender.toString();
        this.to = to.toString();
        this.message = message;
    }

    public UUID getSender() {
        return UUID.fromString(sender);
    }

    public UUID getTo() {
        return UUID.fromString(to);
    }

    public String getMessage() {
        return message;
    }

}
