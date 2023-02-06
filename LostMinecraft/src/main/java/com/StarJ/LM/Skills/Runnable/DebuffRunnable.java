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
import com.StarJ.LM.System.MessageStore;

public class DebuffRunnable extends BukkitRunnable {
	private final static HashMap<UUID, HashMap<Skill, HashMap<Integer, DebuffInfo>>> infos = new HashMap<UUID, HashMap<Skill, HashMap<Integer, DebuffInfo>>>();
	private final LivingEntity att;
	private final LivingEntity vic;
	private final Skill skill;
	private final String debuffName;
	private final int num;

	public DebuffRunnable(LivingEntity att, LivingEntity vic, Skill skill, String debuffName, int num) {
		this.att = att;
		this.vic = vic;
		this.skill = skill;
		this.debuffName = debuffName;
		this.num = num;
	}

	public static void run(LivingEntity att, LivingEntity vic, Skill skill, double duration, String debuffName,
			int num) {
		if (skill != null)
			if (duration > 0) {
				UUID key = vic.getUniqueId();
				HashMap<Skill, HashMap<Integer, DebuffInfo>> skillInfo = infos.containsKey(key) ? infos.get(key)
						: new HashMap<Skill, HashMap<Integer, DebuffInfo>>();
				HashMap<Integer, DebuffInfo> buffs = skillInfo.containsKey(skill) ? skillInfo.get(skill)
						: new HashMap<Integer, DebuffInfo>();
				if (buffs.containsKey(num)) {
					DebuffInfo info = buffs.get(num);
					BukkitTask task = info.getTask();
					if (task != null)
						task.cancel();
				}
				buffs.put(num, new DebuffInfo(new DebuffRunnable(att, vic, skill, debuffName, num)
						.runTaskLater(Core.getCore(), (int) duration * 20), att));
				skillInfo.put(skill, buffs);
				infos.put(key, skillInfo);
				Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
				if (Core.getCore().getHashMapStore().getDataStore(vic) != null)
					Core.getCore().getHashMapStore().getDataStore(vic).changeSetting(vic);
			} else
				skill.DebuffEnd(att, vic, num);
	}

	public static boolean has(LivingEntity vic, Skill skill) {
		UUID key = vic.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, DebuffInfo>> buffs = infos.get(key);
			if (buffs.containsKey(skill))
				for (DebuffInfo info : buffs.get(skill).values())
					if (info.getTask() != null && !info.getTask().isCancelled())
						return true;
		}
		return false;

	}

	public static boolean has(LivingEntity vic, Skill skill, int num) {
		UUID key = vic.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, DebuffInfo>> skillInfo = infos.get(key);
			if (skillInfo.containsKey(skill)) {
				HashMap<Integer, DebuffInfo> buffs = skillInfo.get(skill);
				if (buffs.containsKey(num) && buffs.get(num).getTask() != null)
					return !buffs.get(num).getTask().isCancelled();
			}
		}
		return false;
	}

	public static int getCount(LivingEntity vic, Skill skill) {
		UUID key = vic.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, DebuffInfo>> skillInfo = infos.get(key);
			if (skillInfo.containsKey(skill))
				return skillInfo.get(skill).keySet().size();
		}
		return 1;
	}

	public static void cancel(LivingEntity att, LivingEntity vic, Skill skill, int num) {
		cancel(att, vic, skill, num, true);
	}

	public static void cancel(LivingEntity att, LivingEntity vic, Skill skill, int num, boolean act) {
		UUID key = vic.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, DebuffInfo>> skillInfo = infos.get(key);
			if (skillInfo.containsKey(skill)) {
				HashMap<Integer, DebuffInfo> buffs = skillInfo.get(skill);
				if (buffs.containsKey(num)) {
					DebuffInfo info = buffs.get(num);
					BukkitTask task = info.getTask();
					if (!task.isCancelled()) {
						task.cancel();
						if (act)
							skill.DebuffEnd(att, vic, num);
					}
					buffs.remove(num);
					skillInfo.put(skill, buffs);
					infos.put(key, skillInfo);
				}
			}
		}
	}

	public static void cancelAll(LivingEntity vic) {
		cancelAll(vic, true);
	}

	public static void cancelAll(LivingEntity vic, boolean act) {
		UUID key = vic.getUniqueId();
		if (infos.containsKey(key)) {
			HashMap<Skill, HashMap<Integer, DebuffInfo>> skillInfo = infos.get(key);
			for (Skill skill : skillInfo.keySet()) {
				HashMap<Integer, DebuffInfo> buffs = skillInfo.get(skill);
				for (int num : buffs.keySet()) {
					DebuffInfo info = buffs.get(num);
					BukkitTask task = info.getTask();
					if (!task.isCancelled()) {
						task.cancel();
						if (act)
							skill.DebuffEnd(info.getAttaker(), vic, num);
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
		if (skill != null && ((vic instanceof Player && ((OfflinePlayer) vic).isOnline()) || !vic.isDead())) {
			skill.DebuffEnd(att, vic, num);
			if (att instanceof Player)
				MessageStore.sendSystemMsg((Player) att, skill.getDisplayName() + ":" + debuffName + "의 시간이 종료되었습니다.");
		}
		this.cancel();
	}

	private static class DebuffInfo {
		private final BukkitTask task;
		private LivingEntity att;

		public DebuffInfo(BukkitTask task, LivingEntity att) {
			this.task = task;
			this.att = att;
		}

		public BukkitTask getTask() {
			return task;
		}

		public LivingEntity getAttaker() {
			return att;
		}
	}
}
