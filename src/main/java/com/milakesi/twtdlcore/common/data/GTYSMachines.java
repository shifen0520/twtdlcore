package com.milakesi.twtdlcore.common.data;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;

public class GTYSMachines {
    /*public static final MachineDefinition BEDROCK_EXTRACTOR = GTCEu.REGISTRATE
            .machine("my_bedrock_extractor", holder -> new SimpleTieredMachine(holder, GTValues.ULV, (tier -> 3200)))
            .rotationState(RotationState.ALL)
            .recipeType(GTYSRecipeTypes.BEDROCK_SOURCE_EXTRACTOR)
            .workableTieredTier(GTValues.ULV)
            .langValue("岩本源萃取机")
            .register();*/
    public static MachineDefinition[] BEDROCK_EXTRACTOR;
    static {
        BEDROCK_EXTRACTOR = new MachineDefinition[]{GTMachineUtils.registerSimpleMachines("bedrock_extractor", GTYSRecipeTypes.BEDROCK_SOURCE_EXTRACTOR, (tier) -> 8000, false)[0]};
    }
    public static void registerMachines() {
        System.out.println(">>> GTYS Machines Registered Successfully!");
    }
}
