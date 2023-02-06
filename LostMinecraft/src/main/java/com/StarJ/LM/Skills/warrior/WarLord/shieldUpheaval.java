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
import com.StarJ.LM.Skills.Runnable.SkillCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class shieldUpheaval extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 139, 246, 314, 368, 412, 446, 476, 504, 524, 544, 598, 658 };

	public shieldUpheaval() {
		super("shieldUpheaval", ChatColor.WHITE + "방패 격동", Skill.warrior.WarLord.skillGroup.Normal, 12, 116, 180,
				AttackType.Head, "방패로 지면을 내리찍어 주변 적에게 피해를 준다.");
		tripodChoice _11 = new tripodChoice("빠른 준비", "재사용 대기시간이 4.2초 감소한다.");
		tripodChoice _12 = new tripodChoice("날렵한 움직임", "테스트용");
		tripodChoice _13 = new tripodChoice("갑옷 파괴", "스킬 적중시 10.0초간 적에게 주는 피해량이 12.0% 증가됩니다.");
		tripodChoice _21 = new tripodChoice("넓은 타격", "테스트용");
		tripodChoice _22 = new tripodChoice("지진", "테스트용");
		tripodChoice _23 = new tripodChoice("방어 준비", "스킬 사용시 받는 모든 피해가 10.0초간 24.6% 감소한다.");
		tripodChoice _31 = new tripodChoice("두 번째 격동", "체인 조작으로 변경되며 추가 입력 시 한 번 더 공격하여 145.0%의 피해를 준다.");
		tripodChoice _32 = new tripodChoice("강습", "5.0m 이내에 지정한 지점까지 뛰어올라 공격하며 80.0% 증가된 피해를 준다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? 2 : 1;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? SkillType.Chain : SkillType.Normal;
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod1Choice(le) == 0 ? 4.2 : 0;
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
			mul *= 2.45d;
		else if (tc3 == 1)
			mul *= 1.8d;
		return damages[level - 1] * mul;
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
		super.BuffEnd(att, num);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (num == 0)
			ds.setReduceDamage(this.displayName, 0);
		else if (num == 1)
			ds.setDamageIncrease(this.displayName, 0);
	}

	Rel[] downShield = new Rel[] { //
			new Rel(1.25, 0, -0.5), new Rel(1.25, 0, 0.5), //
			//
			new Rel(0.75, 0, 0), new Rel(0.75, 0, -0.5), new Rel(0.75, 0, -1), //
			new Rel(0.75, 0, 0.5), new Rel(0.75, 0, 1), //
			//
			new Rel(0.25, 0, 0), new Rel(0.25, 0, -0.5), new Rel(0.25, 0, -1), //
			new Rel(0.25, 0, 0.5), new Rel(0.25, 0, 1), //
			//
			new Rel(-0.25, 0, 0), new Rel(-0.25, 0, -0.5), new Rel(-0.25, 0, -1), //
			new Rel(-0.25, 0, 0.5), new Rel(-0.25, 0, 1), //
			//
			new Rel(-0.75, 0, 0), new Rel(-0.75, 0, -0.5), new Rel(-0.75, 0, 0.5), //
			new Rel(-1.25, 0, 0), //
	};

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!super.damage(skill, destruction, list, att, vic_e, damage, now))
			return false;
		if (getTripod1Choice(att) == 2) {
			BuffRunnable.run(att, this, 10, 1);// 방깍
			Core.getCore().getHashMapStore().getDataStore(att).setDamageIncrease(this.displayName, 0.12d);
		}
		return true;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);

		Location loc = le.getLocation();
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		if (tc2 == 2) {
			ds.setReduceDamage(this.displayName, 0.246);
			BuffRunnable.run(le, this, 10, 0);// 피감
		}
		double mul = ds.getMultiplyDamage(groupType);

		if (tc3 == 0)
			if (ComboCoolRunnable.hasCombo(le, this)) {
				mul *= 1.45d;
				comboEnd(le);
				SkillCoolRunnable.run(le, this);
			} else
				ComboCoolRunnable.run(le, this, getComboDuration(le));
		List<Entity> list = new ArrayList<Entity>();
		if (tc3 == 1) {
			mul *= 1.8d;
			final double mul_r = mul;

			Location point = getPointBlock(le.getEyeLocation().clone().subtract(0, 0.25, 0), 5);
			le.setVelocity(new Vector(0, 0.2, 0));
			new BukkitRunnable() {

				@Override
				public void run() {
					le.setVelocity(dir.clone().setY(1));
				}
			}.runTaskLater(Core.getCore(), 2);
			new BukkitRunnable() {

				@Override
				public void run() {
					le.setVelocity(point.subtract(le.getLocation()).toVector());
				}
			}.runTaskLater(Core.getCore(), 4);
			new BukkitRunnable() {
				int time = 0;

				@Override
				public void run() {
					if (le.isOnGround()) {
						le.setVelocity(new Vector());
						Location loc = le.getLocation();
						for (Rel rel : downShield) {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.1, 0.1, 0.1);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
								damage(list, le, et, damages[level - 1] * mul_r);
						}
						this.cancel();
					} else {
						this.time++;
						if (time > 200)
							this.cancel();
					}

				}
			}.runTaskTimer(Core.getCore(), 6, 1);

		} else {
			final double mul_r = mul;

			for (Rel rel : downShield) {
				Location now = loc.clone()
						.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
				EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.1, 0.1, 0.1);
				for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
					damage(list, le, et, damages[level - 1] * mul_r);
			}
		}
		return false;
	}
}
