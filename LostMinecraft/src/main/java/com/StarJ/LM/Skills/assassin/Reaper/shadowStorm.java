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
import com.StarJ.LM.Skills.Runnable.HoldingRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class shadowStorm extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 156, 277, 355, 415, 463, 503, 537, 565, 590, 612 };
	protected final double[] damages2 = new double[] { 315, 561, 716, 841, 941, 1016, 1086, 1142, 1187, 1227 };
	protected final double[][] damages3 = new double[][] { { 313, 556, 711, 833, 930, 1011, 1078, 1137, 1185, 1229 },
			{ 235, 418, 534, 626, 698, 759, 810, 854, 890, 924 } };

	public shadowStorm() {
		super("shadowStorm", ChatColor.WHITE + "쉐도우 스톰", Skill.assassin.Reaper.skillGroup.Shadow, 22, 41, 1200,
				AttackType.Back, "전방으로 이동하며 빠르게 찔러 피해를 준다.", "그림자를 소환하여 총 10회에 걸쳐 난무를 펼친다.", "끝까지 난무를 사용시 전진하며 피해를 준다.",
				"난무 도중에 취소 시 적은 피해를 준다.");
		tripodChoice _11 = new tripodChoice("그림자 충전", "공격 적중 시 어둠 게이지 및 혼돈 게이지 획득량이 45.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("치명적인 그림자", "치명타 적중률이 40.0% 증가한다.");
		tripodChoice _13 = new tripodChoice("꿰뚫는 일격", "적에게 주는 피해가 62.0% 증가한다.");
		tripodChoice _21 = new tripodChoice("난무 강화", "치명타 피해량이 120.0% 증가한다.");
		tripodChoice _22 = new tripodChoice("빠른 준비", "테스트용");
		tripodChoice _23 = new tripodChoice("마침표", "마지막 공격의 피해가 150.0% 증가한다.");
		tripodChoice _31 = new tripodChoice("검은 폭풍", "적에게 주는 피해가 95.0% 증가한다.", "그림자가 사방에서 난무 공격을 펼쳐 공격 범위가 증가한다.");
		tripodChoice _32 = new tripodChoice("그림자 지대", "일반 조작으로 변경된다.", "적에게 주는 피해가 70.0% 증가한다.",
				"전방으로 이동하며 찌르는 공격으로 변경된다.", "지나간 자리에 원형의 그림자 공간을 생성한다.", "그림자가 5회 공격하며 마지막에 폭발한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 1 ? 7 : 12;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return getTripod3Choice(le) == 1 ? SkillType.Normal : SkillType.Holding;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc1 == 2)
			mul *= 1.62d;
		if (tc3 == 0)
			mul *= 1.95d;
		else if (tc3 == 1)
			mul *= 1.7d;
		return (damages1[level - 1] + damages2[level - 1] + damages3[0][level - 1] * (tc2 == 2 ? 2.5 : 1)) * mul;
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
			new Rel(0.25, 0, 0), new Rel(0.5, 0, 0), new Rel(0.75, 0, 0), new Rel(1, 0, 0), //
			new Rel(1.25, 0, 0), new Rel(1.5, 0, 0), new Rel(1.75, 0, 0), new Rel(2, 0, 0), //
			new Rel(2.25, 0, 0), new Rel(2.5, 0, 0),//
	};
	private Rel[] left = new Rel[] { //
			new Rel(0.5, 0, -0.5), new Rel(0.75, 0, -0.5), //
			new Rel(1, 0, -0.25), new Rel(1.25, 0, -0.25), //
			new Rel(1.5, 0, 0), new Rel(1.75, 0, 0.25), new Rel(2, 0, 0.5),//
	};
	private Rel[] right = new Rel[] { //
			new Rel(0.5, 0, 0.5), new Rel(0.75, 0, 0.5), //
			new Rel(1, 0, 0.25), new Rel(1.25, 0, 0.25), //
			new Rel(1.5, 0, 0), new Rel(1.75, 0, -0.25), new Rel(2, 0, -0.5),//
	};
	private Rel[] star = new Rel[] { //
			new Rel(1, 0, 0), //
			new Rel(-0.809, 0, -0.588), //
			new Rel(0.309, 0, 0.951), //
			new Rel(0.309, 0, -0.951), //
			new Rel(-0.809, 0, 0.588),//
	};

	@Override
	public void applyIdentity(LivingEntity att, double addIdentity) {
		if (getTripod1Choice(att) == 0)
			setExtraMultiplyIdentity(att, 0.45d);
		super.applyIdentity(att, addIdentity);
		setExtraMultiplyIdentity(att, 0);
	}

	@Override
	public void Holding(LivingEntity att, int times) {
		int tc1 = getTripod1Choice(att);
//		int tc2 = getTripod2Choice(att);
		int tc3 = getTripod3Choice(att);
		int level = getLevel(att);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		double mul = ds.getMultiplyDamage(groupType) * (tc1 == 2 ? 1.62d : 1)
				* (tc3 == 0 ? 1.95d : (tc3 == 1 ? 1.7d : 1));
		Location loc = att.getEyeLocation().clone().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection().clone().setY(0).normalize();
		List<Entity> list = new ArrayList<Entity>();
		Rel[] rels = times % 2 == 1 ? right : left;
		if (tc3 == 0) {
			Location loc2 = loc.clone().add(EffectStore.getRel(dir, 3, 0, 3));
			Location loc3 = loc.clone().add(EffectStore.getRel(dir, 3, 0, -3));
			Location middle = new Location(loc.getWorld(), (loc.getX() + loc2.getX() + loc3.getX()) / 3,
					(loc.getY() + loc2.getY() + loc3.getY()) / 3, (loc.getZ() + loc2.getZ() + loc3.getZ()) / 3);
			Vector dir2 = middle.clone().subtract(loc2).toVector().setY(0).normalize();
			Vector dir3 = middle.clone().subtract(loc3).toVector().setY(0).normalize();
			for (Rel rel : rels) {
				Location now = loc.clone()
						.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
				Location now2 = loc2.clone()
						.add(EffectStore.getRel(dir2, rel.getFront(), rel.getHeight(), rel.getRight()));
				Location now3 = loc3.clone()
						.add(EffectStore.getRel(dir3, rel.getFront(), rel.getHeight(), rel.getRight()));
				EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
				EffectStore.spawnRedStone(now2, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
				EffectStore.spawnRedStone(now3, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
				for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
					damage(list, att, et, damages2[level - 1] / 10 * mul);
				for (Entity et : now.getWorld().getNearbyEntities(now2, 1, 1, 1))
					damage(list, att, et, damages2[level - 1] / 10 * mul);
				for (Entity et : now.getWorld().getNearbyEntities(now3, 1, 1, 1))
					damage(list, att, et, damages2[level - 1] / 10 * mul);
			}
		} else
			for (Rel rel : rels) {
				Location now = loc.clone()
						.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
				EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
				for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
					damage(list, att, et, damages2[level - 1] / 10 * mul);
			}
		super.Holding(att, times);
	}

	@Override
	public void HoldingSucceed(LivingEntity att, int times) {
		int tc1 = getTripod1Choice(att);
		int tc2 = getTripod2Choice(att);
		int tc3 = getTripod3Choice(att);
		int level = getLevel(att);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		double mul = ds.getMultiplyDamage(groupType) * (tc1 == 2 ? 1.62d : 1) * (tc2 == 2 ? 2.5 : 1)
				* (tc3 == 0 ? 1.95d : (tc3 == 1 ? 1.7d : 1));
		Location loc = att.getEyeLocation().clone().subtract(0, 0.25, 0);
		Location point = getPointBlock(loc, 6);
		Vector dir = point.clone().subtract(loc).toVector();
		double length = dir.length();
		dir.normalize();
		List<Entity> list = new ArrayList<Entity>();
		for (double l = 0; l < length; l += 0.5) {
			Location now = loc.clone().add(dir.clone().multiply(l));
			EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 50, 0.5d, 0.5d, 0.5d);
			for (Entity et : now.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
				damage(list, att, et, damages3[0][level - 1] * mul);
		}
		att.teleport(point);
		super.HoldingSucceed(att, times);
	}

	@Override
	public void HoldingFail(LivingEntity att, int times) {
		int tc1 = getTripod1Choice(att);
		int tc2 = getTripod2Choice(att);
		int tc3 = getTripod3Choice(att);
		int level = getLevel(att);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		double mul = ds.getMultiplyDamage(groupType) * (tc1 == 2 ? 1.62d : 1) * (tc2 == 2 ? 2.5 : 1)
				* (tc3 == 0 ? 1.95d : (tc3 == 1 ? 1.7d : 1));
		Location loc = att.getEyeLocation().clone().subtract(0, 0.25, 0);
		Location point = getPointBlock(loc, 6);
		Vector dir = point.clone().subtract(loc).toVector();
		double length = dir.length();
		dir.normalize();
		List<Entity> list = new ArrayList<Entity>();
		for (double l = 0; l < length; l += 0.5) {
			Location now = loc.clone().add(dir.clone().multiply(l));
			EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 20, 0.5d, 0.5d, 0.5d);
			for (Entity et : now.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
				damage(list, att, et, damages3[1][level - 1] * mul);
		}
		att.teleport(point);
		super.HoldingFail(att, times);
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		int tc2 = getTripod2Choice(att);
		if (getTripod1Choice(att) == 1)
			ds.setExtraCriticalChance(this.displayName, 0.4);
		if (tc2 == 0)
			ds.setExtraCriticalDamage(this.displayName, 1.2);
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		ds.setExtraCriticalChance(this.displayName, 0);
		ds.setExtraCriticalDamage(this.displayName, 0);
		return flag;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType) * (tc1 == 2 ? 1.62d : 1)
				* (tc3 == 0 ? 1.95d : (tc3 == 1 ? 1.7d : 1));
		if (tc3 == 1) {
			Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
			Location point = getPointBlock(loc, 8);
			Vector dir = point.clone().subtract(loc).toVector();
			double length = dir.length();
			dir.normalize();
			List<Entity> list = new ArrayList<Entity>();
			for (double l = 0; l < length; l += 0.5) {
				Location now = loc.clone().add(dir.clone().multiply(l));
				EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 50, 0.5d, 0.5d, 0.5d);
				for (Entity et : now.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
					damage(list, le, et, damages1[level - 1] * mul);
			}
			Location middle = new Location(loc.getWorld(), (loc.getX() + point.getX()) / 2,
					(loc.getY() + point.getY()) / 2, (loc.getZ() + point.getZ()) / 2);
			Vector dir2 = loc.getDirection().clone().setY(0).normalize();
			new BukkitRunnable() {
				@Override
				public void run() {
					for (int c = 0; c <= 5; c++) {
						List<Entity> list = new ArrayList<Entity>();
						if (c == 5) {
							new BukkitRunnable() {
								@Override
								public void run() {
									for (int i = 1; i <= 5; i++)
										for (Rel rel : star) {
											Location now = middle.clone().add(EffectStore.getRel(dir2, rel.getFront(),
													rel.getHeight(), rel.getRight(), i * 0.5));
											EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 50, 0.5d, 0.5d, 0.5d);
											for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
												damage(list, le, et,
														damages3[0][level - 1] * mul * (tc2 == 2 ? 2.5 : 1));
										}
								}
							}.runTaskLater(Core.getCore(), c * 5 + 5);
						} else
							new BukkitRunnable() {
								@Override
								public void run() {
									for (int i = 1; i <= 5; i++)
										for (Rel rel : star) {
											Location now = middle.clone().add(EffectStore.getRel(dir2, rel.getFront(),
													rel.getHeight(), rel.getRight(), i * 0.5));
											EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 20, 0.2d, 0.2d, 0.2d);
											for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
												damage(list, le, et, damages2[level - 1] / 5 * mul);
										}
								}
							}.runTaskLater(Core.getCore(), c * 5);
					}
				}
			}.runTaskLater(Core.getCore(), 3);
			le.teleport(point);
		} else {
			le.setVelocity(new Vector(0, 0.2, 0));
			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(le.getLocation().getDirection());
				}
			}.runTaskLater(Core.getCore(), 2);

			new BukkitRunnable() {

				@Override
				public void run() {
					le.setVelocity(new Vector());
					Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
					Vector dir = loc.getDirection().clone().setY(0).normalize();
					List<Entity> list = new ArrayList<Entity>();
					for (Rel rel : sting) {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
							damage(list, le, et, damages1[level - 1] * mul);
					}
					HoldingRunnable.run(le, shadowStorm.this, 10, 2);
				}
			}.runTaskLater(Core.getCore(), 5);
		}
		return false;
	}
}
