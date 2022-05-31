package net.frozenblock.wilderwild.registry;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.frozenblock.wilderwild.WilderWild;
import net.frozenblock.wilderwild.block.*;
import net.frozenblock.wilderwild.block.entity.TermiteMoundBlockEntity;
import net.frozenblock.wilderwild.item.FloweredLilyPadItem;
import net.frozenblock.wilderwild.mixin.SignTypeAccessor;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.SignType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;

public abstract class RegisterBlocks {
    // CHISELED PACKED MUD
    public static final Block CHISELED_MUD_BRICKS = registerBlock("chiseled_mud_bricks",
            new Block(FabricBlockSettings.copy(Blocks.CHISELED_STONE_BRICKS)
                    .strength(1.5F)
                    .requiresTool()
                    .sounds(BlockSoundGroup.MUD_BRICKS)),
            ItemGroup.BUILDING_BLOCKS
    );

    // FLOWERED LILY PAD
    public static final Block FLOWERED_LILY_PAD = new FloweredLilyPadBlock(
            FabricBlockSettings.copy(Blocks.LILY_PAD)
            .sounds(RegisterBlockSoundGroups.LILYPAD)
    );

    // HOLLOW LOGS
    public static final Block HOLLOWED_OAK_LOG = registerBlock("hollowed_oak_log", createHollowedLogBlock(MapColor.OAK_TAN, MapColor.SPRUCE_BROWN), ItemGroup.DECORATIONS);
    public static final Block HOLLOWED_SPRUCE_LOG = registerBlock("hollowed_spruce_log", createHollowedLogBlock(MapColor.SPRUCE_BROWN, MapColor.BROWN), ItemGroup.DECORATIONS);
    public static final Block HOLLOWED_BIRCH_LOG = registerBlock("hollowed_birch_log", createHollowedLogBlock(MapColor.PALE_YELLOW, MapColor.OFF_WHITE), ItemGroup.DECORATIONS);
    public static final Block HOLLOWED_JUNGLE_LOG = registerBlock("hollowed_jungle_log", createHollowedLogBlock(MapColor.DIRT_BROWN, MapColor.SPRUCE_BROWN), ItemGroup.DECORATIONS);
    public static final Block HOLLOWED_ACACIA_LOG = registerBlock("hollowed_acacia_log", createHollowedLogBlock(MapColor.ORANGE, MapColor.STONE_GRAY), ItemGroup.DECORATIONS);
    public static final Block HOLLOWED_DARK_OAK_LOG = registerBlock("hollowed_dark_oak_log", createHollowedLogBlock(MapColor.BROWN, MapColor.BROWN), ItemGroup.DECORATIONS);
    public static final Block HOLLOWED_MANGROVE_LOG = registerBlock("hollowed_mangrove_log", createHollowedLogBlock(MapColor.RED, MapColor.SPRUCE_BROWN), ItemGroup.DECORATIONS);
    // SCULK
    public static final Block SCULK_ECHOER = registerBlock("sculk_echoer",
            new SculkEchoerBlock(AbstractBlock.Settings
                    .of(Material.SCULK, MapColor.CYAN)
                    .strength(3.0F, 3.0F)
                    .sounds(BlockSoundGroup.SCULK_CATALYST), 8),
            ItemGroup.DECORATIONS
    );
    public static final Block SCULK_JAW = registerBlock("sculk_jaw",
            new SculkJawBlock(AbstractBlock.Settings
                    .of(Material.SCULK)
                    .strength(0.6F)
                    .sounds(BlockSoundGroup.SCULK)),
            ItemGroup.DECORATIONS
    );
    public static final Block OSSEOUS_SCULK = registerBlock("osseous_sculk",
            new OsseousSculkBlock(AbstractBlock.Settings
                    .of(Material.STONE,
                    MapColor.PALE_YELLOW)
                    .requiresTool()
                    .strength(2.0F)
                    .sounds(RegisterBlockSoundGroups.OSSEOUS_SCULK)),
            ItemGroup.DECORATIONS
    );
    public static final Block HANGING_TENDRIL = registerBlock("hanging_tendril",
            new HangingTendrilBlock(FabricBlockSettings
                    .copyOf(Blocks.SCULK_SENSOR)
                    .strength(0.7F)
                    .collidable(false)
                    .luminance((state) -> 1)
                    .sounds(RegisterBlockSoundGroups.HANGING_TENDRIL)
                    .emissiveLighting((state, world, pos) -> HangingTendrilBlock.shouldHavePogLighting(state)), 4),
            ItemGroup.DECORATIONS
    );
    public static final Block ECHO_GLASS = registerBlock("echo_glass",
            new EchoGlassBlock(AbstractBlock.Settings
                    .of(Material.GLASS, MapColor.CYAN)
                    .strength(0.3F)
                    .nonOpaque()
                    .sounds(RegisterBlockSoundGroups.ECHO_GLASS)
            ),
            ItemGroup.DECORATIONS
    );

