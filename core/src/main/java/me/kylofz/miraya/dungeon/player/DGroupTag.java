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
package me.kylofz.miraya.dungeon.player;

import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.dungeon.hologram.HologramProviders;

/**
 * Floating name tag above a player showing their group.
 *
 * @author Daniel Saukel, kylofz
 */
public class DGroupTag {

    private DGamePlayer player;
    private Object hologram;
    private double yOffset = 3.5;

    public DGroupTag(DungeonsXL plugin, DGamePlayer player) {
        this.player = player;
        DGroup group = player.getGroup();
        if (group == null) {
            return;
        }
        var provider = HologramProviders.get();
        if (provider == null) {
            return;
        }
        String name = "mirayadungeon_grouptag_" + player.getPlayer().getUniqueId().toString().substring(0, 8);
        hologram = provider.create(name, player.getPlayer().getLocation().clone().add(0, yOffset, 0),
                java.util.List.of(group.getName()));
    }

    public void update() {
        if (hologram == null) {
            return;
        }
        var provider = HologramProviders.get();
        if (provider != null) {
            provider.move(hologram, player.getPlayer().getLocation().clone().add(0, yOffset, 0));
        }
    }

}
