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
import me.kylofz.miraya.dungeon.config.DMessage;
import me.kylofz.miraya.dungeon.dungeon.DDungeon;
import me.kylofz.miraya.dungeon.player.DPermission;
import me.kylofz.miraya.dungeon.world.WorldConfig;
import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.util.FileUtil;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

/**
 * @author Frank Baumann, Daniel Saukel
 */
public class ImportCommand extends DCommand {

    public ImportCommand(DungeonsXL plugin) {
        super(plugin);
        setMinArgs(1);
        setMaxArgs(1);
        setCommand("import");
        setHelp(DMessage.CMD_IMPORT_HELP.getMessage());
        setPermission(DPermission.IMPORT.getNode());
        setPlayerCommand(true);
        setConsoleCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        File target = new File(DungeonsXL.MAPS, args[1]);
        File source = new File(Bukkit.getWorldContainer(), args[1]);

        if (!source.exists()) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NO_SUCH_MAP.getMessage(args[1]));
            return;
        }

        if (target.exists()) {
            MessageUtil.sendMessage(sender, DMessage.ERROR_NAME_IN_USE.getMessage(args[1]));
            return;
        }

        World world = Bukkit.getWorld(args[1]);
        if (world != null) {
            world.save();
        }

        MessageUtil.log(plugin, "&6Creating new map.");
        MessageUtil.log(plugin, "&6Importing world...");

        FileUtil.copyDir(source, target, "playerdata", "stats");

        DDungeon dungeon = DDungeon.create(plugin, args[1]);
        if (world != null && world.getEnvironment() != Environment.NORMAL) {
            WorldConfig config = dungeon.getConfig(true);
            config.setWorldEnvironment(world.getEnvironment());
            config.save();
        }
        MessageUtil.sendMessage(sender, DMessage.CMD_IMPORT_SUCCESS.getMessage(args[1]));
    }

}
