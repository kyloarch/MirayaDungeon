/*
 * Copyright (C) 2020-2026 Daniel Saukel
 *
 * All rights reserved.
 */
package me.kylofz.miraya.dungeon.xxl;

import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.dungeon.api.DungeonModule;
import me.kylofz.miraya.dungeon.api.Requirement;
import me.kylofz.miraya.dungeon.api.Reward;
import me.kylofz.miraya.dungeon.api.dungeon.GameRule;
import me.kylofz.miraya.dungeon.api.sign.DungeonSign;
import me.kylofz.miraya.dungeon.api.trigger.Trigger;
import me.kylofz.miraya.dungeon.xxl.requirement.*;
import me.kylofz.miraya.dungeon.xxl.sign.*;
import me.kylofz.miraya.dungeon.xxl.util.GlowUtil;
import me.kylofz.miraya.compatibility.Internals;
import me.kylofz.miraya.plugin.PluginInit;
import me.kylofz.miraya.plugin.DREPluginSettings;
import me.kylofz.miraya.util.Registry;

/**
 * @author Daniel Saukel
 */
public class DungeonsXXL extends PluginInit implements DungeonModule {

    private static DungeonsXXL instance;
    private DungeonsXL dxl;
    private GlowUtil glowUtil;

    public DungeonsXXL() {
        settings = DREPluginSettings.builder()
                .internals(Internals.v1_16_R3)
                .metrics(false)
                .spigotMCResourceId(-1)
                .build();
    }

    @Override
    public void onEnable() {
        instance = this;
        dxl = DungeonsXL.getInstance();
        glowUtil = new GlowUtil(this);
    }

    /**
     * Returns the instance of this plugin.
     *
     * @return the instance of this plugin
     */
    public static DungeonsXXL getInstance() {
        return instance;
    }

    /**
     * Returns the current {@link me.kylofz.miraya.dungeon.DungeonsXL} singleton.
     *
     * @return the current {@link me.kylofz.miraya.dungeon.DungeonsXL} singleton
     */
    public DungeonsXL getDXL() {
        return dxl;
    }

    /**
     * The loaded instance of GlowUtil.
     *
     * @return the loaded instance of GlowUtil
     */
    public GlowUtil getGlowUtil() {
        return glowUtil;
    }

    @Override
    public void initRequirements(Registry<String, Class<? extends Requirement>> registry) {
        registry.add("feeItems", FeeItemsRequirement.class);
    }

    @Override
    public void initRewards(Registry<String, Class<? extends Reward>> registry) {
    }

    @Override
    public void initSigns(Registry<String, Class<? extends DungeonSign>> registry) {
        registry.add("FIREWORK", FireworkSign.class);
        registry.add("GLOWINGBLOCK", GlowingBlockSign.class);
        registry.add("INTERACTWALL", InteractWallSign.class);
        registry.add("PARTICLE", ParticleSign.class);
    }

    @Override
    public void initGameRules(Registry<String, GameRule> registry) {
    }

    @Override
    public void initTriggers(Registry<Character, Class<? extends Trigger>> triggerRegistry) {
    }

}
