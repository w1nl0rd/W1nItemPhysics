package ru.w1nlord.w1nitemphysics.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.w1nlord.w1nitemphysics.accessor.IItemEntityRenderState;
import ru.w1nlord.w1nitemphysics.config.Config;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {
    @Unique
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void updatePhysicsState(ItemEntity entity, ItemEntityRenderState state, float tickDelta, CallbackInfo ci) {
        ((IItemEntityRenderState) state).setOnGround(entity.isOnGround());
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionfc;)V"), index = 0)
    private Quaternionfc modifyRotation(Quaternionfc quaternion) {
        return switch (Config.getMode()) {
            case NORMAL -> quaternion;
            case THREE_D, TWO_D -> new Quaternionf();
        };
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void applyPhysics(ItemEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState, CallbackInfo ci) {
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
            case TWO_D -> matrices.multiply(mc.gameRenderer.getCamera().getRotation());
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"), index = 1)
    private float modifyHeight(float y) {
        return switch (Config.getMode()) {
            case NORMAL -> y;
            case THREE_D, TWO_D -> 0.1F;
        };
    }
}