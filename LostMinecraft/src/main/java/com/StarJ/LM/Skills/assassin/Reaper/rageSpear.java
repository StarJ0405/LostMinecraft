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

public class rageSpear extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 698, 1241, 1589, 1859, 2074, 2254, 2404, 2534, 2641, 2740, 3014,
			3315 };

	public rageSpear() {
		super("rageSpear", ChatColor.WHITE + "레이지 스피어", Skill.assassin.Reaper.skillGroup.SurpiseAttack, 25, 46, 0,
				AttackType.Back, "붉은 그림자의 기운을 모아 전방의 적을 찔러 피해를 준다.");
		tripodChoice _11 = new tripodChoice("치명적인 단검", "치명타 적중률이 40.0% 증가한다.");
		tripodChoice _12 = new tripodChoice("빠른 준비", "재사용 대기시간이 9.0초 감소한다.");
		tripodChoice _13 = new tripodChoice("학살", "테스트용");
		tripodChoice _21 = new tripodChoice("강인함", "테스트용");
		tripodChoice _22 = new tripodChoice("고립", "테스트용");
		tripodChoice _23 = new tripodChoice("기습", "공격이 백어택으로 적중 시 적에게 주는 피해가 70.0% 증가한다.");
		tripodChoice _31 = new tripodChoice("집중 공격", "시전 중 어둠의 기운으로 대상을 정면으로 끌어당기며 피해를 줍니다.",
				"당기면서 기본 피해량의 2% 총 3회의 추가 피해를 줍니다.", "주는 피해가 85.0% 증가한다.");
		tripodChoice _32 = new tripodChoice("처형", "테스트용");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public int getTimes(LivingEntity le) {
		return getTripod3Choice(le) == 0 ? 4 : 1;
	}

	@Override
	public double getReduceCooldown(LivingEntity le) {
		return getTripod1Choice(le) == 1 ? 9 : 0;
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
			mul *= 1.7d;
		if (tc3 == 0)
			mul *= 1.85d;
		return damages[level - 1] * mul + (tc3 == 0 ? damages[level - 1] * 0.06 * mul : 0);
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

	Rel[] sting = new Rel[] { //
			new Rel(0.25, 0, 0), new Rel(0.5, 0, 0), new Rel(0.75, 0, 0), new Rel(1, 0, 0), //
			new Rel(1.25, 0, 0), new Rel(1.5, 0, 0), new Rel(1.75, 0, 0), new Rel(2, 0, 0), //
			new Rel(2.25, 0, 0), new Rel(2.5, 0, 0),//
	};
	Rel[] reverseSting = new Rel[] { //
			new Rel(2.5, 0, 0), new Rel(2.25, 0, 0), //
			new Rel(2, 0, 0), new Rel(1.75, 0, 0), new Rel(1.5, 0, 0), new Rel(1.25, 0, 0), //
			new Rel(1, 0, 0), new Rel(0.75, 0, 0), new Rel(0.5, 0, 0), new Rel(0.25, 0, 0),//
	};

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (getTripod1Choice(att) == 0)
			ds.setExtraCriticalChance(this.displayName, 0.4d);
		if (now != null && now.equals(AttackType.Back) && getTripod2Choice(att) == 2)
			damage *= 1.7d;
		boolean flag = super.damage(skill, destruction, list, att, vic_e, damage, now);
		ds.setExtraCriticalChance(this.displayName, 0);
		return flag;
	}

	@Override
	public boolean Use(LivingEntity le) {
//		int tc1 = getTripod1Choice(le);
//		int tc2 = getTripod2Choice(le);
		int tc3 = getTripod3Choice(le);
		int level = getLevel(le);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double mul = ds.getMultiplyDamage(groupType) * (tc3 == 0 ? 1.85d : 1);
		if (super.Use(le))
			return true;

		if (tc3 == 0) {
			int ii = 0;
			List<Entity> list1 = new ArrayList<Entity>();
			List<Entity> list2 = new ArrayList<Entity>();
			List<Entity> list3 = new ArrayList<Entity>();
			for (Rel rel : reverseSting) {
				List<Entity> list = ii / 3 == 0 ? list1 : (ii / 3 == 1 ? list2 : list3);
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection().clone().setY(0).normalize();
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.5));
						EffectStore.spawnRedStone(now, 222, 222, 222, 1f, 25, 1d, 1d, 1d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
							damage(list, le, et, damages[level - 1] * 0.02 * mul);
					}
				}.runTaskLater(Core.getCore(), ii);
				ii++;
			}
			int max = sting.length;
			int i = sting.length;
			List<Entity> list = new ArrayList<Entity>();
			for (Rel rel : sting) {
				int j = i;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection().clone().setY(0).normalize();
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.5));
						double off = 1 + j * 4d / max;
						EffectStore.spawnRedStone(now, 128, 0, 0, 0.75f, 10 * j, 0.1d * off, 0.1d * off, 0.1d * off);
						EffectStore.spawnRedStone(now, 128, 0, 0, 1f, 10, 0d, 0d, 0d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
							damage(list, le, et, damages[level - 1] * mul);
					}
				}.runTaskLater(Core.getCore(), ii + 6 + max - i);
				i--;
			}
		} else {
			EffectStore.spawnRedStone(le.getEyeLocation(), 128, 0, 0, 0.75f, 50, 0.5d, 0.5d, 0.5d);
			int max = sting.length;
			int i = sting.length;
			List<Entity> list = new ArrayList<Entity>();
			for (Rel rel : sting) {
				int j = i;
				new BukkitRunnable() {
					@Override
					public void run() {
						Location loc = le.getEyeLocation().clone().subtract(0, 0.25, 0);
						Vector dir = loc.getDirection().clone().setY(0).normalize();
						Location now = loc.clone()
								.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight(), 1.5));
						double off = 1 + j * 4d / max;
						EffectStore.spawnRedStone(now, 128, 0, 0, 0.75f, 10 * j, 0.1d * off, 0.1d * off, 0.1d * off);
						EffectStore.spawnRedStone(now, 128, 0, 0, 1f, 10, 0d, 0d, 0d);
						for (Entity et : now.getWorld().getNearbyEntities(now, 1, 2, 1))
							damage(list, le, et, damages[level - 1] * mul);
					}
				}.runTaskLater(Core.getCore(), 6 + max - i);
				i--;
			}
		}
		return false;
	}
}
