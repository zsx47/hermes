package net.thisisz.hermes.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.server.BroadcastMessageEvent;

public class ChatListener implements Listener {

    private HermesChatSpigot plugin;

    public ChatListener(HermesChatSpigot parent) {
        plugin = parent;
    }

    public HermesChatSpigot getPlugin() {
        return plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatEvent(AsyncPlayerChatEvent event) {
        if (!event.isCancelled()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("HermesChatMessage");
            out.writeUTF(event.getMessage());
            event.getPlayer().sendPluginMessage(getPlugin(), "BungeeCord", out.toByteArray());
            event.setCancelled(true);
        }
    }

}
