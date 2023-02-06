package com.StarJ.LM.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.StarJ.LM.Core;
import com.StarJ.LM.System.ConfigStore;
import com.StarJ.LM.System.MessageStore;
import com.StarJ.LM.System.Permissions;

public class TOPCommand implements CommandExecutor, TabCompleter {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 2) {
			OfflinePlayer off = Bukkit.getOfflinePlayer(args[0]);
			if (off.isOnline()) {
				Player player = off.getPlayer();
				if (label.equalsIgnoreCase("top")) {
					if (!player.hasPermission(args[1])) {
						player.addAttachment(Core.getCore()).setPermission(args[1], true);
						MessageStore.sendSystemMsg(player, ChatColor.RED + args[1] + "의 권한을 부여했습니다.");
					} else
						MessageStore.sendSystemMsg(player, ChatColor.RED + "이미 " + args[1] + "의 권한을 가지고 있습니다.");
				} else if (label.equalsIgnoreCase("detop")) {
					if (player.hasPermission(args[1])) {
						player.addAttachment(Core.getCore()).setPermission(args[1], false);
						MessageStore.sendSystemMsg(player, ChatColor.RED + args[1] + "의 권한을 제거했습니다.");
					} else
						MessageStore.sendSystemMsg(player, ChatColor.RED + args[1] + "의 권한을 가지고 있지 않습니다.");
				}
				ConfigStore.savePermission(player);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		final List<String> list = new ArrayList<String>();
		if (args.length == 1) {
			for (Player player : Bukkit.getOnlinePlayers())
				if (args[0].equals("") || player.getName().toLowerCase().startsWith(args[0].toLowerCase()))
					list.add(player.getName());
		} else if (args.length == 2)
			for (Permissions permission : Permissions.values())
				if (args[1].equals("") || permission.name().toLowerCase().startsWith(args[1]))
					list.add(permission.name());
		return list;
	}
}
