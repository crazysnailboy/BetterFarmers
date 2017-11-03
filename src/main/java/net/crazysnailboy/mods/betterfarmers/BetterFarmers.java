package net.crazysnailboy.mods.betterfarmers;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.10.2")
@IFMLLoadingPlugin.TransformerExclusions("net.crazysnailboy.mods.betterfarmers")
@Mod(modid = BetterFarmers.MODID, name = BetterFarmers.MODNAME, version = BetterFarmers.VERSION, acceptedMinecraftVersions = "[1.10.2]", updateJSON = BetterFarmers.UPDATEJSON)
public class BetterFarmers implements IFMLLoadingPlugin
{

	public static final String MODID = "betterfarmers";
	public static final String MODNAME = "Better Farmers";
	public static final String VERSION = "${version}";
	public static final String UPDATEJSON = "https://raw.githubusercontent.com/crazysnailboy/BetterFarmers/master/update.json";

	public static final Logger logger = LogManager.getLogger(MODID);


	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
	}


	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { net.crazysnailboy.mods.betterfarmers.asm.BFClassTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}

}
