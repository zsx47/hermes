package net.thisisz.hermes.bungee.storage.tasks;

import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.storage.driver.StorageDriver;
import net.thisisz.hermes.bungee.storage.CachedUser;

public class SaveNickname implements Runnable {

    private final HermesChat plugin;
    private final CachedUser user;
    private final StorageDriver driver;

    public SaveNickname(CachedUser user, HermesChat plugin, StorageDriver driver) {
        this.user = user;
        this.plugin = plugin;
        this.driver = driver;
    }

    private HermesChat getPlugin() {
        return this.plugin;
    }

    @Override
    public void run() {
        if (driver == null) {
            getPlugin().getStorageController().getDriverInThread();
        }
        try {
            driver.setNickname(user.getUUID(), user.getNickname());
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                getPlugin().getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
            getPlugin().getLogger().warning("Failed to save nickname for uuid: " + user.getUUID().toString());
        }
    }
}
