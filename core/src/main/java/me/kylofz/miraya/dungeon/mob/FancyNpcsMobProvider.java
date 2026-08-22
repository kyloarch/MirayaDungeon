package me.kylofz.miraya.dungeon.mob;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import me.kylofz.miraya.dungeon.api.DungeonsAPI;
import me.kylofz.miraya.dungeon.api.world.GameWorld;
import me.kylofz.miraya.dungeon.api.mob.ExternalMobProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

/**
 * ExternalMobProvider implementation for FancyNpcs.
 * <p>
 * Summons clones of an existing FancyNpcs NPC template (referenced by name) at the target location.
 *
 * @author kylofz
 */
public class FancyNpcsMobProvider implements ExternalMobProvider {

    private final DungeonsAPI api;

    private static final String IDENTIFIER = "FN";
    private static final Set<Npc> SPAWNED_NPCS = new HashSet<>();

    public FancyNpcsMobProvider(DungeonsAPI api) {
        this.api = api;
    }

    /**
     * @return the spawned FancyNpcs NPCs
     */
    public Set<Npc> getSpawnedNPCs() {
        return SPAWNED_NPCS;
    }

    public void removeSpawnedNPCs(World world) {
        for (Npc npc : new HashSet<>(SPAWNED_NPCS)) {
            Location loc = npc.getData().getLocation();
            if (loc != null && world.equals(loc.getWorld())) {
                removeSpawnedNPC(npc);
            }
        }
    }

    private void removeSpawnedNPC(Npc npc) {
        SPAWNED_NPCS.remove(npc);
        npc.removeForAll();
        FancyNpcsPlugin.get().getNpcManager().removeNpc(npc);
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String getRawCommand() {
        return null;
    }

    @Override
    public String getCommand(String mob, String world, double x, double y, double z) {
        return null;
    }

    @Override
    public void summon(String mob, Location location) {
        var manager = FancyNpcsPlugin.get().getNpcManager();
        Npc source = manager.getNpc(mob);
        if (source == null) {
            return;
        }

        GameWorld gameWorld = api.getGameWorld(location.getWorld());
        if (gameWorld == null) {
            return;
        }

        NpcData data = new NpcData(source.getData().getName() + "_" + UUID.randomUUID().toString().substring(0, 8),
                UUID.randomUUID(), location.clone());
        data.setDisplayName(source.getData().getDisplayName());
        data.setSkinData(source.getData().getSkinData());
        if (source.getData().getType() != null) {
            data.setType(source.getData().getType());
        } else {
            data.setType(EntityType.PLAYER);
        }
        data.setTurnToPlayer(source.getData().isTurnToPlayer());
        data.setShowInTab(false);
        data.setSpawnEntity(true);

        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        npc.create();
        npc.spawnForAll();
        manager.registerNpc(npc);
        SPAWNED_NPCS.add(npc);
    }

}
