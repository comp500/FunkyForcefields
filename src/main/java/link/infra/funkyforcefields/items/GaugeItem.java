package link.infra.funkyforcefields.items;

import link.infra.funkyforcefields.regions.ForcefieldFluid;
import link.infra.funkyforcefields.transport.FluidContainerComponent;
import nerdhub.cardinal.components.api.component.BlockComponentProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.*;

public class GaugeItem extends Item {
	public GaugeItem(Settings settings) {
		super(settings);
	}

	private static List<Direction> findValidDirections(BlockComponentProvider provider, BlockView world, BlockPos pos) {
		List<Direction> dirs = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			if (provider.hasComponent(world, pos, FluidContainerComponent.TYPE, dir)) {
				dirs.add(dir);
			}
		}
		if (provider.hasComponent(world, pos, FluidContainerComponent.TYPE, null)) {
			dirs.add(null);
		}
		return dirs;
	}

	private static void printInformation(PlayerEntity player, FluidContainerComponent component) {
		// TODO: translate
		ForcefieldFluid fluid = component.getContainedFluid();
		if (fluid == null) {
			player.sendMessage(new TranslatableText("fluidname").append(new TranslatableText("none")));
		} else {
			player.sendMessage(new TranslatableText("fluidname").append(fluid.getFluidName()));
		}
		player.sendMessage(new TranslatableText("volume").append(Float.toString(component.getContainerVolume())));
		player.sendMessage(new TranslatableText("thermal_diffusivity").append(Float.toString(component.getThermalDiffusivity())));
		player.sendMessage(new TranslatableText("pressure").append(Float.toString(component.getPressure())));
		player.sendMessage(new TranslatableText("temperature").append(Float.toString(component.getTemperature())));
	}

	private static void printDirectionList(PlayerEntity player, List<Direction> directions) {
		if (directions.size() == 0 || (directions.size() == 1 && directions.get(0) == null)) {
			return;
		}
		Text text = new LiteralText("");
		for (int i = 0; i < directions.size(); i++) {
			switch (directions.get(i)) {
				case NORTH:
					text = text.append(new TranslatableText("item.funkyforcefields.gauge.north"));
					break;
				case EAST:
					text = text.append(new TranslatableText("item.funkyforcefields.gauge.east"));
					break;
				case SOUTH:
					text = text.append(new TranslatableText("item.funkyforcefields.gauge.south"));
					break;
				case WEST:
					text = text.append(new TranslatableText("item.funkyforcefields.gauge.west"));
					break;
				case UP:
					text = text.append(new TranslatableText("item.funkyforcefields.gauge.up"));
					break;
				case DOWN:
					text = text.append(new TranslatableText("item.funkyforcefields.gauge.down"));
					break;
				default:
					continue;
			}
			if (i < directions.size() - 1) {
				text = text.append(",");
			}
		}
		player.sendMessage(text.append(":"));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockPos pos = context.getBlockPos();
		BlockState state = context.getWorld().getBlockState(pos);
		Block block = state.getBlock();
		PlayerEntity player = context.getPlayer();
		if (player == null) {
			return ActionResult.FAIL;
		}
		if (block instanceof BlockComponentProvider) {
			if (context.getWorld().isClient()) {
				return ActionResult.SUCCESS;
			}
			BlockComponentProvider provider = (BlockComponentProvider) block;
			List<Direction> dirs = findValidDirections(provider, context.getWorld(), pos);
			if (dirs.size() > 0) {
				if (dirs.size() == 1) {
					FluidContainerComponent component = provider.getComponent(context.getWorld(), pos, FluidContainerComponent.TYPE, dirs.get(0));
					assert component != null;
					printInformation(player, component);
					return ActionResult.SUCCESS;
				}
				HashMap<FluidContainerComponent, List<Direction>> uniqueComponents = new HashMap<>();
				for (Direction dir : dirs) {
					Objects.requireNonNull(uniqueComponents.computeIfAbsent(provider.getComponent(context.getWorld(), pos, FluidContainerComponent.TYPE, dir), ignored -> new ArrayList<>())).add(dir);
				}
				if (uniqueComponents.size() == 1) {
					printInformation(player, uniqueComponents.keySet().iterator().next());
					return ActionResult.SUCCESS;
				} else if (uniqueComponents.size() > 1) {
					for (Map.Entry<FluidContainerComponent, List<Direction>> entry : uniqueComponents.entrySet()) {
						printDirectionList(player, entry.getValue());
						printInformation(player, entry.getKey());
					}
					return ActionResult.SUCCESS;
				}
			}
		}
		if (!context.getWorld().isClient()) {
			// TODO: translate
			player.sendMessage(new TranslatableText("not a block!!!"));
		}
		return ActionResult.FAIL;
	}
}
