package me.kylofz.miraya.dungeon.economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.util.List;

/**
 * Chest GUI shop. Items are defined by admins in shops.yml; bought with dungeon coins.
 *
 * @author kylofz
 */
public class ShopMenu implements Listener {

    public static final String TITLE = ChatColor.DARK_GRAY + "Miraya Shop";
    private final CoinManager coins;
    private final ShopConfig config;

    public ShopMenu(CoinManager coins, ShopConfig config) {
        this.coins = coins;
        this.config = config;
    }

    public void open(Player player) {
        var entries = config.getEntries();
        int size = Math.min(54, Math.max(27, ((entries.size() + 1 + 8) / 9) * 9));
        var inv = Bukkit.createInventory(null, size, TITLE);

        for (ShopConfig.ShopEntry entry : entries.values()) {
            inv.addItem(entry.buildDisplayItem());
        }

        // boss key entry pinned at the end
        if (config.getBossKey().isEnabled()) {
            ItemStack key = config.getBossKey().buildItem();
            var meta = key.getItemMeta();
            if (meta != null) {
                List<String> lore = new java.util.ArrayList<>(meta.getLore() != null ? meta.getLore() : List.of());
                lore.add("");
                lore.add(ChatColor.GOLD + "Cost: " + ChatColor.YELLOW + config.getBossKey().getShopCost() + " coins");
                meta.setLore(lore);
                key.setItemMeta(meta);
            }
            inv.addItem(key);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(TITLE)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player) || event.getCurrentItem() == null
                || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        // boss key?
        if (config.getBossKey().isEnabled() && event.getCurrentItem().getType() == config.getBossKey().getMaterial()
                && event.getCurrentItem().getItemMeta() != null
                && event.getCurrentItem().getItemMeta().getDisplayName().contains("Boss Key")) {
            buy(player, config.getBossKey().getShopCost(), config.getBossKey().buildItem(), "Boss Key");
            return;
        }

        // regular item — match by display name
        String clickedName = event.getCurrentItem().getItemMeta() != null
                ? event.getCurrentItem().getItemMeta().getDisplayName() : "";
        for (ShopConfig.ShopEntry entry : config.getEntries().values()) {
            if (entry.buildDisplayItem().getItemMeta() != null
                    && entry.buildDisplayItem().getItemMeta().getDisplayName().equals(clickedName)) {
                buy(player, entry);
                return;
            }
        }
    }

    private void buy(Player player, ShopConfig.ShopEntry entry) {
        if (!coins.take(player.getUniqueId(), entry.getCost())) {
            player.sendMessage(ChatColor.RED + "Not enough coins! You have "
                    + ChatColor.YELLOW + coins.getCoins(player) + ChatColor.RED + ".");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }
        deliver(player, entry.buildPurchaseItem(), entry.isCommand(), entry.getId(), entry.getCommand());
    }

    private void buy(Player player, int cost, ItemStack item, String label) {
        if (!coins.take(player.getUniqueId(), cost)) {
            player.sendMessage(ChatColor.RED + "Not enough coins! You have "
                    + ChatColor.YELLOW + coins.getCoins(player) + ChatColor.RED + ".");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }
        deliver(player, item, false, label.toLowerCase().replace(' ', '_'), null);
    }

    private void deliver(Player player, ItemStack item, boolean isCommand, String id, String command) {
        if (isCommand && command != null) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        } else {
            player.getInventory().addItem(item).values()
                    .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
        }
        player.sendMessage(ChatColor.GREEN + "Bought " + ChatColor.WHITE
                + id.replace('_', ' ') + ChatColor.GREEN + " for "
                + ChatColor.YELLOW + coins.getCoins(player) + ChatColor.GREEN + " coins left.");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
    }

}
