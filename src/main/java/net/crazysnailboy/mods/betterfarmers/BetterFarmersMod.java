package net.crazysnailboy.mods.betterfarmers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.crazysnailboy.mods.betterfarmers.capability.villager.CapabilityVillagerFarmingHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod(modid = BetterFarmersMod.MODID, name = BetterFarmersMod.NAME, version = BetterFarmersMod.VERSION, updateJSON = BetterFarmersMod.UPDATEJSON)
public class BetterFarmersMod
{

	public static final String MODID = "betterfarmers";
	public static final String NAME = "Better Farmers";
	public static final String VERSION = "${version}";
	public static final String UPDATEJSON = "https://raw.githubusercontent.com/crazysnailboy/BetterFarmers/master/update.json";

	public static final Logger LOGGER = LogManager.getLogger(MODID);


	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CapabilityVillagerFarmingHandler.register();
	}

}
