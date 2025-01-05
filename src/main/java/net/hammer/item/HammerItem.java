  package net.hammer.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
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
                destroyBlocksInPlane(stack, world, pos, player, yaw, pitch);
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

    private void destroyBlocksInPlane(ItemStack stack, World world, BlockPos center, PlayerEntity player, float yaw, float pitch) {
        if (pitch > 45) { // Смотрит вниз
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos targetPos = center.add(dx, 0, dz); // Плоскость X-Z текущего слоя
                    destroyBlock(stack, world, targetPos, player);
                }
            }
        } else if (pitch < -45) { // Смотрит вверх
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos targetPos = center.add(dx, 0, dz); // Слой X-Z над текущим слоем
                    destroyBlock(stack, world, targetPos, player);
                }
            }
        } else {
            double radYaw = Math.toRadians(yaw);
            int dirX = (int) Math.round(-Math.sin(radYaw)); // Направление X взгляда
            int dirZ = (int) Math.round(Math.cos(radYaw));  // Направление Z взгляда

            if (Math.abs(dirX) > Math.abs(dirZ)) {
                // Если игрок смотрит влево/вправо (по оси X)
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        BlockPos targetPos = center.add(0, dy, dz); // Область Y-Z
                        destroyBlock(stack, world, targetPos, player);
                    }
                }
            } else {
                // Если игрок смотрит вперед/назад (по оси Z)
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        BlockPos targetPos = center.add(dx, dy, 0); // Область X-Y
                        destroyBlock(stack, world, targetPos, player);
                    }
                }
            }
        }
    }

    private void destroyBlock(ItemStack stack, World world, BlockPos pos, PlayerEntity player) {
        BlockState blockState = world.getBlockState(pos);
        if (!blockState.isAir() && blockState.getHardness(world, pos) >= 0) { // Проверяем, что блок существует и поддается разрушению
            world.breakBlock(pos, true, player); // Ломаем блок с выпадением дропа
            stack.damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand())); // Уменьшаем прочность инструмента
        }
    }
}