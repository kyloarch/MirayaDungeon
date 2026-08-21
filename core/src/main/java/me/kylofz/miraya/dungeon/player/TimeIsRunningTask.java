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
package me.kylofz.miraya.dungeon.player;

import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.dungeon.api.event.group.GroupPlayerKickEvent;
import me.kylofz.miraya.dungeon.api.player.GamePlayer;
import me.kylofz.miraya.dungeon.api.player.PlayerGroup;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.chat.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Daniel Saukel
 */
public class TimeIsRunningTask extends BukkitRunnable {

    private DungeonsXL plugin;

    private PlayerGroup group;
    private int time;
    private int timeLeft;

    public TimeIsRunningTask(DungeonsXL plugin, PlayerGroup group, int time) {
        this.plugin = plugin;
        this.group = group;
        this.time = time;
        this.timeLeft = time;
    }

    @Override
    public void run() {
        timeLeft--;

        String color = ChatColor.GREEN.toString();

        try {
            color = (double) timeLeft / (double) time > 0.25 ? ChatColor.GREEN.toString() : ChatColor.DARK_RED.toString();

        } catch (ArithmeticException exception) {
            color = ChatColor.DARK_RED.toString();

        } finally {
            for (Player player : group.getMembers().getOnlinePlayers()) {
                MessageUtil.sendActionBarMessage(player, DMessage.PLAYER_TIME_LEFT.getMessage(color, String.valueOf(timeLeft)));

                GamePlayer dPlayer = plugin.getPlayerCache().getGamePlayer(player);
                if (timeLeft > 0) {
                    continue;
                }

                GroupPlayerKickEvent groupPlayerKickEvent = new GroupPlayerKickEvent(group, dPlayer, GroupPlayerKickEvent.Cause.TIME_EXPIRED);
                Bukkit.getServer().getPluginManager().callEvent(groupPlayerKickEvent);

                if (!groupPlayerKickEvent.isCancelled()) {
                    MessageUtil.broadcastMessage(DMessage.PLAYER_TIME_KICK.getMessage(player.getName()));
                    dPlayer.leave();
                }

                cancel();
            }
        }

    }

}
