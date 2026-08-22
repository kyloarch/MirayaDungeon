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
package me.kylofz.miraya.dungeon;

import me.kylofz.miraya.dungeon.adapter.block.BlockAdapter;
import me.kylofz.miraya.dungeon.adapter.block.BlockAdapterBlockData;
import me.kylofz.miraya.dungeon.adapter.block.BlockAdapterMagicValues;
import me.kylofz.miraya.dungeon.adapter.server.ServerAdapter;
import me.kylofz.miraya.dungeon.adapter.server.ServerAdapterPaper;
import me.kylofz.miraya.dungeon.adapter.server.ServerAdapterSpigot;
import me.kylofz.miraya.dungeon.api.DungeonModule;
import me.kylofz.miraya.dungeon.api.DungeonsAPI;
import me.kylofz.miraya.dungeon.api.Requirement;
import me.kylofz.miraya.dungeon.api.Reward;
import me.kylofz.miraya.dungeon.api.dungeon.Dungeon;
import me.kylofz.miraya.dungeon.api.dungeon.Game;
import me.kylofz.miraya.dungeon.api.dungeon.GameRule;
import me.kylofz.miraya.dungeon.api.event.group.GroupCreateEvent;
import me.kylofz.miraya.dungeon.api.mob.DungeonMob;
import me.kylofz.miraya.dungeon.api.mob.ExternalMobProvider;
import me.kylofz.miraya.dungeon.api.mob.MobSet;
import me.kylofz.miraya.dungeon.api.player.GroupAdapter;
import me.kylofz.miraya.dungeon.api.player.PlayerCache;
import me.kylofz.miraya.dungeon.api.player.PlayerClass;
import me.kylofz.miraya.dungeon.api.player.PlayerGroup;
import me.kylofz.miraya.dungeon.api.sign.DungeonSign;
import me.kylofz.miraya.dungeon.api.trigger.Trigger;
import me.kylofz.miraya.dungeon.api.world.EditWorld;
import me.kylofz.miraya.dungeon.api.world.GameWorld;
import me.kylofz.miraya.dungeon.api.world.InstanceWorld;
import me.kylofz.miraya.dungeon.command.DCommandRegistry;
import me.kylofz.miraya.dungeon.config.MainConfig;
import me.kylofz.miraya.dungeon.config.MainConfig.BackupMode;
import me.kylofz.miraya.dungeon.dungeon.DDungeon;
import me.kylofz.miraya.dungeon.global.GlobalProtectionCache;
import me.kylofz.miraya.dungeon.global.GlobalProtectionListener;
import me.kylofz.miraya.dungeon.economy.CoinConfig;
import me.kylofz.miraya.dungeon.economy.CoinListener;
import me.kylofz.miraya.dungeon.economy.CoinManager;
import me.kylofz.miraya.dungeon.economy.ShopConfig;
import me.kylofz.miraya.dungeon.economy.ShopMenu;
import me.kylofz.miraya.dungeon.mob.FancyNpcsMobProvider;
import me.kylofz.miraya.dungeon.mob.CustomExternalMobProvider;
import me.kylofz.miraya.dungeon.mob.DMob;
import me.kylofz.miraya.dungeon.mob.DMobListener;
import me.kylofz.miraya.dungeon.mob.ExternalMobPlugin;
import me.kylofz.miraya.dungeon.player.DGamePlayer;
import me.kylofz.miraya.dungeon.player.DGlobalPlayer;
import me.kylofz.miraya.dungeon.player.DGroup;
import me.kylofz.miraya.dungeon.player.DInstancePlayer;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.dungeon.player.DPlayerListener;
import me.kylofz.miraya.dungeon.player.SecureModeTask;
import me.kylofz.miraya.dungeon.player.groupadapter.*;
import me.kylofz.miraya.dungeon.reward.RewardListener;
import me.kylofz.miraya.dungeon.sign.DSignListener;
import me.kylofz.miraya.dungeon.sign.button.EndSign;
import me.kylofz.miraya.dungeon.sign.passive.RewardChestSign;
import me.kylofz.miraya.dungeon.sign.passive.SignScript;
import me.kylofz.miraya.dungeon.sign.windup.CommandScript;
import me.kylofz.miraya.dungeon.sign.windup.MobSign;
import me.kylofz.miraya.dungeon.trigger.TriggerListener;
import me.kylofz.miraya.dungeon.util.DependencyVersion;
import me.kylofz.miraya.dungeon.util.PlaceholderUtil;
import me.kylofz.miraya.dungeon.world.DEditWorld;
import me.kylofz.miraya.dungeon.world.DWorldListener;
import me.kylofz.miraya.dungeon.world.WorldConfig;
import me.kylofz.miraya.XLib;
import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.compatibility.RuntimeType;
import me.kylofz.miraya.compatibility.Version;
import me.kylofz.miraya.mob.ExMob;
import me.kylofz.miraya.plugin.PluginInit;
import me.kylofz.miraya.util.FileUtil;
import me.kylofz.miraya.util.Registry;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSignOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Frank Baumann, Tobias Schmitz, Daniel Saukel
 */
