package com.StarJ.LM.Skills.assassin.Reaper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.Skills.Runnable.ComboCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class deathSide extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 103, 183, 234, 274, 306, 332, 354, 373, 389, 403, 443, 487 };
	protected final double[] damages2 = new double[] { 239, 425, 545, 637, 710, 773, 824, 868, 905, 939, 1032, 1136 };

	public deathSide() {
		super("deathSide", ChatColor.WHITE + "데스 사이드", Skill.assassin.Reaper.skillGroup.Dagger, 12, 25, 375,
				AttackType.Back, "빠르게 전진하며 2회 돌려 베어 피해를 준다.", "스킬을 다시 한번 입력 시 크게 베어 피해를 준다.");
		tripodChoice _11 = new tripodChoice("독:부식", "마지막 공격에 단검 공격에 '부식 독'을 부여한다.",
				"부식 독은 8.0초 동안 매 초마다 기본 피해량의 2% 피해를 준다.", "최대 3회까지 중첩된다.", "스킬 적중시 12초간 적에게 주는 피해가 12.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("독:출혈", "마지막 공격에 단검 공격에 '출혈 독'을 부여한다.",
				"출혈 독은 8.0초 동안 매 초마다 기본 피해량의 2% 피해를 준다.", "최대 3중첩이 가능하다", "3중첩 시 '과다 출혈'를 부여한다.",
				"과다 출혈은 8.0초 동안 매 초마다 기본 피해량의 40% 피해를 준다.");
		tripodChoice _13 = new tripodChoice("독:신경", "테스트용");
		tripodChoice _21 = new tripodChoice("기습", "공격이 백어택으로 적중 시 적에게 주는 피해가 70.0% 증가한다.");
		tripodChoice _22 = new tripodChoice("약육강식", "테스트용");
		tripodChoice _23 = new tripodChoice("치명적인 단검", "치명타 적중률이 60.0% 증가한다.");
		tripodChoice _31 = new tripodChoice("데스 콜", "일반 조작으로 변경된다.", "더욱 크게 1회 베어 기본 피해량의 120.0% 피해를 준다.",
				"치명타 피해량이 160.0% 증가한다.");
		tripodChoice _32 = new tripodChoice("폭풍의 눈", "테스트용");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? SkillType.Normal : SkillType.Combo;
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? 1 : 3;
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
			mul *= 1.2d;
		return (damages1[level - 1] + damages2[level - 1]) * mul;
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
	private Rel[] RightDownToLeftUp = new Rel[] { //
			new Rel(0.5, 0, 1), //
			new Rel(0.75, 0, 1), new Rel(1, 0, 1), //
			new Rel(1.25, 0.25, 0.75), new Rel(1.5, 0.5, 0.5), //
			new Rel(1.75, 0.75, 0.25), new Rel(2, 1, 0), //
			new Rel(1.75, 1.25, -0.75), new Rel(1.5, 1.5, -0.5), //
			new Rel(1.25, 1.75, -0.75), new Rel(1, 2, -1), //
			new Rel(0.75, 2, -1), new Rel(0.5, 2, -1), //
	};
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
					new tickRunnable(att, vic, (damages1[getLevel(att) - 1] + damages2[getLevel(att) - 1]) * 0.4, times,
							tickType.blood).runTaskTimer(Core.getCore(), 10, 20));
		} else {
			sbt.setTask(new tickRunnable(att, vic, damage * stack, times, tickType.blood).runTaskTimer(Core.getCore(),
					10, 20));
			sbt.setStack(stack);
		}
		return;
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		int tc2 = getTripod2Choice(att);
		if (now != null && now.equals(AttackType.Back) && tc2 == 0)
			damage *= 1.7d;
		else if (tc2 == 2)
			ds.setExtraCriticalChance(this.displayName, 0.6);
		if (getTripod3Choice(att) == 0)
			ds.setExtraCriticalDamage(this.displayName, 1.6);
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		ds.setExtraCriticalChance(this.displayName, 0);
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
		if (tc3 == 0) {
			List<Entity> list = new ArrayList<Entity>();
			for (Rel rel : RightDownToLeftUp) {
				Location now = loc.clone()
						.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.2d));
				EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
				for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
					if (damage(list, le, et, (damages1[level - 1] + damages2[level - 1]) * 1.2 * mul)) {
						if (tc1 == 0) {
							runPoison(le, (LivingEntity) et, (damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);
							BuffRunnable.run(le, this, 12, 0); // 피증
							ds.setDamageIncrease(this.displayName, 0.12);
						} else if (tc1 == 1)
							runBlood(le, (LivingEntity) et, (damages1[level - 1] + damages2[level - 1]) * 0.02, 8, 3);
					}
			}
		} else {
			if (ComboCoolRunnable.hasCombo(le, this)) {
				List<Entity> list = new ArrayList<Entity>();
				for (Rel rel : RightDownToLeftUp) {
					Location now = loc.clone()
							.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
					EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
					for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
						if (damage(list, le, et, damages2[level - 1] * mul)) {
							if (tc1 == 0) {
								runPoison(le, (LivingEntity) et, (damages1[level - 1] + damages2[level - 1]) * 0.02, 8,
										3);
								BuffRunnable.run(le, this, 12, 0); // 피증
								ds.setDamageIncrease(this.displayName, 0.12);
							} else if (tc1 == 1)
								runBlood(le, (LivingEntity) et, (damages1[level - 1] + damages2[level - 1]) * 0.02, 8,
										3);
						}
				}
				comboEnd(le);
			} else {
				List<Entity> list1 = new ArrayList<Entity>();
				List<Entity> list2 = new ArrayList<Entity>();
				for (Rel rel : circle) {
					Location now = loc.clone()
							.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 0.5));
					EffectStore.spawnRedStone(now, 0, 255, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
					for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1)) {
						damage(list1, le, et, damages1[level - 1] / 2 * mul);
						damage(list2, le, et, damages1[level - 1] / 2 * mul);
					}
				}
				ComboCoolRunnable.run(le, this, getComboDuration(le));
			}
		}
		return false;
	}
}
