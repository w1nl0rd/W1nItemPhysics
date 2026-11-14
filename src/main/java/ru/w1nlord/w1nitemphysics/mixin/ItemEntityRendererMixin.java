package ru.w1nlord.w1nitemphysics.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.w1nlord.w1nitemphysics.accessor.IItemEntityRenderState;
import ru.w1nlord.w1nitemphysics.config.Config;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/ItemEntity;Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;F)V", at = @At("TAIL"))
    private void updatePhysicsState(ItemEntity entity, ItemEntityRenderState state, float tickDelta, CallbackInfo ci) {
        ((IItemEntityRenderState) state).setOnGround(entity.isOnGround());
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"), index = 0)
    private Quaternionf modifyRotation(Quaternionf quaternion) {
        return switch (Config.getMode()) {
            case NORMAL -> quaternion;
            case THREE_D, TWO_D -> new Quaternionf();
        };
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/ItemEntityRenderer;renderStack(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/ItemStackEntityRenderState;Lnet/minecraft/util/math/random/Random;)V", shift = At.Shift.BEFORE))
    private void applyPhysics(ItemEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        switch (Config.getMode()) {
            case NORMAL -> {
            }
            case THREE_D -> {
                if (((IItemEntityRenderState) state).isOnGround()) {
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                } else {
                    float rotation = ItemEntity.getRotation(state.age, state.uniqueOffset) * 300.0F;
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation));
                }
            }
            case TWO_D -> matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());
        }
    }

    @ModifyArg(method = "render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"), index = 1)
    private float modifyHeight(float y) {
        return switch (Config.getMode()) {
            case NORMAL, TWO_D -> y;
            case THREE_D -> 0.1F;
        };
    }
}