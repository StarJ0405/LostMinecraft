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
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class lastGraffiti extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 666, 1184, 1517, 1775, 1980, 2153, 2296, 2419, 2522, 2616, 2877,
			3165 };

	public lastGraffiti() {
		super("lastGraffiti", ChatColor.WHITE + "라스트 그래피티", Skill.assassin.Reaper.skillGroup.SurpiseAttack, 22, 123, 0,
				AttackType.Back, "연기처럼 사라져 이동 후 강하게 내려찍어 피해를 준다.");
		tripodChoice _11 = new tripodChoice("범위 증가", "테스트용");
		tripodChoice _12 = new tripodChoice("급소 타격", "치명타 적중률이 40.0% 증가한다.");
		tripodChoice _13 = new tripodChoice("무력화 강화", "테스트용");
		tripodChoice _21 = new tripodChoice("신속한 준비", "테스트용");
		tripodChoice _22 = new tripodChoice("급습 강화", "적에게 주는 피해가 60.0% 증가한다.");
		tripodChoice _23 = new tripodChoice("혼돈 강화", "테스트용");
		tripodChoice _31 = new tripodChoice("유언", "더욱 높이 뛰어올라 강하게 내려찍어 기본 피해량의 180.0% 피해를 준다.", "피격 범위가 넓어진다.");
		tripodChoice _32 = new tripodChoice("그림자 올가미", "테스트용");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Point;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 1)
			mul *= 1.6d;
		if (tc3 == 0)
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
	public int getDestruction() {
		return 1;
	}

	private Rel[] circle = new Rel[] { //
			new Rel(2, 0, 0), new Rel(-2, 0, 0), //
			//
			new Rel(1.984, 0, 0.25), new Rel(1.984, 0, -0.25), //
			new Rel(-1.984, 0, 0.25), new Rel(-1.984, 0, -0.25), //
			//
			new Rel(1.936, 0, 0.5), new Rel(1.936, 0, -0.5), //
			new Rel(-1.936, 0, 0.5), new Rel(-1.936, 0, -0.5), //
			//
			new Rel(1.854, 0, 0.75), new Rel(1.854, 0, -0.75), //
			new Rel(-1.854, 0, 0.75), new Rel(-1.854, 0, -0.75), //
			//
			new Rel(1.732, 0, 1), new Rel(1.732, 0, -1), //
			new Rel(-1.732, 0, 1), new Rel(-1.732, 0, -1), //
			//
			new Rel(1.561, 0, 1.25), new Rel(1.561, 0, -1.25), //
			new Rel(-1.561, 0, 1.25), new Rel(-1.561, 0, -1.25), //
			//
			new Rel(1.323, 0, 1.5), new Rel(1.323, 0, -1.5), //
			new Rel(-1.323, 0, 1.5), new Rel(-1.323, 0, -1.5), //
			//
			new Rel(0.968, 0, 1.75), new Rel(0.968, 0, -1.75), //
			new Rel(-0.968, 0, 1.75), new Rel(-0.968, 0, -1.75), //
			//
			new Rel(0.75, 0, 1.854), new Rel(0.75, 0, -1.854), //
			new Rel(-0.75, 0, 1.854), new Rel(-0.75, 0, -1.854), //
			//
			new Rel(0.5, 0, 1.936), new Rel(0.5, 0, -1.936), //
			new Rel(-0.5, 0, 1.936), new Rel(-0.5, 0, -1.936), //
			//
			new Rel(0.25, 0, 1.984), new Rel(0.25, 0, -1.984), //
			new Rel(-0.25, 0, 1.984), new Rel(-0.25, 0, -1.984), //
			//
			new Rel(0, 0, 2), new Rel(0, 0, -2),//
	};

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (getTripod1Choice(att) == 1)
			ds.setExtraCriticalChance(this.displayName, 0.4d);
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		ds.setExtraCriticalChance(this.displayName, 0);
		return flag;

	}

	@Override
	public boolean Use(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType) * (tc2 == 1 ? 1.6d : 1) * (tc3 == 0 ? 1.8 : 1);
		if (super.Use(le))
			return true;

		int level = getLevel(le);

		Location loc = le.getEyeLocation();
		Location point = getPointBlock(loc, 12);
		le.setVelocity(new Vector(0, (tc3 == 0 ? 4 : 2), 0));

		Core.getCore().getHashMapStore().addPventFallDamage(le, true);
		new BukkitRunnable() {
			int time = 0;

			@Override
			public void run() {
				Location loc = le.getLocation();
				if (le.isOnGround()) {
					this.cancel();
					le.setVelocity(new Vector());
					List<Entity> list = new ArrayList<Entity>();
					Vector dir = loc.getDirection().clone().setY(0).normalize();
					for (Rel rel : circle) {
						Location now1 = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						if (tc3 == 0) {
							Location now3 = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.5));
							EffectStore.spawnRedStone(now3, 128, 0, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
							for (Entity et : now3.getWorld().getNearbyEntities(now3, 1, 2, 1))
								damage(list, le, et, damages[level - 1] * mul);
						} else
							EffectStore.spawnRedStone(now1, 128, 0, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
						for (Entity et : now1.getWorld().getNearbyEntities(now1, 1, 2, 1))
							damage(list, le, et, damages[level - 1] * mul);

						Location now2 = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 0.5));
						for (Entity et : now1.getWorld().getNearbyEntities(now2, 1, 2, 1))
							damage(list, le, et, damages[level - 1] * mul);
					}
					new BukkitRunnable() {

						@Override
						public void run() {
							Core.getCore().getHashMapStore().addPventFallDamage(le, false);
						}
					}.runTaskLater(Core.getCore(), 1);
				} else {
					EffectStore.spawnRedStone(loc, 128, 0, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
					if (time == 0) {
						Vector dir = point.clone().subtract(loc).toVector().normalize().multiply(2);
						le.setVelocity(dir);
					}
				}
				this.time++;
				if (time > 20 * 60 * 1)
					this.cancel();
			}
		}.runTaskTimer(Core.getCore(), 5, 1);

		return false;
	}
}
