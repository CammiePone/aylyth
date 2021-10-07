package moriyashiine.aylyth.common.recipe;

import com.google.gson.JsonObject;
import moriyashiine.aylyth.common.registry.ModItems;
import moriyashiine.aylyth.common.registry.ModRecipeTypes;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.Random;

public class ShuckingRecipe extends ShapelessRecipe {
	private static final Random RANDOM = new Random();
	
	public ShuckingRecipe(ShapelessRecipe recipe) {
		super(recipe.getId(), recipe.getGroup(), recipe.getOutput(), recipe.getIngredients());
	}
	
	@Override
	public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
		DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
		for (int i = 0; i < defaultedList.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.isOf(ModItems.YMPE_DAGGER)) {
				ItemStack dagger = stack.copy();
				dagger.damage(1, RANDOM, null);
				if (dagger.getDamage() >= dagger.getMaxDamage()) {
					dagger = ItemStack.EMPTY;
				}
				defaultedList.set(i, dagger);
			}
		}
		return defaultedList;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModRecipeTypes.SHUCKING_RECIPE_SERIALIZER;
	}
	
	public static class Serializer implements RecipeSerializer<ShuckingRecipe> {
		@Override
		public ShuckingRecipe read(Identifier id, JsonObject json) {
			return new ShuckingRecipe(SHAPELESS.read(id, json));
		}
		
		@Override
		public ShuckingRecipe read(Identifier id, PacketByteBuf buf) {
			return new ShuckingRecipe(SHAPELESS.read(id, buf));
		}
		
		@Override
		public void write(PacketByteBuf buf, ShuckingRecipe recipe) {
			SHAPELESS.write(buf, recipe);
		}
	}
}