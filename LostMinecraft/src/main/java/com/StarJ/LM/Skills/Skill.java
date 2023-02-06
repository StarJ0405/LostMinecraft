package com.StarJ.LM.Skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.Skills.Runnable.ChargingRunnable;
import com.StarJ.LM.Skills.Runnable.ComboCoolRunnable;
import com.StarJ.LM.Skills.Runnable.DebuffRunnable;
import com.StarJ.LM.Skills.Runnable.HoldingPerfectRunnable;
import com.StarJ.LM.Skills.Runnable.HoldingRunnable;
import com.StarJ.LM.Skills.Runnable.SkillCoolRunnable;
import com.StarJ.LM.Skills.assassin.reaperDSIdentity;
import com.StarJ.LM.Skills.assassin.Reaper.blackMist;
import com.StarJ.LM.Skills.assassin.Reaper.blink;
import com.StarJ.LM.Skills.assassin.Reaper.callOfKnife;
import com.StarJ.LM.Skills.assassin.Reaper.dancingOfFury;
import com.StarJ.LM.Skills.assassin.Reaper.deathSide;
import com.StarJ.LM.Skills.assassin.Reaper.distortion;
import com.StarJ.LM.Skills.assassin.Reaper.eclipseRequiem;
import com.StarJ.LM.Skills.assassin.Reaper.iblisto;
import com.StarJ.LM.Skills.assassin.Reaper.lastGraffiti;
import com.StarJ.LM.Skills.assassin.Reaper.lunarEclipseCadenza;
import com.StarJ.LM.Skills.assassin.Reaper.nightMare;
import com.StarJ.LM.Skills.assassin.Reaper.phantomDancer;
import com.StarJ.LM.Skills.assassin.Reaper.rageSpear;
import com.StarJ.LM.Skills.assassin.Reaper.saberStinger;
import com.StarJ.LM.Skills.assassin.Reaper.shadowDot;
import com.StarJ.LM.Skills.assassin.Reaper.shadowStorm;
import com.StarJ.LM.Skills.assassin.Reaper.shadowTrap;
import com.StarJ.LM.Skills.assassin.Reaper.silentSmasher;
import com.StarJ.LM.Skills.assassin.Reaper.spinningDagger;
import com.StarJ.LM.Skills.assassin.Reaper.spiritCatch;
import com.StarJ.LM.Skills.warrior.warLordJTIdentity;
import com.StarJ.LM.Skills.warrior.WarLord.auraOfNelasia;
import com.StarJ.LM.Skills.warrior.WarLord.bash;
import com.StarJ.LM.Skills.warrior.WarLord.burstCannon;
import com.StarJ.LM.Skills.warrior.WarLord.chargeStinger;
import com.StarJ.LM.Skills.warrior.WarLord.counterSpear;
import com.StarJ.LM.Skills.warrior.WarLord.cryOfHate;
import com.StarJ.LM.Skills.warrior.WarLord.dashUpperFire;
import com.StarJ.LM.Skills.warrior.WarLord.fireBullet;
import com.StarJ.LM.Skills.warrior.WarLord.hookChain;
import com.StarJ.LM.Skills.warrior.WarLord.lightningOfGuardian;
import com.StarJ.LM.Skills.warrior.WarLord.protectionOfGuardian;
import com.StarJ.LM.Skills.warrior.WarLord.riffAttack;
import com.StarJ.LM.Skills.warrior.WarLord.risingSpear;
import com.StarJ.LM.Skills.warrior.WarLord.sharpSpear;
import com.StarJ.LM.Skills.warrior.WarLord.shieldCharge;
import com.StarJ.LM.Skills.warrior.WarLord.shieldShove;
import com.StarJ.LM.Skills.warrior.WarLord.shieldUpheaval;
import com.StarJ.LM.Skills.warrior.WarLord.spearOfJudgment;
import com.StarJ.LM.Skills.warrior.WarLord.spearShot;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.DataStore.ActionBarRunnable;
import com.StarJ.LM.System.EffectStore;
import com.StarJ.LM.System.EffectStore.SpellType;
import com.StarJ.LM.System.JobStore.Identity;
import com.StarJ.LM.System.MessageStore;
import com.StarJ.LM.System.Permissions;
import com.google.common.base.Predicate;

public abstract class Skill {
	private static final List<Skill> list = new ArrayList<Skill>();
	private static final HashMap<UUID, HashMap<Skill, Long>> cooldowns = new HashMap<UUID, HashMap<Skill, Long>>();
	protected static final HashMap<UUID, HashMap<Skill, StackableBukkitTask>> blood = new HashMap<UUID, HashMap<Skill, StackableBukkitTask>>();
	protected static final HashMap<UUID, HashMap<Skill, StackableBukkitTask>> poison = new HashMap<UUID, HashMap<Skill, StackableBukkitTask>>();
	protected static final HashMap<UUID, HashMap<Skill, StackableBukkitTask>> fire = new HashMap<UUID, HashMap<Skill, StackableBukkitTask>>();

	//
	protected final String key;
	protected final String displayName;
	protected final skillGroupType groupType;
	protected final String[] lores;
	protected final double cooldown;
	protected final double identity;
	protected final double knockDown;
	protected final AttackType[] types;
	protected final HashMap<UUID, Integer> tripod1Choice;
	protected final HashMap<UUID, Integer> tripod2Choice;
	protected final HashMap<UUID, Integer> tripod3Choice;
	protected final HashMap<UUID, Integer> skillLevel;
	protected final HashMap<UUID, Double> extraMultiplyIdentity;
	protected final HashMap<UUID, Integer> maxStack;
	protected final HashMap<UUID, Integer> nowStack;
	protected final int max;

	public Skill(String key, String displayName, skillGroupType groupType, double cooldown, double knockDown,
			double identity, String... lores) {
		this(key, displayName, 12, groupType, cooldown, knockDown, identity, new AttackType[] {}, lores);
	}

	public Skill(String key, String displayName, skillGroupType groupType, double cooldown, String... lores) {
		this(key, displayName, 12, groupType, cooldown, 0, 0, new AttackType[] {}, lores);
	}

	public Skill(String key, String displayName, int max, skillGroupType groupType, double cooldown, double knockDown,
			double identity, String... lores) {
		this(key, displayName, max, groupType, cooldown, knockDown, identity, new AttackType[] {}, lores);
	}

