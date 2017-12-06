package net.thisisz.hermes.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thisisz.hermes.bungee.HermesChat;

public class VanishedJoin extends Command {

    public VanishedJoin() {
        super("vanishjoin", "hermes.vanishjoin", "vjoin");
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        getPlugin().getMessagingController().sendLoginNotification((ProxiedPlayer) commandSender);
    }
}
