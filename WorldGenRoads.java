package freeradicalx.humanbiomes;

import java.util.HashMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;
import freeradicalx.roads.RoadData;
import freeradicalx.util.ExtraUtils;

public class WorldGenRoads implements IWorldGenerator{

	World world;
	Chunk chunk;
	IChunkProvider chunkProvider;
	long seed;
	int width = -5; 								//change this line for wider or narrower highways. Width of highway == abs(width * 2)
	int roadResolution = 3;							//controls how small each road segment is. 1 = 1 chunk
	int roadScale = 64;								//number of chunks, on average, between road "vertices" (Where roads meet at intersections)

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World theWorld,
			IChunkProvider chunkGenerator, IChunkProvider theChunkProvider) {
		
		//System.out.print("Starting gen on " + chunkX + "," + chunkZ + "... ");
		
		world = theWorld;
		chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		chunkProvider = theChunkProvider;
		
		System.out.println(world.getBiomeGenForCoords((chunkX*16)+8, (chunkZ*16)+8));
		
		BiomeGenBase biome = world.getBiomeGenForCoords((chunkX*16)+8, (chunkZ*16)+8);
		if(biome instanceof BiomeGenTest3){
			switch(world.provider.dimensionId){
			case 0: generateSurface(chunkX, chunkZ);
			case 1: generateEnd(world, random, chunkX, chunkZ);
			case -1: generateNether(world, random, chunkX, chunkZ);
		}		
	}
		
	}
	private void generateNether(World world2, Random random, int chunkX, int chunkZ) {}
	private void generateEnd(World world2, Random random, int chunkX, int chunkZ) {}
	
	private void generateSurface(int chunkX, int chunkZ) {

		
		//System.out.print("Starting gen on " + chunkX + "," + chunkZ + "... ");
		
		if(MinecraftServer.getServer().worldServers.length > 0){
			seed = MinecraftServer.getServer().worldServers[0].getSeed();
		}
		else{
			seed = 12345;
		}
		
		int thisX = (chunkX * 16) + 8;
		int thisZ = (chunkZ * 16) + 8;
		int thisY = ExtraUtils.groundHeight(world, thisX, thisZ);
		
		String nearbyRoads[] = processNearbyRoads(chunkX, chunkZ, BiomeListener.roadScale, true);
		
		int roadCount = 0;
		boolean[] counter = new boolean[4];
		//String[] roadTags = new String[4];
		
		for (int i = 0; i < nearbyRoads.length; i++){
			int[] pointsToDrawTo = checkToDrawRoads(nearbyRoads[i], chunkX, chunkZ);
			if (pointsToDrawTo != null){
				roadCount++;
				counter[i] = true;
				for (int j = 0; j < pointsToDrawTo.length; j+=2){
					
					int thatX = (pointsToDrawTo[j] * 16) + 8;
					int thatZ = (pointsToDrawTo[j+1] * 16) + 8;
					int thatY = ExtraUtils.groundHeight(world, thatX, thatZ);
					
					if (Math.abs(thisY - thatY) > 20){
						thatY = ExtraUtils.groundHeight(world, thatX, thatZ);
					}
					
					//System.out.println("        Drawing " + chunkX + ", " + chunkZ + " to " + pointsToDrawTo[j] + ", " + pointsToDrawTo[j+1]);
					
					drawSegment(thisX, thisY, thisZ, thatX, thatY, thatZ);
				}
			}
		}
		
		/*
		if (!(roadCount == 0 || roadCount == 4)){
			System.out.println("INCONSISTANT NUMBER OF DRAWN ROADS FROM " + chunkX + "," + chunkZ + "!: " + roadCount);
			for (int m = 0; m < counter.length; m++){
				if (counter[m] == false){
					System.out.print("   " + nearbyRoads[m] + ", nearbyRoads[" + m + "]:");
					for (int p = 0; p < ((int[])HumanBiomes.getRoadCache().get(nearbyRoads[m])).length; p+=2){
						System.out.print(" [" + ((int[])HumanBiomes.getRoadCache().get(nearbyRoads[m]))[p] + "," + ((int[])HumanBiomes.getRoadCache().get(nearbyRoads[m]))[p+1] + "]");
					}
					System.out.println();
				}
			}
		} */
		
		//System.out.print("Done!");
		
		//System.out.println("Done!");
		
	}

	int[] fillRoadPoints(int[] roadPoints){ 
		
		if(roadPoints.length <= 4){ 
			return roadPoints;
		}
		  
		int x1 = roadPoints[0];
		int z1 = roadPoints[1];
		int x2 = roadPoints[roadPoints.length-2];
		int z2 = roadPoints[roadPoints.length-1];
		int mx = (x1 + x2) / 2;
		int mz = (z1 + z2) / 2;
		int dx = x1 - mx;
		int dz = z1 - mz;
		int leftX = mx + (-dz / 2);
		int leftZ = mz + (dx / 2);
		int rightX = mx + (dz / 2);
		int rightZ = mz + (-dx / 2);
		int[] possibilities = new int[6];
		possibilities[0] = leftX;
		possibilities[1] = leftZ;
		possibilities[2] = mx;
		possibilities[3] = mz;
		possibilities[4] = rightX;
		possibilities[5] = rightZ;
		
		chunkProvider.loadChunk(x1, z1);
		chunkProvider.loadChunk(x2, z2);
		chunkProvider.loadChunk(possibilities[0], possibilities[1]);
		chunkProvider.loadChunk(possibilities[2], possibilities[3]);
		chunkProvider.loadChunk(possibilities[4], possibilities[5]);
		
		int avgHeight = ((world.getHeightValue((x1*16)+8, (z1*16)+8) + world.getHeightValue((x2*16)+8, (z2*16)+8)) / 2);
		  
		int height1 = Math.abs(world.getHeightValue((possibilities[0]*16)+8, (possibilities[1]*16)+8) - avgHeight);
		int height2 = Math.abs(world.getHeightValue((possibilities[2]*16)+8, (possibilities[3]*16)+8) - avgHeight);
		int height3 = Math.abs(world.getHeightValue((possibilities[4]*16)+8, (possibilities[5]*16)+8) - avgHeight);
		
		int newSlot = (roadPoints.length-2) / 2;
		if (newSlot % 2 == 1){ newSlot--; }
		  
		int baseFirstX = (roadPoints[0] / roadScale) * roadScale;
		int baseFirstY = (roadPoints[1] / roadScale) * roadScale;
		int baseSecondX = (roadPoints[roadPoints.length-2] / roadScale) * roadScale;
		int baseSecondY = (roadPoints[roadPoints.length-1] / roadScale) * roadScale;
		  
		if (height1 < height3 && height1 < height2 &&
			(baseFirstX < possibilities[0] && possibilities[0] < baseSecondX+roadScale) &&
		    (baseFirstY < possibilities[1] && possibilities[1] < baseSecondY+roadScale)){
			roadPoints[newSlot] = possibilities[0]; roadPoints[newSlot+1] = possibilities[1];
		}
		else if (height3 < height1 && height3 < height2 &&
		    (baseFirstX < possibilities[4] && possibilities[4] < baseSecondX+roadScale) &&
		    (baseFirstY < possibilities[5] && possibilities[5] < baseSecondY+roadScale)){
		    roadPoints[newSlot] = possibilities[4]; roadPoints[newSlot+1] = possibilities[5];
		}
		else{
			roadPoints[newSlot] = possibilities[2]; roadPoints[newSlot+1] = possibilities[3]; 
		}
		  
		int[] newArray1 = new int[newSlot+2];
		int[] newArray2 = new int[roadPoints.length - newArray1.length + 2];

		if(roadPoints.length <= 4){
		    return roadPoints;
		}
		else{
			newArray1[0]=roadPoints[0];
		    newArray1[1]=roadPoints[1];
		    newArray1[newArray1.length-2]=roadPoints[newSlot];
		    newArray1[newArray1.length-1]=roadPoints[newSlot+1];

		    newArray2[0]=roadPoints[newSlot];
		    newArray2[1]=roadPoints[newSlot+1];
		    newArray2[newArray2.length-2]=roadPoints[roadPoints.length-2];
		    newArray2[newArray2.length-1]=roadPoints[roadPoints.length-1];
		}

		int[] filledArray1 = fillRoadPoints(newArray1);
		int[] filledArray2 = fillRoadPoints(newArray2);
		  
		for (int i = 0; i < filledArray1.length-2; i++){
		    roadPoints[i] = filledArray1[i];
		}
		for (int i = 0; i < filledArray2.length; i++){
		    roadPoints[newSlot + i] = filledArray2[i];
		}
		  
		return roadPoints;
	}
	
	int[] fillRoadPointsLinear(int[] roadPoints){
		
		if(roadPoints.length <= 4){ 
			return roadPoints;
		}
		  
		int x1 = roadPoints[0];
		int z1 = roadPoints[1];
		int x2 = roadPoints[roadPoints.length-2];
		int z2 = roadPoints[roadPoints.length-1];
		int mx = (x1 + x2) / 2;
		int mz = (z1 + z2) / 2;
		
		chunkProvider.loadChunk(x1, z1);
		chunkProvider.loadChunk(x2, z2);
		chunkProvider.loadChunk(mx, mz);

		int newSlot = (roadPoints.length-2) / 2;
		if (newSlot % 2 == 1){ newSlot--; }

		roadPoints[newSlot] = mx; roadPoints[newSlot+1] = mz;
		  
		int[] newArray1 = new int[newSlot+2];
		int[] newArray2 = new int[roadPoints.length - newArray1.length + 2];

		if(roadPoints.length <= 6){
		    return roadPoints;
		}
		else{
			newArray1[0]=roadPoints[0];
		    newArray1[1]=roadPoints[1];
		    newArray1[newArray1.length-2]=roadPoints[newSlot];
		    newArray1[newArray1.length-1]=roadPoints[newSlot+1];

		    newArray2[0]=roadPoints[newSlot];
		    newArray2[1]=roadPoints[newSlot+1];
		    newArray2[newArray2.length-2]=roadPoints[roadPoints.length-2];
		    newArray2[newArray2.length-1]=roadPoints[roadPoints.length-1];
		}

		int[] filledArray1 = fillRoadPointsLinear(newArray1);
		int[] filledArray2 = fillRoadPointsLinear(newArray2);
		  
		for (int i = 0; i < filledArray1.length-2; i++){
		    roadPoints[i] = filledArray1[i];
		}
		for (int i = 0; i < filledArray2.length; i++){
		    roadPoints[newSlot + i] = filledArray2[i];
		}
		  
		return roadPoints;
	}
	
	int[] roadVertex(int X, int Z, int scale){
		//int baseX = (int) ((Math.floor((float)X / (float)scale)) * scale);
		//int baseY = (int) ((Math.floor((float)Y / (float)scale)) * scale);
		if (X < 0){ X -=(BiomeListener.roadScale-1);}
		if (Z < 0){ Z -=(BiomeListener.roadScale-1);}
		
		int baseX = ((X / scale) * scale);
		int baseY = ((Z / scale) * scale);
		
		int[] vertex = {
				(int) (Math.abs(((hash(baseX + 5000) * seed) + hash(Z / scale)) % scale) + baseX),
				(int) (Math.abs(((hash(baseY + 5000) * seed) + hash(X / scale)) % scale) + baseY)
		};
		return vertex;
	}
	
	int hash(int n) {
		n = ((n >> 16) ^ n) * 0x45d9f3b;
		n = ((n >> 16) ^ n) * 0x45d9f3b;
		n = ((n >> 16) ^ n);
		return n;
	}
	
    //Draws a road segment from one x,z point to another. Does it's own y (height) calculations.
    public void drawSegment(int x1, int y1, int z1, int x2, int y2, int z2){
    	
    	float angle = 0;
    	float length = 0;
    	float diffX = Math.abs(x1 - x2);
    	float diffZ = Math.abs(z1 - z2);
    	float diffY = Math.abs(y1 - y2);
    	
    	if (x1 > x2){
    		diffX = -diffX;
    	}
    	if (y1 > y2){
    		diffY = -diffY;
    	}
    	if (z1 > z2){
    		diffZ = -diffZ;
    	}
    	
    	if (diffX != 0){
    		angle = (float) Math.atan(diffZ/diffX);
    		if (diffX <= 0){
    			angle = (float) (angle + Math.PI);
    		}
    	}
    	if (diffX == 0){
    		if (diffZ > 0){
    			angle = (float) 1.57;
    		}
    		if (diffZ <= 0){
    			angle = (float) -1.57;
    		}
    	}
    	
    	length = (float) (Math.sqrt((diffX * diffX) + (diffZ * diffZ)));
    	
		float incrementY = diffY / length;
		
		for (int progress = 0; progress <= Math.round(length); progress++){
			for (int crosspave = width; crosspave <= (Math.abs(width)); crosspave++){
	
				int rotatedX = (int)Math.round(x1 + (progress * Math.cos(angle)) - (crosspave * Math.sin(angle)));
				int rotatedZ = (int)Math.round(z1 + (progress * Math.sin(angle)) + (crosspave * Math.cos(angle)));
				int rotatedY = Math.round(y1 + incrementY*progress);
				
				for(int ycount = rotatedY; ycount <= (rotatedY+10); ycount++){ //overhead clearance to create above road
					if (ycount == rotatedY){
						int ground = ExtraUtils.groundHeightSea(world, rotatedX, rotatedZ);
						//An explaination of the long block ID check lists here:
						//In this for loop the road gen clears out all "underbrush"-like blocks that one would not find under an elevated road,
						//like leaves, wood, and any other non-"ground" blocks. It does this in a column of 2x2 blocks to account for holes
						//that would be left behind if the roadway is going diagonal (Floating point math + integer blocks = some blocks getting missed).
						//However if needs to check EACH of these four columns for ground vs underbrush, lest there be one column that's in a ravine
						//and one that's not, which if it didn't check each column would start clearing out a cavern below the road in a runaway
						//chain reaction til the end of the segment (At the least).
						for (int count = ycount-1; count >= ground; --count){
							if (world.getBlockId(rotatedX, count, rotatedZ) != 1
									&& world.getBlockId(rotatedX, count, rotatedZ) != 2
									&& world.getBlockId(rotatedX, count, rotatedZ) != 3
									&& world.getBlockId(rotatedX, count, rotatedZ) != 4
									&& world.getBlockId(rotatedX, count, rotatedZ) != 7
									&& world.getBlockId(rotatedX, count, rotatedZ) != 8
									&& world.getBlockId(rotatedX, count, rotatedZ) != 9
									&& world.getBlockId(rotatedX, count, rotatedZ) != 10
									&& world.getBlockId(rotatedX, count, rotatedZ) != 11
									&& world.getBlockId(rotatedX, count, rotatedZ) != 12
									&& world.getBlockId(rotatedX, count, rotatedZ) != 13
									&& world.getBlockId(rotatedX, count, rotatedZ) != 24){
								world.setBlock(rotatedX, count, rotatedZ, 0, 0, 2);
								if (world.getBlockId(rotatedX+1, count, rotatedZ) != 1
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 2
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 3
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 4
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 7
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 8
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 9
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 10
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 11
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 12
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 13
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 24){
									world.setBlock(rotatedX+1, count, rotatedZ, 0, 0, 2);
								}
								if (world.getBlockId(rotatedX+1, count, rotatedZ+1) != 1
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 2
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 3
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 4
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 7
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 8
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 9
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 10
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 11
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 12
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 13
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 24){
									world.setBlock(rotatedX+1, count, rotatedZ+1, 0, 0, 2);
								}
								if (world.getBlockId(rotatedX, count, rotatedZ+1) != 1
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 2
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 3
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 4
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 7
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 8
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 9
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 10
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 11
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 12
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 13
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 24){
									world.setBlock(rotatedX, count, rotatedZ+1, 0, 0, 2);
								}
							}
						}
						//Generate berm
						if (Math.abs(ycount - ground) <= 2 && (Math.abs(ycount - ground) > 0 && ycount > ground)){
							for (int count = ground; count < ycount; ++count){
								world.setBlock(rotatedX,count,rotatedZ,4,0,2);
								world.setBlock(rotatedX+1,count,rotatedZ,4,0,2);
								world.setBlock(rotatedX+1,count,rotatedZ+1,4,0,2);
								world.setBlock(rotatedX,count,rotatedZ+1,4,0,2);
							}
						}
						//Generate supporting pillars
						if (Math.abs(ycount - ground) > 2 && ycount > ground && progress % 9 == 0 && (crosspave == width+1 || crosspave == Math.abs(width)-1)){
							for (int count = ground; count < ycount; ++count){
								world.setBlock(rotatedX,count,rotatedZ,4,0,2);
								world.setBlock(rotatedX+1,count,rotatedZ,4,0,2);
								world.setBlock(rotatedX+1,count,rotatedZ+1,4,0,2);
								world.setBlock(rotatedX,count,rotatedZ+1,4,0,2);
							}
						}
						//Generate road base layer (under asphalt)
						world.setBlock(rotatedX,ycount-1,rotatedZ,4,0,2);
						world.setBlock(rotatedX+1,ycount-1,rotatedZ,4,0,2);
						world.setBlock(rotatedX+1,ycount-1,rotatedZ+1,4,0,2);
						world.setBlock(rotatedX,ycount-1,rotatedZ+1,4,0,2);
						
						//Generate asphalt
						world.setBlock(rotatedX,ycount,rotatedZ,35,15,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ,35,15,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ+1,35,15,2);
						world.setBlock(rotatedX,ycount,rotatedZ+1,35,15,2);
					}
					
					//Generate lane lines
					if (ycount == rotatedY && crosspave == 0 && progress % 6 != 0){
						world.setBlock(rotatedX,ycount,rotatedZ,35,4,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ,35,4,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ+1,35,4,2);
						world.setBlock(rotatedX,ycount,rotatedZ+1,35,4,2);
					}
					
					//Carve out empty space for road to travel through
					if (ycount > rotatedY){
						world.setBlock(rotatedX,ycount,rotatedZ,0,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ,0,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ+1,0,0,2);
						world.setBlock(rotatedX,ycount,rotatedZ+1,0,0,2);
					}
				}
				for(int ycount = rotatedY+10; ycount <= (rotatedY+20); ycount++){
					int groundcount = 0;
					if (world.getBlockId(rotatedX,ycount,rotatedZ) == 1
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 2
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 3
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 7
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 12
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 13
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 24){
						++groundcount;
					}
					else {
						world.setBlock(rotatedX,ycount,rotatedZ,0,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ,0,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ+1,0,0,2);
						world.setBlock(rotatedX,ycount,rotatedZ+1,0,0,2);
					}
					if (groundcount >= 2){
						ycount = 21;
					}
				}
			}
		}
    }
    
    //Draws a road segment from one x,z point to another. Does it's own y (height) calculations.
    public void drawRuralSegment(int x1, int y1, int z1, int x2, int y2, int z2){
    	
    	float angle = 0;
    	float length = 0;
    	float diffX = Math.abs(x1 - x2);
    	float diffZ = Math.abs(z1 - z2);
    	float diffY = Math.abs(y1 - y2);
    	
    	if (x1 > x2){
    		diffX = -diffX;
    	}
    	if (y1 > y2){
    		diffY = -diffY;
    	}
    	if (z1 > z2){
    		diffZ = -diffZ;
    	}
    	
    	if (diffX != 0){
    		angle = (float) Math.atan(diffZ/diffX);
    		if (diffX <= 0){
    			angle = (float) (angle + Math.PI);
    		}
    	}
    	if (diffX == 0){
    		if (diffZ > 0){
    			angle = (float) 1.57;
    		}
    		if (diffZ <= 0){
    			angle = (float) -1.57;
    		}
    	}
    	
    	length = (float) (Math.sqrt((diffX * diffX) + (diffZ * diffZ)));
    	
		float incrementY = diffY / length;
		
		for (int progress = 0; progress <= Math.round(length); progress++){
			for (int crosspave = width; crosspave <= (Math.abs(width)); crosspave++){
	
				int rotatedX = (int)Math.round(x1 + (progress * Math.cos(angle)) - (crosspave * Math.sin(angle)));
				int rotatedZ = (int)Math.round(z1 + (progress * Math.sin(angle)) + (crosspave * Math.cos(angle)));
				int rotatedY = Math.round(y1 + incrementY*progress);
				
				for(int ycount = rotatedY; ycount <= (rotatedY+10); ycount++){ //overhead clearance to create above road
					if (ycount == rotatedY){
						int ground = ExtraUtils.groundHeightSea(world, rotatedX, rotatedZ);
						//An explaination of the long block ID check lists here:
						//In this for loop the road gen clears out all "underbrush"-like blocks that one would not find under an elevated road,
						//like leaves, wood, and any other non-"ground" blocks. It does this in a column of 2x2 blocks to account for holes
						//that would be left behind if the roadway is going diagonal (Floating point math + integer blocks = some blocks getting missed).
						//However if needs to check EACH of these four columns for ground vs underbrush, lest there be one column that's in a ravine
						//and one that's not, which if it didn't check each column would start clearing out a cavern below the road in a runaway
						//chain reaction til the end of the segment (At the least).
						for (int count = ycount-1; count >= ground; --count){
							if (world.getBlockId(rotatedX, count, rotatedZ) != 1
									&& world.getBlockId(rotatedX, count, rotatedZ) != 2
									&& world.getBlockId(rotatedX, count, rotatedZ) != 3
									&& world.getBlockId(rotatedX, count, rotatedZ) != 4
									&& world.getBlockId(rotatedX, count, rotatedZ) != 7
									&& world.getBlockId(rotatedX, count, rotatedZ) != 8
									&& world.getBlockId(rotatedX, count, rotatedZ) != 9
									&& world.getBlockId(rotatedX, count, rotatedZ) != 10
									&& world.getBlockId(rotatedX, count, rotatedZ) != 11
									&& world.getBlockId(rotatedX, count, rotatedZ) != 12
									&& world.getBlockId(rotatedX, count, rotatedZ) != 13
									&& world.getBlockId(rotatedX, count, rotatedZ) != 24){
								world.setBlock(rotatedX, count, rotatedZ, 0, 0, 2);
								if (world.getBlockId(rotatedX+1, count, rotatedZ) != 1
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 2
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 3
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 4
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 7
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 8
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 9
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 10
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 11
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 12
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 13
										&& world.getBlockId(rotatedX+1, count, rotatedZ) != 24){
									world.setBlock(rotatedX+1, count, rotatedZ, 0, 0, 2);
								}
								if (world.getBlockId(rotatedX+1, count, rotatedZ+1) != 1
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 2
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 3
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 4
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 7
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 8
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 9
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 10
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 11
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 12
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 13
										&& world.getBlockId(rotatedX+1, count, rotatedZ+1) != 24){
									world.setBlock(rotatedX+1, count, rotatedZ+1, 0, 0, 2);
								}
								if (world.getBlockId(rotatedX, count, rotatedZ+1) != 1
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 2
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 3
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 4
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 7
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 8
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 9
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 10
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 11
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 12
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 13
										&& world.getBlockId(rotatedX, count, rotatedZ+1) != 24){
									world.setBlock(rotatedX, count, rotatedZ+1, 0, 0, 2);
								}
							}
						}
						//Generate dirt berm
						
						if (Math.abs(ycount - ground) <= 5 && (Math.abs(ycount - ground) > 0 && ycount >= ground)){
							for (int count = ground; count < ycount; ++count){
								world.setBlock(rotatedX,count,rotatedZ,3,0,2);
								world.setBlock(rotatedX+1,count,rotatedZ,3,0,2);
								world.setBlock(rotatedX+1,count,rotatedZ+1,3,0,2);
								world.setBlock(rotatedX,count,rotatedZ+1,3,0,2);
							}
						}
						
						//Generate dirt roadtop or spruce wooden bridge top
						if (Math.abs(ycount - ground) <= 5){
						world.setBlock(rotatedX,ycount,rotatedZ,3,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ,3,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ+1,3,0,2);
						world.setBlock(rotatedX,ycount,rotatedZ+1,3,0,2);
						}
						else{
						world.setBlock(rotatedX,ycount,rotatedZ,5,1,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ,5,1,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ+1,5,1,2);
						world.setBlock(rotatedX,ycount,rotatedZ+1,5,1,2);
						}
						
						if (crosspave == 5 || crosspave == -5){
							world.setBlock(rotatedX,ground,rotatedZ,13,0,2);
							world.setBlock(rotatedX+1,ground,rotatedZ,13,0,2);
							world.setBlock(rotatedX+1,ground,rotatedZ+1,13,0,2);
							world.setBlock(rotatedX,ground,rotatedZ+1,13,0,2);
						}
					}
					
					//Carve out empty space for road to travel through
					if (ycount > rotatedY){
						world.setBlock(rotatedX,ycount,rotatedZ,0,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ,0,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ+1,0,0,2);
						world.setBlock(rotatedX,ycount,rotatedZ+1,0,0,2);
					}
				}
				for(int ycount = rotatedY+10; ycount <= (rotatedY+20); ycount++){
					int groundcount = 0;
					if (world.getBlockId(rotatedX,ycount,rotatedZ) == 1
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 2
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 3
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 7
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 12
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 13
							|| world.getBlockId(rotatedX,ycount,rotatedZ) == 24){
						++groundcount;
					}
					else {
						world.setBlock(rotatedX,ycount,rotatedZ,0,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ,0,0,2);
						world.setBlock(rotatedX+1,ycount,rotatedZ+1,0,0,2);
						world.setBlock(rotatedX,ycount,rotatedZ+1,0,0,2);
					}
					if (groundcount >= 2){
						ycount = 21;
					}
				}
			}
		}
    } 
    
    public int[] checkToDrawRoads(String roadKey, int chunkX, int chunkZ){
            
        if (HumanBiomes.getRoadCache().get(roadKey) != null){
	    	int[] cachedSegment = (int[]) HumanBiomes.getRoadCache().get(roadKey);
	        for (int j = 0; j < cachedSegment.length; j+=2){
	        	if (chunkX == cachedSegment[j] && chunkZ == cachedSegment[j+1]){
			    	if (j != 0 && (j+3) < cachedSegment.length){
			            int[] nearbyRoads = {
			            	cachedSegment[j-2],
			            	cachedSegment[j-1],
			            	cachedSegment[j+2],
			            	cachedSegment[j+3] };
				        return nearbyRoads;
			    	}
			    	else if ((j+3) < cachedSegment.length){
			            int[] nearbyRoads = {
			            	cachedSegment[j+2],
					        cachedSegment[j+3] };
				        return nearbyRoads;
			    	}
			    	else if (j != 0){
			            int[] nearbyRoads = {
			            	cachedSegment[j-2],
					        cachedSegment[j-1] };
				        return nearbyRoads;
		    		}
	            }
	        }
        }
        else{ System.out.println(roadKey + " is null!"); }   	
    	return null;
    }
    
    public String processRoad(int[] primaryVertex, int[] secondaryVertex, int scale, boolean pathfinding){
		int x1, z1, x2, z2;
    	int tileX1 = (primaryVertex[0] / scale) * scale;
    	int tileZ1 = (primaryVertex[1] / scale) * scale;
    	int tileX2 = (secondaryVertex[0] / scale) * scale;
    	int tileZ2 = (secondaryVertex[1] / scale) * scale;
		int dx = Math.abs(secondaryVertex[0] - primaryVertex[0]);
		int dz = Math.abs(secondaryVertex[1] - primaryVertex[1]);
        String thisSegment;
        //System.out.println(dx+","+dz);
        if ( (tileX1 < tileX2) || (tileZ1 < tileZ2) ){
        	//thisSegment = tileX1 + "" + tileZ1 + "" + tileX2 + "" + tileZ2;
        	thisSegment = primaryVertex[0] + "," + primaryVertex[1] + " to " + secondaryVertex[0] + "," + secondaryVertex[1];
			x1 = primaryVertex[0];
        	x2 = secondaryVertex[0];
        	z1 = primaryVertex[1];
        	z2 = secondaryVertex[1];
        }
        else {
        	//thisSegment = tileX2 + "" + tileZ2 + "" + tileX1 + "" + tileZ1;
        	thisSegment = secondaryVertex[0] + "," + secondaryVertex[1] + " to " + primaryVertex[0] + "," + primaryVertex[1];
			x1 = secondaryVertex[0];
			x2 = primaryVertex[0];
			z1 = secondaryVertex[1];
			z2 = primaryVertex[1];
        }

        if ( HumanBiomes.getRoadCache().containsKey(thisSegment) ) {
        	//System.out.println("This segment already cached");
        	return thisSegment;
        }
        
		int[] roadPoints;
		if (pathfinding == true && (dx+dz) > roadResolution+1)
			{ roadPoints = new int[(int)((Math.abs(Math.sqrt((dx*dx) + (dz*dz)))) / roadResolution) * 2]; }
		else
			{ roadPoints = new int[4]; }
		//System.out.println("    roadPoints.length: " +roadPoints.length+ " dx,dz: "+dx+","+dz);
		
    	roadPoints[0] = x1;
    	roadPoints[1] = z1;
    	if (pathfinding == true && (dx+dz) > roadResolution+1){
    		roadPoints[roadPoints.length-2] = x2;
    		roadPoints[roadPoints.length-1] = z2;
			roadPoints = fillRoadPoints(roadPoints);
    	}
    	else {
    		roadPoints[2] = x2;
    		roadPoints[3] = z2;
    	}
		
		HumanBiomes.getRoadCache().put(thisSegment, roadPoints);
		//System.out.println("tile:" + tileX1 + "," + tileZ1 + " vertex:" + primaryVertex[0] + "," + primaryVertex[1] + ": " + thisSegment + " seed: " + seed);
		return thisSegment;
    }
    
    public String[] processNearbyRoads(int X, int Z, int scale, boolean pathfinding){
		String[] nearbyRoads = new String[4];
		//if (X < 0){ X +=3;}
		//if (Z < 0){ Z +=3;}
		int[] thisVertex = roadVertex(X, Z, scale);
		int nearbyRoadsIndex = 0;
		
		//System.out.println("(" + ((X / scale) * scale) + ", " + ((Z / scale) * scale) + ") " + X + ", " + Z + " / " + thisVertex[0] + ", " + thisVertex[1] + "):");
		//String[] vertices = new String[4];
		
		for(int a = -1; a < 2; a++){
			for(int b = -1; b < 2; b++){
				if ( (a == 0 && b == -1) || (a == 0 && b == 1) || (a == 1 && b == 0) || (a == -1 && b == 0) ){
			        int[] otherVertex = roadVertex((thisVertex[0]+(a*scale)), (thisVertex[1]+(b*scale)), scale);
			        //String vertices1 = thisVertex[0] + "" + thisVertex[1];
			        //String vertices2 = otherVertex[0] + "" + otherVertex[1];
			        //if (vertices1.equals(vertices2)){
			        //	System.out.println(vertices1 + " equals " + vertices2 + "!");
			        //}
			        
			        int thisX = (otherVertex[0] * 16) + 8;
			        int thisZ = (otherVertex[1] * 16) + 8;
			        
			        /*
			        int thisY = world.getHeightValue(thisX, thisZ) - 1;
			        if (!(world.getBlockId(thisX, thisY, thisZ) == 35 && world.getBlockMetadata(thisX, thisY, thisZ) == 5)){
			        	for (int j = thisY; j < thisY + 40; j++){
			        		world.setBlock(thisX, j, thisZ,35,5,2);
			        	}
			        }*/
			        
			        nearbyRoads[nearbyRoadsIndex] = processRoad(thisVertex, otherVertex, scale, pathfinding);
			        nearbyRoadsIndex++;
				}
			}
		}
		
		return nearbyRoads;
    }
	

}
