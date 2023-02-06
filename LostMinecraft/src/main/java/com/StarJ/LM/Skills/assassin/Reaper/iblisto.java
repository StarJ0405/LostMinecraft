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

public class iblisto extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 102, 181, 232, 272, 303, 330, 352, 371, 387, 401, 441, 485 };

	public iblisto() {
		super("iblisto", ChatColor.WHITE + "이블리스토", Skill.assassin.Reaper.skillGroup.Dagger, 12, 21, 428,
				AttackType.Back, "전방으로 4회 광기어린 난도질을 하여 피해를 준다.");
		tripodChoice _11 = new tripodChoice("독:부식", "마지막 공격에 단검 공격에 '부식 독'을 부여한다.",
				"부식 독은 8.0초 동안 매 초마다 기본 피해량의 1% 피해를 준다.", "최대 3회까지 중첩된다.", "스킬 적중시 12초간 적에게 주는 피해가 12.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("독:출혈", "마지막 공격에 단검 공격에 '출혈 독'을 부여한다.",
				"출혈 독은 8.0초 동안 매 초마다 기본 피해량의 1% 피해를 준다.", "최대 3중첩이 가능하다", "3중첩 시 '과다 출혈'를 부여한다.",
				"과다 출혈은 8.0초 동안 매 초마다 기본 피해량의 34% 피해를 준다.");
		tripodChoice _13 = new tripodChoice("독:신경", "테스트용");
		tripodChoice _21 = new tripodChoice("강인함", "테스트용");
		tripodChoice _22 = new tripodChoice("치명적인 단검", "치명타 적중률이 60.0% 증가한다.");
		tripodChoice _23 = new tripodChoice("심호흡", "테스트용");
		tripodChoice _31 = new tripodChoice("죽음의 일격", "테스트용");
		tripodChoice _32 = new tripodChoice("속전속결", "공격 횟수가 2회로 변경 되어 기본 피해량의 160.0% 피해를 준다.", "2회 모두 독 중첩을 부여한다.");
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
			mul *= 1.6d;
		return damages[level - 1] * 4 * mul
				+ (tc1 == 0 || tc1 == 1 ? damages[level - 1] * (tc3 == 1 ? 0.16 : 0.08) : 0);
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
			overblood.put(uuid, new tickRunnable(att, vic, damages[getLevel(att) - 1] * 4 * 0.34, times, tickType.blood)
					.runTaskTimer(Core.getCore(), 10, 20));
		} else {
			sbt.setTask(new tickRunnable(att, vic, damage * stack, times, tickType.blood).runTaskTimer(Core.getCore(),
					10, 20));
			sbt.setStack(stack);
		}
		return;
	}

	private Rel[] LeftUpToRightDown = new Rel[] { //
			new Rel(0.5, 2, -1), new Rel(0.75, 2, -1), //
			new Rel(1, 2, -1), new Rel(1.25, 1.75, -0.75), //
			new Rel(1.5, 1.5, -0.5), new Rel(1.75, 1.25, -0.75), //
			new Rel(2, 1, 0), new Rel(1.75, 0.75, 0.25), //
			new Rel(1.5, 0.5, 0.5), new Rel(1.25, 0.25, 0.75), //
			new Rel(1, 0, 1), new Rel(0.75, 0, 1), //
			new Rel(0.5, 0, 1),//
	};
	private Rel[] RightUpToLeftDown = new Rel[] { //
			new Rel(0.5, 2, 1), new Rel(0.75, 2, 1), //
			new Rel(1, 2, 1), new Rel(1.25, 1.75, 0.75), //
			new Rel(1.5, 1.5, 0.5), new Rel(1.75, 1.25, 0.75), //
			new Rel(2, 1, 0), new Rel(1.75, 0.75, -0.25), //
			new Rel(1.5, 0.5, -0.5), new Rel(1.25, 0.25, -0.75), //
			new Rel(1, 0, -1), new Rel(0.75, 0, -1), //
			new Rel(0.5, 0, -1),//
	};
	private Rel[] RightDownToLeftUp = new Rel[] { //
			new Rel(0.5, 0, 1), //
			new Rel(0.75, 0, 1), new Rel(1, 0, 1), //
			new Rel(1.25, 0.25, 0.75), new Rel(1.5, 0.5, 0.5), //
			new Rel(1.75, 0.75, 0.25), new Rel(2, 1, 0), //
			new Rel(1.75, 1.25, -0.75), new Rel(1.5, 1.5, -0.5), //
			new Rel(1.25, 1.75, -0.75), new Rel(1, 2, -1), //
			new Rel(0.75, 2, -1), new Rel(0.5, 2, -1), //
	};
	private Rel[] LeftDownToRightUp = new Rel[] { //
			new Rel(0.5, 0, -1), //
			new Rel(0.75, 0, -1), new Rel(1, 0, -1), //
			new Rel(1.25, 0.25, -0.75), new Rel(1.5, 0.5, -0.5), //
			new Rel(1.75, 0.75, -0.25), new Rel(2, 1, 0), //
			new Rel(1.75, 1.25, 0.75), new Rel(1.5, 1.5, 0.5), //
			new Rel(1.25, 1.75, 0.75), new Rel(1, 2, 1), //
			new Rel(0.75, 2, 1), new Rel(0.5, 2, 1), //
	};

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (getTripod2Choice(att) == 1)
			ds.setExtraCriticalChance(this.displayName, 0.6);
		boolean end = super.damage(skill, destruction, list, att, vic_e, damage, now);
		ds.setExtraCriticalChance(this.displayName, 0);
		return end;

	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		double mul = ds.getMultiplyDamage(groupType) * (tc3 == 1 ? 1.6 : 1);
		if (tc3 == 1) {
			Location loc = le.getLocation();
			Vector dir = loc.getDirection().clone().setY(0).normalize();
			List<Entity> list = new ArrayList<Entity>();
			int i = 0;
			for (Rel rel : LeftUpToRightDown) {
				i++;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
						for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
							if (damage(list, le, et, damages[level - 1] * 2 * mul))
								if (tc1 == 0) {
									runPoison(le, (LivingEntity) et, damages[level - 1] * 0.01, 8, 3);
									BuffRunnable.run(le, iblisto.this, 12, 0); // 피증
									ds.setDamageIncrease(iblisto.this.displayName, 0.12);
								} else if (tc1 == 1)
									runBlood(le, (LivingEntity) et, damages[level - 1] * 0.01, 8, 3);
					}
				}.runTaskLater(Core.getCore(), i / 2);
			}
			new BukkitRunnable() {

				@Override
				public void run() {
					Location loc = le.getLocation();
					Vector dir = loc.getDirection().clone().setY(0).normalize();
					List<Entity> list = new ArrayList<Entity>();
					int i = 0;
					for (Rel rel : RightDownToLeftUp) {
						i++;
						new BukkitRunnable() {

							@Override
							public void run() {
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
								for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
									if (damage(list, le, et, damages[level - 1] * 2 * mul))
										if (tc1 == 0) {
											runPoison(le, (LivingEntity) et, damages[level - 1] * 0.01, 8, 3);
											BuffRunnable.run(le, iblisto.this, 12, 0); // 피증
											ds.setDamageIncrease(iblisto.this.displayName, 0.12);
										} else if (tc1 == 1)
											runBlood(le, (LivingEntity) et, damages[level - 1] * 0.01, 8, 3);
							}
						}.runTaskLater(Core.getCore(), i / 2);
					}
				}
			}.runTaskLater(Core.getCore(), 8);
		} else {
			Location loc = le.getLocation();
			Vector dir = loc.getDirection().clone().setY(0).normalize();
			List<Entity> list = new ArrayList<Entity>();
			int i = 0;
			for (Rel rel : RightUpToLeftDown) {
				i++;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
						for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
							damage(list, le, et, damages[level - 1] * mul);
					}
				}.runTaskLater(Core.getCore(), i / 2);
			}
			new BukkitRunnable() {

				@Override
				public void run() {
					Location loc = le.getLocation();
					Vector dir = loc.getDirection().clone().setY(0).normalize();
					List<Entity> list = new ArrayList<Entity>();
					int i = 0;
					for (Rel rel : LeftUpToRightDown) {
						i++;
						new BukkitRunnable() {

							@Override
							public void run() {
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
								for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
									damage(list, le, et, damages[level - 1] * mul);
							}
						}.runTaskLater(Core.getCore(), i / 2);
					}
				}
			}.runTaskLater(Core.getCore(), 8);
			new BukkitRunnable() {

				@Override
				public void run() {
					Location loc = le.getLocation();
					Vector dir = loc.getDirection().clone().setY(0).normalize();
					List<Entity> list = new ArrayList<Entity>();
					int i = 0;
					for (Rel rel : RightDownToLeftUp) {
						i++;
						new BukkitRunnable() {
							@Override
							public void run() {
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
								for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
									damage(list, le, et, damages[level - 1] * mul);
							}
						}.runTaskLater(Core.getCore(), i / 2);
					}
				}
			}.runTaskLater(Core.getCore(), 8 * 2);

			new BukkitRunnable() {

				@Override
				public void run() {
					Location loc = le.getLocation();
					Vector dir = loc.getDirection().clone().setY(0).normalize();
					List<Entity> list = new ArrayList<Entity>();
					int i = 0;
					for (Rel rel : LeftDownToRightUp) {
						i++;
						new BukkitRunnable() {
							@Override
							public void run() {
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1, 0.1, 0.1);
								for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
									if (damage(list, le, et, damages[level - 1] * mul))
										if (tc1 == 0) {
											runPoison(le, (LivingEntity) et, damages[level - 1] * 0.01, 8, 3);
											BuffRunnable.run(le, iblisto.this, 12, 0); // 피증
											ds.setDamageIncrease(iblisto.this.displayName, 0.12);
										} else if (tc1 == 1)
											runBlood(le, (LivingEntity) et, damages[level - 1] * 0.01, 8, 3);
							}
						}.runTaskLater(Core.getCore(), i / 2);
					}
				}
			}.runTaskLater(Core.getCore(), 8 * 3);
		}
		return false;
	}
}
