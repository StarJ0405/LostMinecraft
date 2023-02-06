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

public class BuffRunnable extends BukkitRunnable {
	private final static HashMap<UUID, HashMap<Skill, HashMap<Integer, BukkitTask>>> infos = new HashMap<UUID, HashMap<Skill, HashMap<Integer, BukkitTask>>>();
	private final LivingEntity le;
	private final Skill skill;
	private final int num;

	public BuffRunnable(LivingEntity le, Skill skill, int num) {
		this.le = le;
		this.skill = skill;
		this.num = num;
	}

	public static void run(LivingEntity le, Skill skill, double duration, int num) {
		if (skill != null)
			if (duration > 0) {
				UUID key = le.getUniqueId();
				HashMap<Skill, HashMap<Integer, BukkitTask>> skillInfo = infos.containsKey(key) ? infos.get(key)
						: new HashMap<Skill, HashMap<Integer, BukkitTask>>();
				HashMap<Integer, BukkitTask> buffs = skillInfo.containsKey(skill) ? skillInfo.get(skill)
						: new HashMap<Integer, BukkitTask>();
				if (buffs.containsKey(num)) {
					BukkitTask task = buffs.get(num);
					if (task != null)
						task.cancel();
				}
				buffs.put(num, new BuffRunnable(le, skill, num).runTaskLater(Core.getCore(), (int) duration * 20));
				skillInfo.put(skill, buffs);
				infos.put(key, skillInfo);
				Core.getCore().getHashMapStore().getDataStore(le).changeSetting(le);
			} else
				skill.BuffEnd(le, num);
	}

	public static boolean has(LivingEntity le, Skill skill) {
		UUID key = le.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, BukkitTask>> buffs = infos.get(key);
			if (buffs.containsKey(skill))
				for (BukkitTask task : buffs.get(skill).values())
					if (!task.isCancelled())
						return true;
		}
		return false;

	}

	public static boolean has(LivingEntity le, Skill skill, int num) {
		UUID key = le.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, BukkitTask>> skillInfo = infos.get(key);
			if (skillInfo.containsKey(skill)) {
				HashMap<Integer, BukkitTask> buffs = skillInfo.get(skill);
				if (buffs.containsKey(num))
					return !buffs.get(num).isCancelled();
			}
		}
		return false;
	}

	public static int getCount(LivingEntity le, Skill skill) {
		UUID key = le.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, BukkitTask>> skillInfo = infos.get(key);
			if (skillInfo.containsKey(skill))
				return skillInfo.get(skill).keySet().size();
		}
		return 1;
	}

	public static void cancel(LivingEntity le, Skill skill, int num) {
		cancel(le, skill, num, true);
	}

	public static void cancel(LivingEntity le, Skill skill, int num, boolean act) {
		UUID key = le.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, BukkitTask>> skillInfo = infos.get(key);
			if (skillInfo.containsKey(skill)) {
				HashMap<Integer, BukkitTask> buffs = skillInfo.get(skill);
				if (buffs.containsKey(num)) {
					BukkitTask task = buffs.get(num);
					if (!task.isCancelled()) {
						task.cancel();
						if (act)
							skill.BuffEnd(le, num);
					}
					buffs.remove(num);
					skillInfo.put(skill, buffs);
					infos.put(key, skillInfo);
				}
			}
		}
	}

	public static void cancelAll(LivingEntity le) {
		cancelAll(le, true);
	}

	public static void cancelAll(LivingEntity le, boolean act) {
		UUID key = le.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, BukkitTask>> skillInfo = infos.get(key);
			for (Skill skill : skillInfo.keySet()) {
				HashMap<Integer, BukkitTask> buffs = skillInfo.get(skill);
				for (int num : buffs.keySet()) {
					BukkitTask task = buffs.get(num);
					if (!task.isCancelled()) {
						task.cancel();
						if (act)
							skill.BuffEnd(le, num);
					}
					buffs.remove(num);
					skillInfo.put(skill, buffs);
					infos.put(key, skillInfo);
				}
			}
		}
	}

	@Override
	public void run() {
		if (skill != null && ((le instanceof Player && ((OfflinePlayer) le).isOnline()) || !le.isDead()))
			skill.BuffEnd(le, num);
		this.cancel();
	}

}
