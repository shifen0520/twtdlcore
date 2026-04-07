package com.milakesi.twtdlcore.api.machine.elemental;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.gui.widget.PredicatedImageWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IUIMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.Position;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SimpleElementalMachine extends ElementalWorkableMachine implements IUIMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER;

    @Persisted
    public final NotifiableItemStackHandler importItems;

    @Persisted
    public final NotifiableItemStackHandler exportItems;

    public SimpleElementalMachine(IMachineBlockEntity holder, boolean isHighPressure, Object... args) {
        super(holder, isHighPressure, args);
        this.importItems = this.createImportItemHandler(args);
        this.exportItems = this.createExportItemHandler(args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected NotifiableFluidTank createElementalTank(Object... args) {
        return new NotifiableFluidTank(this, 1, 32000, IO.IN);
    }

    protected NotifiableItemStackHandler createImportItemHandler(Object... args) {
        return new NotifiableItemStackHandler(this,
                this.getRecipeType().getMaxInputs(ItemRecipeCapability.CAP), IO.IN);
    }

    protected NotifiableItemStackHandler createExportItemHandler(Object... args) {
        return new NotifiableItemStackHandler(this,
                this.getRecipeType().getMaxOutputs(ItemRecipeCapability.CAP), IO.OUT);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.addHandlerList(RecipeHandlerList.of(IO.IN,
                new IRecipeHandler[]{new ElementalEnergyRecipeHandler(this.elementalTank, this.getConversionRate())}));
    }

    @Override
    public void onMachineRemoved() {
        this.clearInventory(this.importItems.storage);
        this.clearInventory(this.exportItems.storage);
    }

    @Override
    public double getConversionRate() {
        return this.isHighPressure() ? 2.0 : 1.0;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        Table<IO, RecipeCapability<?>, Object> storages = Tables.newCustomTable(new EnumMap<>(IO.class), LinkedHashMap::new);
        storages.put(IO.IN, ItemRecipeCapability.CAP, this.importItems.storage);
        storages.put(IO.OUT, ItemRecipeCapability.CAP, this.exportItems.storage);

        WidgetGroup group = this.getRecipeType().getRecipeUI().createUITemplate(
                this.recipeLogic::getProgressPercent, storages, new CompoundTag(),
                Collections.emptyList(), true, this.isHighPressure);

        Position pos = new Position((Math.max(group.getSize().width + 4 + 8, 176) - 4 - group.getSize().width) / 2 + 4, 32);
        group.setSelfPosition(pos);

        ModularUI ui = new ModularUI(176, 166, this, entityPlayer)
                .background(GuiTextures.BACKGROUND_STEAM.get(this.isHighPressure))
                .widget(group)
                .widget(new LabelWidget(5, 5, this.getBlockState().getBlock().getDescriptionId()));

        PredicatedImageWidget indicator = new PredicatedImageWidget(
                pos.x + group.getSize().width / 2 - 9,
                pos.y + group.getSize().height / 2 - 9,
                18, 18,
                GuiTextures.INDICATOR_NO_STEAM.get(this.isHighPressure))
                .setPredicate(this.recipeLogic::isWaiting);

        return ui.widget(indicator)
                .widget(UITemplate.bindPlayerInventory(entityPlayer.getInventory(),
                        GuiTextures.SLOT_STEAM.get(this.isHighPressure), 7, 84, true));
    }

    static {
        MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SimpleElementalMachine.class,
                ElementalWorkableMachine.MANAGED_FIELD_HOLDER);
    }

    public Map<IO, List<RecipeHandlerList>> getCapabilitiesProxy() {
        return this.capabilitiesProxy;
    }

    public Map<IO, Map<RecipeCapability<?>, List<IRecipeHandler<?>>>> getCapabilitiesFlat() {
        return this.capabilitiesFlat;
    }
}