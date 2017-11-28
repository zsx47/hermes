package net.thisisz.hermes.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.storage.CachedUser;

import java.util.Objects;
import java.util.UUID;

public class Nickname extends Command {

    public Nickname() {
        super("nickname", "hermes.nickname", "nick");
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CachedUser user = getPlugin().getStorageController().getCachedUser((ProxiedPlayer) sender);
        if (args.length == 0) {
            getPlugin().getMessagingController().sendNewNotification(((ProxiedPlayer) sender).getUniqueId(), "Your current display name is " + user.getDisplayName());
        } else if (args.length == 1) {
            if (Objects.equals(args[0].toUpperCase(), "OFF")) {
                getPlugin().getMessagingController().sendNewNotification(((ProxiedPlayer) sender).getUniqueId(), "Disabled nickname!");
                user.setNickname(null);
            } else {
                if (sender.hasPermission("hermes.nickname")) {
                    user.setNickname(args[0]);
                    getPlugin().getMessagingController().sendNewNotification(((ProxiedPlayer) sender).getUniqueId(), "Your display name is now " + args[0]);
                } else {
                    getPlugin().getMessagingController().sendNewErrorMessage((ProxiedPlayer) sender, "You do not have permission to do this.");
                }
            }
        } else if (args.length == 2) {
            if (sender.hasPermission("hermes.nickname.other")) {
                UUID player;
                player = getPlugin().getProxy().getPlayer(args[0]).getUniqueId();
                if (player == null && getPlugin().getRedisBungeeAPI() != null) {
                    player = getPlugin().getRedisBungeeAPI().getUuidFromName(args[0]);
                }
                if (player == null) {
                    getPlugin().getMessagingController().sendNewErrorMessage((ProxiedPlayer) sender, "Player not found with name " + args[0]);
                } else {
                    if (getPlugin().getStorageController().isLoaded(player)) {
                        CachedUser userOther = getPlugin().getStorageController().getCachedUser(player);
                        if (Objects.equals(args[0].toUpperCase(), "OFF")) {
                                userOther.setNickname(null);
                                getPlugin().getMessagingController().sendNewNotification(((ProxiedPlayer) sender).getUniqueId(), "Disabled nickname for " + userOther.getName());
                        } else {
                                userOther.setNickname(args[1]);
                                getPlugin().getMessagingController().sendNewNotification(((ProxiedPlayer) sender).getUniqueId(), "Set nickname for " + userOther.getName() + " to " + args[1]);
                        }
                    } else {
                        getPlugin().getMessagingController().sendNewErrorMessage((ProxiedPlayer) sender, "Player info is not loaded for some reason.");
                    }
                }
            } else {
                getPlugin().getMessagingController().sendNewErrorMessage((ProxiedPlayer) sender, "You do not have permission to do this.");
            }
        } else {
            sender.sendMessage(new ComponentBuilder("Usage: /nickname <nickname>").create());
        }
    }


}
