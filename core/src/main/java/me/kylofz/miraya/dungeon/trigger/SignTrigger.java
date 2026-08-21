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

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class SignTrigger extends AbstractTrigger {

    private int id;

    public SignTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
        char key = expression.getText().charAt(0);
        int i = Character.toUpperCase(key) == getKey() ? 1 : 0;
        id = NumberUtil.parseInt(expression.getText().substring(i));
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.GENERIC;
    }

    @Override
    public boolean isIdentifiableByValue() {
        return true;
    }

    @Override
    public boolean onTrigger(boolean switching) {
        if (switching != isTriggered()) {
            setTriggered(switching);
        }
        return isTriggered();
    }

    /* Statics */
    public static SignTrigger getById(int id, GameWorld gameWorld) {
        if (gameWorld == null) {
            return null;
        }
        for (Trigger uncasted : gameWorld.getTriggers()) {
            if (!(uncasted instanceof SignTrigger)) {
                continue;
            }
            SignTrigger trigger = (SignTrigger) uncasted;
            if (id == trigger.id) {
                return trigger;
            }
        }
        return null;
    }

}
