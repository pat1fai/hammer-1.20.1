package net.hammer.item;

import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

import java.util.function.Supplier;

public enum ModToolMaterial implements ToolMaterial {
    WOODHAMMER(0, 250, 2F, 1F, 14, () -> Ingredient.ofItems(Items.WOODEN_PICKAXE)),
    STONEHAMMER(1, 380, 4F, 2F, 7, () -> Ingredient.ofItems(Items.COBBLESTONE)),
    IRONHAMMER(2,700, 7F, 3F, 15, () -> Ingredient.ofItems(Items.IRON_INGOT)),
    GOLDHAMMER(0, 150, 14F, 0F, 22, () -> Ingredient.ofItems(Items.GOLD_INGOT)),
    DIAMONDHAMMER(3, 3122, 10F, 4F, 20, () -> Ingredient.ofItems(Items.DIAMOND)),
    NETHERITEHAMMER(4, 4062, 14F, 6F, 34, () -> Ingredient.ofItems(Items.NETHERITE_INGOT));

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantanility;
    private final Supplier<Ingredient> repairIngredient;

    ModToolMaterial(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantanility, Supplier<Ingredient> repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantanility = enchantanility;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurability() {
        return this.itemDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantanility;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
