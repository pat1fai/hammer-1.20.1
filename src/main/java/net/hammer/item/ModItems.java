package net.hammer.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.hammer.HammerMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item WOODHAMMER = new HammerItem(
            ModToolMaterial.WOODHAMMER, 1, -1F, new FabricItemSettings()
    );
    public static final Item STONEHAMMER = new HammerItem(
            ModToolMaterial.STONEHAMMER, 2, -2F, new FabricItemSettings()
    );
    public static final Item IRONHAMMER = new HammerItem(
            ModToolMaterial.IRONHAMMER, 3, -3F, new FabricItemSettings()
    );
    public static final Item GOLDHAMMER = new HammerItem(
            ModToolMaterial.GOLDHAMMER, 2, -3F, new FabricItemSettings()
    );
    public static final Item DIAMONDHAMMER = new HammerItem(
            ModToolMaterial.DIAMONDHAMMER, 3, -3F, new FabricItemSettings()
    );


    public static void registerModItems() {
        HammerMod.LOGGER.info("Registering Mod Items...");

        Registry.register(Registries.ITEM, new Identifier(HammerMod.MOD_ID, "wood_hammer"), WOODHAMMER);
        Registry.register(Registries.ITEM, new Identifier(HammerMod.MOD_ID, "stone_hammer"), STONEHAMMER);
        Registry.register(Registries.ITEM, new Identifier(HammerMod.MOD_ID, "iron_hammer"), IRONHAMMER);
        Registry.register(Registries.ITEM, new Identifier(HammerMod.MOD_ID, "gold_hammer"), GOLDHAMMER);
        Registry.register(Registries.ITEM, new Identifier(HammerMod.MOD_ID, "diamond_hammer"), DIAMONDHAMMER);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(WOODHAMMER);
            entries.add(STONEHAMMER);
            entries.add(IRONHAMMER);
            entries.add(GOLDHAMMER);
            entries.add(DIAMONDHAMMER);
        });
    }
}
