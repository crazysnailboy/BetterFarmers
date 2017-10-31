package net.crazysnailboy.mods.betterfarmers.capability.villager;

import java.lang.reflect.Field;
import net.crazysnailboy.mods.betterfarmers.util.ReflectionHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;


public class VillagerFarmingHandler implements IVillagerFarmingHandler
{

	private static final Field isWillingToMateField = ReflectionHelper.getDeclaredField(EntityVillager.class, "isWillingToMate", "field_175565_bs");
	private EntityVillager villager;

	@Override
	public void setEntity(EntityVillager villager)
	{
		this.villager = villager;
	}

	@Override
	public EntityVillager getEntity()
	{
		return villager;
	}

	private boolean isFarmer()
	{
		return this.villager.getProfessionForge().getRegistryName().equals(new ResourceLocation("minecraft:farmer"));
	}


	@Override
	public boolean isFarmItemInInventory()
	{
		boolean isFarmItemInInventory = false;
		for (int i = 0; i < this.villager.getVillagerInventory().getSizeInventory(); ++i)
		{
			ItemStack itemstack = this.villager.getVillagerInventory().getStackInSlot(i);
			if (!itemstack.isEmpty() && (itemstack.getItem() instanceof ItemSeeds || itemstack.getItem() instanceof ItemSeedFood))
			{
				isFarmItemInInventory = true;
				break;
			}
		}
//		BetterFarmersMod.LOGGER.info("VillagerFarmingHandler.isFarmItemInInventory = " + isFarmItemInInventory + " (" + this.villager.getProfessionForge().getRegistryName().toString() + ")");
		return isFarmItemInInventory;
	}

	@Override
	public boolean wantsMoreFood()
	{
		boolean wantsMoreFood = false;
		if (this.isFarmer())
		{
			wantsMoreFood = !this.hasEnoughItems(5);
		}
		else
		{
			wantsMoreFood = !this.hasEnoughItems(1);
		}
//		BetterFarmersMod.LOGGER.info("VillagerFarmingHandler.wantsMoreFood = " + wantsMoreFood + " (" + this.villager.getProfessionForge().getRegistryName().toString() + ")");
		return wantsMoreFood;
	}

	@Override
	public boolean canAbandonItems()
	{
		boolean canAbandonItems = this.hasEnoughItems(2);
//		BetterFarmersMod.LOGGER.info("VillagerFarmingHandler.canAbandonItems = " + canAbandonItems + " (" + this.villager.getProfessionForge().getRegistryName().toString() + ")");
		return canAbandonItems;
	}

	@Override
	public boolean hasEnoughFoodToBreed()
	{
		boolean hasEnoughFoodToBreed = this.hasEnoughItems(1);
//		BetterFarmersMod.LOGGER.info("VillagerFarmingHandler.hasEnoughFoodToBreed = " + hasEnoughFoodToBreed + " (" + this.villager.getProfessionForge().getRegistryName().toString() + ")");
		return hasEnoughFoodToBreed;
	}


	private boolean hasEnoughItems(int multiplier)
	{
		for (int i = 0; i < this.villager.getVillagerInventory().getSizeInventory(); ++i)
		{
			ItemStack itemstack = this.villager.getVillagerInventory().getStackInSlot(i);
			if (!itemstack.isEmpty())
			{
				if (itemstack.getItem() == Items.BREAD && itemstack.getCount() >= 3 * multiplier || itemstack.getItem() instanceof ItemSeedFood && itemstack.getCount() >= 12 * multiplier)
				{
					return true;
				}
				if (this.isFarmer() && itemstack.getItem() == Items.WHEAT && itemstack.getCount() >= 9 * multiplier)
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean getIsWillingToMate(boolean updateFirst)
	{
		boolean isWillingToMate = ReflectionHelper.getFieldValue(isWillingToMateField, villager);
		if (!isWillingToMate && updateFirst && this.hasEnoughFoodToBreed())
		{
			boolean flag = false;

			for (int i = 0; i < this.villager.getVillagerInventory().getSizeInventory(); ++i)
			{
				ItemStack itemstack = this.villager.getVillagerInventory().getStackInSlot(i);

				if (!itemstack.isEmpty())
				{
					if (itemstack.getItem() == Items.BREAD && itemstack.getCount() >= 3)
					{
						flag = true;
						this.villager.getVillagerInventory().decrStackSize(i, 3);
					}
					else if ((itemstack.getItem() instanceof ItemSeedFood) && itemstack.getCount() >= 12)
					{
						flag = true;
						this.villager.getVillagerInventory().decrStackSize(i, 12);
					}
				}

				if (flag)
				{
					this.villager.world.setEntityState(this.villager, (byte)18);
					isWillingToMate = true;
					this.setIsWillingToMate(isWillingToMate);
					break;
				}
			}
		}

		return isWillingToMate;
	}

	@Override
	public void setIsWillingToMate(boolean isWillingToMate)
	{
		this.villager.setIsWillingToMate(isWillingToMate);
	}

	@Override
	public void updateEquipmentIfNeeded(EntityItem itemEntity)
	{
		ItemStack itemstack = itemEntity.getItem();
		Item item = itemstack.getItem();

		if (this.canVillagerPickupItem(item))
		{
			ItemStack itemstack1 = this.villager.getVillagerInventory().addItem(itemstack);
			if (itemstack1.isEmpty())
			{
				itemEntity.setDead();
			}
			else
			{
				itemstack.setCount(itemstack1.getCount());
			}
		}
	}

	private boolean canVillagerPickupItem(Item item)
	{
		return (item == Items.BREAD || item == Items.WHEAT || item instanceof ItemSeeds || item instanceof ItemSeedFood);
	}


	@Override
	public NBTTagCompound serializeNBT()
	{
		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound)
	{
	}

}
