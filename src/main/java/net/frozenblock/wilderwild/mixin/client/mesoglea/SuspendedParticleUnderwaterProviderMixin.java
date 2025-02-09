package net.frozenblock.wilderwild.mixin.client.mesoglea;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.wilderwild.block.MesogleaBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SuspendedParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(SuspendedParticle.UnderwaterProvider.class)
public class SuspendedParticleUnderwaterProviderMixin {

	@WrapOperation(
		method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/SuspendedParticle;setColor(FFF)V"
		)
	)
	public void wilderWild$changeColorToMesoglea(
		SuspendedParticle instance, float r, float g, float b, Operation<Void> original,
		SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z
	) {
		BlockState state = clientLevel.getBlockState(BlockPos.containing(x, y, z));
		if (state.getBlock() instanceof MesogleaBlock mesogleaBlock) {
			int mesogleaColor = mesogleaBlock.getWaterFogColorOverride();
			r = FastColor.ARGB32.red(mesogleaColor) / 255F;
			g = FastColor.ARGB32.green(mesogleaColor) / 255F;
			b  = FastColor.ARGB32.blue(mesogleaColor) / 255F;
		}

		original.call(instance, r, g, b);
	}

}
