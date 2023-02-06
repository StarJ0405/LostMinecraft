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
import com.StarJ.LM.Skills.Runnable.DebuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class callOfKnife extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 90, 161, 206, 242, 270, 293, 313, 330, 344, 357, 392, 431 };
	protected final double[] damages2 = new double[] { 136, 242, 309, 363, 406, 442, 472, 499, 519, 540, 594, 653 };
	protected final double[] damages3 = new double[] { 226, 402, 514, 602, 672, 731, 780, 823, 857, 889, 977, 1075 };

	public callOfKnife() {
		super("callOfKnife", ChatColor.WHITE + "콜 오브 나이프", Skill.assassin.Reaper.skillGroup.Shadow, 20, 72, 500,
				"지점에 사신의 검을 소환한다.", "주변에 피해를 주고 2초간 이동속도를 5% 감소시킨다.", "주변에 피해를 주고 폭발하여 피해를 준다.",
				"이동속도 감소는 최대 5회 중첩된다.");
		tripodChoice _11 = new tripodChoice("그림자 충전", "공격 적중 시 어둠 게이지 및 혼돈 게이지 획득량이 45.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("약육강식", "테스트용");
		tripodChoice _13 = new tripodChoice("그림자 강탈", "적중 시 3.0초간 리퍼의 이동속도가 19.2% 증가한다.");
		tripodChoice _21 = new tripodChoice("지속되는 부름", "지속시간이 증가하여 2회 공격이 추가된다.");
		tripodChoice _22 = new tripodChoice("성장하는 어둠", "마지막 폭발의 피해가 100.0% 증가한다.");
		tripodChoice _23 = new tripodChoice("치명적인 그림자", "치명타 적중률이 60.0%증가한다.");
		tripodChoice _31 = new tripodChoice("절망의 부름", "스택이 가능하게 변경된다.", "최대 2회까지 중첩이 가능하다.", "충전 시간이 25.0초로 변경된다.");
		tripodChoice _32 = new tripodChoice("공포의 부름", "테스트용");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod2Choice(le) == 0 ? 7 : 5;// 1 + 3(+2) + 1

	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Point;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
//		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		return (damages1[level - 1] + damages2[level - 1] * (tc2 == 0 ? (5d / 3) : 1)
				+ damages3[level - 1] * (tc2 == 1 ? 2 : 1)) * mul;
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
	public double getReduceCooldown(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? -5 : 0;

	}

	@Override
	public void setTripod1Choice(LivingEntity le, int choice) {
		super.setTripod1Choice(le, choice);
		if (getTripod3Choice(le) == 0) {
			setMaxStack(le, 2);
			setNowStack(le, 2);
		} else {
			setMaxStack(le, -1);
			setNowStack(le, -1);
		}
	}

	@Override
	public void setTripod2Choice(LivingEntity le, int choice) {
		super.setTripod2Choice(le, choice);
		if (getTripod3Choice(le) == 0) {
			setMaxStack(le, 2);
			setNowStack(le, 2);
		} else {
			setMaxStack(le, -1);
			setNowStack(le, -1);
		}
	}

	@Override
	public void setTripod3Choice(LivingEntity le, int choice) {
		super.setTripod3Choice(le, choice);
		if (getTripod3Choice(le) == 0) {
			setMaxStack(le, 2);
			setNowStack(le, 2);
		} else {
			setMaxStack(le, -1);
			setNowStack(le, -1);
		}
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
	private Rel[] sword = new Rel[] { //
			new Rel(0, 0, 0), //
			new Rel(0, 0.25, 0), new Rel(0, 0.25, -0.25), new Rel(0, 0.25, 0.25), //
			new Rel(0, 0.5, 0), new Rel(0, 0.5, -0.25), new Rel(0, 0.5, 0.25), //
			new Rel(0, 0.75, 0), new Rel(0, 0.75, -0.25), new Rel(0, 0.75, 0.25), //
			new Rel(0, 1, 0), new Rel(0, 1, -0.25), new Rel(0, 1, 0.25), //
			new Rel(0, 1.25, 0), new Rel(0, 1.25, -0.25), new Rel(0, 1.25, 0.25), //
			//
			new Rel(0, 1.5, 0), new Rel(0, 1.5, -0.25), new Rel(0, 1.5, 0.25), //
			new Rel(0, 1.5, -0.5), new Rel(0, 1.5, 0.5), //
			new Rel(0, 1.5, -0.75), new Rel(0, 1.5, 0.75), //
			//
			new Rel(0, 1.75, 0), new Rel(0, 2, 0), //
	};

	@Override
	public void applyIdentity(LivingEntity att, double addIdentity) {
		if (getTripod1Choice(att) == 0)
			setExtraMultiplyIdentity(att, 0.45d);
		super.applyIdentity(att, addIdentity);
		setExtraMultiplyIdentity(att, 0);
	}

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		if (num == 0)
			Core.getCore().getHashMapStore().getDataStore(att).setExtraWalkspeed(this.displayName, 0);
		super.BuffEnd(att, num);

	}
	@Override
	public void DebuffEnd(LivingEntity att, LivingEntity vic, int num) {
		DataStore.setWalkSpeed(vic, this.displayName, 0);
		super.DebuffEnd(att, vic, num);
	}
	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (getTripod2Choice(att) == 2)
			ds.setExtraCriticalChance(this.displayName, 0.6);
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		ds.setExtraCriticalChance(this.displayName, 0);
		if (flag) {
			LivingEntity vic = (LivingEntity) vic_e;
			if (DebuffRunnable.has(vic, this, 2)) {
				DebuffRunnable.run(att, vic, this, 2, "이동속도감소", 3);
				DebuffRunnable.cancel(att, vic, this, 2, false);
				DataStore.setWalkSpeed(vic, this.displayName, -0.2);
			} else if (DebuffRunnable.has(vic, this, 1)) {
				DebuffRunnable.run(att, vic, this, 2, "이동속도감소", 2);
				DebuffRunnable.cancel(att, vic, this, 1, false);
				DataStore.setWalkSpeed(vic, this.displayName, -0.15);
			} else if (DebuffRunnable.has(vic, this, 0)) {
				DebuffRunnable.run(att, vic, this, 2, "이동속도감소", 1);
				DebuffRunnable.cancel(att, vic, this, 0, false);
				DataStore.setWalkSpeed(vic, this.displayName, -0.1);
			} else if (DebuffRunnable.has(vic, this)) {
				DebuffRunnable.run(att, vic, this, 2, "이동속도감소", 4);
				DebuffRunnable.cancel(att, vic, this, 3, false);
				DataStore.setWalkSpeed(vic, this.displayName, -0.25);
			} else {
				DataStore.setWalkSpeed(vic, this.displayName, -0.05);
				DebuffRunnable.run(att, vic, this, 2, "이동속도감소", 0);
			}
			if (getTripod1Choice(att) == 2) {
				BuffRunnable.run(att, this, 3, 0);
				ds.setExtraWalkspeed(this.displayName, 0.192);
			}
		}
		return flag;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
//		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
		Location point = getPointBlock(loc, 3);
		Vector dir = loc.getDirection().clone().setY(0).normalize();
		List<Entity> list1 = new ArrayList<Entity>();
		List<Entity> list3 = new ArrayList<Entity>();
		final int max = 10;
		int h = 0;
		for (; h <= max; h++) {
			final double height = h / 2d;
			new BukkitRunnable() {
				@Override
				public void run() {
					for (Rel rel : sword) {
						Location now = point.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						now = now.clone().add(0, max * 0.5 - height, 0);
						EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
							damage(list1, le, et, damages1[level - 1] * mul);
					}
				}
			}.runTaskLater(Core.getCore(), h);
		}
		int c = 0;
		for (; c < (tc2 == 0 ? 5 : 3); c++) {
			List<Entity> list2 = new ArrayList<Entity>();
			new BukkitRunnable() {

				@Override
				public void run() {
					for (int r = 0; r < 5; r++)
						for (Rel rel : circle) {
							Location now = point.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
									rel.getRight(), 0.5 * (r + 1)));
							EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
							for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
								damage(list2, le, et, damages2[level - 1] / 3 * mul);
						}
				}
			}.runTaskLater(Core.getCore(), h + c * 5);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for (int r = 0; r < 5; r++)
					for (Rel rel : circle) {
						Location now = point.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
								rel.getRight(), 0.5 * (r + 1)));
						EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.5d, 0.5d, 0.5d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
							damage(list3, le, et, damages3[level - 1] * (tc2 == 1 ? 2 : 1) * mul);
					}
			}
		}.runTaskLater(Core.getCore(), h + c * 5 + 3);
		return false;
	}
}
