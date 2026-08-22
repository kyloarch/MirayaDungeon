package me.kylofz.miraya.dungeon.command;

import me.kylofz.miraya.chat.MessageUtil;
import me.kylofz.miraya.dungeon.DungeonsXL;
import me.kylofz.miraya.dungeon.player.DPermission;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Opens the dungeon coin shop or shows balance.
 *
 * @author kylofz
 */
public class ShopCommand extends DCommand {

    public ShopCommand(DungeonsXL plugin) {
        super(plugin);
        setCommand("shop");
        setMinArgs(0);
        setMaxArgs(0);
        setHelp("&6/mirayadungeon shop &7- Open the dungeon coin shop.");
        setPermission(DPermission.MAIN.getNode());
        setPlayerCommand(true);
    }

    @Override
    public void onExecute(String[] args, CommandSender sender) {
        Player player = (Player) sender;
        var coins = plugin.getCoinManager();
        MessageUtil.sendMessage(sender, ChatColor.GOLD + "Coins: " + ChatColor.YELLOW + coins.getCoins(player));
        plugin.getShopMenu().open(player);
    }

}
