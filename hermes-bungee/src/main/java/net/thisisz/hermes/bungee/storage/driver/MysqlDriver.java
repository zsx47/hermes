package net.thisisz.hermes.bungee.storage.driver;

import net.thisisz.hermes.bungee.HermesChat;
import net.thisisz.hermes.bungee.storage.StorageController;
import net.thisisz.hermes.bungee.storage.exception.driver.mysql.MysqlConnectionException;
import net.thisisz.hermes.bungee.storage.exception.driver.mysql.MysqlDriverException;
import net.thisisz.hermes.bungee.storage.exception.driver.mysql.MysqlFatalException;

import java.sql.*;
import java.util.Objects;
import java.util.UUID;

public class MysqlDriver implements StorageDriver {

    private StorageController controller;
    private Connection connection;
    private String tablePrefix, host, database, username, password, port;

    public MysqlDriver(StorageController parent) throws MysqlFatalException {
        this.controller = parent;
        this.tablePrefix = getPlugin().getConfiguration().getString("mysql_prefix");
        this.host = getPlugin().getConfiguration().getString("mysql_host");
        this.database = getPlugin().getConfiguration().getString("mysql_database");
        this.username = getPlugin().getConfiguration().getString("mysql_user");
        this.password = getPlugin().getConfiguration().getString("mysql_pass");
        this.port = getPlugin().getConfiguration().getString("mysql_port");
    }

