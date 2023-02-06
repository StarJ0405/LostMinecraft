package com.StarJ.LM.Skills.assassin.Reaper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.ComboCoolRunnable;
import com.StarJ.LM.Skills.Runnable.DebuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;
import com.StarJ.LM.System.EntityStore;

public class blink extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 224, 398, 509, 595, 664, 723, 770, 811, 846, 877, 964, 1061 };

	public blink() {
		super("blink", ChatColor.WHITE + "블링크", Skill.assassin.Reaper.skillGroup.Shadow, 20, 28, 500, AttackType.Back,
				"마우스 지점으로 6초간 유지되는 그림자 분신을 소환한다.", "추가 키 입력 시 분신과 함께 마우스 방향으로 표창을 던져 피해를 준다.");
		tripodChoice _11 = new tripodChoice("그림자 충전", "공격 적중 시 어둠 게이지 및 혼돈 게이지 획득량이 45.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("꿰뚫는 일격", "적에게 주는 피해가 62.0% 증가한다.");
		tripodChoice _13 = new tripodChoice("발목 절단", "리퍼에게 직접 적중된 대상의 이동속도를 3.0초간 29.4% 감소시킨다.");
		tripodChoice _21 = new tripodChoice("전진 베기", "더 이상 표창을 던지지 않는다.", "같은 방향으로 전진하며 베는 공격으로 변경된다.",
				"리퍼는 기본 피해량의 총 190.0% 피해를 준다.");
		tripodChoice _22 = new tripodChoice("소용돌이 표창", "도착한 지점에서 1.5초간 소용돌이치는 표창으로 변경된다.",
				"리퍼는 기본 피해량의 160.0%에 해당하는 피해를 준다.");
		tripodChoice _23 = new tripodChoice("분쇄 표창", "세로로 표창을 던지면서 지나가는 바닥에 그림자 지대가 일어난다.",
				"기본 피해량의 175.0%에 해당하는 피해를 준다.");
		tripodChoice _31 = new tripodChoice("죽음의 그림자", "테스트용");
		tripodChoice _32 = new tripodChoice("메아리", "리퍼에게 직접 적중된 대상의 거리 증가에 따라 최대 144.0% 피해가 증가한다.",
				"전진 베기 트라이포드는 적용 시 적에게 주는 리퍼의 피해가 135.0% 증가한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		int tc2 = getTripod2Choice(le);
		return tc2 == 1 ? 10 : (tc2 == 2 ? 4 : 2);
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Chain;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc1 == 1)
			mul *= 1.62d;
		if (tc2 == 0)
			if (tc3 == 1)
				mul *= 0.5 + 1.9 * 2.35;
			else
				mul *= 0.5 + 1.9;
		else if (tc2 == 1)
			mul *= 0.5 + 1.6;
		else if (tc2 == 2)
			mul *= 1.75d;

		if (tc3 == 1 && tc2 != 0)
			mul *= 2.44d;
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
	public void applyIdentity(LivingEntity att, double addIdentity) {
		if (getTripod1Choice(att) == 0)
			setExtraMultiplyIdentity(att, 0.45d);
		super.applyIdentity(att, addIdentity);
		setExtraMultiplyIdentity(att, 0);
	}

	@Override
	public void DebuffEnd(LivingEntity att, LivingEntity vic, int num) {
		if (num == 0)
			DataStore.setWalkSpeed(vic, this.displayName, 0);
		super.DebuffEnd(att, vic, num);
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		if (flag && getTripod1Choice(att) == 2) {
			LivingEntity vic = (LivingEntity) vic_e;
			DebuffRunnable.run(att, vic, this, 3, "이동속도감소", 0);
			DataStore.setWalkSpeed(vic, this.displayName, -0.294);
		}
		return flag;
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
	private Rel[] vertical_circle = new Rel[] { //
			new Rel(2, 0, 0), new Rel(-2, 0, 0), //
			//
			new Rel(1.984, 0.25, 0), new Rel(1.984, -0.25, 0), //
			new Rel(-1.984, 0.25, 0), new Rel(-1.984, -0.25, 0), //
			//
			new Rel(1.936, 0.5, 0), new Rel(1.936, -0.5, 0), //
			new Rel(-1.936, 0.5, 0), new Rel(-1.936, -0.5, 0), //
			//
			new Rel(1.854, 0.75, 0), new Rel(1.854, -0.75, 0), //
			new Rel(-1.854, 0.75, 0), new Rel(-1.854, -0.75, 0), //
			//
			new Rel(1.732, 1, 0), new Rel(1.732, -1, 0), //
			new Rel(-1.732, 1, 0), new Rel(-1.732, -1, 0), //
			//
			new Rel(1.561, 1.25, 0), new Rel(1.561, -1.25, 0), //
			new Rel(-1.561, 1.25, 0), new Rel(-1.561, -1.25, 0), //
			//
			new Rel(1.323, 1.5, 0), new Rel(1.323, -1.5, 0), //
			new Rel(-1.323, 1.5, 0), new Rel(-1.323, -1.5, 0), //
			//
			new Rel(0.968, 1.75, 0), new Rel(0.968, -1.75, 0), //
			new Rel(-0.968, 1.75, 0), new Rel(-0.968, -1.75, 0), //
			//
			new Rel(0.75, 1.854, 0), new Rel(0.75, -1.854, 0), //
			new Rel(-0.75, 1.854, 0), new Rel(-0.75, -1.854, 0), //
			//
			new Rel(0.5, 1.936, 0), new Rel(0.5, -1.936, 0), //
			new Rel(-0.5, 1.936, 0), new Rel(-0.5, -1.936, 0), //
			//
			new Rel(0.25, 1.984, 0), new Rel(0.25, -1.984, 0), //
			new Rel(-0.25, 1.984, 0), new Rel(-0.25, -1.984, 0), //
			//
			new Rel(0, 2, 0), new Rel(0, -2, 0),//
	};
	private HashMap<LivingEntity, LivingEntity> shadow = new HashMap<LivingEntity, LivingEntity>();

	private double getPower(Entity att, Entity vic) {
		double length = att.getLocation().distance(vic.getLocation());
		// 144%
		if (length > 6)
			length = 6;
		return 1 + length * 1.44 / 6;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		Location loc1 = le.getEyeLocation().clone().subtract(0, 0.25, 0);
		Location point = getPoint(le, loc1, 10);
		if (ComboCoolRunnable.hasCombo(le, this)) {
			if (shadow.containsKey(le)) {
				int tc1 = getTripod1Choice(le);
				int tc2 = getTripod2Choice(le);
				int tc3 = getTripod3Choice(le);
				int level = getLevel(le);
				DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
				double mul = ds.getMultiplyDamage(groupType) * (tc1 == 1 ? 1.62d : 1);

				LivingEntity shadow = this.shadow.get(le);
				Location loc2 = shadow.getEyeLocation().clone().subtract(0, 0.25, 0);
				Vector dir1 = point.clone().subtract(loc1).toVector().normalize().multiply(0.5d);
				Vector dir2 = point.clone().subtract(loc2).toVector().normalize().multiply(0.5d);
				loc2.setDirection(dir2);
				List<Entity> list1 = new ArrayList<Entity>();
				List<Entity> list2 = new ArrayList<Entity>();
				if (tc2 == 0) {
					Location point1 = getPointBlock(loc1, 8);
					int i2 = 0;
					final int max2 = 16;
					for (; i2 <= max2; i2++) {
						Location now2 = loc2.clone().add(dir2.clone().multiply(1 + i2));
						new BukkitRunnable() {
							@Override
							public void run() {
								EffectStore.spawnRedStone(now2, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								for (Entity et : now2.getWorld().getNearbyEntities(now2, 1, 1, 1))
									damage(list2, le, et, damages[level - 1] / 2 * mul,
											AttackType.getAttackType(et, shadow));
							}
						}.runTaskLater(Core.getCore(), i2 / 2);
					}
					int max1 = (int) (point1.clone().subtract(loc1).length() * 2);
					int i1 = 0;
					for (; i1 <= max1; i1++) {
						Location now1 = loc1.clone().add(dir1.clone().multiply(1 + i1));
						new BukkitRunnable() {
							@Override
							public void run() {
								EffectStore.spawnRedStone(now1, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								for (Entity et : now1.getWorld().getNearbyEntities(now1, 1, 1, 1))
									damage(list1, le, et, damages[level - 1] * 1.9 * mul * 2.35);
							}
						}.runTaskLater(Core.getCore(), i1 / 2);
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							le.teleport(point1);
							EffectStore.Directional.SWEEP_ATTACK.spawnDirectional(point, 1, 0.1, 0.1, 0.1, 0);
						}
					}.runTaskLater(Core.getCore(), i2 / 4);
					Location now2 = loc2.clone().add(dir2.clone().multiply(1 + max2));
					new BukkitRunnable() {
						@Override
						public void run() {
							shadow.teleport(now2);
							EffectStore.Directional.SWEEP_ATTACK.spawnDirectional(now2, 1, 0.1, 0.1, 0.1, 0);
						}
					}.runTaskLater(Core.getCore(), i2 / 4);
					new BukkitRunnable() {
						@Override
						public void run() {
							shadow.remove();
						}
					}.runTaskLater(Core.getCore(), i2 / 2 + 5);
				} else if (tc2 == 1) {
					Location point1 = getPoint(le, loc1, 8);
					int max1 = (int) (point1.clone().subtract(loc1).length() * 2);
					int i1 = 0;
					for (; i1 <= max1; i1++) {
						Location now1 = loc1.clone().add(dir1.clone().multiply(1 + i1));
						new BukkitRunnable() {
							@Override
							public void run() {
								EffectStore.spawnRedStone(now1, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
							}
						}.runTaskLater(Core.getCore(), i1 / 2);
					}
					Location now1 = loc1.clone().add(dir1.clone().multiply(1 + i1));
					for (int c = 0; c < 5; c++) {
						List<Entity> list = new ArrayList<Entity>();
						for (Rel rel : circle) {
							new BukkitRunnable() {
								@Override
								public void run() {
									Location now = now1.clone().add(
											EffectStore.getRel(dir1, rel.getFront(), rel.getHeight(), rel.getRight()));
									EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
									for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
										damage(list, le, et,
												damages[level - 1] * 1.6 / 5 * mul * (tc3 == 1 ? getPower(le, et) : 1));
								}
							}.runTaskLater(Core.getCore(), i1 / 2 + c * 5);
						}
					}
					Location point2 = getPoint(shadow, loc2, 8);
					int max2 = (int) (point2.clone().subtract(loc2).length() * 2);
					int i2 = 0;
					for (; i2 <= max2; i2++) {
						Location now2 = loc2.clone().add(dir2.clone().multiply(1 + i2));
						new BukkitRunnable() {
							@Override
							public void run() {
								EffectStore.spawnRedStone(now2, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
							}
						}.runTaskLater(Core.getCore(), i2 / 2);
					}
					Location now2 = loc2.clone().add(dir2.clone().multiply(1 + i2));
					for (int c = 0; c < 5; c++) {
						List<Entity> list = new ArrayList<Entity>();
						for (Rel rel : circle) {
							new BukkitRunnable() {
								@Override
								public void run() {
									Location now = now2.clone().add(
											EffectStore.getRel(dir2, rel.getFront(), rel.getHeight(), rel.getRight()));
									EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
									for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
										damage(list, le, et,
												damages[level - 1] / 2 / 5 * mul * (tc3 == 1 ? getPower(le, et) : 1));
								}
							}.runTaskLater(Core.getCore(), i2 + c * 5);
						}
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							shadow.remove();
						}
					}.runTaskLater(Core.getCore(), i2 / 2 + 5);
				} else if (tc2 == 2) {
					int i = 0;
					final int max = 16;
					loc1 = le.getLocation();
					loc2 = shadow.getLocation();
					for (; i <= max; i++) {
						Location now1 = loc1.clone().add(dir1.clone().multiply(1 + i));
						Location now2 = loc2.clone().add(dir2.clone().multiply(1 + i));
						new BukkitRunnable() {
							@Override
							public void run() {
								for (Rel rel : vertical_circle) {
									Location now11 = now1.clone().add(EffectStore.getRel(dir1, rel.getFront(),
											rel.getHeight(), rel.getRight(), 0.5));
									EffectStore.spawnRedStone(now11, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
									for (Entity et : now11.getWorld().getNearbyEntities(now11, 1, 1, 1))
										if (damage(list1, le, et, damages[level - 1] / 2 * 1.75 / 3 * mul
												* (tc3 == 1 ? getPower(le, et) : 1))) {
											damage(new ArrayList<Entity>(), le, et, damages[level - 1] / 2 * 1.75 / 3
													* mul * (tc3 == 1 ? getPower(le, et) : 1));
											damage(new ArrayList<Entity>(), le, et, damages[level - 1] / 2 * 1.75 / 3
													* mul * (tc3 == 1 ? getPower(le, et) : 1));
										}
									Location now22 = now2.clone().add(EffectStore.getRel(dir2, rel.getFront(),
											rel.getHeight(), rel.getRight(), 0.5));
									EffectStore.spawnRedStone(now22, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
									for (Entity et : now22.getWorld().getNearbyEntities(now22, 1, 1, 1))
										if (damage(list2, le, et,
												damages[level - 1] / 2 * 1.75 / 3 * mul
														* (tc3 == 1 ? getPower(le, et) : 1),
												AttackType.getAttackType(et, shadow))) {
											damage(new ArrayList<Entity>(), le, et,
													damages[level - 1] / 2 * 1.75 / 3 * mul
															* (tc3 == 1 ? getPower(le, et) : 1),
													AttackType.getAttackType(et, shadow));
											damage(new ArrayList<Entity>(), le, et,
													damages[level - 1] / 2 * 1.75 / 3 * mul
															* (tc3 == 1 ? getPower(le, et) : 1),
													AttackType.getAttackType(et, shadow));
										}
								}
							}
						}.runTaskLater(Core.getCore(), i * 2);
					}

					new BukkitRunnable() {

						@Override
						public void run() {
							shadow.remove();
						}
					}.runTaskLater(Core.getCore(), i * 2 + 5);
				} else {
					int i = 0;
					final int max = 16;
					for (; i <= max; i++) {
						Location now1 = loc1.clone().add(dir1.clone().multiply(1 + i));
						Location now2 = loc2.clone().add(dir2.clone().multiply(1 + i));
						new BukkitRunnable() {
							@Override
							public void run() {
								EffectStore.spawnRedStone(now1, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								EffectStore.spawnRedStone(now2, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								for (Entity et : now1.getWorld().getNearbyEntities(now1, 1, 1, 1))
									damage(list1, le, et,
											damages[level - 1] / 2 * mul * (tc3 == 1 ? getPower(le, et) : 1));
								for (Entity et : now2.getWorld().getNearbyEntities(now2, 1, 1, 1))
									damage(list2, le, et,
											damages[level - 1] / 2 * mul * (tc3 == 1 ? getPower(le, et) : 1),
											AttackType.getAttackType(et, shadow));
							}
						}.runTaskLater(Core.getCore(), i / 2);
					}

					new BukkitRunnable() {

						@Override
						public void run() {
							shadow.remove();
						}
					}.runTaskLater(Core.getCore(), i / 2 + 5);
				}
				this.shadow.remove(le);
			}
			comboEnd(le);
		} else {
			Entity shadow = EntityStore.Shadow.spawnEntity(point);
			this.shadow.put(le, (LivingEntity) shadow);
			ComboCoolRunnable.run(le, this, getComboDuration(le));
		}
		return false;
	}
}
