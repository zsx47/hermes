package net.thisisz.hermes.bungee.messaging.network.object;

import java.util.UUID;

public class NetworkMeMessage {

    private String sender, server, message;

    public NetworkMeMessage(UUID sender, String server, String message) {
        this.sender = sender.toString();
        this.server = server;
        this.message = message;
    }

    public UUID getSender() {
        return UUID.fromString(sender);
    }

    public String getServer() {
        return server;
    }

    public String getMessage() {
        return message;
    }


}
