package com.StarJ.LM.Listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import com.StarJ.LM.Core;
import com.StarJ.LM.Items.Items;
import com.StarJ.LM.Items.WeaponItems;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.Skills.Runnable.DebuffRunnable;
import com.StarJ.LM.System.ConfigStore;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.DataStore.ActionBarRunnable;
import com.StarJ.LM.System.EntityStore;
import com.StarJ.LM.System.GUIStore;
import com.StarJ.LM.System.GUIStore.InventoryItem;

public class PlayerListener implements Listener {
	@EventHandler
	public void Events(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		ConfigStore.loadJob(player);
	}

	@EventHandler
	public void Events(PlayerQuitEvent e) {
	}

	@EventHandler
	public void Events(FoodLevelChangeEvent e) {
		Player player = (Player) e.getEntity();
		if (!Core.getCore().isVanillaWorld(player.getWorld()))
			e.setFoodLevel(19);
	}

	@EventHandler
	public void Events(PlayerToggleSneakEvent e) {
		Player player = e.getPlayer();
		World world = player.getWorld();
		if (Core.getCore().isPvp(world)) {
			ActionBarRunnable.run(player);
		}
	}

	@EventHandler
	public void Events(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		World world = player.getWorld();
		if (e.getAction().equals(Action.PHYSICAL) && e.getClickedBlock() != null
				&& e.getClickedBlock().getType().equals(Material.FARMLAND)) {
			e.setCancelled(true);
			return;
		} else if (e.getHand() != null && e.getHand().equals(EquipmentSlot.HAND))
			if (!Core.getCore().isVanillaWorld(world)) {
				if (Core.getCore().isPvp(world)) {
					if (e.getAction().equals(Action.RIGHT_CLICK_AIR)
							|| e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						ItemStack main = player.getInventory().getItemInMainHand();
						Items i = Items.valueOf(main);
						if (i != null)
							e.setCancelled(!i.Use(player, e.getClickedBlock()));
						else if (main.hasItemMeta() && main.getItemMeta().hasLocalizedName()) {
							String local = main.getItemMeta().getLocalizedName();
							if (local.equals("skill") || local.equals("empty") || Skill.valueOf(local) != null)
								e.setCancelled(true);
						}
					} else
						e.setCancelled(true);
				}
			}
	}

	@EventHandler
	public void Events(PlayerInteractEntityEvent e) {
		Player player = e.getPlayer();
		World world = player.getWorld();
		if (e.getHand().equals(EquipmentSlot.HAND))
			if (!Core.getCore().isVanillaWorld(world)) {
				ItemStack main = player.getInventory().getItemInMainHand();
				Items i = Items.valueOf(main);
				Entity target = e.getRightClicked();
				if (i != null && target instanceof LivingEntity)
					e.setCancelled(!i.Use(player, (LivingEntity) target));
				else if (main.hasItemMeta() && main.getItemMeta().hasLocalizedName()) {
					String local = main.getItemMeta().getLocalizedName();
					if (local.equals("skill") || local.equals("empty") || Skill.valueOf(local) != null)
						e.setCancelled(true);
				}
			}
	}

	@EventHandler
	public void Events(PlayerInteractAtEntityEvent e) {
		Player player = e.getPlayer();
		World world = player.getWorld();
		if (e.getHand().equals(EquipmentSlot.HAND))
			if (!Core.getCore().isVanillaWorld(world)) {
				ItemStack main = player.getInventory().getItemInMainHand();
				Items i = Items.valueOf(main);
				Entity target = e.getRightClicked();
				// DPS
				if (target.hasMetadata("type")) {
					String type = "";
					for (MetadataValue mv : target.getMetadata("type"))
						if (mv.getOwningPlugin().equals(Core.getCore()))
							type = mv.asString();
					EntityStore es = EntityStore.valueOf(type);
					if (es != null && es.equals(EntityStore.Scarecrow)) {
						ItemStack remove = new ItemStack(Material.BARRIER);
						ItemMeta meta = remove.getItemMeta();
						meta.setDisplayName(ChatColor.RED + "지우기");
						meta.setLocalizedName(target.getUniqueId().toString());
						remove.setItemMeta(meta);
						InventoryItem ii = new InventoryItem(remove, 8);
						GUIStore.DPS.openInv(player, ii);
					}

					e.setCancelled(true);
				} else if (i != null && target instanceof LivingEntity)
					e.setCancelled(!i.Use(player, (LivingEntity) target));
				else if (main.hasItemMeta() && main.getItemMeta().hasLocalizedName()) {
					String local = main.getItemMeta().getLocalizedName();
					if (local.equals("skill") || local.equals("empty") || Skill.valueOf(local) != null)
						e.setCancelled(true);
				}
			}
	}

	@EventHandler
	public void Events(PlayerItemHeldEvent e) {
		Player player = e.getPlayer();
		World world = player.getWorld();
		if (!Core.getCore().isVanillaWorld(world)) {
			if (Core.getCore().isPvp(world)) {
				DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
				int slot = e.getNewSlot();
				Skill skill = null;
				if (slot < 7)
					skill = ds.getSkills()[slot];
				else if (slot == 8)
					skill = ds.getSkills()[slot - 1];
				if (skill != null)
					skill.Use(player);
				player.getInventory().setHeldItemSlot(7);
			}
		}
	}

	@EventHandler
	public void Events(PlayerSwapHandItemsEvent e) {
		Player player = e.getPlayer();
		World world = player.getWorld();
		ItemStack main = e.getOffHandItem();
		if (!Core.getCore().isVanillaWorld(world)) {
			if (Core.getCore().isPvp(world)) {
				e.setCancelled(true);
				Items i = Items.valueOf(main);
				if (i != null && i instanceof WeaponItems)
					((WeaponItems) i).useF(player);
			}
		}
	}

	@EventHandler
	public void Events(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		World world = player.getWorld();
		ItemStack item = e.getItemDrop().getItemStack();
		if (!Core.getCore().isVanillaWorld(world)) {
			if (Core.getCore().isPvp(world)) {
				e.setCancelled(true);
				Items i = Items.valueOf(item);
				if (i != null && i instanceof WeaponItems)
					((WeaponItems) i).useQ(player);
			}
		}
	}

	@EventHandler
	public void Events(PlayerDeathEvent e) {
		Player player = e.getEntity();
		World world = player.getWorld();
		if (!Core.getCore().isVanillaWorld(world)) {
			if (Core.getCore().isPvp(world)) {
				Skill.removeDebuff(player);
				BuffRunnable.cancelAll(player);
				DebuffRunnable.cancelAll(player);
			}
		}
	}

	@EventHandler
	public void Events(EntityDeathEvent e) {
		LivingEntity le = e.getEntity();
		World world = le.getWorld();
		if (!Core.getCore().isVanillaWorld(world)) {
			if (Core.getCore().isPvp(world)) {
				Skill.removeDebuff(le);
				BuffRunnable.cancelAll(le);
				DebuffRunnable.cancelAll(le);
			}
		}
	}

	@EventHandler
	public void Events(PlayerRespawnEvent e) {
		Player player = e.getPlayer();
		World world = player.getWorld();
		if (!Core.getCore().isVanillaWorld(world)) {
			e.setRespawnLocation(world.getSpawnLocation());
			if (Core.getCore().isPvp(world)) {
				DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
				ds.setHealth(ds.getMaxHealth());
			}
		}
	}

}
