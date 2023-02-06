package com.StarJ.LM.Skills.assassin;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.DataStore.ActionBarRunnable;
import com.StarJ.LM.System.EntityStore;
import com.StarJ.LM.System.JobStore.Identity;
import com.StarJ.LM.System.MessageStore;

public class reaperDSIdentity extends Skill {

	public reaperDSIdentity() {
		super("reaperDSIdentity", ChatColor.WHITE + "페르소나", 1, Skill.skillGroupType.Identity, 0d,
				"그림자 환영을 소환하고, 최대 10초 동안 은신 상태가 됩니다.", "게이지가 지속적으로 감소합니다.", "스킬을 사용 또는 게이지가 모두 소모시 해제됩니다.",
				"은신 중 이동속도가 30% 증가합니다.",
				"페르소나 상태에서 " + assassin.Reaper.skillGroup.SurpiseAttack.getName() + ChatColor.WHITE + "이 간결해집니다.",
				assassin.Reaper.skillGroup.SurpiseAttack.getName() + ChatColor.WHITE + "의 피해량이 160% 증가한다.");
	}

	@Override
	public SkillType getSkillType(LivingEntity le) {
		return SkillType.Normal;
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

	private HashMap<UUID, LivingEntity> shadows = new HashMap<UUID, LivingEntity>();

	@SuppressWarnings("deprecation")
	@Override
	public void BuffEnd(LivingEntity att, int num) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		Identity iden = ds.getIdentity();
		if (shadows.containsKey(att.getUniqueId())) {
			LivingEntity preShadow = shadows.get(att.getUniqueId());
			if (preShadow != null && !preShadow.isDead())
				preShadow.remove();
			shadows.remove(att.getUniqueId());
		}
		ds.setSkillGroupTypeDamage(assassin.Reaper.skillGroup.SurpiseAttack, this.displayName, 0);
		ds.setExtraWalkspeed(this.displayName, 0);
		new BukkitRunnable() {

			@Override
			public void run() {
				iden.setLock(false);
				iden.setNow(0);
			}
		}.runTaskLater(Core.getCore(), 5);
		if (att instanceof Player)
			for (Player other : Bukkit.getOnlinePlayers())
				other.showPlayer(Core.getCore(), (Player) att);
		else
			for (Player other : Bukkit.getOnlinePlayers())
				other.showEntity(Core.getCore(), att);
		att.setInvisible(false);
		super.BuffEnd(att, num);
	}

	@Override
	public void otherUse(Skill pre, LivingEntity att) {
		super.otherUse(pre, att);
		if (pre.equals(this))
			return;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		ds.removeUsesList(att, this);
		BuffEnd(att, 0);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean Use(LivingEntity le) {
		if (super.Use(le))
			return true;
		if (BuffRunnable.has(le, this))
			return true;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		Identity iden = ds.getIdentity();
		if (iden.getNow() < iden.getMax()) {
			if (le instanceof Player)
				MessageStore.sendSystemMsg((Player) le, iden.getDisplay() + " 게이지가 부족합니다.");
			return true;
		}

		if (shadows.containsKey(le.getUniqueId())) {
			LivingEntity preShadow = shadows.get(le.getUniqueId());
			if (preShadow != null && !preShadow.isDead())
				preShadow.remove();
		}
		Core.getCore().getHashMapStore().setTarget(le, Core.getCore().getHashMapStore().getTarget(le));
		LivingEntity shadow = (LivingEntity) EntityStore.Persona.spawnEntity(le.getLocation());
		if (shadow != null) {
			shadow.getEquipment().setItemInMainHand(ds.getJob().getWeapon().getItem(le));
			shadows.put(le.getUniqueId(), shadow);
			shadow.setMetadata("owner", new FixedMetadataValue(Core.getCore(), le.getUniqueId().toString()));
		}
		le.setVelocity(new Vector(0, 0.2, 0));

		new BukkitRunnable() {
			@Override
			public void run() {
				Location loc = le.getLocation();
				Vector dir = loc.getDirection().setY(0).normalize();
				le.setVelocity(dir.clone().multiply(-1));
			}
		}.runTaskLater(Core.getCore(), 2);
		BuffRunnable.run(le, this, 11, 0);
		ds.setSkillGroupTypeDamage(assassin.Reaper.skillGroup.SurpiseAttack, this.displayName, 1.6);
		ds.setExtraWalkspeed(this.displayName, 0.3);
		if (le instanceof Player)
			for (Player other : Bukkit.getOnlinePlayers())
				other.hidePlayer(Core.getCore(), (Player) le);
		else
			for (Player other : Bukkit.getOnlinePlayers())
				other.hideEntity(Core.getCore(), le);
		le.setInvisible(true);
		ds.addUsesList(le, this);
		iden.setLock(true);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (BuffRunnable.has(le, reaperDSIdentity.this)) {
					double now = iden.getNow() - iden.getMax() * 0.05;
					if (now <= 0) {
						now = 0;
						BuffEnd(le, 0);
						this.cancel();
					} else {
						iden.setLock(false);
						iden.setNow(now);
						iden.setLock(true);
						ActionBarRunnable.run(le);
					}
				} else
					this.cancel();
			}
		}.runTaskTimer(Core.getCore(), 2, 10);
		return true;
	}
}