	public Skill(String key, String displayName, int max, skillGroupType groupType, double cooldown, String... lores) {
		this(key, displayName, max, groupType, cooldown, 0, 0, new AttackType[] {}, lores);
	}

	public Skill(String key, String displayName, int max, skillGroupType groupType, double cooldown, double knockDown,
			double identity, AttackType type, String... lores) {
		this(key, displayName, max, groupType, cooldown, knockDown, identity, new AttackType[] { type }, lores);
	}

	public Skill(String key, String displayName, int max, skillGroupType groupType, double cooldown, AttackType type,
			String... lores) {
		this(key, displayName, max, groupType, cooldown, 0, 0, new AttackType[] { type }, lores);
	}

	public Skill(String key, String displayName, skillGroupType groupType, double cooldown, double knockDown,
			double identity, AttackType type, String... lores) {
		this(key, displayName, 12, groupType, cooldown, knockDown, identity, new AttackType[] { type }, lores);
	}

	public Skill(String key, String displayName, skillGroupType groupType, double cooldown, AttackType type,
			String... lores) {
		this(key, displayName, 12, groupType, cooldown, 0, 0, new AttackType[] { type }, lores);
	}

	public Skill(String key, String displayName, skillGroupType groupType, double cooldown, double knockDown,
			double identity, AttackType[] types, String... lores) {
		this(key, displayName, 12, groupType, cooldown, knockDown, identity, types, lores);
	}

	public Skill(String key, String displayName, skillGroupType groupType, double cooldown, AttackType[] types,
			String... lores) {
		this(key, displayName, 12, groupType, cooldown, 0, 0, types, lores);
	}

	public Skill(String key, String displayName, int max, skillGroupType groupType, double cooldown, double knockDown,
			double identity, AttackType[] types, String... lores) {
		this.key = key;
		this.displayName = displayName;
		this.max = max;
		this.groupType = groupType;
		this.cooldown = cooldown;
		this.knockDown = knockDown;
		this.identity = identity;
		this.types = types;
		this.lores = lores;
		this.tripod1Choice = new HashMap<UUID, Integer>();
		this.tripod2Choice = new HashMap<UUID, Integer>();
		this.tripod3Choice = new HashMap<UUID, Integer>();
		this.skillLevel = new HashMap<UUID, Integer>();
		this.extraMultiplyIdentity = new HashMap<UUID, Double>();
		this.maxStack = new HashMap<UUID, Integer>();
		this.nowStack = new HashMap<UUID, Integer>();
		list.add(this);
	}

	public String getKey() {
		return key;
	}

	public String getDisplayName() {
		return displayName;
	}

	public skillGroupType getGroupType(LivingEntity le) {
		return groupType;
	}

	public abstract SkillType getSkillType(LivingEntity le);

	public double getCooldown(LivingEntity le) {
		if (le == null)
			return cooldown;
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		return (cooldown - getReduceCooldown(le)) * ds.getReduceCooldown(le)
				* ds.getSkillGroupTypeReduceCooldown(groupType);
	}

	public double getReduceCooldown(LivingEntity le) {
		return 0;
	}

	public int getComboDuration(LivingEntity le) {
		SkillType now = getSkillType(le);
		return now.equals(SkillType.Chain) ? 80 : (now.equals(SkillType.Combo) ? 20 : 0);
	}

	public List<String> getLore(LivingEntity le) {
		List<String> lore = new ArrayList<String>();
		lore.add(getSkillType(le).getName() + "                                    " + groupType.getName());
		if (!groupType.equals(skillGroupType.Identity) && !groupType.equals(skillGroupType.Awakening))
			lore.add(ChatColor.YELLOW + "레벨 : " + getLevel(le));
		if (types != null)
			if (types.length == 1)
				lore.add(ChatColor.YELLOW + "공격 타입 : " + types[0].getDisplayName());
			else if (types.length == 2)
				lore.add(ChatColor.YELLOW + "공격 타입 : " + types[0].getDisplayName() + ", " + types[1].getDisplayName());
		if (this.knockDown > 0)
			lore.add(ChatColor.YELLOW + "무력화 수치 : " + this.knockDown);
		if (getDestruction() > 0)
			lore.add(ChatColor.YELLOW + "파괴 수치 : " + getDestruction());
		if (this.identity > 0)
			lore.add(ChatColor.YELLOW + Core.getCore().getHashMapStore().getDataStore(le).getIdentity().getDisplay()
					+ " 획득량 : " + Math.round(this.identity * getMultiplyIdentity(le) * 100d) / 100d);
		for (String l : lores)
			lore.add(ChatColor.WHITE + l);
		if (getTotalDamage(le) > 0)
			lore.add(ChatColor.GREEN + "총 피해량 : " + Math.round(getTotalDamage(le) * 100d) / 100d);
		if (getCooldown(le) > 0)
			lore.add(ChatColor.GREEN + "재사용대기시간 : " + Math.round(getCooldown(le) * 100d) / 100d);
		int _1 = getTripod1Choice(le);
		int _2 = getTripod2Choice(le);
		int _3 = getTripod3Choice(le);
		if (_1 > -1) {
			String tripod = ChatColor.WHITE + "[" + getTripod1()[_1].display;
			if (_2 > -1)
				tripod += ", " + getTripod2()[_2].display;
			if (_3 > -1)
				tripod += ", " + getTripod3()[_3].display;
			lore.add(tripod + "]");
		}
		return lore;
	}

	public double getTotalDamage(LivingEntity le) {
		return 0;
	}

	public int getTimes(LivingEntity le) {
		return 1;
	}

	public int getDestruction() {
		return 0;
	}

	public int getMax() {
		return max;
	}

	public int getUsedSkillPoint(LivingEntity le) {
		int sum = 0;
		for (int level = 2; level <= getLevel(le); level++)
			sum += getNeedSkillPoint(level - 1);
		return sum;
	}

	public static int getNeedSkillPoint(int level) {
		if (level >= 12)
			return 0;
		else if (level < 3)
			return 1;
		else if (level == 3)
			return 2;
		else if (level < 6)
			return 4;
		else if (level < 9)
			return 8;
		else if (level == 9)
			return 12;
		else
			return 6;
	}

