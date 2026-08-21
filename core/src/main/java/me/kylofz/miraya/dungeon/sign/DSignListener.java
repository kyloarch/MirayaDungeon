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
package me.kylofz.miraya.dungeon.sign;

import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.dungeon.api.DungeonsAPI;
import me.kylofz.miraya.dungeon.api.player.GamePlayer;
import me.kylofz.miraya.dungeon.api.sign.DungeonSign;
import me.kylofz.miraya.dungeon.api.world.EditWorld;
import me.kylofz.miraya.dungeon.api.world.GameWorld;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DPlayerListener;
import me.kylofz.miraya.dungeon.trigger.InteractTrigger;
import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.item.VanillaItem;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DSignListener implements Listener {

    private DungeonsAPI api;

    public DSignListener(DungeonsAPI api) {
        this.api = api;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (DPlayerListener.isCitizensNPC(player)) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        GamePlayer dPlayer = api.getPlayerCache().getGamePlayer(player);
        if (dPlayer == null) {
            return;
        }

        GameWorld gameWorld = dPlayer.getGameWorld();
        if (gameWorld == null) {
            return;
        }

        InteractTrigger trigger = InteractTrigger.getByBlock(clickedBlock, gameWorld);
        if (trigger == null) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            trigger.trigger(true, player);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        if (lines[0].length() < 3 || !lines[0].startsWith("[")) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (!(state instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) state;
        EditWorld editWorld = api.getEditWorld(sign.getWorld());
        if (editWorld == null) {
            return;
        }

        // Override sign plugins color codes etc.
        sign.setLine(0, lines[0]);
        sign.setLine(1, lines[1]);
        sign.setLine(2, lines[2]);
        sign.setLine(3, lines[3]);

        if (DungeonsXL.LEGACY_SIGNS.containsKey(lines[0].substring(1, lines[0].length() - 1).toUpperCase())) {
            MessageUtil.sendMessage(player, ChatColor.RED + "Error: This sign is deprecated!");
            MessageUtil.sendMessage(player, ChatColor.LIGHT_PURPLE + "https://github.com/DRE2N/DungeonsXL/wiki/Legacy-support#updating");
            event.setCancelled(true);
            event.getBlock().setType(VanillaItem.AIR.getMaterial());
            return;
        }

        DungeonSign dsign = editWorld.createDungeonSign(sign, sign.getLines());
        if (dsign == null) {
            return;
        }

        if (!player.hasPermission(dsign.getBuildPermission())) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NO_PERMISSIONS.getMessage());
            return;
        }

        if (dsign.validate()) {
            editWorld.registerSign(block);
            MessageUtil.sendMessage(player, DMessage.PLAYER_SIGN_CREATED.getMessage());

        } else {
            editWorld.removeDungeonSign(block);
            MessageUtil.sendMessage(player, DMessage.ERROR_SIGN_WRONG_FORMAT.getMessage());
        }
    }

}
