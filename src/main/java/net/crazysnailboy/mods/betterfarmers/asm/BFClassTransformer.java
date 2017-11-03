package net.crazysnailboy.mods.betterfarmers.asm;

import java.util.Arrays;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import net.crazysnailboy.mods.betterfarmers.BetterFarmers;
import net.minecraft.launchwrapper.IClassTransformer;

public class BFClassTransformer implements IClassTransformer
{

	private static final String[] classesBeingTransformed = new String[] { "net.minecraft.entity.passive.EntityVillager" };


	@Override
	public byte[] transform(String name, String transformedName, byte[] classBeingTransformed)
	{
		boolean isObfuscated = !name.equals(transformedName);
		int index = Arrays.asList(classesBeingTransformed).indexOf(transformedName);
		return index != -1 ? transform(index, classBeingTransformed, isObfuscated) : classBeingTransformed;
	}

	private static byte[] transform(int index, byte[] classBeingTransformed, boolean isObfuscated)
	{
		try
		{
			ClassNode classNode = new ClassNode();
			ClassReader classReader = new ClassReader(classBeingTransformed);
			classReader.accept(classNode, 0);

			switch(index)
			{
				case 0:
					transformVillager(classNode, isObfuscated);
					break;
			}

			ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			classNode.accept(classWriter);
			return classWriter.toByteArray();
		}
		catch (Exception ex)
		{
			BetterFarmers.logger.catching(ex);
		}
		return classBeingTransformed;
	}

	private static void transformVillager(ClassNode classNode, boolean isObfuscated)
	{
		BetterFarmers.logger.info("----------");

		Pair<String,String> canVillagerPickupItem = ImmutablePair.<String,String>of(!isObfuscated ? "canVillagerPickupItem" : "a", !isObfuscated ? "(Lnet/minecraft/item/Item;)Z" : "(Ladx;)Z");
		Pair<String,String> getIsWillingToMate = ImmutablePair.<String,String>of(!isObfuscated ? "getIsWillingToMate" : "r", "(Z)Z");
		Pair<String,String> hasEnoughItems = ImmutablePair.<String,String>of(!isObfuscated ? "hasEnoughItems" : "m", "(I)Z");
		Pair<String,String> initEntityAI = ImmutablePair.<String,String>of(!isObfuscated ? "initEntityAI" : "r", "()V");
		Pair<String,String> isFarmItemInInventory = ImmutablePair.<String,String>of(!isObfuscated ? "isFarmItemInInventory" : "dn", "()Z");
		Pair<String,String> onGrowingAdult = ImmutablePair.<String,String>of(!isObfuscated ? "onGrowingAdult" : "o", "()V");
		Pair<String,String> setAdditionalAItasks = ImmutablePair.<String,String>of(!isObfuscated ? "setAdditionalAItasks" : "do", "()V");
		Pair<String,String> wantsMoreFood = ImmutablePair.<String,String>of(!isObfuscated ? "wantsMoreFood" : "dm", "()Z");

		for (MethodNode method : classNode.methods)
		{
			if (method.name.equals(canVillagerPickupItem.getLeft()) && method.desc.equals(canVillagerPickupItem.getRight()))
			{
				transformMethod_canVillagerPickupItem(method, isObfuscated);
			}
			else if (method.name.equals(getIsWillingToMate.getLeft()) && method.desc.equals(getIsWillingToMate.getRight()))
			{
				transformMethod_getIsWillingToMate(method, isObfuscated);
			}
			else if (method.name.equals(hasEnoughItems.getLeft()) && method.desc.equals(hasEnoughItems.getRight()))
			{
				transformMethod_hasEnoughItems(method, isObfuscated);
			}
			else if (method.name.equals(initEntityAI.getLeft()) && method.desc.equals(initEntityAI.getRight()))
			{
				transformMethod_initEntityAI(method, isObfuscated);
			}
			else if (method.name.equals(isFarmItemInInventory.getLeft()) && method.desc.equals(isFarmItemInInventory.getRight()))
			{
				transformMethod_isFarmItemInInventory(method, isObfuscated);
			}
			else if (method.name.equals(onGrowingAdult.getLeft()) && method.desc.equals(onGrowingAdult.getRight()))
			{
				transformMethod_onGrowingAdult(method, isObfuscated);
			}
			else if (method.name.equals(setAdditionalAItasks.getLeft()) && method.desc.equals(setAdditionalAItasks.getRight()))
			{
				transformMethod_setAdditionalAItasks(method, isObfuscated);
			}
			else if (method.name.equals(wantsMoreFood.getLeft()) && method.desc.equals(wantsMoreFood.getRight()))
			{
				transformMethod_wantsMoreFood(method, isObfuscated);
			}
		}

		BetterFarmers.logger.info("----------");
	}


