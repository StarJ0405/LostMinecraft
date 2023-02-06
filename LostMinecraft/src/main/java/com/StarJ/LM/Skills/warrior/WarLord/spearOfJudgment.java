package com.StarJ.LM.Skills.warrior.WarLord;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import com.StarJ.LM.Skills.Skill;

public class spearOfJudgment extends Skill {

	public spearOfJudgment() {
		super("spearOfJudgment", ChatColor.WHITE + "심판의 창", 1, Skill.skillGroupType.Awakening, 300, 81, 2);
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;

	}

	@Override
	public double getTotalDamage(LivingEntity le) {
		return 0;

	}

	@Override
	public tripodChoice[] getTripod1() {
		return null;
	}

	@Override
	public tripodChoice[] getTripod2() {
		return null;
	}

	@Override
	public tripodChoice[] getTripod3() {
		return null;
	}
}
