package com.StarJ.LM.Items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Items {
	private static final List<Items> list = new ArrayList<Items>();
	//
	public static final Info info = new Info();
	//
	protected final String key;
	protected final String displayName;
	protected final Material type;

	public Items(String key, String displayName, Material type) {
		this.key = key;
		this.displayName = displayName;
		this.type = type;
		list.add(this);
	}

	public abstract List<String> getLore(LivingEntity le);

	public ItemStack getItem(LivingEntity le) {
		ItemStack i = new ItemStack(type);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(displayName);
		meta.setLocalizedName(key);
		meta.setLore(getLore(le));
		i.setItemMeta(meta);
		return i;
	}

	public boolean Use(LivingEntity le, Block target) {
		return false;
	}

	public boolean Use(LivingEntity le, LivingEntity target) {
		return false;
	}

	public boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot, ClickType type,
			ItemStack currentItem, ItemStack cursor) {
		return false;
	}

	public static Items valueOf(ItemStack item) {
		if (item != null && item.hasItemMeta() && item.getItemMeta().hasLocalizedName())
			return valueOf(item.getItemMeta().getLocalizedName());

		return null;
	}

	public static Items valueOf(String key) {
		for (Items i : values())
			if (i.key.equalsIgnoreCase(key))
				return i;
		return null;
	}

	public static List<Items> values() {
		return list;
	}
}
