package com.milakesi.twtdlcore.api.machine.elemental;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.ICleanroomProvider;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.feature.IMufflableMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.IRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.*;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ElementalWorkableMachine extends ElementalMachine implements IRecipeLogicMachine, IMufflableMachine, IMachineLife {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER;

    private @Nullable ICleanroomProvider cleanroom;

    @Persisted
    @DescSynced
    public final RecipeLogic recipeLogic;

    public final GTRecipeType[] recipeTypes = this.getDefinition().getRecipeTypes();
    public int activeRecipeType = 0;

    @Persisted
    @DescSynced
    @RequireRerender
    protected Direction outputFacing;

    @Persisted
    @DescSynced
    protected boolean isMuffled;

    protected boolean previouslyMuffled = true;
    protected final Map<IO, List<RecipeHandlerList>> capabilitiesProxy;
    protected final Map<IO, Map<RecipeCapability<?>, List<IRecipeHandler<?>>>> capabilitiesFlat;
    protected final List<ISubscription> traitSubscriptions;

    public ElementalWorkableMachine(IMachineBlockEntity holder, boolean isHighPressure, Object... args) {
        super(holder, isHighPressure, args);
        this.recipeLogic = this.createRecipeLogic(args);
        this.capabilitiesProxy = new EnumMap<>(IO.class);
        this.capabilitiesFlat = new EnumMap<>(IO.class);
        this.traitSubscriptions = new ArrayList<>();
        this.outputFacing = this.hasFrontFacing() ? this.getFrontFacing().getOpposite() : Direction.UP;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        // 收集所有 IRecipeHandlerTrait
        Map<IO, List<IRecipeHandler<?>>> ioTraits = new Object2ObjectOpenHashMap<>();
        for (MachineTrait trait : this.getTraits()) {
            if (trait instanceof IRecipeHandlerTrait<?> handlerTrait) {
                ioTraits.computeIfAbsent(handlerTrait.getHandlerIO(), k -> new ArrayList<>()).add(handlerTrait);
            }
        }

        // 注册 Elemental 能量处理器
        IRecipeHandler<?> energyHandler = new ElementalEnergyRecipeHandler(this.elementalTank, this.getConversionRate());

        for (Map.Entry<IO, List<IRecipeHandler<?>>> entry : ioTraits.entrySet()) {
            List<IRecipeHandler<?>> handlers = new ArrayList<>(entry.getValue());
            handlers.add(energyHandler);
            RecipeHandlerList handlersList = RecipeHandlerList.of(entry.getKey(), handlers);
            this.addHandlerList(handlersList);
            this.traitSubscriptions.add(handlersList.subscribe(this.recipeLogic::updateTickSubscription));
        }
    }

    protected RecipeLogic createRecipeLogic(Object... args) {
        return new RecipeLogic(this);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        this.traitSubscriptions.forEach(ISubscription::unsubscribe);
        this.traitSubscriptions.clear();
        this.capabilitiesProxy.clear();
        this.capabilitiesFlat.clear();
        this.recipeLogic.inValid();
    }

    public boolean hasOutputFacing() {
        return true;
    }

    public void setOutputFacing(@Nullable Direction outputFacing) {
        if (this.hasFrontFacing() && this.outputFacing != outputFacing) {
            this.outputFacing = outputFacing;
        }
    }

    @Override
    public boolean isFacingValid(Direction facing) {
        if (facing == this.getOutputFacing()) {
            return false;
        }
        return super.isFacingValid(facing);
    }

    @Override
    protected InteractionResult onWrenchClick(Player player, InteractionHand hand, Direction gridSide, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown()) {
            if (this.hasFrontFacing() || gridSide == this.getFrontFacing()) {
                return InteractionResult.PASS;
            } else {
                this.setOutputFacing(gridSide);
                return InteractionResult.sidedSuccess(player.level().isClientSide);
            }
        } else {
            return super.onWrenchClick(player, hand, gridSide, hitResult);
        }
    }

    public boolean keepSubscribing() {
        return false;
    }

    @Override
    public @Nullable GTRecipeType getRecipeType() {
        return this.recipeTypes[this.activeRecipeType];
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.previouslyMuffled != this.isMuffled) {
            this.previouslyMuffled = this.isMuffled;
            if (this.recipeLogic != null) {
                this.recipeLogic.updateSound();
            }
        }
    }

    @Override
    public @Nullable ResourceTexture sideTips(Player player, BlockPos pos, BlockState state, Set<GTToolType> toolTypes, Direction side) {
        if (!toolTypes.contains(GTToolType.WRENCH) || player.isShiftKeyDown() || this.hasOutputFacing() || side == this.getFrontFacing()) {
            return super.sideTips(player, pos, state, toolTypes, side);
        }
        return GuiTextures.TOOL_IO_FACING_ROTATION;
    }

    @Nullable
    public ICleanroomProvider getCleanroom() {
        return this.cleanroom;
    }

    public void setCleanroom(@Nullable ICleanroomProvider cleanroom) {
        this.cleanroom = cleanroom;
    }



    public RecipeLogic getRecipeLogic() {
        return this.recipeLogic;
    }

    public GTRecipeType[] getRecipeTypes() {
        return this.recipeTypes;
    }

    public int getActiveRecipeType() {
        return this.activeRecipeType;
    }

    public void setActiveRecipeType(int activeRecipeType) {
        this.activeRecipeType = activeRecipeType;
    }

    public boolean isMuffled() {
        return this.isMuffled;
    }

    public void setMuffled(boolean isMuffled) {
        this.isMuffled = isMuffled;
    }

    static {
        MANAGED_FIELD_HOLDER = new ManagedFieldHolder(ElementalWorkableMachine.class, ElementalMachine.MANAGED_FIELD_HOLDER);
    }
}