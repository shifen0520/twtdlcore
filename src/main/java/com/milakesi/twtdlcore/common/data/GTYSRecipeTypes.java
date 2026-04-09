package com.milakesi.twtdlcore.common.data;

import static com.lowdragmc.lowdraglib.gui.texture.ProgressTexture.FillDirection.LEFT_TO_RIGHT;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;

public class GTYSRecipeTypes {

  public static final GTRecipeType BEDROCK_SOURCE_EXTRACTOR =
      register("bedrock_source", "岩本源萃取")
          .setEUIO(IO.IN)
          .setMaxIOSize(4, 9, 1, 1)
          .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, LEFT_TO_RIGHT)
          .setSound(GTSoundEntries.ARC);

  public static GTRecipeType register(String name, String group, RecipeType<?>... proxyRecipes) {
    GTRecipeType recipeType = new GTRecipeType(GTCEu.id(name), group, proxyRecipes);
    GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, recipeType.registryName, recipeType);
    GTRegistries.register(
        BuiltInRegistries.RECIPE_SERIALIZER, recipeType.registryName, new GTRecipeSerializer());
    GTRegistries.RECIPE_TYPES.register(recipeType.registryName, recipeType);
    return recipeType;
  }
}
