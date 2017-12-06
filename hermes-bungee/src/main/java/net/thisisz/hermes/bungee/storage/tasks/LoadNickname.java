package net.thisisz.hermes.bungee.storage.tasks;

import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.storage.driver.StorageDriver;
import net.thisisz.hermes.bungee.storage.CachedUser;

public class LoadNickname implements Runnable {

    private HermesChat plugin;
    private CachedUser user;
    private StorageDriver driver;

    public LoadNickname(CachedUser user, HermesChat plugin, StorageDriver driver) {
        this.user = user;
        this.plugin = plugin;
        this.driver = driver;
    }

    private HermesChat getPlugin(){
        return this.plugin;
    }

    @Override
    public void run() {
        if (driver == null) {
            getPlugin().getStorageController().getDriverInThread();
        }
        try {
            String nickname = driver.getNickname(user.getUUID());
            user.setNicknameNoSave(nickname);
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                getPlugin().getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
            getPlugin().getLogger().warning("Failed to load nickname for uuid: " + user.getUUID().toString());
        }
    }
}
