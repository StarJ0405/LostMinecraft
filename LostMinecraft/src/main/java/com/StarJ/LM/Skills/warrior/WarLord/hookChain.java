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
import com.StarJ.LM.Skills.Runnable.DebuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class hookChain extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 76, 133, 170, 200, 224, 243, 258, 273, 284, 295, 324, 356 };
	protected final double[] damages2 = new double[] { 86, 153, 195, 228, 255, 276, 294, 311, 323, 335, 368, 405 };

	public hookChain() {
		super("hookChain", ChatColor.WHITE + "갈고리 사슬", Skill.warrior.WarLord.skillGroup.Normal, 24, 29, 60,
				"갈고리 사슬을 길게 뻗어 피해를 준다.", "갈고리를 앞으로 끌어오며 피해를 준다.");
		tripodChoice _11 = new tripodChoice("빠른 준비", "재사용 대기시간이 5.0초 감소한다");
		tripodChoice _12 = new tripodChoice("마력 조절", "테스트용");
		tripodChoice _13 = new tripodChoice("강화된 일격", "적에게 주는 피해가 35.0% 증가한다.");
		tripodChoice _21 = new tripodChoice("날카로운 사슬", "적중된 적을 5.0초 동안 출혈 상태로 만든다.", "매 초마다 기본 피해량의 27% 피해를 준다.");
		tripodChoice _22 = new tripodChoice("족쇄", "적중된 적의 이동속도를 6.0초간 57.6% 감소시킨다.");
		tripodChoice _23 = new tripodChoice("공격 준비", "공격 적중 시 자신의 공격력이 5.0초간 33.5% 증가한다.");
		tripodChoice _31 = new tripodChoice("넓은 타격", "테스트용");
		tripodChoice _32 = new tripodChoice("도발", "테스트용");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return 2;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
//		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc1 == 2)
			mul *= 1.35d;

		return (damages1[level - 1] + damages2[level - 1]) * mul
				+ (tc2 == 0 ? (damages1[level - 1] + damages2[level - 1]) * 1.89d : 0);
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod1Choice(le) == 0 ? 5 : 0;
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

	Rel[] chain = new Rel[] { //
			new Rel(0.5, 0, 0.15), new Rel(0.5, 0, -0.15), new Rel(0.5, 0, 0.5), new Rel(0.5, 0, -0.5), //
			new Rel(1, 0, 0.3), new Rel(1, 0, -0.3), new Rel(1, 0, 1), new Rel(1, 0, -1), //
			new Rel(1.5, 0, 0.45), new Rel(1.5, 0, -0.45), new Rel(1.5, 0, 1.5), new Rel(1.5, 0, -1.5), //
			new Rel(2, 0, 0.6), new Rel(2, 0, -0.6), new Rel(2, 0, 2), new Rel(2, 0, -2), //
			new Rel(2.5, 0, 0.75), new Rel(2.5, 0, -0.75), new Rel(2.5, 0, 2.5), new Rel(2.5, 0, -2.5), //
			new Rel(3, 0, 0.9), new Rel(3, 0, -0.9), new Rel(3, 0, 3), new Rel(3, 0, -3), //
			new Rel(3.5, 0, 1.05), new Rel(3.5, 0, -1.05), new Rel(3.5, 0, 3.5), new Rel(3.5, 0, -3.5), //
			new Rel(4, 0, 1.2), new Rel(4, 0, -1.2), new Rel(4, 0, 4), new Rel(4, 0, -4), //
			new Rel(4.5, 0, 1.35), new Rel(4.5, 0, -1.35),//
	};
	Rel[] back = new Rel[] { //
			new Rel(4.5, 0, 1.35), new Rel(4.5, 0, -1.35), //
			new Rel(4, 0, 1.2), new Rel(4, 0, -1.2), new Rel(4, 0, 4), new Rel(4, 0, -4), //
			new Rel(3.5, 0, 1.05), new Rel(3.5, 0, -1.05), new Rel(3.5, 0, 3.5), new Rel(3.5, 0, -3.5), //
			new Rel(3, 0, 0.9), new Rel(3, 0, -0.9), new Rel(3, 0, 3), new Rel(3, 0, -3), //
			new Rel(2.5, 0, 0.75), new Rel(2.5, 0, -0.75), new Rel(2.5, 0, 2.5), new Rel(2.5, 0, -2.5), //
			new Rel(2, 0, 0.6), new Rel(2, 0, -0.6), new Rel(2, 0, 2), new Rel(2, 0, -2), //
			new Rel(1.5, 0, 0.45), new Rel(1.5, 0, -0.45), new Rel(1.5, 0, 1.5), new Rel(1.5, 0, -1.5), //
			new Rel(1, 0, 0.3), new Rel(1, 0, -0.3), new Rel(1, 0, 1), new Rel(1, 0, -1), //
			new Rel(0.5, 0, 0.15), new Rel(0.5, 0, -0.15), new Rel(0.5, 0, 0.5), new Rel(0.5, 0, -0.5), //
	};

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		super.BuffEnd(att, num);
		if (num == 0)
			Core.getCore().getHashMapStore().getDataStore(att).setPower(this.displayName, 0);
	}

	@Override
	public void DebuffEnd(LivingEntity att, LivingEntity vic, int num) {
		super.DebuffEnd(att, vic, num);
		if (getTripod2Choice(att) == 1) {
			DataStore.setWalkSpeed(vic, this.displayName, 0);
			DataStore.applyWalkspeed(vic);
		}
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!super.damage(skill, destruction, list, att, vic_e, damage, now))
			return false;
		if (skill) {
			int tc2 = getTripod2Choice(att);
			if (tc2 == 0) {
				int level = getLevel(att);
				runBlood(att, (LivingEntity) vic_e, (damages1[level - 1] + damages2[level - 1]) * 0.27, 5);
			} else if (tc2 == 1) {
				LivingEntity vic = (LivingEntity) vic_e;
				DebuffRunnable.run(att, vic, this, 6, "족쇄", 0);
				DataStore.setWalkSpeed(vic, this.displayName, -0.576);
				DataStore.applyWalkspeed(vic);
			}
		}
		return true;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
//		int tc3 = getTripod3Choice(player);
		int level = getLevel(le);

		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		if (tc2 == 2) {
			BuffRunnable.run(le, this, 5, 0);
			ds.setPower(this.displayName, 0.335);
		}

		double mul = ds.getMultiplyDamage(groupType);
		if (tc1 == 2)
			mul *= 1.35d;

		final double mul_r = mul;
		int i = 0;
		List<Entity> list = new ArrayList<Entity>();
		for (Rel rel : chain) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
					Vector dir = loc.getDirection();
					dir.setY(0);
					dir.normalize();
					Location now = loc.clone()
							.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
					EffectStore.spawnRedStone(now, 0, 0, 0, 1, 10, 0, 0, 0);
					for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
						damage(list, le, et, damages1[level - 1] * mul_r);

				}
			}.runTaskLater(Core.getCore(), (i / 4) * 2);
			i++;
		}
		i += 16;
		List<Entity> list2 = new ArrayList<Entity>();
		for (Rel rel : back) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
					Vector dir = loc.getDirection();
					dir.setY(0);
					dir.normalize();
					Location now = loc.clone()
							.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight()));
					EffectStore.spawnRedStone(now, 50, 50, 50, 1, 10, 0, 0, 0);
					for (Entity et : now.getWorld().getNearbyEntities(now, 1, 1, 1))
						damage(list2, le, et, damages2[level - 1] * mul_r);
				}
			}.runTaskLater(Core.getCore(), (i / 4) * 2);
			i++;
		}
		return false;
	}
}
