package net.thisisz.hermes.bungee.messaging;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.thisisz.hermes.bungee.Callback;
import net.thisisz.hermes.bungee.AsyncTask;
import net.thisisz.hermes.bungee.HermesChat;

public class SendChatMessage implements Callback, AsyncTask {

    private ProxiedPlayer sender;
    private Server server;
    private String message;

    public SendChatMessage(ProxiedPlayer sender, Server server, String message) {
        this.sender = sender;
        this.server = server;
        this.message = message;
    }
    
    public void run() {
        MessagingController.getMessagingController().sendChatMessage(this.sender, this.server, this.message);
    }
}
