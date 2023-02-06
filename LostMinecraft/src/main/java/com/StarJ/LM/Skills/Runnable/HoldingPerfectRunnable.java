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

public class HoldingPerfectRunnable extends BukkitRunnable {
	private final static HashMap<UUID, HashMap<String, BukkitTask>> holdings = new HashMap<UUID, HashMap<String, BukkitTask>>();
	private final LivingEntity le;
	private final Skill skill;
	private final int max_times;
	private final int perfect_start;
	private final int perfect_end;
	private int times;

	public HoldingPerfectRunnable(LivingEntity le, Skill skill, int max_times, int perfect_start, int perfect_end) {
		this.le = le;
		this.skill = skill;
		this.max_times = max_times;
		this.times = 0;
		this.perfect_start = perfect_start > perfect_end ? perfect_end : perfect_start;
		this.perfect_end = perfect_start > perfect_end ? perfect_start : perfect_end;
	}

	public static void run(LivingEntity le, Skill skill, int times, int perfect_start, int perfect_end, int tick) {
		if (skill != null)
			if (times > 0) {
				UUID key = le.getUniqueId();
				HashMap<String, BukkitTask> holds = holdings.containsKey(key) ? holdings.get(key)
						: new HashMap<String, BukkitTask>();
				if (holds.containsKey(skill.getKey())) {
					BukkitTask task = holds.get(skill.getKey());
					if (task != null)
						task.cancel();
				}
				holds.put(skill.getKey(), new HoldingPerfectRunnable(le, skill, times, perfect_start, perfect_end)
						.runTaskTimer(Core.getCore(), tick, tick));
				holdings.put(key, holds);
				Core.getCore().getHashMapStore().getDataStore(le).changeSetting(le);
			} else
				skill.HoldingSucceed(le, 0);

	}

	public static boolean has(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		if (holdings.containsKey(uuid)) {
			HashMap<String, BukkitTask> holds = holdings.get(uuid);
			for (String key : holds.keySet())
				if (holds.containsKey(key)) {
					BukkitTask task = holds.get(key);
					if (!task.isCancelled())
						return true;
				}
		}
		return false;
	}

	public static boolean has(LivingEntity le, Skill skill) {
		UUID uuid = le.getUniqueId();
		if (holdings.containsKey(uuid)) {
			HashMap<String, BukkitTask> holds = holdings.get(uuid);
			if (holds.containsKey(skill.getKey())) {
				BukkitTask task = holds.get(skill.getKey());
				if (!task.isCancelled())
					return true;
			}
		}
		return false;
	}

	public static void cancel(LivingEntity le, Skill skill) {
		UUID key = le.getUniqueId();
		if (holdings.containsKey(key)) {
			HashMap<String, BukkitTask> holds = holdings.get(key);
			if (holds.containsKey(skill.getKey())) {
				BukkitTask task = holds.get(skill.getKey());
				if (!task.isCancelled())
					task.cancel();
				holds.remove(skill.getKey());
				holdings.put(key, holds);
			}
		}
	}

	public void run() {
		if (skill != null && ((le instanceof Player && ((OfflinePlayer) le).isOnline()) || !le.isDead())) {
			if (this.times < this.max_times) {
				if (!(le instanceof Player) || ((Player) le).isSneaking()) {
					this.times++;
					if (le instanceof Player) {
						String sub = "";
						for (int c = 0; c < max_times; c++) {
							if (c == this.perfect_start) {
								sub += ChatColor.RED;
							} else if (c == this.perfect_end + 1)
								sub += ChatColor.WHITE;
							if (c <= times) {
								sub += "■";
							} else
								sub += "□";
						}
						((Player) le).sendTitle(ChatColor.GOLD + skill.getDisplayName(), sub, 1, 20, 1);
					}
					skill.Holding(le, times);
				} else {
					if (this.times >= this.perfect_start && this.times <= this.perfect_end) {
						skill.HoldingPerfectSucceed(le);
						if (le instanceof Player)
							((Player) le).sendTitle(ChatColor.GOLD + skill.getDisplayName(),
									ChatColor.GREEN + "SUCCEED", 1, 20, 1);
					} else {
						skill.HoldingPerfectFail(le);
						if (le instanceof Player)
							((Player) le).sendTitle(ChatColor.GOLD + skill.getDisplayName(), ChatColor.RED + "FAIL", 1,
									20, 1);
					}
					this.cancel();
				}
			} else {
				skill.HoldingPerfectFail(le);
				if (le instanceof Player)
					((Player) le).sendTitle(ChatColor.GOLD + skill.getDisplayName(), ChatColor.RED + "FAIL", 1, 20, 1);
				this.cancel();
			}
		} else
			this.cancel();
	}

}