	private static void transformMethod_canVillagerPickupItem(MethodNode method, boolean isObfuscated)
	{
		BetterFarmers.logger.info("transformMethod_canVillagerPickupItem");

		/*
		Replacing:
			return itemIn == Items.BREAD || itemIn == Items.POTATO || itemIn == Items.CARROT || itemIn == Items.WHEAT || itemIn == Items.WHEAT_SEEDS || itemIn == Items.BEETROOT || itemIn == Items.BEETROOT_SEEDS;
		With:
			return VillagerHooks.canVillagerPickupItem(itemIn);
		 */

		String EntityVillager = (!isObfuscated ? "net/minecraft/entity/passive/EntityVillager" : "zn");
		String Item = (!isObfuscated ? "net/minecraft/item/Item" : "adx");
		String itemIn = (!isObfuscated ? "itemIn" : "p_175558_1_");

		method.instructions.clear();

		MethodVisitor mv = (MethodVisitor)method;
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/crazysnailboy/mods/betterfarmers/hooks/VillagerHooks", "canVillagerPickupItem", "(L" + Item + ";)Z", false);
		mv.visitInsn(Opcodes.IRETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + EntityVillager + ";", null, l0, l1, 0);
		mv.visitLocalVariable(itemIn, "L" + Item + ";", null, l0, l1, 1);
		mv.visitMaxs(1, 2);
		mv.visitEnd();
	}

