package link.infra.funkyforcefields.regions;

import link.infra.funkyforcefields.FunkyForcefields;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.Random;

public class ForcefieldFluids {
	public static final ForcefieldFluid WATER = new ForcefieldFluid() {
		@Override
		public boolean allowsEntity(Entity ent) {
			return ent instanceof ItemEntity;
		}

		@Override
		public void applyCollisionEffect(World world, BlockPos pos, Entity entity) {
			if (!entity.isFireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
				entity.damage(DamageSource.IN_FIRE, 1.0f);
			}
		}

		@Override
		public Identifier getBaseIdentifier() {
			return new Identifier(FunkyForcefields.MODID, "water_forcefield");
		}

		@Override
		public void displayTick(World world, BlockPos pos, Random random, VoxelShape shape) {
			Box box = shape.getBoundingBox();
			for (int i = 0; i < 10; i++) {
				world.addImportantParticle(ParticleTypes.CLOUD,
					pos.getX() + box.minX + (box.getXLength() * random.nextFloat()),
					pos.getY() + box.minY + (box.getYLength() * random.nextFloat()),
					pos.getZ() + box.minZ + (box.getZLength() * random.nextFloat()),
					0, 0, 0);
			}
		}

		@Override
		public TranslatableText getFluidName() {
			return new TranslatableText("block.minecraft.water");
		}

		@Override
		public boolean hasModel() {
			return false;
		}

		@Override
		public RenderLayer getRenderLayer() {
			return null;
		}
	};

	public static final ForcefieldFluid LAVA = new ForcefieldFluid() {
		@Override
		public boolean allowsEntity(Entity ent) {
			return true;
		}

		@Override
		public void applyCollisionEffect(World world, BlockPos pos, Entity entity) {
			// TODO: item smelting?
			if (!entity.isFireImmune()) {
				entity.damage(DamageSource.IN_FIRE, 5.0f);
			}
		}

		@Override
		public Identifier getBaseIdentifier() {
			return new Identifier(FunkyForcefields.MODID, "lava_forcefield");
		}

		@Override
		public TranslatableText getFluidName() {
			return new TranslatableText("block.minecraft.lava");
		}
	};

	public static final ForcefieldFluid GLASS = new ForcefieldFluid() {
		@Override
		public boolean allowsEntity(Entity ent) {
			return false;
		}

		@Override
		public void applyCollisionEffect(World world, BlockPos pos, Entity entity) {}

		@Override
		public Identifier getBaseIdentifier() {
			return new Identifier(FunkyForcefields.MODID, "glass_forcefield");
		}

		@Override
		public TranslatableText getFluidName() {
			return new TranslatableText("block.minecraft.glass");
		}

		@Environment(EnvType.CLIENT)
		@Override
		public RenderLayer getRenderLayer() {
			return RenderLayer.getCutout();
		}
	};

	public static final ForcefieldFluid NETHER_PORTAL = new ForcefieldFluid() {
		@Override
		public boolean allowsEntity(Entity ent) {
			return true;
		}

		@Override
		public void applyCollisionEffect(World world, BlockPos pos, Entity entity) {
			if (!entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
				entity.setInNetherPortal(pos);
			}
		}

		@Override
		public Identifier getBaseIdentifier() {
			return new Identifier(FunkyForcefields.MODID, "portal_forcefield");
		}

		@Override
		public TranslatableText getFluidName() {
			return new TranslatableText("block.minecraft.nether_portal");
		}
	};

	public static final ForcefieldFluid FUNKY_GOO = new ForcefieldFluid() {
		@Override
		public boolean allowsEntity(Entity ent) {
			return ent instanceof PlayerEntity;
		}

		@Override
		public void applyCollisionEffect(World world, BlockPos pos, Entity entity) {
			if (!(entity instanceof PlayerEntity) && !entity.isFireImmune()) {
				entity.damage(DamageSource.IN_FIRE, 5.0f);
			}
		}

		@Override
		public Identifier getBaseIdentifier() {
			return new Identifier(FunkyForcefields.MODID, "funky_goo_forcefield");
		}

		@Override
		public TranslatableText getFluidName() {
			return new TranslatableText("block.funkyforcefields.funky_goo");
		}
	};

	public static void register() {
		Registry.register(ForcefieldFluid.REGISTRY, new Identifier(FunkyForcefields.MODID, "water"), WATER);
		Registry.register(ForcefieldFluid.REGISTRY, new Identifier(FunkyForcefields.MODID, "lava"), LAVA);
		Registry.register(ForcefieldFluid.REGISTRY, new Identifier(FunkyForcefields.MODID, "glass"), GLASS);
		Registry.register(ForcefieldFluid.REGISTRY, new Identifier(FunkyForcefields.MODID, "nether_portal"), NETHER_PORTAL);
		Registry.register(ForcefieldFluid.REGISTRY, new Identifier(FunkyForcefields.MODID, "funky_goo"), FUNKY_GOO);
	}
}
