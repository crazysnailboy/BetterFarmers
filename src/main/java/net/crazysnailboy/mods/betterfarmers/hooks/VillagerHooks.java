package net.crazysnailboy.mods.betterfarmers.hooks;

import java.lang.reflect.Field;
import net.crazysnailboy.mods.betterfarmers.util.ReflectionHelper;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class VillagerHooks
{

	private static final Field EntityVillager_isWillingToMate = ReflectionHelper.getDeclaredField(EntityVillager.class, "isWillingToMate", "field_175565_bs");
	private static final Field EntityVillager_areAdditionalTasksSet = ReflectionHelper.getDeclaredField(EntityVillager.class, "areAdditionalTasksSet", "field_175564_by");


	public static boolean canVillagerPickupItem(Item item)
	{
		return (item == Items.BREAD || item == Items.WHEAT || item instanceof ItemSeeds || item instanceof ItemSeedFood);
	}

	public static boolean getIsWillingToMate(EntityVillager villager, boolean updateFirst)
	{
		boolean isWillingToMate = ReflectionHelper.getFieldValue(EntityVillager_isWillingToMate, villager);
		if (!isWillingToMate && updateFirst && villager.hasEnoughFoodToBreed())
		{
			boolean flag = false;
			for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i)
			{
				ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);
				if (itemstack != null)
				{
					if (itemstack.getItem() == Items.BREAD && itemstack.stackSize >= 3)
					{
						flag = true;
						villager.getVillagerInventory().decrStackSize(i, 3);
					}
					else if ((itemstack.getItem() instanceof ItemSeedFood) && itemstack.stackSize >= 12)
					{
						flag = true;
						villager.getVillagerInventory().decrStackSize(i, 12);
					}
				}
				if (flag)
				{
					villager.world.setEntityState(villager, (byte)18);
					isWillingToMate = true;
					break;
				}
			}
		}
		ReflectionHelper.setFieldValue(EntityVillager_isWillingToMate, villager, isWillingToMate);
		return isWillingToMate;
	}

	public static boolean hasEnoughItems(EntityVillager villager, int multiplier)
	{
		boolean isFarmer = VillagerHooks.isFarmer(villager.getProfessionForge());
		for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i)
		{
			ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);
			if (itemstack != null)
			{
				if (itemstack.getItem() == Items.BREAD && itemstack.stackSize >= 3 * multiplier || itemstack.getItem() instanceof ItemSeedFood && itemstack.stackSize >= 12 * multiplier)
				{
					return true;
				}
				if (isFarmer && itemstack.getItem() == Items.WHEAT && itemstack.stackSize >= 9 * multiplier)
				{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isFarmer(VillagerProfession profession)
	{
		return profession.getRegistryName().toString().equals("minecraft:farmer");
	}

	public static boolean isFarmItemInInventory(EntityVillager villager)
	{
		for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i)
		{
			ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);
			if (itemstack != null && (itemstack.getItem() instanceof ItemSeeds || itemstack.getItem() instanceof ItemSeedFood))
			{
				return true;
			}
		}
		return false;
	}


	public static void onGrowingAdult(EntityVillager villager)
	{
		if (isFarmer(villager.getProfessionForge()))
		{
			villager.tasks.addTask(8, new net.crazysnailboy.mods.betterfarmers.entity.ai.EntityAIHarvestFarmland(villager, 0.6D));
		}
        else if (villager.getProfessionForge().getRegistryName().toString().equals("minecraft:priest"))
        {
        	villager.tasks.addTask(8, new net.crazysnailboy.mods.betterfarmers.entity.ai.EntityAIHarvestNetherwart(villager, 0.6D));
        }
	}

	public static void setAdditionalAItasks(EntityVillager villager)
	{
		boolean areAdditionalTasksSet = ReflectionHelper.getFieldValue(EntityVillager_areAdditionalTasksSet, villager);
        if (!areAdditionalTasksSet)
        {
        	ReflectionHelper.setFieldValue(EntityVillager_areAdditionalTasksSet, villager, true);
            if (villager.isChild())
            {
            	villager.tasks.addTask(8, new EntityAIPlay(villager, 0.32D));
            }
            else if (isFarmer(villager.getProfessionForge()))
            {
            	villager.tasks.addTask(6, new net.crazysnailboy.mods.betterfarmers.entity.ai.EntityAIHarvestFarmland(villager, 0.6D));
            }
            else if (villager.getProfessionForge().getRegistryName().toString().equals("minecraft:priest"))
            {
    			villager.tasks.addTask(6, new net.crazysnailboy.mods.betterfarmers.entity.ai.EntityAIHarvestNetherwart(villager, 0.6D));
            }
        }
	}


	public static boolean wantsMoreFood(EntityVillager villager)
	{
		boolean isFarmer = VillagerHooks.isFarmer(villager.getProfessionForge());
		return (!VillagerHooks.hasEnoughItems(villager, isFarmer ? 5 : 1));
	}

}