public class DungeonsXL extends JavaPlugin implements DungeonsAPI {

    /* Plugin & lib instances */
    private static DungeonsXL instance;
    private XLib xlib;
    private PluginInit init;

    /* Economy */
    private CoinManager coinManager;
    private CoinConfig coinConfig;
    private CoinListener coinListener;
    private ShopMenu shopMenu;
    private ShopConfig shopConfig;

    /* Util instances */
    public static final BlockAdapter BLOCK_ADAPTER = Version.isAtLeast(Version.MC1_13) ? new BlockAdapterBlockData() : new BlockAdapterMagicValues();
    public static final ServerAdapter SERVER_ADAPTER = RuntimeType.get() == RuntimeType.PAPER ? new ServerAdapterPaper() : new ServerAdapterSpigot();

    /* Constants */
    public static final String[] EXCLUDED_FILES = {"config.yml", "uid.dat", "DXLData.data", "data"};

    /* Folders of internal features */
    public static final File SIGNS = new File(SCRIPTS, "signs");
    public static final File COMMANDS = new File(SCRIPTS, "commands");

    /* Legacy */
    public static final Map<String, Class<? extends DungeonSign>> LEGACY_SIGNS = new HashMap<>();

    static {
        LEGACY_SIGNS.put("CHEST", RewardChestSign.class);
        LEGACY_SIGNS.put("EXTERNALMOB", MobSign.class);
        LEGACY_SIGNS.put("FLOOR", EndSign.class);
    }

    /* Caches & registries */
    private Set<DungeonModule> modules = new HashSet<>();
    private Collection<GroupAdapter> groupAdapters = new HashSet<>();
    private PlayerCache playerCache;
    private Collection<Game> gameCache;
    private Registry<String, PlayerClass> classRegistry;
    private Registry<String, Class<? extends DungeonSign>> signRegistry;
    private Registry<String, Class<? extends Requirement>> requirementRegistry;
    private Registry<String, Class<? extends Reward>> rewardRegistry;
    private Registry<String, Dungeon> dungeonRegistry;
    private Registry<Integer, InstanceWorld> instanceCache;
    private Registry<String, GameRule> gameRuleRegistry;
    private Registry<Character, Class<? extends Trigger>> triggerRegistry;
    private Registry<String, ExternalMobProvider> externalMobProviderRegistry;
    private Registry<String, PlayerGroup> playerGroupCache;

    @Deprecated
    private class SignRegistry extends Registry<String, Class<? extends DungeonSign>> {

        @Override
        public Class<? extends DungeonSign> get(String key) {
            Class<? extends DungeonSign> clss = super.get(key);
            if (clss == null) {
                return LEGACY_SIGNS.get(key);
            }
            return clss;
        }
    }

    private class GameRuleRegistry extends Registry<String, GameRule> {

        @Override
        public void add(String key, GameRule rule) {
            super.add(key, rule);
            if (loaded) {
                GameRule.DEFAULT_VALUES.setState(rule, rule.getDefaultValue());
                mainConfig.getDefaultWorldConfig().updateGameRule(rule);
                for (Dungeon dungeon : dungeonRegistry) {
                    WorldConfig cfg = ((DDungeon) dungeon).getConfig(false);
                    cfg.updateGameRule(rule);
                }
                dungeonRegistry.forEach(Dungeon::setupRules);
            }
        }

    }

    private class PlayerGroupCache extends Registry<String, PlayerGroup> {

        @Override
        public PlayerGroup get(String key) {
            PlayerGroup group = elements.get(key);
            if (group != null) {
                return group;
            }
            for (PlayerGroup value : elements.values()) {
                if (((DGroup) value).getUntaggedName().equalsIgnoreCase(key)) {
                    return value;
                }
            }
            return null;
        }

    }

    /* Global state variables */
    private boolean loaded, loadingWorld;

    private MainConfig mainConfig;

