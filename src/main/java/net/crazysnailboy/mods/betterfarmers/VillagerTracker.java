package net.crazysnailboy.mods.betterfarmers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import net.crazysnailboy.mods.betterfarmers.capability.villager.CapabilityVillagerFarmingHandler;
import net.crazysnailboy.mods.betterfarmers.capability.villager.IVillagerFarmingHandler;
import net.crazysnailboy.mods.betterfarmers.entity.ai.EntityAIHarvestFarmlandBF;
import net.crazysnailboy.mods.betterfarmers.entity.ai.EntityAIVillagerInteractBF;
import net.crazysnailboy.mods.betterfarmers.entity.ai.EntityAIVillagerMateBF;
import net.minecraft.entity.ai.EntityAIHarvestFarmland;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;


@EventBusSubscriber
public class VillagerTracker
{

	private static HashMap<UUID, EntityVillager> children = new HashMap<UUID, EntityVillager>();


	private static void addChild(EntityVillager villager)
	{
//		BetterFarmersMod.LOGGER.info("addChild");

		UUID uuid = villager.getUniqueID();
		if (!children.containsKey(uuid)) children.put(uuid, villager);
	}

	private static void removeChild(EntityVillager villager)
	{
//		BetterFarmersMod.LOGGER.info("removeChild");

		UUID uuid = villager.getUniqueID();
		children.remove(uuid);
	}


	@SubscribeEvent
	public static void onBabyEntitySpawn(BabyEntitySpawnEvent event)
	{
		if (event.getChild() instanceof EntityVillager)
		{
//			BetterFarmersMod.LOGGER.info("onBabyEntitySpawn");

			EntityVillager villager = (EntityVillager)event.getChild();
			addChild(villager);
		}
	}

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (!event.getWorld().isRemote && event.getEntity() instanceof EntityVillager)
		{
//			BetterFarmersMod.LOGGER.info("onEntityJoinWorld");

			EntityVillager villager = (EntityVillager)event.getEntity();
			if (villager.isChild()) addChild(villager);
			initVillagerAI(villager);
		}
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		if (event.phase == Phase.END)
		{
			if (!children.isEmpty())
			{
				for (EntityVillager child : children.values())
				{
					if (child != null && !child.isChild())
					{
//						BetterFarmersMod.LOGGER.info("onServerTick");

						initVillagerAI(child);
						removeChild(child);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingEntityUpdate(LivingUpdateEvent event)
	{
		if (event.getEntityLiving() instanceof EntityVillager)
		{
			EntityVillager villager = (EntityVillager)event.getEntityLiving();
			World world = villager.getWorld();

			if (!world.isRemote && villager.canPickUpLoot() && villager.isEntityAlive() && world.getGameRules().getBoolean("mobGriefing"))
			{
				if (villager.hasCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null))
				{
					IVillagerFarmingHandler capability = villager.getCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null);
					for (EntityItem entityitem : world.getEntitiesWithinAABB(EntityItem.class, villager.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D)))
					{
						if (!entityitem.isDead && !entityitem.getItem().isEmpty() && !entityitem.cannotPickup())
						{
							capability.updateEquipmentIfNeeded(entityitem);
						}
					}
				}
			}
		}
	}


	private static void initVillagerAI(EntityVillager villager)
	{
//		BetterFarmersMod.LOGGER.info("initVillagerAI :: profession=" + villager.getProfessionForge().getRegistryName().toString());

//		int removed = 0; int added = 0;

		Iterator<EntityAITasks.EntityAITaskEntry> iterator = villager.tasks.taskEntries.iterator();
		while (iterator.hasNext())
		{
			EntityAITasks.EntityAITaskEntry taskEntry = iterator.next();
			if (taskEntry.action instanceof EntityAIHarvestFarmland || taskEntry.action instanceof EntityAIVillagerInteract || taskEntry.action instanceof EntityAIVillagerMate)
			{
				iterator.remove();
//            	removed++;
			}
		}

//		BetterFarmersMod.LOGGER.info("initVillagerAI :: removed=" + removed);


		villager.tasks.addTask(6, new EntityAIVillagerMateBF(villager));
//        added++;
		villager.tasks.addTask(9, new EntityAIVillagerInteractBF(villager));
//        added++;

		if (villager.getProfessionForge().getRegistryName().equals(new ResourceLocation("minecraft:farmer")) && !villager.isChild())
		{
			villager.tasks.addTask(6, new EntityAIHarvestFarmlandBF(villager, 0.6D));
//        	added++;
		}
//		BetterFarmersMod.LOGGER.info("initVillagerAI :: added=" + added);

	}

}
