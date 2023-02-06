package com.StarJ.LM.Skills.warrior.WarLord;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.DataStore.ActionBarRunnable;
import com.StarJ.LM.System.EffectStore;

public class auraOfNelasia extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] reduce = new double[] { 0.1, 0.11, 0.12, 0.13, 0.15, 0.17, 0.19, 0.21, 0.23, 0.25 };

	public auraOfNelasia() {
		super("auraOfNelasia", ChatColor.WHITE + "넬라시아의 기운", 10, Skill.warrior.WarLord.skillGroup.Normal, 40,
				"강하게 포효하여 자신 받는 모든 피해를 6초간 감소시킨다.");
		tripodChoice _11 = new tripodChoice("가벼운 발걸음", "자신의 이동속도가 8.0초간 24.6% 증가한다.");
		tripodChoice _12 = new tripodChoice("마력 조절", "테스트용");
		tripodChoice _13 = new tripodChoice("빠른 준비", "재사용 대기시간이 8.0초 감소한다.");
		tripodChoice _21 = new tripodChoice("효과 증대", "넬라시아의 기운으로 감소되는 피해가 35.0% 증가된다.");
		tripodChoice _22 = new tripodChoice("정화의 함성", "테스트용");
		tripodChoice _23 = new tripodChoice("효과 유지", "넬라시아의 기운으로 인하여 받는 모든 지속시간이 6.0초 증가한다.");
		tripodChoice _31 = new tripodChoice("리더쉽", "테스트용");
		tripodChoice _32 = new tripodChoice("생존", "6.0초간 자신의 최대 생명력의 32.0%에 해당하는 보호막을 생성한다.");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public List<String> getLore(LivingEntity le) {
		List<String> lore = super.getLore(le);
		for (int i = 0; i < lore.size(); i++) {
			String l = lore.get(i);
			if (l.contains("총 피해량")) {
				lore.set(i, ChatColor.GREEN + "피해 감소량 : " + Math.round(getTotalDamage(le) * 100) + "%");
				break;
			}
		}
		return lore;
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod1Choice(le) == 2 ? 8d : 0;
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int level = getLevel(le);
		int tc2 = getTripod2Choice(le);
		return reduce[level - 1] + (tc2 == 0 ? 0.35 : 0);
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
		super.BuffEnd(att, num);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (num == 0)
			ds.setReduceDamage(this.displayName, 0);
		else if (num == 1) {
			ds.setExtraWalkspeed(this.displayName, 0);
			ds.changeSetting(att);
		} else if (num == 2) {
			ds.setAbsorption(this.displayName, 0);
			ds.confirmHealthPercent(att);
			ActionBarRunnable.run(att);
		}
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);

		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();
		for (int i = 0; i < 10; i++)
			new BukkitRunnable() {

				@Override
				public void run() {
					EffectStore.Directional.ELECTRIC_SPARK.spawnDirectional(loc, 100, 2, 2, 2, 1);
				}
			}.runTaskLater(Core.getCore(), i);
		double duration = 6d;
		if (tc2 == 2)
			duration = 12d;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		BuffRunnable.run(le, this, duration, 0); // 피감
		ds.setReduceDamage(this.displayName, getTotalDamage(le));
		if (tc1 == 0) {
			BuffRunnable.run(le, this, duration, 1);// 이속
			ds.setExtraWalkspeed(this.displayName, 0.246);
			ds.changeSetting(le);
		}
		if (tc3 == 1) {
			BuffRunnable.run(le, this, duration, 2);// 보호막
			ds.setAbsorption(this.displayName, ds.getMaxHealth() * 0.32);
			ds.confirmHealthPercent(le);
			ActionBarRunnable.run(le);
		}

		ds.addAttackedList(le, this);
		return false;
	}
}
