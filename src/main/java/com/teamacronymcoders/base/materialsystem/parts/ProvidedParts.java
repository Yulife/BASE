package com.teamacronymcoders.base.materialsystem.parts;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.materialsystem.MaterialException;
import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.materialsystem.blocks.SubBlockOrePart;
import com.teamacronymcoders.base.materialsystem.blocks.SubBlockPart;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPart;
import com.teamacronymcoders.base.materialsystem.materialparts.MaterialPartData;
import com.teamacronymcoders.base.subblocksystem.SubBlockSystem;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static net.minecraftforge.fml.common.registry.ForgeRegistries.BLOCKS;

public class ProvidedParts {
    private IBaseMod mod;
    private MaterialSystem materialSystem;
    private SubBlockSystem subBlockSystem;

    private IBlockState stone = BLOCKS.getValue(new ResourceLocation("stone")).getDefaultState();

    public ProvidedParts(IBaseMod mod, MaterialSystem materialSystem, SubBlockSystem subBlockSystem) {
        this.mod = mod;
        this.materialSystem = materialSystem;
        this.subBlockSystem = subBlockSystem;
    }

    public void initPartsAndTypes() {
        PartType item = new PartType("Item", materialSystem::setupItem);
        PartType block = new PartType("Block", materialPart ->
                subBlockSystem.registerSubBlock(new SubBlockPart(materialPart)));
        PartType ore = new PartType("Ore", this::createOreSubBlocks);
        materialSystem.registerPartType(item);
        materialSystem.registerPartType(block);
        materialSystem.registerPartType(ore);

        registerPart(new PartBuilder(materialSystem).setName("Ingot").setPartType(item));
        registerPart(new PartBuilder(materialSystem).setName("Beam").setPartType(item));
        registerPart(new PartBuilder(materialSystem).setName("Gear").setPartType(item));
        registerPart(new PartBuilder(materialSystem).setName("Bolt").setPartType(item));
        registerPart(new PartBuilder(materialSystem).setName("Dust").setPartType(item));
        registerPart(new PartBuilder(materialSystem).setName("Plate").setPartType(item));
        registerPart(new PartBuilder(materialSystem).setName("Nugget").setPartType(item));


        List<PartDataPiece> blockDataPieces = Lists.newArrayList();
        blockDataPieces.add(new PartDataPiece("hardness", false));
        blockDataPieces.add(new PartDataPiece("resistance", false));
        blockDataPieces.add(new PartDataPiece("harvestLevel", false));
        blockDataPieces.add(new PartDataPiece("harvestTool", false));
        registerPart(new PartBuilder(materialSystem).setName("Storage").setPartType(block).setData(blockDataPieces));

        List<PartDataPiece> oreDataPieces = Lists.newArrayList();
        oreDataPieces.addAll(blockDataPieces);
        oreDataPieces.add(new PartDataPiece("variants", false));
        oreDataPieces.add(new PartDataPiece("dropType", false));
        registerPart(new PartBuilder(materialSystem).setName("Ore").setPartType(ore).setData(oreDataPieces));
        registerPart(new PartBuilder(materialSystem).setName("Poor Ore").setPartType(ore).setData(oreDataPieces));
        registerPart(new PartBuilder(materialSystem).setName("Dense Ore").setPartType(ore).setData(oreDataPieces));
    }

    private void registerPart(PartBuilder partBuilder) {
        try {
            partBuilder.createPart();
        } catch (MaterialException e) {
            mod.getLogger().getLogger().error(e);
        }
    }

    private void createOreSubBlocks(MaterialPart materialPart) {
        MaterialPartData data = materialPart.getData();
        if (data.containsDataPiece("variant")) {
            String[] variantNames = data.getDataPiece("variant").split(",");
            int[] hardness = getArrayForField(data, "hardness");
            int[] resistance = getArrayForField(data, "resistance");
            int[] harvestLevel = getArrayForField(data, "harvestLevel");
            String[] harvestTool = null;
            String[] drops = null;

            if (data.containsDataPiece("harvestTool")) {
                harvestTool = data.getDataPiece("harvestTool").split(",");
            }

            if (data.containsDataPiece("drops")) {
                drops = data.getDataPiece("drops").split(",");
            }

            for (int i = 0; i < variantNames.length; i++) {
                String variantName = variantNames[i];
                MaterialPart variantMaterialPart = new MaterialPart(materialSystem, materialPart.getMaterial(), materialPart.getPart(), variantName);
                IBlockState blockState = getBlockStateFor(variantName);
                MaterialPartData variantData = variantMaterialPart.getData();
                trySetData(hardness, i, "hardness", variantData);
                trySetData(resistance, i, "resistance", variantData);
                trySetData(harvestLevel, i, "harvestTool", variantData);
                if (harvestTool != null && harvestTool.length > i) {
                    data.addDataPiece("harvestTool", harvestTool[i]);
                }
                if (drops != null &&  drops.length > i) {
                    data.addDataPiece("drops", drops[i]);
                }
                subBlockSystem.registerSubBlock(new SubBlockOrePart(variantMaterialPart, blockState, this.materialSystem));
            }
        } else {
            subBlockSystem.registerSubBlock(new SubBlockOrePart(materialPart, stone, this.materialSystem));
        }
    }

    private void trySetData(int[] numbers, int place, String fieldName, MaterialPartData data) {
        if (numbers != null && numbers.length > place) {
            data.addDataPiece(fieldName, Integer.toString(numbers[place]));
        }
    }

    private int[] getArrayForField(MaterialPartData data, String fieldName) {
        int[] returned = null;
        if (data.containsDataPiece(fieldName)) {
            String[] stringPieces = data.getDataPiece(fieldName).split(",");
            returned = new int[stringPieces.length];
            for (int i = 0; i < stringPieces.length; i++) {
                returned[i] = Integer.parseInt(stringPieces[i]);
            }
        }
        return returned;
    }

    private IBlockState getBlockStateFor(String variantName) {
        IBlockState variant = BLOCKS.getValue(new ResourceLocation("stone")).getDefaultState();
        String[] variantArray = variantName.split(":");
        String blockString;
        int meta = 0;
        if (variantArray.length < 2) {
            blockString = variantArray[0];
        } else {
            blockString = variantArray[0] + ":" + variantArray[1];
        }
        if (variantArray.length == 3) {
            meta = Integer.parseInt(variantArray[2]);
        }
        Block block = BLOCKS.getValue(new ResourceLocation(blockString));
        if (block != null) {
            //noinspection deprecation
            variant = block.getStateFromMeta(meta);
        } else {
            this.mod.getLogger().fatal("Couldn't find block for variant string: " + blockString);
        }
        return variant;
    }
}
