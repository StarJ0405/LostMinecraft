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

public class ComboCoolRunnable extends BukkitRunnable {
	private final static HashMap<UUID, HashMap<Skill, ComboInfo>> tasks = new HashMap<UUID, HashMap<Skill, ComboInfo>>();
	private final LivingEntity le;
	private final Skill skill;

	public ComboCoolRunnable(LivingEntity le, Skill skill) {
		this.le = le;
		this.skill = skill;
	}

	public static boolean hasCombo(LivingEntity le, Skill skill) {
		UUID key = le.getUniqueId();
		if (tasks.containsKey(key)) {
			HashMap<Skill, ComboInfo> hs = tasks.get(key);
			if (hs.containsKey(skill))
				return !hs.get(skill).getTask().isCancelled();
		}
		return false;
	}

	public static int getCombo(LivingEntity le, Skill skill) {
		UUID key = le.getUniqueId();
		if (tasks.containsKey(key)) {
			HashMap<Skill, ComboInfo> hs = tasks.get(key);
			if (hs.containsKey(skill))
				return hs.get(skill).getCombo();
		}
		return 0;
	}

	public static void setCombo(LivingEntity le, Skill skill, int combo) {
		UUID key = le.getUniqueId();
		if (tasks.containsKey(key)) {
			HashMap<Skill, ComboInfo> hs = tasks.get(key);
			if (hs.containsKey(skill))
				hs.get(skill).setCombo(combo);
		}
	}

	public static void EndCombo(LivingEntity le, Skill skill) {
		UUID key = le.getUniqueId();
		if (tasks.containsKey(key)) {
			HashMap<Skill, ComboInfo> hs = tasks.get(key);
			if (hs.containsKey(skill)) {
				ComboInfo info = hs.get(skill);
				BukkitTask task = info.getTask();
				if (!task.isCancelled())
					task.cancel();
				hs.remove(skill);
			}
			tasks.put(key, hs);
		}
	}

	public static void run(LivingEntity le, Skill skill, int duration) {
		UUID key = le.getUniqueId();
		if (!tasks.containsKey(key)) {
			tasks.put(key, new HashMap<Skill, ComboInfo>());
		}
		HashMap<Skill, ComboInfo> hs = tasks.get(key);
		if (hs.containsKey(skill)) {
			ComboInfo info = hs.get(skill);
			BukkitTask task = info.getTask();
			if (!task.isCancelled())
				task.cancel();
		}
		if (skill != null && duration > 0)
			hs.put(skill, new ComboInfo(new ComboCoolRunnable(le, skill).runTaskLater(Core.getCore(), duration)));
		tasks.put(key, hs);
		Core.getCore().getHashMapStore().getDataStore(le).changeSetting(le);
	}

	@Override
	public void run() {
		if (hasCombo(le, skill) && skill != null
				&& ((le instanceof Player && ((OfflinePlayer) le).isOnline()) || !le.isDead()))
			skill.comboEnd(le);
		this.cancel();

	}

	private static class ComboInfo {
		private final BukkitTask task;
		private int combo;

		public ComboInfo(BukkitTask task) {
			this.task = task;
			this.combo = 1;
		}

		public BukkitTask getTask() {
			return task;
		}

		public int getCombo() {
			return combo;
		}

		public void setCombo(int combo) {
			this.combo = combo;
		}
	}
}
