package com.StarJ.LM.Skills.assassin.Reaper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.Skills.Runnable.ComboCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class nightMare extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 371, 658, 842, 986, 1099, 1196, 1276, 1343, 1400, 1452, 1597,
			1756 };

	public nightMare() {
		super("nightMare", ChatColor.WHITE + "나이트메어", Skill.assassin.Reaper.skillGroup.Dagger, 12, 28, 375,
				AttackType.Back, "마우스 방향으로 단검을 던져 피해를 준다.", "적중시 스킬이 다시 활성화 된다.", "스킬을 다시 한번 입력 시 대상의 반대쪽으로 이동한다.",
				"이동 후 이동속도가 2초 동안 10% 증가한다");
		tripodChoice _11 = new tripodChoice("독:부식", "단검 공격에 '부식 독'을 부여한다.", "부식 독은 8.0초 동안 매 초마다 기본 피해량의 1% 피해를 준다.",
				"최대 3회까지 중첩된다.", "스킬 적중시 12초간 적에게 주는 피해가 12.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("독:출혈", "단검 공격에 '출혈 독'을 부여한다.", "출혈 독은 8.0초 동안 매 초마다 기본 피해량의 1% 피해를 준다.",
				"최대 3중첩이 가능하다", "3중첩 시 '과다 출혈'를 부여한다.", "과다 출혈은 8.0초 동안 매 초마다 기본 피해량의 37% 피해를 준다.");
		tripodChoice _13 = new tripodChoice("독:신경", "테스트용");
		tripodChoice _21 = new tripodChoice("비열한 일격", "테스트용");
		tripodChoice _22 = new tripodChoice("암수", "무조건 치명타가 발생하며, 치명타 피해가 44.0% 증가한다");
		tripodChoice _23 = new tripodChoice("빠른 준비", "재사용 대기시간이 5.0초 감소한다.");
		tripodChoice _31 = new tripodChoice("배후 공격", "이동 시 추가로 주변 3.0m를 내려찍어 기본 피해량의 60.0% 피해를 준다.");
		tripodChoice _32 = new tripodChoice("그림자 감추기", "단검 공격 성공 시 3.0초간 은신 상태가 되며 이동속도가 20.0% 증가한다.");
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
		return SkillType.Chain;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		return damages[level - 1] * mul + (tc3 == 0 ? damages[level - 1] * mul * 0.6 : 0)
				+ (tc1 == 0 || tc1 == 1 ? damages[level - 1] * 0.08 : 0);
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
		return getTripod2Choice(le) == 2 ? 5 : 0;
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (getTripod2Choice(att) == 1) {
			ds.setExtraCriticalChance(this.displayName, 2);
			ds.setExtraCriticalDamage(this.displayName, 0.44);
		}
		boolean end = super.damage(skill, destruction, list, att, vic_e, damage, now);
		ds.setExtraCriticalChance(this.displayName, 0);
		ds.setExtraCriticalDamage(this.displayName, 0);
		return end;

	}

	HashMap<UUID, BukkitTask> overblood = new HashMap<UUID, BukkitTask>();

	@Override
	public void runBlood(LivingEntity att, LivingEntity vic, double damage, int times, int max) {
		if (att == vic)
			return;
		UUID uuid = vic.getUniqueId();
		if (!blood.containsKey(uuid))
			blood.put(uuid, new HashMap<Skill, StackableBukkitTask>());
		HashMap<Skill, StackableBukkitTask> hs = blood.get(uuid);
		if (!hs.containsKey(this))
			hs.put(this, new StackableBukkitTask());
		StackableBukkitTask sbt = hs.get(this);
		int stack = sbt.getStack();
		stack += 1;
		if (stack > max)
			stack = max;
		if (stack == 3) {
			sbt.stop();
			if (overblood.containsKey(uuid))
				overblood.get(uuid).cancel();
			overblood.put(uuid, new tickRunnable(att, vic, damages[getLevel(att) - 1] * 0.37, times, tickType.blood)
					.runTaskTimer(Core.getCore(), 10, 20));
		} else {
			sbt.setTask(new tickRunnable(att, vic, damage * stack, times, tickType.blood).runTaskTimer(Core.getCore(),
					10, 20));
			sbt.setStack(stack);
		}
		return;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void BuffEnd(LivingEntity att, int num) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (num == 0) {
			if (att instanceof Player)
				for (Player other : Bukkit.getOnlinePlayers())
					other.showPlayer(Core.getCore(), (Player) att);
			else
				for (Player other : Bukkit.getOnlinePlayers())
					other.showEntity(Core.getCore(), att);
			att.setInvisible(false);
			ds.setExtraWalkspeed(this.displayName, 0);
		} else if (num == 1)
			ds.setDamageIncrease(this.displayName, 0);
		super.BuffEnd(att, num);
	}

	public static HashMap<LivingEntity, Entity> target = new HashMap<LivingEntity, Entity>();

	@SuppressWarnings("deprecation")
	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		List<Entity> list = new ArrayList<Entity>();
		if (ComboCoolRunnable.hasCombo(le, this)) {
			if (target.containsKey(le)) {
				comboEnd(le);
				Location playerLoc = le.getLocation();
				Entity target = nightMare.target.get(le);
				Location targetLoc = target.getLocation();
				Vector dir = targetLoc.clone().subtract(playerLoc).toVector().setY(0).normalize();
				Location point = targetLoc.clone().add(dir);
				try {
					point.setDirection(dir.clone().multiply(-1));
				} catch (Exception ex) {
				}
				try {
					le.teleport(point);
				} catch (Exception ex) {
				}

				nightMare.target.remove(le);
				if (tc3 == 0)
					damage(list, le, target, damages[level - 1] * 0.6 * mul);
				BuffRunnable.run(le, this, 2, 0);// 이속
				ds.setExtraWalkspeed(this.displayName, 0.1);
				ds.changeSetting(le);
				le.setInvisible(false);
				for (Player other : Bukkit.getOnlinePlayers())
					if (le instanceof Player)
						other.showPlayer(Core.getCore(), (Player) le);
					else
						other.showEntity(Core.getCore(), le);
			}
			comboEnd(le);
		} else {
			if (target.containsKey(le))
				target.remove(le);
			Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
			Vector dir = loc.getDirection();
			new BukkitRunnable() {
				int i = 0;

				@Override
				public void run() {
					if (i > 10) {
						this.cancel();
					} else {
						this.i++;
						Location now = loc.clone().add(dir.clone().multiply(i));
						EffectStore.spawnRedStone(now, 0, 222, 0, 1, 1, 0, 0, 0);
						for (Entity et : now.getWorld().getNearbyEntities(now, 0.5, 0.5, 0.5)) {
							if (damage(list, le, et, damages[level - 1] * mul)) {
								target.put(le, et);
								new BukkitRunnable() {
									@Override
									public void run() {
										ComboCoolRunnable.run(le, nightMare.this, getComboDuration(le));
									}
								}.runTaskLater(Core.getCore(), 10);
								this.cancel();
								if (tc3 == 1) {
									BuffRunnable.run(le, nightMare.this, 3, 0); // 이속
									le.setInvisible(true);
									if (le instanceof Player)
										for (Player other : Bukkit.getOnlinePlayers())
											other.hidePlayer(Core.getCore(), (Player) le);
									else
										for (Player other : Bukkit.getOnlinePlayers())
											other.hideEntity(Core.getCore(), le);
									ds.setExtraWalkspeed(nightMare.this.displayName, 0.2);
									ds.changeSetting(le);
								}

								if (tc1 == 0) {
									runPoison(le, (LivingEntity) et, damages[level - 1] * 0.01, 8, 3);
									BuffRunnable.run(le, nightMare.this, 12, 1); // 피증
									ds.setDamageIncrease(nightMare.this.displayName, 0.12);
								} else if (tc1 == 1)
									runBlood(le, (LivingEntity) et, damages[level - 1] * 0.01, 8, 3);
								return;
							}
						}
					}
				}
			}.runTaskTimer(Core.getCore(), 0, 1);
		}

		return false;
	}
}
