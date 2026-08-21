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
package me.kylofz.miraya.dungeon.command;

import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.dungeon.api.event.DataReloadEvent;
import me.kylofz.miraya.dungeon.api.player.GroupAdapter;
import me.kylofz.miraya.dungeon.api.player.InstancePlayer;
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.chat.DefaultFontInfo;
import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.compatibility.Version;
import java.util.Collection;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ReloadCommand extends DCommand {

    public ReloadCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("reload");
        setMinArgs(0);
        setMaxArgs(1);
        setHelp(DMessage.CMD_RELOAD_HELP.getMessage());
        setPermission(DPermission.RELOAD.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        if (plugin.isLoadingWorld()) {
            MessageUtil.sendMessage(sender, DMessage.CMD_RELOAD_FAIL.getMessage());
            return;
        }

        Collection<InstancePlayer> dPlayers = this.dPlayers.getAllInstancePlayers();
        if (!dPlayers.isEmpty() && args.length == 1 && sender instanceof Player) {
            MessageUtil.sendMessage(sender, DMessage.CMD_RELOAD_PLAYERS.getMessage());
            ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dungeonsxl reload -force");
            String message = DefaultFontInfo.getCenterSpaces(DMessage.BUTTON_OKAY.getMessage()) + DMessage.BUTTON_OKAY.getMessage();
            TextComponent text = new TextComponent(message);
            text.setClickEvent(onClick);
            ((Player) sender).spigot().sendMessage(text);
            return;
        }

        PluginManager plugins = Bukkit.getPluginManager();

        DataReloadEvent event = new DataReloadEvent();
        plugins.callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        dPlayers.forEach(InstancePlayer::leave);

        int maps = DungeonsXL.MAPS.listFiles().length - 1;
        int loaded = plugin.getInstanceCache().size();
        int players = this.dPlayers.getAllGamePlayers().size();
        String internals = Version.get().getRelocationTarget();
        String vault = "";
        if (plugins.getPlugin("Vault") != null) {
            vault = plugins.getPlugin("Vault").getDescription().getVersion();
        }
        String xlib = plugins.getPlugin("MirayaAPI-Runtime").getDescription().getVersion();

        plugin.saveData();
        plugin.initFolders();
        plugin.reload();
        plugin.checkState();
        plugin.getGroupAdapters().forEach(GroupAdapter::clear);

        MessageUtil.sendPluginTag(sender, plugin);
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_RELOAD_SUCCESS.getMessage());
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_LOADED.getMessage(String.valueOf(maps), String.valueOf(loaded), String.valueOf(players)));
        MessageUtil.sendCenteredMessage(sender, DMessage.CMD_MAIN_COMPATIBILITY.getMessage(internals, vault, xlib));
    }

}
