package com.StarJ.LM.System;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.StarJ.LM.Core;

public class HashMapStore {
	private HashMap<UUID, DataStore> ds = new HashMap<UUID, DataStore>();
	private HashMap<UUID, BukkitTask> actionbar = new HashMap<UUID, BukkitTask>();
	private HashMap<UUID, DPS> dps = new HashMap<UUID, DPS>();
	private HashMap<UUID, Target> lastTarget = new HashMap<UUID, Target>();
	private Set<UUID> preventFallDamage = new HashSet<UUID>();

	// DataStore
	public DataStore getDataStore(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		if (le instanceof Player) {
			if (!ds.containsKey(uuid))
				ds.put(uuid, new DataStore(le));
			return ds.get(uuid);
		} else if (le.hasMetadata("type")) {
			for (MetadataValue mv : le.getMetadata("type"))
				if (mv.getOwningPlugin().equals(Core.getCore()))
					if (!mv.asString().equalsIgnoreCase(EntityStore.Minion.name()))
						return null;
			if (!ds.containsKey(uuid))
				ds.put(uuid, new DataStore(le));
			return ds.get(uuid);
		}
		return null;
	}

	public void removeDataStore(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		if (ds.containsKey(uuid))
			ds.remove(uuid);
	}

	// Actionbar
	public void setActionbarTask(LivingEntity le, BukkitTask task) {
		UUID uuid = le.getUniqueId();
		if (actionbar.containsKey(uuid))
			actionbar.get(uuid).cancel();
		actionbar.put(uuid, task);
	}

	public void cancelActionbarTask(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		if (actionbar.containsKey(uuid))
			actionbar.get(uuid).cancel();
	}

	// DPS
	public DPS getDPS(Player player) {
		UUID uuid = player.getUniqueId();
		if (!dps.containsKey(uuid))
			dps.put(uuid, new DPS());
		return dps.get(uuid);
	}

	public class DPS {
		private long now;
		private double damage;
		private long end;
		private int tick;
		private int critical;

		public DPS() {
			this.now = 0l;
			this.damage = 0d;
			this.end = 0l;
			this.tick = 0;
			this.critical = 0;
		}

		public void reset() {
			this.now = 0l;
			this.damage = 0d;
			this.end = 0l;
			this.tick = 0;
			this.critical = 0;
		}

		public double getTime() {
			return ((end - now) / 100) / 10.0d;
		}

		public double getDamage() {
			return damage;
		}

		public void addDamage(double damage) {
			this.damage += damage;
			this.end = System.currentTimeMillis();
			if (this.now == 0l)
				this.now = this.end;
		}

		public void addTick() {
			this.tick++;
		}

		public int getTick() {
			return tick;
		}

		public void addCritical() {
			this.critical++;
		}

		public int getCritical() {
			return critical;
		}
	}

	//
	public LivingEntity getTarget(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		return this.lastTarget.containsKey(uuid) ? this.lastTarget.get(le.getUniqueId()).getTarget() : null;
	}

	public void setTarget(LivingEntity le, LivingEntity target) {
		if (target == null)
			return;
		UUID uuid = le.getUniqueId();
		if (this.lastTarget.containsKey(uuid)) {
			Target preTarget = this.lastTarget.get(uuid);
			if (preTarget.getTarget().equals(target))
				preTarget.reStart();
			else {
				preTarget.cancel();
				this.lastTarget.put(uuid, new Target(le, target));
			}
		} else
			this.lastTarget.put(uuid, new Target(le, target));
	}

	public class Target {
		private final LivingEntity le;
		private final LivingEntity target;
		private BukkitTask task;

		public Target(LivingEntity le, LivingEntity target) {
			this.le = le;
			this.target = target;
			task = new BukkitRunnable() {

				@Override
				public void run() {
					HashMapStore.this.lastTarget.remove(le.getUniqueId());
					this.cancel();
				}
			}.runTaskLater(Core.getCore(), 20 * 15);
		}

		public LivingEntity getTarget() {
			return target;
		}

		public void cancel() {
			this.task.cancel();
		}

		public void reStart() {
			this.task.cancel();
			this.task = new BukkitRunnable() {

				@Override
				public void run() {
					HashMapStore.this.lastTarget.remove(le.getUniqueId());
					this.cancel();
				}
			}.runTaskLater(Core.getCore(), 20 * 10);
		}
	}

	// 낙하 데미지
	public boolean isPreventFallDamage(LivingEntity le) {
		return le != null && this.preventFallDamage.contains(le.getUniqueId());
	}

	public void addPventFallDamage(LivingEntity le, boolean preventFallDamage) {
		if (preventFallDamage)
			this.preventFallDamage.add(le.getUniqueId());
		else
			this.preventFallDamage.remove(le.getUniqueId());

	}
}
