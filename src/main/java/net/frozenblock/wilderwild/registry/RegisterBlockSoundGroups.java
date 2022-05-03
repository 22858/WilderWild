package net.frozenblock.wilderwild.registry;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

import static net.frozenblock.wilderwild.registry.RegisterSounds.*;

public class RegisterBlockSoundGroups {

    public static BlockSoundGroup OSSEOUS_SCULK = new BlockSoundGroup(1.0f, 1.0f,
            BLOCK_OSSEOUS_SCULK_BREAK,
            BLOCK_OSSEOUS_SCULK_STEP,
            BLOCK_OSSEOUS_SCULK_PLACE,
            BLOCK_OSSEOUS_SCULK_HIT,
            BLOCK_OSSEOUS_SCULK_FALL);

    public static BlockSoundGroup HANGING_TENDRIL = new BlockSoundGroup(1.0f, 1.25f,
            RegisterSounds.BLOCK_HANGING_TENDRIL_BREAK,
            RegisterSounds.BLOCK_HANGING_TENDRIL_STEP,
            RegisterSounds.BLOCK_HANGING_TENDRIL_PLACE,
            RegisterSounds.BLOCK_HANGING_TENDRIL_HIT,
            RegisterSounds.BLOCK_HANGING_TENDRIL_FALL);

    public static BlockSoundGroup ECHO_GLASS = new BlockSoundGroup(1.0f, 1.0f,
            RegisterSounds.BLOCK_ECHO_GLASS_CRACK,
            SoundEvents.BLOCK_GLASS_STEP,
            SoundEvents.BLOCK_GLASS_PLACE,
            SoundEvents.BLOCK_GLASS_HIT,
            SoundEvents.BLOCK_GLASS_FALL);


    public static void init() {
        //Just to make sure this class gets loaded.
    }
}