	public int getLevel(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		return skillLevel.containsKey(uuid) ? skillLevel.get(uuid) : 1;
	}

	public void setLevel(LivingEntity le, int level) {
		UUID uuid = le.getUniqueId();
		if (level > 12)
			level = 12;
		if (level > 1)
			skillLevel.put(uuid, level);
		else
			skillLevel.remove(uuid);
		if (level < 4)
			setTripod1Choice(le, -1);
		else if (level < 7)
			setTripod2Choice(le, -1);
		else if (level < 10)
			setTripod3Choice(le, -1);
	}

	public boolean Use(LivingEntity le) {
		SkillType type = getSkillType(le);
		if ((type.equals(SkillType.Charge) || type.equals(SkillType.Holding)) && le instanceof Player
				&& !((Player) le).isSneaking()) {
			if (type.equals(SkillType.Charge))
				MessageStore.sendSystemMsg((Player) le, "차징 스킬은 쉬프트 중에만 사용 가능합니다.");
			else if (type.equals(SkillType.Holding))
				MessageStore.sendSystemMsg((Player) le, "홀딩 스킬은 쉬프트 중에만 사용 가능합니다.");
			return true;
		}
		if (HoldingRunnable.has(le) || HoldingPerfectRunnable.has(le)) {
			le.sendMessage(ChatColor.RED + "홀딩 스킬 사용 중에는 다른 스킬 사용이 불가능합니다.");
			return true;
		}
		if (ChargingRunnable.has(le)) {
			le.sendMessage(ChatColor.RED + "차징 스킬 사용 중에는 다른 스킬 사용이 불가능합니다.");
			return true;
		}
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		if (ComboCoolRunnable.hasCombo(le, this)) {
			if (getSkillType(le).equals(SkillType.Chain)) {
				for (String key : ds.getUsesList(le)) {
					Skill skill = Skill.valueOf(key);
					if (skill != null)
						skill.otherUse(this, le);
				}
			}
			for (String key : ds.getUsesList(le)) {
				Skill skill = Skill.valueOf(key);
				if (skill != null)
					skill.otherUse(this, le);
			}
			return false;// 진행
		}
		double cool = getCooldownNow(le);
		if (!le.hasPermission(Permissions.skillTester.name())) {
			int max = getMaxStack(le);
			int now = getNowStack(le);
			if (max == 1) {
				if (cool > 0) {
					le.sendMessage(this.getDisplayName() + ChatColor.AQUA + " : " + cool);
					return true; // 종료
				} else
					SkillCoolRunnable.run(le, this);
			} else if (now > 0) {
				if (!SkillCoolRunnable.has(le, this))
					SkillCoolRunnable.run(le, this);
				setNowStack(le, now - 1);
			} else {
				le.sendMessage(this.getDisplayName() + ChatColor.AQUA + " : " + cool);
				return true; // 종료
			}
		} else
			SkillCoolRunnable.run(le, this, 0.05);
		for (String key : ds.getUsesList(le)) {
			Skill skill = Skill.valueOf(key);
			if (skill != null)
				skill.otherUse(this, le);
		}
		ActionBarRunnable.run(le);
		ds.changeSetting(le);
		return false;// 진행
	}

	public void otherUse(Skill pre, LivingEntity att) {

	}

	public double Attack(LivingEntity att, LivingEntity vic, double damage) {
		return damage;
	}

	public double Attacked(LivingEntity vic, LivingEntity att, double damage) {
		return damage;
	}

	public void Holding(LivingEntity att, int times) {

	}

	public void HoldingPerfectSucceed(LivingEntity att) {
		HoldingPerfectRunnable.cancel(att, this);
		Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
	}

	public void HoldingPerfectFail(LivingEntity att) {
		HoldingPerfectRunnable.cancel(att, this);
		Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
	}

	public void HoldingSucceed(LivingEntity att, int times) {
		HoldingRunnable.cancel(att, this);
		Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
	}

	public void HoldingFail(LivingEntity att, int times) {
		HoldingRunnable.cancel(att, this);
		Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
	}

	public void Charging(LivingEntity att, int times, int charge) {

	}

	public void ChargingFinish(LivingEntity att, int charge) {
		ChargingRunnable.cancel(att, this);
		Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
	}

	public void comboEnd(LivingEntity att) {
		ComboCoolRunnable.EndCombo(att, this);
		Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
	}

	public void End(LivingEntity att) {
		SkillCoolRunnable.end(att, this);
		int max = getMaxStack(att);
		if (max > 1) {
			int now = getNowStack(att) + 1;
			if (now < max) {
				setNowStack(att, now);
				SkillCoolRunnable.run(att, this);
			} else
				setNowStack(att, max);
		}
		Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
	}

	public void BuffEnd(LivingEntity att, int num) {
		BuffRunnable.cancel(att, this, num, false);
		Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
	}

	public void DebuffEnd(LivingEntity att, LivingEntity vic, int num) {
		DebuffRunnable.cancel(att, vic, this, num, false);
		Core.getCore().getHashMapStore().getDataStore(att).changeSetting(att);
		if (Core.getCore().getHashMapStore().getDataStore(vic) != null)
			Core.getCore().getHashMapStore().getDataStore(vic).changeSetting(vic);
	}

	public void applyKnockDown(LivingEntity att) {

	}

	public void applyDestruction(LivingEntity att) {
	}

	public void applyIdentity(LivingEntity att) {
		applyIdentity(att, this.identity / getTimes(att));
	}

	public void applyIdentity(LivingEntity att, double addIdentity) {
		Identity identity = Core.getCore().getHashMapStore().getDataStore(att).getIdentity();
		identity.setNow(identity.getNow() + addIdentity * getMultiplyIdentity(att));
	}

