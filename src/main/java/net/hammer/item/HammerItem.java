package net.hammer.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HammerItem extends PickaxeItem {

    public HammerItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient() && miner instanceof PlayerEntity player) {
            // Проверяем: если земля или гравий — ломаем только один блок
            if (isSingleBlockBreaking(state)) {
                stack.damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand()));
            } else {
                // Ломаем в области 3x3 вокруг блока
                destroyArea3x3(stack, world, pos, player);
            }
        }

        return super.postMine(stack, world, state, pos, miner); // Ломаем основной блок
    }

    private boolean isSingleBlockBreaking(BlockState state) {
        // Проверяем, если блок земля или гравий — ломаем только один блок
        String blockName = state.getBlock().getTranslationKey();
        return blockName.equals("block.minecraft.dirt") || blockName.equals("block.minecraft.gravel");
    }

    private void destroyArea3x3(ItemStack stack, World world, BlockPos center, PlayerEntity player) {
        // Разрушаем область 3x3 вокруг блока
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos targetPos = center.add(dx, dy, dz); // Смещение по X, Y и Z
                    destroyBlock(stack, world, targetPos, player);
                }
            }
        }
    }

    private void destroyBlock(ItemStack stack, World world, BlockPos pos, PlayerEntity player) {
        // Ломаем блок, если это возможно
        BlockState state = world.getBlockState(pos);

        if (!state.isAir() && state.getHardness(world, pos) >= 0) { // Проверка: блок существует и разрушаем
            world.breakBlock(pos, true, player); // Разрушаем блок
            stack.damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand())); // Урон инструменту
        }
    }
}
