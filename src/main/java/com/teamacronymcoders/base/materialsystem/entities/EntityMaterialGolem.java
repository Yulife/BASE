package com.teamacronymcoders.base.materialsystem.entities;

import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.world.World;

public class EntityMaterialGolem extends EntityIronGolem {
    public EntityMaterialGolem(World world) {
        super(world);
    }

    public EntityMaterialGolem(World world, MaterialPart materialPart) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }
}
