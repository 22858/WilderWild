package net.frozenblock.wilderwild.block;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class NematocystBlock extends AmethystClusterBlock implements SimpleWaterloggedBlock {

    public NematocystBlock(int height, int xzOffset, Properties properties) {
        super(height, xzOffset, properties);
    }

    @Override
    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
    }

    @Override
    public boolean skipRendering(@NotNull BlockState blockState, BlockState blockState2, @NotNull Direction direction) {
        return blockState2.is(this) || super.skipRendering(blockState, blockState2, direction);
    }
}
