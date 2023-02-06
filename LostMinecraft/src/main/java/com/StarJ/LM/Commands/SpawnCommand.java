package com.StarJ.LM.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.StarJ.LM.System.EntityStore;

public class SpawnCommand implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1 || args.length == 2) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				EntityStore es = EntityStore.valueOf(args[0]);
				if (es != null) {
					Location loc = player.getLocation();
					if (args.length == 2)
						if (args[1].equals("1")) {
							loc.setPitch(0f);
						} else if (args[1].equals("2")) {
							loc.setPitch(0f);
							loc.setYaw(0f);
						} else if (args[1].equals("3")) {
							loc = loc.getBlock().getLocation();
						}
					es.spawnEntity(loc);
					return true;
				}
			}
		} else if (args.length == 5 || args.length == 7) {
			try {
				EntityStore es = EntityStore.valueOf(args[0]);
				if (es != null) {
					World world = Bukkit.getWorld(args[1]);
					if (world != null) {
						double x = Double.parseDouble(args[2]);
						double y = Double.parseDouble(args[3]);
						double z = Double.parseDouble(args[4]);
						float yaw = 0f;
						float pitch = 0f;
						if (args.length == 6) {
							yaw = Float.parseFloat(args[5]);
							pitch = Float.parseFloat(args[6]);
						}
						Location loc = new Location(world, x, y, z, yaw, pitch);
						es.spawnEntity(loc);
						return true;
					}
				}
			} catch (NumberFormatException nfe) {

			}
		}
		return false;
	}
	// 0 : 전체 복사, 1: pitch0 위치 복사, 2 : 위치 복사, 3: 블럭 복사
	// spawn [type]
	// spawn [type] [0/1/2]
	// spawn [type] [world] <x> <y> <z>
	// spawn [type] [world] <x> <y> <z> <yaw> <pitch>

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		final List<String> list = new ArrayList<String>();
		if (args.length == 1) {
			for (EntityStore es : EntityStore.values())
				if (args[args.length - 1].equals("")
						|| es.name().toUpperCase().startsWith(args[args.length - 1].toUpperCase()))
					list.add(es.name());
		} else if (args.length == 2) {
			if (args[args.length - 1].equals("")) {
				list.add("0");
				list.add("1");
				list.add("2");
			}
			for (World world : Bukkit.getWorlds())
				if (args[args.length - 1].equals("")
						|| world.getName().toUpperCase().startsWith(args[args.length - 1].toUpperCase()))
					list.add(world.getName());
		} else if (sender instanceof Player) {
			Location loc = ((Player) sender).getLocation();
			if (args.length == 3) {
				list.add(loc.getBlockX() + "");
				list.add(loc.getX() + "");
			} else if (args.length == 4) {
				list.add(loc.getBlockY() + "");
				list.add(loc.getY() + "");
			} else if (args.length == 5) {
				list.add(loc.getBlockZ() + "");
				list.add(loc.getZ() + "");
			}
		}
		return list;
	}
}
