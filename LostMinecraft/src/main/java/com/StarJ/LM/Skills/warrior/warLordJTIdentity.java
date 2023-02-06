package com.StarJ.LM.Skills.warrior;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Skill.warrior.WarLord.skillGroup;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.Skills.Runnable.SkillCoolRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.EffectStore;
import com.StarJ.LM.System.DataStore.ActionBarRunnable;
import com.StarJ.LM.System.JobStore.Identity;
import com.StarJ.LM.System.JobStore.IdentityCalc;

public class warLordJTIdentity extends Skill {

	public warLordJTIdentity() {
		super("warLordJTIdentity", ChatColor.WHITE + "방어태세", 1, skillGroupType.Identity, 3d, "온/오프형 토글형 스킬",
				"방어태세 중 일반 스킬 피해량이 20% 증가한다.", "실드 게이지에 비례하여 보호막을 획득한다.", "최대 체력 100%만큼 보호막을 획득가능하다.",
				"0.1초마다 실드 게이지의 0.1%씩 소모된다.", "피격시 10초동안 주는 피해량이 6% 증가한다.", "이 버프는 3회까지 중첩된다.");
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
	}

	@Override
	public double getCooldown(LivingEntity le) {
		return this.cooldown;
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

	@Override
	public double Attacked(LivingEntity vic, LivingEntity att, double damage) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(vic);
		if (BuffRunnable.has(vic, this, 2) || BuffRunnable.has(vic, this, 3)) {
			BuffRunnable.run(vic, this, 10, 3); // 3스택
			BuffRunnable.run(vic, this, 10, 2); // 2스택
			BuffRunnable.run(vic, this, 10, 1); // 1스택
			ds.setExtraDamageIncrease(this.displayName, 0.18);
		} else if (BuffRunnable.has(vic, this, 1)) {
			BuffRunnable.run(vic, this, 10, 2); // 2스택
			BuffRunnable.run(vic, this, 10, 1); // 1스택
			ds.setExtraDamageIncrease(this.displayName, 0.12);
		} else {
			BuffRunnable.run(vic, this, 10, 1); // 1스택
			ds.setExtraDamageIncrease(this.displayName, 0.06);
		}
		return super.Attacked(vic, att, damage);
	}

	@Override
	public void BuffEnd(LivingEntity att, int num) {
		super.BuffEnd(att, num);
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		if (num == 0) {
			Identity iden = ds.getIdentity();
			double perAbp = ds.getAbsorption(warLordJTIdentity.this.displayName)
					/ (ds.getMaxHealth() * ds.getJob().getIdentityCalcs()[0].getCalc(att));
			iden.setNow(iden.getMax() * perAbp);
			ds.setAbsorption(this.displayName, 0);
			ds.setExtraWalkspeed(this.displayName, 0);
			ds.removeAttackedList(att, this);
			ds.confirmHealthPercent(att);
			ds.changeSetting(att);
			iden.setLock(false);
			ds.setSkillGroupTypeDamage(skillGroup.Normal, this.displayName, 0);
		} else
			ds.setExtraDamageIncrease(this.displayName, 0);
	}

	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		if (BuffRunnable.has(le, this, 0)) {
			BuffEnd(le, 0);
			SkillCoolRunnable.end(le, this);
		} else {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
			Identity iden = ds.getIdentity();
			double perIden = iden.getNow() / iden.getMax();
			BuffRunnable.run(le, this, 200, 0); // 방어태세 지속
			IdentityCalc idenCalc = ds.getJob().getIdentityCalcs()[0];
			ds.setSkillGroupTypeDamage(skillGroup.Normal, this.displayName, 0.2d);
			ds.setAbsorption(this.displayName, perIden * ds.getMaxHealth() * idenCalc.getCalc(le));
			ds.setExtraWalkspeed(this.displayName, -0.8d);
			ds.addAttackedList(le, this);
			ds.confirmHealthPercent(le);
			ds.changeSetting(le);
			iden.setLock(true);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (BuffRunnable.has(le, warLordJTIdentity.this, 0)) {
						double perAbp = ds.getAbsorption(warLordJTIdentity.this.displayName)
								/ (ds.getMaxHealth() * idenCalc.getCalc(le));
						double now = iden.getMax() * (perAbp - 0.001);
						iden.setLock(false);
						iden.setNow(now);
						iden.setLock(true);
						if (now < 0) {
							this.cancel();
							BuffEnd(le, 0);
						} else {
							ds.setAbsorption(warLordJTIdentity.this.displayName,
									now / iden.getMax() * ds.getMaxHealth() * idenCalc.getCalc(le));
							ds.confirmHealthPercent(le);
						}
						EffectStore.Directional.BUBBLE_POP.spawnDirectional(le.getEyeLocation(), 10, 0.1, 0.1, 0.1, 0.5f);
						ActionBarRunnable.run(le);
					} else
						this.cancel();
				}
			}.runTaskTimer(Core.getCore(), 0, 2);
		}
		return false;
	}
}
