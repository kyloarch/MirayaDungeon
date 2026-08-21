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
package me.kylofz.miraya.dungeon.world.block;

import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.dungeon.api.DungeonsAPI;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DGamePlayer;
import me.kylofz.miraya.dungeon.player.DGroup;
import me.kylofz.miraya.category.Category;
import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.item.VanillaItem;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Daniel Saukel
 */
public class TeamBed extends TeamBlock implements MultiBlock {

    private Block attachedBlock;

    public TeamBed(DungeonsAPI api, Block block, DGroup owner) {
        super(api, block, owner);
        attachedBlock = getAttachedBlock();
    }

    /* Getters and setters */
    public Block getAttachedBlock(Block block) {
        if (Category.BEDS.containsBlock(block.getRelative(BlockFace.EAST))) {
            return block.getRelative(BlockFace.EAST);

        } else if (Category.BEDS.containsBlock(block.getRelative(BlockFace.NORTH))) {
            return block.getRelative(BlockFace.NORTH);

        } else if (Category.BEDS.containsBlock(block.getRelative(BlockFace.WEST))) {
            return block.getRelative(BlockFace.WEST);

        } else if (Category.BEDS.containsBlock(block.getRelative(BlockFace.SOUTH))) {
            return block.getRelative(BlockFace.SOUTH);

        } else {
            return null;
        }
    }

    @Override
    public Block getAttachedBlock() {
        if (attachedBlock != null) {
            return attachedBlock;

        } else {
            return getAttachedBlock(block);
        }
    }

    /* Actions */
    @Override
    public boolean onBreak(BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        if (owner.getMembers().contains(breaker)) {
            MessageUtil.sendMessage(breaker, DMessage.ERROR_BLOCK_OWN_TEAM.getMessage());
            return true;
        }

        for (DGamePlayer player : owner.getDGamePlayers()) {
            player.setLives(1);
        }
        owner.setLives(0);

        owner.getGameWorld().sendMessage(DMessage.GROUP_BED_DESTROYED.getMessage(owner.getName(), api.getPlayerCache().getGamePlayer(breaker).getName()));
        Block block1 = event.getBlock();
        if (DungeonsXL.BLOCK_ADAPTER.isBedHead(block)) {
            Block block2 = getAttachedBlock(block1);
            if (block2 != null) {
                block2.setType(VanillaItem.AIR.getMaterial());
            }
        }
        block1.setType(VanillaItem.AIR.getMaterial());
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{block=" + block + "; attachedBlock=" + attachedBlock + "; owner=" + owner + "}";
    }

}
