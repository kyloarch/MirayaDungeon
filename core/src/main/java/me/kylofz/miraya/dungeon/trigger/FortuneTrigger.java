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
import me.kylofz.miraya.dungeon.api.trigger.TriggerListener;
import me.kylofz.miraya.dungeon.api.trigger.TriggerTypeKey;
import me.kylofz.miraya.util.NumberUtil;
import java.util.Random;

/**
 * @author Daniel Saukel
 */
public class FortuneTrigger extends AbstractTrigger {

    private double chance = 0;

    public FortuneTrigger(DungeonsAPI api, TriggerListener owner, LogicalExpression expression, String value) {
        super(api, owner, expression, value);
        this.chance = NumberUtil.parseDouble(value, chance);
    }

    @Override
    public char getKey() {
        return TriggerTypeKey.FORTUNE;
    }

    @Override
    public boolean isIdentifiableByValue() {
        return false;
    }

    /* Getters and setters */
    /**
     * @return the chance
     */
    public double getChance() {
        return chance;
    }

    /**
     * @param chance the chance to set
     */
    public void setChance(double chance) {
        this.chance = chance;
    }

    /* Actions */
    @Override
    public boolean onTrigger(boolean switching) {
        int random = new Random().nextInt(100);
        if (chance * 100 >= random) {
            setTriggered(true);
        }
        return true;
    }

}
