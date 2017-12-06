package net.thisisz.hermes.spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HermesChatSpigot extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getServer().getLogger().info("Hermes bungee enabled.");
    }

    @Override
    public void onDisable() {

    }

}
