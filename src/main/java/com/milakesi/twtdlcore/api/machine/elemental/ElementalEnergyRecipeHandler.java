package com.milakesi.twtdlcore.api.machine.elemental;

import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.utils.GTMath;

import com.milakesi.twtdlcore.common.data.GTYSMaterials;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class ElementalEnergyRecipeHandler implements IRecipeHandler<EnergyStack> {
  private final NotifiableFluidTank elementalTank;
  private final double conversionRate;

  public ElementalEnergyRecipeHandler(NotifiableFluidTank elementalTank, double conversionRate) {
    this.elementalTank = elementalTank;
    this.conversionRate = conversionRate;
  }

  public List<EnergyStack> handleRecipeInner(IO io, GTRecipe recipe, List<EnergyStack> left, boolean simulate) {
    ListIterator<EnergyStack> it = left.listIterator();

    while(true) {
      while(it.hasNext()) {
        EnergyStack stack = (EnergyStack)it.next();
        if (stack.isEmpty()) {
          it.remove();
        } else {
          long totalEU = stack.getTotalEU();
          int totalSteam = GTMath.saturatedCast((long)Math.ceil((double)totalEU * this.conversionRate));
          if (totalSteam > 0) {
            FluidIngredient elemental = io == IO.IN ? FluidIngredient.of(GTYSMaterials.Elemental.getFluidTag(), totalSteam) : FluidIngredient.of(GTYSMaterials.Elemental.getFluid(totalSteam));
            ArrayList<FluidIngredient> list = new ArrayList();
            list.add(elemental);
            List<FluidIngredient> leftElemental = this.elementalTank.handleRecipeInner(io, recipe, list, simulate);
            if (leftElemental != null && !leftElemental.isEmpty()) {
              totalEU = (long)((double)((FluidIngredient)leftElemental.get(0)).getAmount() / this.conversionRate);
              it.set(new EnergyStack(totalEU));
            } else {
              it.remove();
            }
          }
        }
      }

      return left.isEmpty() ? null : left;
    }
  }

  public @NotNull List<Object> getContents() {
    List<FluidStack> tankContents = new ArrayList();

    for(int i = 0; i < this.elementalTank.getTanks(); ++i) {
      FluidStack stack = this.elementalTank.getFluidInTank(i);
      if (!stack.isEmpty()) {
        tankContents.add(stack);
      }
    }

    long sum = tankContents.stream().mapToLong(FluidStack::getAmount).sum();
    long realSum = (long)Math.ceil((double)sum * this.conversionRate);
    return List.of(realSum);
  }

  public double getTotalContentAmount() {
    List<FluidStack> tankContents = new ArrayList();

    for(int i = 0; i < this.elementalTank.getTanks(); ++i) {
      FluidStack stack = this.elementalTank.getFluidInTank(i);
      if (!stack.isEmpty()) {
        tankContents.add(stack);
      }
    }

    long sum = tankContents.stream().mapToLong(FluidStack::getAmount).sum();
    return (double)((long)Math.ceil((double)sum * this.conversionRate));
  }

  public RecipeCapability<EnergyStack> getCapability() {
    return EURecipeCapability.CAP;
  }

  public long getCapacity() {
    return (long)this.elementalTank.getTankCapacity(0);
  }

  public long getStored() {
    FluidStack stack = this.elementalTank.getFluidInTank(0);
    return stack != FluidStack.EMPTY ? (long)stack.getAmount() : 0L;
  }
}