    /* Caches & registries of internal features */
    private GlobalProtectionCache protections;
    private Registry<String, SignScript> signScriptRegistry;
    private Registry<String, CommandScript> commandScriptRegistry;

    @Override
    public void onEnable() {
        if (!DependencyVersion.XLIB.getSupportedVersion().startsWith("7.0")) {// To Do: Smarter check?
            getLogger().log(Level.SEVERE, "DungeonsXL requires XLib v{0} or higher to run.", DependencyVersion.XLIB.getSupportedVersion());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;
        xlib = XLib.getInstance();
        init = new PluginInit(this, xlib, DependencyVersion.META);
        initFolders();
        DPermission.register();
        registerModule(new DXLModule());
        init();
        checkState();
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderUtil(this, "dxl").register();
        }
        if (getServer().getPluginManager().isPluginEnabled("Parties")) {
            registerGroupAdapter(new PartiesAdapter(this));
        }
        init.init(new DCommandRegistry(this, init), mainConfig.isUpdaterEnabled());

        /* Economy: coins, shop, boss key */
        coinManager = new CoinManager(getDataFolder());
        File shopFile = new File(getDataFolder(), "shops.yml");
        shopConfig = ShopConfig.load(shopFile);
        File coinFile = new File(getDataFolder(), "coins.yml");
        YamlConfiguration coinCfg = YamlConfiguration.loadConfiguration(coinFile);
        coinConfig = CoinConfig.load(coinCfg);
        try {
            coinCfg.save(coinFile);
        } catch (java.io.IOException exception) {
            exception.printStackTrace();
        }
        coinListener = new CoinListener(this, coinManager, coinConfig);
        getServer().getPluginManager().registerEvents(coinListener, this);
        shopMenu = new ShopMenu(coinManager, shopConfig);
        getServer().getPluginManager().registerEvents(shopMenu, this);

        loaded = true;
    }

