package link.infra.funkyforcefields.regions;

import link.infra.funkyforcefields.blocks.ForcefieldBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.Random;

public interface ForcefieldFluid {
	SimpleRegistry<ForcefieldFluid> REGISTRY = new SimpleRegistry<ForcefieldFluid>() {
		@Override
		public <V extends ForcefieldFluid> V set(int rawId, Identifier id, V entry) {
			ForcefieldBlocks.registerFluid(entry);
			return super.set(rawId, id, entry);
		}
	};

	ForcefieldFluid EMPTY = new ForcefieldFluid() {
		@Override
		public boolean allowsEntity(Entity ent) {
			return false;
		}

		@Override
		public void applyCollisionEffect(World world, BlockPos pos, Entity entity) {}

		@Override
		public Identifier getBaseIdentifier() {
			return null;
		}
	};

	boolean allowsEntity(Entity ent);
	void applyCollisionEffect(World world, BlockPos pos, Entity entity);
	Identifier getBaseIdentifier();
	default void displayTick(World world, BlockPos pos, Random random, VoxelShape shape) {}

	default boolean hasModel() {
		return true;
	}
	@Environment(EnvType.CLIENT)
	default RenderLayer getRenderLayer() {
		return RenderLayer.getTranslucent();
	}
	// TODO: conversion from Minecraft fluids
}
