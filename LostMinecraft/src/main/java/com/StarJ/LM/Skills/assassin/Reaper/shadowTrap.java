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
import com.StarJ.LM.Skills.Runnable.SkillCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class shadowTrap extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 179, 319, 408, 477, 533, 579, 618, 652, 679, 705, 775, 853 };

	public shadowTrap() {
		super("shadowTrap", ChatColor.WHITE + "쉐도우 트랩", Skill.assassin.Reaper.skillGroup.Shadow, 14, 96, 428,
				AttackType.Back, "자신의 위치에 그림자 덫을 소환한다.", "뒤로 이동한 뒤 그림자를 터트려 주변에 피해를 준다.");
		tripodChoice _11 = new tripodChoice("치명적인 그림자", "치명타 적중률이 40.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("날렵한 몸놀림", "스킬 적중 시 4.0초간 이동속도가 24.6% 증가한다.");
		tripodChoice _13 = new tripodChoice("덫 강화", "테스트용");
		tripodChoice _21 = new tripodChoice("도약", "테스트용");
		tripodChoice _22 = new tripodChoice("그림자 늪", "그림자 덫이 폭발 후 3.0초간 그림자 늪을 소환하여 0.5초 마다 피해를 준다.",
				"이동속도를 3.0초간 69.0% 감소시킨다.");
		tripodChoice _23 = new tripodChoice("그림자 올가미", "그림자 덫이 추가로 폭발하며 45.0%의 추가 피해를 준다.");
		tripodChoice _31 = new tripodChoice("연속 공격", "스택이 가능하게 변경된다.", "최대 2회 중첩이 가능하지만 충전 시간이 20.0초로 변경된다.");
		tripodChoice _32 = new tripodChoice("그림자 활성", "스킬 사용 시 그림자 스킬들의 재사용 대기시간이 2.4초 감소한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		int tc2 = getTripod2Choice(le);
		return tc2 == 1 ? 7 : (tc2 == 2 ? 2 : 1);

	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? -6 : 0;

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

	@Override
	public double getTotalDamage(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
//		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 1)
			mul *= 1.2d;
		else if (tc2 == 2)
			mul *= 1.45d;
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
		if (num == 0)
			Core.getCore().getHashMapStore().getDataStore(att).setExtraWalkspeed(this.displayName, 0);
		super.BuffEnd(att, num);
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
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		int tc1 = getTripod1Choice(att);
		if (tc1 == 0)
			ds.setExtraCriticalChance(this.displayName, 0.4);
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		if (flag) {
			if (tc1 == 1) {
				ds.setExtraWalkspeed(this.displayName, 0.246d);
				BuffRunnable.run(att, this, 4, 0);
			}
			if (getTripod2Choice(att) == 1) {
				LivingEntity vic = (LivingEntity) vic_e;
				DataStore.setWalkSpeed(vic, this.displayName, -0.69);
				DebuffRunnable.run(att, vic, this, 3, "그림자 늪", 0);
			}
		}
		ds.setExtraCriticalChance(this.displayName, 0);
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

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		Location loc = le.getLocation();
		Vector dir = loc.getDirection().clone().setY(0).normalize();
		le.setVelocity(new Vector(0, 0.2, 0));
		new BukkitRunnable() {
			@Override
			public void run() {
				le.setVelocity(dir.clone().multiply(-1));
			}
		}.runTaskLater(Core.getCore(), 2);

		new BukkitRunnable() {
			@Override
			public void run() {
				le.setVelocity(new Vector());
				List<Entity> list = new ArrayList<Entity>();
				for (Rel rel : circle) {
					Location now = loc.clone()
							.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
					EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
					for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
						damage(list, le, et, damages[level - 1] * mul);
					now = loc.clone()
							.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 0.5));
					for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
						damage(list, le, et, damages[level - 1] * mul);
				}
				if (tc2 == 1) {
					for (int i = 1; i <= 6; i++)
						new BukkitRunnable() {

							@Override
							public void run() {
								List<Entity> list = new ArrayList<Entity>();
								for (Rel rel : circle) {
									Location now = loc.clone().add(
											EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
									EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 30, 0.5d, 0.5d, 0.5d);
									for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
										damage(list, le, et, damages[level - 1] / 30 * mul);
									now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
											rel.getRight(), 0.5));
									for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
										damage(list, le, et, damages[level - 1] / 30 * mul);
								}
							}
						}.runTaskLater(Core.getCore(), i * 4);
				} else if (tc2 == 2) {
					new BukkitRunnable() {

						@Override
						public void run() {
							List<Entity> list = new ArrayList<Entity>();
							for (Rel rel : circle) {
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 30, 0.5d, 0.5d, 0.5d);
								for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
									damage(list, le, et, damages[level - 1] * 0.45 * mul);
								now = loc.clone().add(
										EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 0.5));
								for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
									damage(list, le, et, damages[level - 1] * 0.45 * mul);
							}
						}
					}.runTaskLater(Core.getCore(), 4);
				}
			}
		}.runTaskLater(Core.getCore(), 6);
		if (tc3 == 1)
			for (Skill skill : Skill.assassin.Reaper.getShadows())
				SkillCoolRunnable.run(le, skill, skill.getCooldownNow(le) - 2.4d);
		return false;
	}
}
