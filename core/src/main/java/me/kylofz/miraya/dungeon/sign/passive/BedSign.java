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
import me.kylofz.miraya.dungeon.api.sign.Passive;
import me.kylofz.miraya.dungeon.api.world.InstanceWorld;
import me.kylofz.miraya.dungeon.player.DGroup;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.dungeon.util.BlockUtilCompat;
import me.kylofz.miraya.dungeon.world.DGameWorld;
import me.kylofz.miraya.dungeon.world.block.TeamBed;
import me.kylofz.miraya.category.Category;
import me.kylofz.miraya.util.NumberUtil;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 * @author Daniel Saukel
 */
public class BedSign extends Passive {

    private int team;

    public BedSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "Bed";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".bed";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public boolean isSetToAir() {
        return true;
    }

    @Override
    public boolean validate() {
        return NumberUtil.parseInt(getLine(1), -1) != -1;
    }

    @Override
    public void initialize() {
        this.team = NumberUtil.parseInt(getLine(1));
        Block block = BlockUtilCompat.getAttachedBlock(getSign().getBlock());

        if (Category.BEDS.containsBlock(block)) {
            if (getGame().getGroups().size() > team) {
                ((DGameWorld) getGameWorld()).addGameBlock(new TeamBed(api, block, (DGroup) getGame().getGroups().get(team)));
            }
        } else {
            markAsErroneous("No bed attached");
        }
    }

}
