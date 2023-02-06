package com.StarJ.LM.Listener;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.StarJ.LM.Core;
import com.StarJ.LM.Skills.Skill;
import com.StarJ.LM.System.DataStore;
import com.StarJ.LM.System.DataStore.ActionBarRunnable;
import com.StarJ.LM.System.EntityStore;
import com.StarJ.LM.System.HashMapStore.DPS;

public class EntityListener implements Listener {

	@EventHandler
	public void Events(EntityDamageByEntityEvent e) {
		Entity attEntity = e.getDamager();
		if (attEntity instanceof Projectile && ((Projectile) attEntity).getShooter() != null)
			attEntity = (Entity) ((Projectile) attEntity).getShooter();
		Entity vicEntity = e.getEntity();
		World world = vicEntity.getWorld();
		if (!Core.getCore().isVanillaWorld(world))
			if (Core.getCore().isPvp(world)) {
				if (attEntity instanceof LivingEntity && vicEntity instanceof LivingEntity) {
					LivingEntity att = (LivingEntity) attEntity;
					LivingEntity vic = (LivingEntity) vicEntity;
					if (att.hasMetadata("owner")) {
						for (MetadataValue mv : att.getMetadata("owner"))
							if (mv.getOwningPlugin().equals(Core.getCore())) {
								UUID uuid = UUID.fromString(mv.asString());
								if (uuid != null) {
									Entity et = Bukkit.getEntity(uuid);
									if (et instanceof LivingEntity)
										att = (LivingEntity) et;
								}
								break;
							}
					}
					if (vic.hasMetadata("owner")) {
						for (MetadataValue mv : vic.getMetadata("owner"))
							if (mv.getOwningPlugin().equals(Core.getCore())) {
								UUID uuid = UUID.fromString(mv.asString());
								if (uuid != null) {
									Entity et = Bukkit.getEntity(uuid);
									if (att.equals(et)) {
										e.setCancelled(true);
										return;
									}
								}
								break;
							}
					}

					Core.getCore().getHashMapStore().setTarget(att, vic);
					if (Core.getCore().isPvp(world)) {
						DataStore ads = Core.getCore().getHashMapStore().getDataStore(att);
						DataStore vds = Core.getCore().getHashMapStore().getDataStore(vic);
						double damage = e.getDamage();
						if (vds != null) {
							Random r = new Random();
							boolean critical = false;
							if (ads != null) {
								critical = r.nextDouble() < ads.getCriticalChance(att);
								damage = damage * (critical ? ads.getExtraCriticalDamage() : 1);
								for (String key : ads.getAttackList(att)) {
									Skill skill = Skill.valueOf(key);
									if (skill != null)
										damage = skill.Attack(att, vic, damage);
								}
							}
							damage *= vds.getReduceDamage();
							for (String key : vds.getAttackedList(vic)) {
								Skill skill = Skill.valueOf(key);
								if (skill != null)
									damage = skill.Attacked(vic, att, damage);
							}
							ActionBarRunnable.run(att);
							spawnDamage(vic.getEyeLocation().clone().subtract(0, 1.5d, 0), damage, critical);
							double health = vds.getHealth() - vds.damageAbsorption(damage);
							vds.setHealth(health);
							vds.confirmHealthPercent(vic);
							vic.playEffect(EntityEffect.HURT);
							vic.getWorld().playSound(vic.getLocation(), vic.getHurtSound(), 1f, 1f);
							e.setCancelled(true);
							if (ads != null)
								ads.setAttackType(null);
							ActionBarRunnable.run(vic);
						} else {
							Random r = new Random();
							boolean critical = false;
							if (ads != null) {
								critical = r.nextDouble() < ads.getCriticalChance(att);
								damage = damage * (critical ? ads.getExtraCriticalDamage() : 1);
								for (String key : ads.getAttackList(att)) {
									Skill skill = Skill.valueOf(key);
									if (skill != null)
										damage = skill.Attack(att, vic, damage);
								}
								ActionBarRunnable.run(att);
								if (att instanceof Player && vic.hasMetadata("type"))
									for (MetadataValue mv : vic.getMetadata("type"))
										if (mv.getOwningPlugin().equals(Core.getCore()))
											try {
												EntityStore es = EntityStore.valueOf(mv.asString());
												if (es != null && es.equals(EntityStore.Scarecrow)) {
													DPS dps = Core.getCore().getHashMapStore().getDPS((Player) att);
													dps.addDamage(damage);
													dps.addTick();
													if (critical)
														dps.addCritical();
													e.setCancelled(true);
													vic.playEffect(EntityEffect.HURT);
												}
											} catch (Exception ex) {

											}
								ads.setAttackType(null);
							}
							ActionBarRunnable.run(vic);
							spawnDamage(vic.getEyeLocation().clone().subtract(0, 1.5d, 0), damage, critical);
							vic.playEffect(EntityEffect.HURT);
							vic.getWorld().playSound(vic.getLocation(), vic.getHurtSound(), 1f, 1f);
							if (vic.hasMetadata("health") && vic.hasMetadata("maxHealth")) {
								double health = vic.getHealth();
								for (MetadataValue mv : vic.getMetadata("health"))
									if (mv.getOwningPlugin().equals(Core.getCore()))
										health = mv.asDouble();
								health -= damage;
								vic.setMetadata("health", new FixedMetadataValue(Core.getCore(), health));
								DataStore.confirmHealthPercentNoDataStore(vic);
								e.setCancelled(true);
							} else
								e.setDamage(damage);
						}
					}
				} else
					e.setCancelled(true);
			}
	}

