package com.StarJ.LM.System;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.Skills.Skill.tripodChoice;
import com.StarJ.LM.Skills.Runnable.BuffRunnable;
import com.StarJ.LM.System.DataStore.ActionBarRunnable;
import com.StarJ.LM.System.HashMapStore.DPS;
import com.StarJ.LM.System.JobStore.IdentityCalc;

public enum GUIStore {
	JobGroup(ChatColor.GREEN + "직업군 선택창") {
		@Override
		public void openInv(Player player, int size, InventoryItem... ii) {
			Inventory inv = Bukkit.createInventory(null, 5 * 9, getTitle());
			Group pre = Core.getCore().getHashMapStore().getDataStore(player).getJob().getGroup();
			Group now;
			// 전사
			now = Group.warrior;
			inv.setItem(10, getJobGroupItem(pre, now));
			// 무도가
			now = Group.martialArtist;
			inv.setItem(13, getJobGroupItem(pre, now));
			// 헌터
			now = Group.hunter;
			inv.setItem(16, getJobGroupItem(pre, now));
			// 마법사
			now = Group.mage;
			inv.setItem(28, getJobGroupItem(pre, now));
			// 암살자
			now = Group.assassin;
			inv.setItem(31, getJobGroupItem(pre, now));
			// 스페셜리스트
			now = Group.specialist;
			inv.setItem(34, getJobGroupItem(pre, now));

			inv.setItem(inv.getSize() - 1, getBack());
			player.openInventory(inv);
		}

		private ItemStack getJobGroupItem(Group pre, Group now) {
			ItemStack i = new ItemStack(now.getType());
			ItemMeta meta = i.getItemMeta();
			meta.setDisplayName(now.getName());
			meta.setLocalizedName(now.name());
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.WHITE + "클릭시 " + now.getName() + ChatColor.WHITE + " 선택창을 엽니다.");
			meta.setLore(lore);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			i.setItemMeta(meta);
			if (pre.equals(now))
				i.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			return i;
		}

