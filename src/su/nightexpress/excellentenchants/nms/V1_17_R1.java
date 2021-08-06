package su.nightexpress.excellentenchants.nms;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.excellentenchants.manager.enchants.armor.EnchantFlameWalker;

public class V1_17_R1 implements EnchantNMS {

    @Override
    public void handleFlameWalker(@NotNull LivingEntity entity1, @NotNull Location loc, int level) {
        Entity entity = ((CraftLivingEntity) entity1).getHandle();
        BlockPosition pos = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        World world = ((CraftWorld) entity1.getWorld()).getHandle();

        IBlockData bStone = Blocks.m.getBlockData();
        float rad = Math.min(16, 2 + level);

        org.bukkit.World w1 = entity1.getWorld();

        BlockPosition.MutableBlockPosition posMut = new BlockPosition.MutableBlockPosition();
        for (BlockPosition bNear : BlockPosition.a(pos.b(-rad, -1.0, -rad), pos.b(rad, -1.0, rad))) {
            if (!bNear.a(entity.getPositionVector(), rad)) continue;
            posMut.d(bNear.getX(), bNear.getY() + 1, bNear.getZ());

            IBlockData bLavaUp = world.getType(posMut);
            IBlockData bLava = world.getType(bNear);

            if (!bLavaUp.isAir()) continue;
            // меня заебало нахуй искать и подбирать ебучую лаву в NMS
            Block normal = w1.getBlockAt(bNear.getX(), bNear.getY(), bNear.getZ());
            if (normal.getType() != org.bukkit.Material.LAVA) continue;
            if (bLava.get(BlockFluids.a) != 0) continue;
            if (!bStone.canPlace(world, bNear)) continue;
            if (!world.a(bStone, bNear, VoxelShapeCollision.a())) continue;
            if (!CraftEventFactory.handleBlockFormEvent(world, bNear, bStone, entity)) continue;

            world.getBlockTickList().a(bNear, Blocks.m, MathHelper.nextInt(Rnd.rnd, 60, 120));

            Location loc2 = new Location(world.getWorld(), bNear.getX(), bNear.getY(), bNear.getZ());
            EnchantFlameWalker.addBlock(loc2.getBlock(), Rnd.get(1, 6));
        }
    }
}
