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
import me.kylofz.miraya.dungeon.api.dungeon.Game;
import me.kylofz.miraya.dungeon.api.event.group.GroupCreateEvent;
import me.kylofz.miraya.dungeon.api.player.GlobalPlayer;
import me.kylofz.miraya.dungeon.api.world.GameWorld;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.dungeon.DGame;
import me.kylofz.miraya.dungeon.player.DGamePlayer;
import me.kylofz.miraya.dungeon.player.DGroup;
import me.kylofz.miraya.dungeon.player.DInstancePlayer;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.config.CommonMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Daniel Saukel
 */
public class TestCommand extends DCommand {

    public TestCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("test");
        setMinArgs(1);
        setMaxArgs(1);
        setHelp(DMessage.CMD_TEST_HELP.getMessage());
        setPlayerCommand(true);
        setConsoleCommand(false);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        GlobalPlayer dPlayer = dPlayers.get(player);
        if (dPlayer instanceof DInstancePlayer) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_DUNGEON.getMessage());
            return;
        }

        Dungeon dungeon = plugin.getDungeonRegistry().get(args[1]);
        if (dungeon == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NO_SUCH_DUNGEON.getMessage(args[1]));
            return;
        }

        if (!dungeon.isInvitedPlayer(player) && !DPermission.hasPermission(player, DPermission.TEST)) {
            MessageUtil.sendMessage(player, CommonMessage.CMD_NO_PERMISSION.getMessage());
            return;
        }

        DGroup group = (DGroup) dPlayer.getGroup();
        if (group != null && group.isPlaying()) {
            MessageUtil.sendMessage(player, DMessage.ERROR_LEAVE_GROUP.getMessage());
            return;
        } else if (group == null) {
            group = DGroup.create(plugin, GroupCreateEvent.Cause.COMMAND, player, null, null, dungeon);
            if (group == null) {
                return;
            }
        }
        if (!group.getLeader().equals(player) && !DPermission.hasPermission(player, DPermission.BYPASS)) {
            MessageUtil.sendMessage(player, DMessage.ERROR_NOT_LEADER.getMessage());
            return;
        }
        group.setDungeon(dungeon);

        if (!dPlayer.checkRequirements(dungeon)) {
            return;
        }

        Game game = new DGame(plugin, dungeon, group);
        game.setRewards(false);
        GameWorld gameWorld = game.ensureWorldIsLoaded(false);
        if (gameWorld == null) {
            MessageUtil.sendMessage(player, DMessage.ERROR_TOO_MANY_INSTANCES.getMessage());
            return;
        }
        for (Player groupPlayer : group.getMembers().getOnlinePlayers()) {
            new DGamePlayer(plugin, groupPlayer, group.getGameWorld());
        }
    }

}
