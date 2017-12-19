package net.thisisz.hermes.bungee;

import me.lucko.luckperms.api.event.user.UserDataRecalculateEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;
import net.thisisz.hermes.bungee.storage.CachedUser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.UUID;

public class HermesListener implements net.md_5.bungee.api.plugin.Listener {

    private HermesChat plugin;

    public HermesListener(HermesChat parent) {
        this.plugin = parent;
    }

    public HermesChat getPlugin() {
        return this.plugin;
    }

    @EventHandler
    public void onChatEvent(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if (channel.equals("HermesChatMessage")) {
                    String message = in.readUTF();
                    if (((ProxiedPlayer) event.getReceiver()).hasPermission("hermes.chat")) {
                        getPlugin().getStorageController().getUser(((ProxiedPlayer) event.getReceiver()).getUniqueId()).thenAcceptAsync((user) -> {
                           user.isMuted().thenAcceptAsync((muted) -> {
                               if (muted) {
                                   ((ProxiedPlayer) event.getReceiver()).sendMessage(new ComponentBuilder("You have been muted.").create());
                               } else {
                                   getPlugin().getMessagingController().sendChatMessage((ProxiedPlayer) event.getReceiver(), getPlugin().getProxy().getPlayer(event.getReceiver().toString()).getServer(), message);
                               }
                           });
                        });
                    }
                }
            } catch (Exception e) {
                getPlugin().getLogger().info("failed to read plugin message event.");
            }
        }
    }

    @EventHandler
    public void onUserDataRecalculate(UserDataRecalculateEvent event)  {
        UUID uuid = event.getUser().getUuid();
        if (getPlugin().getStorageController().isLoaded(uuid)) {
            CachedUser user = getPlugin().getStorageController().getCachedUser(uuid);
            user.updateUserData();
        }
    }

}
