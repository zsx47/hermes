package net.thisisz.hermes.bungee.storage;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.messaging.filter.Filter;
import net.thisisz.hermes.bungee.storage.driver.StorageDriver;
import net.thisisz.hermes.bungee.storage.exception.controller.GenericControllerException;
import net.thisisz.hermes.bungee.storage.exception.driver.mysql.MysqlFatalException;
import net.thisisz.hermes.bungee.storage.tasks.LoadNickname;
import net.thisisz.hermes.bungee.storage.driver.MysqlDriver;
import net.thisisz.hermes.bungee.storage.exception.driver.GenericDriverException;
import net.thisisz.hermes.bungee.storage.exception.driver.mysql.MysqlSQLException;
import net.thisisz.hermes.bungee.storage.tasks.SaveNickname;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class StorageController {

    private HermesChat plugin;
    private StorageDriver driver;
    private Map<UUID, CachedUser> userCache;

    public StorageController(HermesChat parent) {
        this.plugin = parent;
        this.userCache = new HashMap<UUID, CachedUser>();
        getDriverInThread();
    }

    public void getDriverInThread() {
        this.driver = getDriver();
    }

    private StorageDriver getDriver() {
        try {
            StorageDriver driver = new MysqlDriver(this);
            driver.runDriverInit();
            return driver;
        } catch (GenericDriverException e) {
            plugin.getLogger().warning(e.getMessage());
            if (e instanceof MysqlFatalException) {
                Exception e1 = ((MysqlFatalException) e).getException();
                if (e1 != null) {
                    plugin.getLogger().warning(e1.getMessage());
                    if (e1 instanceof MysqlSQLException) {
                        SQLException e2 = ((MysqlSQLException) e1).getSqlException();
                        if (e2 != null) {
                            plugin.getLogger().warning(e2.getMessage());
                        }
                    }
                }
            }
            return null;
        }
    }

    public void loadNicknameAsync(CachedUser user) throws GenericControllerException {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new LoadNickname(user, getPlugin(), driver));
    }

    public void loadNickname(CachedUser user) {
        Runnable loadNick = new LoadNickname(user, getPlugin(), driver);
        loadNick.run();
    }

    public void saveNicknameAsync(CachedUser user) throws GenericControllerException {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(), new SaveNickname(user, getPlugin(), driver));
    }

    public void saveNickname(CachedUser user) {
        Runnable saveNick = new SaveNickname(user, getPlugin(), driver);
        saveNick.run();
    }

    public String getNickname(ProxiedPlayer user) {
        if (userCache.containsKey(user.getUniqueId())) {
            return userCache.get(user.getUniqueId()).getNickname();
        } else {
            loadLocalUserToCache(user);
            return userCache.get(user.getUniqueId()).getNickname();
        }
    }

    public void setNickname(ProxiedPlayer user, String nickname) {
        if (userCache.containsKey(user.getUniqueId())) {
            userCache.get(user.getUniqueId()).setNickname(nickname);
        } else {
            loadLocalUserToCache(user);
            userCache.get(user.getUniqueId()).setNickname(nickname);
        }
    }

    public void loadNonLocalUserToCache(UUID uuid, String name) {
        if (userCache.containsKey(uuid)) {
            userCache.get(uuid).update();
        } else {
            userCache.put(uuid, new CachedUser(uuid, this, name));
        }
    }

    public boolean isLoaded(UUID uuid) {
        return userCache.containsKey(uuid);
    }

    @Deprecated
    public CachedUser getCachedUser(UUID uuid) {
        if (userCache.containsKey(uuid)) {
            return userCache.get(uuid);
        } else {
            if (getPlugin().getProxy().getPlayer(uuid) != null) {
                loadLocalUserToCache(uuid);
                return userCache.get(uuid);
            } else {
                if (getPlugin().getRedisBungeeAPI() != null) {
                    loadNonLocalUserToCache(uuid, getPlugin().getRedisBungeeAPI().getNameFromUuid(uuid));
                    return userCache.get(uuid);
                } else {
                    loadNonLocalUserToCache(uuid,"name loading");
                    return userCache.get(uuid);
                }
            }
        }
    }

    public CompletableFuture<CachedUser> getUser(UUID uuid) {
        if (userCache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(userCache.get(uuid));
        } else {
            return CompletableFuture.supplyAsync(() -> {
                if (getPlugin().getProxy().getPlayer(uuid) != null) {
                    loadLocalUserToCache(uuid);
                    return userCache.get(uuid);
                } else {
                    if (getPlugin().getRedisBungeeAPI() != null) {
                        loadNonLocalUserToCache(uuid, getPlugin().getRedisBungeeAPI().getNameFromUuid(uuid));
                        return userCache.get(uuid);
                    } else {
                        loadNonLocalUserToCache(uuid,"name loading");
                        return userCache.get(uuid);
                    }
                }
            });
        }
    }

    public CachedUser getCachedUser(ProxiedPlayer user) {
        return getCachedUser(user.getUniqueId());
    }

    public void loadLocalUserToCache(UUID uuid) {
        if (userCache.containsKey(uuid)) {
            userCache.get(uuid).update();
        } else {
            userCache.put(uuid, new CachedUser(uuid, this));
        }
    }

    public Map<UUID, String> findUsers(String name) {
        Map<UUID, String> uuids = new HashMap<UUID, String>();
        if (getPlugin().getRedisBungeeAPI() != null) {
            for (UUID uuid: getPlugin().getRedisBungeeAPI().getPlayersOnline()) {
                if (isLoaded(uuid)) {
                    CachedUser user = getCachedUser(uuid);
                    if (user.getName().contains(name) || user.getDisplayName().contains(name)) {
                        uuids.put(user.getUUID(), user.getName());
                    }
                } else {
                    String uname = getPlugin().getRedisBungeeAPI().getNameFromUuid(uuid);
                    if (uname.contains(name)) {
                        uuids.put(uuid, uname);
                    }
                }
            }
        } else {
            for (ProxiedPlayer player : getPlugin().getProxy().getPlayers()) {
                if (player.getDisplayName().contains(name) || player.getName().contains(name)) {
                    uuids.put(player.getUniqueId(), player.getName());
                }
            }
        }
        return uuids;
    }

    public void loadLocalUserToCache(ProxiedPlayer user) {
        loadLocalUserToCache(user.getUniqueId());
    }

    private HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    public void unloadCachedPlayer(UUID uuid) {
        userCache.remove(uuid);
    }

    public void logChat(UUID uuid, String message) {
        getPlugin().getProxy().getScheduler().runAsync(getPlugin(),() -> driver.logChat(uuid, message));
    }

}
