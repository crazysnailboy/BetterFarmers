package net.crazysnailboy.mods.betterfarmers.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class EntityAIHarvestNetherwart extends EntityAIMoveToBlock
{
	private final EntityVillager theVillager;
	private boolean hasNetherWart;
	private boolean wantsToReapStuff;
	private int currentTask;

	public EntityAIHarvestNetherwart(EntityVillager theVillagerIn, double speedIn)
	{
		super(theVillagerIn, speedIn, 16);
		this.theVillager = theVillagerIn;
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.runDelay <= 0)
		{
			if (!this.theVillager.world.getGameRules().getBoolean("mobGriefing"))
			{
				return false;
			}

			this.currentTask = -1;
			this.hasNetherWart = this.isNetherwartInInventory(theVillager);
			this.wantsToReapStuff = true; //this.theVillager.wantsMoreFood();
		}

		return super.shouldExecute();
	}

	@Override
	public boolean continueExecuting()
	{
		return this.currentTask >= 0 && super.continueExecuting();
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();
	}

	@Override
	public void resetTask()
	{
		super.resetTask();
	}

	@Override
	public void updateTask()
	{
		super.updateTask();
		this.theVillager.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.theVillager.getVerticalFaceSpeed());

		if (this.getIsAboveDestination())
		{
			World world = this.theVillager.world;
			BlockPos blockpos = this.destinationBlock.up();
			IBlockState iblockstate = world.getBlockState(blockpos);
			Block block = iblockstate.getBlock();


			if (this.currentTask == 0 && block instanceof BlockNetherWart && isMaxAge(iblockstate)) // if (this.currentTask == 0 && block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate))
			{
				world.destroyBlock(blockpos, true);
			}
			else if (this.currentTask == 1 && iblockstate.getMaterial() == Material.AIR)
			{
				InventoryBasic inventorybasic = this.theVillager.getVillagerInventory();

				for (int i = 0; i < inventorybasic.getSizeInventory(); ++i)
				{
					ItemStack itemstack = inventorybasic.getStackInSlot(i);
					boolean flag = false;


					if (itemstack != null)
					{
						if (itemstack.getItem() == Items.NETHER_WART)
						{
							world.setBlockState(blockpos, Blocks.NETHER_WART.getDefaultState(), 3);
							flag = true;
						}
					}

					if (flag)
					{
						--itemstack.stackSize;

						if (itemstack.stackSize <= 0)
						{
							inventorybasic.setInventorySlotContents(i, (ItemStack)null);
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

		if (block == Blocks.SOUL_SAND)
		{
			pos = pos.up();
			IBlockState iblockstate = worldIn.getBlockState(pos);
			block = iblockstate.getBlock();

			if (block instanceof BlockNetherWart && isMaxAge(iblockstate) && this.wantsToReapStuff && (this.currentTask == 0 || this.currentTask < 0))
			{
				this.currentTask = 0;
				return true;
			}

			if (iblockstate.getMaterial() == Material.AIR && this.hasNetherWart && (this.currentTask == 1 || this.currentTask < 0))
			{
				this.currentTask = 1;
				return true;
			}
		}

		return false;
	}


	private boolean isNetherwartInInventory(EntityVillager villager)
	{
		for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i)
		{
			ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);
			if (itemstack != null && itemstack.getItem() == Items.NETHER_WART)
			{
				return true;
			}
		}
		return false;
	}

	private boolean isMaxAge(IBlockState state)
	{
        return ((Integer)state.getValue(BlockNetherWart.AGE)).intValue() >= 3;
	}


}
