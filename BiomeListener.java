package freeradicalx.humanbiomes;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ttftcuts.atg.api.events.listenable.ATGBiomeGroupAssignmentEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;

public class BiomeListener {
	
	public static int biomeScale = 8;					//AVERAGE DISTANCE ACROSS A BIOME CELL, IN CHUNKS
	public static int roadScale = 20;
	public static long seed;
	HumanBiomesPerlin perlin = new HumanBiomesPerlin();
	
	
	@ForgeSubscribe
	public void biomeReassign(ATGBiomeGroupAssignmentEvent event)
	{	
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			//int density = perlin.getDensity(event.x >> 4, event.z >> 4);
			int density = biomeCheck(event.x >> 4, event.z >> 4, biomeScale);
			
			//if (density != 130){
			//	System.out.println("testing " + (event.x >> 4) + ", " + (event.z >> 4) + ": " + density);
			//}
			/*
			if (density > 127 && density < 169){
				event.setGroup("Rural");
			}
			if (density >= 169 && density < 211){
				event.setGroup("Suburban");
			}
			if (density >= 211){
				event.setGroup("City");
			}
			*/
			
			if (density == 128){
				event.setGroup("Rural");
			}
			if (density == 186){
				event.setGroup("Suburban");
			}
			if (density == 255){
				event.setGroup("City");
			}
			
		}
	}
	
	int biomeCheck(int X, int Y, int scale){
		  
		int thisDensity;
		int shortestDistance = scale * 10;
		int[] nearestVertex = new int[2];
		  
		for (int checkX = -1; checkX < 2; checkX++){
			for (int checkY = -1; checkY < 2; checkY++){
		      
				int otherX = (X+(checkX*scale));
				int otherY = (Y+(checkY*scale));
				int[] otherVertex = biomeVertex(otherX, otherY, scale);

				int distanceX = Math.abs(Math.abs(X) - Math.abs(otherVertex[0]));
				int distanceY = Math.abs(Math.abs(Y) - Math.abs(otherVertex[1]));
				int distance = (int)Math.sqrt( Math.abs((distanceX * distanceX) + Math.abs(distanceY * distanceY)) );
		      
				if (distance < shortestDistance){
					shortestDistance = distance;
					nearestVertex[0] = otherVertex[0];
					nearestVertex[1] = otherVertex[1];
				}
		    }
		}

		thisDensity = perlin.getDensity( ((nearestVertex[0] / (scale/2))), ((nearestVertex[1] / (scale/2))) );
	    return thisDensity;
	}
	
	public static int[] biomeVertex(int X, int Y, int scale){
		int baseX = (X / scale) * scale;
		int baseY = (Y / scale) * scale;
		int[] vertex = {
		    (int) (Math.abs(((hash(baseX) * getSeed()) + hash(Y / scale)) % scale) + baseX),
		    (int) (Math.abs(((hash(baseY) * getSeed()) + hash(X / scale)) % scale) + baseY)
		};
		  	return vertex;
	}

	public static int hash(int n) {
		 n = ((n >> 16) ^ n) * 0x45d9f3b;
		 n = ((n >> 16) ^ n) * 0x45d9f3b;
		 n = ((n >> 16) ^ n);
		 return n;
	}
	
	public static long getSeed(){
		
		if(MinecraftServer.getServer().worldServers.length > 0){
			seed = MinecraftServer.getServer().worldServers[0].getSeed();
		}
		else{
			return 130;
		}
		return seed;
	}
	
}