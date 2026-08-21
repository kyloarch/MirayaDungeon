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
import me.kylofz.miraya.dungeon.api.event.group.GroupPlayerLeaveEvent;
import me.kylofz.miraya.dungeon.api.player.GamePlayer;
import me.kylofz.miraya.dungeon.api.sign.Button;
import me.kylofz.miraya.dungeon.api.trigger.Trigger;
import me.kylofz.miraya.dungeon.api.world.InstanceWorld;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.dungeon.trigger.InteractTrigger;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Milan Albrecht, Daniel Saukel
 */
public class LeaveSign extends Button {

    public LeaveSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public Trigger getDefaultTrigger() {
        return new InteractTrigger(api, this);
    }

    @Override
    public String getName() {
        return "Leave";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".leave";
    }

    @Override
    public boolean isOnDungeonInit() {
        return true;
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
        InteractTrigger.applyDefaultSignLayout(this, DMessage.SIGN_LEAVE.getMessage(), "");
    }

    @Override
    public boolean push(Player player) {
        GamePlayer gamePlayer = api.getPlayerCache().getGamePlayer(player);
        if (gamePlayer != null) {
            GroupPlayerLeaveEvent event = new GroupPlayerLeaveEvent(gamePlayer.getGroup(), gamePlayer);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            gamePlayer.leave();
        }

        return true;
    }

}
