package com.milakesi.twtdlcore.common.data.material;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;

public class MaterialA {
  private Material material;

  public MaterialA(Material material) {
    this.material = material;
  }

  public Material getMaterial() {
    return material;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

  public static Material Elemental;

  public static Material Elemental() {
    if (Elemental == null) {}
    return Elemental;
  }
}
