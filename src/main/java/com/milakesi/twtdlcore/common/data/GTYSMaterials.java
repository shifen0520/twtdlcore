package com.milakesi.twtdlcore.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.MarkerMaterial;
import com.gregtechceu.gtceu.api.data.chemical.material.MarkerMaterials;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.materials.ElementMaterials;
import com.gregtechceu.gtceu.common.data.materials.FirstDegreeMaterials;
import com.gregtechceu.gtceu.common.data.materials.GCYMMaterials;
import com.gregtechceu.gtceu.common.data.materials.HigherDegreeMaterials;
import com.gregtechceu.gtceu.common.data.materials.MaterialFlagAddition;
import com.gregtechceu.gtceu.common.data.materials.OrganicChemistryMaterials;
import com.gregtechceu.gtceu.common.data.materials.SecondDegreeMaterials;
import com.gregtechceu.gtceu.common.data.materials.UnknownCompositionMaterials;
import com.gregtechceu.gtceu.utils.memoization.GTMemoizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class GTYSMaterials {

    public static Material Elemental;

    public static void init() {
        Elemental = new Material.Builder(GTCEu.id("elemental"))
                .liquid()
                .color(0x00FFAA)
                .iconSet(MaterialIconSet.SHINY)
                .buildAndRegister();
    }
}
