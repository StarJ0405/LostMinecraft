package com.StarJ.LM.Skills.Runnable;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;

public class SkillCoolRunnable extends BukkitRunnable {
	private final static HashMap<UUID, HashMap<Skill, BukkitTask>> tasks = new HashMap<UUID, HashMap<Skill, BukkitTask>>();
	private final LivingEntity le;
	private final Skill skill;

	public SkillCoolRunnable(LivingEntity le, Skill skill) {
		this.le = le;
		this.skill = skill;
	}

	public static void end(LivingEntity le, Skill skill) {
		UUID uuid = le.getUniqueId();
		if (!tasks.containsKey(uuid))
			tasks.put(uuid, new HashMap<Skill, BukkitTask>());
		HashMap<Skill, BukkitTask> hs = tasks.get(uuid);
		if (hs.containsKey(skill)) {
			BukkitTask task = hs.get(skill);
			if (!task.isCancelled())
				task.cancel();
			hs.remove(skill);
			tasks.put(uuid, hs);
		}
		skill.setCooldownNow(le, 0);
	}

	public static boolean has(LivingEntity le, Skill skill) {
		UUID uuid = le.getUniqueId();
		if (tasks.containsKey(uuid)) {
			HashMap<Skill, BukkitTask> hs = tasks.get(uuid);
			if (hs.containsKey(skill))
				return !hs.get(skill).isCancelled();
		}
		return false;
	}

	public static void run(LivingEntity le, Skill skill) {
		if (skill != null)
			run(le, skill, skill.getCooldown(le));
	}

	public static void run(LivingEntity le, Skill skill, double pre_cool) {
		if (skill != null) {
			if (pre_cool > 0) {
				int cool = (int) (pre_cool * 20);
				skill.setCooldownNow(le, cool * 1000 / 20);
				UUID uuid = le.getUniqueId();
				if (!tasks.containsKey(uuid))
					tasks.put(uuid, new HashMap<Skill, BukkitTask>());
				HashMap<Skill, BukkitTask> hs = tasks.get(uuid);
				if (hs.containsKey(skill)) {
					BukkitTask task = hs.get(skill);
					if (!task.isCancelled())
						task.cancel();
				}
				hs.put(skill, new SkillCoolRunnable(le, skill).runTaskLater(Core.getCore(), cool));
				tasks.put(uuid, hs);
			}
		}
	}

	@Override
	public void run() {
		if (skill != null && ((le instanceof Player && ((OfflinePlayer) le).isOnline()) || !le.isDead()))
			skill.End(le);
		this.cancel();
	}

}
