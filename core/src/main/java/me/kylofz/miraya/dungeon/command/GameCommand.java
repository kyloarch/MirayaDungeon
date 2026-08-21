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
import me.kylofz.miraya.dungeon.api.player.PlayerGroup;
import me.kylofz.miraya.dungeon.api.world.GameWorld;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.dungeon.DGame;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.chat.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class GameCommand extends DCommand {

    public GameCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("game");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp(DMessage.CMD_GAME_HELP.getMessage());
        setPermission(DPermission.GAME.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        PlayerGroup dGroup = plugin.getPlayerGroup(player);
        if (dGroup == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_JOIN_GROUP.getMessage());
            return;
        }

        GameWorld gameWorld = dGroup.getGameWorld();
        if (gameWorld == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_GAME.getMessage());
            return;
        }

        DGame game = (DGame) gameWorld.getGame();
        if (game == null) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_GAME.getMessage());
            return;
        }

        MessageUtil.sendCenteredMessage(sender, "&4&l[ &6Game &4&l]");
        String groups = "";
        for (PlayerGroup group : game.getGroups()) {
            groups += (group == game.getGroups().get(0) ? "" : "&b, &e") + group.getName();
        }
        MessageUtil.sendMessage(sender, "&bGroups: &e" + groups);
        MessageUtil.sendMessage(sender, "&bDungeon: &e" + (dGroup.getDungeon().getName() == null ? "N/A" : dGroup.getDungeon().getName()));
        MessageUtil.sendMessage(sender, "&bMap: &e" + (dGroup.getGameWorld() == null ? "N/A" : dGroup.getGameWorld().getName()));
        MessageUtil.sendMessage(sender, "&bKills: &e" + game.getGameKills());
    }

}
