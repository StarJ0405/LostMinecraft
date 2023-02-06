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
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class distortion extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 185, 327, 421, 492, 550, 600, 640, 676, 702, 728 };
	protected final double[] damages2 = new double[] { 183, 325, 417, 488, 545, 593, 633, 666, 694, 719 };

	public distortion() {
		super("distortion", ChatColor.WHITE + "디스토션", Skill.assassin.Reaper.skillGroup.Shadow, 12, 22, 333,
				"빠르게 전방으로 이동하며 피해를 준다.", "도착 시 이동거리에 있던 대상에게 피해를 추가로 준다.");
		tripodChoice _11 = new tripodChoice("그림자 충전", "어둠 게이지 및 혼돈 게이지 획득량이 45.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("치명적인 그림자", "치명타 적중률이 40.0%증가한다.");
		tripodChoice _13 = new tripodChoice("빠른 준비", "재사용 대기시간이 4.0초 감소한다.");
		tripodChoice _21 = new tripodChoice("회피의 달인", "테스트용");
		tripodChoice _22 = new tripodChoice("순풍", "스킬 시전 후 4.0초간 이동속도가 30.0% 증가한다.");
		tripodChoice _23 = new tripodChoice("약육강식", "테스트용");
		tripodChoice _31 = new tripodChoice("그림자 질주", "적에게 주는 피해가 80.0% 증가한다.");
		tripodChoice _32 = new tripodChoice("유령 질주", "테스트용");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return 6; // 5+1
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
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

	@Override
	public void applyIdentity(LivingEntity att, double addIdentity) {
		if (getTripod1Choice(att) == 0)
			setExtraMultiplyIdentity(att, 0.45d);
		super.applyIdentity(att, addIdentity);
		setExtraMultiplyIdentity(att, 0);
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod1Choice(le) == 2 ? 4 : 0;
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (getTripod1Choice(att) == 1)
			ds.setExtraCriticalChance(this.displayName, 0.4);
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		ds.setExtraCriticalChance(this.displayName, 0);
		return flag;
	}

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		if (num == 0)
			Core.getCore().getHashMapStore().getDataStore(att).setExtraWalkspeed(this.displayName, 0);
		super.BuffEnd(att, num);
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType) * (tc3 == 0 ? 1.8 : 1);
		Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
		Location point = getPointBlock(loc, 10);
		Vector dir = point.clone().subtract(loc).toVector();
		double length = dir.length();
		dir.normalize();
		List<Entity> list = new ArrayList<Entity>();
		if (tc2 == 1) {
			BuffRunnable.run(le, this, 4, 0);
			ds.setExtraWalkspeed(this.displayName, 0.3);
		}
		for (double l = 0; l < length; l += 0.5) {
			Location now = loc.clone().add(dir.clone().multiply(l));
			EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
			for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
				if (damage(list, le, et, damages1[level - 1] / 5 * mul)) {
					for (int c = 1; c < 6; c++) {
						if (c == 5) {
							new BukkitRunnable() {
								@Override
								public void run() {
									damage(new ArrayList<Entity>(), le, et, damages2[level - 1] * mul);
								}
							}.runTaskLater(Core.getCore(), c + 3);
						} else
							new BukkitRunnable() {
								@Override
								public void run() {
									damage(new ArrayList<Entity>(), le, et, damages1[level - 1] / 5 * mul);
								}
							}.runTaskLater(Core.getCore(), c);
					}
				}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				le.teleport(point);
			}
		}.runTaskLater(Core.getCore(), 2);
		return false;
	}
}
