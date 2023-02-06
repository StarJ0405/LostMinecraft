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
import com.StarJ.LM.Skills.Runnable.ComboCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class sharpSpear extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 158, 281, 358, 421, 470, 509, 542, 573, 597, 620, 682, 750 };

	public sharpSpear() {
		super("sharpSpear", ChatColor.WHITE + "날카로운 창", Skill.warrior.WarLord.skillGroup.Lance, 5, 59, 39,
				AttackType.Head, "창을 힘차게 앞으로 내질러 피해를 준다.");
		tripodChoice _11 = new tripodChoice("진격", "전진한 후 부채꼴 모양으로 공격한다.");
		tripodChoice _12 = new tripodChoice("관통", "찌르는 공격의 폭이 좁아진다.", "대신 더 멀리 찌른다.");
		tripodChoice _13 = new tripodChoice("대회전", "창을 머리 위로 크게 휘둘러 360도 공격을 한다.");
		tripodChoice _21 = new tripodChoice("상처", "처음 적중된 적을 4.0초 동안 출혈 상태로 만든다.", "매 초마다 기본 피해량의 20%의 추가 피해를 준다.");
		tripodChoice _22 = new tripodChoice("무방비 표적", "테스트용");
		tripodChoice _23 = new tripodChoice("변칙 공격", "테스트용");
		tripodChoice _31 = new tripodChoice("멈추지 않는 창격", "콤보 조작으로 변경된다.", "두 번째 공격은 145.0%의 피해를 입힌다.");
		tripodChoice _32 = new tripodChoice("창격 강화", "더 멀리 공격한다.", "적에게 주는 피해가 80.0% 증가된다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? 2 : 1;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc3 == 0)
			mul *= 2.45d;
		else if (tc3 == 1)
			mul *= 1.8d;

		return damages[level - 1] * mul + (tc2 == 0 ? damages[level - 1] * 0.8 : 0);
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

	private Rel[] sting_normal = new Rel[] { //
			new Rel(0.25, 0.0, 0.15), new Rel(0.25, 0.0, -0.15), //
			new Rel(0.5, 0.0, 0.15), new Rel(0.5, 0.0, -0.15), //
			new Rel(0.75, 0.0, 0.15), new Rel(0.75, 0.0, -0.15), //
			new Rel(1, 0.0, 0.15), new Rel(1, 0.0, -0.15), //
			new Rel(1.25, 0.0, 0.15), new Rel(1.25, 0.0, -0.15), //
			new Rel(1.5, 0.0, 0.15), new Rel(1.5, 0.0, -0.15), //
			new Rel(1.75, 0.0, 0.15), new Rel(1.75, 0.0, -0.15), //
			new Rel(2, 0.0, 0.15), new Rel(2, 0.0, -0.15), //
			new Rel(2.25, 0.0, 0.15), new Rel(2.25, 0.0, -0.15), //
			new Rel(2.5, 0.0, 0.15), new Rel(2.5, 0.0, -0.15),//
	};
	private Rel[] sting_long = new Rel[] { //
			new Rel(0.25, 0, 0), new Rel(0.5, 0, 0), new Rel(0.75, 0, 0), new Rel(1, 0, 0), //
			new Rel(1.25, 0, 0), new Rel(1.5, 0, 0), new Rel(1.75, 0, 0), new Rel(2, 0, 0), //
			new Rel(2.25, 0, 0), new Rel(2.5, 0, 0), new Rel(2.75, 0, 0), new Rel(3, 0, 0), //
			new Rel(3.25, 0, 0), new Rel(3.5, 0, 0), new Rel(3.75, 0, 0), new Rel(4, 0, 0)//
	};
	private Rel[] wide = new Rel[] { //
			new Rel(0.25, 0.0, 0), new Rel(0.25, 0.0, 0.2), new Rel(0.25, 0.0, -0.2), //
			new Rel(0.5, 0.0, 0), new Rel(0.5, 0.0, 0.4), new Rel(0.5, 0.0, -0.4), //
			new Rel(0.75, 0.0, 0), new Rel(0.75, 0.0, 0.6), new Rel(0.75, 0.0, -0.6), //
			new Rel(1, 0.0, 0), new Rel(1, 0.0, 0.8), new Rel(1, 0.0, -0.8), //
			new Rel(1.25, 0.0, 0), new Rel(1.25, 0.0, 1), new Rel(1.25, 0.0, -1), //
			new Rel(1.5, 0.0, 0), new Rel(1.5, 0.0, 1.2), new Rel(1.5, 0.0, -1.2), //
			new Rel(1.75, 0.0, 0), new Rel(1.75, 0.0, 1.4), new Rel(1.75, 0.0, -1.4), //
			new Rel(2, 0.0, 0), new Rel(2, 0.0, 1.6), new Rel(2, 0.0, -1.6), //
			new Rel(2.25, 0.0, 0), new Rel(2.25, 0.0, 1.8), new Rel(2.25, 0.0, -1.8), //
			new Rel(2.5, 0.0, 0), new Rel(2.5, 0.0, 2), new Rel(2.5, 0.0, -2),//
	};
//	private Rel[] circle = new Rel[] { //
//			new Rel(0, 0, 1), new Rel(0.25, 0, Math.sqrt(1 - 0.25 * 0.25)), //
//			new Rel(0.5, 0, Math.sqrt(1 - 0.5 * 0.5)), new Rel(0.75, 0, Math.sqrt(1 - 0.75 * 0.75)), //
//			//
//			new Rel(1, 0, 0), new Rel(0.75, 0, -Math.sqrt(1 - 0.75 * 0.75)), //
//			new Rel(0.5, 0, -Math.sqrt(1 - 0.5 * 0.5)), new Rel(0.25, 0, -Math.sqrt(1 - 0.25 * 0.25)), //
//			//
//			new Rel(0, 0, -1), new Rel(-0.25, 0, -Math.sqrt(1 - 0.25 * 0.25)), //
//			new Rel(-0.5, 0, -Math.sqrt(1 - 0.5 * 0.5)), new Rel(-0.75, 0, -Math.sqrt(1 - 0.75 * 0.75)), //
//			//
//			new Rel(-1, 0, 0), new Rel(-0.75, 0, Math.sqrt(1 - 0.75 * 0.75)), //
//			new Rel(-0.5, 0, Math.sqrt(1 - 0.5 * 0.5)), new Rel(-0.25, 0, Math.sqrt(1 - 0.25 * 0.25)), //
//	};
	private Rel[] circle = new Rel[] { //
			new Rel(2, 0, 0), new Rel(1.984, 0, 0.25), new Rel(1.936, 0, 0.5), //
			new Rel(1.854, 0, 0.75), new Rel(1.732, 0, 1), new Rel(1.561, 0, 1.25), //
			new Rel(1.323, 0, 1.5), new Rel(0.968, 0, 1.75), new Rel(0.75, 0, 1.854), //
			new Rel(0.5, 0, 1.936), new Rel(0.25, 0, 1.984),
			////
			new Rel(0, 0, 2), new Rel(-0.25, 0, 1.984), new Rel(-0.5, 0, 1.936), //
			new Rel(-0.75, 0, 1.854), new Rel(-0.968, 0, 1.75), new Rel(-1.323, 0, 1.5), //
			new Rel(-1.561, 0, 1.25), new Rel(-1.732, 0, 1), new Rel(-1.854, 0, 0.75), //
			new Rel(-1.936, 0, 0.5), new Rel(-1.984, 0, 0.25), //
			////
			new Rel(-2, 0, 0), new Rel(-1.984, 0, -0.25), new Rel(-1.936, 0, -0.5), //
			new Rel(-1.854, 0, -0.75), new Rel(-1.732, 0, -1), new Rel(-1.561, 0, -1.25), //
			new Rel(-1.323, 0, -1.5), new Rel(-0.968, 0, -1.75), new Rel(0.75, 0, -1.854), //
			new Rel(-0.5, 0, -1.936), new Rel(-0.25, 0, -1.984), //
			////
			new Rel(0, 0, -2), new Rel(0.25, 0, -1.984), new Rel(0.5, 0, -1.936), //
			new Rel(-0.75, 0, -1.854), new Rel(0.968, 0, -1.75), new Rel(1.323, 0, -1.5), //
			new Rel(1.561, 0, -1.25), new Rel(1.732, 0, -1), new Rel(1.854, 0, -0.75), //
			new Rel(1.936, 0, -0.5), new Rel(1.984, 0, -0.25), //
	};

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!super.damage(skill, destruction, list, att, vic_e, damage, now))
			return false;
		if (skill && getTripod2Choice(att) == 0)
			runBlood(att, (LivingEntity) vic_e, damages[getLevel(att) - 1] * 0.2, 4);
		return true;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(player);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);

		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();

		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		double range = tc3 == 1 ? 1.3 : 1;
		if (tc3 == 1)
			mul *= 1.8d;
		else if (tc3 == 0)
			if (ComboCoolRunnable.hasCombo(le, this)) {
				mul *= 1.45d;
				comboEnd(le);
			} else
				ComboCoolRunnable.run(le, this, getComboDuration(le));
		List<Entity> list = new ArrayList<Entity>();
		list.add(le);
		if (tc1 == 0) {
			le.setVelocity(new Vector(0, 0.1, 0));
			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(dir);
				}
			}.runTaskLater(Core.getCore(), 2);
			final double mul_r = mul;
			new BukkitRunnable() {
				@Override
				public void run() {
					le.setVelocity(new Vector());
					Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
					Vector dir = loc.getDirection();
					dir.setY(0);
					dir.normalize();
					int i = 0;
					for (Rel rel : wide) {
						i++;
						new BukkitRunnable() {
							@Override
							public void run() {
								Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
										rel.getRight(), range));
								EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
								for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
									damage(list, le, et, damages[level - 1] * mul_r);
							}
						}.runTaskLater(Core.getCore(), i / 5);
					}
				}
			}.runTaskLater(Core.getCore(), 6);
		} else if (tc1 == 2) {
			int i = 0;
			final double mul_c = mul;
			for (Rel rel : circle) {
				i++;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), range));
						EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
							damage(list, le, et, damages[level - 1] * mul_c);
					}
				}.runTaskLater(Core.getCore(), i / 5);
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone().add(
								EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), range * 0.75));
						EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
							damage(list, le, et, damages[level - 1] * mul_c);
					}
				}.runTaskLater(Core.getCore(), i / 5);
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone().add(
								EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), range * 0.5));
						EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
							damage(list, le, et, damages[level - 1] * mul_c);
					}
				}.runTaskLater(Core.getCore(), i / 5);
			}
		} else {
			int i = 0;
			final double mul_n = mul;
			Rel[] now = sting_normal;
			if (tc1 == 1)
				now = sting_long;
			for (Rel rel : now) {
				i++;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), range));
						EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
							damage(list, le, et, damages[level - 1] * mul_n);
					}
				}.runTaskLater(Core.getCore(), i / (tc1 == 1 ? 4 : 3));
			}
		}
		return false;
	}
}
