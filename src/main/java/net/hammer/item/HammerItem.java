package net.hammer.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class HammerItem extends PickaxeItem {

    public HammerItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient() && miner instanceof PlayerEntity player) {
            if (isSingleBlockBreaking(state)) {
                stack.damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand()));
            } else {
                float yaw = player.getYaw();
                float pitch = player.getPitch();

                destroyBlocksInFront(stack, world, pos, player, yaw, pitch);
            }
        }

        return super.postMine(stack, world, state, pos, miner); // Ломаем основной блок
    }

    private boolean isSingleBlockBreaking(BlockState state) {
        String blockName = state.getBlock().getTranslationKey();
        return blockName.equals("block.minecraft.dirt") ||
                blockName.equals("block.minecraft.gravel") ||
                blockName.equals("block.minecraft.sand");
    }

    private void destroyBlocksInFront(ItemStack stack, World world, BlockPos center, PlayerEntity player, float yaw, float pitch) {
        Vec3i direction = getDirectionVector(yaw, pitch);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos targetPos = center.add(direction.getX() + dx, direction.getY() + dy, direction.getZ() + dz);
                    destroyBlock(stack, world, targetPos, player);
                }
            }
        }
    }

    private Vec3i getDirectionVector(float yaw, float pitch) {
        double radYaw = Math.toRadians(yaw);
        double radPitch = Math.toRadians(pitch);

        int x = (int) -Math.sin(radYaw);
        int y = (int) -Math.sin(radPitch);
        int z = (int) Math.cos(radYaw);

        return new Vec3i(x, y, z);
    }

    private void destroyBlock(ItemStack stack, World world, BlockPos pos, PlayerEntity player) {
        BlockState state = world.getBlockState(pos);
        if (!state.isAir() && state.getHardness(world, pos) >= 0) { // Проверка: блок существует и разрушаем
            world.breakBlock(pos, true, player); // Разрушаем блок
            stack.damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand())); // Урон инструменту
        }
    }
}