    public static final Block TERMITE_MOUND = registerBlock("termite_mound",
            new TermiteMound(AbstractBlock.Settings
                    .of(Material.WOOD, MapColor.BROWN)
                    .strength(0.3F)
                    .sounds(RegisterBlockSoundGroups.COARSEDIRT)
            ),
            ItemGroup.DECORATIONS
    );

    // PLANTS
    public static final Block DATURA = registerBlock("datura",
            new TallFlowerBlock(FabricBlockSettings.copy(Blocks.ROSE_BUSH)
                    .strength(0.0F)
                    .nonOpaque()),
            ItemGroup.DECORATIONS
    );
    public static final Block CATTAIL = registerBlock("cattail",
            new WaterloggableTallFlowerBlock(FabricBlockSettings.copy(Blocks.ROSE_BUSH)
                    .sounds(BlockSoundGroup.WET_GRASS)
                    .strength(0.0F)
                    .nonOpaque()),
            ItemGroup.DECORATIONS
    );
    public static final Block CARNATION = registerBlock("carnation",
            new FlowerBlock(StatusEffects.REGENERATION, 12, FabricBlockSettings
            .copy(Blocks.DANDELION)
            .sounds(BlockSoundGroup.SPORE_BLOSSOM)
            .strength(0.0F)
            .nonOpaque()),
            ItemGroup.DECORATIONS
    );
    public static final Block POTTED_CARNATION = registerBlockWithoutBlockItem("potted_carnation",
            new FlowerPotBlock(RegisterBlocks.CARNATION,
                    AbstractBlock.Settings.of(Material.DECORATION)
                            .breakInstantly()
                            .nonOpaque())
    );
    public static final Block WHITE_DANDELION = registerBlock("white_dandelion",
            new WhiteDandelionBlock(StatusEffects.SLOW_FALLING, 12, FabricBlockSettings
                    .copy(Blocks.DANDELION)
                    .sounds(BlockSoundGroup.SPORE_BLOSSOM)
                    .strength(0.0F)
                    .nonOpaque()),
            ItemGroup.DECORATIONS
    );
    public static final Block POTTED_WHITE_DANDELION = registerBlockWithoutBlockItem("potted_white_dandelion",
            new FlowerPotBlock(RegisterBlocks.WHITE_DANDELION,
                    AbstractBlock.Settings.of(Material.DECORATION)
                            .breakInstantly()
                            .nonOpaque())
    );
    public static final Block MILKWEED = registerBlock("milkweed",
            new MilkweedBlock(FabricBlockSettings.copy(Blocks.ROSE_BUSH)
                    .strength(0.0F)
                    .nonOpaque()),
            ItemGroup.DECORATIONS
    );

 
    public static final Block POLLEN_BLOCK = registerBlock("pollen",
            new PollenBlock(FabricBlockSettings
            .copyOf(Blocks.GRASS)
            .collidable(false)
            .mapColor(MapColor.PALE_YELLOW)
            .sounds(BlockSoundGroup.VINE)),
            ItemGroup.DECORATIONS
    );
    public static final Block BROWN_SHELF_FUNGUS = registerBlock("brown_shelf_fungus",
            new ShelfFungusBlock(FabricBlockSettings
                    .copyOf(Blocks.BROWN_MUSHROOM_BLOCK)
                    .lightLevel(1)
                    .collidable(false)
                    .nonOpaque()
                    .sounds(RegisterBlockSoundGroups.MUSHROOM)),
            ItemGroup.DECORATIONS
    );
    public static final Block RED_SHELF_FUNGUS = registerBlock("red_shelf_fungus",
            new ShelfFungusBlock(FabricBlockSettings
                    .copyOf(Blocks.RED_MUSHROOM_BLOCK)
                    .collidable(false)
                    .nonOpaque()
                    .sounds(RegisterBlockSoundGroups.MUSHROOM)),
            ItemGroup.DECORATIONS
    );

