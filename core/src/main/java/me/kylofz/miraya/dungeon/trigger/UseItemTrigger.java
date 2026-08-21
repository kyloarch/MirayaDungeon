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
import me.kylofz.miraya.item.ExItem;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class UseItemTrigger extends AbstractTrigger {

    private String name;
    private ExItem item;

    public UseItemTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
        name = value;
        item = api.getXLib().getExItem(name);
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.USE_ITEM;
    }

    @Override
    public boolean isIdentifiableByValue() {
        return true;
    }

    @Override
    public boolean onTrigger(boolean switching) {
        setTriggered(true);
        return true;
    }

    /* Statics */
    public static UseItemTrigger getByItemOrName(ExItem item, String name, GameWorld gameWorld) {
        if ((item == null && name == null) || gameWorld == null) {
            return null;
        }
        for (Trigger uncasted : gameWorld.getTriggers()) {
            if (!(uncasted instanceof UseItemTrigger)) {
                continue;
            }
            UseItemTrigger trigger = (UseItemTrigger) uncasted;
            if (name != null && name.equalsIgnoreCase(trigger.name)) {
                return trigger;
            } else if (item != null && item.equals(trigger.item)) {
                return trigger;
            }
        }
        return null;
    }

}
