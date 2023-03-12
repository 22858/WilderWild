/*
 * Copyright 2022-2023 FrozenBlock
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

package net.frozenblock.wilderwild.misc;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.frozenblock.lib.datagen.api.FrozenBiomeTagProvider;
import net.frozenblock.lib.feature_flag.api.FrozenFeatureFlags;
import net.frozenblock.lib.tag.api.FrozenBiomeTags;
import net.frozenblock.lib.tag.api.FrozenItemTags;
import net.frozenblock.wilderwild.registry.RegisterBlocks;
import net.frozenblock.wilderwild.registry.RegisterEntities;
import net.frozenblock.wilderwild.registry.RegisterItems;
import net.frozenblock.wilderwild.registry.RegisterStructures;
import net.frozenblock.lib.tag.api.FrozenItemTags;
import net.frozenblock.wilderwild.registry.RegisterBlocks;
import net.frozenblock.wilderwild.registry.RegisterEntities;
import net.frozenblock.wilderwild.registry.RegisterItems;
import net.frozenblock.wilderwild.registry.RegisterWorldgen;
import net.frozenblock.wilderwild.tag.WilderBiomeTags;
import net.frozenblock.wilderwild.tag.WilderBlockTags;
import net.frozenblock.wilderwild.tag.WilderEntityTags;
import net.frozenblock.wilderwild.world.generation.WilderFeatureBootstrap;
import net.frozenblock.wilderwild.world.generation.noise.WilderNoise;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.frozenblock.wilderwild.registry.RegisterBlocks;
import net.frozenblock.wilderwild.tag.WilderBlockTags;
import net.frozenblock.wilderwild.tag.WilderEntityTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class WilderWildDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		WilderFeatureFlags.init();
		FrozenFeatureFlags.rebuild();
		final FabricDataGenerator.Pack pack = dataGenerator.createPack();
		pack.addProvider(WilderBlockLootProvider::new);
		pack.addProvider(WilderWorldgenProvider::new);
		pack.addProvider(WilderBiomeTagProvider::new);
		pack.addProvider(WilderBlockTagProvider::new);
		pack.addProvider(WilderItemTagProvider::new);
		pack.addProvider(WilderEntityTagProvider::new);
		/*final FabricDataGenerator.Pack experimentalPack = dataGenerator.createBuiltinResourcePack(WilderSharedConstants.id("update_1_20_additions"));
		experimentalPack.addProvider((FabricDataGenerator.Pack.Factory<ExperimentRecipeProvider>) ExperimentRecipeProvider::new);
		experimentalPack.addProvider(ExperimentBlockLootTableProvider::new);
		experimentalPack.addProvider(ExperimentBlockTagProvider::new);
		experimentalPack.addProvider(
				(FabricDataGenerator.Pack.Factory<PackMetadataGenerator>) packOutput -> PackMetadataGenerator.forFeaturePack(
						packOutput, Component.translatable("dataPack.wilderwild.update_1_20_additions.description"), FeatureFlagSet.of(WilderFeatureFlags.UPDATE_1_20_ADDITIONS)
				)
		);*/
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		registryBuilder.add(Registries.CONFIGURED_FEATURE, WilderFeatureBootstrap::bootstrapConfigured);
		registryBuilder.add(Registries.PLACED_FEATURE, WilderFeatureBootstrap::bootstrapPlaced);
		registryBuilder.add(Registries.BIOME, RegisterWorldgen::bootstrap);
		registryBuilder.add(Registries.NOISE, WilderNoise::bootstrap);
		registryBuilder.add(Registries.PROCESSOR_LIST, RegisterStructures::bootstrapProcessor);
		registryBuilder.add(Registries.TEMPLATE_POOL, RegisterStructures::bootstrapTemplatePool);
		registryBuilder.add(Registries.STRUCTURE, RegisterStructures::bootstrap);
		registryBuilder.add(Registries.STRUCTURE_SET, RegisterStructures::bootstrapStructureSet);
	}

	private static class WilderWorldgenProvider extends FabricDynamicRegistryProvider {

		public WilderWorldgenProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void configure(HolderLookup.Provider registries, Entries entries) {
			WilderFeatureBootstrap.bootstrap(entries);
		}

		@Override
		public String getName() {
			return "Wilder Wild Dynamic Registries";
		}
	}

	private static class WilderBiomeTagProvider extends FrozenBiomeTagProvider {

		public WilderBiomeTagProvider(FabricDataOutput output, CompletableFuture registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider arg) {
			this.generateBiomeTags();
			this.generateClimateAndVegetationTags();
			this.generateUtilityTags();
			this.generateFeatureTags();
			this.generateStructureTags();
		}

		private void generateBiomeTags() {

			this.getOrCreateTagBuilder(WilderBiomeTags.WILDER_WILD_BIOMES)
					.addOptional(RegisterWorldgen.CYPRESS_WETLANDS)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.OASIS)
					.addOptional(RegisterWorldgen.WARM_RIVER)
					.addOptional(RegisterWorldgen.JELLYFISH_CAVES)
					.addOptional(RegisterWorldgen.ARID_FOREST)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(RegisterWorldgen.PARCHED_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.FLOWER_FIELD)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.SNOWY_OLD_GROWTH_PINE_TAIGA)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(BiomeTags.IS_OVERWORLD)
					.addOptionalTag(WilderBiomeTags.WILDER_WILD_BIOMES);

			this.getOrCreateTagBuilder(BiomeTags.IS_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.CAVES)
					.addOptional(RegisterWorldgen.JELLYFISH_CAVES);

			this.getOrCreateTagBuilder(WilderBiomeTags.DARK_FOREST)
					.add(Biomes.DARK_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.GROVE)
					.add(Biomes.GROVE);

			this.getOrCreateTagBuilder(WilderBiomeTags.MEADOW)
					.add(Biomes.MEADOW);

			this.getOrCreateTagBuilder(WilderBiomeTags.NON_FROZEN_PLAINS)
					.add(Biomes.PLAINS)
					.add(Biomes.SUNFLOWER_PLAINS)
					.addOptional(RegisterWorldgen.FLOWER_FIELD);

			this.getOrCreateTagBuilder(WilderBiomeTags.NORMAL_SAVANNA)
					.add(Biomes.SAVANNA)
					.add(Biomes.SAVANNA_PLATEAU)
					.addOptional(RegisterWorldgen.ARID_SAVANNA);

			this.getOrCreateTagBuilder(WilderBiomeTags.SHORT_TAIGA)
					.add(Biomes.TAIGA)
					.add(Biomes.SNOWY_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.SNOWY_PLAINS)
					.add(Biomes.SNOWY_PLAINS);

			this.getOrCreateTagBuilder(WilderBiomeTags.TALL_PINE_TAIGA)
					.add(Biomes.OLD_GROWTH_PINE_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.TALL_SPRUCE_TAIGA)
					.add(Biomes.OLD_GROWTH_SPRUCE_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.WINDSWEPT_FOREST)
					.add(Biomes.WINDSWEPT_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.WINDSWEPT_HILLS)
					.add(Biomes.WINDSWEPT_HILLS)
					.add(Biomes.WINDSWEPT_GRAVELLY_HILLS);

			this.getOrCreateTagBuilder(WilderBiomeTags.WINDSWEPT_SAVANNA)
					.add(Biomes.WINDSWEPT_SAVANNA);

			this.getOrCreateTagBuilder(BiomeTags.IS_TAIGA)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(BiomeTags.IS_SAVANNA)
					.addOptional(RegisterWorldgen.ARID_SAVANNA);

			this.getOrCreateTagBuilder(BiomeTags.IS_RIVER)
					.addOptional(RegisterWorldgen.OASIS);

			this.getOrCreateTagBuilder(WilderBiomeTags.RAINFOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST);
		}

		private void generateClimateAndVegetationTags() {
			this.getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_HOT)
					.addOptional(RegisterWorldgen.OASIS)
					.addOptional(RegisterWorldgen.ARID_FOREST)
					.addOptional(RegisterWorldgen.ARID_SAVANNA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_TEMPERATE)
					.addOptional(RegisterWorldgen.CYPRESS_WETLANDS)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.PARCHED_FOREST)
					.addOptional(RegisterWorldgen.WARM_RIVER)
					.addOptional(RegisterWorldgen.FLOWER_FIELD)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_COLD)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.SNOWY_OLD_GROWTH_PINE_TAIGA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_WET)
					.addOptional(RegisterWorldgen.CYPRESS_WETLANDS)
					.addOptional(RegisterWorldgen.OASIS)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_DRY)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(RegisterWorldgen.ARID_FOREST)
					.addOptional(RegisterWorldgen.PARCHED_FOREST);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.TREE_CONIFEROUS)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.SNOWY_OLD_GROWTH_PINE_TAIGA)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.TREE_DECIDUOUS)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.VEGETATION_DENSE)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.CYPRESS_WETLANDS)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.RAINFOREST);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.FLORAL)
					.add(Biomes.SUNFLOWER_PLAINS)
					.addOptional(RegisterWorldgen.FLOWER_FIELD);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.SNOWY)
					.addOptional(RegisterWorldgen.SNOWY_OLD_GROWTH_PINE_TAIGA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.AQUATIC)
					.addOptional(RegisterWorldgen.WARM_RIVER)
					.addOptional(RegisterWorldgen.JELLYFISH_CAVES);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.TREE_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.TREE_SAVANNA)
					.addOptional(RegisterWorldgen.PARCHED_FOREST)
					.addOptional(RegisterWorldgen.ARID_SAVANNA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.SAVANNA)
					.addOptional(RegisterWorldgen.ARID_SAVANNA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.DESERT)
					.addOptional(RegisterWorldgen.OASIS);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.FOREST)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.PARCHED_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.ARID_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.RIVER)
					.addOptional(RegisterWorldgen.WARM_RIVER);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.SWAMP)
					.addOptional(RegisterWorldgen.CYPRESS_WETLANDS);

			this.getOrCreateTagBuilder(ConventionalBiomeTags.UNDERGROUND)
					.addOptional(RegisterWorldgen.JELLYFISH_CAVES);
		}

		private void generateUtilityTags() {
			this.getOrCreateTagBuilder(WilderBiomeTags.FIREFLY_SPAWNABLE)
					.add(Biomes.JUNGLE)
					.add(Biomes.SPARSE_JUNGLE)
					.add(Biomes.BAMBOO_JUNGLE)
					.add(Biomes.DARK_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.RAINFOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.FIREFLY_SPAWNABLE_CAVE);

			this.getOrCreateTagBuilder(WilderBiomeTags.FIREFLY_SPAWNABLE_DURING_DAY)
					.add(Biomes.SWAMP)
					.add(Biomes.MANGROVE_SWAMP);

			this.getOrCreateTagBuilder(WilderBiomeTags.NO_SNOW_BLANKET);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_JELLYFISH)
					.add(Biomes.WARM_OCEAN)
					.addOptional(RegisterWorldgen.JELLYFISH_CAVES);

			this.getOrCreateTagBuilder(WilderBiomeTags.NO_POOLS)
					.addOptional(Biomes.DEEP_DARK);

			this.getOrCreateTagBuilder(WilderBiomeTags.PEARLESCENT_JELLYFISH)
					.addOptional(RegisterWorldgen.JELLYFISH_CAVES);

			this.getOrCreateTagBuilder(WilderBiomeTags.JELLYFISH_SPECIAL_SPAWN)
					.addOptional(RegisterWorldgen.JELLYFISH_CAVES);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_TUMBLEWEED_ENTITY)
					.add(Biomes.DESERT)
					.add(Biomes.BADLANDS)
					.add(Biomes.ERODED_BADLANDS)
					.add(Biomes.WOODED_BADLANDS)
					.add(Biomes.WINDSWEPT_SAVANNA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_CLAY_PATH)
					.addOptionalTag(WilderBiomeTags.SAND_BEACHES)
					.addOptionalTag(WilderBiomeTags.MULTI_LAYER_SAND_BEACHES)
					.addOptional(RegisterWorldgen.OASIS)
					.addOptional(RegisterWorldgen.WARM_RIVER);

			this.getOrCreateTagBuilder(FrozenBiomeTags.CAN_LIGHTNING_OVERRIDE)
					.add(Biomes.DESERT)
					.add(Biomes.BADLANDS)
					.add(Biomes.ERODED_BADLANDS);

			this.getOrCreateTagBuilder(WilderBiomeTags.GRAVEL_BEACH)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.FROZEN_RIVER)
					.add(RegisterWorldgen.MIXED_FOREST)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.add(Biomes.OLD_GROWTH_PINE_TAIGA)
					.add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
					.add(Biomes.TAIGA)
					.add(Biomes.SNOWY_TAIGA)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.SAND_BEACHES)
					.add(Biomes.DARK_FOREST)
					.add(Biomes.FLOWER_FOREST)
					.add(Biomes.FOREST)
					.addOptional(RegisterWorldgen.PARCHED_FOREST)
					.addOptional(RegisterWorldgen.ARID_FOREST)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.MULTI_LAYER_SAND_BEACHES)
					.add(Biomes.BAMBOO_JUNGLE)
					.add(Biomes.JUNGLE)
					.add(Biomes.SAVANNA)
					.add(Biomes.SPARSE_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(new ResourceLocation("terralith", "arid_highlands"));
		}

		private void generateFeatureTags() {
			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_FALLEN_BIRCH_TREES)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_FALLEN_OAK_AND_BIRCH_TREES)
					.add(Biomes.FOREST)
					.add(Biomes.FLOWER_FOREST)
					.addOptional(RegisterWorldgen.PARCHED_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_FALLEN_OAK_AND_SPRUCE_TREES)
					.add(Biomes.WINDSWEPT_FOREST)
					.add(Biomes.WINDSWEPT_HILLS);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_MOSSY_FALLEN_MIXED_TREES)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_MOSSY_FALLEN_OAK_AND_BIRCH)
					.addOptional(RegisterWorldgen.RAINFOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_MOSS_LAKE)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.RAINFOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.FOREST_GRASS)
					.add(Biomes.FOREST)
					.add(Biomes.FLOWER_FOREST)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.add(Biomes.DARK_FOREST)
					.add(Biomes.TAIGA)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.FLOWER_FIELD)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_HUGE_RED_MUSHROOM)
					.add(Biomes.FOREST)
					.add(Biomes.FLOWER_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_HUGE_BROWN_MUSHROOM)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_COMMON_BROWN_MUSHROOM)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.DARK_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_COMMON_RED_MUSHROOM)
					.add(Biomes.DARK_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_BIG_MUSHROOMS)
					.add(Biomes.BIRCH_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SWAMP_MUSHROOM)
					.add(Biomes.SWAMP);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SMALL_SPONGE)
					.add(Biomes.WARM_OCEAN);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SMALL_SPONGE_RARE)
					.add(Biomes.LUKEWARM_OCEAN)
					.add(Biomes.DEEP_LUKEWARM_OCEAN)
					.add(Biomes.LUSH_CAVES);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_CARNATION)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.add(Biomes.FLOWER_FOREST)
					.add(Biomes.FOREST)
					.add(Biomes.DARK_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_DATURA)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_CATTAIL)
					.add(Biomes.SWAMP)
					.add(Biomes.MANGROVE_SWAMP)
					.add(Biomes.DARK_FOREST)
					.add(Biomes.JUNGLE)
					.add(Biomes.BAMBOO_JUNGLE)
					.add(Biomes.SPARSE_JUNGLE)
					.addOptional(RegisterWorldgen.CYPRESS_WETLANDS)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_CATTAIL_COMMON)
					.add(Biomes.SWAMP)
					.add(Biomes.MANGROVE_SWAMP)
					.add(Biomes.DARK_FOREST)
					.add(Biomes.JUNGLE)
					.addOptional(RegisterWorldgen.CYPRESS_WETLANDS)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SEEDING_DANDELION)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.add(Biomes.FLOWER_FOREST)
					.add(Biomes.SUNFLOWER_PLAINS)
					.add(Biomes.FOREST)
					.add(Biomes.MEADOW)
					.add(Biomes.WINDSWEPT_HILLS)
					.add(Biomes.WINDSWEPT_FOREST)
					.add(Biomes.DARK_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_MILKWEED)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.FLOWER_FOREST)
					.add(Biomes.SUNFLOWER_PLAINS)
					.add(Biomes.PLAINS)
					.add(Biomes.FOREST)
					.add(Biomes.MEADOW)
					.add(Biomes.SWAMP)
					.add(Biomes.SPARSE_JUNGLE)
					.add(Biomes.JUNGLE)
					.add(Biomes.DARK_FOREST)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_TUMBLEWEED_PLANT)
					.add(Biomes.DESERT)
					.add(Biomes.BADLANDS)
					.add(Biomes.ERODED_BADLANDS)
					.add(Biomes.WOODED_BADLANDS)
					.add(Biomes.WINDSWEPT_SAVANNA)
					.add(Biomes.SAVANNA_PLATEAU)
					.addOptional(RegisterWorldgen.ARID_SAVANNA);

			this.getOrCreateTagBuilder(WilderBiomeTags.SWAMP_TREES)
					.add(Biomes.SWAMP);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_PALMS)
					.add(Biomes.DESERT)
					.addOptional(RegisterWorldgen.ARID_SAVANNA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SHORT_SPRUCE)
					.add(Biomes.TAIGA)
					.add(Biomes.SNOWY_TAIGA)
					.add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
					.add(Biomes.OLD_GROWTH_PINE_TAIGA)
					.add(Biomes.WINDSWEPT_FOREST)
					.add(Biomes.WINDSWEPT_HILLS)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.SNOWY_OLD_GROWTH_PINE_TAIGA)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_BIG_SHRUB)
					.add(Biomes.BADLANDS)
					.add(Biomes.WOODED_BADLANDS)
					.add(Biomes.ERODED_BADLANDS);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_POLLEN)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.add(Biomes.FOREST)
					.add(Biomes.FLOWER_FOREST)
					.add(Biomes.SUNFLOWER_PLAINS)
					.addOptional(RegisterWorldgen.FLOWER_FIELD)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.PARCHED_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_FIELD_FLOWERS)
					.add(Biomes.SUNFLOWER_PLAINS)
					.addOptional(RegisterWorldgen.FLOWER_FIELD);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SHORT_MEGA_SPRUCE)
					.add(Biomes.TAIGA)
					.add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
					.add(Biomes.OLD_GROWTH_PINE_TAIGA)
					.add(Biomes.SNOWY_TAIGA)
					.add(Biomes.GROVE)
					.addOptional(RegisterWorldgen.SNOWY_OLD_GROWTH_PINE_TAIGA)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SHORT_MEGA_SPRUCE_SNOWY)
					.add(Biomes.SNOWY_TAIGA)
					.add(Biomes.GROVE)
					.addOptional(RegisterWorldgen.SNOWY_OLD_GROWTH_PINE_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_RED_SHELF_FUNGUS)
					.add(Biomes.DARK_FOREST)
					.add(Biomes.FOREST)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.PLAINS)
					.add(Biomes.FLOWER_FOREST)
					.add(Biomes.SUNFLOWER_PLAINS)
					.addOptional(RegisterWorldgen.FLOWER_FIELD)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_BROWN_SHELF_FUNGUS)
					.add(Biomes.DARK_FOREST)
					.add(Biomes.FOREST)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.PLAINS)
					.add(Biomes.FLOWER_FOREST)
					.add(Biomes.SUNFLOWER_PLAINS)
					.add(Biomes.SWAMP)
					.add(Biomes.MANGROVE_SWAMP)
					.add(Biomes.TAIGA)
					.add(Biomes.SNOWY_TAIGA)
					.addOptional(RegisterWorldgen.FLOWER_FIELD)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.PARCHED_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_GLORY_OF_THE_SNOW)
					.add(Biomes.BIRCH_FOREST)
					.add(Biomes.OLD_GROWTH_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_FLOWERING_WATER_LILY)
					.add(Biomes.SWAMP)
					.add(Biomes.MANGROVE_SWAMP);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_BERRY_PATCH)
					.add(Biomes.FLOWER_FOREST)
					.addOptional(RegisterWorldgen.FLOWER_FIELD)
					.addOptional(RegisterWorldgen.MIXED_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_LARGE_FERN_AND_GRASS)
					.add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
					.add(Biomes.OLD_GROWTH_PINE_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_LARGE_FERN_AND_GRASS_RARE)
					.add(Biomes.TAIGA)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_NEW_RARE_GRASS)
					.add(Biomes.WINDSWEPT_FOREST)
					.add(Biomes.WINDSWEPT_HILLS);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_DECORATIVE_MUD)
					.add(Biomes.SWAMP);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_PACKED_MUD_ORE)
					.add(Biomes.WINDSWEPT_SAVANNA)
					.add(Biomes.DESERT)
					.addOptional(RegisterWorldgen.OASIS)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(RegisterWorldgen.ARID_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_COARSE_DIRT_PATH)
					.add(Biomes.TAIGA)
					.add(Biomes.OLD_GROWTH_PINE_TAIGA)
					.add(Biomes.OLD_GROWTH_SPRUCE_TAIGA)
					.add(Biomes.WINDSWEPT_FOREST)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_COARSE_DIRT_PATH_SMALL)
					.add(Biomes.BADLANDS)
					.add(Biomes.WOODED_BADLANDS)
					.add(Biomes.ERODED_BADLANDS);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_PACKED_MUD_PATH_BADLANDS)
					.add(Biomes.BADLANDS)
					.add(Biomes.WOODED_BADLANDS)
					.add(Biomes.ERODED_BADLANDS);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SANDSTONE_PATH)
					.add(Biomes.DESERT)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(RegisterWorldgen.ARID_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SCORCHED_SAND)
					.add(Biomes.DESERT)
					.addOptional(RegisterWorldgen.OASIS)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(RegisterWorldgen.ARID_FOREST);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_SCORCHED_RED_SAND)
					.add(Biomes.BADLANDS)
					.add(Biomes.ERODED_BADLANDS)
					.add(Biomes.WOODED_BADLANDS);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_TERMITE_MOUND)
					.addOptionalTag(BiomeTags.IS_SAVANNA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_TAIGA_FOREST_ROCK)
					.add(Biomes.TAIGA)
					.add(Biomes.SNOWY_TAIGA)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_MOSS_PATH)
					.add(Biomes.JUNGLE)
					.add(Biomes.SPARSE_JUNGLE)
					.add(Biomes.BAMBOO_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.OASIS);

			this.getOrCreateTagBuilder(WilderBiomeTags.HAS_PACKED_MUD_PATH)
					.add(Biomes.SAVANNA)
					.add(Biomes.SAVANNA_PLATEAU)
					.add(Biomes.SAVANNA_PLATEAU)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(RegisterWorldgen.PARCHED_FOREST);
		}

		private void generateStructureTags() {
			this.getOrCreateTagBuilder(WilderBiomeTags.ABANDONED_CABIN_HAS_STRUCTURE)
					.addOptionalTag(BiomeTags.IS_OCEAN)
					.addOptionalTag(BiomeTags.IS_RIVER)
					.addOptionalTag(BiomeTags.IS_MOUNTAIN)
					.addOptionalTag(BiomeTags.IS_HILL)
					.addOptionalTag(BiomeTags.IS_TAIGA)
					.addOptionalTag(BiomeTags.IS_JUNGLE)
					.addOptionalTag(BiomeTags.IS_FOREST)
					.add(Biomes.STONY_SHORE)
					.add(Biomes.MUSHROOM_FIELDS)
					.add(Biomes.ICE_SPIKES)
					.add(Biomes.WINDSWEPT_SAVANNA)
					.add(Biomes.DESERT)
					.add(Biomes.SAVANNA)
					.add(Biomes.SNOWY_PLAINS)
					.add(Biomes.PLAINS)
					.add(Biomes.SUNFLOWER_PLAINS)
					.add(Biomes.SWAMP)
					.add(Biomes.MANGROVE_SWAMP)
					.add(Biomes.SAVANNA_PLATEAU)
					.add(Biomes.DRIPSTONE_CAVES)
					.add(Biomes.DEEP_DARK)
					.addOptionalTag(WilderBiomeTags.WILDER_WILD_BIOMES);

			this.getOrCreateTagBuilder(BiomeTags.HAS_DESERT_PYRAMID)
					.addOptional(RegisterWorldgen.OASIS);

			this.getOrCreateTagBuilder(BiomeTags.HAS_IGLOO)
					.addOptional(RegisterWorldgen.SNOWY_OLD_GROWTH_PINE_TAIGA);

			this.getOrCreateTagBuilder(BiomeTags.HAS_MINESHAFT)
					.addOptionalTag(WilderBiomeTags.WILDER_WILD_BIOMES);

			this.getOrCreateTagBuilder(BiomeTags.HAS_PILLAGER_OUTPOST)
					.addOptional(RegisterWorldgen.OASIS)
					.addOptional(RegisterWorldgen.FLOWER_FIELD)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.ARID_FOREST)
					.addOptional(RegisterWorldgen.PARCHED_FOREST);

			this.getOrCreateTagBuilder(BiomeTags.HAS_RUINED_PORTAL_DESERT)
					.addOptional(RegisterWorldgen.OASIS)
					.addOptional(RegisterWorldgen.ARID_SAVANNA)
					.addOptional(RegisterWorldgen.ARID_FOREST);

			this.getOrCreateTagBuilder(BiomeTags.HAS_RUINED_PORTAL_STANDARD)
					.addOptional(RegisterWorldgen.FLOWER_FIELD)
					.addOptional(RegisterWorldgen.BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.OLD_GROWTH_BIRCH_TAIGA)
					.addOptional(RegisterWorldgen.DARK_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST)
					.addOptional(RegisterWorldgen.MIXED_FOREST)
					.addOptional(RegisterWorldgen.PARCHED_FOREST)
					.addOptional(RegisterWorldgen.SEMI_BIRCH_FOREST)
					.addOptional(RegisterWorldgen.RAINFOREST)
					.addOptional(RegisterWorldgen.TEMPERATE_RAINFOREST)
					.addOptional(RegisterWorldgen.DARK_TAIGA);

			this.getOrCreateTagBuilder(BiomeTags.HAS_RUINED_PORTAL_JUNGLE)
					.addOptional(RegisterWorldgen.BIRCH_JUNGLE)
					.addOptional(RegisterWorldgen.SPARSE_BIRCH_JUNGLE);

			this.getOrCreateTagBuilder(BiomeTags.HAS_RUINED_PORTAL_OCEAN)
					.addOptional(RegisterWorldgen.JELLYFISH_CAVES);

			this.getOrCreateTagBuilder(BiomeTags.HAS_RUINED_PORTAL_SWAMP)
					.addOptional(RegisterWorldgen.CYPRESS_WETLANDS);

			this.getOrCreateTagBuilder(BiomeTags.HAS_VILLAGE_PLAINS)
					.addOptional(RegisterWorldgen.FLOWER_FIELD);

			this.getOrCreateTagBuilder(BiomeTags.HAS_VILLAGE_SAVANNA)
					.addOptional(RegisterWorldgen.ARID_SAVANNA);

			this.getOrCreateTagBuilder(BiomeTags.HAS_WOODLAND_MANSION)
					.addOptional(RegisterWorldgen.OLD_GROWTH_DARK_FOREST);
		}
	}

	private static final class WilderBlockLootProvider extends FabricBlockLootTableProvider {

		private WilderBlockLootProvider(FabricDataOutput dataOutput) {
			super(dataOutput);
		}

		@Override
		public void generate() {
			this.add(RegisterBlocks.BAOBAB_HANGING_SIGN, noDrop());
			this.add(RegisterBlocks.CYPRESS_HANGING_SIGN, noDrop());
		}

		@Override
		public void accept(BiConsumer<ResourceLocation, LootTable.Builder> resourceLocationBuilderBiConsumer) {

		}
	}

	private static final class WilderBlockTagProvider extends FabricTagProvider.BlockTagProvider {
		public WilderBlockTagProvider(FabricDataOutput output, CompletableFuture completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider arg) {
			this.generateDeepDark();
			this.generateHollowedAndTermites();
			this.generateCoconutSplitters();
			this.getOrCreateTagBuilder(BlockTags.SAND)
					.add(RegisterBlocks.SCORCHED_SAND)
					.add(RegisterBlocks.SCORCHED_RED_SAND);

		}

		private void generateCoconutSplitters() {
			this.getOrCreateTagBuilder(WilderBlockTags.SPLITS_COCONUTS)
					.addOptionalTag(BlockTags.MINEABLE_WITH_PICKAXE)
					.addOptionalTag(BlockTags.BASE_STONE_OVERWORLD)
					.addOptionalTag(BlockTags.BASE_STONE_NETHER)
					.addOptionalTag(BlockTags.DRAGON_IMMUNE)
					.addOptionalTag(BlockTags.WITHER_IMMUNE)
					.addOptionalTag(BlockTags.LOGS);
		}

		private void generateDeepDark() {
			this.getOrCreateTagBuilder(WilderBlockTags.ANCIENT_CITY_BLOCKS)
					.add(Blocks.COBBLED_DEEPSLATE)
					.add(Blocks.COBBLED_DEEPSLATE_STAIRS)
					.add(Blocks.COBBLED_DEEPSLATE_SLAB)
					.add(Blocks.COBBLED_DEEPSLATE_WALL)
					.add(Blocks.POLISHED_DEEPSLATE)
					.add(Blocks.POLISHED_DEEPSLATE_STAIRS)
					.add(Blocks.POLISHED_DEEPSLATE_SLAB)
					.add(Blocks.POLISHED_DEEPSLATE_WALL)
					.add(Blocks.DEEPSLATE_BRICKS)
					.add(Blocks.DEEPSLATE_BRICK_STAIRS)
					.add(Blocks.DEEPSLATE_BRICK_SLAB)
					.add(Blocks.DEEPSLATE_BRICK_WALL)
					.add(Blocks.CRACKED_DEEPSLATE_BRICKS)
					.add(Blocks.DEEPSLATE_TILES)
					.add(Blocks.DEEPSLATE_TILE_STAIRS)
					.add(Blocks.CHISELED_DEEPSLATE)
					.add(Blocks.REINFORCED_DEEPSLATE)
					.add(Blocks.POLISHED_BASALT)
					.add(Blocks.SMOOTH_BASALT)
					.add(Blocks.DARK_OAK_LOG)
					.add(Blocks.DARK_OAK_PLANKS)
					.add(Blocks.DARK_OAK_FENCE)
					.add(Blocks.LIGHT_BLUE_CARPET)
					.add(Blocks.BLUE_CARPET)
					.add(Blocks.LIGHT_BLUE_WOOL)
					.add(Blocks.GRAY_WOOL)
					.add(Blocks.CHEST)
					.add(Blocks.LADDER)
					.add(Blocks.CANDLE)
					.add(Blocks.WHITE_CANDLE)
					.add(Blocks.SOUL_LANTERN)
					.add(Blocks.SOUL_FIRE)
					.add(Blocks.SOUL_SAND);

			this.getOrCreateTagBuilder(WilderBlockTags.ANCIENT_HORN_NON_COLLIDE)
					.add(Blocks.SCULK)
					.add(RegisterBlocks.OSSEOUS_SCULK)
					.addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS)
					.addOptionalTag(ConventionalBlockTags.GLASS_PANES)
					.addOptionalTag(BlockTags.LEAVES)
					.add(Blocks.BELL)
					.add(Blocks.POINTED_DRIPSTONE)
					.add(Blocks.BAMBOO)
					.add(Blocks.ICE)
					.add(RegisterBlocks.SCULK_STAIRS)
					.add(RegisterBlocks.SCULK_SLAB)
					.add(RegisterBlocks.SCULK_WALL);

			this.getOrCreateTagBuilder(WilderBlockTags.SCULK_SLAB_REPLACEABLE)
					.add(Blocks.STONE_SLAB)
					.add(Blocks.GRANITE_SLAB)
					.add(Blocks.DIORITE_SLAB)
					.add(Blocks.ANDESITE_SLAB)
					.add(Blocks.BLACKSTONE_SLAB);

			this.getOrCreateTagBuilder(WilderBlockTags.SCULK_SLAB_REPLACEABLE_WORLDGEN)
					.add(Blocks.COBBLED_DEEPSLATE_SLAB)
					.add(Blocks.POLISHED_DEEPSLATE_SLAB)
					.add(Blocks.DEEPSLATE_BRICK_SLAB)
					.add(Blocks.DEEPSLATE_TILE_SLAB)
					.addOptionalTag(WilderBlockTags.SCULK_SLAB_REPLACEABLE);

			this.getOrCreateTagBuilder(WilderBlockTags.SCULK_STAIR_REPLACEABLE)
					.add(Blocks.STONE_STAIRS)
					.add(Blocks.GRANITE_STAIRS)
					.add(Blocks.DIORITE_STAIRS)
					.add(Blocks.ANDESITE_STAIRS)
					.add(Blocks.BLACKSTONE_STAIRS);

			this.getOrCreateTagBuilder(WilderBlockTags.SCULK_STAIR_REPLACEABLE_WORLDGEN)
					.add(Blocks.COBBLED_DEEPSLATE_STAIRS)
					.add(Blocks.POLISHED_DEEPSLATE_STAIRS)
					.add(Blocks.DEEPSLATE_BRICK_STAIRS)
					.add(Blocks.DEEPSLATE_TILE_STAIRS)
					.addOptionalTag(WilderBlockTags.SCULK_STAIR_REPLACEABLE);

			this.getOrCreateTagBuilder(WilderBlockTags.SCULK_VEIN_REMOVE)
					.add(Blocks.SCULK)
					.add(RegisterBlocks.SCULK_WALL)
					.add(RegisterBlocks.SCULK_SLAB)
					.add(RegisterBlocks.SCULK_STAIRS);

			this.getOrCreateTagBuilder(WilderBlockTags.SCULK_WALL_REPLACEABLE)
					.add(Blocks.COBBLESTONE_WALL)
					.add(Blocks.GRANITE_WALL)
					.add(Blocks.DIORITE_WALL)
					.add(Blocks.ANDESITE_WALL)
					.add(Blocks.BLACKSTONE_WALL);

			this.getOrCreateTagBuilder(WilderBlockTags.SCULK_WALL_REPLACEABLE_WORLDGEN)
					.add(Blocks.COBBLED_DEEPSLATE_WALL)
					.add(Blocks.POLISHED_DEEPSLATE_WALL)
					.add(Blocks.DEEPSLATE_BRICK_WALL)
					.add(Blocks.DEEPSLATE_TILE_WALL)
					.addOptionalTag(WilderBlockTags.SCULK_WALL_REPLACEABLE);
		}

		private void generateHollowedAndTermites() {
			this.getOrCreateTagBuilder(WilderBlockTags.BLOCKS_TERMITE)
					.addOptionalTag(ConventionalBlockTags.GLASS_BLOCKS)
					.addOptionalTag(ConventionalBlockTags.GLASS_PANES)
					.add(RegisterBlocks.ECHO_GLASS);

			this.getOrCreateTagBuilder(ConventionalBlockTags.GLASS_BLOCKS)
					.add(RegisterBlocks.ECHO_GLASS);

			this.getOrCreateTagBuilder(WilderBlockTags.BUSH_MAY_PLACE_ON)
					.addOptionalTag(BlockTags.SAND)
					.addOptionalTag(BlockTags.DIRT)
					.addOptionalTag(BlockTags.DEAD_BUSH_MAY_PLACE_ON);

			this.getOrCreateTagBuilder(WilderBlockTags.KILLS_TERMITE)
					.add(Blocks.WATER)
					.add(Blocks.LAVA)
					.add(Blocks.POWDER_SNOW)
					.add(Blocks.WATER_CAULDRON)
					.add(Blocks.LAVA_CAULDRON)
					.add(Blocks.POWDER_SNOW_CAULDRON)
					.add(Blocks.CRIMSON_ROOTS)
					.add(Blocks.CRIMSON_PLANKS)
					.add(Blocks.WARPED_PLANKS)
					.add(Blocks.WEEPING_VINES)
					.add(Blocks.WEEPING_VINES_PLANT)
					.add(Blocks.TWISTING_VINES)
					.add(Blocks.TWISTING_VINES_PLANT)
					.add(Blocks.CRIMSON_SLAB)
					.add(Blocks.WARPED_SLAB)
					.add(Blocks.CRIMSON_PRESSURE_PLATE)
					.add(Blocks.WARPED_PRESSURE_PLATE)
					.add(Blocks.CRIMSON_FENCE)
					.add(Blocks.WARPED_FENCE)
					.add(Blocks.CRIMSON_TRAPDOOR)
					.add(Blocks.WARPED_TRAPDOOR)
					.add(Blocks.CRIMSON_FENCE_GATE)
					.add(Blocks.WARPED_FENCE_GATE)
					.add(Blocks.CRIMSON_STAIRS)
					.add(Blocks.WARPED_STAIRS)
					.add(Blocks.CRIMSON_BUTTON)
					.add(Blocks.WARPED_BUTTON)
					.add(Blocks.CRIMSON_DOOR)
					.add(Blocks.WARPED_DOOR)
					.add(Blocks.CRIMSON_SIGN)
					.add(Blocks.WARPED_SIGN)
					.add(Blocks.CRIMSON_WALL_SIGN)
					.add(Blocks.WARPED_WALL_SIGN)
					.add(Blocks.CRIMSON_STEM)
					.add(Blocks.WARPED_STEM)
					.add(Blocks.STRIPPED_WARPED_STEM)
					.add(Blocks.STRIPPED_WARPED_HYPHAE)
					.add(Blocks.WARPED_NYLIUM)
					.add(Blocks.WARPED_FUNGUS)
					.add(Blocks.STRIPPED_CRIMSON_STEM)
					.add(Blocks.STRIPPED_CRIMSON_HYPHAE)
					.add(Blocks.CRIMSON_NYLIUM)
					.add(Blocks.CRIMSON_FUNGUS)
					.add(Blocks.REDSTONE_WIRE)
					.add(Blocks.REDSTONE_BLOCK)
					.add(Blocks.REDSTONE_TORCH)
					.add(Blocks.REDSTONE_WALL_TORCH)
					.addOptional(ResourceKey.create(
							Registries.BLOCK,
							WilderSharedConstants.id("hollowed_crimson_stem")
					))
					.addOptional(ResourceKey.create(
							Registries.BLOCK,
							WilderSharedConstants.id("hollowed_warped_stem")
					))
					.addOptional(ResourceKey.create(
							Registries.BLOCK,
							WilderSharedConstants.id("stripped_hollowed_crimson_stem")
					))
					.addOptional(ResourceKey.create(
							Registries.BLOCK,
							WilderSharedConstants.id("stripped_hollowed_warped_stem")
					))
					.addOptionalTag(BlockTags.WARPED_STEMS)
					.addOptionalTag(BlockTags.CRIMSON_STEMS);

			this.getOrCreateTagBuilder(WilderBlockTags.TERMITE_BREAKABLE)
					.addOptionalTag(BlockTags.LEAVES)
					.addOptionalTag(WilderBlockTags.STRIPPED_HOLLOWED_LOGS)
					.add(Blocks.BAMBOO)
					.add(Blocks.DEAD_BUSH)
					.add(Blocks.STRIPPED_ACACIA_WOOD)
					.add(Blocks.STRIPPED_BIRCH_WOOD)
					.add(Blocks.STRIPPED_DARK_OAK_WOOD)
					.add(Blocks.STRIPPED_JUNGLE_WOOD)
					.add(Blocks.STRIPPED_MANGROVE_WOOD)
					.add(Blocks.STRIPPED_OAK_WOOD)
					.add(Blocks.STRIPPED_SPRUCE_WOOD)
					.add(Blocks.STRIPPED_ACACIA_WOOD)
					.addOptional(
							ResourceKey.create(
									Registries.BLOCK,
									WilderSharedConstants.id("stripped_baobab_wood")
							)
					)
					.addOptional(
							ResourceKey.create(
									Registries.BLOCK,
									WilderSharedConstants.id("stripped_cypress_wood")
							)
					)
					.addOptional(
							ResourceKey.create(
									Registries.BLOCK,
									WilderSharedConstants.id("stripped_palm_wood")
							)
					)
					.addOptional(
							ResourceKey.create(
									Registries.BLOCK,
									new ResourceLocation("immersive_weathering", "leaf_piles")
							)
					);

			this.getOrCreateTagBuilder(WilderBlockTags.SAND_POOL_REPLACEABLE)
					.add(Blocks.SAND);
		}
	}

	private static final class WilderItemTagProvider extends FabricTagProvider.ItemTagProvider {

		public WilderItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider arg) {
			this.getOrCreateTagBuilder(FrozenItemTags.ALWAYS_SAVE_COOLDOWNS)
					.add(RegisterItems.ANCIENT_HORN);
		}
	}

	private static final class WilderEntityTagProvider extends FabricTagProvider.EntityTypeTagProvider {

		public WilderEntityTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
			super(output, completableFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider arg) {
			this.getOrCreateTagBuilder(WilderEntityTags.STAYS_IN_MESOGLEA)
					.add(RegisterEntities.JELLYFISH);
		}
	}

	private static class ExperimentBlockLootTableProvider extends FabricBlockLootTableProvider {
		protected ExperimentBlockLootTableProvider(FabricDataOutput dataOutput) {
			super(dataOutput);
		}

		@Override
		public void generate() {
			this.dropSelf(RegisterBlocks.BAOBAB_HANGING_SIGN);
			this.dropSelf(RegisterBlocks.CYPRESS_HANGING_SIGN);
		}

		@Override
		public void accept(BiConsumer<ResourceLocation, LootTable.Builder> resourceLocationBuilderBiConsumer) {

		}
	}

	private static class ExperimentBlockTagProvider extends FabricTagProvider.BlockTagProvider {

		public ExperimentBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected void addTags(HolderLookup.Provider arg) {
			this.tag(BlockTags.CEILING_HANGING_SIGNS)
					.add(key(RegisterBlocks.BAOBAB_HANGING_SIGN))
					.add(key(RegisterBlocks.CYPRESS_HANGING_SIGN));

			this.tag(BlockTags.WALL_HANGING_SIGNS)
					.add(key(RegisterBlocks.BAOBAB_WALL_HANGING_SIGN))
					.add(key(RegisterBlocks.CYPRESS_WALL_HANGING_SIGN));
		}

		private static ResourceKey<Block> key(Block block) {
			return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
		}
	}

	private static class ExperimentRecipeProvider extends RecipeProvider {

		public ExperimentRecipeProvider(PackOutput packOutput) {
			super(packOutput);
		}

		@Override
		public void buildRecipes(final @NotNull Consumer<FinishedRecipe> consumer) {
			generateForEnabledBlockFamilies(consumer, FeatureFlagSet.of(WilderFeatureFlags.UPDATE_1_20_ADDITIONS));
			hangingSign(consumer, RegisterItems.BAOBAB_HANGING_SIGN, RegisterBlocks.STRIPPED_BAOBAB_LOG);
			hangingSign(consumer, RegisterItems.CYPRESS_HANGING_SIGN, RegisterBlocks.STRIPPED_CYPRESS_LOG);
		}
	}
}
