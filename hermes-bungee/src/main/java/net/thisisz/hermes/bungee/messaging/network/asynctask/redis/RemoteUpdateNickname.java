package net.thisisz.hermes.bungee.messaging.network.asynctask.redis;

import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.messaging.network.object.NetworkNicknameUpdate;
import net.thisisz.hermes.bungee.messaging.network.provider.RedisBungeeProvider;

public class RemoteUpdateNickname implements Runnable {

    private NetworkNicknameUpdate nicknameUpdate;

    public RemoteUpdateNickname(NetworkNicknameUpdate nicknameUpdate) {
        this.nicknameUpdate = nicknameUpdate;
    }
    
    private HermesChat getPlugin() {
    	return HermesChat.getPlugin();
    }

    @Override
    public void run() {
        if (getPlugin().getStorageController().isLoaded(nicknameUpdate.getUUID())) {
        	getPlugin().getStorageController().getCachedUser(nicknameUpdate.getUUID()).setNicknameNoSave(nicknameUpdate.getNewNickname());
        }
    }

}
