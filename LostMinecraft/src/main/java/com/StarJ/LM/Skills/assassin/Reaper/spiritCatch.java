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
import com.StarJ.LM.Skills.Runnable.DebuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class spiritCatch extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 69, 122, 157, 183, 205, 222, 237, 249, 259, 269, 295, 325 };
	protected final double[] damages2 = new double[] { 208, 369, 473, 553, 616, 670, 714, 752, 784, 813, 894, 983 };

	public spiritCatch() {
		super("spiritCatch", ChatColor.WHITE + "스피릿 캐치", Skill.assassin.Reaper.skillGroup.Dagger, 8, 31, 333,
				AttackType.Back, "전방을 향해 빠르게 찔러 피해를 준다.", "돌진해 마지막 공격으로 피해를 준다.");
		tripodChoice _11 = new tripodChoice("독:부식", "마지막 공격에 단검 공격에 '부식 독'을 부여한다.",
				"부식 독은 8.0초 동안 매 초마다 기본 피해량의 2% 피해를 준다.", "최대 3회까지 중첩된다.", "스킬 사용시 12초간 적에게 12.0% 증가된 피해를 입힌다.");
		tripodChoice _12 = new tripodChoice("독:출혈", "마지막 공격에 단검 공격에 '출혈 독'을 부여한다.",
				"출혈 독은 8.0초 동안 매 초마다 기본 피해량의 2% 피해를 준다.", "최대 3중첩이 가능하다", "3중첩 시 '과다 출혈'를 부여한다.",
				"과다 출혈은 8.0초 동안 매 초마다 기본 피해량의 50% 피해를 준다.");
		tripodChoice _13 = new tripodChoice("독:신경", "테스트용");
		tripodChoice _21 = new tripodChoice("도약", "테스트용");
		tripodChoice _22 = new tripodChoice("발목 절단", "공격 적중 시 6.0초간 이동속도를 46.2% 감소시킨다.");
		tripodChoice _23 = new tripodChoice("날카로운 단검", "치명타 피해가 100.0% 증가한다.");
		tripodChoice _31 = new tripodChoice("그림자 잔상", "바로 마지막 공격을 한다.", "그림자 잔상이 추가로 공격한다.",
				"마지막 공격의 140.0% 피해를 주며, 독 중첩을 시킨다.");
		tripodChoice _32 = new tripodChoice("속전속결", "바로 마지막 공격을 한다.", "피해가 195.0% 증가한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 1 ? 1 : 2;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc3 == 1)
			mul *= 2.95d;
		return (damages1[level - 1] + damages2[level - 1]) * mul + (tc3 == 0 ? damages2[level - 1] * 1.4 * mul : 0)
				+ (tc1 == 0 || tc1 == 1 ? (damages1[level - 1] + damages2[level - 1]) * (tc3 == 0 ? 0.32 : 0.16) : 0);
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

	@Override
	public int getDestruction() {
		return 1;
	}

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		if (num == 0)
			Core.getCore().getHashMapStore().getDataStore(att).setDamageIncrease(this.displayName, 0);
		super.BuffEnd(att, num);
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
					new tickRunnable(att, vic, (damages1[getLevel(att) - 1] + damages2[getLevel(att) - 1]) * 0.5, times,
							tickType.blood).runTaskTimer(Core.getCore(), 10, 20));
		} else {
			sbt.setTask(new tickRunnable(att, vic, damage * stack, times, tickType.blood).runTaskTimer(Core.getCore(),
					10, 20));
			sbt.setStack(stack);
		}
		return;
	}

	Rel[] sting = new Rel[] { //
			new Rel(0.25, 0, 0), new Rel(0.5, 0, 0), new Rel(0.75, 0, 0), new Rel(1, 0, 0), //
			new Rel(1.25, 0, 0), new Rel(1.5, 0, 0), new Rel(1.75, 0, 0), new Rel(2, 0, 0), //
			new Rel(2.25, 0, 0), new Rel(2.5, 0, 0),//
	};

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		int tc2 = getTripod2Choice(att);
		if (tc2 == 2)
			ds.setExtraCriticalDamage(this.displayName, 1);
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		if (!flag)
			if (tc2 == 1) {
				LivingEntity vic = (LivingEntity) vic_e;
				DebuffRunnable.run(att, vic, this, 6, "발목 절단", 0);
				DataStore.setWalkSpeed(vic, this.displayName, -0.462);
				DataStore.applyWalkspeed(vic);
			}
		ds.setExtraCriticalDamage(this.displayName, 0);
		return flag;
	}

	@Override
	public void DebuffEnd(LivingEntity att, LivingEntity vic, int num) {
		if (num == 0) {
			DataStore.setWalkSpeed(vic, this.displayName, 0);
			DataStore.applyWalkspeed(vic);
		}
		super.DebuffEnd(att, vic, num);
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection().clone().setY(0).normalize();
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);

		double mul = ds.getMultiplyDamage(groupType);

		if (tc3 == 0) {
			List<Entity> list = new ArrayList<Entity>();
			List<Entity> list2 = new ArrayList<Entity>();
			le.setVelocity(new Vector(0, 0.2, 0));
			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(le.getLocation().getDirection());
				}
			}.runTaskLater(Core.getCore(), 2);

			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(new Vector());
					int i = 0;
					for (Rel rel : sting) {
						i++;
						new BukkitRunnable() {
							@Override
							public void run() {
								Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
								Vector dir = loc.getDirection().clone().setY(0).normalize();
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.1, 0.1, 0.1);
								for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
									if (damage(list, le, et, (damages1[level - 1] + damages2[level - 1]) * mul))
										if (tc1 == 0) {
											runPoison(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);
											BuffRunnable.run(le, spiritCatch.this, 12, 0); // 피증
											ds.setDamageIncrease(spiritCatch.this.displayName, 0.12);
										} else if (tc1 == 1)
											runBlood(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);

							}
						}.runTaskLater(Core.getCore(), i / 3);
					}
					for (Rel rel : sting) {
						i++;
						new BukkitRunnable() {
							@Override
							public void run() {
								Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
								Vector dir = loc.getDirection().clone().setY(0).normalize();
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.1, 0.1, 0.1);
								for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
									if (damage(list2, le, et, damages2[level - 1] * 1.4 * mul))
										if (tc1 == 0) {
											runPoison(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);
											BuffRunnable.run(le, spiritCatch.this, 12, 0); // 피증
											ds.setDamageIncrease(spiritCatch.this.displayName, 0.12);
										} else if (tc1 == 1)
											runBlood(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);
							}
						}.runTaskLater(Core.getCore(), i / 3);
					}
				}
			}.runTaskLater(Core.getCore(), 5);
		} else if (tc3 == 1) {
			List<Entity> list = new ArrayList<Entity>();
			le.setVelocity(new Vector(0, 0.2, 0));
			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(le.getLocation().getDirection());
				}
			}.runTaskLater(Core.getCore(), 2);

			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(new Vector());
					int i = 0;
					for (Rel rel : sting) {
						i++;
						new BukkitRunnable() {

							@Override
							public void run() {
								Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
								Vector dir = loc.getDirection().clone().setY(0).normalize();
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.1, 0.1, 0.1);
								for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
									if (damage(list, le, et, (damages1[level - 1] + damages2[level - 1]) * mul * 2.95))
										if (tc1 == 0) {
											runPoison(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);
											BuffRunnable.run(le, spiritCatch.this, 12, 0); // 피증
											ds.setDamageIncrease(spiritCatch.this.displayName, 0.12);
										} else if (tc1 == 1)
											runBlood(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);

							}
						}.runTaskLater(Core.getCore(), i / 3);
					}
				}
			}.runTaskLater(Core.getCore(), 5);
		} else {
			List<Entity> list = new ArrayList<Entity>();
			List<Entity> list2 = new ArrayList<Entity>();
			int i = 0;
			for (Rel rel : sting) {
				i++;
				new BukkitRunnable() {

					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.1, 0.1, 0.1);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
							damage(list, le, et, damages1[level - 1] * mul);
					}
				}.runTaskLater(Core.getCore(), i / 3);
			}

			new BukkitRunnable() {

				@Override
				public void run() {
					le.setVelocity(new Vector(0, 0.2, 0));
				}
			}.runTaskLater(Core.getCore(), i / 3);

			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(le.getLocation().getDirection());
				}
			}.runTaskLater(Core.getCore(), i / 3 + 2);

			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(new Vector());
					int i = 0;
					for (Rel rel : sting) {
						i++;
						new BukkitRunnable() {

							@Override
							public void run() {
								Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
								Vector dir = loc.getDirection().clone().setY(0).normalize();
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.1, 0.1, 0.1);
								for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
									if (damage(list2, le, et, damages2[level - 1] * mul))
										if (tc1 == 0) {
											runPoison(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);
											BuffRunnable.run(le, spiritCatch.this, 12, 0); // 피증
											ds.setDamageIncrease(spiritCatch.this.displayName, 0.12);
										} else if (tc1 == 1)
											runBlood(le, (LivingEntity) et,
													(damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);
							}
						}.runTaskLater(Core.getCore(), i / 3);
					}
				}
			}.runTaskLater(Core.getCore(), i / 3 + 5);
		}
		return false;
	}
}
