/*
 * Copyright (C) 2020-2026 Daniel Saukel
 *
 * All rights reserved.
 */
package me.kylofz.miraya.dungeon.xxl.sign;

import me.kylofz.miraya.dungeon.api.DungeonsAPI;
import me.kylofz.miraya.dungeon.api.sign.Button;
import me.kylofz.miraya.dungeon.api.world.InstanceWorld;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.dungeon.xxl.util.FireworkUtil;
import org.bukkit.block.Sign;

/**
 * @author Daniel Saukel
 */
public class FireworkSign extends Button {

    public FireworkSign(DungeonsAPI api, Sign sign, String[] lines, InstanceWorld instance) {
        super(api, sign, lines, instance);
    }

    @Override
    public String getName() {
        return "Firework";
    }

    @Override
    public String getBuildPermission() {
        return DPermission.SIGN.getNode() + ".firework";
    }

    @Override
    public boolean isOnDungeonInit() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isSetToAir() {
        return true;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void push() {
        FireworkUtil.spawnRandom(getSign().getLocation());
    }

}
