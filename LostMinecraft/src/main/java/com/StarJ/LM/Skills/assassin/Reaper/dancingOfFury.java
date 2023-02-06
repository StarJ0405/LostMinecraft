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

public class dancingOfFury extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages1 = new double[] { 299, 529, 678, 792, 886, 962, 1024, 1078, 1122, 1162, 1278,
			1406 };
	protected final double[] damages2 = new double[] { 477, 795, 1018, 1192, 1330, 1446, 1543, 1625, 1694, 1758, 1933,
			2127 };

	public dancingOfFury() {
		super("dancingOfFury", ChatColor.WHITE + "댄싱 오브 퓨리", Skill.assassin.Reaper.skillGroup.SurpiseAttack, 28, 42, 0,
				AttackType.Back, "붉은 그림자의 기운을 모아 전방 주변을 그림자 처럼 빠르게 움직인다.", "제자리로 돌아와 마무리 피해를 준다.");
		tripodChoice _11 = new tripodChoice("날렵한 움직임", "테스트용");
		tripodChoice _12 = new tripodChoice("꿰뚫는 일격", "적에게 주는 피해가 62.0% 증가한다.");
		tripodChoice _13 = new tripodChoice("부위파괴 강화", "테스트용");
		tripodChoice _21 = new tripodChoice("혼돈 강화", "테스트용");
		tripodChoice _22 = new tripodChoice("신속한 준비", "테스트용");
		tripodChoice _23 = new tripodChoice("치명적인 단검", "치명타 적중률이 60.0% 증가한다.");
		tripodChoice _31 = new tripodChoice("그림자의 춤", "테스트용");
		tripodChoice _32 = new tripodChoice("절제된 동작", "그림자 공격이 3회로 줄어들어 더욱 빠르게 공격한다.", "기본 피해량의 170.0% 피해를 준다.");
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
		return 10;// 9+1
	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType);
		if (tc1 == 1)
			mul *= 1.62d;
		if (tc3 == 1)
			mul *= 1.7d;

		return (damages1[level - 1] + damages2[level - 1]) * mul;
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
	public int getDestruction() {
		return 1;
	}

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (getTripod2Choice(att) == 2)
			ds.setExtraCriticalChance(this.displayName, 0.6);
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		ds.setExtraCriticalChance(this.displayName, 0);
		return flag;
	}

	@Override
	public boolean Use(LivingEntity le) {
		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType) * (tc1 == 1 ? 1.62 : 1) * (tc3 == 1 ? 1.7d : 1);
		if (super.Use(le))
			return true;
		final int max = tc3 == 1 ? 3 : 9;
		int i = 0;
		for (; i < max; i++)
			new BukkitRunnable() {
				@Override
				public void run() {
					Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
					Vector dir = loc.getDirection().clone().setY(0).normalize();
					Location now = loc.clone().add(EffectStore.getRel(dir, 4, 0, 0));
					EffectStore.spawnRedStone(now, 128, 0, 0, 0.75f, 100, 1.5d, 1.5d, 1.5d);
					EffectStore.Directional.SWEEP_ATTACK.spawnDirectional(now, 3, 1.5d, 1.5d, 1.5d, 1);
					List<Entity> list = new ArrayList<Entity>();
					for (Entity et : now.getWorld().getNearbyEntities(now, 4, 3, 4))
						damage(list, le, et, damages1[level - 1] / max * mul);
				}
			}.runTaskLater(Core.getCore(), i * (tc3 == 1 ? 3 : 2));
		new BukkitRunnable() {
			@Override
			public void run() {
				Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
				Vector dir = loc.getDirection().clone().setY(0).normalize();
				Location now = loc.clone().add(EffectStore.getRel(dir, 4, 0, 0));
				EffectStore.spawnRedStone(now, 222, 0, 0, 1f, 500, 1.5d, 1.5d, 1.5d);
				EffectStore.Directional.SWEEP_ATTACK.spawnDirectional(now, 30, 1.5d, 1.5d, 1.5d, 1);
				List<Entity> list = new ArrayList<Entity>();
				for (Entity et : now.getWorld().getNearbyEntities(now, 4, 3, 4))
					damage(list, le, et, damages2[level - 1] * mul);
			}
		}.runTaskLater(Core.getCore(), i * 2 + 6);
		return false;
	}
}
