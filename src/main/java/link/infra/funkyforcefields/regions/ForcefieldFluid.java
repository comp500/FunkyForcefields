package link.infra.funkyforcefields.regions;

import com.mojang.serialization.Lifecycle;

import link.infra.funkyforcefields.FunkyForcefields;
import link.infra.funkyforcefields.blocks.ForcefieldBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.Random;

public interface ForcefieldFluid {
	RegistryKey<Registry<ForcefieldFluid>> REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier(FunkyForcefields.MODID, "forcefield_type"));

	SimpleRegistry<ForcefieldFluid> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<ForcefieldFluid>(REGISTRY_KEY, Lifecycle.stable()) {
		public <V extends ForcefieldFluid> V set(int rawId, RegistryKey<ForcefieldFluid> key, V entry, Lifecycle lifecycle) {
			ForcefieldBlocks.registerFluid(entry);
			return super.set(rawId, key, entry, lifecycle);
		}
	})
		.buildAndRegister();

	boolean allowsEntity(Entity ent);
	void applyCollisionEffect(World world, BlockPos pos, Entity entity);
	Identifier getBaseIdentifier();
	default void displayTick(World world, BlockPos pos, Random random, VoxelShape shape) {}
	TranslatableText getFluidName();

	default boolean hasModel() {
		return true;
	}
	@Environment(EnvType.CLIENT)
	default RenderLayer getRenderLayer() {
		return RenderLayer.getTranslucent();
	}
	// TODO: conversion from Minecraft fluids
}
