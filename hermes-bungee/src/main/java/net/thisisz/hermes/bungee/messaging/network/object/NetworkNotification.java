package net.thisisz.hermes.bungee.messaging.network.object;

import java.util.UUID;

public class NetworkNotification {

    public String receiver, message;

    public NetworkNotification(UUID receiver, String message) {
        this.receiver = receiver.toString();
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "NetworkMessage [receiver=" + receiver + ", message=" + message + "]";
    }

}
