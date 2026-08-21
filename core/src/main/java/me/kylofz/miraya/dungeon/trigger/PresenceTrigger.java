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
import me.kylofz.miraya.dungeon.api.trigger.LogicalExpression;
import me.kylofz.miraya.dungeon.api.trigger.TriggerListener;
import me.kylofz.miraya.dungeon.api.trigger.TriggerTypeKey;

/**
 * @author Daniel Saukel
 */
public class PresenceTrigger extends DistanceTrigger {

    public PresenceTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.PRESENCE;
    }

    @Override
    public boolean onTrigger(boolean switching) {
        setTriggered(true);
        return true;
    }

    @Override
    public void postTrigger() {
        setTriggered(false);
    }

}
