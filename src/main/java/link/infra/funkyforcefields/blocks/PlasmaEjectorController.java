package link.infra.funkyforcefields.blocks;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.netty.buffer.Unpooled;
import link.infra.funkyforcefields.FunkyForcefields;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class PlasmaEjectorController extends SyncedGuiDescription {
	private ScreenHandlerContext context;
	private int currLength = 3;

	public PlasmaEjectorController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(null, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));

		currLength = context.get((world, pos) -> {
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
		title.setHorizontalAlignment(HorizontalAlignment.CENTER);
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

		this.context = context;

		root.validate(this);
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);
		if (world.isClient()) {
			context.run((world, pos) -> {
				PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
				byteBuf.writeBlockPos(pos);
				byteBuf.writeInt(currLength);
				ClientPlayNetworking.send(FunkyForcefields.PLASMA_EJECTOR_CONFIG_PACKET, byteBuf);
			});
		}
	}
}
