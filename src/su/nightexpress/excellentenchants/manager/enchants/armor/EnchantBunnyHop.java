package su.nightexpress.excellentenchants.manager.enchants.armor;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.JYML;
import su.nightexpress.excellentenchants.ExcellentEnchants;
import su.nightexpress.excellentenchants.api.enchantment.EnchantPriority;
import su.nightexpress.excellentenchants.api.enchantment.IEnchantPotionTemplate;
import su.nightexpress.excellentenchants.api.enchantment.type.PassiveEnchant;

public class EnchantBunnyHop extends IEnchantPotionTemplate implements PassiveEnchant {

    public EnchantBunnyHop(@NotNull ExcellentEnchants plugin, @NotNull JYML cfg) {
        super(plugin, cfg, EnchantPriority.MEDIUM, PotionEffectType.JUMP);
    }

    @Override
    @NotNull
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_FEET;
    }

    @Override
    public boolean use(@NotNull LivingEntity entity, int level) {
        if (!this.checkTriggerChance(level)) return false;

        return this.addEffect(entity, level);
    }
}
