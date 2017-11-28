package net.thisisz.hermes.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thisisz.hermes.bungee.HermesChat;

import java.util.*;

public class PrivateMessage extends Command {

    public PrivateMessage() {
        super("message", "hermes.message", "msg", "pm");
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length >= 2) {
            Runnable sendpm = () -> {
                Map<UUID, String> uuids = getPlugin().getStorageController().findUsers(args[0]);
                if (uuids.size() > 1) {
                    commandSender.sendMessage(new ComponentBuilder(ChatColor.RED + "Too many players with similar names found try to be more specific.").create());
                } else if (uuids.size() == 1) {
                    String message = "";
                    List<String> messageParts = new ArrayList<String>(Arrays.asList(args));
                    messageParts.remove(0);
                    for (String s: messageParts) {
                        message = message + " " + s;
                    }
                    for (UUID uuid:uuids.keySet()) {
                        getPlugin().getMessagingController().sendPrivateMessage(((ProxiedPlayer)commandSender).getUniqueId(), uuid, message);
                    }
                } else {
                    commandSender.sendMessage(new ComponentBuilder(ChatColor.RED + "Couldn't find the player you are trying to message are you sure you spelled it right.").create());
                }
            };
            getPlugin().getProxy().getScheduler().runAsync(getPlugin(), sendpm);
        } else {
            commandSender.sendMessage(new ComponentBuilder(ChatColor.YELLOW + "usage: /message <to> <message>").create());
        }
    }


}
