package net.crazysnailboy.mods.betterfarmers.capability;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;


public class Capabilities
{

	public static class Provider<V> implements ICapabilitySerializable<NBTBase>
	{
		protected final Capability<V> capability;
		protected final EnumFacing facing;

		protected V instance;

		public Provider(Capability<V> capability, @Nullable EnumFacing facing)
		{
			this.capability = capability;
			this.facing = facing;
			this.instance = capability.getDefaultInstance();
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing)
		{
			return this.capability == capability;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing)
		{
			return this.capability == capability ? this.capability.cast(instance) : null;
		}

		@Override
		public NBTBase serializeNBT()
		{
			return this.capability.getStorage().writeNBT(this.capability, this.instance, null);
		}

		@Override
		public void deserializeNBT(NBTBase nbt)
		{
			this.capability.getStorage().readNBT(this.capability, this.instance, null, nbt);
		}
	}


	public interface IEntityAware<T extends Entity>
	{
		void setEntity(T entity);
		T getEntity();
	}

	public static class EntityAwareProvider<V extends IEntityAware, E extends Entity> extends Provider<V>
	{
		public EntityAwareProvider(Capability<V> capability, EnumFacing facing, E entity)
		{
			super(capability, facing);
			this.instance.setEntity(entity);
		}
	}


	public static class Storage<V extends INBTSerializable> implements Capability.IStorage<V>
	{
		@Override
		public NBTBase writeNBT(Capability<V> capability, V instance, EnumFacing side)
		{
			return ((INBTSerializable)instance).serializeNBT();
		}

		@Override
		public void readNBT(Capability<V> capability, V instance, EnumFacing side, NBTBase nbt)
		{
			instance.deserializeNBT((NBTTagCompound)nbt);
		}
	}

}
