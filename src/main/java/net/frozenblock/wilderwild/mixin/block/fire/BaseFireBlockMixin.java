/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of Wilder Wild.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.frozenblock.wilderwild.mixin.block.fire;

import net.frozenblock.wilderwild.config.BlockConfig;
import net.frozenblock.wilderwild.registry.RegisterSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {

	@Inject(method = "animateTick", at = @At("HEAD"))
	public void wilderWild$animateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo info) {
		if (BlockConfig.get().fire.soulFireSounds && state.is(Blocks.SOUL_FIRE) && random.nextInt(48) == 0) {
			level.playLocalSound(
				pos.getX() + 0.5,
				pos.getY() + 0.5,
				pos.getZ() + 0.5,
				RegisterSounds.BLOCK_SOUL_FIRE_AMBIENT,
				SoundSource.BLOCKS,
				0.6F + random.nextFloat(),
				random.nextFloat() * 0.7F + 0.3F,
				false
			);
		}
	}

	@Inject(
		method = "animateTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/BaseFireBlock;canBurn(Lnet/minecraft/world/level/block/state/BlockState;)Z",
			ordinal = 0,
			shift = At.Shift.BEFORE
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void wilderWild$magmaSmoke(
		BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo info,
		BlockPos blockPos2,BlockState blockState2
	) {
		if (BlockConfig.get().fire.extraMagmaParticles && blockState2.is(Blocks.MAGMA_BLOCK)) {
			if (random.nextFloat() <= 0.015F) {
				level.addParticle(
					ParticleTypes.LAVA,
					false,
					(double) pos.getX() + 0.5D + random.nextDouble() / 3D * (random.nextBoolean() ? 1D : -1D),
					(double) pos.getY() + random.nextDouble() + random.nextDouble(),
					(double) pos.getZ() + 0.5D + random.nextDouble() / 3D * (random.nextBoolean() ? 1D : -1D),
					(random.nextDouble() - 0.5D) * 0.05D,
					random.nextDouble() * 0.12D + 0.06D,
					(random.nextDouble() - 0.5D) * 0.05D
				);
			}

			if (random.nextFloat() <= 0.175F) {
				level.addParticle(
					ParticleTypes.LARGE_SMOKE,
					false,
					(double) pos.getX() + 0.5D + random.nextDouble() / 3D * (random.nextBoolean() ? 1D : -1D),
					(double) pos.getY() + random.nextDouble() + random.nextDouble(),
					(double) pos.getZ() + 0.5D + random.nextDouble() / 3D * (random.nextBoolean() ? 1D : -1D),
					(random.nextDouble() - 0.5D) * 0.05D,
					random.nextDouble() * 0.12D + 0.06D,
					(random.nextDouble() - 0.5D) * 0.05D
				);
			}
		}
	}

}
