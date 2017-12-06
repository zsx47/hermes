package net.thisisz.hermes.bungee;

import java.util.UUID;

public class LoadPlayerThenCallback implements AsyncTask, Callback {

    private UUID uuid;
    private Callback callback;

    public LoadPlayerThenCallback(UUID uuid, Callback callback) {
        this.uuid = uuid;
        this.callback = callback;
    }

    @Override
    public void run() {
        //load player in this thread
        HermesChat.getPlugin().getStorageController().loadLocalUserToCache(uuid);
        //run callback
        callback.run();
    }


}
