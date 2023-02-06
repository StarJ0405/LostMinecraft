package com.StarJ.LM.System;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.StarJ.LM.Core;
import com.google.gson.JsonObject;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementDisplay;
import net.minecraft.advancements.AdvancementFrameType;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionInstance;
import net.minecraft.advancements.critereon.LootSerializationContext;
import net.minecraft.commands.CustomFunction;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;

public class MessageStore {
	public static void sendSystemMsg(Player player, String msg) {
		player.sendMessage(ChatColor.WHITE + "<" + ChatColor.GOLD + "로마" + ChatColor.WHITE + "> " + msg);
	}

	public static void sendServerMsg(String msg) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + msg);
	}

	public static void sendErrorMsg(String msg) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + msg);
	}

	public static void sendActionbar(Player player, String msg) {
		((CraftPlayer) player).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
	}

	public static void sendActionBar(String msg) {
		for (Player player : Bukkit.getOnlinePlayers())
			((CraftPlayer) player).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
	}

	public static void sendToastMsg(Player player, Material icon, String msg) {
		sendToastMsg(player, icon, AdvancementFrameType.a, msg);
	}

	public static void sendToastMsg(Player player, Material icon, AdvancementFrameType frame, String msg) {
		new Toast(icon, msg, frame).sendMessage(player);
	}

	private static class Toast {
		private final String key;
		private final MinecraftKey mine_key;
		private final AdvancementDisplay display;
		private final AdvancementRewards advancement_reward;
		private final Advancement adv;
		private final String background;

		public Toast(Material icon, String msg, AdvancementFrameType frame) {
			this.key = "msg";
			this.mine_key = new MinecraftKey(Core.getCore().getName().toLowerCase(), key.toLowerCase());
			this.background = "textures/block/blue_concrete.png";
			this.display = new AdvancementDisplay(CraftItemStack.asNMSCopy(new ItemStack(icon)),
					IChatBaseComponent.ChatSerializer.a(msg), IChatBaseComponent.ChatSerializer.a(" "),
					new MinecraftKey("minecraft", background), frame, true, false, false);
			this.advancement_reward = new AdvancementRewards(0, new MinecraftKey[0], new MinecraftKey[0],
					new CustomFunction.a(mine_key));
			String[][] s = { { this.mine_key.toString() } };
			this.adv = new Advancement(this.mine_key, null, this.display, this.advancement_reward, getMap(), s);
		}

		public MinecraftKey getMinecraftkey() {
			return this.mine_key;
		}

		public Map<String, Criterion> getMap() {
			Map<String, Criterion> map = new HashMap<String, Criterion>();
			map.put(getMinecraftkey().toString(), new Criterion(null));

			new CriterionInstance() {
				public JsonObject a(LootSerializationContext context) {
					JsonObject obj = new JsonObject();
					obj.addProperty("isDone", false);
					return obj;
				}

				public MinecraftKey a() {
					return new MinecraftKey("minecraft", "impossible");
				}

			};
			return map;
		}

		public Advancement getAdvancement() {
			return this.adv;
		}

		public void sendMessage(Player player) {
			final EntityPlayer p = ((CraftPlayer) player).getHandle();
			final Advancement ad = getAdvancement();
			net.minecraft.advancements.AdvancementProgress progress = p.N().b(ad);
			if (!progress.a())
				p.N().a(ad, getMinecraftkey().toString());
			try {
				new BukkitRunnable() {
					public void run() {
						p.N().b(ad, getMinecraftkey().toString());
					}
				}.runTaskLater(Core.getCore(), 2);
			} catch (Exception ex) {

			}
		}
	}
}
