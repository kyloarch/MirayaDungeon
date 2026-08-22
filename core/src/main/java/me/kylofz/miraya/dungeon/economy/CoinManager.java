package me.kylofz.miraya.dungeon.economy;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

/**
 * Dungeon coins — SQLite-backed, cached in memory.
 * <p>
 * Very slow progression by design: coins are rare and precious.
 *
 * @author kylofz
 */
public class CoinManager {

    private final Map<UUID, Integer> cache = new ConcurrentHashMap<>();
    private Connection connection;

    public CoinManager(File dataFolder) {
        try {
            File file = new File(dataFolder, "coins.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
            try (PreparedStatement stmt = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS miraya_coins (uuid TEXT PRIMARY KEY, balance INTEGER NOT NULL DEFAULT 0)")) {
                stmt.executeUpdate();
            }
            loadAll();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void loadAll() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT uuid, balance FROM miraya_coins");
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cache.put(UUID.fromString(rs.getString("uuid")), rs.getInt("balance"));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int getCoins(UUID uuid) {
        return cache.getOrDefault(uuid, 0);
    }

    public int getCoins(Player player) {
        return getCoins(player.getUniqueId());
    }

    public void add(UUID uuid, int amount) {
        if (amount <= 0) {
            return;
        }
        int newBalance = Math.min(getCoins(uuid) + amount, 999_999);
        set(uuid, newBalance);
    }

    /**
     * @return true if the player had enough coins and they were taken
     */
    public boolean take(UUID uuid, int amount) {
        int balance = getCoins(uuid);
        if (amount <= 0 || balance < amount) {
            return false;
        }
        set(uuid, balance - amount);
        return true;
    }

    private void set(UUID uuid, int balance) {
        cache.put(uuid, balance);
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO miraya_coins (uuid, balance) VALUES (?, ?) "
                        + "ON CONFLICT(uuid) DO UPDATE SET balance = excluded.balance")) {
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, balance);
            stmt.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
