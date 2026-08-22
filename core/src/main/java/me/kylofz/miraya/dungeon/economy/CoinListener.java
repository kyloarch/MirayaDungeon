package me.kylofz.miraya.dungeon.economy;

import java.io.File;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import me.kylofz.miraya.dungeon.DungeonsXL;

/**
 * Awards dungeon coins. Very slow progression by design.
 *
 * @author kylofz
 */
public class CoinListener implements Listener {

    private static final Random RANDOM = new Random();
    private final DungeonsXL plugin;
    private final CoinManager coins;
    private final CoinConfig config;

    public CoinListener(DungeonsXL plugin, CoinManager coins, CoinConfig config) {
        this.plugin = plugin;
        this.coins = coins;
        this.config = config;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) {
            return;
        }
        // only inside instance worlds
        if (plugin.getGameWorld(entity.getWorld()) == null) {
            return;
        }
        // rare mob-kill coins
        if (RANDOM.nextDouble() * 100 < config.getMobKillChance()) {
            award(killer, config.getMobKillCoins());
        }
    }

    /**
     * Called when a player finishes a floor (End sign / boss defeated).
     */
    public void onFloorClear(Player player) {
        award(player, config.getFloorClearCoins());
    }

    public void loadDefaults(File dataFolder) {
        File file = new File(dataFolder, "coins.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.addDefault("rewards.mob-kill-chance-percent", config.getMobKillChance());
        cfg.addDefault("rewards.mob-kill-coins", config.getMobKillCoins());
        cfg.addDefault("rewards.boss-kill-coins", config.getBossKillCoins());
        cfg.addDefault("rewards.floor-clear-coins", config.getFloorClearCoins());
        cfg.addDefault("start-balance", config.getStartBalance());
        try {
            cfg.save(file);
        } catch (java.io.IOException exception) {
            exception.printStackTrace();
        }
    }

    public String describe() {
        return ChatColor.GRAY + "Mob kill: " + ChatColor.WHITE + config.getMobKillChance() + "% chance of "
                + config.getMobKillCoins() + ChatColor.GRAY + " | Boss: " + ChatColor.WHITE
                + config.getBossKillCoins() + ChatColor.GRAY + " | Clear: " + ChatColor.WHITE
                + config.getFloorClearCoins();
    }

    private void award(Player player, int amount) {
        coins.add(player.getUniqueId(), amount);
        player.sendMessage(ChatColor.GOLD + "+" + ChatColor.YELLOW + amount + ChatColor.GOLD + " coin"
                + (amount == 1 ? "" : "s") + ChatColor.GRAY + " (" + coins.getCoins(player) + " total)");
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.7f, 1.4f);
    }

}
