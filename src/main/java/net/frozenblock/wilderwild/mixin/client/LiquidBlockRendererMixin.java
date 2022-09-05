package net.frozenblock.wilderwild.mixin.client;

import net.frozenblock.wilderwild.block.MesogleaBlock;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LiquidBlockRenderer.class, priority = 69420)
public class LiquidBlockRendererMixin {

    @Inject(method = "shouldRenderFace", at = @At(value = "HEAD"), cancellable = true)
    private static void shouldRenderFace(BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, FluidState fluidState, BlockState blockState, Direction direction, FluidState fluidState2, CallbackInfoReturnable<Boolean> info) {
        if (blockState.getBlock() instanceof MesogleaBlock) {
            if (direction != Direction.UP) {
                info.cancel();
                info.setReturnValue(false);
            }
        }
    }

}
