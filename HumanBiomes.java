package freeradicalx.humanbiomes;

import java.util.HashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import ttftcuts.atg.api.ATGBiomes;
import ttftcuts.atg.api.ATGBiomes.BiomeType;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import freeradicalx.roads.WorldGenRoadsOLD;
import freeradicalx.zombies.SpawnListener;
import freeradicalx.zombies.ZombiesCommonProxy;

@Mod(
		modid = "humanbiomes",
		name = "Human Biomes",
		version = "0.0.1")

@NetworkMod(
		serverSideRequired = false,
		clientSideRequired = true)

public class HumanBiomes {

	WorldGenRoads roadGen = new WorldGenRoads();
	public static HashMap<String, int[]> roadCache = new HashMap<String, int[]>();
	
	public static BiomeGenBase TestBiome1 = (new BiomeGenTest(50)).setColor(13786898).setBiomeName("Test Biome 1").setTemperatureRainfall(.5F, .5F).setMinMaxHeight(0F, 1F);
	public static BiomeGenBase TestBiome2 = (new BiomeGenTest2(51)).setColor(13786898).setBiomeName("Test Biome 2").setTemperatureRainfall(.5F, .5F).setMinMaxHeight(0F, 1F);
	public static BiomeGenBase TestBiome3 = (new BiomeGenTest3(52)).setColor(13786898).setBiomeName("Test Biome 3").setTemperatureRainfall(.5F, .5F).setMinMaxHeight(0F, 1F);

	@EventHandler
    public void load(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(roadGen);
		
		GameRegistry.addBiome(TestBiome1);
		GameRegistry.addBiome(TestBiome2);
		GameRegistry.addBiome(TestBiome3);
		
		MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeListener());
		MinecraftForge.TERRAIN_GEN_BUS.register(new PopulateChunkListener());
		
		ATGBiomes.enableBiomeGroupAssignmentEvent();
		//System.out.println("SEND GROUP ASSIGNMENT EVENTS: " + ttftcuts.atg.gen.ATGBiomeManager.sendGroupAssignmentEvents);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if ( Loader.isModLoaded("ATG") ) { 
			
			ATGBiomes.addBiomeGroup(ATGBiomes.BiomeType.LAND, "Rural", .5D, .5D, 0D, 0D, .5D, (long)123.321, false);
			ATGBiomes.addBiomeGroup(ATGBiomes.BiomeType.LAND, "Suburban", .5D, .5D, 0D, 0D, .5D, (long)123.321, false);
			ATGBiomes.addBiomeGroup(ATGBiomes.BiomeType.LAND, "City", .5D, .5D, 0D, 0D, .5D, (long)123.321, false);
	        ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "Rural", TestBiome3, 1.0);
	        ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "Suburban", TestBiome2, 1.0);
	        ATGBiomes.addBiome(ATGBiomes.BiomeType.LAND, "City", TestBiome1, 1.0);
	        //ATGBiomes.modGroupSuitability(ATGBiomes.BiomeType.LAND, "City", 1D);
	    }
	}
	
	public static HashMap getRoadCache(){
		return roadCache;
	}
	
}
