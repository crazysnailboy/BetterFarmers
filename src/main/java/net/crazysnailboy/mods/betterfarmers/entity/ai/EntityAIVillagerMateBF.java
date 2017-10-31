package net.crazysnailboy.mods.betterfarmers.entity.ai;

import net.crazysnailboy.mods.betterfarmers.capability.villager.CapabilityVillagerFarmingHandler;
import net.crazysnailboy.mods.betterfarmers.capability.villager.IVillagerFarmingHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;


public class EntityAIVillagerMateBF extends EntityAIBase
{

	private final EntityVillager villager;
	private EntityVillager mate;
	private final World world;
	private int matingTimeout;
	Village village;

	public EntityAIVillagerMateBF(EntityVillager villager)
	{
		this.villager = villager;
		this.world = villager.world;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute()
	{
		if (this.villager.getGrowingAge() != 0)
		{
			return false;
		}
		else if (this.villager.getRNG().nextInt(500) != 0)
		{
			return false;
		}
		else
		{
			this.village = this.world.getVillageCollection().getNearestVillage(new BlockPos(this.villager), 0);
			if (this.village == null)
			{
				return false;
			}
			else if (this.checkSufficientDoorsPresentForNewVillager() && this.getIsWillingToMate(this.villager, true))
			{
				Entity entity = this.world.findNearestEntityWithinAABB(EntityVillager.class, this.villager.getEntityBoundingBox().grow(8.0D, 3.0D, 8.0D), this.villager);
				if (entity == null)
				{
					return false;
				}
				else
				{
					this.mate = (EntityVillager)entity;
					return this.mate.getGrowingAge() == 0 && this.getIsWillingToMate(this.mate, true);
				}
			}
			else
			{
				return false;
			}
		}
	}

	private boolean getIsWillingToMate(EntityVillager villager, boolean updateFirst)
	{
		if (villager.hasCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null))
		{
			IVillagerFarmingHandler capability = villager.getCapability(CapabilityVillagerFarmingHandler.VILLAGER_FARMING_CAPABILITY, null);
			return capability.getIsWillingToMate(updateFirst);
		}
		return false;
	}

	@Override
	public void startExecuting()
	{
		this.matingTimeout = 300;
		this.villager.setMating(true);
	}

	@Override
	public void resetTask()
	{
		this.village = null;
		this.mate = null;
		this.villager.setMating(false);
	}

	@Override
	public boolean shouldContinueExecuting()
	{
		return this.matingTimeout >= 0 && this.checkSufficientDoorsPresentForNewVillager() && this.villager.getGrowingAge() == 0 && this.getIsWillingToMate(this.villager, false);
	}

	@Override
	public void updateTask()
	{
		--this.matingTimeout;
		this.villager.getLookHelper().setLookPositionWithEntity(this.mate, 10.0F, 30.0F);

		if (this.villager.getDistanceSqToEntity(this.mate) > 2.25D)
		{
			this.villager.getNavigator().tryMoveToEntityLiving(this.mate, 0.25D);
		}
		else if (this.matingTimeout == 0 && this.mate.isMating())
		{
			this.giveBirth();
		}

		if (this.villager.getRNG().nextInt(35) == 0)
		{
			this.world.setEntityState(this.villager, (byte)12);
		}
	}

	private boolean checkSufficientDoorsPresentForNewVillager()
	{
		if (!this.village.isMatingSeason())
		{
			return false;
		}
		else
		{
			int i = (int)((double)((float)this.village.getNumVillageDoors()) * 0.35D);
			return this.village.getNumVillagers() < i;
		}
	}

	private void giveBirth()
	{
		net.minecraft.entity.EntityAgeable entityvillager = this.villager.createChild(this.mate);
		this.mate.setGrowingAge(6000);
		this.villager.setGrowingAge(6000);
		this.mate.setIsWillingToMate(false);
		this.villager.setIsWillingToMate(false);

		final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(villager, mate, entityvillager);
		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event) || event.getChild() == null)
		{
			return;
		}
		entityvillager = event.getChild();
		entityvillager.setGrowingAge(-24000);
		entityvillager.setLocationAndAngles(this.villager.posX, this.villager.posY, this.villager.posZ, 0.0F, 0.0F);
		this.world.spawnEntity(entityvillager);
		this.world.setEntityState(entityvillager, (byte)12);
	}
}