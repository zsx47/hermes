package net.thisisz.hermes.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thisisz.hermes.bungee.HermesChat;

public class Me extends Command {

    public  Me() {
        super("me", "hermes.me");
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("usage: /me <message>");
        } else {
            String message = "";
            for (String s: args) {
                message = message + " " + s;
            }
            getPlugin().getMessagingController().sendMeMessage(((ProxiedPlayer) commandSender), ((ProxiedPlayer) commandSender).getServer(), message);
        }
    }

}