    public HermesChat getPlugin() {
        return HermesChat.getPlugin();
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    //retry database connection 5 times then throw MysqlDriverException if it still fails
    private void retryDatabaseConnection() throws MysqlConnectionException {
        for(int i=1; i<5; i++) {
            try {
                openConnection();
            } catch (Exception e) {
                getPlugin().getLogger().warning("Failed to reconnect to database try: " + i);
                if (getPlugin().DebugMode()) {
                    e.printStackTrace();
                }
            }
        }
        throw new MysqlConnectionException("Mysql driver failed to reconnect to database!");
    }

    //Gets new statement and attempts to handle SQLException once before throwing MysqlSQLException
    private Statement getNewStatement() {
        try {
            openConnection();
            return connection.createStatement();
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Gets new statement and attempts to handle SQLException once before throwing MysqlSQLException
    private PreparedStatement getNewPreparedStatement(String statement) {
        try {
            openConnection();
            return connection.prepareStatement(statement);
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Execute sql query string and passes other exception
    private ResultSet executeQuery(String query) {
        Statement statement = getNewStatement();
        try {
            openConnection();
            return statement.executeQuery(query);
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Execute sql query string and passes other exception
    private int executeUpdate(String query) {
        Statement statement = getNewStatement();
        try {
            openConnection();
            return statement.executeUpdate(query);
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    private DatabaseMetaData getMetaData() {
        try {
            openConnection();
            return connection.getMetaData();
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Create table if it does not exist and throw MysqlFatalException upon any error
    private void createTablesIfNotExists() throws MysqlFatalException {
        try {
            executeUpdate("CREATE TABLE IF NOT EXISTS `" + tablePrefix + "nicknames` (uuid varchar(36) not null primary key, nickname varchar(200) null, UNIQUE (uuid));");
            executeUpdate("CREATE TABLE IF NOT EXISTS " + tablePrefix + "chat_log\n" +
                    "(\n" +
                    "  uuid      VARCHAR(36)                         NOT NULL,\n" +
                    "  message   TEXT                                NULL,\n" +
                    "  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL\n" +
                    ");");
            executeUpdate("CREATE TABLE IF NOT EXISTS " + tablePrefix + "pm_log\n" +
                    "(\n" +
                    "  sender_uuid      VARCHAR(36)                         NOT NULL,\n" +
                    "  receiver_uuid      VARCHAR(36)                         NOT NULL,\n" +
                    "  message   TEXT                                NULL,\n" +
                    "  timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL\n" +
                    ");");
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                e.printStackTrace();
            }
            throw new MysqlFatalException("Failed to check tables!", e);
        }
    }

    private void checkTableFormats() throws MysqlFatalException {
        try {
            DatabaseMetaData md = getMetaData();
            ResultSet rs = md.getColumns(null, null, tablePrefix + "nicknames", "uuid");
            ResultSet rs2 = md.getColumns(null, null, tablePrefix + "nicknames", "nickname");
            ResultSet rs3 = md.getColumns(null, null, tablePrefix + "chat_log", "uuid");
            ResultSet rs4 = md.getColumns(null, null, tablePrefix + "chat_log", "message");
            ResultSet rs5 = md.getColumns(null, null, tablePrefix + "chat_log", "timestamp");
            ResultSet rs6 = md.getColumns(null, null, tablePrefix + "pm_log", "sender_uuid");
            ResultSet rs7 = md.getColumns(null, null, tablePrefix + "pm_log", "receiver_uuid");
            ResultSet rs8 = md.getColumns(null, null, tablePrefix + "pm_log", "message");
            ResultSet rs9 = md.getColumns(null, null, tablePrefix + "pm_log", "timestamp");
            if (rs.next() && rs2.next() && rs3.next() && rs4.next() && rs5.next() && rs6.next() && rs7.next() && rs8.next() && rs9.next()) {
                return;
            }
            throw new MysqlFatalException("Table format incorrect.");
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                e.printStackTrace();
            }
            throw new MysqlFatalException("Failed to check table structures.", e);
        }
    }

    //Driver init method will not make any attempt to catch MysqlFatalExceptions
    @Override
    public void runDriverInit() throws MysqlFatalException {
        try {
            openConnection();
        } catch (Exception e) {
            try {
                retryDatabaseConnection();
            } catch (MysqlConnectionException e2) {
                if (getPlugin().DebugMode()) {
                    e2.printStackTrace();
                }
            }
        }
        createTablesIfNotExists();
        checkTableFormats();
    }

    public String getNickname(UUID uuid) throws MysqlFatalException {
        try {
            ResultSet rs = executeQuery("SELECT * FROM " + tablePrefix + "nicknames WHERE uuid='" + uuid.toString() + "';");
            rs.last();
            int total = rs.getRow();
            if (total > 1) {
                throw new MysqlDriverException("More than one nickname record for each uuid.");
            } else if (total == 0) {
                return null;
            }
            if (getPlugin().DebugMode()) {
                getPlugin().getLogger().info("query returned " + total + " rows");
            }
            rs.first();
            String nickname = rs.getString("nickname");
            if (Objects.equals(nickname, "")) {
                return null;
            }
            return nickname;
        } catch (Exception e) {
            if (getPlugin().DebugMode()) {
                getPlugin().getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
            throw new MysqlFatalException("Failed to get nickname from database.", e);
        }
    }

    public void setNickname(UUID uuid, String nickname) throws MysqlFatalException {
        try {
            PreparedStatement pstmt = getNewPreparedStatement("INSERT INTO " + tablePrefix + "nicknames (uuid, nickname) VALUES (?, ?) ON DUPLICATE KEY UPDATE nickname=VALUES(nickname);");
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, nickname);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw new MysqlFatalException("Failed to set nickname for uuid '" + uuid.toString() + "'.");
        }
    }

    @Override
    public void logChat(UUID uuid, String message) {
        try {
            PreparedStatement pstmt = getNewPreparedStatement("INSERT INTO " + tablePrefix + "chat_log (uuid, message, timestamp) VALUES (?, ?, ?)");
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, message);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logPm(UUID sender, UUID receiver, String message) {
        try {
            PreparedStatement pstmt = getNewPreparedStatement("INSERT INTO " + tablePrefix + "pm_log (sender_uuid, receiver_uuid, message, timestamp) VALUES (?, ?, ?, ?)");
            pstmt.setString(1, sender.toString());
            pstmt.setString(2, receiver.toString());
            pstmt.setString(3, message);
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
