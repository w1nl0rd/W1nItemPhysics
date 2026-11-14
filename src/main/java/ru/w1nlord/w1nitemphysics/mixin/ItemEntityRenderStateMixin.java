package ru.w1nlord.w1nitemphysics.mixin;

import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import ru.w1nlord.w1nitemphysics.accessor.IItemEntityRenderState;

@Mixin(ItemEntityRenderState.class)
public abstract class ItemEntityRenderStateMixin implements IItemEntityRenderState {
    @Unique
    private boolean onGround;

    @Override
    public boolean isOnGround() {
        return onGround;
    }

    @Override
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}