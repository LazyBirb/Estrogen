package dev.mayaqq.estrogen.registry;

import dev.mayaqq.estrogen.Estrogen;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;

public class EstrogenParticles {
    public static final Registrar<ParticleType<?>> PARTICLES = Estrogen.REGISTRIES.getRegistrar(Registries.PARTICLE_TYPE);

    public static final RegistryEntry<SimpleParticleType> DASH = PARTICLES.<SimpleParticleType>entry("dash", () -> new SimpleParticleType(true) {}).register();
    public static final RegistryEntry<SimpleParticleType> MOTH_FUZZ = PARTICLES.<SimpleParticleType>entry("moth_fuzz", () -> new SimpleParticleType(true) {}).register();
}
