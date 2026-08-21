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
package me.kylofz.miraya.dungeon.mob;

import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.dungeon.api.dungeon.GameRule;
import me.kylofz.miraya.dungeon.api.dungeon.GameRuleContainer;
import me.kylofz.miraya.dungeon.api.event.mob.DungeonMobDeathEvent;
import me.kylofz.miraya.dungeon.api.world.GameWorld;
import me.kylofz.miraya.dungeon.api.world.InstanceWorld;
import me.kylofz.miraya.dungeon.trigger.MobTrigger;
import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.mob.ExMob;
import me.kylofz.miraya.mob.VanillaMob;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * @author Daniel Saukel, Frank Baumann
 */
public class DMobListener implements Listener {

    private DungeonsXL plugin;

    public DMobListener(DungeonsXL plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        World world = event.getLocation().getWorld();

        InstanceWorld instance = plugin.getInstanceWorld(world);
        if (instance == null) {
            return;
        }

        switch (event.getSpawnReason()) {
            case CHUNK_GEN:
            case JOCKEY:
            case MOUNT:
            case NATURAL:
                event.setCancelled(true);
                return;
        }

        VanillaMob vm = VanillaMob.get(event.getEntityType());
        if (vm == VanillaMob.PIGLIN || vm == VanillaMob.PIGLIN_BRUTE) {
            ((PiglinAbstract) event.getEntity()).setImmuneToZombification(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = event.getEntity();
        World world = entity.getWorld();
        GameWorld gameWorld = plugin.getGameWorld(world);
        if (gameWorld == null) {
            return;
        }
        if (!gameWorld.isPlaying()) {
            return;
        }

        DMob dMob = (DMob) plugin.getDungeonMob(entity);
        if (dMob == null) {
            return;
        }

        DungeonMobDeathEvent dMobDeathEvent = new DungeonMobDeathEvent(dMob);
        Bukkit.getServer().getPluginManager().callEvent(dMobDeathEvent);
        if (dMobDeathEvent.isCancelled()) {
            return;
        }

        ExMob type = dMob.getType();
        GameRuleContainer rules = gameWorld.getDungeon().getRules();
        if (!DMob.getDrops(type, rules.getState(GameRule.MOB_ITEM_DROPS))) {
            event.getDrops().clear();
        }
        if (!DMob.getDrops(type, rules.getState(GameRule.MOB_EXP_DROPS))) {
            event.setDroppedExp(0);
        }

        MessageUtil.debug(plugin, dMob + " dead, sets: " + dMob.getMobSets());
        dMob.getMobSets().forEach(s -> s.kill(entity));
        MobTrigger.triggerForSets(gameWorld, dMob.getMobSets());
    }

    // Prevent undead combustion from the sun.
    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event instanceof EntityCombustByEntityEvent) {
            return;
        }
        Entity entity = event.getEntity();
        if ((entity instanceof Skeleton || entity instanceof Zombie) && plugin.getGameWorld(entity.getWorld()) != null) {
            event.setCancelled(true);
        }
    }

}
