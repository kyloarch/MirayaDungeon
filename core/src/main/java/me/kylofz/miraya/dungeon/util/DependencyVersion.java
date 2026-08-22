/*
 * Copyright (C) 2012-2013 Frank Baumann; 2015-2026 Daniel Saukel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.kylofz.miraya.dungeon.util;

import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.compatibility.Version;
import me.kylofz.miraya.plugin.PluginMeta;
import me.kylofz.miraya.plugin.VersionComparator;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Predicate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Lists compatible plugin versions.
 *
 * @author Daniel Saukel
 */
public enum DependencyVersion {

    XLIB("MirayaAPI-Runtime", getProperties().getProperty("dependencyVersion.xlib")),
    DECENT_HOLOGRAMS("DecentHolograms", "2.8.8"),
    FANCY_HOLOGRAMS("FancyHolograms", getProperties().getProperty("dependencyVersion.fancyholograms")),
    FANCY_NPCS("FancyNpcs", getProperties().getProperty("dependencyVersion.fancynpcs")),
    PARTIES("Parties", getProperties().getProperty("dependencyVersion.parties")),
    PLACEHOLDER_API("PlaceholderAPI", getProperties().getProperty("dependencyVersion.placeholderapi")),
    VAULT("Vault", "1.7.3-b131"),
    MYTHIC_MOBS("MythicMobs", "5.11.2-6a371d59");

    /**
     * Meta information about this project.
     */
    public static final PluginMeta META = new PluginMeta.Builder("DungeonsXL")
            .minVersion(Version.MC1_8_8)
            .maxVersion(Version.MC1_21_11)
            .paperState(PluginMeta.State.SUPPORTED)
            .spigotState(PluginMeta.State.SUPPORTED)
            .economyState(PluginMeta.State.SUPPORTED)
            .permissionsState(PluginMeta.State.SUPPORTED)
            .spigotMCResourceId(9488)
            .bStatsResourceId(1039)
            .versionComparator(VersionComparator.SEM_VER_SNAPSHOT)
            .build();

    private static Properties properties;

    private String name;
    private String version;
    private Plugin plugin;

    DependencyVersion(String name, String version) {
        this(name, version, null);
    }

    DependencyVersion(String name, String version, Predicate<String> enabled) {
        this.name = name;
        this.version = version;
        if (enabled == null || enabled.test(name)) {
            plugin = Bukkit.getPluginManager().getPlugin(name);
        }
    }

    public String getName() {
        return name;
    }

    public String getSupportedVersion() {
        return version;
    }

    public String getEnabledVersion() {
        return plugin.getDescription().getVersion();
    }

    public boolean isEnabled() {
        return plugin != null;
    }

    public boolean check() {
        return isEnabled() && getSupportedVersion().equals(getEnabledVersion());
    }

    public static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            try {
                properties.load(DungeonsXL.class.getClassLoader().getResourceAsStream("dxl.properties"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return properties;
    }

}
