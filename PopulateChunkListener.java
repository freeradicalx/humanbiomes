package freeradicalx.humanbiomes;

import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;

public class PopulateChunkListener
{

	@ForgeSubscribe	//This method works
	public void onPopulateEvent(Populate event)
	{
		if (event.type == EventType.LAKE || event.type == EventType.LAVA || event.type == EventType.DUNGEON){
			event.setResult(Result.DENY);
		}		
	}
	
	@ForgeSubscribe //This method doesn't seem to be working in ATG
	public void onInitMapGenEvent(InitMapGenEvent event)
	{
		event.setResult(Result.DENY);		
	}
	
}
