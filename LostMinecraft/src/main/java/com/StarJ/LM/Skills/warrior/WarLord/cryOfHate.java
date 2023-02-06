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
import com.StarJ.LM.System.DataStore.ActionBarRunnable;
import com.StarJ.LM.System.EffectStore;

public class cryOfHate extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 207, 366, 467, 549, 613, 664, 709, 750, 780, 810, 891, 980 };

	public cryOfHate() {
		super("cryOfHate", ChatColor.WHITE + "증오의 함성", Skill.warrior.WarLord.skillGroup.Normal, 30, 21, 45,
				"강하게 포효하여 자신을 중심으로 적에게 피해를 준다.");
		tripodChoice _11 = new tripodChoice("빠른 준비", "재사용 대기시간이 6.0초 감소한다.");
		tripodChoice _12 = new tripodChoice("넓은 타격", "공격 범위가 증가한다.");
		tripodChoice _13 = new tripodChoice("방어 준비", "자신이 받는 모든 피해가 6.0초간 30.0% 감소한다.");
		tripodChoice _21 = new tripodChoice("도발 유지", "테스트용");
		tripodChoice _22 = new tripodChoice("약육강식", "테스트용");
		tripodChoice _23 = new tripodChoice("보호막", "4.0초간 자신의 최대 생명력의 41.0% 만큼 보호막을 생성한다.");
		tripodChoice _31 = new tripodChoice("약점 노출", "공격 적중 시 실드 게이지를 20.0% 회복한다.",
				"12.0초간 약점을 노출시켜 대상이 자신에게 받는 피해를 4.0% 증가시킨다.", "헤드 어택 및 백 어택의 경우, 받는 피해 효과가 추가로 5.0% 증가한다.");
		tripodChoice _32 = new tripodChoice("고함", "테스트용");
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
//		int tc2 = getTripod2Choice(le);
//		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);

		return damages[level - 1] * mul;
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod1Choice(le) == 0 ? 6 : 0;
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
	public void BuffEnd(LivingEntity att, int num) {
		super.BuffEnd(att, num);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (num == 0)
			ds.setReduceDamage(this.displayName, 0);
		else if (num == 1) {
			ds.setAbsorption(this.displayName, 0);
			ds.confirmHealthPercent(att);
			ActionBarRunnable.run(att);
		} else if (num == 2)
			ds.setAttackTypeDamage(this.displayName, 0);
	}

	@Override
	public void DebuffEnd(LivingEntity att, LivingEntity vic, int num) {
		super.DebuffEnd(att, vic, num);

	}

	@Override
	public void applyIdentity(LivingEntity att) {
		super.applyIdentity(att);
		if (getTripod3Choice(att) == 0)
			applyIdentity(att, Core.getCore().getHashMapStore().getDataStore(att).getIdentity().getMax() * 0.2);
	}

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

		double range = 1d;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		if (tc1 == 1)
			range = 1.5d;
		else if (tc1 == 2) {
			ds.setReduceDamage(this.displayName, 0.3);
			BuffRunnable.run(le, this, 6, 0);// 피감
		}
		if (tc2 == 2) {
			ds.setAbsorption(this.displayName, ds.getMaxHealth() * 0.41);
			BuffRunnable.run(le, this, 4, 1);// 보호막
			ds.confirmHealthPercent(le);
			ActionBarRunnable.run(le);
		}
		if (tc3 == 0) {
			BuffRunnable.run(le, this, 12, 2); // 백헤드
			ds.setAttackTypeDamage(this.displayName, 0.09);
		}
		double mul = ds.getMultiplyDamage(groupType);
		List<Entity> list = new ArrayList<Entity>();

		final double mul_r = mul;
		final double range_r = range;
		for (int i = 0; i < 8; i++) {
			final double j = i;
			new BukkitRunnable() {
				@Override
				public void run() {
					Location loc = le.getLocation();
					Vector dir = loc.getDirection();
					dir.setY(0);
					dir.normalize();
					for (Rel rel : circle) {
						Location now = loc.clone().add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(),
								rel.getRight(), j * range_r * 0.5));
						EffectStore.spawnRedStone(now, 0, 200, 255, 1, 10, 0, 0, 0);
						for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
							damage(list, le, et, damages[level - 1] * mul_r);

					}
				}
			}.runTaskLater(Core.getCore(), i);
		}

		return false;
	}
}
