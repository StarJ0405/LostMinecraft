package com.StarJ.LM.Skills.assassin.Reaper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.Skills.Runnable.ComboCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class saberStinger extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 111, 197, 252, 296, 330, 359, 383, 403, 420, 436, 479, 527 };

	public saberStinger() {
		super("saberStinger", ChatColor.WHITE + "샤벨 스팅거", Skill.assassin.Reaper.skillGroup.Dagger, 14, 29, 500,
				AttackType.Back, "적의 빈틈을 노려 날카롭게 2회 찔러 피해를 준다.", "스킬을 다시 한번 입력 시 더욱 빠르게 2회 찔러 피해를 준다.");
		tripodChoice _11 = new tripodChoice("독:부식", "마지막 공격에 단검 공격에 '부식 독'을 부여한다.",
				"부식 독은 8.0초 동안 매 초마다 기본 피해량의 3% 피해를 준다.", "최대 3회까지 중첩된다.", "스킬 적중시 12초간 적에게 주는 피해가 12.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("독:출혈", "마지막 공격에 단검 공격에 '출혈 독'을 부여한다.",
				"출혈 독은 8.0초 동안 매 초마다 기본 피해량의 3% 피해를 준다.", "최대 3중첩이 가능하다", "3중첩 시 '과다 출혈'를 부여한다.",
				"과다 출혈은 8.0초 동안 매 초마다 기본 피해량의 62% 피해를 준다.");
		tripodChoice _13 = new tripodChoice("독:신경", "테스트용");
		tripodChoice _21 = new tripodChoice("두개의 그림자", "그림자를 이용하여 양쪽에서 같이 공격하여 피해가 60.0% 증가한다.");
		tripodChoice _22 = new tripodChoice("날카로운 단검", "적에게 주는 치명타 피해량이 100.0% 증가한다.");
		tripodChoice _23 = new tripodChoice("회피의 달인", "테스트용");
		tripodChoice _31 = new tripodChoice("낙인 활성", "공격 성공 시 3초간 '낙인 활성' 버프가 추가 된다.", "단검 스킬의 재사용 대기시간이 21.0% 감소한다.");
		tripodChoice _32 = new tripodChoice("속전속결", "일반 조작으로 변경된다.", "제자리에서 기본 피해량의 145.0% 피해를 주는 2회 공격을 한다.",
				"2회 모두 독 중첩을 시킨다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 1 ? 2 : 4;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return getTripod3Choice(le) == 1 ? SkillType.Normal : SkillType.Combo;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 0)
			mul *= 1.6d;
		if (tc3 == 1)
			mul *= 1.45d;
		return damages[level - 1] * mul * 2
				+ (tc1 == 0 || tc1 == 1 ? damages[level - 1] * (tc3 == 1 ? 0.48 : 0.24) : 0);
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

	Rel[] wideRight = new Rel[] { //
			new Rel(0, 0, 2), new Rel(0.5, 0, 2), //
			new Rel(1, 0, 1.5), new Rel(1.5, 0, 1.5), //
			new Rel(2, 0, 1), //
			new Rel(2.5, 0, 0.5), new Rel(2.5, 0, 0), //
			new Rel(3, 0, -0.5), new Rel(3, 0, -1), //
	};

	Rel[] wideLeft = new Rel[] { //
			new Rel(0, 0, -2), new Rel(0.5, 0, -2), //
			new Rel(1, 0, -1.5), new Rel(1.5, 0, -1.5), //
			new Rel(2, 0, -1), //
			new Rel(2.5, 0, -0.5), new Rel(2.5, 0, 0), //
			new Rel(3, 0, 0.5), new Rel(3, 0, 1),//
	};
	Rel[] thinLeft = new Rel[] { //
			new Rel(0, 0, -1), new Rel(0.5, 0, -1), //
			new Rel(1, 0, -0.5), new Rel(1.5, 0, -0.5), //
			new Rel(1, 0, -0.5), new Rel(1.5, 0, -0.5), //
			new Rel(2, 0, 0), new Rel(2.5, 0, 0), //
			new Rel(3, 0, 0.5), new Rel(3.5, 0, 0.5),//
	};
	Rel[] thinRight = new Rel[] { //
			new Rel(0, 0, 1), new Rel(0.5, 0, 1), //
			new Rel(1, 0, 0.5), new Rel(1.5, 0, 0.5), //
			new Rel(1, 0, 0.5), new Rel(1.5, 0, 0.5), //
			new Rel(2, 0, -0), new Rel(2.5, 0, -0), //
			new Rel(3, 0, -0.5), new Rel(3.5, 0, -0.5),//
	};

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		Core.getCore().getHashMapStore().getDataStore(att)
				.setSkillGroupTypeReduceCooldown(Skill.assassin.Reaper.skillGroup.Dagger, this.displayName, 0d);
		super.BuffEnd(att, num);
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (getTripod2Choice(att) == 1)
			ds.setExtraCriticalDamage(this.displayName, 1);
		boolean end = super.damage(skill, destruction, list, att, vic_e, damage, now);
		if (end && getTripod3Choice(att) == 0) {
			BuffRunnable.run(att, this, 3, 0);
			ds.setSkillGroupTypeReduceCooldown(Skill.assassin.Reaper.skillGroup.Dagger, this.displayName, 0.21d);
		}
		ds.setExtraCriticalDamage(this.displayName, 0);
		return end;
	}

	HashMap<UUID, BukkitTask> overblood = new HashMap<UUID, BukkitTask>();

	@Override
	public void runBlood(LivingEntity att, LivingEntity vic, double damage, int times, int max) {
		if (att == vic)
			return;
		UUID uuid = vic.getUniqueId();
		if (!blood.containsKey(uuid))
			blood.put(uuid, new HashMap<Skill, StackableBukkitTask>());
		HashMap<Skill, StackableBukkitTask> hs = blood.get(uuid);
		if (!hs.containsKey(this))
			hs.put(this, new StackableBukkitTask());
		StackableBukkitTask sbt = hs.get(this);
		int stack = sbt.getStack();
		stack += 1;
		if (stack > max)
			stack = max;
		if (stack == 3) {
			sbt.stop();
			if (overblood.containsKey(uuid))
				overblood.get(uuid).cancel();
			overblood.put(uuid, new tickRunnable(att, vic, damages[getLevel(att) - 1] * 2 * 0.62, times, tickType.blood)
					.runTaskTimer(Core.getCore(), 10, 20));
		} else {
			sbt.setTask(new tickRunnable(att, vic, damage * stack, times, tickType.blood).runTaskTimer(Core.getCore(),
					10, 20));
			sbt.setStack(stack);
		}
		return;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection().clone().setY(0).normalize();
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		double mul = ds.getMultiplyDamage(groupType) * (tc2 == 0 ? 1.6d : 1);
		List<Entity> list1 = new ArrayList<Entity>();
		List<Entity> list2 = new ArrayList<Entity>();
		if (tc3 == 1) {
			int i = 0;
			for (Rel rel : thinLeft) {
				i++;
				new BukkitRunnable() {

					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
						for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
							if (damage(list1, le, et, damages[level - 1] * mul * 1.45))
								if (tc1 == 0) {
									runPoison(le, (LivingEntity) et, damages[level - 1] * 0.03, 8, 3);
									BuffRunnable.run(le, saberStinger.this, 12, 0); // 피증
									ds.setDamageIncrease(saberStinger.this.displayName, 0.12);
								} else if (tc1 == 1)
									runBlood(le, (LivingEntity) et, damages[level - 1] * 0.03, 8, 3);
					}
				}.runTaskLater(Core.getCore(), i / 2);
			}
			for (Rel rel : thinRight) {
				i++;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
						for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
							if (damage(list2, le, et, damages[level - 1] * mul * 1.45))
								if (tc1 == 0) {
									runPoison(le, (LivingEntity) et, damages[level - 1] * 0.03, 8, 3);
									BuffRunnable.run(le, saberStinger.this, 12, 0); // 피증
									ds.setDamageIncrease(saberStinger.this.displayName, 0.12);
								} else if (tc1 == 1)
									runBlood(le, (LivingEntity) et, damages[level - 1] * 0.03, 8, 3);

					}
				}.runTaskLater(Core.getCore(), i / 2);
			}
		} else {
			if (ComboCoolRunnable.hasCombo(le, this)) {
				int i = 0;
				for (Rel rel : thinLeft) {
					i++;
					new BukkitRunnable() {

						@Override
						public void run() {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
							for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
								damage(list2, le, et, damages[level - 1] * mul / 2);
						}
					}.runTaskLater(Core.getCore(), i / 2);
				}
				for (Rel rel : thinRight) {
					i++;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
							for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
								if (damage(list1, le, et, damages[level - 1] * mul / 2))
									if (tc1 == 0) {
										runPoison(le, (LivingEntity) et, damages[level - 1] * 0.03, 8, 3);
										BuffRunnable.run(le, saberStinger.this, 12, 0); // 피증
										ds.setDamageIncrease(saberStinger.this.displayName, 0.12);
									} else if (tc1 == 1)
										runBlood(le, (LivingEntity) et, damages[level - 1] * 0.03, 8, 3);

						}
					}.runTaskLater(Core.getCore(), i / 2);
				}
				comboEnd(le);
			} else {
				int i = 0;
				for (Rel rel : wideRight) {
					i++;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
							for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
								damage(list1, le, et, damages[level - 1] * mul / 2);
							for (Entity et : now.getWorld().getNearbyEntities(loc.clone().add(dir), 1, 1, 1))
								damage(list1, le, et, damages[level - 1] * mul / 2);
						}
					}.runTaskLater(Core.getCore(), i / 2);
				}
				for (Rel rel : wideLeft) {
					i++;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
							for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
								damage(list2, le, et, damages[level - 1] * mul / 2);

							for (Entity et : now.getWorld().getNearbyEntities(loc.clone().add(dir), 1, 1, 1))
								damage(list2, le, et, damages[level - 1] * mul / 2);
						}
					}.runTaskLater(Core.getCore(), i / 2);
				}
				ComboCoolRunnable.run(le, this, getComboDuration(le));
			}
		}
		return false;
	}
}
