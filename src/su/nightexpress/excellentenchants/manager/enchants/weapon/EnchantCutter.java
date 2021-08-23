package su.nightexpress.excellentenchants.manager.enchants.weapon;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.manager.leveling.Scaler;
import su.nexmedia.engine.utils.EffectUT;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.MsgUT;
import su.nexmedia.engine.utils.NumberUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.excellentenchants.ExcellentEnchants;
import su.nightexpress.excellentenchants.api.enchantment.EnchantPriority;
import su.nightexpress.excellentenchants.api.enchantment.IEnchantChanceTemplate;
import su.nightexpress.excellentenchants.api.enchantment.type.CombatEnchant;
import su.nightexpress.excellentenchants.manager.object.EnchantScaler;

import java.util.function.UnaryOperator;

public class EnchantCutter extends IEnchantChanceTemplate implements CombatEnchant {

    protected Scaler durabilityReduction;
    protected Sound  sound;

    public EnchantCutter(@NotNull ExcellentEnchants plugin, @NotNull JYML cfg) {
        super(plugin, cfg, EnchantPriority.LOWEST);
        this.durabilityReduction = new EnchantScaler(this, "Settings.Item.Durability_Reduction");
        this.sound = cfg.getEnum("Settings.Item.Sound", Sound.class);
    }

    @Override
    protected void updateConfig() {
        super.updateConfig();

        cfg.addMissing("Sound", Sound.ENTITY_ITEM_BREAK.name());
        if (cfg.contains("settings.item-damage-modifier")) {
            String damageModifier = cfg.getString("settings.item-damage-modifier", "").replace("%level%", PLACEHOLDER_LEVEL);

            cfg.set("Settings.Item.Durability_Reduction", damageModifier);
            cfg.set("settings.item-damage-modifier", null);
        }
    }

    @Override
    public @NotNull UnaryOperator<String> replacePlaceholders(int level) {
        return str -> super.replacePlaceholders(level).apply(str.replace("%damage%", NumberUT.format(this.getDurabilityReduction(level) * 100D - 100D)));
    }

    @Override
    public boolean use(@NotNull EntityDamageByEntityEvent e, @NotNull LivingEntity damager, @NotNull LivingEntity victim, @NotNull ItemStack weapon, int level) {

        if (!this.checkTriggerChance(level)) return false;

        EntityEquipment equipment = victim.getEquipment();
        if (equipment == null) return false;

        ItemStack[] armor = equipment.getArmorContents();
        if (armor.length == 0) return false;

        int get = Rnd.get(armor.length);
        ItemStack cut = armor[get];

        if (cut == null || ItemUT.isAir(cut)) return false;

        ItemMeta meta = cut.getItemMeta();
        if (!(meta instanceof Damageable damageable)) return false;

        damageable.setDamage((int) (Math.max(1, damageable.getDamage()) * this.getDurabilityReduction(level)));

        armor[get] = null;
        equipment.setArmorContents(armor);

        Item drop = victim.getWorld().dropItemNaturally(victim.getLocation(), cut);
        drop.setPickupDelay(40);
        drop.getVelocity().multiply(3D);

        EffectUT.playEffect(victim.getEyeLocation(), Particle.ITEM_CRACK.name() + ":" + cut.getType().name(), 0.2f, 0.15f, 0.2f, 0.15f, 40);
        if (this.sound != null) MsgUT.sound(victim.getLocation(), this.sound.name());
        return true;
    }

    @Override
    @NotNull
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    public final double getDurabilityReduction(int level) {
        return this.durabilityReduction.getValue(level);
    }
}
