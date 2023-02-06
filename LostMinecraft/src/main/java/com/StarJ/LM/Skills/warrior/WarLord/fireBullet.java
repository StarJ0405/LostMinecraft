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

public class fireBullet extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 143, 254, 324, 381, 425, 461, 491, 519, 540, 561, 617, 678 };

	public fireBullet() {
		super("fireBullet", ChatColor.WHITE + "파이어 불릿", Skill.warrior.WarLord.skillGroup.Lance, 7, 83, 60,
				AttackType.Head, "근거리 적에게 포격을 가하여 피해를 준다.");
		tripodChoice _11 = new tripodChoice("단단한 갑옷", "테스트용");
		tripodChoice _12 = new tripodChoice("행운의 기회", "테스트용");
		tripodChoice _13 = new tripodChoice("마력 조절", "테스트용");
		tripodChoice _21 = new tripodChoice("부위파괴 강화", "테스트용");
		tripodChoice _22 = new tripodChoice("소이탄", "적중된 적에게 6.0초 동안 화상 상태로 만든다.", "매 초마다 기본피해량의 15%의 추가 피해를 입힌다.",
				"화상은 최대 2회 중첩 가능하다.");
		tripodChoice _23 = new tripodChoice("강화된 일격", "적에게 주는 피해가 50.0% 증가된다.");
		tripodChoice _31 = new tripodChoice("공격찿울", "2회 연속 포격을 가해 총 95.2% 증가된 피해를 준다.");
		tripodChoice _32 = new tripodChoice("한방포격", "포격속도가 느려지며, 공격 범위가 30.0% 증가한다.", "적에게 주는 피해가 144.0% 증가한다.");
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
		return getTripod3Choice(le) == 0 ? 2 : 1;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 2)
			mul *= 1.5d;
		if (tc3 == 0)
			mul *= 1.952d;
		else if (tc3 == 1)
			mul *= 2.44d;
		return damages[level - 1] * mul + (tc2 == 1 ? damages[level - 1] * 0.15 * (tc3 == 0 ? 13 : 6) : 0);
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
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!super.damage(skill, destruction, list, att, vic_e, damage, now))
			return false;
		if (skill && getTripod2Choice(att) == 1)
			runFire(att, (LivingEntity) vic_e, damages[getLevel(att) - 1] * 0.15, 6, 2);
		return true;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
//		int tc1 = getTripod1Choice(player);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);

		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();

		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);

		if (tc2 == 2)
			mul *= 1.5d;
		List<Entity> list = new ArrayList<Entity>();
		if (tc3 == 0) {
			int times = 5;
			for (int i = 0; i < times; i++) {
				final int j = i;
				final double mul_r = mul;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						Location now = loc.clone().add(EffectStore.getRel(dir, 1, 0, 0));
						List<Entity> list = new ArrayList<Entity>();
						if (j >= times - 2) {
							EffectStore.spawnRedStone(now, 200, 0, 0, 1, 150, 0.5, 0.5, 0.5);
							EffectStore.spawnRedStone(now, 0, 0, 0, 1, 150, 0.5, 0.5, 0.5);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
								damage(list, le, et, damages[level - 1] * 0.976 * mul_r);
						} else
							EffectStore.spawnRedStone(now, 100, 100, 100, 1, (j + 1) * 30, j * 0.15, j * 0.15,
									j * 0.15);
					}
				}.runTaskLater(Core.getCore(), i * 10);
			}
		} else if (tc3 == 1) {
			int times = 6;
			mul *= 2.44d;
			for (int i = 0; i < times; i++) {
				final int j = i;
				final double mul_r = mul;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						Location now = loc.clone().add(EffectStore.getRel(dir, 1, 0, 0));
						if (j >= times - 1) {
							EffectStore.spawnRedStone(now, 200, 0, 0, 1, 150, 0.5, 0.5, 0.5);
							EffectStore.spawnRedStone(now, 0, 0, 0, 1, 150, 0.5, 0.5, 0.5);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
								damage(list, le, et, damages[level - 1] * mul_r);

						} else
							EffectStore.spawnRedStone(now, 100, 100, 100, 1, (j + 1) * 10, j * 0.1, j * 0.1, j * 0.1);
					}
				}.runTaskLater(Core.getCore(), i * 10);
			}
		} else {
			int times = 4;
			for (int i = 0; i < times; i++) {
				final int j = i;
				final double mul_r = mul;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						Location now = loc.clone().add(EffectStore.getRel(dir, 1, 0, 0));
						if (j >= times - 1) {
							EffectStore.spawnRedStone(now, 200, 0, 0, 1, 150, 0.5, 0.5, 0.5);
							EffectStore.spawnRedStone(now, 0, 0, 0, 1, 150, 0.5, 0.5, 0.5);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
								damage(list, le, et, damages[level - 1] * mul_r);
						} else
							EffectStore.spawnRedStone(now, 100, 100, 100, 1, (j + 1) * 30, j * 0.15, j * 0.15,
									j * 0.15);
					}
				}.runTaskLater(Core.getCore(), i * 10);
			}
		}

		return false;
	}

	// 1

}
