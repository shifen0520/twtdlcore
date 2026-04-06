package com.milakesi.twtdlcore.api.machine.elemental;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import javax.annotation.ParametersAreNonnullByDefault;

import com.milakesi.twtdlcore.common.data.GTYSMaterials;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ElementalMachine extends MetaMachine implements ITieredMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER;
    public static final BooleanProperty ELEMENTAL_PROPERTY;
    public final boolean isHighPressure;
    @Persisted
    public final NotifiableFluidTank steamTank;

    public ElementalMachine(IMachineBlockEntity holder, boolean isHighPressure, Object... args) {
        super(holder);
        this.isHighPressure = isHighPressure;
        this.steamTank = this.createSteamTank(args);
        this.steamTank.setFilter((fluidStack) -> {
            return fluidStack.getFluid().is(GTYSMaterials.Elemental.getFluidTag());
        });
    }

    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int getTier() {
        return this.isHighPressure ? 1 : 0;
    }

    protected abstract NotifiableFluidTank createSteamTank(Object... var1);

    @Generated
    public boolean isHighPressure() {
        return this.isHighPressure;
    }

    static {
        MANAGED_FIELD_HOLDER = new ManagedFieldHolder(ElementalMachine.class, MetaMachine.MANAGED_FIELD_HOLDER);
        ELEMENTAL_PROPERTY = GTMachineModelProperties.IS_STEEL_MACHINE;
    }
}
