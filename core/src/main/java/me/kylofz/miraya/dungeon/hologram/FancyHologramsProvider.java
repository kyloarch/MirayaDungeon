package me.kylofz.miraya.dungeon.hologram;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import de.oliver.fancyholograms.api.data.TextHologramData;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * FancyHolograms implementation.
 *
 * @author kylofz
 */
public class FancyHologramsProvider implements HologramProvider {

    @Override
    public String getName() {
        return "FancyHolograms";
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("FancyHolograms") != null;
    }

    @Override
    public Object create(String name, Location location, List<String> lines) {
        HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
        deleteExisting(manager, name);
        TextHologramData data = new TextHologramData(name, location.clone());
        data.setText(lines);
        Hologram hologram = manager.create(data);
        hologram.createHologram();
        manager.addHologram(hologram);
        return hologram;
    }

    @Override
    public void move(Object handle, Location location) {
        if (!(handle instanceof Hologram hologram)) {
            return;
        }
        hologram.getData().setLocation(location.clone());
        hologram.refreshForViewersInWorld();
    }

    @Override
    public void delete(Object handle) {
        if (!(handle instanceof Hologram hologram)) {
            return;
        }
        hologram.deleteHologram();
        FancyHologramsPlugin.get().getHologramManager().removeHologram(hologram);
    }

    private void deleteExisting(HologramManager manager, String name) {
        manager.getHologram(name).ifPresent(existing -> {
            existing.deleteHologram();
            manager.removeHologram(existing);
        });
    }

}
