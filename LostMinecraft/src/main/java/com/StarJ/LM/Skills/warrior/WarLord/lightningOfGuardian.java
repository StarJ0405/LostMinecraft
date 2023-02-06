package com.StarJ.LM.Skills.warrior.WarLord;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class lightningOfGuardian extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 399, 707, 904, 1061, 1186, 1284, 1369, 1448, 1507, 1567, 1732,
			1896 };

	public lightningOfGuardian() {
		super("lightningOfGuardian", ChatColor.WHITE + "가디언의 낙뢰", Skill.warrior.WarLord.skillGroup.Normal, 20, 133, 45,
				"낙뢰를 떨어뜨려 자신을 중심으로 주변 적에게 피해를 준다.");
		tripodChoice _11 = new tripodChoice("마력 조절", "테스트용");
		tripodChoice _12 = new tripodChoice("식지않는 열기", "테스트용");
		tripodChoice _13 = new tripodChoice("피해 행운", "주변에 랜덤으로 낙뢰를 7개 떨어뜨립니다.", "적중된 적에게 119% 추가 피해를 준다.");
		tripodChoice _21 = new tripodChoice("강인함", "테스트용");
		tripodChoice _22 = new tripodChoice("강인한 일격", "적에게 주는 피해가 50.0% 증가한다.");
		tripodChoice _23 = new tripodChoice("넓은 타격", "테스트용");
		tripodChoice _31 = new tripodChoice("번개 줄기", "낙뢰가 떨어진 후, 십자 방향으로 번개가 뻗어 나가며 80.0% 추가 피해를 준다.");
		tripodChoice _32 = new tripodChoice("감전", "테스트용");
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
		return SkillType.Normal;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 1)
			mul *= 1.5d;
		return damages[level - 1] * mul + (tc1 == 2 ? damages[level - 1] * 1.19d * mul : 0)
				+ (tc3 == 0 ? damages[level - 1] * 0.8d * mul : 0);
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

	Rel[] up = new Rel[] { //
			new Rel(0, 0.5, 0), new Rel(0, 1, 0), new Rel(0, 1.5, 0), new Rel(0, 2, 0), //
			new Rel(0, 2.5, 0), new Rel(0, 3, 0), new Rel(0, 3.5, 0), new Rel(0, 4, 0), //
			new Rel(0, 4.5, 0), new Rel(0, 5, 0), new Rel(0, 5.5, 0), new Rel(0, 6, 0),//
	};
	Rel[] lightning = new Rel[] { //
			new Rel(0, 0.25, 0), new Rel(0, 0.5, 0.5), new Rel(0, 0.75, 1), //
			new Rel(0, 1, 0.5), new Rel(0, 1.25, 0), //
			new Rel(0, 1.5, -0.5), new Rel(0, 1.75, -1), //
			new Rel(0, 2, -0.5), new Rel(0, 2.25, 0),//
	};
	Rel[] plus = new Rel[] { //
			new Rel(2, 0, 0), new Rel(-2, 0, 0), new Rel(0, 0, 2), new Rel(0, 0, -2), //
			new Rel(4, 0, 0), new Rel(-4, 0, 0), new Rel(0, 0, 4), new Rel(0, 0, -4), //
			new Rel(6, 0, 0), new Rel(-6, 0, 0), new Rel(0, 0, 6), new Rel(0, 0, -6), //
			new Rel(8, 0, 0), new Rel(-8, 0, 0), new Rel(0, 0, 8), new Rel(0, 0, -8), //
	};

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
		if (tc2 == 1)
			mul *= 1.5d;
		final double mul_r = mul;
		int i = 0;
		Location ground = le.getLocation();
		for (Rel rel : up) {
			i++;
			new BukkitRunnable() {
				@Override
				public void run() {
					Location now = loc.clone()
							.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
					EffectStore.spawnRedStone(now, 0, 150, 255, 1, 10, 0, 0, 0);
				}
			}.runTaskLater(Core.getCore(), i / 2);
		}
		List<Entity> list = new ArrayList<Entity>();
		new BukkitRunnable() {
			@Override
			public void run() {
				ground.getWorld().strikeLightningEffect(ground);
				for (int i = 0; i < 6; i++) {
					final int j = i;
					new BukkitRunnable() {
						@Override
						public void run() {
							for (Rel rel : circle) {
								Location now = ground.clone().add(EffectStore.getRel(dir, rel.getFront(),
										rel.getHeight(), rel.getRight(), j * 0.5d));
								EffectStore.spawnRedStone(now, 0, 150, 255, 1, 10, 0, 0, 0);
								for (Entity et : ground.getWorld().getNearbyEntities(now, 1, 1, 1))
									damage(list, le, et, damages[level - 1] * mul_r);
							}
						}
					}.runTaskLater(Core.getCore(), i);
				}
			}
		}.runTaskLater(Core.getCore(), i / 2 + 4);

		if (tc1 == 2) {
			List<Entity> light = new ArrayList<Entity>();
			for (int c = 0; c < 7; c++)
				new BukkitRunnable() {
					@Override
					public void run() {
						Random r = new Random();
						Location rand = ground.clone().add(r.nextInt(7) - 3, 0, r.nextInt(7) - 3);
						for (Rel rel : lightning) {
							Location now = rand.clone()
									.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
							EffectStore.spawnRedStone(now, 0, 200, 255, 1, 10, 0, 0, 0);
						}
						for (Entity et : ground.getWorld().getNearbyEntities(rand, 1, 1, 1))
							damage(false, light, le, et, damages[level - 1] * 1.19 * mul_r);
					}
				}.runTaskLater(Core.getCore(), i / 2 + 20);
		}
		if (tc3 == 0)
			new BukkitRunnable() {
				@Override
				public void run() {
					List<Entity> list = new ArrayList<Entity>();
					int i = 0;
					for (Rel rel : plus) {
						new BukkitRunnable() {
							@Override
							public void run() {
								Location now = ground.clone()
										.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
								ground.getWorld().strikeLightningEffect(now);
								for (Entity et : ground.getWorld().getNearbyEntities(now, 1, 1, 1))
									damage(list, le, et, damages[level - 1] * 0.8 * mul_r);
							}
						}.runTaskLater(Core.getCore(), (i / 4) * 5 + 5);
						i++;
					}
					ground.getWorld().strikeLightningEffect(ground);
					for (Entity et : ground.getWorld().getNearbyEntities(ground, 1, 1, 1))
						damage(list, le, et, damages[level - 1] * 0.8 * mul_r);
				}
			}.runTaskLater(Core.getCore(), i / 2 + 6);
		return false;
	}
}
