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
import me.kylofz.miraya.dungeon.api.world.InstanceWorld;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.dungeon.util.ContainerAdapter;
import me.kylofz.miraya.item.VanillaItem;
import java.util.Arrays;
import java.util.List;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

/**
 * @author Daniel Saukel
 */
public class DungeonChestSign extends ChestSign {

    public DungeonChestSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "DungeonChest";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".dungeonchest";
    }

    @Override
    public void initialize() {
        // For consistency with reward chests but also for intuitiveness, both lines should be possible
        if (!getLine(1).isEmpty()) {
            lootTable = api.getXLib().getLootTable(getLine(1));
        }
        if (!getLine(2).isEmpty()) {
            lootTable = api.getXLib().getLootTable(getLine(2));
        }

        checkChest();
        if (chest != null) {
            setToAir();
        } else {
            getSign().getBlock().setType(VanillaItem.CHEST.getMaterial());
            chest = getSign().getBlock();
        }

        List<ItemStack> list = null;
        if (lootTable != null) {
            list = lootTable.generateLootList();
        }
        if (chestContent != null) {
            if (list != null) {
                list.addAll(Arrays.asList(chestContent));
            } else {
                list = Arrays.asList(chestContent);
            }
        }
        if (list == null) {
            return;
        }

        chestContent = Arrays.copyOfRange(list.toArray(new ItemStack[list.size()]), 0, 26);
        ContainerAdapter.getBlockInventory(chest).setContents(chestContent);
    }

}
