package net.thisisz.hermes.bungee.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.thisisz.hermes.bungee.HermesChat;

public class PrivateMessage extends Command {


    public PrivateMessage(String name) {
        super("message", "hermes.message", "msg", "pm");
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

    }


}
