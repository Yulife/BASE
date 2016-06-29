package com.acronym.base.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.IOException;
import java.util.Map;

import static net.minecraft.client.Minecraft.getMinecraft;

/**
 * Created by Jared on 6/29/2016.
 */
public class ResourceUtils {

    /**
     * Gets the IResource from an item
     *
     * @param item
     * @return IResource
     */
    public static IResource getResourceFromItem(ItemStack item) {
        try {
            return Minecraft.getMinecraft().getResourceManager().getResource(getResourceLocationFromItem(item));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ResourceLocation getResourceLocationFromItem(ItemStack item) {
        //TODO replace reflection with an AT
        Map<String, TextureAtlasSprite> map = ReflectionHelper.getPrivateValue(TextureMap.class, getMinecraft().getTextureMapBlocks(), "mapRegisteredSprites");
        for (Map.Entry<String, TextureAtlasSprite> entry : map.entrySet()) {
            if (entry.getKey().endsWith(item.getItem().getRegistryName().getResourcePath())) {
                try {
                    ResourceLocation res = new ResourceLocation(entry.getKey().split(":")[0], "textures/" + entry.getValue().getIconName().split(":")[1] + ".png");
                    return res;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}