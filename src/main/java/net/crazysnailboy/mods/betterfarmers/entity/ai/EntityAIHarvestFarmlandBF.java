package net.crazysnailboy.mods.betterfarmers.entity.ai;

import net.crazysnailboy.mods.betterfarmers.BetterFarmersMod;
import net.crazysnailboy.mods.betterfarmers.capability.villager.CapabilityVillagerFarmingHandler;
import net.crazysnailboy.mods.betterfarmers.capability.villager.IVillagerFarmingHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;


public class EntityAIHarvestFarmlandBF extends EntityAIMoveToBlock
{

	private final EntityVillager villager;
	private boolean hasFarmItem;
	private boolean wantsToReapStuff;
	private int currentTask;

	public EntityAIHarvestFarmlandBF(EntityVillager villager, double speed)
	{
		super(villager, speed, 16);
		this.villager = villager;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.runDelay <= 0)
		{
			if (!this.villager.world.getGameRules().getBoolean("mobGriefing"))
			{
				return false;
			}

			if (this.villager.hasCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null))
			{
				IVillagerFarmingHandler capability = this.villager.getCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null);

				this.currentTask = -1;
				this.hasFarmItem = capability.isFarmItemInInventory();
				this.wantsToReapStuff = capability.wantsMoreFood();
			}
		}

		boolean shouldExecute = super.shouldExecute();
		if (shouldExecute)
		{
			BetterFarmersMod.LOGGER.info("EntityAIHarvestFarmlandBF.shouldExecute = " + shouldExecute);
		}
		return shouldExecute;
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return this.currentTask >= 0 && super.shouldContinueExecuting();
	}

	@Override
	public void updateTask()
	{
		super.updateTask();
		this.villager.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.villager.getVerticalFaceSpeed());

		if (this.getIsAboveDestination())
		{
			World world = this.villager.world;
			BlockPos blockpos = this.destinationBlock.up();
			IBlockState iblockstate = world.getBlockState(blockpos);
			Block block = iblockstate.getBlock();

			if (this.currentTask == 0 && block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate))
			{
				world.destroyBlock(blockpos, true);
			}
			else if (this.currentTask == 1 && iblockstate.getMaterial() == Material.AIR)
			{
				InventoryBasic inventorybasic = this.villager.getVillagerInventory();
				for (int i = 0; i < inventorybasic.getSizeInventory(); ++i)
				{
					ItemStack itemstack = inventorybasic.getStackInSlot(i);
					boolean flag = false;

					if (!itemstack.isEmpty() && (itemstack.getItem() instanceof IPlantable))
					{
						IBlockState blockState = ((IPlantable)itemstack.getItem()).getPlant(null, null);
						if (blockState != null)
						{
							world.setBlockState(blockpos, blockState, 3);
							flag = true;
						}
					}

					if (flag)
					{
						itemstack.shrink(1);
						if (itemstack.isEmpty())
						{
							inventorybasic.setInventorySlotContents(i, ItemStack.EMPTY);
						}
						break;
					}
				}
			}

			this.currentTask = -1;
			this.runDelay = 10;
		}
	}

	@Override
	protected boolean shouldMoveTo(World worldIn, BlockPos pos)
	{
		Block block = worldIn.getBlockState(pos).getBlock();

		if (block == Blocks.FARMLAND)
		{
			pos = pos.up();
			IBlockState iblockstate = worldIn.getBlockState(pos);
			block = iblockstate.getBlock();

			if (block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate) && this.wantsToReapStuff && (this.currentTask == 0 || this.currentTask < 0))
			{
				this.currentTask = 0;
				return true;
			}

			if (iblockstate.getMaterial() == Material.AIR && this.hasFarmItem && (this.currentTask == 1 || this.currentTask < 0))
			{
				this.currentTask = 1;
				return true;
			}
		}

		return false;
	}

}