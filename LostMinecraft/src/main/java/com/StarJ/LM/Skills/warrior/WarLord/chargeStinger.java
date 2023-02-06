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
import com.StarJ.LM.Skills.Runnable.ChargingRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class chargeStinger extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[][] damages = new double[][] {
			{ 229, 406, 519, 609, 680, 737, 786, 831, 865, 898, 987, 1086 },
			{ 534, 948, 1211, 1421, 1589, 1720, 1835, 1940, 2019, 2098, 2307, 2538 },
			{ 764, 1354, 1730, 2030, 2269, 2457, 2619, 2769, 2882, 2994, 3293, 3622 },
			{ 1305, 2313, 2955, 3467, 3875, 4197, 4473, 4729, 4922, 5114, 5624, 6186 } };

	public chargeStinger() {
		super("chargeStinger", ChatColor.WHITE + "차지 스팅거", Skill.warrior.WarLord.skillGroup.Lance, 30, 75, 90,
				AttackType.Head, "기운을 모아 전방으로 미끄러져 창을 내지른다.");
		tripodChoice _11 = new tripodChoice("상처", "3.0초 동안 출혈 상태로 만든다.", "매 초마다 기본 오버차지 피해량의 15%의 추가 피해를 입힌다.");
		tripodChoice _12 = new tripodChoice("약점 포착", "테스트용");
		tripodChoice _13 = new tripodChoice("단단한 갑옷", "테스트용");
		tripodChoice _21 = new tripodChoice("부위파괴 강화", "테스트용");
		tripodChoice _22 = new tripodChoice("차지 강화", "차지 단계가 올라갈수록 공격력이 30.0% 상승한다.");
		tripodChoice _23 = new tripodChoice("강인함", "테스트용");
		tripodChoice _31 = new tripodChoice("라이트닝 차지", "차지 단계가 1단계부터 시작된다.");
		tripodChoice _32 = new tripodChoice("라스트 차지", "차지 단계가 1단계 더 증가된다.", "오버 차지 시 적에게 주는 피해가 70.8% 증가한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Charge;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		int max = 2;
		if (tc3 == 1) {
			max = 3;
			mul *= 1.9d;
		} else if (tc2 == 1)
			mul *= 1.6d;
		return damages[max][level - 1] * mul + (tc1 == 0 ? damages[2][level - 1] * 0.45 : 0);
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

	private Rel[] sting = new Rel[] { //
			new Rel(0.25, 0.0, 0.00), new Rel(0.5, 0.0, 0.00), new Rel(0.75, 0.0, 0.00), new Rel(1, 0.0, 0.00), //
			new Rel(1.25, 0.0, 0.00), new Rel(1.5, 0.0, 0.00), new Rel(1.75, 0.0, 0.00), new Rel(2, 0.0, 0.00), //
			new Rel(2.25, 0.0, 0.00), new Rel(2.5, 0.0, 0.00)
			//
	};

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc3 = getTripod3Choice(le);
		int charge = 0;
		int max_charge = 2;
		if (tc3 == 0)
			charge = 1;
		else if (tc3 == 1)
			max_charge = 3;

		ChargingRunnable.run(le, this, 6, max_charge, charge, 3);
		return false;
	}

	@Override
	public void Charging(LivingEntity att, int times, int charge) {
		super.Charging(att, times, charge);
		Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
		if (charge == 0)
			EffectStore.Directional.CLOUD.spawnDirectional(loc, 5, 0.5, 0.5, 0.5, 0.25);
		else if (charge == 1)
			EffectStore.Directional.GLOW_SQUID_INK.spawnDirectional(loc, 5, 0.5, 0.5, 0.5, 0.25);
		else if (charge == 2)
			EffectStore.Directional.GLOW.spawnDirectional(loc, 5, 0.5, 0.5, 0.5, 0.25);
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!super.damage(skill, destruction, list, att, vic_e, damage, now))
			return false;
		if (skill) {
			int level = getLevel(att);
			if (getTripod1Choice(att) == 0)
				runBlood(att, (LivingEntity) vic_e, damages[2][level - 1] * 0.15, 3);
		}
		return true;
	}

	@Override
	public void ChargingFinish(LivingEntity att, int charge) {
		super.ChargingFinish(att, charge);

//		int tc1 = getTripod1Choice(att);
		int tc2 = getTripod2Choice(att);
//		int tc3 = getTripod3Choice(att);
		int level = getLevel(att);

		Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();

		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		double mul = ds.getMultiplyDamage(groupType);

		if (tc2 == 1)
			mul *= 1 + 0.3d * charge;
		att.setVelocity(new Vector(0, 0.1, 0));
		new BukkitRunnable() {
			@Override
			public void run() {
				att.setVelocity(dir);
			}
		}.runTaskLater(Core.getCore(), 2);

		final double mul_r = mul;
		new BukkitRunnable() {
			@Override
			public void run() {
				Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
				Vector dir = loc.getDirection();
				dir.setY(0);
				dir.normalize();
				att.setVelocity(new Vector());
				List<Entity> list = new ArrayList<Entity>();
				int i = 0;
				for (Rel rel : sting) {
					i++;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.2));
							EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5))
								damage(list, att, et, damages[charge][level - 1] * mul_r);
						}
					}.runTaskLater(Core.getCore(), i / 3);
				}
			}
		}.runTaskLater(Core.getCore(), 6);
	}

	@Override
	public int getDestruction() {
		return 1;
	}
}
