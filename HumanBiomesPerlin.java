package freeradicalx.humanbiomes;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class HumanBiomesPerlin {
	
	//MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
	//World world = server.worldServers[0];
	//World world;
	long seed; // = world.getSeed();
	
	float density;
	int x, z;
	
	public int getDensity(int x, int z){	

		if(MinecraftServer.getServer().worldServers.length > 0){
			seed = MinecraftServer.getServer().worldServers[0].getSeed();
		}
		else{
			return 130;
		}
		
		this.x = x;
		this.z = z;	
		
		/*
		density = 	(perlinPass(x,z,4) +
					perlinPass(x,z,8) +
					perlinPass(x,z,16) +
					perlinPass(x,z,64) +
					perlinPass(x,z,256)) / 5;
		
	    //density -= 80;
	    if (density < 0){density = 0;}
	    density *= 2;
	    if (density > 255){density = 255;}
	    if (density >= 105 && density < 128){density = 128;}
	    if (density < 105){density = 0;}
		
	    //System.out.println(density);
	     */
		
		/*//DEFAULTS
	    density = (perlinPassSmooth(x,z,4) +
                perlinPassSmooth(x,z,16) +
                perlinPassSmooth(x,z,32) +
                perlinPassSmooth(x,z,32) +
                perlinPassSmooth(x,z,64) +
                perlinPassSmooth(x,z,64) +
                perlinPassSmooth(x,z,64)) / 6;
	    */
		
		/*
	    density = (perlinPassSmooth(x,z,8) +
                perlinPassSmooth(x,z,32) +
                perlinPassSmooth(x,z,64) +
                perlinPassSmooth(x,z,64) +
                perlinPassSmooth(x,z,128) +
                perlinPassSmooth(x,z,128) +
                perlinPassSmooth(x,z,128)) / 6;
	    
	    density -= 80;         
	    if (density < 0){density = 0;}
	    density *= 2;
	    if (density > 255){density = 255;}
	    
	    if (density <= 50){density = 0;}
	    if (density > 50 && density <= 128){density = 128;}
	    if (density > 128 && density <= 210){density = 186;}
	    if (density > 210 && density <= 255){density = 255;}
		*/
		
		int density = (
	    	perlinPassSmooth(x,z,4) +
	     	perlinPassSmooth(x,z,16) +
	      	perlinPassSmooth(x,z,16) +
	      	perlinPassSmooth(x,z,32) +
	       	perlinPassSmooth(x,z,32) +
	       	perlinPassSmooth(x,z,32)) / 5;

		density -= 80;         
		if (density < 0){density = 0;}
		density *= 2;
		if (density > 255){density = 255;}
	  
		if (density <= 20){density = 0;}
		if (density > 20 && density <= 128){density = 128;}
		if (density > 128 && density <= 210){density = 186;}
		if (density > 210 && density <= 255){density = 255;}
		
		
	    //System.out.println(x+","+z+": "+density);
		return (int)density;
	}
	
	private int perlinPass(int x, int z, int scale){
		
		return (int) Math.abs(((hash(x / scale) + hash(z / scale)) * seed) % 256);
		
		/*
	    int value0 = (int) Math.abs(((hash(x / scale) + hash(z / scale)) * seed) % 256);
	    int value1 = (int) Math.abs(((hash((x / scale) + scale) + hash(z / scale)) * seed) % 256);
	    int value2 = (int) Math.abs(((hash(x / scale) + hash((z / scale) + scale)) * seed) % 256);
	    int value3 = (int) Math.abs(((hash((x / scale) + scale) + hash((z / scale) + scale)) * seed) % 256);
	    
        int topRow = ((value0 * (scale - (x % scale))) + (value1 * (x % scale))) / scale;
        int bottomRow = ((value2 * (scale - (x % scale))) + (value3 * (x % scale))) / scale;
		
        return ((topRow * (scale - (z % scale))) + (bottomRow * (z % scale))) / scale;
        */
	}
	
	private int hash(int n){
	    n = ((n >> 16) ^ n) * 0x45d9f3b;
	    n = ((n >> 16) ^ n) * 0x45d9f3b;
	    n = ((n >> 16) ^ n);
	    return n;
	}
	
	private int perlinPassSmooth(int x, int z, int scale){
		  if(x < 0){x -= scale;}
		  if(z < 0){z -= scale;}
		  int baseX = (x / scale) * scale;
		  int baseZ = (z / scale) * scale;
		  int addX = (x % scale);
		  int addZ = (z % scale);
		  int topRow, bottomRow, thisPixel;
		  
		  int value0 = (int) Math.abs(( (hash(baseX) + hash(baseZ)) * seed) % 256);
		  int value1 = (int) Math.abs(( (hash(baseX + scale) + hash(baseZ)) * seed) % 256);
		  int value2 = (int) Math.abs(( (hash(baseX) + hash(baseZ + scale)) * seed) % 256);
		  int value3 = (int) Math.abs(( (hash(baseX + scale) + hash(baseZ + scale)) * seed) % 256);
		  
		  if (x >= 0){
			    topRow = ((value0 * (scale - addX)) + (value1 * addX)) / scale;
			    bottomRow = ((value2 * (scale - addX)) + (value3 * addX)) / scale;
			  }
			  else{
			    topRow = ((value1 * (scale - addX)) + (value0 * addX)) / scale;
			    bottomRow = ((value3 * (scale - addX)) + (value2 * addX)) / scale;
			  }
			  
			  if (z >= 0){
			    thisPixel = ((topRow * (scale - addZ)) + (bottomRow * addZ)) / scale;
			  }
			  else{
			    thisPixel = ((bottomRow * (scale - addZ)) + (topRow * addZ)) / scale;
			  }

			  return thisPixel;
		  
		  //int topRow = ((value0 * (scale - addX)) + (value1 * addX)) / scale;
		  //int bottomRow = ((value2 * (scale - addX)) + (value3 * addX)) / scale;

		  //return ((topRow * (scale - addZ)) + (bottomRow * addZ)) / scale;
	}

}
