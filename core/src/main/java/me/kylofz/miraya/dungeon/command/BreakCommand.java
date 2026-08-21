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
import me.kylofz.miraya.dungeon.api.player.GlobalPlayer;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.chat.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class BreakCommand extends DCommand {

    public BreakCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("break");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_BREAK_HELP.getMessage());
        setPermission(DPermission.BREAK.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        GlobalPlayer dGlobalPlayer = dPlayers.get(player);

        if (dGlobalPlayer.isInBreakMode()) {
            dGlobalPlayer.setInBreakMode(false);
            MessageUtil.sendMessage(sender, DMessage.CMD_BREAK_PROTECTED_MODE.getMessage());

        } else {
            dGlobalPlayer.setInBreakMode(true);
            MessageUtil.sendMessage(sender, DMessage.CMD_BREAK_BREAK_MODE.getMessage());
        }
    }

}
