package com.teamacronymcoders.base.client.models.blocksided;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.blocks.properties.PropertySideType;
import com.teamacronymcoders.base.blocks.properties.SideType;
import com.teamacronymcoders.base.client.models.ModelUtils;
import com.teamacronymcoders.base.client.models.blocksided.ITextureNamer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

//Borrowed from IE with Permission
@SideOnly(Side.CLIENT)
public class BakedModelBlockSided implements IBakedModel {
    private static final String MODEL_PREFIX = "confSides_";
    private static final String RESOURCE_LOCATION = "models/block/smartmodel/" + MODEL_PREFIX;
    //Holy shit, this type-chaining is messy. But I wanted to use lambdas!
    public static HashMap<String, ITextureNamer> TYPES = new HashMap<>();

    static {
        TYPES.put("hud_", new ITextureNamer() {//horizontal, up, down
            @Override
            public String nameFromSide(EnumFacing side, SideType sideType) {
                return side.ordinal() < 2 ? side.getName() : "side";
            }
        });
    }

    public static HashMap<String, List<BakedQuad>> modelCache = new HashMap<>();

    final String name;
    final String modid;
    public TextureAtlasSprite[][] textures;

    public BakedModelBlockSided(String modid, String name, TextureAtlasSprite[][] textures) {
        this.name = name;
        this.modid = modid;
        this.textures = textures;
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        TextureAtlasSprite[] tex = new TextureAtlasSprite[6];
        for (int i = 0; i < tex.length; i++)
            tex[i] = this.textures[i][0];
        char[] keyArray = "000000".toCharArray();
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extended = (IExtendedBlockState) state;
            for (int i = 0; i < PropertySideType.SIDE_TYPE.length; i++)
                if (extended.getUnlistedNames().contains(PropertySideType.SIDE_TYPE[i])) {
                    SideType config = extended.getValue(PropertySideType.SIDE_TYPE[i]);
                    if (config != null) {
                        int c = config.ordinal();
                        tex[i] = this.textures[i][c];
                        keyArray[i] = Character.forDigit(c, 10);
                    }
                }
        }
        String key = name + String.copyValueOf(keyArray);
        if (!modelCache.containsKey(key))
            modelCache.put(key, bakeQuads(tex));
        return modelCache.get(key);
    }

    private static List<BakedQuad> bakeQuads(TextureAtlasSprite[] sprites) {
        List<BakedQuad> quads = Lists.newArrayListWithExpectedSize(6);
        float[] colour = {1, 1, 1, 1};
        Vector3f[] vertices = {new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0, 0)};
        quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.DOWN, sprites[0], new double[]{0, 16, 16, 0}, colour, true));
        vertices = new Vector3f[]{new Vector3f(0, 1, 0), new Vector3f(0, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 1, 0)};
        quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.UP, sprites[1], new double[]{0, 0, 16, 16}, colour, false));

        vertices = new Vector3f[]{new Vector3f(1, 0, 0), new Vector3f(1, 1, 0), new Vector3f(0, 1, 0), new Vector3f(0, 0, 0)};
        quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.NORTH, sprites[2], new double[]{0, 16, 16, 0}, colour, true));
        vertices = new Vector3f[]{new Vector3f(1, 0, 1), new Vector3f(1, 1, 1), new Vector3f(0, 1, 1), new Vector3f(0, 0, 1)};
        quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.SOUTH, sprites[3], new double[]{16, 16, 0, 0}, colour, false));

        vertices = new Vector3f[]{new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, 1, 1), new Vector3f(0, 0, 1)};
        quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.WEST, sprites[4], new double[]{0, 16, 16, 0}, colour, true));
        vertices = new Vector3f[]{new Vector3f(1, 0, 0), new Vector3f(1, 1, 0), new Vector3f(1, 1, 1), new Vector3f(1, 0, 1)};
        quads.add(ModelUtils.createBakedQuad(DefaultVertexFormats.ITEM, vertices, EnumFacing.EAST, sprites[5], new double[]{16, 16, 0, 0}, colour, false));
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    @Nonnull
    public TextureAtlasSprite getParticleTexture() {
        return this.textures[0][0];
    }

    @SuppressWarnings("deprecation")
    static final ItemCameraTransforms defaultTransforms = new ItemCameraTransforms(
            new ItemTransformVec3f(new Vector3f(75, 45, 0), new Vector3f(0, .25f, 0), new Vector3f(0.375f, 0.375f, 0.375f)), //thirdperson left
            new ItemTransformVec3f(new Vector3f(75, 45, 0), new Vector3f(0, .15625f, 0), new Vector3f(0.375f, 0.375f, 0.375f)), //thirdperson left

            new ItemTransformVec3f(new Vector3f(0, 45, 0), new Vector3f(0, 0, 0), new Vector3f(.4f, .4f, .4f)), //firstperson left
            new ItemTransformVec3f(new Vector3f(0, 225, 0), new Vector3f(0, 0, 0), new Vector3f(.4f, .4f, .4f)), //firstperson right

            new ItemTransformVec3f(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)), //head
            new ItemTransformVec3f(new Vector3f(30, 225, 0), new Vector3f(0, 0, 0), new Vector3f(.625f, .625f, .625f)), //gui
            new ItemTransformVec3f(new Vector3f(0, 0, 0), new Vector3f(0, .1875f, 0), new Vector3f(.25f, .25f, .25f)), //ground
            new ItemTransformVec3f(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(.5f, .5f, .5f))); //fixed

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return defaultTransforms;
    }

    @Override
    @Nonnull
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
