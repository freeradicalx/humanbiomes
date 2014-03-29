package freeradicalx.humanbiomes;

import net.minecraftforge.common.MinecraftForge;
import ttftcuts.atg.api.ATGBiomes;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class HumanBiomesCommonProxy {
	
	public void registerBiomeListener(){
		System.out.println(FMLCommonHandler.instance().getSide());
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			MinecraftForge.TERRAIN_GEN_BUS.register(new BiomeListener());
			ATGBiomes.enableBiomeGroupAssignmentEvent();
		}
	}

}
