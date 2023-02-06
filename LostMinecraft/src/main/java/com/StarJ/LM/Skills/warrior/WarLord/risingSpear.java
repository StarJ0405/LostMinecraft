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
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class risingSpear extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 33, 59, 75, 89, 99, 106, 113, 120, 124, 129, 141, 156 };
	protected final double[] damages2 = new double[] { 129, 228, 291, 343, 383, 415, 443, 468, 487, 506, 556, 612 };

	public risingSpear() {
		super("risingSpear", ChatColor.WHITE + "라이징 스피어", Skill.warrior.WarLord.skillGroup.Lance, 9, 140, 24,
				AttackType.Head, "창을 땅에 찔러 넣으며 피해를 준다.", "그 뒤 창을 뽑아 올린다.", "전방의 적들에게 추가로 피해를 준다.");
		tripodChoice _11 = new tripodChoice("돌진", "방패로 밀고 나가며 도착 후 45.0%의 추가피해를 준다.");
		tripodChoice _12 = new tripodChoice("빠른 준비", "재사용 대기시간이 2.0초 감소한다");
		tripodChoice _13 = new tripodChoice("기습공격", "창에 기운을 모으는 대신 간결하고 빠르게 공격한다.");
		tripodChoice _21 = new tripodChoice("강화된 일격", "적에게 주는 피해가 50.0% 증가한다.");
		tripodChoice _22 = new tripodChoice("화염창", "올려치는 속성의 포격을 가하여 대상을 5.0초 동안 화상 상태로 만든다.",
				"매 초마다 기본 피해량의 15% 추가 피해를 입힌다.");
		tripodChoice _23 = new tripodChoice("낙뢰", "테스트용");
		tripodChoice _31 = new tripodChoice("대지파편", "창으로 대지를 강하게 찌르는 순간 부채꼴 형태로 대지파편이 솟구친다.",
				"적중된 적에게 94.8%의 추가피해를 준다.");
		tripodChoice _32 = new tripodChoice("연쇄폭발", "창을 뽑아 올려치는 대신 지면에 포격을 가한다.", "올려치기 공격의 102.0% 피해를 주는 폭발을 일으킨다.",
				"폭발 피해는 중첩 피해 가능하다.", "폭발은 전방으로 뻗어나가며 최대 5회 공격한다");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public int getTimes(LivingEntity le) {
		int times = 2;
		int tc3 = getTripod3Choice(le);
		if (getTripod1Choice(le) == 0)
			times += 1;
		if (tc3 == 0)
			times += 1;
		else if (tc3 == 1)
			times += 4;
		return times;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		double base = 1;
		if (tc1 == 0)
			base += 0.45d;
		if (tc3 == 0)
			base += 0.948d;
		if (tc2 == 0)
			mul *= 1.5d;
		return (damages1[level - 1] + damages2[level - 1]) * base * mul
				+ (tc3 == 1 ? damages2[level - 1] * 4.1 : 0) * mul
				+ (tc2 == 1 ? (damages1[level - 1] + damages2[level - 1]) * 0.75 : 0);
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		return tc1 == 1 ? 2 : 0;
	}

	@Override
	public tripodChoice[] getTripod1() {
		return level1;
	}

	@Override
	public tripodChoice[] getTripod2() {
		return level2;

	}

	@Override
	public tripodChoice[] getTripod3() {
		return level3;
	}

	Rel[] sting = new Rel[] { //
			new Rel(0, 0, 0), //
			new Rel(0.1, -0.05, 0), //
			new Rel(0.2, -0.1, 0), //
			new Rel(0.3, -0.15, 0), //
			new Rel(0.4, -0.2, 0), //
			new Rel(0.5, -0.25, 0), //
			new Rel(0.6, -0.3, 0), //
			new Rel(0.7, -0.35, 0), //
			new Rel(0.8, -0.4, 0), //
			new Rel(0.9, -0.45, 0), //
			new Rel(1, -0.5, 0), //
			new Rel(1.1, -0.55, 0), //
			new Rel(1.2, -0.6, 0), //
			new Rel(1.3, -0.65, 0), //
			new Rel(1.4, -0.7, 0), //
			new Rel(1.5, -0.75, 0), //
			new Rel(1.6, -0.8, 0), //
			new Rel(1.7, -0.85, 0), //
			new Rel(1.8, -0.9, 0), //
			new Rel(1.9, -0.95, 0), //
			new Rel(2, -1, 0), //
	};
	Rel[] explode = new Rel[] { //
			new Rel(0, 0, 0), //
			new Rel(0, 0.25, 0), //
			new Rel(0, 0.5, 0), //
			new Rel(0, 0.75, 0), //
			new Rel(0, 1, 0), //
			new Rel(0, 1.25, 0), //
			new Rel(0, 1.5, 0), //
			new Rel(0, 1.75, 0), //
			new Rel(0, 2, 0),//
	};
	Rel[] explode_wide = new Rel[] { //
			new Rel(2, 0, 0), new Rel(2, 0, 0.5), new Rel(2, 0, -0.5), //
			new Rel(2, 0, 1), new Rel(2, 0, -1), //
			//
			new Rel(1.5, 0, 1.5), new Rel(1.5, 0, -1.5), //
			new Rel(1, 0, 2), new Rel(1, 0, -2), //
			//
			new Rel(0.5, 0, 2), new Rel(0.5, 0, -2), //
			new Rel(0, 0, 2), new Rel(0, 0, -2),//
	};

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!super.damage(skill, destruction, list, att, vic_e, damage, now))
			return false;
		if (skill && getTripod2Choice(att) == 1) {
			int level = getLevel(att);
			runFire(att, (LivingEntity) vic_e, (damages1[level - 1] + damages2[level - 1]) * 0.15, 5);
		}
		return true;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);

		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 0)
			mul *= 1.5d;
		List<Entity> list_sting = new ArrayList<Entity>();
		List<Entity> list_explode1 = new ArrayList<Entity>();
		List<Entity> list_explode2 = new ArrayList<Entity>();
		int delay = 0;
		if (tc1 == 0) {
			delay = 8;
			le.setVelocity(new Vector(0, 0.1, 0));
			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(le.getLocation().getDirection().setY(0).normalize());
				}
			}.runTaskLater(Core.getCore(), 2);
		}
		final double mul_r = mul;
		new BukkitRunnable() {
			@Override
			public void run() {
				final int split = tc1 == 2 ? 5 : 3;
				int i = 0;
				if (tc1 == 0) {
					le.setVelocity(new Vector());
					List<Entity> land_list = new ArrayList<Entity>();
					for (Entity et : le.getWorld().getNearbyEntities(le.getLocation(), 2, 2, 2))
						damage(land_list, le, (LivingEntity) et,
								(damages1[level - 1] + damages2[level - 1]) * 0.45d * mul_r);
				}
				Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
				Vector dir = loc.getDirection();
				dir.setY(0);
				dir.normalize();
				for (Rel rel : sting) {
					i++;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
								damage(list_sting, le, (LivingEntity) et, damages1[level - 1] * mul_r);
						}
					}.runTaskLater(Core.getCore(), i / split);
				}
				Rel last = sting[sting.length - 1];
				Location ground = loc.clone()
						.add(EffectStore.getRel(dir, last.getFront(), last.getHeight(), last.getRight()));

				if (tc3 == 0) {
					i = i / split;
					new BukkitRunnable() {
						@Override
						public void run() {
							for (Rel rel : explode_wide) {
								Location now = ground.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 198, 138, 18, 1, 100, 0.1, 0.1, 0.1);
								for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1)) {
									damage(list_explode2, le, (LivingEntity) et,
											(damages2[level - 1] * 0.948 + damages1[level - 1] * 0.948) * mul_r);
								}
								now = ground.clone().add(
										EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 0.75));
								now.add(0, 0.5, 0);
								EffectStore.spawnRedStone(now, 198, 138, 18, 1, 100, 0.1, 0.1, 0.1);
								for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1)) {
									damage(list_explode2, le, (LivingEntity) et,
											(damages2[level - 1] * 0.948 + damages1[level - 1] * 0.948) * mul_r);
								}
							}
						}
					}.runTaskLater(Core.getCore(), i);
					i = i * 3;
				}
				if (tc3 == 1) {
					i = i / split + split * 2;
					for (int c = 0; c < 5; c++) {
						int j = i + c * 20;
						final int add = c;
						List<Entity> list_explode5 = new ArrayList<Entity>();
						for (Rel rel : explode) {
							j++;
							new BukkitRunnable() {
								@Override
								public void run() {
									Location now = ground.clone().add(dir.clone().multiply(1 + add)).add(
											EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
									EffectStore.Directional.EXPLOSION_NORMAL.spawnDirectional(now, 5, 0.05, 0.05, 0.05,
											0.1);
									for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
										damage(list_explode5, le, (LivingEntity) et,
												damages2[level - 1] * 1.02 * mul_r);
								}
							}.runTaskLater(Core.getCore(), j / split);
						}
					}
				} else {
					i = i / split + split * 2;
					new BukkitRunnable() {
						@Override
						public void run() {
							for (Rel rel : explode) {
								Location now = ground.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.Directional.EXPLOSION_NORMAL.spawnDirectional(now, 5, 0.05, 0.05, 0.05,
										0.05);
								for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
									damage(list_explode1, le, (LivingEntity) et, damages2[level - 1] * mul_r);
							}
						}
					}.runTaskLater(Core.getCore(), i);
				}
			}
		}.runTaskLater(Core.getCore(), delay);

		return false;
	}
}
