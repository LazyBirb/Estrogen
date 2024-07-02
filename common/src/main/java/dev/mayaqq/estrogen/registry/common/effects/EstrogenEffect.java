package dev.mayaqq.estrogen.registry.common.effects;

import dev.architectury.networking.NetworkManager;
import dev.mayaqq.estrogen.client.Dash;
import dev.mayaqq.estrogen.config.EstrogenConfig;
import dev.mayaqq.estrogen.networking.EstrogenC2S;
import dev.mayaqq.estrogen.networking.EstrogenStatusEffectSender;
import dev.mayaqq.estrogen.registry.client.EstrogenKeybinds;
import dev.mayaqq.estrogen.registry.common.EstrogenAttributes;
import dev.mayaqq.estrogen.utils.PlayerLookup;
import dev.mayaqq.estrogen.utils.Time;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

import static dev.mayaqq.estrogen.registry.common.EstrogenAttributes.*;
import static dev.mayaqq.estrogen.registry.common.EstrogenEffects.ESTROGEN_EFFECT;

public class EstrogenEffect extends MobEffect {

    private static final UUID DASH_MODIFIER_UUID = UUID.fromString("2a2591c5-009f-4b24-97f2-b15f43415e4c");
    public short currentDashes = 0;
    public int dashCooldown = 0;
    public int groundCooldown = 0;
    public boolean onCooldown = false;
    private BlockPos lastPos = null;
    private Vec3 dashDirection = null;
    private double dashDeltaModifier = 0.0;
    private final double dashSpeed = 1.0;
    private final double dashEndSpeed = 0.4;
    private final double hyperHSpeed = 2.5;
    private final double hyperVSpeed = 0.5;
    private final double superHSpeed = 1.8;
    private final double superVSpeed = 1.0;
    private boolean canHyper = false;
    private boolean canSuper = false;

    public EstrogenEffect(MobEffectCategory statusEffectType, int color) {
        super(statusEffectType, color);
    }

    private static Boolean shouldRefreshDash(Player player) {
        return player.isOnGround() || player.getLevel().getBlockState(player.blockPosition()).getBlock() instanceof LiquidBlock;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!EstrogenConfig.server().dashEnabled.get()) return;
        if (!(entity instanceof Player player && player.getLevel().isClientSide && player instanceof LocalPlayer)) return;

        groundCooldown--;
        if (groundCooldown < 0) groundCooldown = 0;

        // Whole Dash Mechanic
        if (shouldRefreshDash(player) && groundCooldown == 0) {
            groundCooldown = 4;
            currentDashes = (short) player.getAttributeValue(EstrogenAttributes.DASH_LEVEL.get());
        }
        onCooldown = dashCooldown > 0 || currentDashes == 0;
        Dash.onCooldown = onCooldown;

        // During Dash
        Dash: if (dashCooldown > 0) {
            dashCooldown--;

            // End Dash
            if (dashCooldown == 0) {
                player.setDeltaMovement(dashDirection.scale(dashEndSpeed).scale(dashDeltaModifier));
                break Dash;
            }

            player.setDeltaMovement(dashDirection.scale(dashSpeed).scale(dashDeltaModifier));

            // Hyper (Wavedash)
            Minecraft client = Minecraft.getInstance();
            if (canHyper && client.options.keyJump.isDown() && player.isOnGround()) {
                player.setDeltaMovement(
                        player.getLookAngle().x * hyperHSpeed,
                        hyperVSpeed,
                        player.getLookAngle().z * hyperHSpeed
                );
                canHyper = false;
                dashCooldown = 0;
                break Dash;
            }
            // Super
            if (canSuper && client.options.keyJump.isDown() && player.isOnGround()) {
                player.setDeltaMovement(
                        player.getLookAngle().x * superHSpeed,
                        superVSpeed,
                        player.getLookAngle().z * superHSpeed
                );
                canSuper = false;
                dashCooldown = 0;
                break Dash;
            }

            // Dash Particles
            if (dashCooldown > 0 && dashCooldown % 2 == 0 && entity.blockPosition() != lastPos) {
                NetworkManager.sendToServer(EstrogenC2S.DASH_PARTICLES, new FriendlyByteBuf(Unpooled.buffer()));
            }
            lastPos = entity.blockPosition();
        }

        // Begin Dash
        if (EstrogenKeybinds.dashKey.consumeClick() && !onCooldown) {
            dashCooldown = 5;
            currentDashes--;
            dashDirection = player.getLookAngle();
            dashDeltaModifier = EstrogenConfig.server().dashDeltaModifier.get();

            if (player.getXRot() > 22.5 && player.getXRot() < 67.5) canHyper = true;
            else if (player.getXRot() > -22.5 && player.getXRot() < 22.5) canSuper = true;

            NetworkManager.sendToServer(EstrogenC2S.DASH, new FriendlyByteBuf(Unpooled.buffer()));
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        if (entity instanceof ServerPlayer player) {
            new EstrogenStatusEffectSender().sendRemovePlayerStatusEffect(player, ESTROGEN_EFFECT, PlayerLookup.tracking(player).toArray(ServerPlayer[]::new));
        }

        if (entity instanceof Player) {
            entity.getAttribute(EstrogenAttributes.DASH_LEVEL.get()).removeModifier(DASH_MODIFIER_UUID);
        }

        if (entity instanceof Player player && player.getLevel().isClientSide && player instanceof LocalPlayer) {
            resetDash(entity);
        }

        if (!entity.hasEffect(ESTROGEN_EFFECT) && entity instanceof Player) {
            entity.getAttribute(BOOB_INITIAL_SIZE.get()).setBaseValue(0.0);
            entity.getAttribute(BOOB_GROWING_START_TIME.get()).setBaseValue(-1.0);
        }
    }

    public void resetDash(LivingEntity entity) {
        currentDashes = 0;
        onCooldown = false;
        Dash.onCooldown = false;
        dashCooldown = 0;
        groundCooldown = 0;
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {

        if (entity instanceof ServerPlayer player) {
            new EstrogenStatusEffectSender().sendPlayerStatusEffect(player, ESTROGEN_EFFECT, PlayerLookup.tracking(player).toArray(ServerPlayer[]::new));
        }

        super.addAttributeModifiers(entity, attributes, amplifier);

        AttributeModifier dashModifier = new AttributeModifier(DASH_MODIFIER_UUID, "Dash Level", amplifier + 1, AttributeModifier.Operation.ADDITION);
        entity.getAttribute(DASH_LEVEL.get()).removeModifier(DASH_MODIFIER_UUID);
        entity.getAttribute(DASH_LEVEL.get()).addPermanentModifier(dashModifier);

        AttributeInstance startTime = entity.getAttribute(BOOB_GROWING_START_TIME.get());
        // should fix crash related to applying effect to entity without given attribute
        if (startTime != null && startTime.getBaseValue() < 0.0) {
            double currentTime = Time.currentTime(entity.getLevel());
            entity.getAttribute(BOOB_GROWING_START_TIME.get()).setBaseValue(currentTime);
        }
    }
}
