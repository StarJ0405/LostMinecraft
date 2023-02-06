package com.StarJ.LM.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.System.EntityStore;

public class TestCommand implements CommandExecutor, TabCompleter {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			ArmorStand as = (ArmorStand) EntityStore.Minion.spawnEntity(player.getLocation());
			Skill skill = Skill.assassin.Reaper.rageSpear;
			skill.setLevel(as, 10);
			skill.setTripod1Choice(as, 2);
//			skill.setTripod2Choice(as, 1);
//			skill.setTripod3Choice(as, 1);
			as.setMetadata("skill", new FixedMetadataValue(Core.getCore(), skill.getKey()));
			return true;
		}

		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		final List<String> list = new ArrayList<String>();
		return list;
	}
}
