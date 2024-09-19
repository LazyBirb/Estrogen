package dev.mayaqq.estrogen.registry;

import dev.mayaqq.estrogen.Estrogen;
import dev.mayaqq.estrogen.registry.enchantments.UwufyCurseEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

public class EstrogenEnchantments {

    public static final Registrar<Enchantment> ENCHANTMENTS = Estrogen.REGISTRIES.getRegistrar(Registries.ENCHANTMENT);

    public static final RegistryEntry<UwufyCurseEnchantment> UWUFYING_CURSE = ENCHANTMENTS.entry("uwufy_curse", () ->
        new UwufyCurseEnchantment(Enchantment.Rarity.VERY_RARE, EnchantmentCategory.ARMOR_HEAD, EquipmentSlot.HEAD)).register();
}