    @Override
    public void onDisable() {
        if (!loaded) {
            return;
        }
        loaded = false;
        saveData();
        deleteAllInstances();
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);
        DPermission.unregister();
        if (coinManager != null) {
            coinManager.close();
            coinManager = null;
        }
    }

    public void initFolders() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        BACKUPS.mkdir();
        MAPS.mkdir();
        PLAYERS.mkdir();
        SCRIPTS.mkdir();
        CLASSES.mkdir();
        SIGNS.mkdir();
        COMMANDS.mkdir();
    }

    public void reload() {
        /* Add default values */
        requirementRegistry = new Registry<>();
        modules.forEach(m -> m.initRequirements(requirementRegistry));

        rewardRegistry = new Registry<>();
        modules.forEach(m -> m.initRewards(rewardRegistry));

        signRegistry = new SignRegistry();
        modules.forEach(m -> m.initSigns(signRegistry));

        gameRuleRegistry = new GameRuleRegistry();
        modules.forEach(m -> m.initGameRules(gameRuleRegistry));

        triggerRegistry = new Registry<>();
        modules.forEach(m -> m.initTriggers(triggerRegistry));

        mainConfig = new MainConfig(this, new File(getDataFolder(), "config.yml"));

        /* Maps & dungeons */
        // Maps
        dungeonRegistry = new Registry<>();
        for (File file : MAPS.listFiles()) {
            if (file.isDirectory() && !file.getName().equals(".raw")) {
                DDungeon.create(this, file);
            }
        }
        // Raw map to copy
        if (!DDungeon.RAW.exists()) {
            DDungeon.createRaw();
        }

        /* Scripts & global data */
        classRegistry = new Registry<>();
        for (File script : FileUtil.getFilesForFolder(CLASSES)) {
            PlayerClass clss = new PlayerClass(xlib, script);
            classRegistry.add(clss.getName(), clss);
        }
        signScriptRegistry = new Registry<>();
        for (File script : FileUtil.getFilesForFolder(SIGNS)) {
            SignScript sign = new SignScript(script);
            signScriptRegistry.add(sign.getName(), sign);
        }
        commandScriptRegistry = new Registry<>();
        for (File script : FileUtil.getFilesForFolder(COMMANDS)) {
            CommandScript cmd = new CommandScript(script);
            commandScriptRegistry.add(cmd.getName(), cmd);
        }
        protections = new GlobalProtectionCache(this);
        protections.loadAll();

        /* Integrations */
        // Mobs - Supported providers
        externalMobProviderRegistry = new Registry<>();
        for (ExternalMobPlugin externalMobPlugin : ExternalMobPlugin.values()) {
            externalMobProviderRegistry.add(externalMobPlugin.getIdentifier(), externalMobPlugin);
        }
        if (getServer().getPluginManager().getPlugin("FancyNpcs") != null) {
            FancyNpcsMobProvider fancyNpcsMobProvider = new FancyNpcsMobProvider(this);
            externalMobProviderRegistry.add("FN", fancyNpcsMobProvider);
        } else {
            MessageUtil.log(this, "Could not find compatible FancyNpcs plugin. The mob provider FancyNpcs (\"FN\") will not get enabled...");
        }
        // Mobs - Custom providers
        for (Entry<String, Object> customExternalMobProvider : mainConfig.getExternalMobProviders().entrySet()) {
            externalMobProviderRegistry.add(customExternalMobProvider.getKey(), new CustomExternalMobProvider(customExternalMobProvider));
        }

        /* Players */
        if (mainConfig.isSecureModeEnabled()) {
            new SecureModeTask(this).runTaskTimer(this, mainConfig.getSecureModeCheckInterval(), mainConfig.getSecureModeCheckInterval());
        }
        playerCache = new PlayerCache();
        playerGroupCache = new PlayerGroupCache();

        gameCache = new ArrayList<>();
        instanceCache = new Registry<>();
    }

    public void init() {
        reload();
        new BukkitRunnable() {
            @Override
            public void run() {
                playerCache.getAllInstancePlayers().forEach(p -> ((DInstancePlayer) p).update());
            }
        }.runTaskTimer(this, 2L, 2L);

        /* Initialize listeners */
        getServer().getPluginManager().registerEvents(new DWorldListener(this), this);
        getServer().getPluginManager().registerEvents(new GlobalProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new RewardListener(this), this);
        getServer().getPluginManager().registerEvents(new TriggerListener(this), this);
        getServer().getPluginManager().registerEvents(new DSignListener(this), this);
        getServer().getPluginManager().registerEvents(new DMobListener(this), this);
        getServer().getPluginManager().registerEvents(new DPlayerListener(this), this);

        // TODO: Proper fix, don't forbid in edit worlds but handle properly
        if (!Version.isAtLeast(Version.MC1_20_1)) {
            return;
        }
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerSignOpen(PlayerSignOpenEvent event) {
                if (event.getSign().getWorld().getName().startsWith("DXL_")) {
                    event.setCancelled(true);
                }
            }
        }, this);
    }

    public void saveData() {
        protections.saveAll();
        instanceCache.getAllIf(i -> i instanceof EditWorld).forEach(i -> ((DEditWorld) i).forceSave());
    }

    public void checkState() {
        Bukkit.getOnlinePlayers().forEach(p -> new DGlobalPlayer(this, p));

        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (!file.getName().startsWith("DXL_") || !file.isDirectory()) {
                continue;
            }

            if (file.getName().startsWith("DXL_Edit_")) {
                for (File mapFile : file.listFiles()) {
                    if (!mapFile.getName().startsWith(".id_")) {
                        continue;
                    }

                    String name = mapFile.getName().substring(4);

                    File resource = new File(DungeonsXL.MAPS, name);
                    File backup = new File(DungeonsXL.BACKUPS, resource.getName() + "-" + System.currentTimeMillis() + "_crashbackup");
                    FileUtil.copyDir(resource, backup);
                    // Remove all files from the backupped resource world but not the config & data that we cannot fetch from the instance.
                    remove:
                    for (File remove : FileUtil.getFilesForFolder(resource)) {
                        for (String nope : DungeonsXL.EXCLUDED_FILES) {
                            if (remove.getName().equals(nope)) {
                                continue remove;
                            }
                        }
                        remove.delete();
                    }
                    DDungeon.deleteUnusedFiles(file);
                    FileUtil.copyDir(file, resource, DungeonsXL.EXCLUDED_FILES);
                }
            }

            FileUtil.removeDir(file);
        }
    }

    /* Getters and loaders */
    /**
     * @return the plugin instance
     */
    public static DungeonsXL getInstance() {
        return instance;
    }

    public PluginInit getInitializer() {
        return init;
    }

    @Override
    public XLib getXLib() {
        return xlib;
    }

    public CoinManager getCoinManager() {
        return coinManager;
    }

    public CoinConfig getCoinConfig() {
        return coinConfig;
    }

    public CoinListener getCoinListener() {
        return coinListener;
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }

    public ShopConfig getShopConfig() {
        return shopConfig;
    }

    @Override
    public PlayerCache getPlayerCache() {
        return playerCache;
    }

    @Override
    public Collection<Game> getGameCache() {
        return gameCache;
    }

    @Override
    public Registry<String, PlayerClass> getClassRegistry() {
        return classRegistry;
    }

    @Override
    public Registry<String, Class<? extends DungeonSign>> getSignRegistry() {
        return signRegistry;
    }

    @Override
    public Registry<String, Class<? extends Requirement>> getRequirementRegistry() {
        return requirementRegistry;
    }

    @Override
    public Registry<String, Class<? extends Reward>> getRewardRegistry() {
        return rewardRegistry;
    }

    @Override
    public Registry<String, Dungeon> getDungeonRegistry() {
        return dungeonRegistry;
    }

    @Override
    public Registry<Integer, InstanceWorld> getInstanceCache() {
        return instanceCache;
    }

    @Override
    public Registry<String, GameRule> getGameRuleRegistry() {
        return gameRuleRegistry;
    }

    @Override
    public Registry<Character, Class<? extends Trigger>> getTriggerRegistry() {
        return triggerRegistry;
    }

    @Override
    public Registry<String, ExternalMobProvider> getExternalMobProviderRegistry() {
        return externalMobProviderRegistry;
    }

    @Override
    public Registry<String, PlayerGroup> getGroupCache() {
        return playerGroupCache;
    }

    @Override
    public void registerModule(DungeonModule module) {
        modules.add(module);
    }

    @Override
    public void registerGroupAdapter(GroupAdapter groupAdapter) {
        if (mainConfig.areGroupAdaptersEnabled()) {
            groupAdapters.add(groupAdapter);
        } else {
            MessageUtil.log(this, "&4The group adapter &6" + groupAdapter.getClass().getName() + " &4was not registered because the feature is disabled.");
        }
    }

    /**
     * Returns a collection of the loadedGroupAdapters
     *
     * @return a collection of GroupAdapters
     */
    public Collection<GroupAdapter> getGroupAdapters() {
        return groupAdapters;
    }

    /**
     * Returns true if the plugin is not currently in the process of enabling or disabling or entirely disabled, otherwise false.
     *
     * @return true if the plugin is not currently in the process of enabling or disabling or entirely disabled, otherwise false
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Returns true if the plugin is currently loading a world, false if not.
     * <p>
     * If the plugin is loading a world, it is locked in order to prevent loading two at once.
     *
     * @return true if the plugin is currently loading a world, false if not
     */
    public boolean isLoadingWorld() {
        return loadingWorld;
    }

    /**
     * Notifies the plugin that a world is being loaded.
     * <p>
     * If the plugin is loading a world, it is locked in order to prevent loading two at once.
     *
     * @param loadingWorld if a world is being loaded
     */
    public void setLoadingWorld(boolean loadingWorld) {
        MessageUtil.debug(this, "World loading is now " + (loadingWorld ? "LOCKED" : "UNLOCKED"));
        this.loadingWorld = loadingWorld;
    }

    /**
     * Returns the command registry.
     *
     * @return the command registry
     */
    public DCommandRegistry getCommandRegistry() {
        return (DCommandRegistry) init.getCommandRegistry();
    }

    /**
     * @return the loaded instance of MainConfig
     */
    public MainConfig getMainConfig() {
        return mainConfig;
    }

    /**
     * @return the loaded instance of GlobalProtectionCache
     */
    public GlobalProtectionCache getGlobalProtectionCache() {
        return protections;
    }

    /**
     * Returns a registry of the loaded sign scripts.
     *
     * @return a registry of the loaded sign scripts
     */
    public Registry<String, SignScript> getSignScriptRegistry() {
        return signScriptRegistry;
    }

    /**
     * Returns a registry of the loaded command scripts.
     *
     * @return a registry of the loaded command scripts
     */
    public Registry<String, CommandScript> getCommandScriptRegistry() {
        return commandScriptRegistry;
    }

    /* Object initialization */
    @Override
    public PlayerGroup createGroup(Player leader) {
        return DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, null, null, null);
    }

    @Override
    public PlayerGroup createGroup(Player leader, PlayerGroup.Color color) {
        return DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, null, color, null);
    }

    @Override
    public PlayerGroup createGroup(Player leader, String name) {
        return DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, name, null, null);
    }

    @Override
    public PlayerGroup createGroup(Player leader, Dungeon dungeon) {
        return DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, null, null, dungeon);
    }

    @Override
    public PlayerGroup createGroup(Player leader, Collection<Player> members, String name, Dungeon dungeon) {
        PlayerGroup group = DGroup.create(this, GroupCreateEvent.Cause.CUSTOM, leader, name, null, dungeon);
        if (members != null) {
            members.forEach(group::addMember);
        }
        return group;
    }

    @Override
    public DungeonMob wrapEntity(LivingEntity entity, GameWorld gameWorld, ExMob type, MobSet typeSet, Collection<MobSet> mobSets) {
        if (entity == null) {
            throw new IllegalArgumentException("entity cannot be null");
        }
        if (gameWorld == null) {
            throw new IllegalArgumentException("gameWorld cannot be null");
        }
        if (typeSet == null) {
            throw new IllegalArgumentException("Type MobSet cannot be null");
        }
        DungeonMob mob = getDungeonMob(entity);
        if (mob != null) {
            return mob;
        } else {
            return new DMob(entity, gameWorld, type, typeSet, mobSets);
        }
    }

    /* Getters */
    @Override
    public DungeonMob getDungeonMob(LivingEntity entity) {
        GameWorld gameWorld = getGameWorld(entity.getWorld());
        if (gameWorld == null) {
            return null;
        }
        for (DungeonMob mob : gameWorld.getMobs()) {
            if (mob.getEntity() == entity) {
                return mob;
            }
        }
        return null;
    }

    @Override
    public PlayerGroup getPlayerGroup(Player member) {
        for (PlayerGroup group : playerGroupCache) {
            if (group.getMembers().contains(member)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public Game getGame(Player player) {
        for (Game game : gameCache) {
            if (game.getPlayers().contains(player)) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Game getGame(World world) {
        GameWorld gameWorld = getGameWorld(world);
        return gameWorld != null ? gameWorld.getGame() : null;
    }

    @Override
    public GameWorld getGameWorld(World world) {
        InstanceWorld instance = getInstanceWorld(world);
        return instance instanceof GameWorld ? (GameWorld) instance : null;
    }

    @Override
    public EditWorld getEditWorld(World world) {
        InstanceWorld instance = getInstanceWorld(world);
        return instance instanceof EditWorld ? (EditWorld) instance : null;
    }

    public InstanceWorld getInstanceWorld(World world) {
        for (InstanceWorld instance : instanceCache) {
            if (world.equals(instance.getWorld())) {
                return instance;
            }
        }
        return null;
    }

    @Override
    public boolean isInstance(World world) {
        return world.getName().startsWith("DXL_Game_") || world.getName().startsWith("DXL_Edit_");
    }

    @Override
    public boolean isDungeonItem(ItemStack itemStack) {
        if (!Version.isAtLeast(Version.MC1_16_5)) {
            return false;
        }
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        return itemStack.getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("dungeon_item", this), PersistentDataType.BYTE);
    }

    @Override
    public ItemStack setDungeonItem(ItemStack itemStack, boolean dungeonItem) {
        if (!Version.isAtLeast(Version.MC1_16_5)) {
            return null;
        }
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return null;
        }
        ItemStack dIStack = itemStack.clone();
        ItemMeta meta = dIStack.getItemMeta();
        NamespacedKey key = NamespacedKey.fromString("dungeon_item", this);
        if (dungeonItem) {
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        } else {
            meta.getPersistentDataContainer().remove(key);
        }
        dIStack.setItemMeta(meta);
        return dIStack;
    }

    /**
     * Clean up all instances.
     */
    public void deleteAllInstances() {
        BackupMode backupMode = mainConfig.getBackupMode();
        for (InstanceWorld instance : instanceCache.getAll()) {
            if (backupMode == BackupMode.ON_DISABLE | backupMode == BackupMode.ON_DISABLE_AND_SAVE && instance instanceof EditWorld) {
                instance.getDungeon().backup();
            }

            instance.delete();
        }
    }

    /**
     * Checks if an old player wrapper instance of the user exists. If yes, the old Player of the user is replaced with the new object.
     *
     * @param player the player to check
     * @return if the player exists
     */
    public boolean checkPlayer(Player player) {
        DGamePlayer dPlayer = (DGamePlayer) playerCache.getFirstGamePlayerIf(p -> p.getUniqueId().equals(player.getUniqueId()));
        if (dPlayer == null) {
            return false;
        }

        dPlayer.setPlayer(player);
        playerCache.remove(dPlayer);
        playerCache.add(player, dPlayer);
        dPlayer.setOfflineTimeMillis(0);
        return true;
    }

}
