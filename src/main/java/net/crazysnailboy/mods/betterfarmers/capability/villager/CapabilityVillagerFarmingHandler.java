package net.crazysnailboy.mods.betterfarmers.capability.villager;

import net.crazysnailboy.mods.betterfarmers.BetterFarmersMod;
import net.crazysnailboy.mods.betterfarmers.capability.Capabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@EventBusSubscriber
public class CapabilityVillagerFarmingHandler
{

	@CapabilityInject(IVillagerFarmingHandler.class)
	public static final Capability<IVillagerFarmingHandler> VILLAGER_FARMING_CAPABILITY = null;

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IVillagerFarmingHandler.class, new Capabilities.Storage<>(), () -> new VillagerFarmingHandler());
	}

	@SubscribeEvent
	public static void onAttachCapability(final AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof EntityVillager)
		{
			BetterFarmersMod.LOGGER.info("onAttachCapability");

			EntityVillager villager = (EntityVillager)event.getObject();
			event.addCapability(new ResourceLocation(BetterFarmersMod.MODID, "VillagerFarming"), new Capabilities.EntityAwareProvider(VILLAGER_FARMING_CAPABILITY, null, villager));
		}
	}

}
