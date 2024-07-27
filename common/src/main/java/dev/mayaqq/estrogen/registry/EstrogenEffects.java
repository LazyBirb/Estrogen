package dev.mayaqq.estrogen.registry;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import dev.mayaqq.estrogen.Estrogen;
import dev.mayaqq.estrogen.registry.effects.EstrogenEffect;
import dev.mayaqq.estrogen.registry.effects.TestosteroneEffect;
import dev.mayaqq.estrogen.utils.EstrogenColors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;


public class EstrogenEffects {

    public static final ResourcefulRegistry<MobEffect> MOB_EFFECTS = ResourcefulRegistries.create(BuiltInRegistries.MOB_EFFECT, Estrogen.MOD_ID);

    public static final RegistryEntry<MobEffect> ESTROGEN_EFFECT = MOB_EFFECTS.register("estrogen", () -> new EstrogenEffect(MobEffectCategory.BENEFICIAL, 104164161));
    public static final RegistryEntry<MobEffect> TESTOSTERONE_EFFECT = MOB_EFFECTS.register("testosterone", () -> new TestosteroneEffect(MobEffectCategory.BENEFICIAL, EstrogenColors.TESTOSTERONE_MIXTURE.value));
}