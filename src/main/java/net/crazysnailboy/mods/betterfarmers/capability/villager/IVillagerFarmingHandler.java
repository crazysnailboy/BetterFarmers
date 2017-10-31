package net.crazysnailboy.mods.betterfarmers.capability.villager;

import net.crazysnailboy.mods.betterfarmers.capability.Capabilities.IEntityAware;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;


public interface IVillagerFarmingHandler extends INBTSerializable<NBTTagCompound>, IEntityAware<EntityVillager>
{
	boolean isFarmItemInInventory();
	boolean wantsMoreFood();
	boolean canAbandonItems();
	boolean getIsWillingToMate(boolean updateFirst);
	void setIsWillingToMate(boolean isWillingToMate);
	boolean hasEnoughFoodToBreed();
	void updateEquipmentIfNeeded(EntityItem itemEntity);
}
