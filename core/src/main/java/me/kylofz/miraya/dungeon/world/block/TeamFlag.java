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
import me.kylofz.miraya.dungeon.api.player.GamePlayer;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DGroup;
import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.item.VanillaItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Daniel Saukel
 */
public class TeamFlag extends TeamBlock {

    public TeamFlag(DungeonsAPI api, Block block, DGroup owner) {
        super(api, block, owner);
        reset();
    }

    /* Actions */
    /**
     * Reset a team flag when the capturer dies.
     */
    public void reset() {
        DungeonsXL.BLOCK_ADAPTER.setBlockWoolColor(block, owner.getDColor());
    }

    @Override
    public boolean onBreak(BlockBreakEvent event) {
        Player breaker = event.getPlayer();
        GamePlayer gamePlayer = api.getPlayerCache().getGamePlayer(breaker);
        if (gamePlayer == null) {
            return true;
        }

        if (owner.getMembers().contains(breaker)) {
            MessageUtil.sendMessage(breaker, DMessage.ERROR_BLOCK_OWN_TEAM.getMessage());
            return true;
        }

        owner.getGameWorld().sendMessage(DMessage.GROUP_FLAG_STEALING.getMessage(gamePlayer.getName(), owner.getName()));
        gamePlayer.setRobbedGroup(owner);
        event.getBlock().setType(VanillaItem.AIR.getMaterial());
        return true;
    }

}
