package com.StarJ.LM.Skills.warrior.WarLord;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.ChargingRunnable;
import com.StarJ.LM.Skills.Runnable.HoldingPerfectRunnable;
import com.StarJ.LM.Skills.Runnable.SkillCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class burstCannon extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 392, 695, 888, 1043, 1166, 1262, 1346, 1423, 1480, 1538, 1691,
			1860 };
	protected final double[] damages2 = new double[] { 783, 1388, 1774, 2082, 2327, 2519, 2686, 2841, 2957, 3072, 3379,
			3717 };

	public burstCannon() {
		super("burstCannon", ChatColor.WHITE + "버스트 캐넌", Skill.warrior.WarLord.skillGroup.Lance, 30, 170, 45,
				AttackType.Head, "화력을 응축시켜 전방을 향해 쏟아낸다.", "충분히 화력을 응축하면 피해를 준다.", "충분히 응축하지 못하면 적은 피해를 준다.");
		tripodChoice _11 = new tripodChoice("관통탄", "테스트용");
		tripodChoice _12 = new tripodChoice("확산탄", "테스트용");
		tripodChoice _13 = new tripodChoice("고폭탄", "테스트용");
		tripodChoice _21 = new tripodChoice("정밀한 포격", "퍼펙트 존이 범위가 작아진다.", "성공 시 피해량이 75.0% 증가한다.");
		tripodChoice _22 = new tripodChoice("확정된 공격", "일반 조작으로 변경된다.", "퍼펙트 존 효과에 도달하는 시간이 짧아진다.");
		tripodChoice _23 = new tripodChoice("과충전", "차지 조작으로 변경된다.", "1단계 차지 시 퍼펙트 존 효과가 발생된다.",
				"오버 차지 시 100.0% 증가된 피해를 입힌다.");
		tripodChoice _31 = new tripodChoice("회전포격", "360도 회전하며, 4회 포격을 한다.", "적을 타격할 때마다 재사용 대기 시간이 2.0초씩 감소한다.");
		tripodChoice _32 = new tripodChoice("집중포화", "전방을 향해 4회 연속 집중포격을 가한다.", "총 89.1% 증가된 피해를 입힌다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? 4 : 1;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		int tc2 = getTripod2Choice(le);
		return tc2 == 1 ? SkillType.Normal : (tc2 == 2 ? SkillType.Charge : SkillType.Holding);
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
			mul *= 1.75d;
		else if (tc2 == 2)
			mul *= 2.0d;
		if (tc3 == 1)
			mul *= 1.891d;
		return damages2[level - 1] * mul;
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
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc2 = getTripod2Choice(le);
		int times = 12;
		int tick = 3;
		int start = 7;
		int end = 9;
		if (tc2 == 1) {
			new BukkitRunnable() {
				@Override
				public void run() {
					HoldingPerfectSucceed(le);
				}
			}.runTaskLater(Core.getCore(), (start - 1) * tick);
		} else if (tc2 == 2) {
			ChargingRunnable.run(le, this, times * 3 / 4, 2, tick);
		} else {
			if (tc2 == 0) {
				start = 7;
				end = 8;
			}
			HoldingPerfectRunnable.run(le, this, times, start, end, tick);
		}
		return false;
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!super.damage(skill, destruction, list, att, vic_e, damage, now))
			return false;
		if (getTripod3Choice(att) == 0 && att instanceof Player)
			SkillCoolRunnable.run((Player) att, burstCannon.this, getCooldownNow(att) - 2d);
		return true;
	}

	@Override
	public void ChargingFinish(LivingEntity att, int charge) {
		super.ChargingFinish(att, charge);
		if (charge == 0) {
			HoldingPerfectFail(att);
		} else if (charge == 1) {
			HoldingPerfectSucceed(att);
		} else {
			int tc3 = getTripod3Choice(att);
			int level = getLevel(att);

			Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
			Vector dir = loc.getDirection();
			dir.setY(0);
			dir.normalize();
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
			double mul = ds.getMultiplyDamage(groupType);
			mul *= 2d;

			List<Entity> list = new ArrayList<Entity>();
			if (tc3 == 0) {
				for (int ii = 0; ii < 4; ii++) {
					final int jj = ii;
					final double mul_r = mul;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
							Vector dir = loc.getDirection();
							dir.setY(0);
							dir.normalize();
							if (jj == 1)
								dir = new Vector(-dir.getZ(), dir.getY(), dir.getX());
							else if (jj == 2)
								dir = dir.clone().multiply(-1);
							else if (jj == 3)
								dir = new Vector(dir.getZ(), dir.getY(), -dir.getX());

							for (int i = 1; i <= 3; i++) {
								Location now = loc.clone().add(dir.clone().multiply(i));
								EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
								EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
								EffectStore.spawnRedStone(now, 200, 125, 0, 1, 125, 0.25, 0.25, 0.25);
								for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
									damage(list, att, et, damages2[level - 1] * mul_r);
							}
						}

					}.runTaskLater(Core.getCore(), ii * 5);
				}
			} else if (tc3 == 1) {
				for (int ii = 0; ii < 4; ii++) {
					final double mul_r = mul * 1.891d / 4d;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
							Vector dir = loc.getDirection();
							dir.setY(0);
							dir.normalize();
							List<Entity> list = new ArrayList<Entity>();
							for (int i = 1; i <= 3; i++) {
								Location now = loc.clone().add(dir.clone().multiply(i));
								EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
								EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
								EffectStore.spawnRedStone(now, 200, 125, 0, 1, 125, 0.25, 0.25, 0.25);
								for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
									damage(list, att, et, damages2[level - 1] * mul_r);
							}
						}

					}.runTaskLater(Core.getCore(), ii * 5);
				}
			} else
				for (int i = 1; i <= 3; i++) {
					Location now = loc.clone().add(dir.clone().multiply(i));
					EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
					EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
					EffectStore.spawnRedStone(now, 200, 125, 0, 1, 125, 0.25, 0.25, 0.25);
					for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
						damage(list, att, et, damages2[level - 1] * mul);
				}
		}
	}

	@Override
	public void HoldingPerfectFail(LivingEntity att) {
		super.HoldingPerfectFail(att);
		int tc3 = getTripod3Choice(att);

		int level = getLevel(att);

		Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		double mul = ds.getMultiplyDamage(groupType);
		double range = 1d;

		List<Entity> list = new ArrayList<Entity>();
		if (tc3 == 0) {
			for (int ii = 0; ii < 4; ii++) {
				final int jj = ii;
				final double mul_r = mul;
				final double range_r = range;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						if (jj == 1)
							dir = new Vector(-dir.getZ(), dir.getY(), dir.getX());
						else if (jj == 2)
							dir = dir.clone().multiply(-1);
						else if (jj == 3)
							dir = new Vector(dir.getZ(), dir.getY(), -dir.getX());

						for (int i = 1; i <= 3; i++) {
							Location now = loc.clone().add(dir.clone().multiply(i));
							EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
							EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
							EffectStore.spawnRedStone(now, 200, 125, 0, 1, 125, 0.25, 0.25, 0.25);
							for (Entity et : loc.getWorld().getNearbyEntities(now, range_r, range_r, range_r))
								damage(list, att, et, damages1[level - 1] * mul_r);

						}
					}

				}.runTaskLater(Core.getCore(), ii * 5);
			}
		} else if (tc3 == 1) {
			for (int ii = 0; ii < 4; ii++) {
				final double mul_r = mul * 1.891d / 4d;
				final double range_r = range;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						List<Entity> list = new ArrayList<Entity>();
						for (int i = 1; i <= 3; i++) {
							Location now = loc.clone().add(dir.clone().multiply(i));
							EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
							EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
							EffectStore.spawnRedStone(now, 200, 125, 0, 1, 125, 0.25, 0.25, 0.25);
							for (Entity et : loc.getWorld().getNearbyEntities(now, range_r, range_r, range_r))
								damage(list, att, et, damages1[level - 1] * mul_r);
						}
					}

				}.runTaskLater(Core.getCore(), ii * 5);
			}
		} else
			for (int i = 1; i <= 3; i++) {
				Location now = loc.clone().add(dir.clone().multiply(i));
				EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
				EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
				EffectStore.spawnRedStone(now, 200, 125, 0, 1, 125, 0.25, 0.25, 0.25);
				for (Entity et : loc.getWorld().getNearbyEntities(now, range, range, range))
					damage(list, att, et, damages1[level - 1] * mul);
			}
	}

	@Override
	public void HoldingPerfectSucceed(LivingEntity att) {
		super.HoldingPerfectSucceed(att);
		int tc2 = getTripod2Choice(att);
		int tc3 = getTripod3Choice(att);

		int level = getLevel(att);

		Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 0)
			mul *= 1.75d;
		List<Entity> list = new ArrayList<Entity>();
		if (tc3 == 0) {
			for (int ii = 0; ii < 4; ii++) {
				final int jj = ii;
				final double mul_r = mul;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						if (jj == 1)
							dir = new Vector(-dir.getZ(), dir.getY(), dir.getX());
						else if (jj == 2)
							dir = dir.clone().multiply(-1);
						else if (jj == 3)
							dir = new Vector(dir.getZ(), dir.getY(), -dir.getX());

						for (int i = 1; i <= 3; i++) {
							Location now = loc.clone().add(dir.clone().multiply(i));
							EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
							EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
							EffectStore.spawnRedStone(now, 200, 125, 0, 1, 125, 0.25, 0.25, 0.25);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
								damage(list, att, et, damages2[level - 1] * mul_r);
						}
					}

				}.runTaskLater(Core.getCore(), ii * 5);
			}
		} else if (tc3 == 1) {
			for (int ii = 0; ii < 4; ii++) {
				final double mul_r = mul * 1.891d / 4d;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						List<Entity> list = new ArrayList<Entity>();
						for (int i = 1; i <= 3; i++) {
							Location now = loc.clone().add(dir.clone().multiply(i));
							EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
							EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
							EffectStore.spawnRedStone(now, 200, 125, 0, 1, 125, 0.25, 0.25, 0.25);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
								damage(list, att, et, damages2[level - 1] * mul_r);
						}
					}

				}.runTaskLater(Core.getCore(), ii * 5);
			}
		} else
			for (int i = 1; i <= 3; i++) {
				Location now = loc.clone().add(dir.clone().multiply(i));
				EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
				EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
				EffectStore.spawnRedStone(now, 200, 125, 0, 1, 125, 0.25, 0.25, 0.25);
				for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
					damage(list, att, et, damages2[level - 1] * mul);
			}
	}
}
