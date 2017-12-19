package net.thisisz.hermes.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thisisz.hermes.bungee.HermesChat;

import java.util.*;

public class Mute extends Command {

    public Mute() {
        super("mute", "hermes.mute");
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (args.length == 1) {
            Runnable mute = () -> {
                Map<UUID, String> uuids = getPlugin().getStorageController().findUsers(args[0]);
                if (uuids.size() > 1) {
                    commandSender.sendMessage(new ComponentBuilder(ChatColor.RED + "Too many players with similar names found try to be more specific.").create());
                } else if (uuids.size() == 1) {
                    getPlugin().getMessagingController().setMuted(uuids.keySet().iterator().next(), true);
                } else {
                    commandSender.sendMessage(new ComponentBuilder(ChatColor.RED + "Couldn't find the player you are trying to message are you sure you spelled it right.").create());
                }
            };
            getPlugin().getProxy().getScheduler().runAsync(getPlugin(), mute);
        }
    }

}
