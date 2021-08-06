package su.nightexpress.excellentenchants.manager.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.EntityUT;
import su.nexmedia.engine.utils.ItemUT;
import su.nightexpress.excellentenchants.ExcellentEnchants;
import su.nightexpress.excellentenchants.api.enchantment.type.*;
import su.nightexpress.excellentenchants.manager.EnchantManager;

public class EnchantHandlerListener extends AbstractListener<ExcellentEnchants> {

	public EnchantHandlerListener(@NotNull EnchantManager enchantManager) {
		super(enchantManager.plugin());
	}
	
	// ---------------------------------------------------------------
	// Combat Attacking Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantCombatMelee(EntityDamageByEntityEvent e) {
		if (e.getCause() == DamageCause.THORNS) return;
		if (!(e.getEntity() instanceof LivingEntity victim)) return;
		if (!(e.getDamager() instanceof LivingEntity damager)) return;

		EntityEquipment equipment = damager.getEquipment();
		if (equipment == null) return;
		
		ItemStack weapon = equipment.getItemInMainHand();
		if (ItemUT.isAir(weapon) || weapon.getType() == Material.ENCHANTED_BOOK) return;
		
		EnchantManager.getItemCustomEnchants(weapon, CombatEnchant.class).forEach((combatEnchant, level) -> {
			if (combatEnchant instanceof BowEnchant) return;
			combatEnchant.use(e, damager, victim, weapon, level);
		});
	}
	
	// ---------------------------------------------------------------
	// Armor Defensive Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantCombatArmor(EntityDamageByEntityEvent e) {
		// Prevent armor enchants to have effect if damage is from Thorns.
		if (e.getCause() == DamageCause.THORNS) return;
		
		Entity eVictim = e.getEntity();
		if (!(eVictim instanceof LivingEntity victim)) return;
		
		Entity eDamager = e.getDamager();
		if (eDamager instanceof Projectile projectile) {
			if (projectile.getShooter() instanceof Entity) {
				eDamager = (Entity) projectile.getShooter();
			}
		}
		if (!(eDamager instanceof LivingEntity damager) || eDamager.equals(eVictim)) return;

		EntityEquipment equipDamager = damager.getEquipment();
		if (equipDamager == null) return;
		
		ItemStack weaponDamager = equipDamager.getItemInMainHand();
		
		for (ItemStack armor : EntityUT.getArmor(victim)) {
			if (ItemUT.isAir(armor)) continue;
			
			EnchantManager.getItemCustomEnchants(armor, CombatEnchant.class).forEach((combatEnchant, level) -> {
				combatEnchant.use(e, damager, victim, weaponDamager, level);
			});
		}
	}
	
	// ---------------------------------------------------------------
	// Bow Shooting Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantBowShoot(EntityShootBowEvent e) {
		if (!(e.getProjectile() instanceof Projectile projectile)) return;
		
		LivingEntity shooter = e.getEntity();
		if (shooter.getEquipment() == null) return;

		ItemStack bow = e.getBow();
		if (bow == null || ItemUT.isAir(bow) || bow.getType() == Material.ENCHANTED_BOOK) return;

		EnchantManager.setArrowWeapon(projectile, bow);
		EnchantManager.getItemCustomEnchants(bow, BowEnchant.class).forEach((bowEnchant, level) -> {
			bowEnchant.use(e, shooter, bow, level);
		});
	}
	
	// ---------------------------------------------------------------
	// Bow Damage Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantBowDamage(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof LivingEntity victim)) return;
		if (!(e.getDamager() instanceof Projectile projectile)) return;
		if (!(projectile.getShooter() instanceof LivingEntity damager)) return;
		
		ItemStack bow = EnchantManager.getArrowWeapon(projectile);
		if (bow == null || ItemUT.isAir(bow) || bow.getType() == Material.ENCHANTED_BOOK) return;

		EnchantManager.getItemCustomEnchants(bow, BowEnchant.class).forEach((bowEnchant, level) -> {
			bowEnchant.use(e, damager, victim, bow, level);
		});
	}
	
	// ---------------------------------------------------------------
	// Bow Hit Land Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEnchantBowHit(ProjectileHitEvent e) {
		Projectile projectile = e.getEntity();

		ItemStack bow = EnchantManager.getArrowWeapon(projectile);
		if (bow == null || ItemUT.isAir(bow) || bow.getType() == Material.ENCHANTED_BOOK) return;
		
		EnchantManager.getItemCustomEnchants(bow, BowEnchant.class).forEach((bowEnchant, level) -> {
			bowEnchant.use(e, projectile, bow, level);
		});
	}
	
	// ---------------------------------------------------------------
	// Interaction Related Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantInteract(PlayerInteractEvent e) {
		if (e.useInteractedBlock() == Result.DENY) return;
		if (e.useItemInHand() == Result.DENY) return;
		
		ItemStack item = e.getItem();
		if (item == null || ItemUT.isAir(item) || item.getType() == Material.ENCHANTED_BOOK) return;
		
		Player player = e.getPlayer();
		EnchantManager.getItemCustomEnchants(item, InteractEnchant.class).forEach((interEnchant, level) -> {
			interEnchant.use(e, player, item, level);
		});
	}
	
	// ---------------------------------------------------------------
	// Death Related Enchants
	// ---------------------------------------------------------------
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantDeath(EntityDeathEvent e) {
		LivingEntity dead = e.getEntity();
		for (ItemStack armor : EntityUT.getArmor(dead)) {
			if (armor == null || ItemUT.isAir(armor)) continue;
			
			EnchantManager.getItemCustomEnchants(armor, DeathEnchant.class).forEach((deathEnchant, level) -> {
				deathEnchant.use(e, dead, level);
			});
		}
		
		Player killer = dead.getKiller();
		if (killer == null) return;
		
		ItemStack weapon = killer.getInventory().getItemInMainHand();
		if (ItemUT.isAir(weapon) || weapon.getType() == Material.ENCHANTED_BOOK) return;
		
		EnchantManager.getItemCustomEnchants(weapon, DeathEnchant.class).forEach((deathEnchant, level) -> {
			deathEnchant.use(e, dead, level);
		});
	}

	// Handle BlockBreak enchantments.
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnchantBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE) return;
		
		ItemStack tool = player.getInventory().getItemInMainHand();
		if (ItemUT.isAir(tool) || tool.getType() == Material.ENCHANTED_BOOK) return;
		
		EnchantManager.getItemCustomEnchants(tool, BlockEnchant.class).forEach((blockEnchant, level) -> {
			blockEnchant.use(e, player, tool, level);
		});
	}
}
