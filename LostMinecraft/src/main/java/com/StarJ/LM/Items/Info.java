package com.StarJ.LM.Items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.StarJ.LM.System.GUIStore;

public class Info extends Items {

	public Info() {
		super("info", ChatColor.WHITE + "정보창", Material.COMPASS);
	}

	@Override
	public boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot, ClickType type,
			ItemStack currentItem, ItemStack cursor) {
		if (type.equals(ClickType.RIGHT) || type.equals(ClickType.SHIFT_RIGHT))
			GUIStore.Skills.openInv(player);
		else
			GUIStore.Info.openInv(player);

		return super.Click(player, click, view, slot, rawSlot, type, currentItem, cursor);
	}

	@Override
	public List<String> getLore(LivingEntity le) {
		List<String> list = new ArrayList<String>();
		list.add(ChatColor.WHITE + "좌클릭시 정보창");
		list.add(ChatColor.WHITE + "우클릭시 스킬창");
		return list;
	}

}
