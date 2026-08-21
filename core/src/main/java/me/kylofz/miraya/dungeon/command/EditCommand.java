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
import me.kylofz.miraya.dungeon.api.dungeon.Dungeon;
import me.kylofz.miraya.dungeon.api.event.player.EditPlayerEditEvent;
import me.kylofz.miraya.dungeon.api.player.GlobalPlayer;
import me.kylofz.miraya.dungeon.api.player.InstancePlayer;
import me.kylofz.miraya.dungeon.api.player.PlayerGroup;
import me.kylofz.miraya.dungeon.api.world.EditWorld;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DEditPlayer;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.config.CommonMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class EditCommand extends DCommand {

    public EditCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("edit");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessage.CMD_EDIT_HELP.getMessage());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;

        Dungeon dungeon = plugin.getDungeonRegistry().getFirstIf(d -> d.getName().equalsIgnoreCase(args[1]));
        if (dungeon == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(args[1]));
            return;
        }

        if (!dungeon.isInvitedPlayer(player) && !DPermission.hasPermission(player, DPermission.EDIT)) {
            MessageUtil.sendMessage(player, CommonMessage.CMD_NO_PERMISSION.getMessage());
            return;
        }

        boolean newlyLoaded = dungeon.getEditWorld() == null;
        EditWorld editWorld = dungeon.getOrInstantiateEditWorld(false);
        if (editWorld == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
            return;
        }

        PlayerGroup dGroup = plugin.getPlayerGroup(player);
        GlobalPlayer dPlayer = dPlayers.get(player);

        if (dPlayer instanceof InstancePlayer) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        if (dGroup != null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_GROUP.getMessage());
            return;
        }

        Bukkit.getPluginManager().callEvent(new EditPlayerEditEvent(new DEditPlayer(plugin, player, editWorld), newlyLoaded));
    }

}
