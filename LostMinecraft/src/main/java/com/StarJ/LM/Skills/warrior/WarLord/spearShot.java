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

public class spearShot extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 85, 151, 193, 226, 253, 274, 292, 308, 321, 333, 366, 402 };
	protected final double[] damages2 = new double[] { 170, 301, 385, 452, 505, 547, 583, 617, 642, 666, 732, 805 };
	protected final double[] damages3 = new double[] { 168, 299, 382, 449, 501, 542, 578, 612, 636, 660, 726, 798 };

	public spearShot() {
		super("spearShot", ChatColor.WHITE + "스피어 샷", Skill.warrior.WarLord.skillGroup.Lance, 18, 60, 45,
				AttackType.Head, "창을 크게 내질러 피해를 준다.", "그 후 화력을 창 끝에 모은다.", "창 끝에 화력이 충분히 모이면 그대로 포격한다.");
		tripodChoice _11 = new tripodChoice("단단한 갑옷", "테스트용");
		tripodChoice _12 = new tripodChoice("날렵한 움직임", "테스트용");
		tripodChoice _13 = new tripodChoice("강인함", "테스트용");
		tripodChoice _21 = new tripodChoice("강화된 일격", "적에게 주는 피해가 50.0% 증가한다.");
		tripodChoice _22 = new tripodChoice("약점 포착", "테스트용");
		tripodChoice _23 = new tripodChoice("부위파괴 강화", "테스트용");
		tripodChoice _31 = new tripodChoice("폭멸창", "전방으로 4.0m 돌진하며 공격한다.", "이때 창으로 찌르는 순간 바로 포격한다.",
				"포격 피해가 70.8% 증가한다.");
		tripodChoice _32 = new tripodChoice("근접폭발", "차지 조작으로 변경된다.", "2단계 차지 시 52.5%의 증가된 포격 피해를 준다.",
				"오버 차지 시 150.1%의 증가된 포격 피해를 준다.", "1단계 차지 시 포격 피해가 2.4% 감소된다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? 10 : 2;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return getTripod3Choice(le) == 1 ? SkillType.Charge : SkillType.Normal;
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
			mul *= 1.5d;
		double last = 1d;
		if (tc3 == 0)
			last *= 1.708d;
		else if (tc3 == 1)
			last *= 2.501d;
		return (damages1[level - 1] + damages2[level - 1] + damages3[level - 1] * last) * mul;
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
			new Rel(0.25, 0.0, 0.00), new Rel(0.5, 0.0, 0.00), new Rel(0.75, 0.0, 0.00), new Rel(1, 0.0, 0.00),
			new Rel(1.25, 0.0, 0.00), new Rel(1.5, 0.0, 0.00), new Rel(1.75, 0.0, 0.00), new Rel(2, 0.0, 0.00)
			//
	};

	@Override
	public void Charging(LivingEntity att, int times, int charge) {
		if (charge == 2)
			return;
		super.Charging(att, times, charge);
		int tc2 = getTripod2Choice(att);

		int level = getLevel(att);

		Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();

		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 0)
			mul *= 1.5d;

		Rel rel = sting[sting.length - 1];
		Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
		EffectStore.spawnRedStone(now, 255, 165, 0, 1, 80, 0.2, 0.2, 0.2);
		List<Entity> list = new ArrayList<Entity>();
		for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
			damage(list, att, et, damages2[level - 1] / 8 * mul);
	}

	@Override
	public void ChargingFinish(LivingEntity att, int charge) {
		super.ChargingFinish(att, charge);
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
			mul *= 1.5d;
		double[] muls = new double[] { 0.976, 1.525, 2.501 };
		if (tc3 == 1)
			mul *= muls[charge];
		Rel rel = sting[sting.length - 1];
		Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
		EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
		EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
		List<Entity> list = new ArrayList<Entity>();
		for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
			damage(list, att, et, damages3[level - 1] * mul);
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
//		int tc1 = getTripod1Choice(player);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);

		int level = getLevel(le);

		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();

		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 0)
			mul *= 1.5d;

		List<Entity> list = new ArrayList<Entity>();
		final double mul_n = mul;

		if (tc3 == 0) {
			le.setVelocity(new Vector(0, 0.1, 0));
			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(dir);
				}
			}.runTaskLater(Core.getCore(), 2);
			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(new Vector());
					int i = 0;
					for (Rel rel : sting) {
						i++;
						new BukkitRunnable() {
							@Override
							public void run() {
								Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
								Vector dir = loc.getDirection();
								dir.setY(0);
								dir.normalize();
								Location now = loc.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.2, 0.2, 0.2);
								for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
									damage(list, le, et, damages1[level - 1] * mul_n);
							}
						}.runTaskLater(Core.getCore(), i / 3);
					}
					i += 12;
					List<Entity> list2 = new ArrayList<Entity>();
					new BukkitRunnable() {
						@Override
						public void run() {
							Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
							Vector dir = loc.getDirection();
							dir.setY(0);
							dir.normalize();
							Rel rel = sting[sting.length - 1];
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 255, 165, 0, 1, 80, 0.2, 0.2, 0.2);
							for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
								damage(list2, le, et, (damages2[level - 1] + damages3[level - 1] * 1.708d) * mul_n);
						}
					}.runTaskLater(Core.getCore(), i / 3);
				}
			}.runTaskLater(Core.getCore(), 6);
		} else {
			int i = 0;
			for (Rel rel : sting) {
				i++;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0.2, 0.2, 0.2);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
							damage(list, le, et, damages1[level - 1] * mul_n);
					}
				}.runTaskLater(Core.getCore(), i / 3);
			}
			if (tc3 == 1)
				ChargingRunnable.run(le, this, 4, 2, 6);
			else {
				int times = 8;
				for (int c = 0; c < times; c++) {
					i += 15;
					new BukkitRunnable() {
						@Override
						public void run() {
							Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
							Vector dir = loc.getDirection();
							dir.setY(0);
							dir.normalize();
							Rel rel = sting[sting.length - 1];
							Location now = loc.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 255, 165, 0, 1, 80, 0.2, 0.2, 0.2);
							List<Entity> list = new ArrayList<Entity>();
							for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
								damage(list, le, et, damages2[level - 1] / times * mul_n);
						}
					}.runTaskLater(Core.getCore(), i / 3);
				}
				i += 30;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection();
						dir.setY(0);
						dir.normalize();
						Rel rel = sting[sting.length - 1];
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
						EffectStore.spawnRedStone(now, 0, 0, 0, 1, 125, 0.25, 0.25, 0.25);
						EffectStore.spawnRedStone(now, 200, 0, 0, 1, 125, 0.25, 0.25, 0.25);
						List<Entity> list = new ArrayList<Entity>();
						for (Entity et : loc.getWorld().getNearbyEntities(now, 1.5, 1.5, 1.5))
							damage(list, le, et, damages3[level - 1] * mul_n);
					}
				}.runTaskLater(Core.getCore(), i / 3);
			}
		}
		return false;
	}

	@Override
	public int getDestruction() {
		return 1;
	}
}
