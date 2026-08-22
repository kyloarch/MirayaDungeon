package me.kylofz.miraya.dungeon.hologram;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Resolves the best available hologram provider: FancyHolograms first, DecentHolograms fallback.
 *
 * @author kylofz
 */
public class HologramProviders {

    private static final List<HologramProvider> PROVIDERS = List.of(new FancyHologramsProvider(), new DecentHologramsProvider());
    private static HologramProvider active;

    public static HologramProvider get() {
        if (active != null && active.isAvailable()) {
            return active;
        }
        for (HologramProvider provider : PROVIDERS) {
            if (provider.isAvailable()) {
                active = provider;
                Bukkit.getLogger().info("[MirayaDungeon] Using " + provider.getName() + " for holograms");
                return active;
            }
        }
        return null;
    }

}
