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

public class riffAttack extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 280, 496, 634, 744, 831, 900, 960, 1016, 1057, 1098, 1207, 1328 };

	public riffAttack() {
		super("riffAttack", ChatColor.WHITE + "리프 어택", Skill.warrior.WarLord.skillGroup.Normal, 16, 87, 20,
				"9m 내 지정한 위치로 크게 뛰어올라 내리 찍는다.");
		tripodChoice _11 = new tripodChoice("직접타격", "착지 시 주는 피해량이 40.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("빠른 준비", "재사용 대기시간이 5.6초 감소한다.");
		tripodChoice _13 = new tripodChoice("탁월한 기동성", "테스트용");
		tripodChoice _21 = new tripodChoice("대지진", "테스트용");
		tripodChoice _22 = new tripodChoice("전류지대", "착지 지점에 5.0초간 전류 지대를 생성한다.", "매 초마다 기본 피해량의 14% 피해를 준다.");
		tripodChoice _23 = new tripodChoice("충격파", "착지시 적중된 적에게 45.0% 추가 피해를 준다.");
		tripodChoice _31 = new tripodChoice("낮은 충격", "콤보 조작으로 변경된다.", "추가 입력시 다시 한번 뛰어 올라 내려 찍는다.",
				"두번째 착지는 80.0% 피해를 준다.");
		tripodChoice _32 = new tripodChoice("도약", "최대 18.0m까지 이동 가능하다.", "피해량이 60.0% 증가된다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		int times = 1;
		if (getTripod2Choice(le) == 2)
			times += 1;
		if (getTripod3Choice(le) == 0)
			times *= 2;
		return times;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Point;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc1 == 0)
			mul *= 1.4d;
		if (tc2 == 2)
			mul *= 1.45d;

		if (tc3 == 1)
			mul *= 1.6d;
		else if (tc3 == 0) {
			if (tc2 == 2)
				mul *= 2.7d / 1.45d;
			else
				mul *= 1.8d;
		}

		return damages[level - 1] * mul + (tc2 == 1 ? damages[level - 1] * (tc3 == 0 ? 1.4d : 0.7d) : 0d);
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod1Choice(le) == 1 ? 5.6d : 0;
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
		double mul = ds.getMultiplyDamage(groupType);
		double distance = 9;
		if (tc1 == 0)
			mul *= 1.4d;

		if (tc3 == 1) {
			distance = 18;
			mul *= 1.6d;
		} else if (tc3 == 0)
			if (ComboCoolRunnable.hasCombo(le, this)) {
				mul *= 0.8d;
				comboEnd(le);
			} else
				ComboCoolRunnable.run(le, this, getComboDuration(le));
		Location point = getPointBlock(loc, distance);
		le.setVelocity(new Vector(0, 0.2, 0));
		new BukkitRunnable() {

			@Override
			public void run() {
				le.setVelocity(dir.clone().add(new Vector(0, 1, 0)));
			}
		}.runTaskLater(Core.getCore(), 2);

		new BukkitRunnable() {

			@Override
			public void run() {
				Vector dir = point.clone().subtract(le.getLocation().clone()).toVector();
				dir.normalize();
				le.setVelocity(dir.multiply(2));
			}
		}.runTaskLater(Core.getCore(), 10);
		List<Entity> list = new ArrayList<Entity>();
		List<Entity> list2 = new ArrayList<Entity>();
		final double mul_r = mul;
		new BukkitRunnable() {
			int time = 0;

			@Override
			public void run() {
				if (le.isOnGround()) {
					this.cancel();
					le.setVelocity(new Vector());
					Location loc = le.getLocation();
					for (Rel rel : circle) {
						Location now = loc.clone().add(0, 0.5, 0)
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						if (tc2 != 1)
							EffectStore.spawnRedStone(now, 255, 212, 0, 1, 10, 0, 0, 0);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1)) {
							damage(list, le, et, damages[level - 1] * mul_r);
							if (tc2 == 2) {
								if (!ComboCoolRunnable.hasCombo(le, riffAttack.this))// 반대로 생각
									damage(list2, le, et, damages[level - 1] * mul_r / 0.8d * 0.45);
								else
									damage(list2, le, et, damages[level - 1] * mul_r * 0.45);
							}
						}
					}
					for (Entity et : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
						damage(list, le, et, damages[level - 1] * mul_r);
						if (tc2 == 2)
							if (!ComboCoolRunnable.hasCombo(le, riffAttack.this))// 반대로 생각
								damage(list2, le, et, damages[level - 1] * mul_r / 0.8d * 0.45);
							else
								damage(list2, le, et, damages[level - 1] * mul_r * 0.45);
					}
					if (tc2 == 1) {
						BuffRunnable.run(le, riffAttack.this, 5, 0);
						new BukkitRunnable() {
							int time = 0;

							@Override
							public void run() {
								this.time++;
								List<Entity> list = new ArrayList<Entity>();
								if (BuffRunnable.has(le, riffAttack.this, 0) && this.time <= 5) {
									for (Rel rel : circle) {
										Location now = loc.clone().add(0, 0.5, 0).add(EffectStore.getRel(dir,
												rel.getFront(), rel.getHeight(), rel.getRight()));
										EffectStore.spawnRedStone(now, 0, 150, 255, 1, 10, 0, 0, 0);
										for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
											damage(false, list, le, et, damages[level - 1] * 0.14);
									}
									for (Entity et : loc.getWorld().getNearbyEntities(loc, 1, 1, 1))
										damage(false, list, le, et, damages[level - 1] * 0.14);
								} else
									this.cancel();
							}
						}.runTaskTimer(Core.getCore(), 0, 20);
					}
				} else {
					this.time++;
					if (this.time > 200)
						this.cancel();
				}

			}
		}.runTaskTimer(Core.getCore(), 10, 1);
		return false;
	}
}
