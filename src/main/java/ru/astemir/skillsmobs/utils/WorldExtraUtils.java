package ru.astemir.skillsmobs.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class WorldExtraUtils {


    public static BlockPos getNearestAirBlockAbove(Level level, BlockPos pos){
        BlockPos check = pos;
        while(!level.getBlockState(check).isAir()){
            if (check.getY() >= level.getMaxBuildHeight()){
                break;
            }
            check = check.above();
        }
        return check;
    }


    public static boolean isBiomes(Biome biome, ResourceKey<Biome>... biomes){
        for (ResourceKey<Biome> biomeKey : biomes){
            if (isBiome(biome,biomeKey)){
                return true;
            }
        }
        return false;
    }

    public static boolean isBiome(Biome biome,ResourceKey<Biome> biomeKey){
        if (biomeKey != null && biome != null) {
            ResourceLocation a = ForgeRegistries.BIOMES.getKey(biome);
            if (a != null) {
                ResourceLocation registryBiomeLocation = biomeKey.location();
                if (registryBiomeLocation != null) {
                    if (ForgeRegistries.BIOMES.containsKey(registryBiomeLocation)) {
                        Biome registryBiome = ForgeRegistries.BIOMES.getValue(registryBiomeLocation);
                        if (registryBiome != null){
                            ResourceLocation b = ForgeRegistries.BIOMES.getKey(registryBiome);
                            if (b != null){
                                return a.equals(b);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isIceBlock(BlockState state){
        return state.is(Blocks.ICE) || state.is(Blocks.BLUE_ICE) || state.is(Blocks.FROSTED_ICE) || state.is(Blocks.PACKED_ICE);
    }
}
