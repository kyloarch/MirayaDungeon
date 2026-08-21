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
package me.kylofz.miraya.dungeon.command;

import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.dungeon.api.dungeon.Game;
import me.kylofz.miraya.dungeon.api.event.group.GroupPlayerLeaveEvent;
import me.kylofz.miraya.dungeon.api.event.player.EditPlayerLeaveEvent;
import me.kylofz.miraya.dungeon.api.player.EditPlayer;
import me.kylofz.miraya.dungeon.api.player.GamePlayer;
import me.kylofz.miraya.dungeon.api.player.GlobalPlayer;
import me.kylofz.miraya.dungeon.api.player.PlayerGroup;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.chat.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class LeaveCommand extends DCommand {

    public LeaveCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("leave");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_LEAVE_HELP.getMessage());
        setPermission(DPermission.LEAVE.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        GlobalPlayer globalPlayer = dPlayers.get(player);
        Game game = plugin.getGame(player);

        if (game != null && game.isTutorial()) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NO_LEAVE_IN_TUTORIAL.getMessage());
            return;
        }

        PlayerGroup group = globalPlayer.getGroup();

        if (group == null && !(globalPlayer instanceof EditPlayer)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_JOIN_GROUP.getMessage());
            return;
        }

        if (globalPlayer instanceof GamePlayer) {
            GroupPlayerLeaveEvent event = new GroupPlayerLeaveEvent(group, globalPlayer);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            ((GamePlayer) globalPlayer).leave();
        } else if (globalPlayer instanceof EditPlayer) {
            EditPlayerLeaveEvent event = new EditPlayerLeaveEvent((EditPlayer) globalPlayer, false, true);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            ((EditPlayer) globalPlayer).leave(event.getUnloadIfEmpty());
        } else {
            group.removeMember(player);
        }

        MessageUtil.sendMessage(player, DMessage.CMD_LEAVE_SUCCESS.getMessage());
    }

}
