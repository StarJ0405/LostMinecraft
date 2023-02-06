package com.StarJ.LM.System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.StarJ.LM.Core;
import com.StarJ.LM.Items.Items;
import com.StarJ.LM.Items.WeaponItems;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Skill.AttackType;
import com.StarJ.LM.Skills.Skill.skillGroupType;
import com.StarJ.LM.System.JobStore.Identity;

public class DataStore {
	protected static final HashMap<UUID, HashMap<String, Double>> walkSpeed = new HashMap<UUID, HashMap<String, Double>>(); // 이동속도감소용
	//
	private final UUID uuid;
	private JobStore job;
	private double health; // 현재 체력
	private double maxHealth; // 최대 체력
	private final HashMap<String, Double> absorption; // 보호막
	private final HashMap<String, Double> power; // 공격력 증가
	private final HashMap<String, Double> damageIncrease; // 피해 증가
	private final HashMap<String, Double> attackTypeDamage; // 백헤드 피해 증가
	private final HashMap<String, Double> reduceDamage; // 피해 감소
	private final HashMap<skillGroupType, HashMap<String, Double>> skillGroupTypeDamage; // 특정 타입 스킬 피해 증가
	private final HashMap<skillGroupType, HashMap<String, Double>> skillGroupTypeReduceCooldown; // 특정 타입 스킬 쿨타임 감소
	private final HashMap<String, Double> extraDamageIncrease; // 증폭
	private final HashMap<String, Double> extraWalkspeed; // 추가 이동속도 증가
	private final HashMap<String, Double> extraHealth; // 추가 체력 증가
	private final HashMap<String, Double> extraCriticalChance; // 추가 치명타 확률 증가
	private final HashMap<String, Double> extraCriticalDamage; // 추가 치명타 피해 증가
	private Identity identity;
	private double critical;
	private double speed;
	private double specialization;
	private double enduration;
	private AttackType attackType;
	private final Set<String> attacks;
	private final Set<String> attackeds;
	private final Set<String> uses;
	private final Skill[] skills;
	private WeaponItems weapon;
	private int slot;
	private Skill setting;
	private final int maxSkillPoint;

	protected DataStore(LivingEntity le) {
		this.uuid = le.getUniqueId();
		this.absorption = new HashMap<String, Double>();
		this.power = new HashMap<String, Double>();
		this.damageIncrease = new HashMap<String, Double>();
		this.attackTypeDamage = new HashMap<String, Double>();
		this.reduceDamage = new HashMap<String, Double>();
		this.skillGroupTypeDamage = new HashMap<skillGroupType, HashMap<String, Double>>();
		this.skillGroupTypeReduceCooldown = new HashMap<skillGroupType, HashMap<String, Double>>();
		this.extraDamageIncrease = new HashMap<String, Double>();
		this.extraWalkspeed = new HashMap<String, Double>();
		this.extraHealth = new HashMap<String, Double>();
		this.extraCriticalChance = new HashMap<String, Double>();
		this.extraCriticalDamage = new HashMap<String, Double>();
		this.critical = 0;
		this.speed = 0;
		this.specialization = 0;
		this.enduration = 0;
		this.attackType = null;
		this.attacks = new HashSet<String>();
		this.attackeds = new HashSet<String>();
		this.uses = new HashSet<String>();
		this.skills = new Skill[8];
		this.slot = -1;
		this.setting = null;
		this.maxSkillPoint = 414;
		//
		this.job = JobStore.WarLordJT;
		new BukkitRunnable() {

			@Override
			public void run() {
				job.setting(le);
			}
		}.runTask(Core.getCore());

		this.health = job.getMaxHealth();
		this.maxHealth = job.getMaxHealth();
		this.identity = job.getIdentity();
		this.weapon = job.getWeapon();

	}

	public UUID getUUID() {
		return uuid;
	}

