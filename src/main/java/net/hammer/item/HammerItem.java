  package net.hammer.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

        return super.postMine(stack, world, state, pos, miner);
    }

    private boolean isSingleBlockBreaking(BlockState state) {
        String blockName = state.getBlock().getTranslationKey();
        return blockName.equals("block.minecraft.dirt") ||
                blockName.equals("block.minecraft.gravel") ||
                blockName.equals("block.minecraft.sand");
    }

    private void destroyBlocksInPlane(ItemStack stack, World world, BlockPos center, PlayerEntity player, float yaw, float pitch) {
        if (pitch > 45) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos targetPos = center.add(dx, 0, dz);
                    destroyBlock(stack, world, targetPos, player);
                }
            }
        } else if (pitch < -45) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos targetPos = center.add(dx, 0, dz);
                    destroyBlock(stack, world, targetPos, player);
                }
            }
        } else {
            double radYaw = Math.toRadians(yaw);
            int dirX = (int) Math.round(-Math.sin(radYaw));
            int dirZ = (int) Math.round(Math.cos(radYaw));

            if (Math.abs(dirX) > Math.abs(dirZ)) {
                for (int dz = -1; dz <= 1; dz++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        BlockPos targetPos = center.add(0, dy, dz);
                        destroyBlock(stack, world, targetPos, player);
                    }
                }
            } else {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        BlockPos targetPos = center.add(dx, dy, 0);
                        destroyBlock(stack, world, targetPos, player);
                    }
                }
            }
        }
    }

    private void destroyBlock(ItemStack stack, World world, BlockPos pos, PlayerEntity player) {
        BlockState blockState = world.getBlockState(pos);
        ToolMaterial material = this.getMaterial();
        int miningLevel = material.getMiningLevel();

        if (!blockState.isAir() && blockState.getHardness(world, pos) >= 0 && canBreakBlockWithTool(miningLevel, blockState)) {
            world.breakBlock(pos, true, player);
            stack.damage(1, player, p -> p.sendToolBreakStatus(p.getActiveHand()));
        }
    }

    private boolean canBreakBlockWithTool(int miningLevel, BlockState blockState) {
        boolean canBreakStone = blockState.isOf(Blocks.STONE) || blockState.isOf(Blocks.COBBLESTONE) || blockState.isOf(Blocks.MOSSY_COBBLESTONE) || blockState.isOf(Blocks.ANDESITE) || blockState.isOf(Blocks.DEEPSLATE) || blockState.isOf(Blocks.GRANITE) || blockState.isOf(Blocks.DIORITE);
        boolean canBreakCoal = blockState.isOf(Blocks.COAL_ORE);
        boolean canBreakCopper = blockState.isOf(Blocks.COPPER_ORE) || blockState.isOf(Blocks.DEEPSLATE_COPPER_ORE) || blockState.isOf(Blocks.COPPER_BLOCK);
        boolean canBreakIron = blockState.isOf(Blocks.IRON_ORE) || blockState.isOf(Blocks.IRON_BLOCK);
        boolean canBreakGold = blockState.isOf(Blocks.GOLD_ORE) || blockState.isOf(Blocks.GOLD_BLOCK);
        boolean canBreakDiamond = blockState.isOf(Blocks.DIAMOND_ORE) || blockState.isOf(Blocks.DIAMOND_BLOCK);
        boolean canBreakLapis = blockState.isOf(Blocks.LAPIS_ORE);
        boolean canBreakRedstone = blockState.isOf(Blocks.REDSTONE_ORE) || blockState.isOf(Blocks.REDSTONE_BLOCK);
        boolean canBreakEmerald = blockState.isOf(Blocks.EMERALD_ORE) || blockState.isOf(Blocks.EMERALD_BLOCK);
        boolean canBreakSand = blockState.isOf(Blocks.SAND);
        boolean canBreakAncient = blockState.isOf(Blocks.ANCIENT_DEBRIS);
        boolean canBreakNether = blockState.isOf(Blocks.NETHER_GOLD_ORE) || blockState.isOf(Blocks.NETHER_QUARTZ_ORE) || blockState.isOf(Blocks.NETHERRACK) || blockState.isOf(Blocks.BASALT);

        boolean canBreakDeepslateIron = blockState.isOf(Blocks.DEEPSLATE_IRON_ORE);
        boolean canBreakDeepslateGold = blockState.isOf(Blocks.DEEPSLATE_GOLD_ORE);
        boolean canBreakDeepslateDiamond = blockState.isOf(Blocks.DEEPSLATE_DIAMOND_ORE);
        boolean canBreakDeepslateLapis = blockState.isOf(Blocks.DEEPSLATE_LAPIS_ORE);
        boolean canBreakDeepslateRedstone = blockState.isOf(Blocks.DEEPSLATE_REDSTONE_ORE);
        boolean canBreakDeepslateEmerald = blockState.isOf(Blocks.DEEPSLATE_EMERALD_ORE);

        boolean canBreakCommon = canBreakStone || canBreakCoal || canBreakSand;

        return switch (miningLevel) {
            case 0 -> canBreakCommon; // Деревянная кирка
            case 1 -> canBreakCommon || canBreakCopper || canBreakIron || canBreakLapis || canBreakDeepslateIron || canBreakNether; // Каменная кирка
            case 2 -> canBreakCommon || canBreakCopper || canBreakIron || canBreakGold || canBreakLapis || canBreakRedstone || canBreakDiamond || canBreakDeepslateIron || canBreakDeepslateGold || canBreakDeepslateLapis || canBreakDeepslateRedstone || canBreakDeepslateEmerald || canBreakEmerald || canBreakNether; // Железная кирка
            case 3 -> canBreakCommon || canBreakCopper || canBreakIron || canBreakGold || canBreakDiamond || canBreakLapis || canBreakRedstone || canBreakEmerald || canBreakDeepslateIron || canBreakDeepslateGold || canBreakDeepslateDiamond || canBreakDeepslateLapis || canBreakDeepslateRedstone || canBreakDeepslateEmerald || canBreakNether || canBreakAncient; // Золотая кирка
            case 4 -> canBreakCommon || canBreakCopper || canBreakIron || canBreakGold || canBreakDiamond || canBreakLapis || canBreakRedstone || canBreakEmerald || canBreakDeepslateIron || canBreakDeepslateGold || canBreakDeepslateDiamond || canBreakDeepslateLapis || canBreakDeepslateRedstone || canBreakDeepslateEmerald || canBreakNether || canBreakAncient; // Алмазная кирка
            default -> false;
        };
    }



}
