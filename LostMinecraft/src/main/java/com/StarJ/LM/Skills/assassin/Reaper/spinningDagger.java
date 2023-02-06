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
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class spinningDagger extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 89, 158, 203, 237, 265, 288, 307, 324, 337, 350, 385, 423 };
	protected final double[] damages2 = new double[] { 114, 203, 260, 304, 340, 370, 394, 416, 433, 450, 495, 544 };

	public spinningDagger() {
		super("spinningDagger", ChatColor.WHITE + "스피닝 대거", Skill.assassin.Reaper.skillGroup.Dagger, 12, 60, 428,
				AttackType.Back, "전방으로 이동하며 주변에 단검을 던져 피해를 준다.", "착지 시 한번 더 적을 베어 피해를 준다.");
		tripodChoice _11 = new tripodChoice("독:부식", "마지막 공격에 단검 공격에 '부식 독'을 부여한다.",
				"부식 독은 8.0초 동안 매 초마다 기본 피해량의 3% 피해를 준다.", "최대 3회까지 중첩된다.", "스킬 적중시 12초간 적에게 주는 피해가 12.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("독:출혈", "마지막 공격에 단검 공격에 '출혈 독'을 부여한다.",
				"출혈 독은 8.0초 동안 매 초마다 기본 피해량의 3% 피해를 준다.", "최대 3중첩이 가능하다", "3중첩 시 '과다 출혈'를 부여한다.",
				"과다 출혈은 8.0초 동안 매 초마다 기본 피해량의 68% 피해를 준다.");
		tripodChoice _13 = new tripodChoice("독:신경", "테스트용");
		tripodChoice _21 = new tripodChoice("넓은 공격", "테스트용");
		tripodChoice _22 = new tripodChoice("회전력 강화", "적에게 주는 피해가 60.0% 증가한다.");
		tripodChoice _23 = new tripodChoice("최후의 칼날", "마지막 공격이 무조건 치명타로 적중한다.", "치명타 피해가 100.0% 증가한다.");
		tripodChoice _31 = new tripodChoice("그림자 잔상", "그림자 잔상이 생겨 마지막 공격이 추가된다.", "기본 피해량의 80.0% 피해를 준다.",
				"독 중첩을 시킨다.");
		tripodChoice _32 = new tripodChoice("끝없는 춤", "스택이 가능하게 변경된다.", "최대 2회 중첩이 가능하다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return 4 + (getTripod3Choice(le) == 0 ? 1 : 0);
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 1)
			mul *= 1.6d;
		if (tc3 == 0)
			mul *= 1.8d;
		return (damages1[level - 1] + damages2[level - 1]) * mul
				+ (tc1 == 0 || tc1 == 1 ? (damages1[level - 1] + damages2[level - 1]) * (tc3 == 0 ? 0.48 : 0.24) : 0);
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

	Rel[][] daggers = new Rel[][] { //
			{ //
					new Rel(1, 0, 0), //
					new Rel(0.309, 0, 0.951), new Rel(-0.809, 0, 0.588), //
					new Rel(-0.809, 0, -0.588), new Rel(0.309, 0, -0.951), //
			}, { //
					new Rel(0.5, 0, 0.866), //
					new Rel(-0.208, 0, 0.978), new Rel(-0.995, 0, 0.105), //
					new Rel(-0.407, 0, -0.914), new Rel(0.743, 0, -0.669), //
			}, { //
					new Rel(0.866, 0, 0.5), //
					new Rel(-0.669, 0, 0.743), new Rel(-0.914, 0, -0.407), //
					new Rel(-0.105, 0, -0.995), new Rel(0.978, 0, -0.208), //
			} };
	Rel[] last = new Rel[] { //
			new Rel(0, 0, 1), new Rel(0.309, 0, 0.951), //
			new Rel(0.5, 0, 0.866), new Rel(0.866, 0, 0.5), //
			new Rel(1, 0, 0), new Rel(0.987, 0, -0.208), //
			new Rel(0.743, 0, -0.669), new Rel(0.309, 0, -0.951), //
			new Rel(0, 0, -1),

	};

	@Override
	public void setTripod1Choice(LivingEntity le, int choice) {
		super.setTripod1Choice(le, choice);
		if (getTripod3Choice(le) == 1) {
			setMaxStack(le, 2);
			setNowStack(le, 2);
		} else {
			setMaxStack(le, -1);
			setNowStack(le, -1);
		}
	}

	@Override
	public void setTripod2Choice(LivingEntity le, int choice) {
		super.setTripod2Choice(le, choice);
		if (getTripod3Choice(le) == 1) {
			setMaxStack(le, 2);
			setNowStack(le, 2);
		} else {
			setMaxStack(le, -1);
			setNowStack(le, -1);
		}
	}

	@Override
	public void setTripod3Choice(LivingEntity le, int choice) {
		super.setTripod3Choice(le, choice);
		if (getTripod3Choice(le) == 1) {
			setMaxStack(le, 2);
			setNowStack(le, 2);
		} else {
			setMaxStack(le, -1);
			setNowStack(le, -1);
		}
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
			overblood.put(uuid,
					new tickRunnable(att, vic, (damages1[getLevel(att) - 1] + damages2[getLevel(att) - 1]) * 0.68,
							times, tickType.blood).runTaskTimer(Core.getCore(), 10, 20));
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
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(this.groupType) * (tc2 == 1 ? 1.6d : 1);
		int level = getLevel(le);
		le.setVelocity(new Vector(0, 0.2, 0));
		new BukkitRunnable() {

			@Override
			public void run() {
				le.setVelocity(le.getLocation().getDirection().clone().setY(0).normalize());
			}
		}.runTaskLater(Core.getCore(), 2);
		for (int c = 0; c < 3; c++) {
			Rel[] dagger = daggers[c];
			List<Entity> list = new ArrayList<Entity>();
			new BukkitRunnable() {
				@Override
				public void run() {
					Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
					Vector dir = loc.getDirection().clone().setY(0).normalize();
					for (int i = 0; i < 7; i++) {
						final int j = i;
						new BukkitRunnable() {
							@Override
							public void run() {
								for (Rel rel : dagger) {
									Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(),
											rel.getHeight(), rel.getRight(), 1 + j * 0.25));
									EffectStore.spawnRedStone(now, 0, 255, 0, 0.5f, 10, 0, 0, 0);
									for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
										damage(list, le, et, damages1[level - 1] / 3d * mul);
								}
							}
						}.runTaskLater(Core.getCore(), 2 + i / 3);
					}
				}
			}.runTaskLater(Core.getCore(), 2 + c * 2);
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				le.setVelocity(new Vector());
				Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
				Vector dir = loc.getDirection().clone().setY(0).normalize();
				List<Entity> list = new ArrayList<Entity>();
				int i = 0;
				for (Rel rel : last) {
					i++;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.2));
							EffectStore.spawnRedStone(now, 0, 255, 0, 1f, 10, 0, 0, 0);
							for (Entity et : now.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75)) {
								if (tc2 == 2) {
									ds.setExtraCriticalChance(spinningDagger.this.displayName, 2);
									ds.setExtraCriticalDamage(spinningDagger.this.displayName, 1);
								}
								if (damage(list, le, et, damages2[level - 1] * mul))
									if (tc1 == 0) {
										runPoison(le, (LivingEntity) et,
												(damages1[level - 1] + damages2[level - 1]) * 0.03, 8, 3);
										BuffRunnable.run(le, spinningDagger.this, 12, 0); // 피증
										ds.setDamageIncrease(spinningDagger.this.displayName, 0.12);
									} else if (tc1 == 1)
										runBlood(le, (LivingEntity) et,
												(damages1[level - 1] + damages2[level - 1]) * 0.03, 8, 3);
								ds.setExtraCriticalChance(spinningDagger.this.displayName, 0);
								ds.setExtraCriticalDamage(spinningDagger.this.displayName, 0);
							}

						}
					}.runTaskLater(Core.getCore(), i / 2);
				}
				if (tc3 == 0) {
					List<Entity> list2 = new ArrayList<Entity>();
					for (Rel rel : last) {
						i++;
						new BukkitRunnable() {
							@Override
							public void run() {
								Location now = loc.clone().add(
										EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.2));
								EffectStore.spawnRedStone(now, 0, 255, 0, 1f, 10, 0, 0, 0);
								for (Entity et : now.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75)) {
									if (tc2 == 2) {
										ds.setExtraCriticalChance(spinningDagger.this.displayName, 2);
										ds.setExtraCriticalDamage(spinningDagger.this.displayName, 1);
									}
									if (damage(list2, le, et,
											(damages1[level - 1] + damages2[level - 1]) * 0.8 * mul)) {
										if (tc1 == 0) {
											runPoison(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.03, 8, 3);
											BuffRunnable.run(le, spinningDagger.this, 12, 0); // 피증
											ds.setDamageIncrease(spinningDagger.this.displayName, 0.12);
										} else if (tc1 == 1)
											runBlood(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.03, 8, 3);
									}
									ds.setExtraCriticalChance(spinningDagger.this.displayName, 0);
									ds.setExtraCriticalDamage(spinningDagger.this.displayName, 0);
								}
							}
						}.runTaskLater(Core.getCore(), i / 2);
					}
				}
			}
		}.runTaskLater(Core.getCore(), 7);
		return false;
	}
}
