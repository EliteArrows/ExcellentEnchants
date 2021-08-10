package su.nightexpress.excellentenchants.hooks;

import fr.neatmonster.nocheatplus.NCPAPIProvider;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.ExemptionContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.hooks.Hooks;

public class HookNCP {

    public static void exemptBlocks(@NotNull Player player) {
        if (!Hooks.hasPlugin(EHook.NCP)) return;

        NCPAPIProvider.getNoCheatPlusAPI().getPlayerDataManager().getPlayerData(player).exempt(CheckType.BLOCKBREAK, ExemptionContext.ANONYMOUS_NESTED);
    }

    public static void unexemptBlocks(@NotNull Player player) {
        if (!Hooks.hasPlugin(EHook.NCP)) return;

        NCPAPIProvider.getNoCheatPlusAPI().getPlayerDataManager().getPlayerData(player).unexempt(CheckType.BLOCKBREAK, ExemptionContext.ANONYMOUS_NESTED);
    }
}
