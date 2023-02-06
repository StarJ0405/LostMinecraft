package com.StarJ.LM.System;

import java.lang.reflect.Field;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;

public class EffectStore {
	public static void spawnRedStone(Location loc, int R, int G, int B, float size, int count, double offsetX,
			double offsetY, double offsetZ) {
		DustOptions option = new DustOptions(Color.fromRGB(R, G, B), size);
		loc.getWorld().spawnParticle(Particle.REDSTONE, loc, count, offsetX, offsetY, offsetZ, option);
	}

	public static void spawnRedStone(Player target, Location loc, int R, int G, int B, float size, int count,
			double offsetX, double offsetY, double offsetZ) {
		DustOptions option = new DustOptions(Color.fromRGB(R, G, B), size);
		target.spawnParticle(Particle.REDSTONE, loc, count, offsetX, offsetY, offsetZ, option);
	}

	public static enum SpellType {
		SPELL(Particle.SPELL), SPELL_INSTANT(Particle.SPELL_INSTANT), SPELL_MOB(Particle.SPELL_MOB),
		SPELL_MOB_AMBIENT(Particle.SPELL_MOB_AMBIENT), SPELL_WITCH(Particle.SPELL_WITCH),
		//
		;

		private Particle Paticle;

		private SpellType(Particle Paticle) {
			this.Paticle = Paticle;
		}

		public Particle getPaticle() {
			return Paticle;
		}
	}

	public static void spawnSpellMob(Location loc, int R, int G, int B, SpellType type) {
		if (R > 255)
			R = R % 255;
		if (G > 255)
			G = G % 255;
		if (B > 255)
			B = B % 255;
		loc.getWorld().spawnParticle(type.getPaticle(), loc, 0, R / 255D, G / 255D, B / 255D, 1);
	}

	public static void spawnSpellMob(Player target, Location loc, int R, int G, int B, SpellType type) {
		if (R > 255)
			R = R % 255;
		if (G > 255)
			G = G % 255;
		if (B > 255)
			B = B % 255;
		target.spawnParticle(type.getPaticle(), loc, 0, R / 255D, G / 255D, B / 255D, 1);
	}

	public static void spawnNote(Location loc, double note) {
		if (note > 24)
			note = note % 24;
		loc.getWorld().spawnParticle(Particle.NOTE, loc, 0, note / 24D, 0, 0, 1);
	}

	public static void spawnNote(Player target, Location loc, double note) {
		if (note > 24)
			note = note % 24;
		target.spawnParticle(Particle.NOTE, loc, 0, note / 24D, 0, 0, 1);
	}

	public static void spawnItemCrack(Location loc, int count, Material type) {
		loc.getWorld().spawnParticle(Particle.ITEM_CRACK, loc, count, new ItemStack(type));
	}

	public static void spawnItemCrack(Player target, Location loc, int count, Material type) {
		target.spawnParticle(Particle.ITEM_CRACK, loc, count, new ItemStack(type));
	}

