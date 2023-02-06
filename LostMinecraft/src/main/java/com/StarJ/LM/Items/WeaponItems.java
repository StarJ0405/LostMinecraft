package com.StarJ.LM.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.System.JobStore;

public class WeaponItems extends Items {
	// 전사
	public static final WeaponItems WarLordJT = new WeaponItems(JobStore.WarLordJT, 1, 500,
			Skill.warrior.WarLord.WarLordJT.protectionOfGuardian, Skill.warrior.WarLord.WarLordJT.warLordJTIdentity);
	public static final WeaponItems WarLordGG = new WeaponItems(JobStore.WarLordGG, 1, 500,
			Skill.warrior.WarLord.WarLordGG.spearOfJudgment, null);
	public static final WeaponItems BerserkerGB = new WeaponItems(JobStore.BerserkerGB, 1, 500, null, null);
	public static final WeaponItems BerserkerGG = new WeaponItems(JobStore.BerserkerGG, 1, 500, null, null);
	public static final WeaponItems DestroyerBM = new WeaponItems(JobStore.DestroyerBM, 1, 500, null, null);
	public static final WeaponItems DestroyerJS = new WeaponItems(JobStore.DestroyerJS, 1, 500, null, null);
	public static final WeaponItems HolyKnightS = new WeaponItems(JobStore.HolyKnightS, 1, 500, null, null);
	public static final WeaponItems HolyKnightCA = new WeaponItems(JobStore.HolyKnightCA, 1, 500, null, null);
	// 무도가
	public static final WeaponItems BattleMasterC = new WeaponItems(JobStore.BattleMasterC, 1, 500, null, null);
	public static final WeaponItems BattleMasterAG = new WeaponItems(JobStore.BattleMasterAG, 1, 500, null, null);
	public static final WeaponItems InfighterCS = new WeaponItems(JobStore.InfighterCS, 1, 500, null, null);
	public static final WeaponItems InfighterCD = new WeaponItems(JobStore.InfighterCD, 1, 500, null, null);
	public static final WeaponItems SoulMasterYC = new WeaponItems(JobStore.SoulMasterYC, 1, 500, null, null);
	public static final WeaponItems SoulMasterSM = new WeaponItems(JobStore.SoulMasterSM, 1, 500, null, null);
	public static final WeaponItems LanceMasterJG = new WeaponItems(JobStore.LanceMasterJG, 1, 500, null, null);
	public static final WeaponItems LanceMasterJJ = new WeaponItems(JobStore.LanceMasterJJ, 1, 500, null, null);
	public static final WeaponItems StrikerIP = new WeaponItems(JobStore.StrikerIP, 1, 500, null, null);
	public static final WeaponItems StrikerON = new WeaponItems(JobStore.StrikerON, 1, 500, null, null);
	// 헌터
	public static final WeaponItems DevilHunterGM = new WeaponItems(JobStore.DevilHunterGM, 1, 500, null, null);
	public static final WeaponItems DevilHunterHG = new WeaponItems(JobStore.DevilHunterHG, 1, 500, null, null);
	public static final WeaponItems BlasterHG = new WeaponItems(JobStore.BlasterHG, 1, 500, null, null);
	public static final WeaponItems BlasterPG = new WeaponItems(JobStore.BlasterPG, 1, 500, null, null);
	public static final WeaponItems HawkEyeDD = new WeaponItems(JobStore.HawkEyeDD, 1, 500, null, null);
	public static final WeaponItems HawkEyeJS = new WeaponItems(JobStore.HawkEyeJS, 1, 500, null, null);
	public static final WeaponItems ScouterJU = new WeaponItems(JobStore.ScouterJU, 1, 500, null, null);
	public static final WeaponItems ScouterAG = new WeaponItems(JobStore.ScouterAG, 1, 500, null, null);
	public static final WeaponItems GunslingerPM = new WeaponItems(JobStore.GunslingerPM, 1, 500, null, null);
	public static final WeaponItems GunslingerSS = new WeaponItems(JobStore.GunslingerSS, 1, 500, null, null);
	// 마법사
	public static final WeaponItems SummonerSS = new WeaponItems(JobStore.SummonerSS, 1, 500, null, null);
	public static final WeaponItems SummonerNG = new WeaponItems(JobStore.SummonerNG, 1, 500, null, null);
	public static final WeaponItems ArcanaHH = new WeaponItems(JobStore.ArcanaHH, 1, 500, null, null);
	public static final WeaponItems ArcanaHJ = new WeaponItems(JobStore.ArcanaHJ, 1, 500, null, null);
	public static final WeaponItems BardJG = new WeaponItems(JobStore.BardJG, 1, 500, null, null);
	public static final WeaponItems BardJY = new WeaponItems(JobStore.BardJY, 1, 500, null, null);
	public static final WeaponItems SorceressJH = new WeaponItems(JobStore.SorceressJH, 1, 500, null, null);
	public static final WeaponItems SorceressGR = new WeaponItems(JobStore.SorceressGR, 1, 500, null, null);
	// 암살자
	public static final WeaponItems BladeJG = new WeaponItems(JobStore.BladeJG, 1, 500, null, null);
	public static final WeaponItems BladeB = new WeaponItems(JobStore.BladeB, 1, 500, null, null);
	public static final WeaponItems DemonicMC = new WeaponItems(JobStore.DemonicMC, 1, 500, null, null);
	public static final WeaponItems DemonicWE = new WeaponItems(JobStore.DemonicWE, 1, 500, null, null);
	public static final WeaponItems ReaperDS = new WeaponItems(JobStore.ReaperDS, 2.5, 200,
			Skill.assassin.Reaper.ReaperDS.lunarEclipseCadenza, Skill.assassin.Reaper.ReaperDS.reaperDSIdentity);
	public static final WeaponItems ReaperGJ = new WeaponItems(JobStore.ReaperGJ, 4, 125, null, null);
	// 스페셜리스트
	public static final WeaponItems ArtistMG = new WeaponItems(JobStore.ArtistMG, 1, 500, null, null);
	public static final WeaponItems ArtistHG = new WeaponItems(JobStore.ArtistHG, 1, 500, null, null);
	public static final WeaponItems MeteorologistJN = new WeaponItems(JobStore.MeteorologistJN, 1, 500, null, null);
	public static final WeaponItems MeteorologistIS = new WeaponItems(JobStore.MeteorologistIS, 1, 500, null, null);
	//
	private final double attackCooldown;
	private final double damage;
	private final Skill awakeningSkill;
	private final Skill identitySkill;

