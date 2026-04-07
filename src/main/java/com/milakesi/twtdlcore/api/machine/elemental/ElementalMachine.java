package com.milakesi.twtdlcore.api.machine.elemental;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.ParametersAreNonnullByDefault;

import com.milakesi.twtdlcore.common.data.GTYSMaterials;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ElementalMachine extends MetaMachine implements ITieredMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER;
    public static final BooleanProperty ELEMENTAL_PROPERTY = BooleanProperty.create("elemental");

    public final boolean isHighPressure;

    @Persisted
    public final NotifiableFluidTank elementalTank;

    public ElementalMachine(IMachineBlockEntity holder, boolean isHighPressure, Object... args) {
        super(holder);
        this.isHighPressure = isHighPressure;
        this.elementalTank = this.createElementalTank(args);

        this.elementalTank.setFilter((fluidStack) -> {
            return fluidStack.getFluid() == GTYSMaterials.Elemental.getFluid();
        });
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public int getTier() {
        return this.isHighPressure ? 1 : 0;
    }

    protected abstract NotifiableFluidTank createElementalTank(Object... args);

    @Generated
    public boolean isHighPressure() {
        return this.isHighPressure;
    }

    // 获取转换率（子类必须实现）
    public abstract double getConversionRate();

    // 获取输出方向
    public net.minecraft.core.Direction getOutputFacing() {
        return getFrontFacing() != null ? getFrontFacing().getOpposite() : net.minecraft.core.Direction.UP;
    }

    static {
        MANAGED_FIELD_HOLDER = new ManagedFieldHolder(ElementalMachine.class, MetaMachine.MANAGED_FIELD_HOLDER);
    }
}
