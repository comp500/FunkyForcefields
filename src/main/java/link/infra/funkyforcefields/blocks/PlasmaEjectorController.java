package link.infra.funkyforcefields.blocks;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.netty.buffer.Unpooled;
import link.infra.funkyforcefields.FunkyForcefields;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;

public class PlasmaEjectorController extends CottonCraftingController {
	private BlockContext blockContext;
	private int currLength = 3;

	public PlasmaEjectorController(int syncId, PlayerInventory playerInventory, BlockContext blockContext) {
		super(null, syncId, playerInventory, getBlockInventory(blockContext), getBlockPropertyDelegate(blockContext));

		currLength = blockContext.run((world, pos) -> {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof PlasmaEjectorBlockEntity) {
				return ((PlasmaEjectorBlockEntity) be).length;
			}
			return 3;
		}, 3);

		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		//root.setSize(300, 200);

		WLabel title = new WLabel(new TranslatableText("block.funkyforcefields.plasma_ejector"));
		title.setAlignment(Alignment.CENTER);
		root.add(title, 0, 0, 9, 1);

		WSlider lengthSlider = new WSlider(1, 10, Axis.HORIZONTAL);
		lengthSlider.setValue(currLength);
		root.add(lengthSlider, 4, 1, 5, 1);

		WLabel lengthCount = new WLabel(new LiteralText(Integer.toString(currLength)));
		root.add(lengthCount, 3, 1);

		WLabel lengthLabel = new WLabel(new TranslatableText("block.funkyforcefields.plasma_ejector.length"));
		root.add(lengthLabel, 0, 1);

		root.add(createPlayerInventoryPanel(), 0, 3);

		lengthSlider.setValueChangeListener(val -> {
			lengthCount.setText(new LiteralText(Integer.toString(val)));
			currLength = val;
		});

		this.blockContext = blockContext;

		root.validate(this);
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		if (world.isClient()) {
			blockContext.run((world, pos) -> {
				PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
				byteBuf.writeBlockPos(pos);
				byteBuf.writeInt(currLength);
				ClientSidePacketRegistry.INSTANCE.sendToServer(FunkyForcefields.PLASMA_EJECTOR_CONFIG_PACKET, byteBuf);
			});
		}
	}
}
