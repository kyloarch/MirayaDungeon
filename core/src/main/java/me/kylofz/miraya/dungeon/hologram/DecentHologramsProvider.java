package me.kylofz.miraya.dungeon.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * DecentHolograms implementation.
 *
 * @author kylofz
 */
public class DecentHologramsProvider implements HologramProvider {

    @Override
    public String getName() {
        return "DecentHolograms";
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("DecentHolograms") != null;
    }

    @Override
    public Object create(String name, Location location, List<String> lines) {
        removeExisting(name);
        return DHAPI.createHologram(name, location, lines);
    }

    @Override
    public void move(Object handle, Location location) {
        if (!(handle instanceof String name)) {
            return;
        }
        DHAPI.moveHologram(name, location);
    }

    @Override
    public void delete(Object handle) {
        if (!(handle instanceof String name)) {
            return;
        }
        DHAPI.removeHologram(name);
    }

    private void removeExisting(String name) {
        if (DHAPI.getHologram(name) != null) {
            DHAPI.removeHologram(name);
        }
    }

}
