package net.frozenblock.wilderwild.datafix.wilderwild.datafixers;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.frozenblock.wilderwild.WWConstants;
import net.frozenblock.wilderwild.entity.variant.FireflyColor;
import net.frozenblock.wilderwild.item.MobBottleItem;
import net.minecraft.util.datafix.fixes.ItemStackComponentizationFix;
import net.minecraft.util.datafix.fixes.References;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Optional;

public class FireflyBottleComponentizationFix extends DataFix {

	public FireflyBottleComponentizationFix(Schema outputSchema) {
		super(outputSchema, true);
	}

	private static final Map<String, String> FIREFLY_BOTTLE_TO_COMPONENT = ImmutableBiMap.<String, String>builder()
		.put(WWConstants.string("firefly_bottle"), FireflyColor.ON.getSerializedName())
		.put(WWConstants.string("black_firefly_bottle"), FireflyColor.BLACK.getSerializedName())
		.put(WWConstants.string("blue_firefly_bottle"), FireflyColor.BLUE.getSerializedName())
		.put(WWConstants.string("brown_firefly_bottle"), FireflyColor.BROWN.getSerializedName())
		.put(WWConstants.string("cyan_firefly_bottle"), FireflyColor.CYAN.getSerializedName())
		.put(WWConstants.string("gray_firefly_bottle"), FireflyColor.GRAY.getSerializedName())
		.put(WWConstants.string("green_firefly_bottle"), FireflyColor.GREEN.getSerializedName())
		.put(WWConstants.string("light_blue_firefly_bottle"), FireflyColor.LIGHT_BLUE.getSerializedName())
		.put(WWConstants.string("light_gray_firefly_bottle"), FireflyColor.LIGHT_GRAY.getSerializedName())
		.put(WWConstants.string("lime_firefly_bottle"), FireflyColor.LIME.getSerializedName())
		.put(WWConstants.string("magenta_firefly_bottle"), FireflyColor.MAGENTA.getSerializedName())
		.put(WWConstants.string("orange_firefly_bottle"), FireflyColor.ORANGE.getSerializedName())
		.put(WWConstants.string("pink_firefly_bottle"), FireflyColor.PINK.getSerializedName())
		.put(WWConstants.string("purple_firefly_bottle"), FireflyColor.PURPLE.getSerializedName())
		.put(WWConstants.string("red_firefly_bottle"), FireflyColor.RED.getSerializedName())
		.put(WWConstants.string("white_firefly_bottle"), FireflyColor.WHITE.getSerializedName())
		.put(WWConstants.string("yellow_firefly_bottle"), FireflyColor.YELLOW.getSerializedName())
		.build();

	private static void fixItemStack(ItemStackComponentizationFix.@NotNull ItemStackData itemStackData, Dynamic<?> dynamic) {
		if (FIREFLY_BOTTLE_TO_COMPONENT.containsKey(itemStackData.item)) {
			itemStackData.setComponent(
				WWConstants.string("bottle_entity_data"),
				dynamic.createMap(
					ImmutableMap.of(
						dynamic.createString(MobBottleItem.FIREFLY_BOTTLE_VARIANT_FIELD),
						dynamic.createString(FIREFLY_BOTTLE_TO_COMPONENT.get(itemStackData.item))
					)
				)
			);
		}
	}

	@Override
	protected TypeRewriteRule makeRule() {
		return this.writeFixAndRead(
			"Firefly Bottle items -> componentization",
			this.getInputSchema().getType(References.ITEM_STACK),
			this.getOutputSchema().getType(References.ITEM_STACK), dynamic -> {
				Optional<? extends Dynamic<?>> optional = ItemStackComponentizationFix.ItemStackData.read(dynamic).map(itemStackData -> {
					fixItemStack(itemStackData, itemStackData.tag);
					return itemStackData.write();
				});
				return DataFixUtils.orElse(optional, dynamic);
			}
		);
	}

}
