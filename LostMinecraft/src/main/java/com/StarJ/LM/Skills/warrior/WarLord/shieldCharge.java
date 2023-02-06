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
import com.StarJ.LM.Skills.Runnable.HoldingRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.DataStore.ActionBarRunnable;
import com.StarJ.LM.System.EffectStore;

public class shieldCharge extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 44, 79, 101, 119, 133, 144, 154, 163, 170, 176, 193, 212 };

	public shieldCharge() {
		super("shieldCharge", ChatColor.WHITE + "방패 돌진", Skill.warrior.WarLord.skillGroup.Normal, 16, 72, 160,
				"방패로 전방을 방어하며 빠르게 돌진한다.", "돌진 공격 중에는 매 타격마다 피해를 준다.");
		tripodChoice _11 = new tripodChoice("단단한 갑옷", "테스트용");
		tripodChoice _12 = new tripodChoice("빠른 준비", "재사용 대기시간이 3.0초 감소한다.");
		tripodChoice _13 = new tripodChoice("강화된 일격", "적에게 주는 피해가 40.0% 증가한다.");
		tripodChoice _21 = new tripodChoice("넓은 타격", "테스트용");
		tripodChoice _22 = new tripodChoice("보호막", "돌진 중 최대 생명력의 25.0%에 해당하는 보호막을 생성한다.");
		tripodChoice _23 = new tripodChoice("가벼운 발걸음", "돌진의 이동속도가 20.0% 증가한다.");
		tripodChoice _31 = new tripodChoice("끝나지 않은 공격", "돌진 공격의 주기가 빨라져 2회 더 공격한다.", "돌진 후 기본 피해량의 78.0% 추가 피해를 준다.");
		tripodChoice _32 = new tripodChoice("파괴 전차", "돌진 공격의 주기가 빨라져 2회 더 공격한다.",
				"돌진 공격이 점점 강해져 30.0% 씩 최대 210.0% 까지 증가한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		int times = 8;
		if (getTripod3Choice(le) != -1)
			times += 2;
		return times;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Holding;
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod1Choice(le) == 1 ? 3 : 0;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc1 == 2)
			mul *= 1.4d;
		if (tc3 == 1)
			mul *= 2.26d;

		int times = 8;
		if (tc3 != -1)
			times += 2;

		return damages[level - 1] * mul * times + (tc3 == 0 ? damages[level - 1] * 8 * mul * 0.78 : 0);
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

	Rel[] sting_up = new Rel[] { //
			new Rel(0, -1, 0), //
			new Rel(0.1, -0.95, 0), //
			new Rel(0.2, -0.9, 0), //
			new Rel(0.3, -0.85, 0), //
			new Rel(0.4, -0.8, 0), //
			new Rel(0.5, -0.75, 0), //
			new Rel(0.6, -0.7, 0), //
			new Rel(0.7, -0.65, 0), //
			new Rel(0.8, -0.6, 0), //
			new Rel(0.9, -0.55, 0), //
			new Rel(1, -0.5, 0), //
			new Rel(1.1, -0.45, 0), //
			new Rel(1.2, -0.4, 0), //
			new Rel(1.3, -0.35, 0), //
			new Rel(1.4, -0.3, 0), //
			new Rel(1.5, -0.25, 0), //
			new Rel(1.6, -0.2, 0), //
			new Rel(1.7, -0.15, 0), //
			new Rel(1.8, -0.1, 0), //
			new Rel(1.9, -0.05, 0), //
			new Rel(2, 0, 0), //
	};

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		ds.setAbsorption(this.displayName, ds.getMaxHealth() * 0.25d);
		ds.confirmHealthPercent(le);
		ActionBarRunnable.run(le);
		if (getTripod3Choice(le) != -1)
			HoldingRunnable.run(le, this, 20, 2);
		else
			HoldingRunnable.run(le, this, 16, 3);
		return false;
	}

	@Override
	public void Holding(LivingEntity att, int times) {
		super.Holding(att, times);

		Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();
		int tc2 = getTripod2Choice(att);
		double speed = 0.5d;
		if (tc2 == 2)
			speed *= 1.2d;
		att.setVelocity(dir.clone().multiply(speed));
		List<Entity> list = new ArrayList<Entity>();
		if (times % 2 == 0) {
			int tc1 = getTripod1Choice(att);

			int tc3 = getTripod3Choice(att);
			int level = getLevel(att);

			DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
			double mul = ds.getMultiplyDamage(groupType);

			if (tc1 == 2)
				mul *= 1.4d;
			if (tc3 == 1)
				mul *= Math.min(3.1, 1 + 0.3 * (times / 2));
			final double mul_r = mul;

			EffectStore.Directional.SOUL_FIRE_FLAME.spawnDirectional(loc, 10, 0.1, 0.1, 0.1, 0.1);
			for (Entity et : loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5))
				damage(list, att, et, damages[level - 1] * mul_r);
		}
	}

	@Override
	public void HoldingFail(LivingEntity att, int times) {
		super.HoldingFail(att, times);
		att.setVelocity(new Vector());
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		ds.setAbsorption(this.displayName, 0);
		ds.confirmHealthPercent(att);
		ActionBarRunnable.run(att);

		int tc3 = getTripod3Choice(att);
		if (tc3 == 0) {
			int tc1 = getTripod1Choice(att);
//			int tc2 = getTripod2Choice(att);
			int level = getLevel(att);

			double mul = ds.getMultiplyDamage(groupType);

			if (tc1 == 2)
				mul *= 1.4d;

			final double mul_r = mul;

			Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
			Vector dir = loc.getDirection();
			dir.setY(0);
			dir.normalize();
			int i = 0;
			List<Entity> list = new ArrayList<Entity>();
			for (Rel rel : sting_up) {
				i++;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0, 0, 0);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
							damage(list, att, et, damages[level - 1] * 8 * mul_r * 0.78);
					}
				}.runTaskLater(Core.getCore(), i / 3);
			}
		}
	}

	@Override
	public void HoldingSucceed(LivingEntity att, int times) {
		super.HoldingSucceed(att, times);
		att.setVelocity(new Vector());
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		ds.setAbsorption(this.displayName, 0);
		ds.confirmHealthPercent(att);
		ActionBarRunnable.run(att);

		int tc3 = getTripod3Choice(att);
		if (tc3 == 0) {
			int tc1 = getTripod1Choice(att);
//			int tc2 = getTripod2Choice(att);
			int level = getLevel(att);

			double mul = ds.getMultiplyDamage(groupType);

			if (tc1 == 2)
				mul *= 1.4d;

			final double mul_r = mul;

			Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
			Vector dir = loc.getDirection();
			dir.setY(0);
			dir.normalize();
			int i = 0;
			List<Entity> list = new ArrayList<Entity>();
			for (Rel rel : sting_up) {
				i++;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0, 0, 0);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
							damage(list, att, et, damages[level - 1] * 8 * mul_r * 0.78);
					}
				}.runTaskLater(Core.getCore(), i / 3);
			}
		}
	}
}