	@EventHandler
	public void Events(EntityDamageEvent e) {
		Entity vicEntity = e.getEntity();
		World world = vicEntity.getWorld();
		if (!(e instanceof EntityDamageByEntityEvent || e instanceof EntityDamageByBlockEvent))
			if (!Core.getCore().isVanillaWorld(world))
				if (vicEntity instanceof LivingEntity) {
					LivingEntity vic = (LivingEntity) vicEntity;
					if (Core.getCore().isPvp(world)) {
						if (e.getCause().equals(DamageCause.FALL)
								&& Core.getCore().getHashMapStore().isPreventFallDamage(vic)) {
							e.setCancelled(true);
						} else {
							double damage = e.getDamage();
							DataStore vds = Core.getCore().getHashMapStore().getDataStore(vic);
							if (vds != null) {
								ActionBarRunnable.run(vic);
								damage *= vds.getReduceDamage();
								for (String key : vds.getAttackedList(vic)) {
									Skill skill = Skill.valueOf(key);
									if (skill != null)
										damage = skill.Attacked(vic, null, damage);
								}
								spawnDamage(vic.getEyeLocation().clone().subtract(0, 1.5d, 0), damage);
								double health = vds.getHealth() - vds.damageAbsorption(damage);
								vds.setHealth(health);
								vds.confirmHealthPercent(vic);
								vic.playEffect(EntityEffect.HURT);
								e.setDamage(0.01d);
							} else
								e.setDamage(damage);
						}
					}
				}
	}

	public void spawnDamage(Location loc, double damage) {
		spawnDamage(loc, damage, false);
	}

	public void spawnDamage(Location loc, double damage, boolean critical) {
		if (damage <= 0)
			return;
		Random r = new Random();
		Entity et = EntityStore.Damage.spawnEntity(
				loc.clone().add(r.nextDouble() / 2d - 0.25d, r.nextDouble() / 2d - 0.25d, r.nextDouble() / 2d - 0.25d));
		et.setCustomName((critical ? ChatColor.YELLOW : ChatColor.GREEN) + "" + Math.round(damage));
		new BukkitRunnable() {
			@Override
			public void run() {
				et.remove();
			}
		}.runTaskLater(Core.getCore(), 10);
	}

	@EventHandler
	public void Events(EntityDamageByBlockEvent e) {
		Entity vicEntity = e.getEntity();
		World world = vicEntity.getWorld();
		if (!Core.getCore().isVanillaWorld(world))
			if (vicEntity instanceof LivingEntity) {
				LivingEntity vic = (LivingEntity) vicEntity;
				if (Core.getCore().isPvp(world)) {
					DataStore vds = Core.getCore().getHashMapStore().getDataStore(vic);
					double damage = e.getDamage();
					if (vds != null) {
						ActionBarRunnable.run(vic);
						damage *= vds.getReduceDamage();
						for (String key : vds.getAttackedList(vic)) {
							Skill skill = Skill.valueOf(key);
							if (skill != null)
								damage = skill.Attacked(vic, null, damage);
						}
						spawnDamage(vic.getEyeLocation().clone().subtract(0, 1.5d, 0), damage);
						double health = vds.getHealth() - vds.damageAbsorption(damage);
						vds.setHealth(health);
						vds.confirmHealthPercent(vic);
						e.setDamage(0.01d);
					} else
						e.setDamage(damage);
				}
			}
	}

	@EventHandler
	public void Events(EntityRegainHealthEvent e) {
		Entity et = e.getEntity();
		World world = et.getWorld();
		if (!Core.getCore().isVanillaWorld(world))
			if (et instanceof LivingEntity) {
				LivingEntity le = (LivingEntity) et;
				if (Core.getCore().isPvp(world)) {
					DataStore vds = Core.getCore().getHashMapStore().getDataStore(le);
					if (vds != null) {
						vds.setHealth(vds.getHealth() + e.getAmount());
						vds.confirmHealthPercent(le);
						e.setCancelled(true);
					}
					ActionBarRunnable.run(le);
				}
			}
	}

}
