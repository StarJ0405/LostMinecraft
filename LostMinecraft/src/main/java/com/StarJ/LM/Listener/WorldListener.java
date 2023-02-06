package com.StarJ.LM.Listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import com.StarJ.LM.Core;
import com.StarJ.LM.System.ConfigStore;
import com.StarJ.LM.System.EntityStore;

public class WorldListener implements Listener {
	@EventHandler
	public void Events(PlayerChangedWorldEvent e) {
		Player player = e.getPlayer();
		if (Core.getCore().isVanillaWorld(e.getFrom())) {
			if (!Core.getCore().isVanillaWorld(player.getWorld())) {
				ConfigStore.savePlayerInventory(player);
				player.setGameMode(GameMode.ADVENTURE);
			}
		} else if (Core.getCore().isVanillaWorld(player.getWorld())) {
			ConfigStore.loadPlayerInventory(player);
			player.setGameMode(GameMode.SURVIVAL);
		}
		Core.getCore().getHashMapStore().getDataStore(player).changeSetting(player);
	}

	@EventHandler
	public void Events(ChunkLoadEvent e) {
		for (Entity et : e.getChunk().getEntities())
			if (et instanceof LivingEntity)
				EntityStore.LoadEntity((LivingEntity) et);
	}

	@EventHandler
	public void Events(WorldLoadEvent e) {
		for (Entity et : e.getWorld().getEntities())
			if (et instanceof LivingEntity)
				EntityStore.LoadEntity((LivingEntity) et);
	}

	@EventHandler
	public void Events(ChunkUnloadEvent e) {
		for (Entity et : e.getChunk().getEntities())
			if (et instanceof LivingEntity) {
				EntityStore es = EntityStore.valueOf(et);
				if (es != null)
					es.SaveEntity((LivingEntity) et);
			}
	}
}
