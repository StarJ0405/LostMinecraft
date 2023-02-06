package com.StarJ.LM.Skills.assassin.Reaper;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.System.DataStore;

public class eclipseRequiem extends Skill {
	protected final tripodChoice[] level1;
	protected final tripodChoice[] level2;
	protected final tripodChoice[] level3;
	protected final double[] damages = new double[] { 113, 200, 256, 300, 336, 363, 388, 410, 427, 443, 487, 536 };

	public eclipseRequiem() {
		super("eclipseRequiem", ChatColor.WHITE + "일식:레퀴엠", 1, Skill.skillGroupType.Awakening, 300, 81, 0,
				"적을 방패로 타격하여 피해를 준다.");
		tripodChoice _11 = new tripodChoice("갑옷 파괴", "스킬 사용시 10초간 적에게 12.0% 증가된 피해를 입힌다.");
		tripodChoice _12 = new tripodChoice("날렵한 움직임", "테스트용");
		tripodChoice _13 = new tripodChoice("전격 배쉬", "테스트용");
		tripodChoice _21 = new tripodChoice("공격 준비", "공격 적중 시 자신의 공격력이 5.0초간 33.5% 증가한다.");
		tripodChoice _22 = new tripodChoice("뇌진탕", "테스트용");
		tripodChoice _23 = new tripodChoice("방패 강화", "공격 적중 시 실드 게이지 충전량이 75.0% 증가된다.");
		tripodChoice _31 = new tripodChoice("타종", "공격 범위가 70.0% 증가한다.");
		tripodChoice _32 = new tripodChoice("메아리", "테스트용");
		this.level1 = new tripodChoice[] { _11, _12, _13 };
		this.level2 = new tripodChoice[] { _21, _22, _23 };
		this.level3 = new tripodChoice[] { _31, _32 };
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Chain;
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
}
