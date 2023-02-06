package com.StarJ.LM.Skills.warrior.WarLord;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;

public class bash extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 113, 200, 256, 300, 336, 363, 388, 410, 427, 443, 487, 536 };

	public bash() {
		super("bash", ChatColor.WHITE + "배쉬", Skill.warrior.WarLord.skillGroup.Normal, 10, 154, 90, AttackType.Head,
				"적을 방패로 타격하여 피해를 준다.");
		tripodChoice _11 = new tripodChoice("갑옷 파괴", "스킬 적중시 10.0초간 적에게 주는 피해량이 12.0% 증가됩니다.");
		tripodChoice _12 = new tripodChoice("날렵한 움직임", "테스트용");
		tripodChoice _13 = new tripodChoice("전격 배쉬", "테스트용");
		tripodChoice _21 = new tripodChoice("공격 준비", "공격 적중 시 자신의 공격력이 5.0초간 33.5% 증가한다.");
		tripodChoice _22 = new tripodChoice("뇌진탕", "테스트용");
		tripodChoice _23 = new tripodChoice("방패 강화", "공격 적중 시 실드 게이지 충전량이 75.0% 증가된다.");
		tripodChoice _31 = new tripodChoice("타종", "테스트용");
		tripodChoice _32 = new tripodChoice("메아리", "테스트용");
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
		if (num == 0)
			Core.getCore().getHashMapStore().getDataStore(att).setPower(this.displayName, 0);
		else if (num == 1)
			Core.getCore().getHashMapStore().getDataStore(att).setDamageIncrease(this.displayName, 0);
		super.BuffEnd(att, num);
	}

	private Rel[] shield = new Rel[] { //
			new Rel(1, 1, -0.5), new Rel(1, 1, 0.5), //
			new Rel(1, 0.5, 0), new Rel(1, 0.5, -0.5), new Rel(1, 0.5, 0.5), //
			new Rel(1, 0, 0), new Rel(1, 0, -0.5), new Rel(1, 0, 0.5), //
			new Rel(1, -0.5, 0), new Rel(1, -0.5, -0.5), new Rel(1, -0.5, 0.5), //
			new Rel(1, -1, 0),//
			//
	};

	@Override
	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!super.damage(skill, destruction, list, att, vic_e, damage, now))
			return false;
		if (getTripod1Choice(att) == 0) {
			BuffRunnable.run(att, this, 10, 1);// 피증
			Core.getCore().getHashMapStore().getDataStore(att).setDamageIncrease(this.displayName, 0.12d);
		}
		return true;
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
//		int tc1 = getTripod1Choice(le);
		int tc2 = getTripod2Choice(le);
//		int tc3 = getTripod3Choice(player);
		int level = getLevel(le);

		Location loc = le.getEyeLocation().subtract(0, 0.25, 0);
		Vector dir = loc.getDirection();
		dir.setY(0);
		dir.normalize();
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		if (tc2 == 0) {
			BuffRunnable.run(le, this, 5, 0); // 공증
			ds.setPower(this.displayName, 0.335d);
		}

		double mul = ds.getMultiplyDamage(groupType);
		double[] axisXZ = new double[] { -1.5, 0, 1.5 };
		List<Entity> list = new ArrayList<Entity>();
		for (double XZ : axisXZ)
			for (Rel rel : shield) {
				Location now = loc.clone()
						.add(EffectStore.getRel(dir, rel.getFront(), rel.getHeight(), rel.getRight() + XZ));
				EffectStore.spawnRedStone(now, 255, 212, 0, 1, 10, 0, 0, 0);
				for (Entity et : loc.getWorld().getNearbyEntities(now, 1, 1, 1))
					damage(list, le, et, damages[level - 1] * mul);
			}
		return false;
	}
}
