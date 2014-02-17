package freeradicalx.humanbiomes;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenTest extends BiomeGenBase{

	public BiomeGenTest(int par1) {
		super(par1);
        this.setBiomeName("Test Biome 1");
		this.topBlock = (byte)Block.grass.blockID;
		this.theBiomeDecorator.treesPerChunk = 0;
		this.theBiomeDecorator.flowersPerChunk = 0;
		this.theBiomeDecorator.deadBushPerChunk = 0;
		this.theBiomeDecorator.mushroomsPerChunk = 0;
		this.theBiomeDecorator.generateLakes = false;
	}
	
    public void decorate(World world, Random par2Random, int x, int z)
    {
        super.decorate(world, par2Random, x, z);
        //System.out.println("x: " + x + " z: " + z);
        int X = x / 16;
        int Z = z / 16;
        //System.out.println("X: " + X + " Z: " + Z);
        
        int[] heightSamples = {
        		world.getHeightValue(x, z),
        		world.getHeightValue(x + 8, z),
        		world.getHeightValue(x + 15, z),
        		world.getHeightValue(x, z + 8),
        		world.getHeightValue(x + 8, z + 8),
        		world.getHeightValue(x + 15, z + 8),
        		world.getHeightValue(x, z + 15),
        		world.getHeightValue(x + 8, z + 15),
        		world.getHeightValue(x + 15, z + 15),
        };
        
        Arrays.sort(heightSamples);
        int variance = heightSamples[heightSamples.length - 1] - heightSamples[0];
        int sum = 0;
        for (int i = 0; i < heightSamples.length; i++){
        	sum = sum + heightSamples[i];
        }
        int height = sum / heightSamples.length;
        
    	//int height = (world.getHeightValue(x, z) + world.getHeightValue(x + 15, z) + world.getHeightValue(x, z + 15) + world.getHeightValue(x + 15, z + 15)) / 4;

    	//Buildings
        if ( (X % 5 != 0 && Z % 6 != 0) && !(((Z+4) % 6 == 0 || (Z+3) % 6 == 0 || (Z+2) % 6 == 0) && ((X+3) % 5 == 0 || (X+2) % 5 == 0)) )
        {
        	Chunk chunk;
        	if ((X+4) % 5 == 0 && ((Z+4) % 6 == 0 || (Z+3) % 6 == 0 || (Z+2) % 6 == 0)){
        		chunk = world.getChunkFromBlockCoords(X-1, Z);
        		height = world.getHeightValue(x-1, z+8);
        	}
        	if ((X+1) % 5 == 0 && ((Z+4) % 6 == 0 || (Z+3) % 6 == 0 || (Z+2) % 6 == 0)){
        		chunk = world.getChunkFromBlockCoords(X+1, Z);
        		height = world.getHeightValue(x+16, z+8);
        	}
        	if ((Z+5) % 6 == 0 && ((X+3) % 5 == 0 || (X+2) % 5 == 0)){
        		chunk = world.getChunkFromBlockCoords(X, Z-1);
        		height = world.getHeightValue(x+8, z-1);
        	}
        	if ((Z+1) % 6 == 0 && ((X+3) % 5 == 0 || (X+2) % 5 == 0)){
        		chunk = world.getChunkFromBlockCoords(X, Z+1);
        		height = world.getHeightValue(x+8, z+16);
        	}
        	if ((X+4) % 5 == 0 && (Z+5) % 6 == 0){
        		chunk = world.getChunkFromBlockCoords(X-1, Z-1);
        		height = world.getHeightValue(x-1, z-1);
        	}
        	if ((X+4) % 5 == 0 && (Z+1) % 6 == 0){
        		chunk = world.getChunkFromBlockCoords(X-1, Z+1);
        		height = world.getHeightValue(x-1, z-16);
        	}
        	if ((X+1) % 5 == 0 && (Z+5) % 6 == 0){
        		chunk = world.getChunkFromBlockCoords(X+1, Z+1);
        		height = world.getHeightValue(x+16, z-1);
        	}
        	if ((X+1) % 5 == 0 && (Z+1) % 6 == 0){
        		chunk = world.getChunkFromBlockCoords(X+1, Z+1);
        		height = world.getHeightValue(x+16, z+16);
        	}
        	
        	for (int yCount = 0; yCount < 20; yCount++){

        		if (yCount == 0){
        			for (int xCount = 0; xCount < 16; xCount++){
        				for (int zCount = 0; zCount < 16; zCount++){
        					world.setBlock(x + xCount, height + yCount, z + zCount, Block.grass.blockID);
        					int n = height - 1;
        					while (world.getBlockId(x + xCount, n, z + zCount) == 0){
        						world.setBlock(x + xCount, n, z + zCount, Block.cobblestone.blockID);
        						n--;
        						if (height - n > 50){ break; }
        					}
        				}
        			}
        		}
        		if (yCount > 0 && yCount < 10){
        			for (int xCount = 0; xCount < 16; xCount++){
        				for (int zCount = 0; zCount < 16; zCount++){
        					if ((xCount == 2 || xCount == 13) && (zCount > 1 && zCount < 14)){
        						world.setBlock(x + xCount, height + yCount, z + zCount, Block.brick.blockID);
        					}
        					else if ((xCount > 1 && xCount < 14) && (zCount == 2 || zCount == 13)){
        						world.setBlock(x + xCount, height + yCount, z + zCount, Block.brick.blockID);
        					}
        					else {
        						world.setBlock(x + xCount, height + yCount, z + zCount, 0);
        					}
        				}
        			}
        		}
        		if (yCount == 10){
        			for (int xCount = 0; xCount < 16; xCount++){
        				for (int zCount = 0; zCount < 16; zCount++){
        					if ((xCount > 1 && xCount < 14) && (zCount > 1 && zCount < 14)){
        						world.setBlock(x + xCount, height + yCount, z + zCount, Block.brick.blockID);
        					}
        					else {
        						if (world.getBlockId(x + xCount, height + yCount, z + zCount) != 0){
        							world.setBlock(x + xCount, height + yCount, z + zCount, 0);
        						}
        					}
        				}
        			}
        		}
        		if (yCount > 10){
        			for (int xCount = 0; xCount < 16; xCount++){
        				for (int zCount = 0; zCount < 16; zCount++){
    						if (world.getBlockId(x + xCount, height + yCount, z + zCount) != 0){
    							world.setBlock(x + xCount, height + yCount, z + zCount, 0);
    						}
        				}
        			}
        		}
        	}
        }
        
        //East-west streets
        if (X % 5 == 0 && Z % 6 != 0){
        	
        	int eastHeight = (world.getHeightValue(x, z) + world.getHeightValue(x + 8, z) + world.getHeightValue(x + 15, z)) / 3;
        	int westHeight = (world.getHeightValue(x, z + 15) + world.getHeightValue(x + 8, z + 15) + world.getHeightValue(x + 15, z + 15)) / 3;
        	
        	//if (Math.abs(eastHeight - westHeight) > 16) { return; }
        	
        	float heightIncrement = (float)Math.abs(eastHeight - westHeight) / 16F;
        	if (eastHeight > westHeight) heightIncrement = -heightIncrement;
        	
			for (int zCount = 0; zCount < 16; zCount++){
				for (int xCount = 0; xCount < 16; xCount++){
					
					world.setBlock(x + xCount, (int)(eastHeight + (heightIncrement * zCount)), z + zCount, 35, 15, 3);
					
					if (xCount == 7 && zCount % 4 != 0){
						world.setBlock(x + xCount, (int)(eastHeight + (heightIncrement * zCount)), z + zCount, 35, 4, 3);
					}
					
					int n = (int)(eastHeight + (heightIncrement * zCount)) + 1;
					while (world.getBlockId(x + xCount, n, z + zCount) != 0){
						world.setBlock(x + xCount, n, z + zCount, 0);
						n++;
					}
				}
			}
        }
        
        //North-south streets
        if (X % 5 != 0 && Z % 6 == 0){
        	
        	int northHeight = (world.getHeightValue(x, z) + world.getHeightValue(x, z + 8) + world.getHeightValue(x, z + 15)) / 3;
        	int southHeight = (world.getHeightValue(x + 15, z) + world.getHeightValue(x + 15, z + 8) + world.getHeightValue(x + 15, z + 15)) / 3;
        	
        	//if (Math.abs(northHeight - southHeight) > 16) { return; }
        	
        	float heightIncrement = (float)Math.abs(northHeight - southHeight) / 16F;
        	if (northHeight > southHeight) heightIncrement = -heightIncrement;
        	
        	for (int xCount = 0; xCount < 16; xCount++){
        		for (int zCount = 0; zCount < 16; zCount++){
					
					world.setBlock(x + xCount, (int)(northHeight + (heightIncrement * xCount)), z + zCount, 35, 15, 3);
					
					if (zCount == 7 && xCount % 4 != 0){
						world.setBlock(x + xCount, (int)(northHeight + (heightIncrement * xCount)), z + zCount, 35, 4, 3);
					}
					
					int n = (int)(northHeight + (heightIncrement * xCount)) + 1;
					
					while (world.getBlockId(x + xCount, n, z + xCount) != 0){
						world.setBlock(x + xCount, n, z + zCount, 0);
						n++;
					}
				}
			}
        }
        
        //Intersections
        if (X % 5 == 0 && Z % 6 == 0){
			for (int xCount = 0; xCount < 16; xCount++){
				for (int zCount = 0; zCount < 16; zCount++){
					world.setBlock(x + xCount, height, z + zCount, 35, 15, 3);
					int n = height + 1;
					while (world.getBlockId(x + xCount, n, z + zCount) != 0){
						world.setBlock(x + xCount, n, z + zCount, 0);
						n++;
					}
				}
			}
        }
    }
	
}
