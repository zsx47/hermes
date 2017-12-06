package net.thisisz.hermes.bungee.messaging.network.object;

import java.util.UUID;

public class NetworkMessage {

    public String sender, server, message;

    public NetworkMessage(UUID sender, String server, String message) {
        this.sender = sender.toString();
        this.server = server;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getServer() {
        return server;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "NetworkMessage [sender=" + sender + ", server=" + server + ", message=" + message + "]";
    }

}
