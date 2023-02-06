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
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.Skills.Runnable.ComboCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class dashUpperFire extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 62, 110, 140, 165, 183, 199, 212, 225, 233, 242, 266, 292 };
	protected final double[] damages2 = new double[] { 251, 445, 568, 667, 745, 807, 861, 910, 946, 983, 1081, 1189 };

	public dashUpperFire() {
		super("dashUpperFire", ChatColor.WHITE + "대쉬 어퍼 파이어", Skill.warrior.WarLord.skillGroup.Lance, 16, 109, 60,
				AttackType.Head, "창을 쳐올려 피해를 준다.", "추가 입력을 통해 포격 공격을 한다.");
		tripodChoice _11 = new tripodChoice("빠른 준비", "재사용 대기시간이 5.6초 감소한다.");
		tripodChoice _12 = new tripodChoice("탁원한 기동성", "테스트용");
		tripodChoice _13 = new tripodChoice("공격 준비", "스킬 사용시 자신의 공격력이 4.0초간 22.9% 증가한다.");
		tripodChoice _21 = new tripodChoice("강화된 일격", "적에게 주는 피해가 60.0% 증가한다.");
		tripodChoice _22 = new tripodChoice("약점 포착", "테스트용");
		tripodChoice _23 = new tripodChoice("넓은 타격", "테스트용");
		tripodChoice _31 = new tripodChoice("창격 강화", "창으로 올려친 후 포격을 하지 않고 창으로 즉시 내려친다.",
				"내려치는 공격은 적에게 포격의 120.0% 피해를 준다.");
		tripodChoice _32 = new tripodChoice("멈추지 않는 포성", "포격 공격 시 1회 더 포격한다.", "기존 포격 피해량의 159.5%에 해당하는 피해를 입힌다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 1 ? 3 : 2;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Combo;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 0)
			mul *= 1.6d;
		return (damages1[level - 1] + damages2[level - 1]) * mul + (tc3 == 0 ? damages2[level - 1] * 0.2 * mul : 0)
				+ (tc3 == 1 ? damages2[level - 1] * 0.595 * mul : 0);
	}

	@Override
	public double getCooldown(LivingEntity le) {
		if (le == null)
			return cooldown;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		int tc1 = getTripod1Choice(le);
		return (tc1 == 0 ? cooldown - 5.6 : cooldown) * ds.getReduceCooldown(le);
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		return tc1 == 0 ? 5.6 : 0;
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

	Rel[] sting_down = new Rel[] { //
			new Rel(0, 0, 0), //
			new Rel(0.1, -0.05, 0), //
			new Rel(0.2, -0.1, 0), //
			new Rel(0.3, -0.15, 0), //
			new Rel(0.4, -0.2, 0), //
			new Rel(0.5, -0.25, 0), //
			new Rel(0.6, -0.3, 0), //
			new Rel(0.7, -0.35, 0), //
			new Rel(0.8, -0.4, 0), //
			new Rel(0.9, -0.45, 0), //
			new Rel(1, -0.5, 0), //
			new Rel(1.1, -0.55, 0), //
			new Rel(1.2, -0.6, 0), //
			new Rel(1.3, -0.65, 0), //
			new Rel(1.4, -0.7, 0), //
			new Rel(1.5, -0.75, 0), //
			new Rel(1.6, -0.8, 0), //
			new Rel(1.7, -0.85, 0), //
			new Rel(1.8, -0.9, 0), //
			new Rel(1.9, -0.95, 0), //
			new Rel(2, -1, 0), //
	};

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		if (num == 0)
			Core.getCore().getHashMapStore().getDataStore(att).setPower(this.displayName, 0);
		super.BuffEnd(att, num);
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);

		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		if (tc1 == 2) {
			BuffRunnable.run(le, this, 4, 0);
			ds.setPower(this.displayName, 0.229);
		}

		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 0)
			mul *= 1.6d;

		List<Entity> list = new ArrayList<Entity>();
		if (!ComboCoolRunnable.hasCombo(le, this)) {
			int i = 0;
			final double mul_r = mul;
			for (Rel rel : sting_up) {
				i++;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
							damage(list, le, et, damages1[level - 1] * mul_r);
					}
				}.runTaskLater(Core.getCore(), i / 5);
			}
			if (tc3 != 0)
				new BukkitRunnable() {

					@Override
					public void run() {
						ComboCoolRunnable.run(le, dashUpperFire.this, getComboDuration(le));
					}
				}.runTaskLater(Core.getCore(), i / 5 + 3);
			else {
				List<Entity> list2 = new ArrayList<Entity>();
				i += 6;
				for (Rel rel : sting_down) {
					i++;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
								damage(list2, le, et, damages2[level - 1] * 1.2 * mul_r);
						}
					}.runTaskLater(Core.getCore(), i / 5);
				}
			}
		} else {
			if (tc3 == 1) {
				mul *= 0.7975d;
				final double mul_r = mul;
				new BukkitRunnable() {
					List<Entity> list = new ArrayList<Entity>();

					@Override
					public void run() {
						Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						Rel last = sting_up[sting_up.length - 1];
						Location air = loc.clone()
								.add(EffectStore.getRel(dir, last.getFront(), last.getHeight(), last.getRight()));
						Location now = air;
						EffectStore.spawnRedStone(now, 200, 0, 00, 1, 150, 0.15, 0.15, 0.15);
						EffectStore.spawnRedStone(now, 0, 0, 00, 1, 150, 0.15, 0.15, 0.15);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
							damage(list, le, et, damages2[level - 1] * mul_r);
					}
				}.runTaskLater(Core.getCore(), 10);
			}
			Rel last = sting_up[sting_up.length - 1];
			Location air = loc.clone().add(EffectStore.getRel(dir, last.getFront(), last.getHeight(), last.getRight()));
			Location now = air;
			EffectStore.spawnRedStone(now, 200, 0, 00, 1, 150, 0.15, 0.15, 0.15);
			EffectStore.spawnRedStone(now, 0, 0, 00, 1, 150, 0.15, 0.15, 0.15);
			for (Entity et : loc.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
				damage(list, le, et, damages2[level - 1] * mul);
			comboEnd(le);
		}
		return false;
	}

	@Override
	public int getDestruction() {
		return 1;

	}

}
