package com.StarJ.LM.System;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.System.DataStore.ActionBarRunnable;

import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.monster.EntityZombie;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;

public enum EntityStore {
	Scarecrow("허수아비") {
		@Override
		public Entity spawnEntity(Location loc) {
			WorldServer server = getServer(loc);
			if (server == null)
				return null;
			ARMOR_STAND armor = new ARMOR_STAND(server);
			armor.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			server.b(armor);
			Entity et = armor.getBukkitEntity();
			ArmorStand as = (ArmorStand) et;
			as.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			as.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
			as.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
			as.setCustomName(this.displayName);
			as.setCustomNameVisible(true);
			as.setMetadata("type", new FixedMetadataValue(Core.getCore(), name()));
			return as;
		}

		@Override
		public boolean canLoad() {
			return true;
		}

		class ARMOR_STAND extends EntityArmorStand {
			public ARMOR_STAND(World world) {
				super(EntityTypes.d, world);
			}

			@Override
			public boolean a(DamageSource damagesource, float f) {
				return super.a(damagesource, f);
			}

			@Override
			public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
				return EnumInteractionResult.d;
			}

			@Override
			public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
				return EnumInteractionResult.d;
			}

		}
	},
	Damage("데미지 표기용") {
		@Override
		public Entity spawnEntity(Location loc) {
			WorldServer server = getServer(loc);
			if (server == null)
				return null;
			ARMOR_STAND armor = new ARMOR_STAND(server);
			armor.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			server.b(armor);
			Entity et = armor.getBukkitEntity();
			ArmorStand as = (ArmorStand) et;
			as.setCustomName(this.displayName);
			as.setCustomNameVisible(true);
			as.setInvisible(true);
			as.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10, 0, true, false));
			as.setMetadata("type", new FixedMetadataValue(Core.getCore(), name()));
			as.setInvulnerable(true);
			return as;
		}

		class ARMOR_STAND extends EntityArmorStand {
			public ARMOR_STAND(World world) {
				super(EntityTypes.d, world);
				e(true);
				persistentInvisibility = true;
				b(5, true);
			}

			@Override
			public boolean a(DamageSource damagesource, float f) {
				return super.a(damagesource, 0);
			}

			@Override
			public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
				return EnumInteractionResult.d;

			}

			@Override
			public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
				return EnumInteractionResult.d;
			}
		}
	},
	Minion("미니온") {
		@Override
		public Entity spawnEntity(Location loc) {
			WorldServer server = getServer(loc);
			if (server == null)
				return null;
			Minions armor = new Minions(server);
			armor.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			server.b(armor);
			Entity et = armor.getBukkitEntity();
			ArmorStand as = (ArmorStand) et;
			as.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100d);
			as.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
			as.getEquipment().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
			as.getEquipment().setItemInOffHand(new ItemStack(Material.SHIELD));
			as.setCustomName(this.displayName);
			as.setCustomNameVisible(true);
			as.setMetadata("type", new FixedMetadataValue(Core.getCore(), name()));
			return as;
		}

		class Minions extends EntityArmorStand {

			public Minions(World world) {
				super(EntityTypes.d, world);
			}

			@Override
			public void a(RemovalReason entity_removalreason) {
				super.a(entity_removalreason);
				Core.getCore().getHashMapStore().removeDataStore((LivingEntity) this.getBukkitEntity());
			}

			@Override
			public void l() {
				super.l();
				LivingEntity le = (LivingEntity) this.getBukkitEntity();
				if (le.hasMetadata("skill"))
					for (MetadataValue mv : le.getMetadata("skill"))
						if (mv.getOwningPlugin().equals(Core.getCore())) {
							Skill skill = Skill.valueOf(mv.asString());
							if (skill != null)
								skill.Use(le);
						}
			}

			@Override
			public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
				return EnumInteractionResult.d;
			}

			@Override
			public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
				return EnumInteractionResult.d;
			}
		}
	},
	Persona("페르소나") {
		@Override
		public Entity spawnEntity(Location loc) {
			WorldServer server = getServer(loc);
			if (server == null)
				return null;
			Persona armor = new Persona(server);
			armor.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			server.b(armor);
			Entity et = armor.getBukkitEntity();
			Zombie zb = (Zombie) et;
			zb.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100d);
			zb.setHealth(100d);
			zb.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(0d);
			zb.getEquipment().setHelmet(new ItemStack(Material.ACACIA_BUTTON));
			zb.getEquipment().setHelmetDropChance(0f);
			zb.getEquipment().setChestplateDropChance(0f);
			zb.getEquipment().setLeggingsDropChance(0f);
			zb.getEquipment().setBootsDropChance(0f);
			zb.getEquipment().setItemInMainHandDropChance(0f);
			zb.getEquipment().setItemInOffHandDropChance(0f);
			zb.setCustomName(this.displayName);
			zb.setCustomNameVisible(true);
			zb.setMetadata("type", new FixedMetadataValue(Core.getCore(), name()));
			double health = (JobStore.ReaperDS.getMaxHealth() + JobStore.ReaperGJ.getMaxHealth()) / 2;
			zb.setMetadata("maxHealth", new FixedMetadataValue(Core.getCore(), health));
			zb.setMetadata("health", new FixedMetadataValue(Core.getCore(), health));
			ActionBarRunnable.run(zb);
			return zb;
		}

		class Persona extends EntityZombie {

			public Persona(WorldServer server) {
				super(EntityTypes.bk, server);
			}

			protected void u() {
				this.bS.a(5, (PathfinderGoal) new PathfinderGoalMeleeAttack(this, 1.0D, true));
				this.bT.a(1, (PathfinderGoal) new PathfinderGoalOwnerTarget(this));
			}

			class PathfinderGoalOwnerTarget extends PathfinderGoalTarget {
				private EntityLiving target;

				public PathfinderGoalOwnerTarget(EntityZombie entityzombie) {
					super(entityzombie, false);
					a(EnumSet.of(PathfinderGoal.Type.d));
				}

				public boolean a() {
					LivingEntity le = (LivingEntity) this.e.getBukkitEntity();
					if (!le.hasMetadata("owner"))
						return false;
					LivingEntity owner = null;
					for (MetadataValue mv : le.getMetadata("owner"))
						if (mv.getOwningPlugin().equals(Core.getCore())) {
							UUID uuid = UUID.fromString(mv.asString());
							if (uuid == null)
								return false;
							Entity et = Bukkit.getEntity(uuid);
							if (!(et instanceof LivingEntity))
								return false;
							owner = (LivingEntity) et;
							break;
						}
					if (owner == null)
						return false;
					LivingEntity target = Core.getCore().getHashMapStore().getTarget(owner);
					if (target == null || target == owner)
						return false;
					this.target = ((CraftLivingEntity) target).getHandle();
					return a(this.target, PathfinderTargetCondition.a);
				}

				public void c() {
					this.e.setTarget(this.target, EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true);
					super.c();
				}
			}
		}
	},
	Shadow("그림자") {
		@Override
		public Entity spawnEntity(Location loc) {
			WorldServer server = getServer(loc);
			if (server == null)
				return null;
			Shadow armor = new Shadow(server);
			armor.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			server.b(armor);
			Entity et = armor.getBukkitEntity();
			Zombie zb = (Zombie) et;
			zb.getEquipment().setHelmet(new ItemStack(Material.ACACIA_BUTTON));
			zb.getEquipment().setHelmetDropChance(0f);
			zb.getEquipment().setChestplateDropChance(0f);
			zb.getEquipment().setLeggingsDropChance(0f);
			zb.getEquipment().setBootsDropChance(0f);
			zb.getEquipment().setItemInMainHandDropChance(0f);
			zb.getEquipment().setItemInOffHandDropChance(0f);
			zb.setCustomName(this.displayName);
			zb.setCustomNameVisible(true);
			zb.setInvulnerable(true);
			zb.setGravity(false);
			zb.setSilent(true);
			zb.setMetadata("type", new FixedMetadataValue(Core.getCore(), name()));
			return zb;
		}

		class Shadow extends EntityZombie {
			public Shadow(WorldServer server) {
				super(EntityTypes.bk, server);
			}
			
			protected void u() {
			}
		}
	}
	//
	;

	protected final String displayName;

	private EntityStore(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	protected WorldServer getServer(Location loc) {
		if (Core.getCore().isVanillaWorld(loc.getWorld()))
			return null;
		return ((CraftWorld) loc.getWorld()).getHandle();
	}

	public abstract Entity spawnEntity(Location loc);

	public boolean canLoad() {
		return false;
	}

	public void Load(LivingEntity le, FileConfiguration fc) {
		spawnEntity(le.getLocation());
		le.remove();
	}

	public static void LoadEntity(LivingEntity le) {
		try {
			File file = new File("plugins/LostMinecraft/Entities/" + le.getUniqueId().toString() + ".yml");
			FileConfiguration fc = new YamlConfiguration();
			if (file.exists()) {
				fc.load(file);
				if (!le.hasMetadata("type") && fc.isString("type"))
					try {
						EntityStore es = EntityStore.valueOf(fc.getString("type"));
						if (es != null)
							es.Load(le, fc);
					} catch (Exception ex) {

					}
				file.delete();
			}
			if (le.hasMetadata("type"))
				for (MetadataValue mv : le.getMetadata("type"))
					if (mv.getOwningPlugin().equals(Core.getCore()))
						try {
							EntityStore es = EntityStore.valueOf(mv.asString());
							if (es != null)
								es.Load(le, fc);
						} catch (Exception ex) {

						}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void deleteEntity(LivingEntity le) {
		File file = new File("plugins/LostMinecraft/Entities/" + le.getUniqueId().toString() + ".yml");
		if (file.exists())
			file.delete();
	}

	public void SaveEntity(LivingEntity le) {
		if (canLoad())
			try {
				File file = new File("plugins/LostMinecraft/Entities/" + le.getUniqueId().toString() + ".yml");
				File loc = new File("plugins/LostMinecraft/Entities");
				FileConfiguration fc = new YamlConfiguration();
				if (!file.exists()) {
					loc.mkdirs();
					file.createNewFile();
				}
				fc.load(file);
				//
				fc.set("type", this.name());
				//
				fc.save(file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		else
			le.remove();
	}

	public static EntityStore valueOf(Entity et) {
		if (et != null && et.hasMetadata("type"))
			for (MetadataValue mv : et.getMetadata("type"))
				if (mv.getOwningPlugin().equals(Core.getCore()))
					try {
						return valueOf(mv.asString());
					} catch (Exception ex) {

					}
		return null;
	}

}