	public static void spawnBlockCrack(Location loc, int count, Material type) {
		if (type.isBlock())
			loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, count, type.createBlockData());
	}

	public static void spawnBlockCrack(Player target, Location loc, int count, Material type) {
		if (type.isBlock())
			target.spawnParticle(Particle.BLOCK_CRACK, loc, count, type.createBlockData());
	}

	public static void spawnBlockDust(Location loc, int count, Material type) {
		if (type.isBlock())
			loc.getWorld().spawnParticle(Particle.BLOCK_DUST, loc, count, type.createBlockData());
	}

	public static void spawnBlockDust(Player target, Location loc, int count, Material type) {
		if (type.isBlock())
			target.spawnParticle(Particle.BLOCK_DUST, loc, count, type.createBlockData());
	}

	public static void spawnFallingDust(Location loc, int count, Material type) {
		if (type.isBlock())
			loc.getWorld().spawnParticle(Particle.FALLING_DUST, loc, count, type.createBlockData());
	}

	public static void spawnFallingDust(Player target, Location loc, int count, Material type) {
		if (type.isBlock())
			target.spawnParticle(Particle.FALLING_DUST, loc, count, type.createBlockData());
	}

	public static enum Directional {
		EXPLOSION_NORMAL(Particle.EXPLOSION_NORMAL), EXPLOSION_LARGE(Particle.EXPLOSION_LARGE),
		EXPLOSION_HUGE(Particle.EXPLOSION_HUGE), FIREWORKS_SPARK(Particle.FIREWORKS_SPARK),
		WATER_BUBBLE(Particle.WATER_BUBBLE), WATER_SPLASH(Particle.WATER_SPLASH), WATER_WAKE(Particle.WATER_WAKE),
		SUSPENDED(Particle.SUSPENDED), SUSPENDED_DEPTH(Particle.SUSPENDED_DEPTH), CRIT(Particle.CRIT),
		CRIT_MAGIC(Particle.CRIT_MAGIC), SMOKE_NORMAL(Particle.SMOKE_NORMAL), SMOKE_LARGE(Particle.SMOKE_LARGE),
		DRIP_WATER(Particle.DRIP_WATER), DRIP_LAVA(Particle.DRIP_LAVA), VILLAGER_ANGRY(Particle.VILLAGER_ANGRY),
		VILLAGER_HAPPY(Particle.VILLAGER_HAPPY), TOWN_AURA(Particle.TOWN_AURA), PORTAL(Particle.PORTAL),
		ENCHANTMENT_TABLE(Particle.ENCHANTMENT_TABLE), FLAME(Particle.FLAME), LAVA(Particle.LAVA),
		CLOUD(Particle.CLOUD), SNOWBALL(Particle.SNOWBALL), SNOW_SHOVEL(Particle.SNOW_SHOVEL), SLIME(Particle.SLIME),
		HEART(Particle.HEART), WATER_DROP(Particle.WATER_DROP), MOB_APPEARANCE(Particle.MOB_APPEARANCE),
		DRAGON_BREATH(Particle.DRAGON_BREATH), END_ROD(Particle.END_ROD), DAMAGE_INDICATOR(Particle.DAMAGE_INDICATOR),
		SWEEP_ATTACK(Particle.SWEEP_ATTACK), TOTEM(Particle.TOTEM), SPIT(Particle.SPIT), SQUID_INK(Particle.SQUID_INK),
		BUBBLE_POP(Particle.BUBBLE_POP), CURRENT_DOWN(Particle.CURRENT_DOWN),
		BUBBLE_COLUMN_UP(Particle.BUBBLE_COLUMN_UP), NAUTILUS(Particle.NAUTILUS), DOLPHIN(Particle.DOLPHIN),
		SNEEZE(Particle.SNEEZE), CAMPFIRE_COSY_SMOKE(Particle.CAMPFIRE_COSY_SMOKE),
		CAMPFIRE_SIGNAL_SMOKE(Particle.CAMPFIRE_SIGNAL_SMOKE), COMPOSTER(Particle.COMPOSTER), FLASH(Particle.FLASH),
		FALLING_LAVA(Particle.FALLING_LAVA), LANDING_LAVA(Particle.LANDING_LAVA), FALLING_WATER(Particle.FALLING_WATER),
		DRIPPING_HONEY(Particle.DRIPPING_HONEY), FALLING_HONEY(Particle.FALLING_HONEY),
		LANDING_HONEY(Particle.LANDING_HONEY), FALLING_NECTAR(Particle.FALLING_NECTAR),
		SOUL_FIRE_FLAME(Particle.SOUL_FIRE_FLAME), ASH(Particle.ASH), CRIMSON_SPORE(Particle.CRIMSON_SPORE),
		WARPED_SPORE(Particle.WARPED_SPORE), SOUL(Particle.SOUL),
		DRIPPING_OBSIDIAN_TEAR(Particle.DRIPPING_OBSIDIAN_TEAR), FALLING_OBSIDIAN_TEAR(Particle.FALLING_OBSIDIAN_TEAR),
		LANDING_OBSIDIAN_TEAR(Particle.LANDING_OBSIDIAN_TEAR), REVERSE_PORTAL(Particle.REVERSE_PORTAL),
		WHITE_ASH(Particle.WHITE_ASH), DUST_COLOR_TRANSITION(Particle.DUST_COLOR_TRANSITION),
		VIBRATION(Particle.VIBRATION), FALLING_SPORE_BLOSSOM(Particle.FALLING_SPORE_BLOSSOM),
		SPORE_BLOSSOM_AIR(Particle.SPORE_BLOSSOM_AIR), SMALL_FLAME(Particle.SMALL_FLAME), SNOWFLAKE(Particle.SNOWFLAKE),
		DRIPPING_DRIPSTONE_LAVA(Particle.DRIPPING_DRIPSTONE_LAVA),
		FALLING_DRIPSTONE_LAVA(Particle.FALLING_DRIPSTONE_LAVA),
		DRIPPING_DRIPSTONE_WATER(Particle.DRIPPING_DRIPSTONE_WATER),
		FALLING_DRIPSTONE_WATER(Particle.FALLING_DRIPSTONE_WATER), GLOW_SQUID_INK(Particle.GLOW_SQUID_INK),
		GLOW(Particle.GLOW), WAX_ON(Particle.WAX_ON), WAX_OFF(Particle.WAX_OFF),
		ELECTRIC_SPARK(Particle.ELECTRIC_SPARK), SCRAPE(Particle.SCRAPE), BLOCK_MARKER(Particle.BLOCK_MARKER)
		//
		;

		final Particle particle;

		private Directional(Particle p) {
			this.particle = p;
		}

		public Particle getParticle() {
			return particle;
		}

		public void spawnDirectional(Location loc, int count, double offsetX, double offsetY, double offsetZ,
				double speed) {
			loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed);
		}

		public void spawnDirectional(Player target, Location loc, int count, double offsetX, double offsetY,
				double offsetZ, double speed) {
			target.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed);
		}

	}

	public static abstract class Infos {
		private final Vector rel;
		protected final OfflinePlayer target;

		public Infos(Vector rel) {
			this(rel, null);
		}

		public Infos(Vector rel, OfflinePlayer target) {
			this.rel = rel;
			this.target = target;
		}

		protected Location getAddedLocation(Location pre) {
			return pre.clone().add(rel);
		}

		public abstract void spawn(Location pre);
	}

	public static class RedStoneInfos extends Infos {
		private final int R;
		private final int G;
		private final int B;
		private final float size;
		private final int count;
		private final double offsetX;
		private final double offsetY;
		private final double offsetZ;

		public RedStoneInfos(Vector rel, int R, int G, int B, float size, int count, double offsetX, double offsetY,
				double offsetZ) {
			this(rel, R, G, B, size, count, offsetX, offsetY, offsetZ, null);
		}

		public RedStoneInfos(Vector rel, int R, int G, int B, float size, int count, double offsetX, double offsetY,
				double offsetZ, OfflinePlayer target) {
			super(rel, target);
			this.R = R;
			this.G = G;
			this.B = B;
			this.size = size;
			this.count = count;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
		}

		@Override
		public void spawn(Location pre) {
			Location loc = getAddedLocation(pre);
			if (target != null) {
				if (target.isOnline())
					spawnRedStone(target.getPlayer(), loc, R, G, B, size, count, offsetX, offsetY, offsetZ);
			} else
				spawnRedStone(loc, R, G, B, size, count, offsetX, offsetY, offsetZ);
		}
	}

	public static class SpellMobInfos extends Infos {
		private final int R;
		private final int G;
		private final int B;
		private final SpellType type;

		public SpellMobInfos(Vector rel, int R, int G, int B, SpellType type) {
			this(rel, R, G, B, type, null);
		}

		public SpellMobInfos(Vector rel, int R, int G, int B, SpellType type, OfflinePlayer target) {
			super(rel, target);
			this.R = R;
			this.G = G;
			this.B = B;
			this.type = type;
		}

		@Override
		public void spawn(Location pre) {
			Location loc = getAddedLocation(pre);
			if (target != null) {
				if (target.isOnline())
					spawnSpellMob(target.getPlayer(), loc, R, G, B, type);
			} else
				spawnSpellMob(loc, R, G, B, type);
		}

	}

	public static class NoteInfos extends Infos {
		private final double note;

		public NoteInfos(Vector rel, double note) {
			this(rel, note, null);
		}

		public NoteInfos(Vector rel, double note, OfflinePlayer target) {
			super(rel, target);
			this.note = note;
		}

		@Override
		public void spawn(Location pre) {
			Location loc = getAddedLocation(pre);
			if (target != null) {
				if (target.isOnline())
					spawnNote(target.getPlayer(), loc, note);
			} else
				spawnNote(loc, note);
		}
	}

	public static class ItemCrackInfos extends Infos {
		private final int count;
		private final Material type;

		public ItemCrackInfos(Vector rel, int count, Material type) {
			this(rel, count, type, null);
		}

		public ItemCrackInfos(Vector rel, int count, Material type, OfflinePlayer target) {
			super(rel, target);
			this.count = count;
			this.type = type;
		}

		@Override
		public void spawn(Location pre) {
			Location loc = getAddedLocation(pre);
			if (target != null) {
				if (target.isOnline())
					spawnItemCrack(target.getPlayer(), loc, count, type);
			} else
				spawnItemCrack(loc, count, type);
		}
	}

	public static class BlockCrackInfos extends Infos {
		private final int count;
		private final Material type;

		public BlockCrackInfos(Vector rel, int count, Material type) {
			this(rel, count, type, null);
		}

		public BlockCrackInfos(Vector rel, int count, Material type, OfflinePlayer target) {
			super(rel, target);
			this.count = count;
			this.type = type;
		}

		@Override
		public void spawn(Location pre) {
			Location loc = getAddedLocation(pre);
			if (target != null) {
				if (target.isOnline())
					spawnBlockCrack(target.getPlayer(), loc, count, type);
			} else
				spawnBlockCrack(loc, count, type);
		}
	}

	public static class BlockDustInfos extends Infos {
		private final int count;
		private final Material type;

		public BlockDustInfos(Vector rel, int count, Material type) {
			this(rel, count, type, null);
		}

		public BlockDustInfos(Vector rel, int count, Material type, OfflinePlayer target) {
			super(rel, target);
			this.count = count;
			this.type = type;
		}

		@Override
		public void spawn(Location pre) {
			Location loc = getAddedLocation(pre);
			if (target != null) {
				if (target.isOnline())
					spawnBlockDust(target.getPlayer(), loc, count, type);
			} else
				spawnBlockDust(loc, count, type);
		}
	}

	public static class FallingDustInfos extends Infos {
		private final int count;
		private final Material type;

		public FallingDustInfos(Vector rel, int count, Material type) {
			this(rel, count, type, null);
		}

		public FallingDustInfos(Vector rel, int count, Material type, OfflinePlayer target) {
			super(rel, target);
			this.count = count;
			this.type = type;
		}

		@Override
		public void spawn(Location pre) {
			Location loc = getAddedLocation(pre);
			if (target != null) {
				if (target.isOnline())
					spawnFallingDust(target.getPlayer(), loc, count, type);
			} else
				spawnFallingDust(loc, count, type);
		}
	}

	public static class DirectionalInfos extends Infos {
		private final Directional type;
		private final int count;
		private final double offsetX;
		private final double offsetY;
		private final double offsetZ;
		private final double speed;

		public DirectionalInfos(Vector rel, Directional type, int count, double offsetX, double offsetY, double offsetZ,
				double speed) {
			this(rel, type, count, offsetX, offsetY, offsetZ, speed, null);
		}

		public DirectionalInfos(Vector rel, Directional type, int count, double offsetX, double offsetY, double offsetZ,
				double speed, OfflinePlayer target) {
			super(rel, target);
			this.type = type;
			this.count = count;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.speed = speed;
		}

		@Override
		public void spawn(Location pre) {
			Location loc = getAddedLocation(pre);
			if (target != null) {
				if (target.isOnline())
					type.spawnDirectional(target.getPlayer(), loc, count, offsetX, offsetY, offsetZ, speed);
			} else
				type.spawnDirectional(loc, count, offsetX, offsetY, offsetZ, speed);
		}
	}

	public static Vector getRel(Vector dir, double front, double height, double right) {
		return getRel(dir, front, height, right, 1);
	}

	public static Vector getRel(Vector dir, double front, double height, double right, double size) {
		dir = dir.clone().normalize();
		double x = dir.getX() * front * size - dir.getZ() * right * size;
		double y = height * size;
		double z = dir.getZ() * front * size + dir.getX() * right * size;
		return new Vector(x, y, z);
	}

	public static void setSpin(Player player, boolean spin) {
		try {
			EntityPlayer ep = ((CraftPlayer) player).getHandle();
			DataWatcher dataWatcher = ep.al();
			setField(dataWatcher, "registrationLocked", false);
			dataWatcher.b(DataWatcherRegistry.a.a(8), spin ? (byte) 0x04 : (byte) 0x00);
			setField(dataWatcher, "registrationLocked", true);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

	private static void setField(Object instance, String name, Object value) throws ReflectiveOperationException {
		Validate.notNull(instance);
		Field field = instance.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(instance, value);
	}

}
