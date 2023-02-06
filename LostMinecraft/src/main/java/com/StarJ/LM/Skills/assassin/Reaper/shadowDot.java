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
import com.StarJ.LM.Skills.Runnable.SkillCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class shadowDot extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 50, 89, 113, 132, 147, 161, 172, 181, 189, 196, 215, 237 };
	protected final double[] damages2 = new double[] { 101, 178, 228, 267, 298, 324, 346, 364, 380, 394, 433, 476 };

	public shadowDot() {
		super("shadowDot", ChatColor.WHITE + "쉐도우 닷", Skill.assassin.Reaper.skillGroup.Dagger, 7, 21, 272,
				AttackType.Back, "단검에 힘을 모아 피해를 준다.", "스킬을 다시 한번 입력 시 강하게 피해를 준다.");
		tripodChoice _11 = new tripodChoice("독:부식", "마지막 공격에 단검 공격에 '부식 독'을 부여한다.",
				"부식 독은 8.0초 동안 매 초마다 기본 피해량의 2% 피해를 준다.", "최대 3회까지 중첩된다.", "스킬 적중시 12초간 적에게 주는 피해가 12.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("독:출혈", "마지막 공격에 단검 공격에 '출혈 독'을 부여한다.",
				"출혈 독은 8.0초 동안 매 초마다 기본 피해량의 2% 피해를 준다.", "최대 3중첩이 가능하다", "3중첩 시 '과다 출혈'를 부여한다.",
				"과다 출혈은 8.0초 동안 매 초마다 기본 피해량의 55% 피해를 준다.");
		tripodChoice _13 = new tripodChoice("독:신경", "테스트용");
		tripodChoice _21 = new tripodChoice("빠른 준비", "재사용 대기시간이 3.0초 감소한다.");
		tripodChoice _22 = new tripodChoice("치명적인 단검", "치명타 적중률이 60.0% 증가한다.");
		tripodChoice _23 = new tripodChoice("넓은 공격", "테스트용");
		tripodChoice _31 = new tripodChoice("죽음의 일격", "테스트용");
		tripodChoice _32 = new tripodChoice("급습 활성", "스킬 사용 시 급습 스킬들의 재사용 대기시간이 1.9초 감소한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return 2;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Combo;
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod2Choice(le) == 0 ? 3 : 0;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
//		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);

		return (damages1[level - 1] + damages2[level - 1] * 2) * mul
				+ (tc1 == 0 || tc1 == 1 ? (damages1[level - 1] + damages2[level - 1] * 0.16) : 0);
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
					new tickRunnable(att, vic, (damages1[getLevel(att) - 1] + damages2[getLevel(att) - 1] * 2) * 0.55,
							times, tickType.blood).runTaskTimer(Core.getCore(), 10, 20));
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
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection().clone().setY(0).normalize();
		Location now = loc.clone().add(dir);
		if (ComboCoolRunnable.hasCombo(le, this)) {
			List<Entity> list1 = new ArrayList<Entity>();
			List<Entity> list2 = new ArrayList<Entity>();

			for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5)) {
				damage(list1, le, et, damages2[level - 1] * mul);
				if (damage(list2, le, et, damages2[level - 1] * mul))
					if (tc1 == 0) {
						runPoison(le, (LivingEntity) et, (damages1[level - 1] + damages2[level - 1] * 2) * 0.02, 8, 3);
						BuffRunnable.run(le, this, 12, 0);
						ds.setDamageIncrease(this.displayName, 0.12);
					} else if (tc1 == 1)
						runBlood(le, (LivingEntity) et, (damages1[level - 1] + damages2[level - 1] * 2) * 0.02, 8, 3);
			}
			EffectStore.Directional.SWEEP_ATTACK.spawnDirectional(now, 2, 0, 0, 0, 1);
			comboEnd(le);
		} else {
			List<Entity> list = new ArrayList<Entity>();
			EffectStore.Directional.SWEEP_ATTACK.spawnDirectional(now, 1, 0, 0, 0, 1);
			for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
				damage(list, le, et, damages1[level - 1] * mul);
			ComboCoolRunnable.run(le, this, getComboDuration(le));
			if (tc3 == 1)
				for (Skill skill : Skill.assassin.Reaper.getSurpiseAttacks())
					SkillCoolRunnable.run(le, skill, skill.getCooldownNow(le) - 1.9d);
		}
		return false;
	}
}
