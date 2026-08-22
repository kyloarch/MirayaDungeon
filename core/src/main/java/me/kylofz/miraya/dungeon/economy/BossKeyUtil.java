package me.kylofz.miraya.dungeon.economy;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Utility to identify and consume boss keys.
 *
 * @author kylofz
 */
public class BossKeyUtil {

    private BossKeyUtil() {
    }

    public static boolean isBossKey(Player player, BossKeyConfig config, ItemStack item) {
        if (!config.isEnabled() || item == null || item.getType() != config.getMaterial()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getDisplayName() != null && meta.getDisplayName().contains("Boss Key");
    }

    /**
     * Consumes one boss key from the player's inventory.
     *
     * @return true if a key was found and removed
     */
    public static boolean consumeBossKey(Player player, BossKeyConfig config) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isBossKey(player, config, item)) {
                int amount = item.getAmount();
                if (amount <= 1) {
                    player.getInventory().remove(item);
                } else {
                    item.setAmount(amount - 1);
                }
                return true;
            }
        }
        return false;
    }

}
