package me.kylofz.miraya.dungeon.economy;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Boss key config: item players must hold to fight the boss and proceed to the next level.
 *
 * @author kylofz
 */
public class BossKeyConfig {

    private boolean enabled = true;
    private Material material = Material.NETHER_STAR;
    private String name = "&6&lBoss Key";
    private List<String> lore = List.of("&7Unlocks the boss of this dungeon.", "&7Right-click the End sign with it.");
    private int shopCost = 25;
    private int clearRewardCoins = 3;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public int getShopCost() {
        return shopCost;
    }

    public void setShopCost(int shopCost) {
        this.shopCost = shopCost;
    }

    public int getClearRewardCoins() {
        return clearRewardCoins;
    }

    public void setClearRewardCoins(int clearRewardCoins) {
        this.clearRewardCoins = clearRewardCoins;
    }

    /**
     * Builds a single boss key item.
     */
    public ItemStack buildItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name.replace('&', '\u00a7'));
            meta.setLore(lore.stream().map(line -> line.replace('&', '\u00a7')).toList());
            item.setItemMeta(meta);
        }
        return item;
    }

}
