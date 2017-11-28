package net.thisisz.hermes.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thisisz.hermes.bungee.HermesChat;

public class StaffChat extends Command {

    public StaffChat() {
        super("staffchat", "hermes.staffchat", "sc");
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("hermes.staffchat")) {
            if (args.length == 0) {
                commandSender.sendMessage("usage: /staffchat <message>");
            } else {
                String message = "";
                for (String s: args) {
                    message = message + " " + s;
                }
                getPlugin().getMessagingController().sendStaffChatMessage(((ProxiedPlayer) commandSender), ((ProxiedPlayer) commandSender).getServer(), message);
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "you don't have permission to do this.");
        }
    }
}
