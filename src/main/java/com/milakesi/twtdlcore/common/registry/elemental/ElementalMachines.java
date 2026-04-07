package com.milakesi.twtdlcore.common.registry.elemental;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.milakesi.twtdlcore.api.machine.elemental.SimpleElementalMachine;

import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

public class ElementalMachines {

    public static final MachineDefinition ELEMENTAL_COMPRESSOR = REGISTRATE
            .machine("elemental_compressor",
                    (holder, args) -> new SimpleElementalMachine(holder, false, args))
            .langValue("Elemental Compressor")
            .recipeType(GTRecipeTypes.COMPRESSOR_RECIPES)
            .tier(0)
            .register();

    public static void init() {}
}