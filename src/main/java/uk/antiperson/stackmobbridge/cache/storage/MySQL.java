package uk.antiperson.stackmobbridge.cache.storage;

import uk.antiperson.stackmobbridge.StackMobBridge;
import uk.antiperson.stackmobbridge.UuidUtil;
import uk.antiperson.stackmobbridge.cache.StackStorage;
import uk.antiperson.stackmobbridge.cache.StorageManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class MySQL extends StackStorage {

    private String hostname;
    private int port;
    private String dbName;
    private String username;
    private String password;
    private Connection connection;
    private StackMobBridge smb;

    public MySQL(StackMobBridge smb, StorageManager storageManager) {
        super(storageManager);
        this.smb = smb;
        hostname = smb.getMainConfig().getString("database.ip");
        port = smb.getMainConfig().getInt("database.port");
        dbName = smb.getMainConfig().getString("database.name");
        username = smb.getMainConfig().getString("database.username");
        password = smb.getMainConfig().getString("database.password");
    }

    @Override
    public void loadStorage() {
        smb.getLogger().info("Connecting to database...");
        try {
            makeConnection();
            smb.getLogger().info("Database connection successful!");
            // Convert existing CHAR column UUIDs to BINARY type
            if(isOldUUIDStorageType()) {
                convertToBinaryUUIDStorage();
                return;
            }
            if (connection.createStatement().executeQuery("SHOW TABLES LIKE 'stackmob'").next()) {
                try (ResultSet rs = connection.prepareStatement("SELECT HEX(uuid) as uuid, size FROM stackmob").executeQuery()) {
                    while (rs.next()) {
                        UUID uuid = UuidUtil.fromString(rs.getString(1));
                        int size = rs.getInt(2);
                        getStorageManager().getAmountCache().put(uuid, size);
                    }
                }
            }
        } catch (SQLException e) {
            smb.getLogger().warning("An issue occurred while connecting to the database.");
            smb.getLogger().warning("Please make sure that your database details are correct.");
            e.printStackTrace();
        }
    }

    private boolean isOldUUIDStorageType(){
        try (ResultSet rs = connection.prepareStatement("SELECT * FROM stackmob LIMIT 0").executeQuery()){
            return rs.getMetaData().getColumnType(1) == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    private void convertToBinaryUUIDStorage() {
        smb.getLogger().info("Converting existing database to use BINARY type UUIDs");
        try (ResultSet rs = connection.prepareStatement("SELECT UUID, size FROM stackmob").executeQuery()){
            while (rs.next()){
                getStorageManager().getAmountCache().put(UUID.fromString(rs.getString(1)), rs.getInt(2));
            }
            saveStorage(getStorageManager().getAmountCache());
        }catch (SQLException e){
            smb.getLogger().warning("An error occurred while converting existing database.");
            e.printStackTrace();
        }
    }

    @Override
    public void saveStorage(Map<UUID, Integer> values) {
        try {
            connection.createStatement().execute("DROP TABLE IF EXISTS stackmob");
            connection.createStatement().execute("CREATE TABLE stackmob (uuid BINARY(16) NOT NULL UNIQUE, size INT NOT NULL, primary key (uuid))");
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO stackmob (uuid, size) VALUES (UNHEX(?), ?)")) {
                for (Map.Entry<UUID, Integer> entry : values.entrySet()) {
                    if (entry.getValue() <= 1) continue;
                    statement.setString(1, UuidUtil.filterString(entry.getKey().toString()));
                    statement.setInt(2, entry.getValue());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        closeConnection();
    }

    private void makeConnection() throws SQLException {
        if(connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?useSSL=false&rewriteBatchedStatements=true";
            connection = DriverManager.getConnection(url, username, password);
        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
