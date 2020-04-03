package link.infra.funkyforcefields.blocks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlasmaProjectorBlockEntityRenderer extends BlockEntityRenderer<PlasmaProjectorBlockEntity> {
	public PlasmaProjectorBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(PlasmaProjectorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		matrices.translate(0, 1, 0);
		matrices.translate(0.5f, 0.5f, 0.5f);
		matrices.scale(0.5f, 0.5f, 0.5f);

//		MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(Items.ACACIA_BUTTON), ModelTransformation.Mode.GROUND, 0, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);

		MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX).apply(new Identifier("minecraft:block/white_concrete"));

		VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getTranslucent());

		double offset = Math.sin((Objects.requireNonNull(blockEntity.getWorld()).getTime() + tickDelta) / 8.0) / 4.0;
		//int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
		int lightAbove = 15728640;
//		for (int i = 0; i < 5; i++) {
//			MatrixStack.Entry entry = matrices.peek();
//			consumer.vertex(entry.getModel(), 0, 0, 0)
//				.color(255, 255, 255, 255)
//				.texture(sprite.getMinU(), sprite.getMinV())
//				.overlay(overlay)
//				.light(lightAbove)
//				.normal(entry.getNormal(), 0, 0, 0)
//				.next();
//			consumer.vertex(entry.getModel(), 0, 1, 0)
//				.color(255, 255, 255, 255)
//				.texture(sprite.getMinU(), sprite.getMaxV())
//				.overlay(overlay)
//				.light(lightAbove)
//				.normal(entry.getNormal(), 0, 0, 0)
//				.next();
//			consumer.vertex(entry.getModel(), 1, 1, 0)
//				.color(255, 255, 255, 255)
//				.texture(sprite.getMaxU(), sprite.getMaxV())
//				.overlay(overlay)
//				.light(lightAbove)
//				.normal(entry.getNormal(), 0, 0, 0)
//				.next();
//			consumer.vertex(entry.getModel(), 1, 0, 0)
//				.color(255, 255, 255, 255)
//				.texture(sprite.getMaxU(), sprite.getMinV())
//				.overlay(overlay)
//				.light(lightAbove)
//				.normal(entry.getNormal(), 0, 0, 0)
//				.next();
//			if (i == 3) {
//				matrices.translate(0.5, 0.5, 0.5);
//				matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90));
//				matrices.translate(-0.5, -0.5, -0.5);
//			} else {
//				matrices.translate(0.5, 0.5, 0.5);
//				matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90));
//				matrices.translate(-0.5, -0.5, -0.5);
//			}
//		}

		calcVertices(20, 10);
		MatrixStack.Entry entry = matrices.peek();
		for (int i = 0; i < vertices.size(); i++) {
			Vector3f vertex = vertices.get(i);
			Vector3f normal = normals.get(i);
			Vector3f uv = uvs.get(i);
			consumer.vertex(entry.getModel(), vertex.getX(), vertex.getY(), vertex.getZ())
				.color(255, 255, 255, 255)
				.texture(((sprite.getMaxU() - sprite.getMinU()) * uv.getX()) + sprite.getMinU(), ((sprite.getMaxV() - sprite.getMinV()) * uv.getY()) + sprite.getMinV())
				.overlay(overlay)
				.light(lightAbove)
				.normal(entry.getNormal(), normal.getX(), normal.getY(), normal.getZ())
				.next();
		}

		matrices.pop();
	}

	private static List<Vector3f> vertices = new ArrayList<>();
	private static List<Vector3f> normals = new ArrayList<>();
	private static List<Vector3f> uvs = new ArrayList<>();

	private static void calcVertices(int sectorCount, int stackCount) {
		vertices.clear();
		normals.clear();
		uvs.clear();

		float sectorStep = 2 * (float)Math.PI / sectorCount;
		float stackStep = (float)Math.PI / stackCount;
		for (int i = 0; i <= stackCount; i++) {
			float stackAngle = (float)Math.PI / 2 - i * stackStep;
			float xy = (float) Math.cos(stackAngle);
			float z = (float) Math.sin(stackAngle);

			for (int j = 0; j <= sectorCount; j++) {
				float sectorAngle = j * sectorStep;

				Vector3f vertex = new Vector3f(
					xy * (float)Math.cos(sectorAngle),
					xy * (float)Math.sin(sectorAngle),
					z
				);
				vertices.add(vertex);
				// TODO: normals == vertices?
				normals.add(vertex);

				uvs.add(new Vector3f((float)j / sectorCount, (float)i / stackCount, 0));
			}
		}
	}
}
