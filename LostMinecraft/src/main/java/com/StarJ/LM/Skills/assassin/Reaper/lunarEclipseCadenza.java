package com.StarJ.LM.Skills.assassin.Reaper;

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
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class lunarEclipseCadenza extends Skill {
	protected final double damage1 = 3921;
	protected final double damage2 = 15695;
	protected final double damage3 = 19603;

	public lunarEclipseCadenza() {
		super("lunarEclipseCadenza", ChatColor.WHITE + "월식:카덴차", 1, Skill.skillGroupType.Awakening, 300, 57, 0,
				"모든 그림자와 달의 기운을 집중하여 자신의 주변에 큰 그림자 지대를 소환한다.", "소환시 주변 적에게 피해를 준다.", "그림자 분신이 날뛰어 피해를 준다.",
				"마지막 강한 폭발로 큰 피해를 준다.");
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);

		return (damage1 + damage2 + damage3) * mul;
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
	public boolean Use(LivingEntity le) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (super.Use(le))
			return true;
		Location loc = le.getLocation();
		Vector dir = loc.getDirection().clone().setY(0).normalize();
		List<Entity> list = new ArrayList<Entity>();
		int c = 0;
		for (; c < 5; c++) {
			final int cc = c;
			for (Rel rel : circle)
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
								rel.getRight(), (cc + 1) * 0.5));
						EffectStore.spawnRedStone(now, 222, 222, 222, 0.75f, 10, 0.1d, 0.1d, 0.1d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
							damage(list, le, et, damage1 * mul, AttackType.getAttackType(et, loc));
					}
				}.runTaskLater(Core.getCore(), c * 2);
		}
		int i = 0;
		for (; i < 10; i++) {
			List<Entity> list2 = new ArrayList<Entity>();
			final int ii = i;
			int r = 0;
			for (Rel rel : circle) {
				r++;
				final int rr = r;
				new BukkitRunnable() {
					@Override
					public void run() {
						for (int c = 0; c < 5; c++) {
							Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
									rel.getRight(), (c + 1) * 0.5));
							if (c == 4) {
								EffectStore.spawnRedStone(now, 222, 222, 222, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								if (rr % 5 == 0 && rr < (10 - ii) * 4) {
									EffectStore.spawnRedStone(now.clone().add(0, 0.5, 0), 0, 0, 0, 1f, 100, 0.5d, 0.5d,
											0.5d);
									EffectStore.spawnRedStone(now.clone().add(0, 1, 0), 0, 0, 0, 1f, 100, 0.5d, 0.5d,
											0.5d);
								}
							}
							for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
								damage(list2, le, et, damage2 / 10d * mul, AttackType.getAttackType(et, loc));
						}
					}
				}.runTaskLater(Core.getCore(), c * 2 + i * 5);
			}
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				List<Entity> list = new ArrayList<Entity>();
				for (Rel rel : circle) {
					for (int c = 0; c < 5; c++) {
						Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
								rel.getRight(), (c + 1) * 0.5));
						EffectStore.spawnRedStone(now, 222, 222, 222, 0.75f, 10, 0.5d, 0.5d, 0.5d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
							damage(list, le, et, damage3 * mul, AttackType.getAttackType(et, loc));
					}
				}
			}
		}.runTaskLater(Core.getCore(), c * 2 + i * 5 + 5);
		return false;
	}
}
