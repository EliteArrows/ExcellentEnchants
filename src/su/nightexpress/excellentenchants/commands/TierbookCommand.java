package su.nightexpress.excellentenchants.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.commands.api.ISubCommand;
import su.nexmedia.engine.utils.ItemUT;
import su.nexmedia.engine.utils.PlayerUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.excellentenchants.ExcellentEnchants;
import su.nightexpress.excellentenchants.Perms;
import su.nightexpress.excellentenchants.api.enchantment.ExcellentEnchant;
import su.nightexpress.excellentenchants.manager.EnchantManager;
import su.nightexpress.excellentenchants.manager.object.EnchantTier;

import java.util.Arrays;
import java.util.List;

public class TierbookCommand extends ISubCommand<ExcellentEnchants> {

    public TierbookCommand(@NotNull ExcellentEnchants plugin) {
        super(plugin, new String[]{"tierbook"}, Perms.ADMIN);
    }

    @Override
    @NotNull
    public String description() {
        return plugin.lang().Command_TierBook_Desc.getMsg();
    }

    @Override
    @NotNull
    public String usage() {
        return plugin.lang().Command_TierBook_Usage.getMsg();
    }

    @Override
    public boolean playersOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return PlayerUT.getPlayerNames();
        }
        if (i == 2) {
            return EnchantManager.getTierIds();
        }
        if (i == 3) {
            return Arrays.asList("-1", "1", "5", "10");
        }
        return super.getTab(player, i, args);
    }

    @Override
    public void perform(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length != 4) {
            this.printUsage(sender);
            return;
        }

        Player player = plugin.getServer().getPlayer(args[1]);
        if (player == null) {
            this.errPlayer(sender);
            return;
        }

        EnchantTier tier = EnchantManager.getTierById(args[2].toLowerCase());
        if (tier == null) {
            plugin.lang().Command_TierBook_Error.send(sender);
            return;
        }

        ExcellentEnchant enchant = Rnd.get(tier.getEnchants());
        if (enchant == null) {
            plugin.lang().Error_NoEnchant.send(sender);
            return;
        }

        int level = this.getNumI(sender, args[3], -1, true);
        if (level < 1) {
            level = Rnd.get(enchant.getStartLevel(), enchant.getMaxLevel());
        }

        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantManager.addEnchant(item, enchant, level, true);
        ItemUT.addItem(player, item);

        plugin.lang().Command_TierBook_Done.replace("%tier%", tier.getName()).replace("%player%", player.getName()).send(sender);
    }
}