	public WeaponItems(JobStore job, double attackCooldown, double damage, Skill awakeningSkil, Skill identitySkill) {
		super(job.name(), job.getDisplayName(), job.getType());
		this.attackCooldown = attackCooldown;
		this.damage = damage;
		this.awakeningSkill = awakeningSkil;
		this.identitySkill = identitySkill;
	}

	@Override
	public List<String> getLore(LivingEntity le) {
		List<String> lore = new ArrayList<String>();
		if (awakeningSkill != null)
			lore.add(ChatColor.GREEN + "각성기(Q) : " + awakeningSkill.getDisplayName());
		if (identitySkill != null)
			lore.add(ChatColor.GREEN + "아덴(F) : " + identitySkill.getDisplayName());
		lore.add(ChatColor.AQUA + "공격 속도 : " + attackCooldown);
		lore.add(ChatColor.AQUA + "공격 피해 : " + damage);
		lore.add(ChatColor.GRAY + "DPS : " + damage * attackCooldown);
		return lore;
	}

	public void useF(LivingEntity att) {
		if (identitySkill != null)
			if (att instanceof Player) {
				identitySkill.Use((Player) att);
			} else
				identitySkill.Use(att);
	}

	public void useQ(LivingEntity att) {
		if (awakeningSkill != null)
			if (att instanceof Player) {
				awakeningSkill.Use((Player) att);
			} else
				awakeningSkill.Use(att);
	}

	public Skill getIdentitySkill() {
		return identitySkill;
	}

	public Skill getAwakeningSkill() {
		return awakeningSkill;
	}

	@Override
	public ItemStack getItem(LivingEntity le) {
		ItemStack i = new ItemStack(type);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLocalizedName(key);
		meta.setLore(getLore(le));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
				new AttributeModifier(UUID.randomUUID(), "damage", damage, Operation.ADD_NUMBER, EquipmentSlot.HAND));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(),
				"attackCooldown", attackCooldown, Operation.ADD_NUMBER, EquipmentSlot.HAND));
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.setUnbreakable(true);
		i.setItemMeta(meta);
		return i;
	}

}
