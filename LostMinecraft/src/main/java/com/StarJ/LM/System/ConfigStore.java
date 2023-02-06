package com.StarJ.LM.System;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;

public class ConfigStore {
	public static void savePlayerData(Player player, JobStore job) {
		if (job == null)
			return;
		try {
			File file = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + "/"
					+ job.getGroup().name() + "/" + job.name() + ".yml");
			File f_loc = new File(
					"plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + "/" + job.getGroup().name());
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				f_loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			if (job.getSkills() != null)
				for (Skill skill : job.getSkills()) {
					ConfigurationSection cs = fc.isConfigurationSection(skill.getKey())
							? fc.getConfigurationSection(skill.getKey())
							: fc.createSection(skill.getKey());
					cs.set("레벨", skill.getLevel(player));
					cs.set("트라이포드1", skill.getTripod1Choice(player));
					cs.set("트라이포드2", skill.getTripod2Choice(player));
					cs.set("트라이포드3", skill.getTripod3Choice(player));
					fc.set(skill.getKey(), cs);
				}
			if (ds.getSkills() != null) {
				Skill[] slots = ds.getSkills();
				for (int i = 0; i < 8; i++)
					if (slots.length > i)
						if (slots[i] != null) {
							fc.set(i + "", slots[i].getKey());
						} else
							fc.set(i + "", null);
			}
			fc.set("치명", ds.getCritical());
			fc.set("신속", ds.getSpeed());
			fc.set("인내", ds.getEnduration());
			fc.set("특화", ds.getSpecialization());
			fc.save(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void loadPlayerData(Player player, JobStore job) {
		if (job == null)
			return;
		try {
			File file = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + "/"
					+ job.getGroup().name() + "/" + job.name() + ".yml");
			File f_loc = new File(
					"plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + "/" + job.getGroup().name());
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				f_loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			if (job.getSkills() != null)
				for (Skill skill : job.getSkills())
					if (fc.isConfigurationSection(skill.getKey())) {
						ConfigurationSection cs = fc.getConfigurationSection(skill.getKey());
						if (cs.isInt("레벨"))
							skill.setLevel(player, cs.getInt("레벨"));
						if (cs.isInt("트라이포드1"))
							skill.setTripod1Choice(player, cs.getInt("트라이포드1"));
						if (cs.isInt("트라이포드2"))
							skill.setTripod2Choice(player, cs.getInt("트라이포드2"));
						if (cs.isInt("트라이포드3"))
							skill.setTripod3Choice(player, cs.getInt("트라이포드3"));
					}
			for (int i = 0; i < 8; i++)
				if (fc.isString(i + "")) {
					ds.setSkill(Skill.valueOf(fc.getString(i + "")), i);
				} else
					ds.setSkill(null, i);
			if (fc.isDouble("치명"))
				ds.setCritical(fc.getDouble("치명"));
			else
				ds.setCritical(0);
			if (fc.isDouble("신속"))
				ds.setSpeed(fc.getDouble("신속"));
			else
				ds.setSpeed(0);
			if (fc.isDouble("인내"))
				ds.setEnduration(fc.getDouble("인내"));
			else
				ds.setEnduration(0);
			if (fc.isDouble("특화"))
				ds.setSpecialization(fc.getDouble("특화"));
			else
				ds.setSpecialization(0);

		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void saveJob(Player player) {
		try {
			File file = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + "/player.yml");
			File f_loc = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString());
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				f_loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			fc.set("직업", ds.getJob().name());
			fc.save(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void loadJob(Player player) {
		try {
			File file = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + "/player.yml");
			File f_loc = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString());
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				f_loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			if (fc.isString("직업"))
				try {
					JobStore job = JobStore.valueOf(fc.getString("직업"));
					if (job != null)
						ds.setJob(player, job);
				} catch (Exception ex) {
					ds.setJob(player, ds.getJob());
				}
			else
				ds.setJob(player, ds.getJob());
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void savePermission(Player player) {
		try {
			File file = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + "/player.yml");
			File f_loc = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString());
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				f_loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);
			ConfigurationSection permissionCS = fc.isConfigurationSection("권한") ? fc.getConfigurationSection("권한")
					: fc.createSection("권한");
			for (Permissions ps : Permissions.values())
				permissionCS.set(ps.name(), player.hasPermission(ps.name()));
			fc.save(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void loadPermission(Player player) {
		try {
			File file = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + "/player.yml");
			File f_loc = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString());
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				f_loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);
			if (fc.isConfigurationSection("권한")) {
				ConfigurationSection permissionCS = fc.getConfigurationSection("권한");
				for (Permissions ps : Permissions.values())
					if (permissionCS.isBoolean(ps.name()))
						permissionCS.set(ps.name(), permissionCS.getBoolean(ps.name()));
			} else
				for (Permissions ps : Permissions.values())
					player.addAttachment(Core.getCore()).setPermission(ps.name(), false);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void savePlayerInventory(Player player) {
		try {
			File file = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + "/player.yml");
			File f_loc = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString());
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				f_loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);
			fc.set("name", player.getName());
			ConfigurationSection cs = fc.isConfigurationSection("items") ? fc.getConfigurationSection("items")
					: fc.createSection("items");
			ItemStack[] contents = player.getInventory().getContents();
			for (int i = 0; i < contents.length; i++) {
				ItemStack item = contents[i];
				cs.set(i + "", item != null ? item : new ItemStack(Material.AIR));
			}
			cs.set("level", player.getLevel());
			cs.set("exp", player.getExp());
			fc.save(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void loadPlayerInventory(Player player) {
		try {
			File file = new File("plugins/LostMinecraft/Player/" + player.getUniqueId().toString() + ".yml");
			File f_loc = new File("plugins/LostMinecraft/Player");
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				f_loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);
			ConfigurationSection cs = fc.isConfigurationSection("items") ? fc.getConfigurationSection("items")
					: fc.createSection("items");
			for (int i = 0; i < player.getInventory().getContents().length; i++)
				player.getInventory().setItem(i,
						cs.isItemStack(i + "") ? cs.getItemStack(i + "") : new ItemStack(Material.AIR));
			player.setLevel(cs.isInt("level") ? cs.getInt("level") : 0);
			player.setExp(cs.isDouble("exp") ? (float) cs.getDouble("exp") : 0f);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static Location StringToLocation(String sloc) {
		if (sloc != null) {
			String[] sp = sloc.split(",");
			if (sp.length == 4) {
				World world = Bukkit.getWorld(sp[0]);
				if (world != null)
					try {
						return new Location(world, Integer.valueOf(sp[1]), Integer.valueOf(sp[2]),
								Integer.valueOf(sp[3]));
					} catch (NumberFormatException nfe) {
						nfe.printStackTrace();
					}
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	private static String LocationToString(Location loc) {
		return loc != null
				? loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ()
				: null;
	}

	public static void Save() {
		try {
			File file = new File("plugins/LostMinecraft/config.yml");
			File loc = new File("plugins/LostMinecraft");
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);

			fc.save(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void Load() {
		try {
			File file = new File("plugins/LostMinecraft/config.yml");
			File loc = new File("plugins/LostMinecraft");
			FileConfiguration fc = new YamlConfiguration();
			if (!file.exists()) {
				loc.mkdirs();
				file.createNewFile();
			}
			fc.load(file);

		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