	public void setJob(LivingEntity le, JobStore job) {
		if (this.job != null)
			this.job.removeSetting(le);
		this.job = job;
		if (job != null)
			job.setting(le);
		this.health = job.getMaxHealth();
		this.maxHealth = job.getMaxHealth();
		this.identity = job.getIdentity();
		this.weapon = job.getWeapon();
		if (le instanceof Player)
			ConfigStore.loadPlayerData((Player) le, job);
		changeSetting(le);
	}

	public void changeSetting(LivingEntity le) {
		if (le instanceof Player) {
			Player player = (Player) le;
			World world = player.getWorld();
			if (Core.getCore().isVanillaWorld(world)) {
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0);
				player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(1);
				player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
				player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1f);
				for (Attribute att : Attribute.values())
					if (player.getAttribute(att) != null)
						for (AttributeModifier mod : player.getAttribute(att).getModifiers())
							player.getAttribute(att).removeModifier(mod);
			} else {
				player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(0);
				player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(0);
				player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
				if (Core.getCore().isPvp(world)) {
					Inventory inv = player.getInventory();
					ItemStack empty = getEmptyItemStack();
					Skill[] skills = getSkills();
					ItemStack nullItem = Skill.getNullItemStack();
					ItemStack nullChoosenItem = Skill.getNullItemChoosenStack();
					for (int i = 0; i < 9; i++)
						if (i == 7) {
							inv.setItem(i, weapon != null ? weapon.getItem(player) : nullItem);
						} else if (i == 8) {
							Skill skill = skills[i - 1];
							inv.setItem(i, skill != null ? skill.getItemStack(player)
									: (slot == i ? nullChoosenItem : nullItem));
						} else {
							Skill skill = skills[i];
							inv.setItem(i, skill != null ? skill.getItemStack(player)
									: (slot == i ? nullChoosenItem : nullItem));
						}
					for (int i = 9; i < 36; i++)
						inv.setItem(i, empty);
					inv.setItem(17, Items.info.getItem(player));
					if (weapon != null) {
						if (weapon.getIdentitySkill() != null)
							inv.setItem(Skill.getIdentitySlot(), weapon.getIdentitySkill().getItemStack(player));
						if (weapon.getAwakeningSkill() != null)
							inv.setItem(Skill.getAwakeningSlot(), weapon.getAwakeningSkill().getItemStack(player));
					}
					double preMaxHealth = this.maxHealth;
					double newMaxHealth = job.getMaxHealth() * getMultiplyHealth(player) * getExtraHealth();
					this.maxHealth = newMaxHealth;
					if (newMaxHealth - preMaxHealth > 0)
						setHealth(this.health + newMaxHealth - preMaxHealth);
					else
						setHealth(this.health);

					confirmHealthPercent(player);
					applyWalkspeed(player);
					ActionBarRunnable.run(player);
				} else if (Core.getCore().isDefence(world)) {

				}
			}
		} else {

		}
	}

	private ItemStack getEmptyItemStack() {
		ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.DARK_GRAY + "");
		meta.setLocalizedName("empty");
		item.setItemMeta(meta);
		return item;
	}

	public JobStore getJob() {
		return job;
	}

	public WeaponItems getWeapon() {
		return weapon;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public Skill getSetting() {
		return setting;
	}

	public void setSetting(Skill setting) {
		this.setting = setting;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		if (health > this.maxHealth)
			health = this.maxHealth;
		this.health = health;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public int getMaxSkillPoint() {
		return maxSkillPoint;
	}

	public int getRemainSkillPoint(LivingEntity le) {
		int used = 0;
		if (job.getSkills() != null)
			for (Skill skill : job.getSkills())
				used += skill.getUsedSkillPoint(le);
		return maxSkillPoint - used;
	}

	public double getAllAbsorption() {
		double abp = 0;
		for (double a : absorption.values())
			abp += a;
		return abp;
	}

	public double getAbsorption(String name) {
		return absorption.containsKey(name) ? absorption.get(name) : 0;
	}

	public double damageAbsorption(double damage) {
		for (String K : absorption.keySet()) {
			double V = absorption.get(K);
			if (V > 0)
				if (V > damage) {
					absorption.put(K, V - damage);
					return 0;
				} else {
					absorption.put(K, 0D);
					damage -= V;
				}
		}
		absorption.entrySet().removeIf(entries -> entries.getValue() <= 0D);
		return damage;
	}

	public void setAbsorption(String name, double abp) {
		if (abp > 0) {
			absorption.put(name, abp);
		} else if (absorption.containsKey(name))
			absorption.remove(name);
	}

	public void setPower(String name, double power) {
		if (power != 0)
			this.power.put(name, power);
		else
			this.power.remove(name);
	}

	public double getPower() {
		double power = 0d;
		for (double v : this.power.values())
			if (v > power)
				power = v;
		return 1 + power;
	}

	public void setDamageIncrease(String name, double damageIncrease) {
		if (damageIncrease != 0)
			this.damageIncrease.put(name, damageIncrease);
		else
			this.damageIncrease.remove(name);
	}

	public double getDamageIncrease() {
		double damageIncrease = 0d;
		for (double v : this.damageIncrease.values())
			if (v > damageIncrease)
				damageIncrease = v;
		return 1 + damageIncrease;
	}

	public void setAttackTypeDamage(String name, double attackTypeDamage) {
		if (attackTypeDamage != 0)
			this.attackTypeDamage.put(name, attackTypeDamage);
		else
			this.attackTypeDamage.remove(name);
	}

	public double getAttackTypeDamage() {
		double attackTypeDamage = 0d;
		for (double v : this.attackTypeDamage.values())
			if (v > attackTypeDamage)
				attackTypeDamage = v;
		return 1 + attackTypeDamage;
	}

	public double getMultiplyDamage(skillGroupType skillGroupType) {
		return getPower() * getDamageIncrease() * getExtraDamageIncrease() * getSkillGroupTypeDamage(skillGroupType)
				* (skillGroupType.equals(Skill.skillGroupType.Awakening) ? getMultiplyAwakening() : 1);
	}

	public static void setWalkSpeed(LivingEntity le, String name, double walkSpeed) {
		UUID uuid = le.getUniqueId();
		if (!DataStore.walkSpeed.containsKey(uuid))
			DataStore.walkSpeed.put(uuid, new HashMap<String, Double>());
		HashMap<String, Double> walkSpeeds = DataStore.walkSpeed.get(uuid);
		if (walkSpeed != 0)
			walkSpeeds.put(name, walkSpeed);
		else
			walkSpeeds.remove(name);
		DataStore.walkSpeed.put(uuid, walkSpeeds);
	}

	public static double getWalkSpeed(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		double walkSpeed = 0d;
		if (DataStore.walkSpeed.containsKey(uuid))
			for (double v : DataStore.walkSpeed.get(uuid).values())
				walkSpeed += v;
		return walkSpeed;
	}

	public void setReduceDamage(String name, double reduceDamage) {
		if (reduceDamage != 0)
			this.reduceDamage.put(name, reduceDamage);
		else
			this.reduceDamage.remove(name);
	}

	public double getReduceDamage() {
		double reduceDamage = 0d;
		for (double v : this.reduceDamage.values())
			if (v > reduceDamage)
				reduceDamage = v;
		return 1 - reduceDamage;
	}

	public void setSkillGroupTypeDamage(skillGroupType skillGroupType, String name, double skillGroupTypeDamage) {
		if (!this.skillGroupTypeDamage.containsKey(skillGroupType))
			this.skillGroupTypeDamage.put(skillGroupType, new HashMap<String, Double>());
		HashMap<String, Double> hs = this.skillGroupTypeDamage.get(skillGroupType);
		if (skillGroupTypeDamage != 0)
			hs.put(name, skillGroupTypeDamage);
		else {
			hs.remove(name);
			if (hs.keySet().size() == 0)
				this.skillGroupTypeDamage.remove(skillGroupType);
		}
	}

	public double getSkillGroupTypeDamage(skillGroupType skillGroupType) {
		double damage = 1d;
		if (this.skillGroupTypeDamage.containsKey(skillGroupType)) {
			HashMap<String, Double> hs = this.skillGroupTypeDamage.get(skillGroupType);
			for (double d : hs.values())
				damage *= 1 + d;
		}
		return damage;
	}

	public void setSkillGroupTypeReduceCooldown(skillGroupType skillGroupType, String name,
			double skillGroupTypeReduceCooldown) {
		if (!this.skillGroupTypeReduceCooldown.containsKey(skillGroupType))
			this.skillGroupTypeReduceCooldown.put(skillGroupType, new HashMap<String, Double>());
		HashMap<String, Double> hs = this.skillGroupTypeReduceCooldown.get(skillGroupType);
		if (skillGroupTypeReduceCooldown != 0)
			hs.put(name, skillGroupTypeReduceCooldown);
		else {
			hs.remove(name);
			if (hs.keySet().size() == 0)
				this.skillGroupTypeReduceCooldown.remove(skillGroupType);
		}
	}

	public double getSkillGroupTypeReduceCooldown(skillGroupType skillGroupType) {
		double cooldown = 0d;
		if (this.skillGroupTypeReduceCooldown.containsKey(skillGroupType)) {
			HashMap<String, Double> hs = this.skillGroupTypeReduceCooldown.get(skillGroupType);
			for (double d : hs.values())
				cooldown += d;
			if (cooldown < 0)
				cooldown = 0;
		}
		return 1 - cooldown;
	}

	public void setExtraDamageIncrease(String name, double extraDamageIncrease) {
		if (extraDamageIncrease != 0)
			this.extraDamageIncrease.put(name, extraDamageIncrease);
		else
			this.extraDamageIncrease.remove(name);
	}

	public double getExtraDamageIncrease() {
		double extraDamageIncrease = 1;
		for (double eDI : this.extraDamageIncrease.values())
			extraDamageIncrease *= 1 + eDI;
		return extraDamageIncrease;
	}

	public void setExtraWalkspeed(String name, double extraWalkspeed) {
		if (extraWalkspeed != 0)
			this.extraWalkspeed.put(name, extraWalkspeed);
		else
			this.extraWalkspeed.remove(name);
	}

	public double getExtraWalkspeed() {
		double extraWalkspeed = 0;
		for (double eW : this.extraWalkspeed.values())
			extraWalkspeed += eW;
		return extraWalkspeed;
	}

	public void setExtraHealth(String name, double extraHealth) {
		if (extraHealth != 0)
			this.extraHealth.put(name, extraHealth);
		else
			this.extraHealth.remove(name);
	}

	public double getExtraHealth() {
		double extraHealth = 1;
		for (double eH : this.extraHealth.values())
			extraHealth += eH;
		return extraHealth;
	}

	public void setExtraCriticalChance(String name, double extraCriticalChance) {
		if (extraCriticalChance != 0)
			this.extraCriticalChance.put(name, extraCriticalChance);
		else
			this.extraCriticalChance.remove(name);
	}

	public double getExtraCriticalChance() {
		double extraCriticalChance = 0;
		for (double eCD : this.extraCriticalChance.values())
			extraCriticalChance += eCD;
		return extraCriticalChance;
	}

	public void setExtraCriticalDamage(String name, double extraCriticalDamage) {
		if (extraCriticalDamage != 0)
			this.extraCriticalDamage.put(name, extraCriticalDamage);
		else
			this.extraCriticalDamage.remove(name);
	}

	public double getExtraCriticalDamage() {
		double extraCriticalDamage = 2;
		for (double eCD : this.extraCriticalDamage.values())
			extraCriticalDamage += eCD;
		return extraCriticalDamage;
	}

	public Identity getIdentity() {
		return identity;
	}

	public double getPower(LivingEntity le) {
		return 1;
	}

	public void setAttackType(AttackType attackType) {
		this.attackType = attackType;
	}

	public double getMaxAllStat() {
		return 2800d;
	}

	public double getMaxStat() {
		return 1800d;
	}

	public double getRemainStat() {
		return getMaxAllStat() - (this.critical + this.speed + this.enduration + this.specialization);
	}

	public void setCritical(double stat) {
		if (stat > getMaxStat())
			stat = getMaxStat();
		else if (stat < 0)
			stat = 0d;
		if (stat - this.critical > getRemainStat())
			stat = this.critical + getRemainStat();
		this.critical = stat;
	}

	public double getCritical() {
		return critical;
	}

	public double getCriticalChance(LivingEntity le) {
		return this.critical / 2794.440d + (this.attackType != null ? attackType.getCritialChance() : 0)
				+ getExtraCriticalChance();
	}

	public void setSpeed(double stat) {
		if (stat > getMaxStat())
			stat = getMaxStat();
		else if (stat < 0)
			stat = 0d;
		if (stat - this.speed > getRemainStat())
			stat = this.speed + getRemainStat();
		this.speed = stat;
	}

	public double getSpeed() {
		return speed;
	}

	public double getMuliplyWalkspeed(LivingEntity le) {
		return Math.min(0.4, this.speed / 5822.11d + getExtraWalkspeed() + getWalkSpeed(le));
	}

	public static void applyWalkspeed(LivingEntity le) {
		String name = "walkspeed";
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		double walkspeed = ds != null ? ds.getMuliplyWalkspeed(le) : getWalkSpeed(le);
		AttributeInstance atti = le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		for (AttributeModifier mod : le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getModifiers())
			if (mod.getName().equals(name))
				atti.removeModifier(mod);
		if (walkspeed != 0)
			atti.addModifier(new AttributeModifier(le.getUniqueId(), name, walkspeed, Operation.ADD_SCALAR));

	}

	public double getReduceCooldown(LivingEntity le) {
		return 1 - this.speed / 4657.31d;
	}

	public void setEnduration(double stat) {
		if (stat > getMaxStat())
			stat = getMaxStat();
		else if (stat < 0)
			stat = 0d;
		if (stat - this.enduration > getRemainStat())
			stat = this.enduration + getRemainStat();
		this.enduration = stat;
	}

	public double getEnduration() {
		return enduration;
	}

	public double getMultiplyHealth(LivingEntity le) {
		return 1 + this.enduration / 1222.55d;
	}

	public void setSpecialization(double stat) {
		if (stat > getMaxStat())
			stat = getMaxStat();
		else if (stat < 0)
			stat = 0d;
		if (stat - this.specialization > getRemainStat())
			stat = this.specialization + getRemainStat();
		this.specialization = stat;
	}

	public double getSpecialization() {
		return specialization;
	}

	public double getMultiplyAwakening() {
		return 1 + this.specialization / 1830.20d;
	}

	public ItemStack getInfo() {
		LivingEntity le = (LivingEntity) Bukkit.getEntity(uuid);
		ItemStack i = new ItemStack(le instanceof Player ? Material.PLAYER_HEAD : Material.ZOMBIE_HEAD);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + le.getName());
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "직업 : " + job.getDisplayName());
		lore.add(ChatColor.RED + "공격력 : " + getPower(le) * 100 + "%");
		lore.add(ChatColor.RED + "최대체력 : " + getMaxHealth());
		lore.add(ChatColor.WHITE + "클릭시 직업군 선택창으로 이동");
		meta.setLore(lore);
		i.setItemMeta(meta);
		return i;
	}

	public void addAttackList(LivingEntity le, Skill skill) {
		attacks.add(skill.getKey());
	}

	public List<String> getAttackList(LivingEntity le) {
		List<String> attacks = new ArrayList<String>();
		attacks.addAll(this.attacks);
		return attacks;
	}

	public boolean hasAttackList(LivingEntity le, Skill skill) {
		return attacks.contains(skill.getKey());
	}

	public void removeAttackList(LivingEntity le, Skill skill) {
		if (attacks.contains(skill.getKey()))
			attacks.remove(skill.getKey());
	}

	public void addAttackedList(LivingEntity le, Skill skill) {
		attackeds.add(skill.getKey());
	}

	public List<String> getAttackedList(LivingEntity le) {
		List<String> attackeds = new ArrayList<String>();
		attackeds.addAll(this.attackeds);
		return attackeds;
	}

	public boolean hasAttackedList(LivingEntity le, Skill skill) {
		return attackeds.contains(skill.getKey());
	}

	public void removeAttackedList(LivingEntity le, Skill skill) {
		if (attackeds.contains(skill.getKey()))
			attackeds.remove(skill.getKey());
	}

	public void addUsesList(LivingEntity le, Skill skill) {
		uses.add(skill.getKey());
	}

	public List<String> getUsesList(LivingEntity le) {
		List<String> uses = new ArrayList<String>();
		uses.addAll(this.uses);
		return uses;
	}

	public boolean hasUsesList(LivingEntity le, Skill skill) {
		return uses.contains(skill.getKey());
	}

	public void removeUsesList(LivingEntity le, Skill skill) {
		if (uses.contains(skill.getKey()))
			uses.remove(skill.getKey());
	}

	public Skill[] getSkills() {
		return skills;
	}

	public int getSkillsSlot(Skill skill) {
		for (int i = 0; i < skills.length; i++) {
			if (skills[i] == skill)
				if (i == skills.length - 1)
					return 8;
				else
					return i;
		}
		return -1;
	}

	public void setSkill(Skill skill, int slot) {
		if (slot < skills.length) {
			for (int i = 0; i < skills.length; i++)
				if (skills[i] != null && skill != null && skills[i].equals(skill))
					skills[i] = null;
			skills[slot] = skill;
		}
	}

	public static class ActionBarRunnable extends BukkitRunnable {
		private final LivingEntity le;
		private int time = 0;
		private final int max = 5 * 2;

		public ActionBarRunnable(LivingEntity le) {
			this.le = le;
		}

		public static void run(LivingEntity le) {
			Core.getCore().getHashMapStore().setActionbarTask(le,
					new ActionBarRunnable(le).runTaskTimer(Core.getCore(), 0, 10));
		}

		@Override
		public void run() {
			if ((le instanceof Player && ((OfflinePlayer) le).isOnline()) || !le.isDead()) {
				if (this.time < this.max) {
					DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
					if (ds != null) {
						JobStore job = ds.getJob();
						if (job == null) {
							this.cancel();
							return;
						}
						double maxHealth = ds.getMaxHealth();
						double health = ds.getHealth();
						double abp = ds.getAllAbsorption();
						Identity identity = ds.getIdentity();
						if (le instanceof Player)
							MessageStore.sendActionbar((Player) le,
									ChatColor.GREEN + "체력 : " + Math.round(health)
											+ (abp > 0 ? "(+" + Math.round(abp) + ")" : "") + " / "
											+ Math.round(maxHealth) + "      " + identity.getActionbar());
						else {
							String display = "";
							if (le.hasMetadata("type")) {
								for (MetadataValue mv : le.getMetadata("type"))
									if (mv.getOwningPlugin().equals(Core.getCore())) {
										try {
											EntityStore et = EntityStore.valueOf(mv.asString());
											display = et.getDisplayName();
										} catch (Exception ex) {

										}
									}
							}
							le.setCustomName(ChatColor.WHITE + display + " " + ChatColor.GREEN + "체력 : "
									+ Math.round(health) + (abp > 0 ? "(+" + Math.round(abp) + ")" : "") + " / "
									+ Math.round(maxHealth));
							le.setCustomNameVisible(true);
						}
					} else if (le.hasMetadata("maxHealth") || le.hasMetadata("health")) {
						double maxHealth = le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
						double health = le.getHealth();
						for (MetadataValue mv : le.getMetadata("maxHealth"))
							if (mv.getOwningPlugin().equals(Core.getCore()))
								maxHealth = mv.asDouble();
						for (MetadataValue mv : le.getMetadata("health"))
							if (mv.getOwningPlugin().equals(Core.getCore()))
								health = mv.asDouble();
						double abp = le.getAbsorptionAmount();
						String display = "";
						if (le.hasMetadata("type")) {
							for (MetadataValue mv : le.getMetadata("type"))
								if (mv.getOwningPlugin().equals(Core.getCore())) {
									try {
										EntityStore et = EntityStore.valueOf(mv.asString());
										display = et.getDisplayName();
									} catch (Exception ex) {

									}
								}
						}
						le.setCustomName(ChatColor.WHITE + display + " " + ChatColor.GREEN + "체력 : "
								+ Math.round(health) + (abp > 0 ? "(+" + Math.round(abp) + ")" : "") + " / "
								+ Math.round(maxHealth));
						le.setCustomNameVisible(true);
					}
					this.time++;
				} else {
					if (le instanceof Player)
						MessageStore.sendActionbar((Player) le, "");
					this.cancel();
				}
			} else
				this.cancel();
		}
	}

	public void confirmHealthPercent(LivingEntity vic) {
		double health = getHealth();
		double abp = getAllAbsorption();
		double max_health = job != null ? getMaxHealth() : 100;
		double per_health = health / max_health * 100;
		if (per_health <= 1 && per_health > 0) {
			per_health = 1;
		} else if (per_health >= 99 && per_health < 100) {
			per_health = 99;
		}
		if (per_health > 100) {
			per_health = 100;
		}
		if (per_health <= 0)
			if (health > 0) {
				per_health = 1;
			} else
				per_health = 0;
		double per_abp = abp / max_health * 100;
		if (per_abp <= 1 && per_abp > 0) {
			per_abp = 1;
		}
		if (per_abp <= 0)
			if (abp > 0) {
				per_abp = 1;
			} else
				per_abp = 0;
		final double changed_abp = per_abp;
		final double changed_health = per_health;
		new BukkitRunnable() {
			@Override
			public void run() {
				vic.setAbsorptionAmount(changed_abp > 0 ? ((changed_abp < 1) ? 1 : (int) changed_abp) : 0);
				vic.setHealth(changed_health > 0 ? ((changed_health < 1) ? 1 : (int) changed_health) : 0);
			}
		}.runTaskLater(Core.getCore(), 1);
	}

	public static void confirmHealthPercentNoDataStore(LivingEntity vic) {
		double health = vic.getHealth();
		double abp = vic.getAbsorptionAmount();
		double maxHealth = vic.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		for (MetadataValue mv : vic.getMetadata("maxHealth"))
			if (mv.getOwningPlugin().equals(Core.getCore()))
				maxHealth = mv.asDouble();
		for (MetadataValue mv : vic.getMetadata("health"))
			if (mv.getOwningPlugin().equals(Core.getCore()))
				health = mv.asDouble();
		double per_health = health / maxHealth * 100;
		if (per_health <= 1 && per_health > 0) {
			per_health = 1;
		} else if (per_health >= 99 && per_health < 100) {
			per_health = 99;
		}
		if (per_health > 100) {
			per_health = 100;
		}
		if (per_health <= 0)
			if (health > 0) {
				per_health = 1;
			} else
				per_health = 0;
		double per_abp = abp / maxHealth * 100;
		if (per_abp <= 1 && per_abp > 0) {
			per_abp = 1;
		}
		if (per_abp <= 0)
			if (abp > 0) {
				per_abp = 1;
			} else
				per_abp = 0;
		final double changed_abp = per_abp;
		final double changed_health = per_health;
		new BukkitRunnable() {
			@Override
			public void run() {
				vic.setAbsorptionAmount(changed_abp > 0 ? ((changed_abp < 1) ? 1 : (int) changed_abp) : 0);
				vic.setHealth(changed_health > 0 ? ((changed_health < 1) ? 1 : (int) changed_health) : 0);
			}
		}.runTaskLater(Core.getCore(), 1);
	}
}