	private static void transformMethod_getIsWillingToMate(MethodNode method, boolean isObfuscated)
	{
		BetterFarmers.logger.info("transformMethod_getIsWillingToMate");

		/*
		Replacing:
		With:
			return VillagerHooks.getIsWillingToMate(this, updateFirst);
		 */

		String EntityVillager = (!isObfuscated ? "net/minecraft/entity/passive/EntityVillager" : "zn");
		String updateFirst = (!isObfuscated ? "updateFirst" : "p_175550_1_");

		method.instructions.clear();

		MethodVisitor mv = (MethodVisitor)method;
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/crazysnailboy/mods/betterfarmers/hooks/VillagerHooks", "getIsWillingToMate", "(L" + EntityVillager + ";Z)Z", false);
		mv.visitInsn(Opcodes.IRETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + EntityVillager + ";", null, l0, l1, 0);
		mv.visitLocalVariable(updateFirst, "Z", null, l0, l1, 1);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	private static void transformMethod_hasEnoughItems(MethodNode method, boolean isObfuscated)
	{
		BetterFarmers.logger.info("transformMethod_hasEnoughItems");

		/*
		Replacing:
		With:
			return VillagerHooks.hasEnoughItems(this, multiplier);
		 */

		String EntityVillager = (!isObfuscated ? "net/minecraft/entity/passive/EntityVillager" : "zn");
		String multiplier = (!isObfuscated ? "multiplier" : "p_175559_1_");

		method.instructions.clear();

		MethodVisitor mv = (MethodVisitor)method;
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/crazysnailboy/mods/betterfarmers/hooks/VillagerHooks", "hasEnoughItems", "(L" + EntityVillager + ";I)Z", false);
		mv.visitInsn(Opcodes.IRETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + EntityVillager + ";", null, l0, l1, 0);
		mv.visitLocalVariable(multiplier, "I", null, l0, l1, 1);
		mv.visitMaxs(2, 2);
		mv.visitEnd();
	}

	private static void transformMethod_initEntityAI(MethodNode method, boolean isObfuscated)
	{
		BetterFarmers.logger.info("transformMethod_initEntityAI");

		/*
		Replacing:
			this.tasks.addTask(9, new EntityAIVillagerInteract(this));
		With:
			this.tasks.addTask(9, new net.crazysnailboy.mods.betterfarmers.entity.ai.EntityAIVillagerInteract(this));
		 */

		AbstractInsnNode targetNode = null;
		for (AbstractInsnNode instruction : method.instructions.toArray())
		{
			if (instruction.getOpcode() == Opcodes.NEW)
			{
				if ( ((TypeInsnNode)instruction).desc.equals(!isObfuscated ? "net/minecraft/entity/ai/EntityAIVillagerInteract" : "ux") )
				{
					targetNode = instruction;
					break;
				}
			}
		}

		if (targetNode != null)
		{
			((TypeInsnNode)targetNode).desc = "net/crazysnailboy/mods/betterfarmers/entity/ai/EntityAIVillagerInteract";
			while (targetNode.getOpcode() != Opcodes.INVOKESPECIAL)
			{
				targetNode = targetNode.getNext();
			}
			((MethodInsnNode)targetNode).owner = "net/crazysnailboy/mods/betterfarmers/entity/ai/EntityAIVillagerInteract";
		}
	}

	private static void transformMethod_isFarmItemInInventory(MethodNode method, boolean isObfuscated)
	{
		BetterFarmers.logger.info("transformMethod_isFarmItemInInventory");

		/*
		Replacing:
		With:
			return VillagerHooks.isFarmItemInInventory(this);
		 */

		String EntityVillager = (!isObfuscated ? "net/minecraft/entity/passive/EntityVillager" : "zn");

		method.instructions.clear();

		MethodVisitor mv = (MethodVisitor)method;
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/crazysnailboy/mods/betterfarmers/hooks/VillagerHooks", "isFarmItemInInventory", "(L" + EntityVillager + ";)Z", false);
		mv.visitInsn(Opcodes.IRETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + EntityVillager + ";", null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private static void transformMethod_onGrowingAdult(MethodNode method, boolean isObfuscated)
	{
		BetterFarmers.logger.info("transformMethod_onGrowingAdult");

		/*
		Replacing:
		With:
	    	VillagerHooks.onGrowingAdult(this);
	        super.onGrowingAdult();
		 */

		String EntityVillager = (!isObfuscated ? "net/minecraft/entity/passive/EntityVillager" : "zn");
		String EntityAgeable = (!isObfuscated ? "net/minecraft/entity/EntityAgeable" : "rt");
		String onGrowingAdult = (!isObfuscated ? "onGrowingAdult" : "func_175500_n");

		method.instructions.clear();

		MethodVisitor mv = (MethodVisitor)method;
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/crazysnailboy/mods/betterfarmers/hooks/VillagerHooks", "onGrowingAdult", "(Lnet/minecraft/entity/passive/EntityVillager;)V", false);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, EntityAgeable, onGrowingAdult, "()V", false);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitInsn(Opcodes.RETURN);
		Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitLocalVariable("this", "L" + EntityVillager + ";", null, l0, l3, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private static void transformMethod_setAdditionalAItasks(MethodNode method, boolean isObfuscated)
	{
		BetterFarmers.logger.info("transformMethod_setAdditionalAItasks");

		/*
		Replacing:
  		With:
			VillagerHooks.setAdditionalAItasks(this);
		 */

		String EntityVillager = (!isObfuscated ? "net/minecraft/entity/passive/EntityVillager" : "zn");

		method.instructions.clear();

		MethodVisitor mv = (MethodVisitor)method;
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/crazysnailboy/mods/betterfarmers/hooks/VillagerHooks", "setAdditionalAItasks", "(L" + EntityVillager + ";)V", false);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitInsn(Opcodes.RETURN);
		Label l2 = new Label();
		mv.visitLabel(l2);
		mv.visitLocalVariable("this", "L" + EntityVillager + ";", null, l0, l2, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private static void transformMethod_wantsMoreFood(MethodNode method, boolean isObfuscated)
	{
		BetterFarmers.logger.info("transformMethod_wantsMoreFood");

		/*
		Replacing:
		With:
			return VillagerHooks.wantsMoreFood(this);
		 */

		String EntityVillager = (!isObfuscated ? "net/minecraft/entity/passive/EntityVillager" : "zn");

		method.instructions.clear();

		MethodVisitor mv = (MethodVisitor)method;
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, "net/crazysnailboy/mods/betterfarmers/hooks/VillagerHooks", "wantsMoreFood", "(L" + EntityVillager + ";)Z", false);
		mv.visitInsn(Opcodes.IRETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + EntityVillager + ";", null, l0, l1, 0);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

}
