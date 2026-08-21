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
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.chat.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class UninviteCommand extends DCommand {

    public UninviteCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("uninvite");
        setMinArgs(2);
        setMaxArgs(2);
        setHelp(DMessage.CMD_UNINVITE_HELP.getMessage());
        setPermission(DPermission.UNINVITE.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Dungeon dungeon = plugin.getDungeonRegistry().get(args[2]);
        if (dungeon == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(args[2]));
            return;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if (dungeon.removeInvitedPlayer(player)) {
            MessageUtil.sendMessage(sender, DMessage.CMD_UNINVITE_SUCCESS.getMessage(args[1], args[2]));
        }
    }

}
