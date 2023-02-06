package com.StarJ.LM.Skills.Runnable;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;

public class ChargingRunnable extends BukkitRunnable {
	private final static HashMap<UUID, HashMap<String, BukkitTask>> charging = new HashMap<UUID, HashMap<String, BukkitTask>>();
	private final LivingEntity le;
	private final Skill skill;
	private final int maxTimes;
	private final int max_charge;
	private int startCharge;
	private int times;
	private int maintain;

	public ChargingRunnable(LivingEntity le, Skill skill, int maxTimes, int maxCharge, int startCharge) {
		this.le = le;
		this.skill = skill;
		this.maxTimes = maxTimes;
		this.times = 0;
		this.max_charge = maxCharge;
		this.startCharge = startCharge;
		this.maintain = 0;
	}

	public static void run(LivingEntity le, Skill skill, int maxTimes, int maxCharge, int tick) {
		run(le, skill, maxTimes, maxCharge, 0, tick);
	}

	public static void run(LivingEntity le, Skill skill, int maxTimes, int maxCharge, int startCharge, int tick) {
		if (skill != null)
			if (maxTimes > 0) {
				UUID key = le.getUniqueId();
				HashMap<String, BukkitTask> charges = charging.containsKey(key) ? charging.get(key)
						: new HashMap<String, BukkitTask>();
				if (charges.containsKey(skill.getKey())) {
					BukkitTask task = charges.get(skill.getKey());
					if (task != null)
						task.cancel();
				}
				charges.put(skill.getKey(), new ChargingRunnable(le, skill, maxTimes, maxCharge, startCharge)
						.runTaskTimer(Core.getCore(), tick, tick));
				charging.put(key, charges);
				Core.getCore().getHashMapStore().getDataStore(le).changeSetting(le);
			} else
				skill.HoldingSucceed(le, 0);
	}

	public static boolean has(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		if (charging.containsKey(uuid)) {
			HashMap<String, BukkitTask> charges = charging.get(uuid);
			for (String key : charges.keySet())
				if (charges.containsKey(key)) {
					BukkitTask task = charges.get(key);
					if (!task.isCancelled())
						return true;
				}
		}
		return false;
	}

	public static boolean has(LivingEntity le, Skill skill) {
		UUID uuid = le.getUniqueId();
		if (charging.containsKey(uuid)) {
			HashMap<String, BukkitTask> charges = charging.get(uuid);
			if (charges.containsKey(skill.getKey())) {
				BukkitTask task = charges.get(skill.getKey());
				if (!task.isCancelled())
					return true;
			}
		}
		return false;
	}

	public static void cancel(LivingEntity le, Skill skill) {
		UUID key = le.getUniqueId();
		if (charging.containsKey(key)) {
			HashMap<String, BukkitTask> holds = charging.get(key);
			if (holds.containsKey(skill.getKey())) {
				BukkitTask task = holds.get(skill.getKey());
				if (!task.isCancelled())
					task.cancel();
				holds.remove(skill.getKey());
				charging.put(key, holds);
			}
		}
	}

	@Override
	public void run() {
		if (skill != null && ((le instanceof Player && ((OfflinePlayer) le).isOnline()) || !le.isDead())) {
			if (this.startCharge < this.max_charge) {
				if (!(le instanceof Player) || ((Player) le).isSneaking()) {
					if (this.times == this.maxTimes) {
						this.times = 0;
						this.startCharge++;
					}
					if (le instanceof Player) {
						String sub = "";
						if (this.startCharge == this.max_charge) {
							for (int c = 0; c < maxTimes; c++)
								sub += "■";
						} else
							for (int c = 0; c < maxTimes; c++)
								if (c <= times) {
									sub += "■";
								} else
									sub += "□";
						((Player) le).sendTitle(skill.getDisplayName(), charge_color[startCharge] + sub, 1, 20, 1);
					}
					skill.Charging(le, times, startCharge);
					this.times++;
				} else {
					skill.ChargingFinish(le, startCharge);
					this.cancel();
				}
			} else {
				if ((le instanceof Player && !((Player) le).isSneaking()) || this.maintain > 5) {
					skill.ChargingFinish(le, startCharge);
					this.cancel();
				} else
					this.maintain++;
				if (le instanceof Player) {
					String sub = "";
					for (int c = 0; c < maxTimes; c++)
						sub += "■";
					((Player) le).sendTitle(skill.getDisplayName(), charge_color[startCharge] + sub, 1, 20, 1);
				}
			}
		} else
			this.cancel();
	}

	private ChatColor[] charge_color = new ChatColor[] { ChatColor.WHITE, ChatColor.GOLD, ChatColor.DARK_RED,
			ChatColor.DARK_PURPLE };
}
