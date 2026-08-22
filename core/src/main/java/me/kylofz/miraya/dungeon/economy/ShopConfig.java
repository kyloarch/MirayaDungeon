package me.kylofz.miraya.dungeon.economy;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Admin-configured shop items. Loaded from shops.yml.
 *
 * @author kylofz
 */
public class ShopConfig {

    public static class ShopEntry {
        private String id;
        private Material material = Material.GRASS_BLOCK;
        private String name = "&fItem";
        private List<String> lore = List.of();
        private int cost = 1;
        private int amount = 1;
        private String command = null; // if set, runs as console with %player% instead of giving the item

        public String getId() { return id; }
        public Material getMaterial() { return material; }
        public String getName() { return name; }
        public List<String> getLore() { return lore; }
        public int getCost() { return cost; }
        public int getAmount() { return amount; }
        public boolean isCommand() { return command != null; }
        public String getCommand() { return command; }

        public ItemStack buildDisplayItem() {
            ItemStack item = new ItemStack(material, Math.max(1, Math.min(64, amount)));
            var meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name.replace('&', '\u00a7'));
                List<String> fullLore = new ArrayList<>(lore);
                fullLore.add("");
                fullLore.add("&6Cost: &e" + cost + " coins");
                meta.setLore(fullLore.stream().map(l -> l.replace('&', '\u00a7')).toList());
                item.setItemMeta(meta);
            }
            return item;
        }

        public ItemStack buildPurchaseItem() {
            ItemStack item = new ItemStack(material, Math.max(1, Math.min(64, amount)));
            var meta = item.getItemMeta();
            if (meta != null && command == null) {
                meta.setDisplayName(name.replace('&', '\u00a7'));
                meta.setLore(lore.stream().map(l -> l.replace('&', '\u00a7')).toList());
                item.setItemMeta(meta);
            }
            return item;
        }
    }

    private final Map<String, ShopEntry> entries = new LinkedHashMap<>();
    private BossKeyConfig bossKey;

    public static ShopConfig load(File file) {
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ShopConfig config = new ShopConfig();

        // boss key entry
        config.bossKey = new BossKeyConfig();
        config.bossKey.setEnabled(cfg.getBoolean("boss-key.enabled", true));
        String materialName = cfg.getString("boss-key.material", "NETHER_STAR");
        Material mat = Material.matchMaterial(materialName);
        config.bossKey.setMaterial(mat != null ? mat : Material.NETHER_STAR);
        config.bossKey.setName(cfg.getString("boss-key.name", "&6&lBoss Key"));
        config.bossKey.setLore(cfg.getStringList("boss-key.lore"));
        if (config.bossKey.getLore().isEmpty()) {
            config.bossKey.setLore(List.of("&7Unlocks the boss of this dungeon.", "&7Use it at the End sign."));
        }
        config.bossKey.setShopCost(cfg.getInt("boss-key.cost", 25));

        cfg.addDefault("boss-key.enabled", true);
        cfg.addDefault("boss-key.material", "NETHER_STAR");
        cfg.addDefault("boss-key.name", "&6&lBoss Key");
        cfg.addDefault("boss-key.cost", 25);

        ConfigurationSection section = cfg.getConfigurationSection("items");
        if (section != null) {
            for (String id : section.getKeys(false)) {
                ShopEntry entry = new ShopEntry();
                entry.id = id;
                String m = section.getString(id + ".material", "STONE");
                Material material = Material.matchMaterial(m);
                entry.material = material != null ? material : Material.STONE;
                entry.name = section.getString(id + ".name", "&f" + id);
                entry.lore = section.getStringList(id + ".lore");
                entry.cost = Math.max(0, section.getInt(id + ".cost", 1));
                entry.amount = Math.max(1, section.getInt(id + ".amount", 1));
                String cmd = section.getString(id + ".command");
                entry.command = cmd == null || cmd.isBlank() ? null : cmd;
                config.entries.put(id.toLowerCase(), entry);
            }
            // persist defaults for first run
            if (config.entries.isEmpty()) {
                cfg.set("items.example_item.material", "GOLDEN_APPLE");
                cfg.set("items.example_item.name", "&eGolden Apple");
                cfg.set("items.example_item.cost", 10);
                cfg.set("items.example_item.amount", 1);
            }
        } else {
            cfg.set("items.example_item.material", "GOLDEN_APPLE");
            cfg.set("items.example_item.name", "&eGolden Apple");
            cfg.set("items.example_item.cost", 10);
            cfg.set("items.example_item.amount", 1);
        }

        try {
            cfg.save(file);
        } catch (java.io.IOException exception) {
            exception.printStackTrace();
        }
        return config;
    }

    public Map<String, ShopEntry> getEntries() {
        return entries;
    }

    public BossKeyConfig getBossKey() {
        return bossKey;
    }

}
