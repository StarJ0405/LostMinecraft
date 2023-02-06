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
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class counterSpear extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;

	protected final double[] damages = new double[] { 563, 997, 1274, 1495, 1671, 1810, 1931, 2042, 2125, 2208, 2428,
			2671 };

	public counterSpear() {
		super("counterSpear", ChatColor.WHITE + "카운터 스피어", Skill.warrior.WarLord.skillGroup.Lance, 20, 122, 120,
				AttackType.Head, "방패로 전방을 2초간 방어한다.", "방어 중 보호막이 생긴다.", "반격에 성공하거나, 시간이 지나면 적을 찌른다.");
		tripodChoice _11 = new tripodChoice("공격 준비", "반격 성공시 자신의 공격력이 4.0초간 22.9% 증가한다.");
		tripodChoice _12 = new tripodChoice("단단한 갑옷", "테스트용");
		tripodChoice _13 = new tripodChoice("뇌진탕", "테스트용");
		tripodChoice _21 = new tripodChoice("빠른 준비", "재사용 대기시간이 6.0초 감소한다.");
		tripodChoice _22 = new tripodChoice("강화된 일격", "적에게 주는 피해가 50.0% 증가한다.");
		tripodChoice _23 = new tripodChoice("약점 포착", "테스트용");
		tripodChoice _31 = new tripodChoice("폭격창", "반격 성공 시 창격과 동시에 포격을 가하여 138.5% 증가된 피해를 준다.");
		tripodChoice _32 = new tripodChoice("뇌격창", "테스트용");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
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
		if (tc2 == 1)
			mul *= 1.5d;
		if (tc3 == 0)
			mul *= 2.385d;
		return damages[level - 1] * mul;
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		int tc2 = getTripod2Choice(le);
		return tc2 == 0 ? 6 : 0;
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

	private Rel[] shield = new Rel[] { //
			new Rel(1, 1, -0.5), new Rel(1, 1, 0.5), //
			new Rel(1, 0.5, 0), new Rel(1, 0.5, -0.5), new Rel(1, 0.5, 0.5), //
			new Rel(1, 0, 0), new Rel(1, 0, -0.5), new Rel(1, 0, 0.5), //
			new Rel(1, -0.5, 0), new Rel(1, -0.5, -0.5), new Rel(1, -0.5, 0.5), //
			new Rel(1, -1, 0),//
			//
	};
	private Rel[] sting = new Rel[] { //
			new Rel(0.25, 0.0, 0.00), new Rel(0.5, 0.0, 0.00), new Rel(0.75, 0.0, 0.00), new Rel(1, 0.0, 0.00), //
			new Rel(1.25, 0.0, 0.00), new Rel(1.5, 0.0, 0.00), new Rel(1.75, 0.0, 0.00), new Rel(2, 0.0, 0.00), //
			new Rel(2.25, 0.0, 0.00), new Rel(2.5, 0.0, 0.00)
			//
	};

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		ds.addAttackedList(le, this);
		ds.setAbsorption(this.displayName, ds.getMaxHealth());
		ds.confirmHealthPercent(le);

		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();

		for (Rel rel : shield) {
			Location now = loc.clone()
					.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.2));
			EffectStore.spawnRedStone(now, 0, 0, 2, 1, 10, 0.1, 0.1, 0.1);
		}
		BuffRunnable.run(le, this, 2, 1); // 쉴드량
		BuffRunnable.run(le, this, 2, 2); // 반격 지속시간
		BuffRunnable.run(le, this, 2, 3); // 반격 실패시 공격
		return false;
	}

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (num == 0)
			ds.setPower(this.displayName, 0);
		else if (num == 1) {
			ds.setAbsorption(counterSpear.this.displayName, 0);
			ds.confirmHealthPercent(att);
		} else if (num == 2)
			ds.removeAttackedList(att, counterSpear.this);
		else if (num == 3)
			end(att, false);
		super.BuffEnd(att, num);
	}

	@Override
	public double Attacked(LivingEntity vic, LivingEntity att, double damage) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(vic);
		int tc1 = getTripod1Choice(vic);
		if (tc1 == 0) {
			BuffRunnable.run(vic, this, 4, 0); // 공증
			ds.setPower(this.displayName, 0.229);
			ds.changeSetting(vic);
		}
		ds.removeAttackedList(vic, counterSpear.this);
		BuffRunnable.cancel(vic, this, 3, false);
		BuffRunnable.cancel(vic, this, 2, true);

		end(vic, true);
		return super.Attacked(vic, att, damage);
	}

	public void end(LivingEntity att, boolean counter) {
//		int tc1 = getTripod1Choice(att);
		int tc2 = getTripod2Choice(att);
		int tc3 = getTripod3Choice(att);
		int level = getLevel(att);

		Location loc = att.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();

		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc2 == 1)
			mul *= 1.5d;
		if (counter && tc3 == 0)
			mul *= 2.385d;
		int i = 0;
		final double mul_n = mul;
		List<Entity> list = new ArrayList<Entity>();
		for (Rel rel : sting) {
			i++;
			new BukkitRunnable() {
				@Override
				public void run() {
					Location now = loc.clone()
							.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.2));
					EffectStore.spawnRedStone(now, 200, 200, 0, 1, 10, 0.2, 0.2, 0.2);
					if (counter && tc3 == 0) {
						EffectStore.spawnRedStone(now, 200, 0, 00, 1, 150, 0.2, 0.2, 0.2);
						EffectStore.spawnRedStone(now, 0, 0, 00, 1, 150, 0.2, 0.2, 0.2);
					}
					for (Entity et : loc.getWorld().getNearbyEntities(now, 0.75, 0.75, 0.75))
						damage(list, att, et, damages[level - 1] * mul_n);
				}
			}.runTaskLater(Core.getCore(), i / 3);
		}
	}

	@Override
	public int getDestruction() {
		return 1;
	}
}
