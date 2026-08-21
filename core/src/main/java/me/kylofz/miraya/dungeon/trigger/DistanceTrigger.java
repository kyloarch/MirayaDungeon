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
package me.kylofz.miraya.dungeon.trigger;

import me.kylofz.miraya.dungeon.api.DungeonsAPI;
import me.kylofz.miraya.dungeon.api.trigger.AbstractTrigger;
import me.kylofz.miraya.dungeon.api.trigger.LogicalExpression;
import me.kylofz.miraya.dungeon.api.trigger.Trigger;
import me.kylofz.miraya.dungeon.api.trigger.TriggerListener;
import me.kylofz.miraya.dungeon.api.trigger.TriggerTypeKey;
import me.kylofz.miraya.dungeon.api.world.GameWorld;
import me.kylofz.miraya.util.NumberUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class DistanceTrigger extends AbstractTrigger {

    private int distance = 5;
    private Location loc;

    public DistanceTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);

        distance = NumberUtil.parseInt(value, distance);
        if (distance < 2) {
            distance = 2;
        }
        this.loc = owner.getLocation();
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.DISTANCE;
    }

    @Override
    public boolean isIdentifiableByValue() {
        return false;
    }

    @Override
    public boolean onTrigger(boolean switching) {
        setTriggered(true);
        return true;
    }

    @Override
    public void postTrigger() {
        unregisterTrigger();
        getListeners().clear();
    }

    /* Statics */
    public static void triggerAllInDistance(Player player, GameWorld gameWorld) {
        if (!player.getLocation().getWorld().equals(gameWorld.getWorld())) {
            return;
        }

        for (Trigger trigger : gameWorld.getTriggers().toArray(new Trigger[gameWorld.getTriggers().size()])) {
            if (!(trigger instanceof DistanceTrigger)) {
                continue;
            }
            DistanceTrigger distanceTrigger = (DistanceTrigger) trigger;
            if (player.getLocation().distance(distanceTrigger.loc) < distanceTrigger.distance) {
                distanceTrigger.trigger(true, player);
            }
        }
    }

}
