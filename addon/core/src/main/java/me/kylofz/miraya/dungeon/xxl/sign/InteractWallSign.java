/*
 * Copyright (C) 2020-2026 Daniel Saukel
 *
 * All rights reserved.
 */
package me.kylofz.miraya.dungeon.xxl.sign;

import me.kylofz.miraya.dungeon.api.DungeonsAPI;
import me.kylofz.miraya.dungeon.api.world.InstanceWorld;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.dungeon.sign.passive.InteractSign;
import me.kylofz.miraya.dungeon.trigger.InteractTrigger;
import me.kylofz.miraya.dungeon.api.trigger.LogicalExpression;
import me.kylofz.miraya.dungeon.util.BlockUtilCompat;
import org.bukkit.block.Sign;

/**
 * This sign adds an interact trigger to an attached block, like a "suspicious wall".
 *
 * @author Daniel Saukel
 */
public class InteractWallSign extends InteractSign {

    public InteractWallSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "InteractWall";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".interactwall";
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
        return true;
    }

    @Override
    public void initialize() {
        String id = getSign().getLine(1);
        InteractTrigger trigger = (InteractTrigger) getGameWorld().createTrigger(this, LogicalExpression.parse("I" + id));
        trigger.setInteractBlock(BlockUtilCompat.getAttachedBlock(getSign().getBlock()));
    }

}
