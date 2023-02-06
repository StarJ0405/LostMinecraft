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

public class silentSmasher extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 713, 1266, 1621, 1897, 2116, 2301, 2455, 2586, 2696, 2797, 3076,
			3384 };

	public silentSmasher() {
		super("silentSmasher", ChatColor.WHITE + "사일런트 스매셔", Skill.assassin.Reaper.skillGroup.SurpiseAttack, 25, 76, 0,
				AttackType.Back, "뒤로 점프를 뛰어 공중에서 어둠의 기운을 모은다.", "전방으로 빠르게 이동하며 베어 강력한 피해를 준다.");
		tripodChoice _11 = new tripodChoice("넓은 공격", "테스트용");
		tripodChoice _12 = new tripodChoice("가벼운 동작", "테스트용");
		tripodChoice _13 = new tripodChoice("빠른 준비", "재사용 대기시간이 9.0초 감소한다");
		tripodChoice _21 = new tripodChoice("급선회", "테스트용");
		tripodChoice _22 = new tripodChoice("급하강", "테스트용");
		tripodChoice _23 = new tripodChoice("그림자 잔상", "그림자 분신 공격이 3회 추가된다.", "기본 피해량의 60.0% 피해를 준다.");
		tripodChoice _31 = new tripodChoice("지면 강타", "리퍼가 더욱 강하게 하강한다.", "적에게 주는 피해가 95.0% 증가한다.");
		tripodChoice _32 = new tripodChoice("압살", "테스트용");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod1Choice(le) == 2 ? 9 : 0;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 2)
			mul *= 1.6d;
		if (tc3 == 0)
			mul *= 1.95d;
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
	public boolean Use(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType) * (tc3 == 0 ? 1.95d : 1);
		if (super.Use(le))
			return true;
		le.setVelocity(new Vector(0, 0.2, 0));
		Core.getCore().getHashMapStore().addPventFallDamage(le, true);
		new BukkitRunnable() {

			@Override
			public void run() {
				Vector dir = le.getLocation().getDirection().clone().setY(0).normalize().multiply(-0.5).setY(0.75);
				le.setVelocity(dir);
			}
		}.runTaskLater(Core.getCore(), 2);
		Location point = getPointBlock(le.getEyeLocation(), 15);
		new BukkitRunnable() {

			@Override
			public void run() {

				Location loc = le.getEyeLocation();
				Vector dir = point.clone().subtract(loc).toVector().normalize().multiply(0.5d);
				int l = (int) Math.ceil(loc.distance(point) * 2);
				int delay = 1;
				if (tc2 == 2) {
					int c = 0;
					for (; c < 3; c++) {
						List<Entity> list = new ArrayList<Entity>();
						new BukkitRunnable() {

							@Override
							public void run() {
								le.teleport(loc);
								for (int i = 0; i <= l; i++) {
									Location now = loc.clone().add(dir.clone().multiply(i));
									EffectStore.spawnRedStone(now, 128, 0, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
									for (Entity et : now.getWorld().getNearbyEntities(now, 1, 3, 1))
										damage(list, le, et, damages[level - 1] * 0.2 * mul);
								}
							}
						}.runTaskLater(Core.getCore(), c * 3);
					}
					c++;
					new BukkitRunnable() {

						@Override
						public void run() {
							List<Entity> list = new ArrayList<Entity>();
							for (int i = 0; i <= l; i++) {
								Location now = loc.clone().add(dir.clone().multiply(i));
								EffectStore.spawnRedStone(now, 128, 0, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
								for (Entity et : now.getWorld().getNearbyEntities(now, 1, 3, 1))
									damage(list, le, et, damages[level - 1] * mul);
							}
						}
					}.runTaskLater(Core.getCore(), c * 3);

					delay += c * 3;
				} else {
					List<Entity> list = new ArrayList<Entity>();
					for (int i = 0; i <= l; i++) {
						Location now = loc.clone().add(dir.clone().multiply(i));
						EffectStore.spawnRedStone(now, 128, 0, 0, 0.75f, 10, 0.1d, 0.1d, 0.1d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 3, 1))
							damage(list, le, et, damages[level - 1] * mul);
					}
				}

				new BukkitRunnable() {

					@Override
					public void run() {
						le.teleport(point);
					}
				}.runTaskLater(Core.getCore(), delay);
				new BukkitRunnable() {
					int time = 0;

					@Override
					public void run() {
						if (le.isOnGround())
							Core.getCore().getHashMapStore().addPventFallDamage(le, false);
						this.time++;
						if (time > 20 * 60 * 1)
							this.cancel();
					}
				}.runTaskTimer(Core.getCore(), delay + 1, 1);
			}
		}.runTaskLater(Core.getCore(), 14);
		return false;
	}
}
