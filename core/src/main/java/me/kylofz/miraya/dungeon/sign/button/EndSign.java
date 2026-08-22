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
package me.kylofz.miraya.dungeon.sign.button;

import me.kylofz.miraya.dungeon.api.DungeonsAPI;
import me.kylofz.miraya.dungeon.api.dungeon.GameGoal;
import me.kylofz.miraya.dungeon.api.dungeon.GameRule;
import me.kylofz.miraya.dungeon.api.sign.Button;
import me.kylofz.miraya.dungeon.api.trigger.Trigger;
import me.kylofz.miraya.dungeon.api.world.InstanceWorld;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DGamePlayer;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.dungeon.trigger.InteractTrigger;
import me.kylofz.miraya.chat.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class EndSign extends Button {

    public EndSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public Trigger getDefaultTrigger() {
        return new InteractTrigger(api, this);
    }

    @Override
    public String getName() {
        return "End";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".end";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Override
    public boolean isSetToAir() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void initialize() {
        GameGoal goal = getGame().getRules().getState(GameRule.GAME_GOAL);
        if (goal.getType() != GameGoal.Type.END) {
            setToAir();
            MessageUtil.log(api, "&4An end sign in the dungeon " + getGame().getDungeon().getName() + " is ignored because the game goal is " + goal.toString());
            return;
        }

        InteractTrigger.applyDefaultSignLayout(this, DMessage.SIGN_END.getMessage(), "");
    }

    @Override
    public boolean push(Player player) {
        DGamePlayer dPlayer = (DGamePlayer) api.getPlayerCache().getGamePlayer(player);
        if (dPlayer == null) {
            return true;
        }

        // boss key gate: requires a Boss Key item to proceed
        var plugin = (me.kylofz.miraya.dungeon.DungeonsXL) api;
        var bossKey = plugin.getShopConfig().getBossKey();
        if (bossKey.isEnabled() && !me.kylofz.miraya.dungeon.economy.BossKeyUtil.consumeBossKey(player, bossKey)) {
            player.sendMessage(org.bukkit.ChatColor.RED + "You need a "
                    + org.bukkit.ChatColor.GOLD + "Boss Key" + org.bukkit.ChatColor.RED
                    + " to proceed! Buy one in the shop (" + ChatColor.YELLOW
                    + "/mirayadungeon shop" + ChatColor.RED + ").");
            return true;
        }

        // TODO: Group with 2 players, player A finishs, player B leaves
        if (dPlayer.isFinished()) {
            return true;
        }

        // floor clear coin reward (slow progression)
        plugin.getCoinListener().onFloorClear(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                dPlayer.finish();
            }
        }.runTaskLater(api, 1L);
        return true;
    }

}
