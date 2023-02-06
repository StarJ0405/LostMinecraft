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

public class shieldShove extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 36, 64, 82, 96, 107, 116, 124, 131, 136, 142, 156, 171 };
	protected final double[] damages2 = new double[] { 86, 153, 195, 228, 255, 276, 294, 311, 323, 335, 368, 405 };

	public shieldShove() {
		super("shieldShove", ChatColor.WHITE + "방패 밀치기", Skill.warrior.WarLord.skillGroup.Normal, 9, 121, 60,
				AttackType.Head, "돌진하여 도착지에 방패를 휘둘러 피해를 준다.", "추가 입력을 통해 한번 더 크게 휘두르며 피해를 준다.");
		tripodChoice _11 = new tripodChoice("갑옷 파괴", "스킬 적중시 8.0초간 적에게 주는 피해량이 12.0% 증가됩니다.");
		tripodChoice _12 = new tripodChoice("방어 준비", "2타 적중 시 받는 모든 피해가 5.0초간 30.0% 감소한다.");
		tripodChoice _13 = new tripodChoice("날렵한 움직임", "테스트용");
		tripodChoice _21 = new tripodChoice("방패 강화", "공격 적중 시 실드 게이지 충전량이 75.0% 증가된다.");
		tripodChoice _22 = new tripodChoice("탁월한 기동성", "테스트용");
		tripodChoice _23 = new tripodChoice("약점 포착", "테스트용");
		tripodChoice _31 = new tripodChoice("추가 타격", "각 콤보마다 적에게 46.4%, 95.2%의 추가피해를 가한다.");
		tripodChoice _32 = new tripodChoice("눈 먼 충격", "추가 입력을 통해 한번 더 공격할 수 있다.", "콤보 횟수가 1회 증가한다.",
				"적에게 주는 피해가 총 60.0% 증가한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		int times = 2;
		int tc3 = getTripod3Choice(le);
		if (tc3 == 0)
			times += 2;
		else if (tc3 == 1)
			times += 1;
		return times;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Combo;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc3 == 1)
			mul *= 1.6;
		double mul1 = 1d;
		double mul2 = 1d;
		if (tc3 == 0) {
			mul1 *= 1.464d;
			mul2 *= 1.952d;
		}
		return (damages1[level - 1] * mul1 + damages2[level - 1] * mul2) * mul;
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

	private Rel[] shield = new Rel[] { //
			new Rel(1, 1, -0.5), new Rel(1, 1, 0.5), //
			new Rel(1, 0.5, 0), new Rel(1, 0.5, -0.5), new Rel(1, 0.5, 0.5), //
			new Rel(1, 0, 0), new Rel(1, 0, -0.5), new Rel(1, 0, 0.5), //
			new Rel(1, -0.5, 0), new Rel(1, -0.5, -0.5), new Rel(1, -0.5, 0.5), //
			new Rel(1, -1, 0),//
			//
	};

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (num == 0)
			ds.setReduceDamage(this.displayName, 0);
		else if (num == 1)
			ds.setDamageIncrease(this.displayName, 0);
		super.BuffEnd(att, num);
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!super.damage(skill, destruction, list, att, vic_e, damage, now))
			return false;
		if (getTripod1Choice(att) == 0) {
			BuffRunnable.run(att, this, 8, 1);// 피증
			Core.getCore().getHashMapStore().getDataStore(att).setDamageIncrease(this.displayName, 0.12d);
		}
		return true;
	}

	@Override
	public void applyIdentity(LivingEntity att, double addIdentity) {
		if (getTripod2Choice(att) == 0)
			setExtraMultiplyIdentity(att, 0.75);
		super.applyIdentity(att, addIdentity);
		setExtraMultiplyIdentity(att, 0);
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(player);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		if (tc1 == 1) {
			BuffRunnable.run(le, this, 5, 0);// 피감
			ds.setReduceDamage(this.displayName, 0.3);
		}
		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();

		double mul = ds.getMultiplyDamage(groupType);

		int combo = ComboCoolRunnable.getCombo(le, this);
		double damage = combo == 0 ? damages1[level - 1]
				: (combo == 1 ? damages2[level - 1] : (damages1[level - 1] + damages2[level - 1]) * 0.6);

		final double mul_r = mul;
		if (combo == 0) {
			le.setVelocity(new Vector(0, 0.2, 0));
			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(dir.clone().multiply(1.2));
				}
			}.runTaskLater(Core.getCore(), 2);

			new BukkitRunnable() {
				@Override
				public void run() {
					List<Entity> list = new ArrayList<Entity>();
					le.setVelocity(new Vector());
					Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
					Vector dir = loc.getDirection();
					dir.setY(0);
					dir.normalize();
					for (Rel rel : shield) {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.1, 0.1, 0.1);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
							damage(list, le, et, damage * mul_r);
					}
				}
			}.runTaskLater(Core.getCore(), 6);
			if (tc3 == 0)
				new BukkitRunnable() {
					@Override
					public void run() {
						List<Entity> list = new ArrayList<Entity>();
						Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						for (Rel rel : shield) {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 50, 50, 50, 1, 10, 0.1, 0.1, 0.1);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
								damage(list, le, et, damage * 0.464d * mul_r);
						}
					}
				}.runTaskLater(Core.getCore(), 8);
			ComboCoolRunnable.run(le, this, getComboDuration(le));
		} else {
			List<Entity> list = new ArrayList<Entity>();
			for (Rel rel : shield) {
				Location now = loc.clone()
						.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
				EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.1, 0.1, 0.1);
				for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
					damage(list, le, et, damage * mul_r);
			}
			if (tc3 == 0)
				new BukkitRunnable() {
					@Override
					public void run() {
						List<Entity> list = new ArrayList<Entity>();
						Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						for (Rel rel : shield) {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 50, 50, 50, 1, 10, 0.1, 0.1, 0.1);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
								damage(list, le, et, damage * 0.952d * mul_r);
						}
					}
				}.runTaskLater(Core.getCore(), 2);
			if (combo == 1 && tc3 == 1)
				ComboCoolRunnable.setCombo(le, this, combo + 1);
			else
				comboEnd(le);
		}
		return false;
	}

	@Override
	public int getDestruction() {
		return 1;
	}
}
