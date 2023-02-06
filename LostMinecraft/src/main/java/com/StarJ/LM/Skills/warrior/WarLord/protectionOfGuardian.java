package com.StarJ.LM.Skills.warrior.WarLord;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;
import com.StarJ.LM.System.JobStore.Identity;

public class protectionOfGuardian extends Skill {
	double damage = 14181;

	public protectionOfGuardian() {
		super("protectionOfGuardian", ChatColor.WHITE + "가디언의 수호", 1, Skill.skillGroupType.Awakening, 300, 81, 0,
				"스킬 사용시 실드 게이지를 100% 회복한다.", "자신을 중심으로 반경에 10초 동안 유지된다.", "이때 영역안에 있는 적들에게 피해를 준다.",
				"영역 안에 있으면 모든 피해가 40% 감소된다.");
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		return damage * ds.getMultiplyDamage(groupType);
	}

	@Override
	public tripodChoice[] getTripod1() {
		return null;
	}

	@Override
	public tripodChoice[] getTripod2() {
		return null;
	}

	@Override
	public tripodChoice[] getTripod3() {
		return null;
	}

	private Rel[] circle = new Rel[] { //
			new Rel(2, 0, 0), new Rel(-2, 0, 0), //
			//
			new Rel(1.984, 0, 0.25), new Rel(1.984, 0, -0.25), //
			new Rel(-1.984, 0, 0.25), new Rel(-1.984, 0, -0.25), //
			//
			new Rel(1.936, 0, 0.5), new Rel(1.936, 0, -0.5), //
			new Rel(-1.936, 0, 0.5), new Rel(-1.936, 0, -0.5), //
			//
			new Rel(1.854, 0, 0.75), new Rel(1.854, 0, -0.75), //
			new Rel(-1.854, 0, 0.75), new Rel(-1.854, 0, -0.75), //
			//
			new Rel(1.732, 0, 1), new Rel(1.732, 0, -1), //
			new Rel(-1.732, 0, 1), new Rel(-1.732, 0, -1), //
			//
			new Rel(1.561, 0, 1.25), new Rel(1.561, 0, -1.25), //
			new Rel(-1.561, 0, 1.25), new Rel(-1.561, 0, -1.25), //
			//
			new Rel(1.323, 0, 1.5), new Rel(1.323, 0, -1.5), //
			new Rel(-1.323, 0, 1.5), new Rel(-1.323, 0, -1.5), //
			//
			new Rel(0.968, 0, 1.75), new Rel(0.968, 0, -1.75), //
			new Rel(-0.968, 0, 1.75), new Rel(-0.968, 0, -1.75), //
			//
			new Rel(0.75, 0, 1.854), new Rel(0.75, 0, -1.854), //
			new Rel(-0.75, 0, 1.854), new Rel(-0.75, 0, -1.854), //
			//
			new Rel(0.5, 0, 1.936), new Rel(0.5, 0, -1.936), //
			new Rel(-0.5, 0, 1.936), new Rel(-0.5, 0, -1.936), //
			//
			new Rel(0.25, 0, 1.984), new Rel(0.25, 0, -1.984), //
			new Rel(-0.25, 0, 1.984), new Rel(-0.25, 0, -1.984), //
			//
			new Rel(0, 0, 2), new Rel(0, 0, -2),//
	};

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		super.BuffEnd(att, num);
		if (num == 1)
			Core.getCore().getHashMapStore().getDataStore(att).setReduceDamage(protectionOfGuardian.this.displayName,
					0);
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		List<Entity> list = new ArrayList<Entity>();
		int i = 0;
		for (; i < 25; i += 5)
			new BukkitRunnable() {

				@Override
				public void run() {
					Location loc = le.getLocation();
					loc.getWorld().strikeLightningEffect(loc);
				}
			}.runTaskLater(Core.getCore(), i);
		for (; i < 40; i++) {
			final int j = i;
			new BukkitRunnable() {

				@Override
				public void run() {
					Location loc = le.getLocation();
					Vector dir = loc.getDirection().setY(0);
					for (Rel rel : circle) {
						Location now = loc.clone().add(0, 0.5, 0).add(EffectStore.getRel(dir, rel.getFront(),
								rel.getHeight(), rel.getRight(), (j - 25) * 0.5));
						EffectStore.spawnRedStone(now, 255, 212, 0, 1, 10, 0, 0, 0);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
							damage(list, le, et, damage * mul);
					}
				}
			}.runTaskLater(Core.getCore(), i / 2);
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				BuffRunnable.run(le, protectionOfGuardian.this, 10, 0);// 버프지속
				Identity iden = ds.getIdentity();
				iden.setNow(iden.getMax());
			}
		}.runTaskLater(Core.getCore(), i / 2 + 4);
		Location loc = le.getLocation();
		Vector dir = loc.getDirection().setY(0);
		new BukkitRunnable() {
			int time = 0;

			@Override
			public void run() {
				if (BuffRunnable.has(le, protectionOfGuardian.this, 0)) {
					for (int i = 0; i < 15; i++)
						for (Rel rel : circle) {
							Location now = loc.clone().add(0, 0.5, 0).add(
									EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), i * 0.5));
							if (time % (i + 1) == 0 || i == 14)
								EffectStore.spawnRedStone(now, 255, 212, 0, 1, 10, 0, 0, 0);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
								if (et == le) {
									BuffRunnable.run(le, protectionOfGuardian.this, 1, 1);
									ds.setReduceDamage(protectionOfGuardian.this.displayName, 0.4);
									break;
								}
						}
					this.time++;
					if (time > 200)
						this.cancel();
				} else
					this.cancel();
			}
		}.runTaskTimer(Core.getCore(), i / 2 + 4, 5);
		return false;
	}
}
