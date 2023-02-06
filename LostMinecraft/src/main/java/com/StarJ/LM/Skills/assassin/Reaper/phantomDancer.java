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
import com.StarJ.LM.Skills.Runnable.DebuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;
import com.StarJ.LM.System.EntityStore;

public class phantomDancer extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 73, 130, 167, 196, 218, 237, 253, 266, 277, 287, 315, 347 };

	public phantomDancer() {
		super("phantomDancer", ChatColor.WHITE + "팬텀 댄서", Skill.assassin.Reaper.skillGroup.Dagger, 10, 46, 375,
				AttackType.Back, "춤을 추듯 돌며 피해를 주며 이동한다.", "스킬을 다시 한번 입력 시 추가로 피해를 주며 이동한다.");
		tripodChoice _11 = new tripodChoice("독:부식", "마지막 공격에 단검 공격에 '부식 독'을 부여한다.",
				"부식 독은 8.0초 동안 매 초마다 기본 피해량의 2% 피해를 준다.", "최대 3회까지 중첩된다.", "스킬 적중시 12초간 적에게 주는 피해가 12.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("독:출혈", "마지막 공격에 단검 공격에 '출혈 독'을 부여한다.",
				"출혈 독은 8.0초 동안 매 초마다 기본 피해량의 2% 피해를 준다.", "최대 3중첩이 가능하다", "3중첩 시 '과다 출혈'를 부여한다.",
				"과다 출혈은 8.0초 동안 매 초마다 기본 피해량의 47% 피해를 준다.");
		tripodChoice _13 = new tripodChoice("독:신경", "테스트용");
		tripodChoice _21 = new tripodChoice("발목 절단", "공격 적중 시 3.0초간 이동속도를 35.0% 감소시킨다.");
		tripodChoice _22 = new tripodChoice("넓은 공격", "테스트용");
		tripodChoice _23 = new tripodChoice("날카로운 단검", "치명타 피해량이 90.0%증가한다.");
		tripodChoice _31 = new tripodChoice("마무리 동작", "두 번째 공격한 지점과 마지막 공격한 지점에 그림자 잔상이 소환된다.",
				"기본 피해량의 80.0% 피해를 준다.");
		tripodChoice _32 = new tripodChoice("폭풍의 춤", "처음 공격한 지점과 세 번째 공격한 지점에 그림자 잔상이 소환된다.",
				"기본 피해량의 94.8% 피해를 주며 회전한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		int tc3 = getTripod3Choice(le);
		return 4 + (tc3 == 0 ? 2 : (tc3 == 1 ? 10 : 0));
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Chain;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc3 == 0)
			mul *= 1.8d;
		else if (tc3 == 1)
			mul *= 1.948d;
		return damages[level - 1] * 4 * mul;
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
			overblood.put(uuid, new tickRunnable(att, vic, damages[getLevel(att) - 1] * 4 * 0.47, times, tickType.blood)
					.runTaskTimer(Core.getCore(), 10, 20));
		} else {
			sbt.setTask(new tickRunnable(att, vic, damage * stack, times, tickType.blood).runTaskTimer(Core.getCore(),
					10, 20));
			sbt.setStack(stack);
		}
		return;
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

	private Rel[] sliceRight = new Rel[] { //
			new Rel(0.5, 0.5, -1.5), new Rel(0.75, 0.5, -1.25), //
			new Rel(1, 0.5, -1), new Rel(1.25, 0.375, -0.75), //
			new Rel(1.5, 0.25, -0.5), new Rel(1.5, 0.125, -0.25), //
			new Rel(1.5, 0, 0), new Rel(1.5, -0.125, 0.25), //
			new Rel(1.5, -0.25, 0.5), new Rel(1.25, -0.375, 0.75), //
			new Rel(1, -0.5, 1), new Rel(0.75, -0.5, 1.25), //
			new Rel(0.5, -0.5, 1.5),//
	};
	private Rel[] sliceLeft = new Rel[] { //
			new Rel(0.5, 0.5, 1.5), new Rel(0.75, 0.5, 1.25), //
			new Rel(1, 0.5, 1), new Rel(1.25, 0.375, 0.75), //
			new Rel(1.5, 0.25, 0.5), new Rel(1.5, 0.125, 0.25), //
			new Rel(1.5, 0, 0), new Rel(1.5, -0.125, -0.25), //
			new Rel(1.5, -0.25, -0.5), new Rel(1.25, -0.375, -0.75), //
			new Rel(1, -0.5, -1), new Rel(0.75, -0.5, -1.25), //
			new Rel(0.5, -0.5, -1.5),//

	};
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
		if (num == 0)
			Core.getCore().getHashMapStore().getDataStore(att).setDamageIncrease(this.displayName, 0);
		super.BuffEnd(att, num);
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
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		int tc2 = getTripod2Choice(att);

		if (tc2 == 2)
			ds.setExtraCriticalDamage(this.displayName, 0.9);
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		if (flag && tc2 == 1) {
			LivingEntity vic = (LivingEntity) vic_e;
			DebuffRunnable.run(att, vic, this, 6, "발목 절단", 0);
			DataStore.setWalkSpeed(vic, this.displayName, -0.462);
			DataStore.applyWalkspeed(vic);
		}
		ds.setExtraCriticalDamage(this.displayName, 0);
		return flag;

	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection().setY(0).normalize();

		if (ComboCoolRunnable.hasCombo(le, this)) {
			le.setVelocity(new Vector(0, 0.2, 0));
			new BukkitRunnable() {

				@Override
				public void run() {
					le.setVelocity(dir);
				}
			}.runTaskLater(Core.getCore(), 2);

			new BukkitRunnable() {

				@Override
				public void run() {
					le.setVelocity(new Vector());
					Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
					Vector dir = loc.getDirection().setY(0).normalize();
					int i = 0;
					List<Entity> list = new ArrayList<Entity>();
					for (Rel rel : sliceRight) {
						i++;
						new BukkitRunnable() {
							@Override
							public void run() {
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
									damage(list, le, et, damages[level - 1] * mul);
							}
						}.runTaskLater(Core.getCore(), i / 3);
					}
					if (tc3 == 1) {
						LivingEntity shadow = (LivingEntity) EntityStore.Shadow.spawnEntity(le.getLocation());
						new BukkitRunnable() {
							@Override
							public void run() {
								int ii = 0;
								for (; ii < 5; ii++)
									new BukkitRunnable() {
										@Override
										public void run() {
											Location loc = shadow.getEyeLocation().clone().subtract(0, 0.25, 0);
											List<Entity> list = new ArrayList<Entity>();
											for (int c = 0; c <= 3; c++) {
												final int j = c;
												new BukkitRunnable() {
													@Override
													public void run() {
														for (Rel rel : circle) {
															Location now = loc.clone()
																	.add(EffectStore.getRel(dir, rel.getFront(),
																			rel.getHeight(), rel.getRight(),
																			0.5 + j * 0.25));
															EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0, 0,
																	0);
															for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1,
																	1))
																damage(list, le, et,
																		damages[level - 1] * 2 * 0.948 / 5 * mul,
																		AttackType.getAttackType(et, shadow));
														}
													}
												}.runTaskLater(Core.getCore(), c / 2);
											}
										}
									}.runTaskLater(Core.getCore(), ii * 10);
								new BukkitRunnable() {
									@Override
									public void run() {
										shadow.remove();
									}
								}.runTaskLater(Core.getCore(), ii * 10 + 3 / 2 + 2);
							}
						}.runTaskLater(Core.getCore(), 10);
					}
					le.setVelocity(new Vector(0, 0.2, 0));
					new BukkitRunnable() {

						@Override
						public void run() {
							le.setVelocity(dir);
						}
					}.runTaskLater(Core.getCore(), 2);
					new BukkitRunnable() {

						@Override
						public void run() {
							le.setVelocity(new Vector());
							Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
							Vector dir = loc.getDirection().setY(0).normalize();
							int i = 0;
							List<Entity> list = new ArrayList<Entity>();
							for (Rel rel : sliceRight) {
								i++;
								new BukkitRunnable() {
									@Override
									public void run() {
										Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(),
												rel.getHeight(), rel.getRight()));
										EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
										for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
											if (damage(list, le, et, damages[level - 1] * mul))
												if (tc1 == 0) {
													runPoison(le, (LivingEntity) et, damages[level - 1] * 0.02, 8, 3);
													BuffRunnable.run(le, phantomDancer.this, 12, 0); // 피증
													ds.setDamageIncrease(phantomDancer.this.displayName, 0.12);
												} else if (tc1 == 1)
													runBlood(le, (LivingEntity) et, damages[level - 1] * 0.02, 8, 3);

									}
								}.runTaskLater(Core.getCore(), i / 3);
							}
							if (tc3 == 0) {
								LivingEntity shadow = (LivingEntity) EntityStore.Shadow.spawnEntity(le.getLocation());
								new BukkitRunnable() {
									@Override
									public void run() {
										Location loc = shadow.getEyeLocation().clone().subtract(0, 0.25, 0);
										List<Entity> list = new ArrayList<Entity>();
										for (int c = 0; c <= 3; c++) {
											final int j = c;
											new BukkitRunnable() {
												@Override
												public void run() {
													for (Rel rel : circle) {
														Location now = loc.clone()
																.add(EffectStore.getRel(dir, rel.getFront(),
																		rel.getHeight(), rel.getRight(),
																		0.5 + j * 0.25));
														EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0, 0, 0);
														for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
															damage(list, le, et, damages[level - 1] * 2 * 0.8 * mul,
																	AttackType.getAttackType(et, shadow));
													}
												}
											}.runTaskLater(Core.getCore(), c / 2);
										}
										new BukkitRunnable() {
											@Override
											public void run() {
												shadow.remove();
											}
										}.runTaskLater(Core.getCore(), 3 / 2 + 2);
									}
								}.runTaskLater(Core.getCore(), 10);
							}
						}
					}.runTaskLater(Core.getCore(), 4);
				}
			}.runTaskLater(Core.getCore(), 3);
			comboEnd(le);
		} else {
			ComboCoolRunnable.run(le, this, getComboDuration(le));
			le.setVelocity(new Vector(0, 0.2, 0));
			new BukkitRunnable() {

				@Override
				public void run() {
					le.setVelocity(dir);
				}
			}.runTaskLater(Core.getCore(), 2);

			new BukkitRunnable() {

				@Override
				public void run() {
					le.setVelocity(new Vector());
					Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
					Vector dir = loc.getDirection().setY(0).normalize();
					int i = 0;
					List<Entity> list = new ArrayList<Entity>();
					for (Rel rel : sliceLeft) {
						i++;
						new BukkitRunnable() {
							@Override
							public void run() {
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
									damage(list, le, et, damages[level - 1] * mul);
							}
						}.runTaskLater(Core.getCore(), i / 3);
					}
					if (tc3 == 1) {
						LivingEntity shadow = (LivingEntity) EntityStore.Shadow.spawnEntity(le.getLocation());
						new BukkitRunnable() {
							@Override
							public void run() {
								int ii = 0;
								for (; ii < 5; ii++)
									new BukkitRunnable() {
										@Override
										public void run() {
											Location loc = shadow.getEyeLocation().clone().subtract(0, 0.25, 0);
											List<Entity> list = new ArrayList<Entity>();
											for (int c = 0; c <= 3; c++) {
												final int j = c;
												new BukkitRunnable() {
													@Override
													public void run() {
														for (Rel rel : circle) {
															Location now = loc.clone()
																	.add(EffectStore.getRel(dir, rel.getFront(),
																			rel.getHeight(), rel.getRight(),
																			0.5 + j * 0.25));
															EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0, 0,
																	0);
															for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1,
																	1))
																damage(list, le, et,
																		damages[level - 1] * 2 * 0.948 / 5 * mul,
																		AttackType.getAttackType(et, shadow));
														}
													}
												}.runTaskLater(Core.getCore(), c / 2);
											}
										}
									}.runTaskLater(Core.getCore(), ii * 10);
								new BukkitRunnable() {
									@Override
									public void run() {
										shadow.remove();
									}
								}.runTaskLater(Core.getCore(), ii * 10 + 3 / 2 + 2);
							}
						}.runTaskLater(Core.getCore(), 10);
					}
					le.setVelocity(new Vector(0, 0.2, 0));
					new BukkitRunnable() {

						@Override
						public void run() {
							le.setVelocity(dir);
						}
					}.runTaskLater(Core.getCore(), 2);
					new BukkitRunnable() {

						@Override
						public void run() {
							le.setVelocity(new Vector());
							Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
							Vector dir = loc.getDirection().setY(0).normalize();
							int i = 0;
							List<Entity> list = new ArrayList<Entity>();
							for (Rel rel : sliceLeft) {
								i++;
								new BukkitRunnable() {
									@Override
									public void run() {
										Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(),
												rel.getHeight(), rel.getRight()));
										EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
										for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
											damage(list, le, et, damages[level - 1] * mul);
									}
								}.runTaskLater(Core.getCore(), i / 3);
							}
							if (tc3 == 0) {
								LivingEntity shadow = (LivingEntity) EntityStore.Shadow.spawnEntity(le.getLocation());
								new BukkitRunnable() {
									@Override
									public void run() {
										Location loc = shadow.getEyeLocation().clone().subtract(0, 0.25, 0);
										List<Entity> list = new ArrayList<Entity>();
										for (int c = 0; c <= 3; c++) {
											final int j = c;
											new BukkitRunnable() {
												@Override
												public void run() {
													for (Rel rel : circle) {
														Location now = loc.clone()
																.add(EffectStore.getRel(dir, rel.getFront(),
																		rel.getHeight(), rel.getRight(),
																		0.5 + j * 0.25));
														EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0, 0, 0);
														for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
															damage(list, le, et, damages[level - 1] * 2 * 0.8 * mul,
																	AttackType.getAttackType(et, shadow));
													}
												}
											}.runTaskLater(Core.getCore(), c / 2);
										}
										new BukkitRunnable() {
											@Override
											public void run() {
												shadow.remove();
											}
										}.runTaskLater(Core.getCore(), 3 / 2 + 2);
									}
								}.runTaskLater(Core.getCore(), 10);
							}
						}
					}.runTaskLater(Core.getCore(), 4);
				}
			}.runTaskLater(Core.getCore(), 3);
		}

		return false;
	}
}