		@Override
		public boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot, ClickType type,
				ItemStack currentItem, ItemStack cursor) {
			if (rawSlot == click.getSize() - 1)
				GUIStore.Info.openInv(player);
			if (currentItem != null && currentItem.hasItemMeta() && currentItem.getItemMeta().hasLocalizedName()) {
				Group group = Group.valueOf(currentItem.getItemMeta().getLocalizedName());
				if (group != null)
					group.openInv(player);
			}
			return false;
		}
	},
	Job(ChatColor.GREEN + "직업 선택창") {
		@Override
		public void openInv(Player player, int size, InventoryItem... ii) {
			Inventory inv = Bukkit.createInventory(null, size, getTitle());
			DataStore ps = Core.getCore().getHashMapStore().getDataStore(player);
			for (InventoryItem i : ii) {
				ItemStack item = i.getItem();
				JobStore job = JobStore.valueOf(item.getItemMeta().getLocalizedName());
				if (ps.getJob().equals(job)) {
					item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
					ItemMeta meta = item.getItemMeta();
					List<String> lore = meta.getLore();
					lore.add(ChatColor.GREEN + "현재 선택된 직업입니다.");
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					meta.setLore(lore);
					item.setItemMeta(meta);
					item.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
				}
				inv.setItem(i.getSlot(), item);
			}
			inv.setItem(size - 1, getBack());
			player.openInventory(inv);
		}

		@Override
		public boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot, ClickType type,
				ItemStack currentItem, ItemStack cursor) {
			if (currentItem != null && currentItem.hasItemMeta() && currentItem.getItemMeta().hasLocalizedName()) {
				JobStore job = JobStore.valueOf(currentItem.getItemMeta().getLocalizedName());
				if (job != null) {
					Core.getCore().getHashMapStore().getDataStore(player).setJob(player, job);
					MessageStore.sendSystemMsg(player, job.getDisplayName() + ChatColor.WHITE + "을 직업으로 선택하셨습니다.");
					job.getGroup().openInv(player);
					ConfigStore.saveJob(player);
				}
			}
			if (rawSlot == click.getSize() - 1)
				JobGroup.openInv(player);
			return false;
		}

	},
	Info(ChatColor.GREEN + "정보창") {

		@Override
		public void openInv(Player player, int size, InventoryItem... ii) {
			HashMapStore hs = Core.getCore().getHashMapStore();
			DataStore ds = hs.getDataStore(player);
			Inventory inv = Bukkit.createInventory(null, 3 * 9, ChatColor.GREEN + "정보창");

			inv.setItem(4, ds.getInfo());
			inv.setItem(8, getSkills());

			inv.setItem(10, getCritical(player));
			inv.setItem(12, getSpeed(player));
			inv.setItem(14, getEnduration(player));
			inv.setItem(16, getSpecialization(player));

			player.openInventory(inv);
		}

		public ItemStack getSkills() {
			ItemStack item = new ItemStack(Material.BOOK);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "스킬창");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.WHITE + "클릭시 스킬창");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

		@Override
		public boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot, ClickType type,
				ItemStack currentItem, ItemStack cursor) {
			if (rawSlot == 4)
				JobGroup.openInv(player);
			else if (rawSlot == 8)
				Skills.openInv(player);
			else {
				DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
				int normal = 1;
				int shift = 100;
				if (rawSlot == 10) {
					if (type.equals(ClickType.LEFT))
						ds.setCritical(ds.getCritical() + normal);
					else if (type.equals(ClickType.SHIFT_LEFT))
						ds.setCritical(ds.getCritical() + shift);
					else if (type.equals(ClickType.SWAP_OFFHAND))
						if (ds.getCritical() > 0)
							ds.setCritical(0);
						else
							ds.setCritical(ds.getMaxStat());
					else if (type.equals(ClickType.RIGHT))
						ds.setCritical(ds.getCritical() - normal);
					else if (type.equals(ClickType.SHIFT_RIGHT))
						ds.setCritical(ds.getCritical() - shift);
					click.setItem(rawSlot, getCritical(player));
				} else if (rawSlot == 12) {
					if (type.equals(ClickType.LEFT))
						ds.setSpeed(ds.getSpeed() + normal);
					else if (type.equals(ClickType.SHIFT_LEFT))
						ds.setSpeed(ds.getSpeed() + shift);
					else if (type.equals(ClickType.SWAP_OFFHAND))
						if (ds.getSpeed() > 0)
							ds.setSpeed(0);
						else
							ds.setSpeed(ds.getMaxStat());
					else if (type.equals(ClickType.RIGHT))
						ds.setSpeed(ds.getSpeed() - normal);
					else if (type.equals(ClickType.SHIFT_RIGHT))
						ds.setSpeed(ds.getSpeed() - shift);
					click.setItem(rawSlot, getSpeed(player));
				} else if (rawSlot == 14) {
					if (type.equals(ClickType.LEFT))
						ds.setEnduration(ds.getEnduration() + normal);
					else if (type.equals(ClickType.SHIFT_LEFT))
						ds.setEnduration(ds.getEnduration() + shift);
					else if (type.equals(ClickType.SWAP_OFFHAND))
						if (ds.getEnduration() > 0)
							ds.setEnduration(0);
						else
							ds.setEnduration(ds.getMaxStat());
					else if (type.equals(ClickType.RIGHT))
						ds.setEnduration(ds.getEnduration() - normal);
					else if (type.equals(ClickType.SHIFT_RIGHT))
						ds.setEnduration(ds.getEnduration() - shift);
					click.setItem(rawSlot, getEnduration(player));
				} else if (rawSlot == 16) {
					if (type.equals(ClickType.LEFT))
						ds.setSpecialization(ds.getSpecialization() + normal);
					else if (type.equals(ClickType.SHIFT_LEFT))
						ds.setSpecialization(ds.getSpecialization() + shift);
					else if (type.equals(ClickType.SWAP_OFFHAND))
						if (ds.getSpecialization() > 0)
							ds.setSpecialization(0);
						else
							ds.setSpecialization(ds.getMaxStat());
					else if (type.equals(ClickType.RIGHT))
						ds.setSpecialization(ds.getSpecialization() - normal);
					else if (type.equals(ClickType.SHIFT_RIGHT))
						ds.setSpecialization(ds.getSpecialization() - shift);
					JobStore job = ds.getJob();
					if (job != null) {
						if (BuffRunnable.has(player, job.getWeapon().getIdentitySkill()))
							BuffRunnable.cancelAll(player);
						job.setting(player);
					}
					click.setItem(rawSlot, getSpecialization(player));
				}
				click.setItem(4, ds.getInfo());
				ds.changeSetting(player);
				ActionBarRunnable.run(player);
				ConfigStore.savePlayerData(player, ds.getJob());
			}
			return false;
		}

		public ItemStack getCritical(LivingEntity le) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
			if (ds == null)
				return new ItemStack(Material.AIR);
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "치명");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GREEN + "스탯 : " + ds.getCritical());
			lore.add(ChatColor.YELLOW + "치명타 확률 증가량 : " + Math.round(ds.getCriticalChance(le) * 10000d) / 100d + "%");
			lore.add(ChatColor.WHITE + "클릭시 1씩 증가/감소");
			lore.add(ChatColor.WHITE + "쉬프트 클릭시 100씩 증가/감소");
			lore.add(ChatColor.RED + "F사용시 초기화 혹은 최대치");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

		public ItemStack getSpeed(LivingEntity le) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
			if (ds == null)
				return new ItemStack(Material.AIR);
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "신속");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GREEN + "스탯 : " + ds.getSpeed());
			lore.add(ChatColor.AQUA + "쿨타임 감소률 : " + Math.round((1 - ds.getReduceCooldown(le)) * 10000d) / 100d + "%");
			lore.add(ChatColor.AQUA + "이동속도 증가량 : " + Math.round((ds.getMuliplyWalkspeed(le)) * 10000d) / 100d + "%");
			lore.add(ChatColor.WHITE + "클릭시 1씩 증가/감소");
			lore.add(ChatColor.WHITE + "쉬프트 클릭시 100씩 증가/감소");
			lore.add(ChatColor.RED + "F사용시 초기화 혹은 최대치");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

		public ItemStack getEnduration(LivingEntity le) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
			if (ds == null)
				return new ItemStack(Material.AIR);
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "인내");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GREEN + "스탯 : " + ds.getEnduration());
			lore.add(ChatColor.GREEN + "체력 증가량 : " + Math.round((ds.getMultiplyHealth(le) - 1) * 10000d) / 100d + "%");
			lore.add(ChatColor.WHITE + "클릭시 1씩 증가/감소");
			lore.add(ChatColor.WHITE + "쉬프트 클릭시 100씩 증가/감소");
			lore.add(ChatColor.RED + "F사용시 초기화 혹은 최대치");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

		public ItemStack getSpecialization(LivingEntity le) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(le);
			if (ds == null)
				return new ItemStack(Material.AIR);
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "특화");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GREEN + "스탯 : " + ds.getSpecialization());
			lore.add(ChatColor.GOLD + "각성기 피해 증가량 : " + Math.round((ds.getMultiplyAwakening() - 1) * 10000d) / 100d
					+ "%");
			lore.add(ChatColor.GOLD + "아덴 획득 증가량 : "
					+ Math.round((ds.getJob().getMultiplyIdentity(ds.getSpecialization()) - 1) * 10000d) / 100d + "%");
			JobStore job = ds.getJob();
			if (job != null && job.getIdentityCalcs() != null)
				for (IdentityCalc ic : job.getIdentityCalcs())
					lore.add(ChatColor.GOLD + ic.getDisplayName() + " : "
							+ Math.round(ic.getLore(ds.getSpecialization()) * 10000d) / 100d + "%");
			lore.add(ChatColor.WHITE + "클릭시 1씩 증가/감소");
			lore.add(ChatColor.WHITE + "쉬프트 클릭시 100씩 증가/감소");
			lore.add(ChatColor.RED + "F사용시 초기화 혹은 최대치");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

	},
	SkillChoice(ChatColor.AQUA + "스킬 선택창") {
		@Override
		public void openInv(Player player, int size, InventoryItem... ii) {
			Inventory inv = Bukkit.createInventory(null, 6 * 9, getTitle());
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			JobStore job = ds.getJob();
			if (job != null) {
				Skill[] skills = job.getSkills();
				if (skills != null)
					for (int i = 0; i < skills.length; i++)
						if (skills[i] != null) {
							ItemStack s = skills[i].getNormalItemStack(player);
							s.setAmount(skills[i].getLevel(player));
							inv.setItem(i, s);
						}
			}

			player.openInventory(inv);
		}

		@Override
		public boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot, ClickType type,
				ItemStack currentItem, ItemStack cursor) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			if (ds.getSlot() > -1) {
				if (rawSlot < click.getSize()) {
					Skill skill = Skill.valueOf(currentItem);
					if (skill != null) {
						ds.setSkill(skill, ds.getSlot());
					} else
						ds.setSkill(null, ds.getSlot());
					ConfigStore.savePlayerData(player, ds.getJob());
					player.closeInventory();
				} else if (slot < 7) {
					Skill left = ds.getSkills()[ds.getSlot()];
					Skill right = ds.getSkills()[slot];
					ds.setSkill(left, slot);
					ds.setSkill(right, ds.getSlot());
					ds.setSlot(slot);
					ds.changeSetting(player);
					ConfigStore.savePlayerData(player, ds.getJob());
				} else if (slot == 8) {
					Skill left = ds.getSkills()[ds.getSlot()];
					Skill right = ds.getSkills()[slot];
					ds.setSkill(left, slot);
					ds.setSkill(right, ds.getSlot());
					ds.setSlot(slot - 1);
					ds.changeSetting(player);
					ConfigStore.savePlayerData(player, ds.getJob());
				}
			}
			return false;

		}

		@Override
		public void Close(Player player) {
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			ds.setSlot(-1);
			ds.changeSetting(player);
			super.Close(player);

		}
	},
	Skills(ChatColor.AQUA + "스킬창") {
		@Override
		public void openInv(Player player, int size, InventoryItem... ii) {
			Inventory inv = Bukkit.createInventory(null, 6 * 9, getTitle());
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			JobStore job = ds.getJob();
			if (job != null) {
				Skill[] skills = job.getSkills();
				if (skills != null)
					for (int i = 0; i < skills.length; i++)
						if (skills[i] != null) {
							ItemStack s = skills[i].getNormalItemStack(player);
							s.setAmount(skills[i].getLevel(player));
							inv.setItem(i, s);
						}
			}
			inv.setItem(inv.getSize() - 2, getReset());
			inv.setItem(inv.getSize() - 1, getBack());
			player.openInventory(inv);
		}

		@Override
		public boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot, ClickType type,
				ItemStack currentItem, ItemStack cursor) {
			if (rawSlot == click.getSize() - 1) {
				Info.openInv(player);
				return false;
			}
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			if (rawSlot == click.getSize() - 2) {
				JobStore job = ds.getJob();
				if (job != null)
					for (Skill skill : job.getSkills())
						skill.setLevel(player, 1);
				openInv(player);
				ds.changeSetting(player);
				ConfigStore.savePlayerData(player, ds.getJob());
				return false;
			}

			Skill skill = Skill.valueOf(currentItem);
			if (skill != null) {
				ds.setSetting(skill);
				SkillSetting.openInv(player);
			}
			return false;
		}

		public ItemStack getReset() {
			ItemStack item = new ItemStack(Material.BARRIER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "스킬 초기화");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.WHITE + "클릭시 스킬 전체 초기화");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}
	},
	SkillSetting(ChatColor.AQUA + "스킬 설정창") {
		@Override
		public void openInv(Player player, int size, InventoryItem... ii) {
			Inventory inv = Bukkit.createInventory(null, 6 * 9, getTitle());
			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			if (ds.getSetting() != null) {
				int remain = ds.getRemainSkillPoint(player);
				Skill skill = ds.getSetting();
				ItemStack normal = skill.getNormalItemStack(player);
				int level = skill.getLevel(player);
				normal.setAmount(level);
				inv.setItem(4, normal);

				inv.setItem(6, getSkillPoint(remain / 100));
				inv.setItem(7, getSkillPoint((remain / 10) % 10));
				inv.setItem(8, getSkillPoint(remain % 10));

				inv.setItem(11, getDownItem());
				inv.setItem(15, getUpItem());

				tripodChoice[] _t = skill.getTripod1();
				int choice = skill.getTripod1Choice(player);
				if (_t != null && _t.length > 0) {
					ItemStack _i[] = new ItemStack[] { _t[0].getItemStack(), _t[1].getItemStack(),
							_t[2].getItemStack() };
					if (level < 4)
						_i = new ItemStack[] { _t[0].getBlockItemStack(), _t[1].getBlockItemStack(),
								_t[2].getBlockItemStack() };
					else if (choice > -1 && choice < _t.length)
						_i[choice].addUnsafeEnchantment(Enchantment.DURABILITY, 0);

					inv.setItem(19, _i[0]);
					inv.setItem(22, _i[1]);
					inv.setItem(25, _i[2]);
				}
				_t = skill.getTripod2();
				choice = skill.getTripod2Choice(player);
				if (_t != null && _t.length > 0) {
					ItemStack _i[] = new ItemStack[] { _t[0].getItemStack(), _t[1].getItemStack(),
							_t[2].getItemStack() };
					if (level < 7)
						_i = new ItemStack[] { _t[0].getBlockItemStack(), _t[1].getBlockItemStack(),
								_t[2].getBlockItemStack() };
					else if (choice > -1 && choice < _t.length)
						_i[choice].addUnsafeEnchantment(Enchantment.DURABILITY, 0);

					inv.setItem(28, _i[0]);
					inv.setItem(31, _i[1]);
					inv.setItem(34, _i[2]);
				}
				_t = skill.getTripod3();
				choice = skill.getTripod3Choice(player);
				if (_t != null && _t.length > 0) {
					ItemStack _i[] = new ItemStack[] { _t[0].getItemStack(), _t[1].getItemStack(), };
					if (level < 10)
						_i = new ItemStack[] { _t[0].getBlockItemStack(), _t[1].getBlockItemStack(), };
					else if (choice > -1 && choice < _t.length)
						_i[choice].addUnsafeEnchantment(Enchantment.DURABILITY, 0);

					inv.setItem(38, _i[0]);
					inv.setItem(42, _i[1]);
				}
			}
			inv.setItem(inv.getSize() - 2, getReset());
			inv.setItem(inv.getSize() - 1, getBack());
			player.openInventory(inv);
		}

		public ItemStack getReset() {
			ItemStack item = new ItemStack(Material.BARRIER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "스킬 초기화");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.WHITE + "클릭시 해당 스킬 초기화");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

		public ItemStack getSkillPoint(int amount) {
			ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
			if (amount < 1)
				return new ItemStack(Material.BEDROCK);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "스킬포인트");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.RED + "" + amount);
			meta.setLore(lore);
			item.setItemMeta(meta);
			item.setAmount(amount);
			return item;
		}

		@SuppressWarnings("deprecation")
		public ItemStack getUpItem() {
			ItemStack item = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowUp"));
			meta.setDisplayName(ChatColor.GREEN + "스킬 레벨 올리기");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.WHITE + "클릭시 1레벨");
			lore.add(ChatColor.WHITE + "쉬프트 클릭시 4 / 7 / 10 / 12레벨");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

		@SuppressWarnings("deprecation")
		public ItemStack getDownItem() {
			ItemStack item = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowDown"));
			meta.setDisplayName(ChatColor.GREEN + "스킬 레벨 내리기");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.WHITE + "클릭시 1레벨");
			lore.add(ChatColor.WHITE + "쉬프트 클릭시 1 / 4 / 7 / 10레벨");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

		@Override
		public boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot, ClickType type,
				ItemStack currentItem, ItemStack cursor) {
			if (rawSlot == click.getSize() - 1) {
				Skills.openInv(player);
				return false;
			}

			DataStore ds = Core.getCore().getHashMapStore().getDataStore(player);
			Skill setting = ds.getSetting();
			if (setting != null) {
				int level = setting.getLevel(player);
				int remain = ds.getRemainSkillPoint(player);
				if (rawSlot == click.getSize() - 2) {
					setting.setLevel(player, 1);
				} else if (rawSlot == 15) {
					if (type.isShiftClick()) {
						int need = 0;
						int to = 0;
						if (level < 4)
							to = 4;
						else if (level < 7)
							to = 7;
						else if (level < 10)
							to = 10;
						else if (level <= 12)
							to = 12;
						for (int i = level; i < to; i++) {
							need += Skill.getNeedSkillPoint(i);
							if (remain >= need)
								level = i + 1;
						}
					} else if (remain >= Skill.getNeedSkillPoint(level))
						level += 1;
					if (level > setting.getMax())
						level = setting.getMax();
					setting.setLevel(player, level);
				} else if (rawSlot == 11) {
					if (type.isShiftClick()) {
						if (level > 10)
							level = 10;
						else if (level > 7)
							level = 7;
						else if (level > 4)
							level = 4;
						else
							level = 1;
					} else
						level -= 1;
					setting.setLevel(player, level);
				} else if (rawSlot == 19)
					if (type.isLeftClick())
						setting.setTripod1Choice(player, 0);
					else
						setting.setTripod1Choice(player, -1);
				else if (rawSlot == 22)
					if (type.isLeftClick())
						setting.setTripod1Choice(player, 1);
					else
						setting.setTripod1Choice(player, -1);
				else if (rawSlot == 25)
					if (type.isLeftClick())
						setting.setTripod1Choice(player, 2);
					else
						setting.setTripod1Choice(player, -1);
				else if (rawSlot == 28)
					if (type.isLeftClick())
						setting.setTripod2Choice(player, 0);
					else
						setting.setTripod2Choice(player, -1);
				else if (rawSlot == 31)
					if (type.isLeftClick())
						setting.setTripod2Choice(player, 1);
					else
						setting.setTripod2Choice(player, -1);
				else if (rawSlot == 34)
					if (type.isLeftClick())
						setting.setTripod2Choice(player, 2);
					else
						setting.setTripod2Choice(player, -1);
				else if (rawSlot == 38)
					if (type.isLeftClick())
						setting.setTripod3Choice(player, 0);
					else
						setting.setTripod3Choice(player, -1);
				else if (rawSlot == 42)
					if (type.isLeftClick())
						setting.setTripod3Choice(player, 1);
					else
						setting.setTripod3Choice(player, -1);
			}
			ConfigStore.savePlayerData(player, ds.getJob());
//			player.closeInventory();
//			ds.setSetting(setting);
//			openInv(player);
			int remain = ds.getRemainSkillPoint(player);
			Skill skill = ds.getSetting();
			if (skill == null) {
				player.closeInventory();
				MessageStore.sendSystemMsg(player, ChatColor.RED + "오류로 인해 " + this.getTitle() + "이 닫혔습니다.");
			}
			ItemStack normal = skill.getNormalItemStack(player);
			int level = skill.getLevel(player);
			normal.setAmount(level);
			click.setItem(4, normal);

			click.setItem(6, getSkillPoint(remain / 100));
			click.setItem(7, getSkillPoint((remain / 10) % 10));
			click.setItem(8, getSkillPoint(remain % 10));

			tripodChoice[] _t = skill.getTripod1();
			int choice = skill.getTripod1Choice(player);
			if (_t != null && _t.length > 0) {
				ItemStack _i[] = new ItemStack[] { _t[0].getItemStack(), _t[1].getItemStack(), _t[2].getItemStack() };
				if (level < 4)
					_i = new ItemStack[] { _t[0].getBlockItemStack(), _t[1].getBlockItemStack(),
							_t[2].getBlockItemStack() };
				else if (choice > -1 && choice < _t.length)
					_i[choice].addUnsafeEnchantment(Enchantment.DURABILITY, 0);

				click.setItem(19, _i[0]);
				click.setItem(22, _i[1]);
				click.setItem(25, _i[2]);
			}
			_t = skill.getTripod2();
			choice = skill.getTripod2Choice(player);
			if (_t != null && _t.length > 0) {
				ItemStack _i[] = new ItemStack[] { _t[0].getItemStack(), _t[1].getItemStack(), _t[2].getItemStack() };
				if (level < 7)
					_i = new ItemStack[] { _t[0].getBlockItemStack(), _t[1].getBlockItemStack(),
							_t[2].getBlockItemStack() };
				else if (choice > -1 && choice < _t.length)
					_i[choice].addUnsafeEnchantment(Enchantment.DURABILITY, 0);

				click.setItem(28, _i[0]);
				click.setItem(31, _i[1]);
				click.setItem(34, _i[2]);
			}
			_t = skill.getTripod3();
			choice = skill.getTripod3Choice(player);
			if (_t != null && _t.length > 0) {
				ItemStack _i[] = new ItemStack[] { _t[0].getItemStack(), _t[1].getItemStack(), };
				if (level < 10)
					_i = new ItemStack[] { _t[0].getBlockItemStack(), _t[1].getBlockItemStack(), };
				else if (choice > -1 && choice < _t.length)
					_i[choice].addUnsafeEnchantment(Enchantment.DURABILITY, 0);

				click.setItem(38, _i[0]);
				click.setItem(42, _i[1]);
			}

			ds.changeSetting(player);
			return false;
		}

		@Override
		public void Close(Player player) {
			Core.getCore().getHashMapStore().getDataStore(player).setSetting(null);
			super.Close(player);
		}
	},
	DPS(ChatColor.GREEN + "DPS창") {

		@Override
		public void openInv(Player player, int size, InventoryItem... ii) {
			Inventory inv = Bukkit.createInventory(null, 9, getTitle());
			inv.setItem(4, getDPS(player));
			InventoryItem i = ii[0];
			inv.setItem(inv.getSize() - 1, i.getItem());
			player.openInventory(inv);
		}

		private ItemStack getDPS(Player player) {
			ItemStack item = new ItemStack(Material.COMPASS);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.GOLD + player.getName() + ChatColor.WHITE + "님의 DPS");
			List<String> lore = new ArrayList<String>();
			DPS dps = Core.getCore().getHashMapStore().getDPS(player);
			double damage = dps.getDamage();
			double time = dps.getTime();
			int tick = dps.getTick();
			int critical = dps.getCritical();
			lore.add(ChatColor.RED + "총 피해량 : " + Math.round(damage * 100d) / 100d);
			lore.add(ChatColor.RED + "dps : " + Math.round((time != 0 ? damage / time : damage) * 100d) / 100d);
			lore.add(ChatColor.GREEN + "전투 시간 : " + time + "초");
			lore.add(ChatColor.GREEN + "타수 : " + tick + "회");
			lore.add(ChatColor.YELLOW + "치명타 확률 : " + Math.round(critical * 10000.d / tick) / 100d + "%");
			lore.add(ChatColor.WHITE + "클릭시 초기화");
			meta.setLore(lore);
			item.setItemMeta(meta);
			return item;
		}

		@Override
		public boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot, ClickType type,
				ItemStack currentItem, ItemStack cursor) {
			if (rawSlot == 4) {
				Core.getCore().getHashMapStore().getDPS(player).reset();
				player.closeInventory();
			} else if (rawSlot == click.getSize() - 1 && currentItem != null && currentItem.hasItemMeta()
					&& currentItem.getItemMeta().hasLocalizedName()) {
				String local = currentItem.getItemMeta().getLocalizedName();
				UUID uuid = UUID.fromString(local);
				if (uuid != null) {
					Bukkit.getEntity(uuid).remove();
					player.closeInventory();
				}
			}
			return false;
		}

	}
	//
	;

	public static enum Group {
		warrior("전사", ChatColor.RED, Material.IRON_SWORD) {
			@Override
			public void openInv(Player player) {
				Job.openInv(player, 5 * 9, getJobItem(JobStore.WarLordJT, 10), getJobItem(JobStore.WarLordGG, 11),
						getJobItem(JobStore.BerserkerGB, 15), getJobItem(JobStore.BerserkerGG, 16),
						getJobItem(JobStore.DestroyerBM, 28), getJobItem(JobStore.DestroyerJS, 29),
						getJobItem(JobStore.HolyKnightCA, 33), getJobItem(JobStore.HolyKnightS, 34));
			}
		},
		martialArtist("무도가", ChatColor.YELLOW, Material.LEAD) {
			@Override
			public void openInv(Player player) {
				Job.openInv(player, 5 * 9, getJobItem(JobStore.BattleMasterC, 10),
						getJobItem(JobStore.BattleMasterAG, 11), getJobItem(JobStore.InfighterCD, 13),
						getJobItem(JobStore.InfighterCS, 14), getJobItem(JobStore.SoulMasterSM, 16),
						getJobItem(JobStore.SoulMasterYC, 17), getJobItem(JobStore.LanceMasterJG, 28),
						getJobItem(JobStore.LanceMasterJJ, 29), getJobItem(JobStore.StrikerIP, 31),
						getJobItem(JobStore.StrikerON, 32));
			}
		},
		hunter("헌터", ChatColor.AQUA, Material.CROSSBOW) {
			@Override
			public void openInv(Player player) {
				Job.openInv(player, 5 * 9, getJobItem(JobStore.DevilHunterGM, 10),
						getJobItem(JobStore.DevilHunterHG, 11), getJobItem(JobStore.BlasterHG, 13),
						getJobItem(JobStore.BlasterPG, 14), getJobItem(JobStore.HawkEyeDD, 16),
						getJobItem(JobStore.HawkEyeJS, 17), getJobItem(JobStore.ScouterAG, 28),
						getJobItem(JobStore.ScouterJU, 29), getJobItem(JobStore.GunslingerPM, 31),
						getJobItem(JobStore.GunslingerSS, 32));
			}
		},
		mage("마법사", ChatColor.DARK_PURPLE, Material.STICK) {
			@Override
			public void openInv(Player player) {
				Job.openInv(player, 5 * 9, getJobItem(JobStore.SummonerNG, 10), getJobItem(JobStore.SummonerSS, 11),
						getJobItem(JobStore.ArcanaHH, 15), getJobItem(JobStore.ArcanaHJ, 16),
						getJobItem(JobStore.BardJG, 28), getJobItem(JobStore.BardJY, 29),
						getJobItem(JobStore.SorceressGR, 33), getJobItem(JobStore.SorceressJH, 34));
			}
		},
		assassin("암살자", ChatColor.GRAY, Material.SHEARS) {
			@Override
			public void openInv(Player player) {
				Job.openInv(player, 3 * 9, getJobItem(JobStore.BladeB, 10), getJobItem(JobStore.BladeJG, 11),
						getJobItem(JobStore.DemonicMC, 13), getJobItem(JobStore.DemonicWE, 14),
						getJobItem(JobStore.ReaperDS, 16), getJobItem(JobStore.ReaperGJ, 17));
			}
		},
		specialist("스페셜리스트", ChatColor.GREEN, Material.GLOW_INK_SAC) {
			@Override
			public void openInv(Player player) {
				Job.openInv(player, 3 * 9, getJobItem(JobStore.ArtistHG, 10), getJobItem(JobStore.ArtistMG, 11),
						getJobItem(JobStore.MeteorologistIS, 15), getJobItem(JobStore.MeteorologistJN, 16));
			}
		}
		//
		;

		private final String name;
		private final ChatColor color;
		private final Material type;

		private Group(String name, ChatColor color, Material type) {
			this.name = name;
			this.color = color;
			this.type = type;
		}

		public String getName() {
			return color + name;
		}

		public Material getType() {
			return type;
		}

		public ChatColor getColor() {
			return color;
		}

		public abstract void openInv(Player player);

		public static InventoryItem getJobItem(JobStore job, int slot) {
			ItemStack i = new ItemStack(job.getType());
			ItemMeta meta = i.getItemMeta();
			meta.setDisplayName(job.getDisplayName());
			meta.setLocalizedName(job.name());
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.WHITE + "클릭시 " + job.getDisplayName() + ChatColor.WHITE + "을 선택합니다.");
			meta.setLore(lore);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			i.setItemMeta(meta);
			return new InventoryItem(i, slot);
		}
	}

	private final String title;

	private GUIStore(String title) {
		this.title = title;
	}

	public void openInv(Player player, InventoryItem... ii) {
		openInv(player, 9, ii);
	}

	public abstract void openInv(Player player, int size, InventoryItem... ii);

	public abstract boolean Click(Player player, Inventory click, InventoryView view, int slot, int rawSlot,
			ClickType type, ItemStack currentItem, ItemStack cursor);

	public void Close(Player player) {
		new BukkitRunnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				player.updateInventory();
			}
		}.runTaskLater(Core.getCore(), 1);

	}

	public String getTitle() {
		return title;
	}

	public static GUIStore getGUI(InventoryView view) {
		if (view != null)
			for (GUIStore gui : values())
				if (view.getTitle().equalsIgnoreCase(gui.getTitle()))
					return gui;

		return null;
	}

	public static class InventoryItem {
		private final ItemStack item;
		private final int slot;

		public InventoryItem(ItemStack item, int slot) {
			this.item = item;
			this.slot = slot;
		}

		public ItemStack getItem() {
			return item;
		}

		public int getSlot() {
			return slot;
		}
	}

	private static ItemStack getBack() {
		ItemStack i = new ItemStack(Material.BEDROCK);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "뒤로가기");
		i.setItemMeta(meta);
		return i;
	}
}
