package net.crazysnailboy.mods.betterfarmers.entity.ai;

import net.crazysnailboy.mods.betterfarmers.BetterFarmersMod;
import net.crazysnailboy.mods.betterfarmers.capability.villager.CapabilityVillagerFarmingHandler;
import net.crazysnailboy.mods.betterfarmers.capability.villager.IVillagerFarmingHandler;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;


public class EntityAIVillagerInteractBF extends EntityAIWatchClosest2
{

	private int interactionDelay;
	private final EntityVillager villager;

	public EntityAIVillagerInteractBF(EntityVillager villager)
	{
		super(villager, EntityVillager.class, 3.0F, 0.02F);
		this.villager = villager;
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();
		if (this.canAbandonItems(this.villager) && this.closestEntity instanceof EntityVillager && this.wantsMoreFood((EntityVillager)this.closestEntity))
		{
			this.interactionDelay = 10;
		}
		else
		{
			this.interactionDelay = 0;
		}
	}

	private boolean canAbandonItems(EntityVillager villager)
	{
		if (villager.hasCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null))
		{
			IVillagerFarmingHandler capability = villager.getCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null);
			return capability.canAbandonItems();
		}
		return false;
	}

	private boolean wantsMoreFood(EntityVillager villager)
	{
		if (villager.hasCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null))
		{
			IVillagerFarmingHandler capability = villager.getCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null);
			return capability.wantsMoreFood();
		}
		return false;
	}


	@Override
	public void updateTask()
	{
		super.updateTask();

		if (this.interactionDelay > 0)
		{
			--this.interactionDelay;

			if (this.interactionDelay == 0)
			{
				BetterFarmersMod.LOGGER.info("EntityAIVillagerInteractBF");


				InventoryBasic inventorybasic = this.villager.getVillagerInventory();

				for (int i = 0; i < inventorybasic.getSizeInventory(); ++i)
				{
					ItemStack itemstack = inventorybasic.getStackInSlot(i);
					ItemStack itemstack1 = ItemStack.EMPTY;

					if (!itemstack.isEmpty())
					{
						Item item = itemstack.getItem();

						if ((item == Items.BREAD || item instanceof ItemSeedFood) && itemstack.getCount() > 3)
						{
							int l = itemstack.getCount() / 2;
							itemstack.shrink(l);
							itemstack1 = new ItemStack(item, l, itemstack.getMetadata());
						}
						else if (item == Items.WHEAT && itemstack.getCount() > 5)
						{
							int j = itemstack.getCount() / 2 / 3 * 3;
							int k = j / 3;
							itemstack.shrink(j);
							itemstack1 = new ItemStack(Items.BREAD, k, 0);
						}

						if (itemstack.isEmpty())
						{
							inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
						}
					}

					if (!itemstack1.isEmpty())
					{
						double d0 = this.villager.posY - 0.3D + (double)this.villager.getEyeHeight();
						EntityItem entityitem = new EntityItem(this.villager.world, this.villager.posX, d0, this.villager.posZ, itemstack1);
						float f = 0.3F;
						float f1 = this.villager.rotationYawHead;
						float f2 = this.villager.rotationPitch;
						entityitem.motionX = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F) * 0.3F);
						entityitem.motionZ = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F) * 0.3F);
						entityitem.motionY = (double)(-MathHelper.sin(f2 * 0.017453292F) * 0.3F + 0.1F);
						entityitem.setDefaultPickupDelay();
						this.villager.world.spawnEntity(entityitem);
						break;
					}
				}
			}
		}
	}
}