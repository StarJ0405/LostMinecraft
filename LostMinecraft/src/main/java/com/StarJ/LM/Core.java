package com.StarJ.LM;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.StarJ.LM.Commands.MultiWorldCommand;
import com.StarJ.LM.Commands.SpawnCommand;
import com.StarJ.LM.Commands.TOPCommand;
import com.StarJ.LM.Commands.TestCommand;
import com.StarJ.LM.Listener.EntityListener;
import com.StarJ.LM.Listener.InventoryListener;
import com.StarJ.LM.Listener.PlayerListener;
import com.StarJ.LM.Listener.WorldListener;
import com.StarJ.LM.System.ConfigStore;
import com.StarJ.LM.System.EntityStore;
import com.StarJ.LM.System.HashMapStore;
import com.StarJ.LM.System.MessageStore;
import com.StarJ.LM.System.ScoreboardStore;

public class Core extends JavaPlugin {
	private static Core core;
	private World pvp;
	private World defence;
	private HashMapStore hs;
	private ScoreboardStore sb;

	public static Core getCore() {
		return core;
	}

	public World getPvp() {
		return pvp;
	}

	public World getDefence() {
		return defence;
	}

	public HashMapStore getHashMapStore() {
		return hs;
	}

	public ScoreboardStore getScoreboardStore() {
		return sb;
	}

	public void onEnable() {
		core = this;
		this.hs = new HashMapStore();
		this.sb = new ScoreboardStore();
		//
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new EntityListener(), this);
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new WorldListener(), this);
		//
		getCommand("mw").setExecutor(new MultiWorldCommand());
		getCommand("spawn").setExecutor(new SpawnCommand());
		getCommand("test").setExecutor(new TestCommand());
		getCommand("top").setExecutor(new TOPCommand());
		//
		CreateWorlds();
		for (Player player : Bukkit.getOnlinePlayers()) {
			ConfigStore.loadJob(player);
			ConfigStore.loadPermission(player);
		}

		//
		for (World world : Bukkit.getWorlds())
			for (Entity et : world.getEntities())
				if (et instanceof LivingEntity)
					EntityStore.LoadEntity((LivingEntity) et);
		MessageStore.sendServerMsg("로스트마크 플러그인이 실행되었습니다.");
	}

	public void onDisable() {
		//
		for (World world : Bukkit.getWorlds())
			for (Entity et : world.getEntities())
				if (et instanceof LivingEntity) {
					EntityStore es = EntityStore.valueOf(et);
					if (es != null)
						es.SaveEntity((LivingEntity) et);
				}

		//
		MessageStore.sendServerMsg("로스트마크 플러그인이 종료되었습니다.");
	}

	private void CreateWorlds() {
		pvp = CreateWorld("pvp");
		defence = CreateWorld("defence");
	}

	private World CreateWorld(String name) {
		World world = new WorldCreator(name).type(WorldType.FLAT).createWorld();
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
		world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
		world.setGameRule(GameRule.DISABLE_RAIDS, true);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_ENTITY_DROPS, true);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		world.setGameRule(GameRule.DO_INSOMNIA, false);
		world.setGameRule(GameRule.DO_MOB_LOOT, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
		world.setGameRule(GameRule.DO_TILE_DROPS, true);
		world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
		world.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setGameRule(GameRule.DROWNING_DAMAGE, true);
		world.setGameRule(GameRule.FALL_DAMAGE, true);
		world.setGameRule(GameRule.FIRE_DAMAGE, true);
		world.setGameRule(GameRule.FORGIVE_DEAD_PLAYERS, true);
		world.setGameRule(GameRule.FREEZE_DAMAGE, true);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
		world.setGameRule(GameRule.MOB_GRIEFING, false);
		world.setGameRule(GameRule.NATURAL_REGENERATION, true);
		world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 1);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
		world.setGameRule(GameRule.UNIVERSAL_ANGER, false);
		return world;
	}

	public boolean isVanillaWorld(World world) {
		List<World> worlds = Bukkit.getWorlds();
		return worlds.get(0).equals(world) || worlds.get(1).equals(world) || worlds.get(2).equals(world);
	}

	public boolean isPvp(World world) {
		return pvp != null && world.getName().equalsIgnoreCase(pvp.getName());
	}

	public boolean isDefence(World world) {
		return defence != null && world.getName().equalsIgnoreCase(defence.getName());
	}
}
