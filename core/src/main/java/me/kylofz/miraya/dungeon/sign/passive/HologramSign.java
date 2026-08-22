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
package me.kylofz.miraya.dungeon.sign.passive;

import me.kylofz.miraya.dungeon.api.DungeonsAPI;
import me.kylofz.miraya.dungeon.api.dungeon.GameRule;
import me.kylofz.miraya.dungeon.api.sign.Passive;
import me.kylofz.miraya.dungeon.api.world.InstanceWorld;
import me.kylofz.miraya.dungeon.hologram.HologramProviders;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.item.ExItem;
import me.kylofz.miraya.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;

/**
 * @author Daniel Saukel, kylofz
 */
public class HologramSign extends Passive {

    private Object hologram;

    public HologramSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "Hologram";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".hologram";
    }

    @Override
    public boolean isOnDungeonInit() {
        return true;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isSetToAir() {
        return true;
    }

    @Override
    public boolean validate() {
        if (HologramProviders.get() == null) {
            markAsErroneous("FancyHolograms or DecentHolograms not enabled");
            return false;
        }
        return true;
    }

    @Override
    public void initialize() {
        String text = getGameWorld().getDungeon().getRules().getState(GameRule.MESSAGES).get(NumberUtil.parseInt(getLine(1)));
        if (text == null) {
            markAsErroneous("Unknown message, ID: " + getLine(1));
            return;
        }
        String[] holoLines = text.split("(?i)<br>");
        Location location = getSign().getLocation();
        location = location.add(0.5, NumberUtil.parseDouble(getLine(2), 2.0), 0.5);

        var provider = HologramProviders.get();
        if (provider == null) {
            markAsErroneous("No hologram provider");
            return;
        }

        java.util.List<String> lines = new java.util.ArrayList<>();
        for (String line : holoLines) {
            if (line.startsWith("Item:")) {
                String id = line.replace("Item:", "");
                ExItem exItem = api.getXLib().getExItem(id);
                if (exItem != null) {
                    lines.add("[item] " + exItem.toString());
                }
            } else {
                lines.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
        hologram = provider.create("mirayadungeon_holosign_" + getSign().getLocation().getBlockX()
                + "_" + getSign().getLocation().getBlockY() + "_" + getSign().getLocation().getBlockZ(), location, lines);
    }

}