    private static Block registerBlockWithoutBlockItem(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(WilderWild.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(WilderWild.MOD_ID, name), block);
    }

    private static BlockItem registerBlockItem(String name, Block block, ItemGroup group) {
        return Registry.register(Registry.ITEM, new Identifier(WilderWild.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(group)));
    }

    private static HollowedLogBlock createHollowedLogBlock(MapColor topMapColor, MapColor sideMapColor) {
        return new HollowedLogBlock(AbstractBlock.Settings.of(Material.WOOD,
                (state) -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor)
                .strength(2.0F).sounds(BlockSoundGroup.WOOD));
    }


    public static void registerBlocks() {
        WilderWild.logWild("Registering Blocks for", true);
        Registry.register(Registry.BLOCK, new Identifier(WilderWild.MOD_ID, "flowered_lily_pad"), FLOWERED_LILY_PAD);
        Registry.register(Registry.ITEM, new Identifier(WilderWild.MOD_ID, "flowered_lily_pad"), new FloweredLilyPadItem(FLOWERED_LILY_PAD, new FabricItemSettings().group(ItemGroup.DECORATIONS)));
        CompostingChanceRegistry.INSTANCE.add(CARNATION, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(CATTAIL, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(DATURA, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(MILKWEED, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(RegisterItems.MILKWEED_POD, 0.25F);
        CompostingChanceRegistry.INSTANCE.add(WHITE_DANDELION, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(FLOWERED_LILY_PAD, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(BROWN_SHELF_FUNGUS, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(RED_SHELF_FUNGUS, 0.65F);
    }
    public static final String name = "baobab";
    public static final MapColor planksColor = MapColor.ORANGE;
    public static final MapColor barkColor = MapColor.BROWN;
    
    public static final SignType BAOBAB_SIGN_TYPE = SignTypeAccessor.registerNew(SignTypeAccessor.newSignType("baobab"));
    public static final Block BAOBAB_PLANKS = registerBlock(name + "_planks", new Block(AbstractBlock.Settings.of(Material.WOOD, planksColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_LOG = registerBlock(name + "_log", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? planksColor : barkColor).strength(2.0F).sounds(BlockSoundGroup.WOOD)), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_STRIPPED_LOG = registerBlock("stripped_" + name + "_log", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? planksColor : barkColor).strength(2.0F).sounds(BlockSoundGroup.WOOD)), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_WOOD = registerBlock(name + "_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? planksColor : barkColor).strength(2.0F).sounds(BlockSoundGroup.WOOD)), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_STRIPPED_WOOD = registerBlock("stripped_" + name + "_wood", new PillarBlock(AbstractBlock.Settings.of(Material.WOOD, state -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? planksColor : barkColor).strength(2.0F).sounds(BlockSoundGroup.WOOD)), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_HOLLOWED_LOG = registerBlock("hollowed_" + name + "_log", createHollowedLogBlock(planksColor, barkColor), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_SLAB = registerBlock(name + "_slab", new SlabBlock(AbstractBlock.Settings.of(Material.WOOD, planksColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_STAIRS = registerBlock(name + "_stairs", new StairsBlock(BAOBAB_PLANKS.getDefaultState(), AbstractBlock.Settings.copy(BAOBAB_PLANKS)), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_DOOR = registerBlock(name + "_door", new DoorBlock(AbstractBlock.Settings.of(Material.WOOD, planksColor).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque()), ItemGroup.REDSTONE);
    public static final Block BAOBAB_TRAPDOOR = registerBlock("name" + "_trapdoor", new TrapdoorBlock(AbstractBlock.Settings.of(Material.WOOD, planksColor).strength(3.0F).sounds(BlockSoundGroup.WOOD).nonOpaque().allowsSpawning(RegisterBlocks::never)), ItemGroup.REDSTONE);
    public static final Block BAOBAB_FENCE = registerBlock(name + "_fence", new FenceBlock(AbstractBlock.Settings.of(Material.WOOD, planksColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_GATE = registerBlock(name + "_fence_gate", new FenceGateBlock(AbstractBlock.Settings.of(Material.WOOD, planksColor).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD)), ItemGroup.REDSTONE);
    public static final Block BAOBAB_PLATE = registerBlock(name + "_pressure_plate", new PressurePlateBlock(PressurePlateBlock.ActivationRule.EVERYTHING, AbstractBlock.Settings.of(Material.WOOD, planksColor).noCollision().strength(0.5F).sounds(BlockSoundGroup.WOOD)), ItemGroup.REDSTONE);
    public static final Block BAOBAB_LEAVES = registerBlock(name + "_leaves", new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES, MapColor.GREEN).strength(0.2F).ticksRandomly().sounds(BlockSoundGroup.GRASS).nonOpaque().allowsSpawning(RegisterBlocks::canSpawnOnLeaves).suffocates(RegisterBlocks::never).blockVision(RegisterBlocks::never)), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_BUTTON = registerBlock(name + "_button", new WoodenButtonBlock(AbstractBlock.Settings.copy(Blocks.OAK_BUTTON).mapColor(planksColor)), ItemGroup.REDSTONE);
    public static final Block BAOBAB_SIGN = registerBlock(name + "_sign", new SignBlock(AbstractBlock.Settings.of(Material.WOOD, barkColor).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD), BAOBAB_SIGN_TYPE), ItemGroup.BUILDING_BLOCKS);
    public static final Block BAOBAB_WALL_SIGN = registerBlockWithoutBlockItem(name + "_wall_sign", new SignBlock(AbstractBlock.Settings.of(Material.WOOD, barkColor).noCollision().strength(1.0F).sounds(BlockSoundGroup.WOOD).dropsLike(BAOBAB_SIGN), BAOBAB_SIGN_TYPE));

    public static void addBaobab() {
        StrippableBlockRegistry.register(BAOBAB_LOG, BAOBAB_STRIPPED_LOG);
        StrippableBlockRegistry.register(BAOBAB_WOOD, BAOBAB_STRIPPED_WOOD);

        TermiteMoundBlockEntity.Termite.addDegradable(BAOBAB_LOG, BAOBAB_HOLLOWED_LOG);
        TermiteMoundBlockEntity.Termite.addDegradable(BAOBAB_STRIPPED_LOG, Blocks.AIR);
        TermiteMoundBlockEntity.Termite.addDegradable(BAOBAB_WOOD, BAOBAB_STRIPPED_WOOD);
        TermiteMoundBlockEntity.Termite.addDegradable(BAOBAB_STRIPPED_WOOD, Blocks.AIR);
    }

    protected static boolean never(BlockState state, BlockView world, BlockPos pos) { return false; }

    private static boolean never(BlockState state, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) { return false; }

    protected static Boolean canSpawnOnLeaves(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) { return type == EntityType.OCELOT || type == EntityType.PARROT; }
}