	public double getMultiplyIdentity(LivingEntity att) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
		return ds.getJob().getMultiplyIdentity(att) * getExtraMultiplyIdentity(att);
	}

	public void setExtraMultiplyIdentity(LivingEntity att, double extrMultiplyIdentity) {
		if (extrMultiplyIdentity != 0)
			this.extraMultiplyIdentity.put(att.getUniqueId(), extrMultiplyIdentity);
		else
			this.extraMultiplyIdentity.remove(att.getUniqueId());
	}

	public double getExtraMultiplyIdentity(LivingEntity att) {
		UUID uuid = att.getUniqueId();
		return 1 + (this.extraMultiplyIdentity.containsKey(uuid) ? this.extraMultiplyIdentity.get(uuid) : 0);
	}

	public int getMaxStack(LivingEntity att) {
		UUID uuid = att.getUniqueId();
		return maxStack.containsKey(uuid) ? maxStack.get(uuid) : 1;
	}

	public void setMaxStack(LivingEntity att, int max) {
		if (max > 1)
			maxStack.put(att.getUniqueId(), max);
		else
			maxStack.remove(att.getUniqueId());
	}

	public int getNowStack(LivingEntity att) {
		UUID uuid = att.getUniqueId();
		return nowStack.containsKey(uuid) ? nowStack.get(uuid) : 1;
	}

	public void setNowStack(LivingEntity att, int now) {
		if (now >= 0)
			nowStack.put(att.getUniqueId(), now);
		else
			nowStack.remove(att.getUniqueId());
	}

	public boolean damage(List<Entity> list, LivingEntity att, Entity vic_e, double damage) {
		return damage(true, false, list, att, vic_e, damage, AttackType.getAttackType(vic_e, att));
	}

	public boolean damage(boolean skill, List<Entity> list, LivingEntity att, Entity vic_e, double damage) {
		return damage(skill, false, list, att, vic_e, damage, AttackType.getAttackType(vic_e, att));
	}

	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage) {
		return damage(skill, destruction, list, att, vic_e, damage, AttackType.getAttackType(vic_e, att));
	}

	public boolean damage(List<Entity> list, LivingEntity att, Entity vic_e, double damage, AttackType now) {
		return damage(true, false, list, att, vic_e, damage, now);
	}

	public boolean damage(boolean skill, List<Entity> list, LivingEntity att, Entity vic_e, double damage,
			AttackType now) {
		return damage(skill, false, list, att, vic_e, damage, now);
	}

	public boolean damage(boolean skill, boolean destruction, List<Entity> list, LivingEntity att, Entity vic_e,
			double damage, AttackType now) {
		if (!(vic_e instanceof LivingEntity) || vic_e.isInvulnerable())
			return false;
		if (vic_e.hasMetadata("owner")) {
			for (MetadataValue mv : vic_e.getMetadata("owner"))
				if (mv.getOwningPlugin().equals(Core.getCore())) {
					UUID uuid = UUID.fromString(mv.asString());
					if (uuid != null) {
						Entity owner = Bukkit.getEntity(uuid);
						if (owner.equals(att))
							return false;
					}
					break;
				}
		}
		if (list != null && list.contains(vic_e))
			return false;
		if (att instanceof Player && !((Player) att).isOnline())
			return false;
		if (att.getUniqueId().equals(vic_e.getUniqueId()))
			return false;
		if (skill) {
			applyIdentity(att);
			applyKnockDown(att);
		}
		if (destruction)
			applyDestruction(att);
		if (list != null)
			list.add(vic_e);

		LivingEntity vic = (LivingEntity) vic_e;
		if (now != null && Arrays.asList(types).contains(now)) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(att);
			ds.setAttackType(now);
			int ndt = vic.getNoDamageTicks();
			vic.setNoDamageTicks(0);
			vic.damage(damage * now.getTakeDamageMulti() * ds.getAttackTypeDamage(), att);
			vic.setNoDamageTicks(ndt);
		} else {
			int ndt = vic.getNoDamageTicks();
			vic.setNoDamageTicks(0);
			vic.damage(damage, att);
			vic.setNoDamageTicks(ndt);
		}
		return true;
	}

	public static ItemStack getNullItemStack() {
		ItemStack item = new ItemStack(Material.BEDROCK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + "빈 스킬");
		meta.setLocalizedName("skill");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "클릭시 스킬을 선택할 수 있습니다.");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getNullItemChoosenStack() {
		ItemStack item = new ItemStack(Material.GLOW_ITEM_FRAME);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + "빈 스킬");
		meta.setLocalizedName("skill");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "클릭시 스킬을 선택할 수 있습니다.");
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
		return item;
	}

	public ItemStack getItemStack(LivingEntity le) {
		DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
		int slot = ds.getSlot();
		Skill[] skills = ds.getSkills();
		if (slot > -1 && skills != null && slot < skills.length && skills[slot] == this)
			return getChoosenItemStack(le);
		if (ChargingRunnable.has(le, this) || HoldingRunnable.has(le, this) || HoldingPerfectRunnable.has(le, this))
			return getSneakingItemStack(le);
		if (ComboCoolRunnable.hasCombo(le, this))
			return getComboItemStack(le);
		if (BuffRunnable.has(le, this))
			return getBuffItemStack(le);
		int max = getMaxStack(le);
		int now = getNowStack(le);
		if ((max == 1 || now == 0) && getCooldownNow(le) >= 0.05d)
			return getCoolItemStack(le);
		ItemStack normal = getNormalItemStack(le);
		normal.setAmount(now);
		return normal;
	}

	public ItemStack getChoosenItemStack(LivingEntity le) {
		ItemStack item = new ItemStack(Material.GLOW_ITEM_FRAME);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.displayName);
		meta.setLocalizedName(getKey());
		meta.setLore(getLore(le));
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
		return item;
	}

	public ItemStack getNormalItemStack(LivingEntity le) {
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.displayName);
		meta.setLocalizedName(getKey());
		meta.setLore(getLore(le));
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack getInfoItemStack(LivingEntity le) {
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.displayName);
		meta.setLocalizedName(getKey());
		List<String> lore = new ArrayList<String>();
		lore.addAll(getLore(le));
		lore.add(ChatColor.GRAY + "클릭시 슬롯 변경");
		lore.add(ChatColor.GRAY + "우클릭시 전체 스킬 슬롯 초기화");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack getSneakingItemStack(LivingEntity le) {
		ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.displayName);
		meta.setLocalizedName(getKey());
		meta.setLore(getLore(le));
		meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack getComboItemStack(LivingEntity le) {
		ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.displayName);
		meta.setLocalizedName(getKey());
		meta.setLore(getLore(le));
		meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
		return item;
	}

	public ItemStack getBuffItemStack(LivingEntity le) {
		ItemStack item = new ItemStack(Material.END_CRYSTAL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.displayName);
		meta.setLocalizedName(getKey());
		meta.setLore(getLore(le));
		meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		item.setAmount(BuffRunnable.getCount(le, this));
		return item;
	}

	public ItemStack getCoolItemStack(LivingEntity le) {
		ItemStack item = new ItemStack(Material.BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(this.displayName);
		meta.setLocalizedName(getKey());
		meta.setLore(getLore(le));
		item.setItemMeta(meta);
		return item;
	}

	public abstract tripodChoice[] getTripod1();

	public int getTripod1Choice(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		return tripod1Choice.containsKey(uuid) ? tripod1Choice.get(uuid) : -1;
	}

	public void setTripod1Choice(LivingEntity le, int choice) {
		UUID uuid = le.getUniqueId();
		if (getLevel(le) < 4)
			choice = -1;
		if (choice >= 0)
			tripod1Choice.put(uuid, choice);
		else {
			tripod1Choice.remove(uuid);
			tripod2Choice.remove(uuid);
			tripod3Choice.remove(uuid);
		}
	}

	public abstract tripodChoice[] getTripod2();

	public int getTripod2Choice(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		return tripod2Choice.containsKey(uuid) ? tripod2Choice.get(uuid) : -1;
	}

	public void setTripod2Choice(LivingEntity le, int choice) {
		UUID uuid = le.getUniqueId();
		if (getLevel(le) < 7 || getTripod1Choice(le) == -1)
			choice = -1;
		if (choice >= 0)
			tripod2Choice.put(uuid, choice);
		else {
			tripod2Choice.remove(uuid);
			tripod3Choice.remove(uuid);
		}
	}

	public abstract tripodChoice[] getTripod3();

	public int getTripod3Choice(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		return tripod3Choice.containsKey(uuid) ? tripod3Choice.get(uuid) : -1;
	}

	public void setTripod3Choice(LivingEntity le, int choice) {
		UUID uuid = le.getUniqueId();
		if (getLevel(le) < 10 || getTripod1Choice(le) == -1 || getTripod2Choice(le) == -1)
			choice = -1;
		if (choice >= 0)
			tripod3Choice.put(uuid, choice);
		else
			tripod3Choice.remove(uuid);
	}

	public boolean equals(Skill skill) {
		return key.equals(skill.getKey());
	}

	public static Skill valueOf(ItemStack item) {
		if (item != null && item.hasItemMeta() && item.getItemMeta().hasLocalizedName())
			return valueOf(item.getItemMeta().getLocalizedName());
		return null;
	}

	public static Skill valueOf(String key) {
		for (Skill skill : list)
			if (skill.key.equalsIgnoreCase(key))
				return skill;
		return null;
	}

	public static List<Skill> values() {
		return list;
	}

	public double getCooldownNow(LivingEntity le) {
		UUID uuid = le.getUniqueId();
		if (!cooldowns.containsKey(uuid))
			cooldowns.put(uuid, new HashMap<Skill, Long>());
		HashMap<Skill, Long> hs = cooldowns.get(uuid);
		return hs.containsKey(this) ? (hs.get(this) - System.currentTimeMillis()) / 1000d : 0l;
	}

	public void setCooldownNow(LivingEntity le, long cool) {
		UUID uuid = le.getUniqueId();
		if (!cooldowns.containsKey(uuid))
			cooldowns.put(uuid, new HashMap<Skill, Long>());
		HashMap<Skill, Long> hs = cooldowns.get(uuid);
		hs.put(this, System.currentTimeMillis() + cool);
	}

	public static double getCooldownNow(LivingEntity le, Skill skill) {
		if (le instanceof Player && ((Player) le).getGameMode().equals(GameMode.CREATIVE))
			return 0l;
		return skill != null ? skill.getCooldownNow(le) : 0d;
	}

	public static void setCooldownNow(LivingEntity le, Skill skill, long cool) {
		skill.setCooldownNow(le, cool);
	}

	public static int getIdentitySlot() {
		return 34;
	}

	public static int getAwakeningSlot() {
		return 35;
	}

	public static enum SkillType {
		Normal(ChatColor.GRAY + "일반"), Combo(ChatColor.BLUE + "콤보"), Charge(ChatColor.GREEN + "차지"),
		Holding(ChatColor.GREEN + "홀딩"), Point(ChatColor.GOLD + "지점"), Casting(ChatColor.LIGHT_PURPLE + "캐스팅"),
		Chain(ChatColor.AQUA + "체인")
		//
		;

		private final String name;

		private SkillType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static enum AttackType {
		Back("백 어택", 1.05, 0.1, 1), Head("헤드 어택", 1.2, 0, 1.1)
		//
		;

		private final String displayName;
		private final double takeDamageMulti;
		private final double critialChance;
		private final double knockdownMulti;

		private AttackType(String displayName, double takeDamageMulti, double critialChance, double knockdownMulti) {
			this.displayName = displayName;
			this.takeDamageMulti = takeDamageMulti;
			this.critialChance = critialChance;
			this.knockdownMulti = knockdownMulti;
		}

		public String getDisplayName() {
			return displayName;
		}

		public double getTakeDamageMulti() {
			return takeDamageMulti;
		}

		public double getCritialChance() {
			return critialChance;
		}

		public double getKnockdownMulti() {
			return knockdownMulti;
		}

		public static AttackType getAttackType(Entity vic, Entity att) {
			return getAttackType(vic, att.getLocation());
		}

		public static AttackType getAttackType(Entity vic, Location att_loc) {
			Vector vic_dir = vic.getLocation().getDirection().clone().setY(0).normalize();
			Vector att_dir = vic.getLocation().clone().subtract(att_loc).toVector().setY(0).normalize();
			double angle = vic_dir.angle(att_dir);
			if (angle > 2.4) {
				// head
				return Head;
			} else if (angle < 0.65) {
				// back
				return Back;
			} else
				return null;
		}
	}

	public static class tripodChoice {
		private final static List<tripodChoice> list = new ArrayList<tripodChoice>();
		private final String display;
		private final List<String> lore;

		public tripodChoice(String display, String... lores) {
			this.display = display;
			this.lore = new ArrayList<String>();
			for (String lore : lores)
				this.lore.add(ChatColor.WHITE + lore);
			list.add(this);
		}

		public String getDisplay() {
			return display;
		}

		public ItemStack getItemStack() {
			ItemStack item = new ItemStack(Material.BOOKSHELF);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + display);
			meta.setLore(lore);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			return item;
		}

		public ItemStack getBlockItemStack() {
			ItemStack item = new ItemStack(Material.BEDROCK);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + display);
			meta.setLore(lore);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			return item;
		}

	}

	public static class skillGroupType {
		public final static skillGroupType Awakening = new skillGroupType(ChatColor.DARK_RED + "[각성기]");
		public final static skillGroupType Identity = new skillGroupType(ChatColor.AQUA + "[아이덴티티]");
		private final String name;

		private skillGroupType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static class warrior {
		public static class WarLord {
			public static class skillGroup {
				public final static skillGroupType Lance = new skillGroupType(ChatColor.RED + "[랜스 스킬]");
				public final static skillGroupType Normal = new skillGroupType(ChatColor.GRAY + "[일반 스킬]");
			}

			public static sharpSpear sharpSpear = new sharpSpear();
			public static risingSpear risingSpear = new risingSpear();
			public static fireBullet fireBullet = new fireBullet();
			public static dashUpperFire dashUpperFire = new dashUpperFire();
			public static chargeStinger chargeStinger = new chargeStinger();
			public static counterSpear counterSpear = new counterSpear();
			public static spearShot spearShot = new spearShot();
			public static burstCannon burstCannon = new burstCannon();
			public static shieldShove shieldShove = new shieldShove();
			public static bash bash = new bash();
			public static riffAttack riffAttack = new riffAttack();
			public static lightningOfGuardian lightningOfGuardian = new lightningOfGuardian();
			public static hookChain hookChain = new hookChain();
			public static shieldCharge shieldCharge = new shieldCharge();
			public static cryOfHate cryOfHate = new cryOfHate();
			public static shieldUpheaval shieldUpheaval = new shieldUpheaval();
			public static auraOfNelasia auraOfNelasia = new auraOfNelasia();

			public static class WarLordJT {
				public static protectionOfGuardian protectionOfGuardian = new protectionOfGuardian();
				public static warLordJTIdentity warLordJTIdentity = new warLordJTIdentity();
			}

			public static class WarLordGG {
				public static spearOfJudgment spearOfJudgment = new spearOfJudgment();
			}

			public static Skill[] values() {
				return new Skill[] { sharpSpear, risingSpear, fireBullet, dashUpperFire, chargeStinger, counterSpear,
						spearShot, burstCannon, shieldShove, bash, riffAttack, lightningOfGuardian, hookChain,
						shieldCharge, cryOfHate, shieldUpheaval, auraOfNelasia };
			}
		}

		public static class Berserker {

			public static class BerserkerGB {

			}

			public static class BerserkerGG {

			}
		}

		public static class Destroyer {
			public static class DestroyerBM {

			}

			public static class DestroyerJS {

			}
		}

		public static class HolyKnight {
			public static class HolyKnightS {

			}

			public static class HolyKnightCA {

			}
		}
	}

	public static class martialArtist {
		public static class BattleMaster {
			public static class BattleMasterC {

			}

			public static class BattleMasterAG {

			}
		}

		public static class Infighter {
			public static class InfighterCS {

			}

			public static class InfighterCD {

			}
		}

		public static class SoulMaster {
			public static class SoulMasterYC {

			}

			public static class SoulMasterSM {

			}
		}

		public static class LanceMaster {
			public static class LanceMasterJG {

			}

			public static class LanceMasterJJ {

			}
		}

		public static class Striker {
			public static class StrikerIP {

			}

			public static class StrikerON {

			}
		}
	}

	public static class hunter {
		public static class DevilHunter {
			public static class DevilHunterGM {

			}

			public static class DevilHunterHG {

			}
		}

		public static class Blaster {
			public static class BlasterHG {

			}

			public static class BlasterPG {

			}
		}

		public static class HawkEye {
			public static class HawkEyeDD {

			}

			public static class HawkEyeJS {

			}
		}

		public static class Scouter {
			public static class ScouterJU {

			}

			public static class ScouterAG {

			}
		}

		public static class Gunslinger {
			public static class GunslingerPM {

			}

			public static class GunslingerSS {

			}
		}
	}

	public static class mage {
		public static class Summoner {
			public static class SummonerSS {

			}

			public static class SummonerNG {

			}
		}

		public static class Arcana {
			public static class ArcanaHH {

			}

			public static class ArcanaHJ {

			}
		}

		public static class Bard {
			public static class BardJG {

			}

			public static class BardJY {

			}
		}

		public static class Sorceress {
			public static class SorceressJH {

			}

			public static class SorceressGR {

			}
		}
	}

	public static class assassin {
		public static class Blade {
			public static class BladeJG {

			}

			public static class BladeB {

			}
		}

		public static class Demonic {
			public static class DemonicMC {

			}

			public static class DemonicWE {

			}
		}

		public static class Reaper {
			public static class skillGroup {
				public final static skillGroupType Dagger = new skillGroupType(ChatColor.GREEN + "[단검 스킬]");
				public final static skillGroupType Shadow = new skillGroupType(ChatColor.DARK_PURPLE + "[그림자 스킬]");
				public final static skillGroupType SurpiseAttack = new skillGroupType(ChatColor.DARK_RED + "[급슥 스킬]");
			}

			public static nightMare nightMare = new nightMare();
			public static shadowDot shadowDot = new shadowDot();
			public static spiritCatch spiritCatch = new spiritCatch();
			public static spinningDagger spinningDagger = new spinningDagger();
			public static saberStinger saberStinger = new saberStinger();
			public static iblisto iblisto = new iblisto();
			public static phantomDancer phantomDancer = new phantomDancer();
			public static deathSide deathSide = new deathSide();
			public static distortion distortion = new distortion();
			public static callOfKnife callOfKnife = new callOfKnife();
			public static shadowStorm shadowStorm = new shadowStorm();
			public static blink blink = new blink();
			public static blackMist blackMist = new blackMist();
			public static shadowTrap shadowTrap = new shadowTrap();
			public static lastGraffiti lastGraffiti = new lastGraffiti();
			public static rageSpear rageSpear = new rageSpear();
			public static dancingOfFury dancingOfFury = new dancingOfFury();
			public static silentSmasher silentSmasher = new silentSmasher();

			public static class ReaperDS {
				public static lunarEclipseCadenza lunarEclipseCadenza = new lunarEclipseCadenza();
				public static reaperDSIdentity reaperDSIdentity = new reaperDSIdentity();
			}

			public static class ReaperGJ {
				public static eclipseRequiem eclipseRequiem = new eclipseRequiem();
			}

			public static Skill[] getShadows() {
				return new Skill[] { distortion, callOfKnife, shadowStorm, blink, blackMist, shadowTrap };
			}

			public static Skill[] getSurpiseAttacks() {
				return new Skill[] { lastGraffiti, rageSpear, dancingOfFury, silentSmasher };
			}

			public static Skill[] values() {
				return new Skill[] { nightMare, shadowDot, spiritCatch, spinningDagger, saberStinger, iblisto,
						phantomDancer, deathSide, distortion, callOfKnife, shadowStorm, blink, blackMist, shadowTrap,
						lastGraffiti, rageSpear, dancingOfFury, silentSmasher };
			}

		}
	}

	public static class specialist {
		public static class Artist {
			public static class ArtistMG {

			}

			public static class ArtistHG {

			}
		}

		public static class Meteorologist {
			public static class MeteorologistJN {

			}

			public static class MeteorologistIS {

			}
		}
	}

	public static class Rel {
		private final double front;
		private final double height;
		private final double right;

		public Rel(double front, double height, double right) {
			this.front = front;
			this.height = height;
			this.right = right;
		}

		public double getFront() {
			return front;
		}

		public double getHeight() {
			return height;
		}

		public double getRight() {
			return right;
		}
	}

	public class StackableBukkitTask {
		private int stack;
		private BukkitTask task;

		public StackableBukkitTask() {
			this.stack = 0;
			this.task = null;
		}

		public int getStack() {
			return stack;
		}

		public void setStack(int stack) {
			this.stack = stack;
		}

		public void setTask(BukkitTask task) {
			if (this.task == null || this.task.isCancelled())
				this.stack = 0;
			else
				this.task.cancel();
			this.task = task;
		}

		public void stop() {
			this.stack = 0;
			this.task.cancel();
		}
	}

	public static void removeDebuff(LivingEntity vic) {
		UUID uuid = vic.getUniqueId();
		if (blood.containsKey(uuid))
			for (StackableBukkitTask st : blood.get(uuid).values())
				st.stop();
		if (poison.containsKey(uuid))
			for (StackableBukkitTask st : poison.get(uuid).values())
				st.stop();
		if (fire.containsKey(uuid))
			for (StackableBukkitTask st : fire.get(uuid).values())
				st.stop();
	}

	protected enum tickType {
		blood, poison, fire
	}

	public class tickRunnable extends BukkitRunnable {
		final LivingEntity att;
		final LivingEntity vic;
		final double damage;
		final int max;
		final tickType type;
		final DataStore ds;
		int now;

		public tickRunnable(LivingEntity att, LivingEntity vic, double damage, int max, tickType type) {
			this.att = att;
			this.vic = vic;
			this.damage = damage;
			this.max = max;
			this.now = 0;
			this.type = type;
			this.ds = Core.getCore().getHashMapStore().getDataStore(att);
			;
		}

		@Override
		public void run() {
			if (att == null || vic == null || att.isDead() || vic.isDead()) {
				this.cancel();
				if (vic != null) {
					if (type.equals(tickType.blood) && blood.containsKey(vic.getUniqueId()))
						blood.get(vic.getUniqueId()).remove(Skill.this);
					if (type.equals(tickType.poison) && poison.containsKey(vic.getUniqueId()))
						poison.get(vic.getUniqueId()).remove(Skill.this);
					if (type.equals(tickType.fire) && fire.containsKey(vic.getUniqueId()))
						fire.get(vic.getUniqueId()).remove(Skill.this);
				}
				return;
			} else {
				now++;
				if (this.now >= max) {
					if (type.equals(tickType.blood) && blood.containsKey(vic.getUniqueId()))
						blood.get(vic.getUniqueId()).remove(Skill.this);
					if (type.equals(tickType.poison) && poison.containsKey(vic.getUniqueId()))
						poison.get(vic.getUniqueId()).remove(Skill.this);
					if (type.equals(tickType.fire) && fire.containsKey(vic.getUniqueId()))
						fire.get(vic.getUniqueId()).remove(Skill.this);
					this.cancel();
				}
				ds.setExtraCriticalChance("tick", -100);
				damage(false, new ArrayList<Entity>(), att, vic, damage, null);
				ds.setExtraCriticalChance("tick", 0);
				if (type.equals(tickType.blood))
					EffectStore.spawnBlockCrack(vic.getEyeLocation(), 10, Material.REDSTONE_BLOCK);
				else if (type.equals(tickType.poison)) {
					Random r = new Random();
					Location loc = vic.getEyeLocation();
					for (int i = 0; i < 10; i++) {
						EffectStore.spawnSpellMob(loc.clone().add(r.nextDouble() - 0.5, -0.25, r.nextDouble() - 0.5), 0,
								128, 0, SpellType.SPELL_MOB);
					}
				} else if (type.equals(tickType.fire))
					EffectStore.Directional.LAVA.spawnDirectional(vic.getEyeLocation(), 10, 0.1, 0.1, 0.1, 10);
			}
		}
	}

	public void runBlood(LivingEntity att, LivingEntity vic, double damage, int times) {
		runBlood(att, vic, damage, times, 1);
	}

	public void runBlood(LivingEntity att, LivingEntity vic, double damage, int times, int max) {
		if (vic instanceof LivingEntity && att == vic)
			return;
		UUID uuid = vic.getUniqueId();
		if (!blood.containsKey(uuid))
			blood.put(uuid, new HashMap<Skill, StackableBukkitTask>());
		HashMap<Skill, StackableBukkitTask> hs = blood.get(uuid);
		if (!hs.containsKey(this))
			hs.put(this, new StackableBukkitTask());
		StackableBukkitTask sbt = hs.get(this);
		int stack = sbt.getStack();
		stack += 1;
		if (stack > max)
			stack = max;
		sbt.setTask(
				new tickRunnable(att, vic, damage * stack, times, tickType.blood).runTaskTimer(Core.getCore(), 10, 20));
		sbt.setStack(stack);
	}

	public void runPoison(LivingEntity att, LivingEntity vic, double damage, int times) {
		runPoison(att, vic, damage, times, 1);
	}

	public void runPoison(LivingEntity att, LivingEntity vic, double damage, int times, int max) {
		if (att == vic)
			return;
		UUID uuid = vic.getUniqueId();
		if (!poison.containsKey(uuid))
			poison.put(uuid, new HashMap<Skill, StackableBukkitTask>());
		HashMap<Skill, StackableBukkitTask> hs = poison.get(uuid);
		if (!hs.containsKey(this))
			hs.put(this, new StackableBukkitTask());
		StackableBukkitTask sbt = hs.get(this);
		int stack = sbt.getStack();
		stack += 1;
		if (stack > max)
			stack = max;
		sbt.setTask(new tickRunnable(att, vic, damage * stack, times, tickType.poison).runTaskTimer(Core.getCore(), 10,
				20));
		sbt.setStack(stack);
	}

	public void runFire(LivingEntity att, LivingEntity vic, double damage, int times) {
		runFire(att, vic, damage, times, 1);
	}

	public void runFire(LivingEntity att, LivingEntity vic, double damage, int times, int max) {
		if (att == vic)
			return;
		UUID uuid = vic.getUniqueId();
		if (!fire.containsKey(uuid))
			fire.put(uuid, new HashMap<Skill, StackableBukkitTask>());
		HashMap<Skill, StackableBukkitTask> hs = fire.get(uuid);
		if (!hs.containsKey(this))
			hs.put(this, new StackableBukkitTask());
		StackableBukkitTask sbt = hs.get(this);
		int stack = sbt.getStack();
		stack += 1;
		if (stack > max)
			stack = max;
		sbt.setTask(
				new tickRunnable(att, vic, damage * stack, times, tickType.fire).runTaskTimer(Core.getCore(), 10, 20));
		sbt.setStack(stack);
	}

	public RayTraceResult getRayTraceResult(Location loc, double range, FluidCollisionMode mode,
			boolean ignorePassableBlocks, double raySize, Predicate<Entity> filter) {
		return getRayTraceResult(loc, loc.getDirection(), range, mode, ignorePassableBlocks, raySize, filter);
	}

	public RayTraceResult getRayTraceResult(Location loc, Vector dir, double range, FluidCollisionMode mode,
			boolean ignorePassableBlocks, double raySize, Predicate<Entity> filter) {
		return loc.getWorld().rayTrace(loc, dir, range, mode, ignorePassableBlocks, raySize, filter);
	}

	public RayTraceResult getRayTraceResultBlock(Location loc, double range, FluidCollisionMode mode,
			boolean ignorePassableBlocks) {
		return loc.getWorld().rayTraceBlocks(loc, loc.getDirection(), range, mode, ignorePassableBlocks);
	}

	public RayTraceResult getRayTraceResultBlock(Location loc, Vector dir, double range, FluidCollisionMode mode,
			boolean ignorePassableBlocks) {
		return loc.getWorld().rayTraceBlocks(loc, dir, range, mode, ignorePassableBlocks);
	}

	public RayTraceResult getRayTraceResultEntity(Location loc, double range, double raySize,
			Predicate<Entity> filter) {
		return loc.getWorld().rayTraceEntities(loc, loc.getDirection(), range, raySize, filter);
	}

	public RayTraceResult getRayTraceResultEntity(Location loc, Vector dir, double range, double raySize,
			Predicate<Entity> filter) {
		return loc.getWorld().rayTraceEntities(loc, dir, range, raySize, filter);
	}

	public Location getPointBlock(Location loc, double range) {
		Vector dir = loc.getDirection();
		RayTraceResult rtr = getRayTraceResultBlock(loc, dir, range, FluidCollisionMode.NEVER, true);
		if (rtr != null && rtr.getHitBlock() != null) {
			BlockFace face = rtr.getHitBlockFace();
			return rtr.getHitBlock().getLocation().add(0.5 + face.getModX() * 0.5, 0.5 + face.getModY() * 0.5,
					0.5 + face.getModZ() * 0.5);
		}

		return loc.clone().add(dir.clone().setY(0).multiply(range));
	}

	public Location getPointEntity(LivingEntity att, Location loc, double range) {
		Vector dir = loc.getDirection();
		RayTraceResult rtr = getRayTraceResultEntity(loc, dir, range, 0.1, new Predicate<Entity>() {
			@Override
			public boolean apply(Entity et) {
				return et instanceof LivingEntity && !(et.equals(att));
			}
		});
		if (rtr != null && rtr.getHitEntity() != null) {
			Entity et = rtr.getHitEntity();
			if (et instanceof LivingEntity)
				return ((LivingEntity) et).getEyeLocation().clone().subtract(0, 0.25, 0);
			else
				return et.getLocation();
		}

		return loc.clone().add(dir.clone().setY(0).multiply(range));
	}

	public Location getPoint(LivingEntity att, Location loc, double range) {
		Vector dir = loc.getDirection();
		RayTraceResult rtr = getRayTraceResult(loc, dir, range, FluidCollisionMode.NEVER, true, 0.1,
				new Predicate<Entity>() {
					@Override
					public boolean apply(Entity et) {
						return et instanceof LivingEntity && !(et.equals(att));
					}
				});
		if (rtr != null)
			if (rtr.getHitEntity() != null) {
				Entity et = rtr.getHitEntity();
				if (et instanceof LivingEntity)
					return ((LivingEntity) et).getEyeLocation().clone().subtract(0, 0.25, 0);
				else
					return et.getLocation();
			} else if (rtr.getHitBlock() != null) {
				BlockFace face = rtr.getHitBlockFace();
				return rtr.getHitBlock().getLocation().add(0.5 + face.getModX() * 0.5, 0.5 + face.getModY() * 0.5,
						0.5 + face.getModZ() * 0.5);
			}
		return loc.clone().add(dir.clone().setY(0).multiply(range));
	}
}
