package me.kylofz.miraya.dungeon.economy;

import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Coin reward settings — intentionally stingy for very slow progression.
 *
 * @author kylofz
 */
public class CoinConfig {

    private double mobKillChance = 2.0; // percent — most kills give nothing
    private int mobKillCoins = 1;
    private int bossKillCoins = 1;
    private int floorClearCoins = 3;
    private int startBalance = 0;

    public static CoinConfig load(YamlConfiguration cfg) {
        CoinConfig config = new CoinConfig();
        config.mobKillChance = cfg.getDouble("rewards.mob-kill-chance-percent", config.mobKillChance);
        config.mobKillCoins = cfg.getInt("rewards.mob-kill-coins", config.mobKillCoins);
        config.bossKillCoins = cfg.getInt("rewards.boss-kill-coins", config.bossKillCoins);
        config.floorClearCoins = cfg.getInt("rewards.floor-clear-coins", config.floorClearCoins);
        config.startBalance = cfg.getInt("start-balance", config.startBalance);

        cfg.set("rewards.mob-kill-chance-percent", config.mobKillChance);
        cfg.set("rewards.mob-kill-coins", config.mobKillCoins);
        cfg.set("rewards.boss-kill-coins", config.bossKillCoins);
        cfg.set("rewards.floor-clear-coins", config.floorClearCoins);
        cfg.set("start-balance", config.startBalance);
        return config;
    }

    public double getMobKillChance() {
        return mobKillChance;
    }

    public int getMobKillCoins() {
        return mobKillCoins;
    }

    public int getBossKillCoins() {
        return bossKillCoins;
    }

    public int getFloorClearCoins() {
        return floorClearCoins;
    }

    public int getStartBalance() {
        return startBalance;
    }

    public List<String> toLore() {
        return List.of(
                "&7Mob kill: &f" + mobKillChance + "% &7chance of &f" + mobKillCoins,
                "&7Boss kill: &f" + bossKillCoins,
                "&7Floor clear: &f" + floorClearCoins);
    }

}
