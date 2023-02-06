package com.StarJ.LM.Skills.assassin.Reaper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.DebuffRunnable;
import com.StarJ.LM.Skills.Runnable.HoldingRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class blackMist extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 783, 1390, 1779, 2084, 2326, 2529, 2701, 2848, 2971, 3083, 3391,
			3730 };

	public blackMist() {
		super("blackMist", ChatColor.WHITE + "블랙 미스트", Skill.assassin.Reaper.skillGroup.Shadow, 20, 41, 731,
				"검은 그림자 속에 몸을 감춘 뒤 이동하며 적을 공격한다.", "스킬 시전 중 받는 모든 피해가 20% 감소한다.");
		tripodChoice _11 = new tripodChoice("그림자 충전", "공격 적중 시 어둠 게이지 및 혼돈 게이지 획득량이 45.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("탁월한 기동성", "매 틱마다 이동하는 거리가 100% 증가합니다.");
		tripodChoice _13 = new tripodChoice("넓은 안개", "테스트용");
		tripodChoice _21 = new tripodChoice("안개의 눈", "안개 중심부에 적중 시 적에게 주는 피해가 80.0% 증가한다.");
		tripodChoice _22 = new tripodChoice("강인함", "테스트용");
		tripodChoice _23 = new tripodChoice("퍼지는 안개", "안개에 몸을 숨길 때, 주변에 기본 피해량의 60.0% 추가 피해를 준다.",
				"4초 동안 이동속도를 30.0% 감소시킨다.");
		tripodChoice _31 = new tripodChoice("거짓 안개", "일반 조작으로 변경된다.", "더 이상 직접 리퍼가 이동하지 않고 적을 향해 이동하는 안개를 생성한다.",
				"소환하기 리퍼와 전투중인 대상을 따라간다.", "전투중인 대상이 없는 경우 범위 내 가장 가까운 적을 따라간다.");
		tripodChoice _32 = new tripodChoice("안개 폭발", "홀딩 종료 시 강력한 폭발이 3회 추가된다.", "기본 피해량의 94.8% 추가 피해를 준다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return 14 + (getTripod2Choice(le) == 2 ? 1 : 0);
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? SkillType.Normal : SkillType.Holding;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);

		return damages[level - 1] * mul * (tc2 == 0 ? 1.8d : 1) + (tc2 == 2 ? damages[level - 1] * 0.6 * mul : 0)
				+ (tc3 == 1 ? damages[level - 1] * 0.948 * mul : 0);
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
	public void applyIdentity(LivingEntity att, double addIdentity) {
		if (getTripod1Choice(att) == 0)
			setExtraMultiplyIdentity(att, 0.45d);
		super.applyIdentity(att, addIdentity);
		setExtraMultiplyIdentity(att, 0);
	}

	@Override
	public void HoldingSucceed(LivingEntity att, int times) {
		int tc3 = getTripod3Choice(att);
		if (tc3 == 1) {
			int level = getLevel(att);
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
			double mul = ds.getMultiplyDamage(groupType);

			Location loc = att.getLocation();
			Vector dir = loc.getDirection().clone().setY(0).normalize();
			final int max = 4;
			for (int c = 0; c < 3; c++) {
				List<Entity> list = new ArrayList<Entity>();
				for (int i = 1; i <= max; i++) {
					final int j = i;
					for (Rel rel : circle)
						new BukkitRunnable() {
							@Override
							public void run() {
								Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
										rel.getRight(), 0.5 * j));
								if (j == max)
									EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 2, 0.5))
									damage(list, att, et, damages[level - 1] * 0.948 / 3 * mul);
							}
						}.runTaskLater(Core.getCore(), c * 5 + i);
				}
			}
		}
		super.HoldingSucceed(att, times);
	}

	@Override
	public void HoldingFail(LivingEntity att, int times) {
		int tc3 = getTripod3Choice(att);
		if (tc3 == 1) {
			int level = getLevel(att);
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
			double mul = ds.getMultiplyDamage(groupType);

			Location loc = att.getLocation();
			Vector dir = loc.getDirection().clone().setY(0).normalize();
			final int max = 4;
			for (int c = 0; c < 3; c++) {
				List<Entity> list = new ArrayList<Entity>();
				for (int i = 1; i <= max; i++) {
					final int j = i;
					for (Rel rel : circle)
						new BukkitRunnable() {
							@Override
							public void run() {
								Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
										rel.getRight(), 0.5 * j));
								if (j == max)
									EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 2, 0.5))
									damage(list, att, et, damages[level - 1] * 0.948 / 3 * mul);
							}
						}.runTaskLater(Core.getCore(), c * 5 + i);
				}
			}
		}
		super.HoldingFail(att, times);
	}

	@Override
	public void Holding(LivingEntity att, int times) {
		int tc1 = getTripod1Choice(att);
		int tc2 = getTripod2Choice(att);
//		int tc3 = getTripod3Choice(att);
		int level = getLevel(att);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		double mul = ds.getMultiplyDamage(groupType);

		Location loc = att.getLocation();
		Vector dir = loc.getDirection().clone().setY(0).normalize();
		double speed = tc1 == 1 ? 0.6 : 0.3;
		att.setVelocity(dir.clone().multiply(speed));
		List<Entity> list = new ArrayList<Entity>();
		final int max = 4;
		for (int i = 1; i <= max; i++) {
			final int j = i;
			for (Rel rel : circle)
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 0.5 * j));
						if (j == max)
							EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
						if (tc2 == 0 && j == 1)
							EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 20, 0.3d, 0.3d, 0.3d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 2, 0.5))
							damage(list, att, et, damages[level - 1] / 14 * (tc2 == 0 && j == 1 ? 1.8 : 1) * mul);
					}
				}.runTaskLater(Core.getCore(), i);
		}
		super.Holding(att, times);
	}

	@Override
	public void DebuffEnd(LivingEntity att, LivingEntity vic, int num) {
		if (num == 0)
			DataStore.setWalkSpeed(vic, blackMist.this.displayName, 0);
		super.DebuffEnd(att, vic, num);
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
		double mul = ds.getMultiplyDamage(groupType);

		Location loc = le.getLocation();
		Vector dir = loc.getDirection().clone().setY(0).normalize();
		List<Entity> list = new ArrayList<Entity>();
		final int max = 4;
		for (int i = 1; i <= max; i++) {
			final int j = i;
			for (Rel rel : circle)
				new BukkitRunnable() {

					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 0.5 * j));
						EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
						if (tc2 == 2)
							for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 2, 0.5))
								if (damage(list, le, et, damages[level - 1] * 0.6 * mul)) {
									DebuffRunnable.run(le, (LivingEntity) et, blackMist.this, 4, "퍼지는 안개", 0);
									DataStore.setWalkSpeed((LivingEntity) et, blackMist.this.displayName, -0.3);
								}
					}
				}.runTaskLater(Core.getCore(), i);
		}
		if (tc3 == 0) {
			LivingEntity target = Core.getCore().getHashMapStore().getTarget(le);
			if (target == null)
				for (Entity et : le.getWorld().getNearbyEntities(le.getLocation(), 10, 2, 10, new Predicate<Entity>() {

					@Override
					public boolean test(Entity t) {
						if (t.hasMetadata("owner")) {
							for (MetadataValue mv : t.getMetadata("owner"))
								if (mv.getOwningPlugin().equals(Core.getCore())) {
									UUID uuid = UUID.fromString(mv.asString());
									if (uuid != null) {
										Entity owner = Bukkit.getEntity(uuid);
										if (owner.equals(le))
											return false;
									}
									break;
								}
						}
						return t instanceof LivingEntity && !t.equals(le) && !t.isInvulnerable();
					}
				})) {
					if (target == null)
						target = (LivingEntity) et;
					else {
						Location leLoc = le.getLocation();
						if (leLoc.distance(target.getLocation()) > leLoc.distance(et.getLocation()))
							target = (LivingEntity) et;
					}
				}
			final LivingEntity t = target;
			new BukkitRunnable() {
				int time = 0;
				Location nowLoc = le.getLocation();
				LivingEntity target = t;
				double speed = tc1 == 1 ? 0.15 : 0.075;

				@Override
				public void run() {
					if (target == null) {
						for (Entity et : le.getWorld().getNearbyEntities(le.getLocation(), 10, 2, 10,
								new Predicate<Entity>() {

									@Override
									public boolean test(Entity t) {
										if (t.hasMetadata("owner")) {
											for (MetadataValue mv : t.getMetadata("owner"))
												if (mv.getOwningPlugin().equals(Core.getCore())) {
													UUID uuid = UUID.fromString(mv.asString());
													if (uuid != null) {
														Entity owner = Bukkit.getEntity(uuid);
														if (owner.equals(le))
															return false;
													}
													break;
												}
										}
										return t instanceof LivingEntity && !t.equals(le) && !t.isInvulnerable();
									}
								})) {
							if (target == null)
								target = (LivingEntity) et;
							else {
								Location leLoc = le.getLocation();
								if (leLoc.distance(target.getLocation()) > leLoc.distance(et.getLocation()))
									target = (LivingEntity) et;
							}
						}
					} else {
						Vector dif = target.getLocation().clone().subtract(nowLoc).toVector().normalize()
								.multiply(speed);
						this.nowLoc.add(dif);
					}
					if (this.time % 4 == 0) {
						List<Entity> list = new ArrayList<Entity>();
						for (int i = 1; i <= max; i++) {
							final int j = i;
							for (Rel rel : circle)
								new BukkitRunnable() {
									@Override
									public void run() {
										Location now = nowLoc.clone().add(EffectStore.getRel(dir, rel.getFront(),
												rel.getHeight(), rel.getRight(), 0.5 * j));
										if (j == max)
											EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 10, 0.1d, 0.1d, 0.1d);
										if (tc2 == 0 && j == 1)
											EffectStore.spawnRedStone(now, 148, 0, 211, 0.75f, 20, 0.3d, 0.3d, 0.3d);
										for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 2, 0.5))
											damage(list, le, et,
													damages[level - 1] / 14 * (tc2 == 0 && j == 1 ? 1.8 : 1) * mul);
									}
								}.runTaskLater(Core.getCore(), i);
						}
					}
					this.time++;
					if (this.time == 56)
						this.cancel();
				}
			}.runTaskTimer(Core.getCore(), 0, 1);

		} else {
			new BukkitRunnable() {

				@Override
				public void run() {
					HoldingRunnable.run(le, blackMist.this, 14, 4);
				}
			}.runTaskLater(Core.getCore(), 4);
		}
		return false;
	}
}
