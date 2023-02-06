package com.StarJ.LM.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.StarJ.LM.Core;
import com.StarJ.LM.Items.Items;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.GUIStore;

public class InventoryListener implements Listener {

	@EventHandler
	public void Events(InventoryClickEvent e) {
		Inventory click = e.getClickedInventory();
		if (click == null)
			return;
		InventoryView view = e.getView();
		Player player = (Player) e.getWhoClicked();
		GUIStore gui = GUIStore.getGUI(view);
		int slot = e.getSlot();
		int rawSlot = e.getRawSlot();
		ClickType type = e.getClick();
		ItemStack currentItem = e.getCurrentItem();
		ItemStack cursor = e.getCursor();
		if (gui != null)
			e.setCancelled(!gui.Click(player, click, view, slot, rawSlot, type, currentItem, currentItem));
		else {
			Items i = Items.valueOf(currentItem);
			if (i != null) {
				e.setCancelled(!i.Click(player, click, view, slot, rawSlot, type, currentItem, cursor));
			} else if (currentItem.hasItemMeta() && currentItem.getItemMeta().hasLocalizedName()) {
				String local = currentItem.getItemMeta().getLocalizedName();
				if (local.equals("empty") || (slot >= 9 && (local.equals("skill") || Skill.valueOf(local) != null)))
					e.setCancelled(true);
				else if (slot < 9 && (local.equals("skill") || Skill.valueOf(local) != null)) {
					e.setCancelled(true);
					DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
					ds.setSlot(slot < 7 ? slot : (slot == 8 ? 7 : -1));
					ds.changeSetting(player);
					GUIStore.SkillChoice.openInv(player);
				}
			}
		}
	}

	@EventHandler
	public void Events(InventoryDragEvent e) {
		if (e.getInventory() == null)
			return;
		GUIStore gui = GUIStore.getGUI(e.getView());
		if (gui != null)
			e.setCancelled(true);
	}

	@EventHandler
	public void Events(InventoryCloseEvent e) {
		Player player = (Player) e.getPlayer();
		GUIStore gui = GUIStore.getGUI(e.getView());
		if (gui != null)
			gui.Close(player);
	}
}
