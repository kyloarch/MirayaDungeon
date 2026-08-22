package me.kylofz.miraya.dungeon.hologram;

import java.util.List;
import org.bukkit.Location;

/**
 * Abstraction over hologram plugins (FancyHolograms, DecentHolograms).
 *
 * @author kylofz
 */
public interface HologramProvider {

    String getName();

    boolean isAvailable();

    /**
     * Creates a hologram.
     *
     * @param name  unique identifier
     * @param lines text lines, "Item:<id>" prefix spawns an item line where supported
     * @return opaque handle for later move/delete
     */
    Object create(String name, Location location, List<String> lines);

    void move(Object handle, Location location);

    void delete(Object handle);

}
