package net.thisisz.hermes.bungee.storage.tasks;

import me.lucko.luckperms.api.caching.UserData;
import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.storage.CachedUser;

import java.util.UUID;

public class LoadPrefix implements Runnable {

    private CachedUser user;


    public LoadPrefix(CachedUser user) {
        this.user = user;
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    @Override
    public void run() {
        UUID luckUUID = getPlugin().getLuckApi().getUuidCache().getUUID(user.getUUID());
        try {
            UserData udat = getPlugin().getLuckApi().getUser(luckUUID).getCachedData();
            user.setUserData(udat);
        } catch (NullPointerException e) {
            getPlugin().getLuckApi().getStorage().loadUser(luckUUID).join();
            UserData udat = getPlugin().getLuckApi().getUser(luckUUID).getCachedData();
            getPlugin().getLuckApi().cleanupUser(getPlugin().getLuckApi().getUser(luckUUID));
            user.setUserData(udat);
        }
    }
}
