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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlasmaProjectorBlockEntityRenderer extends BlockEntityRenderer<PlasmaProjectorBlockEntity> {
	public PlasmaProjectorBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public boolean rendersOutsideBoundingBox(PlasmaProjectorBlockEntity blockEntity) {
		return true;
	}

	@Override
	public void render(PlasmaProjectorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();
		//matrices.translate(0, 1, 0);
		matrices.translate(0.5f, 0.5f, 0.5f);
		matrices.scale(0.5f, 0.5f, 0.5f);
		matrices.scale(10f, 10f, 10f);
		matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(90));

		MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(new Identifier("minecraft:block/glass"));

		VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getCutout());

		float offset = (float) (Math.sin((Objects.requireNonNull(blockEntity.getWorld()).getTime() + tickDelta) / 8.0) / 4.0);
		//int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
		int lightAbove = 15728640;
		// TODO: BAKE THOSE QUADS!!!

		calcVertices(20, 10);
		//calcVertices(200, 100);
		MatrixStack.Entry entry = matrices.peek();
		for (int i = 0; i < vertices.size(); i++) {
			Vec3f vertex = vertices.get(i);
			Vec3f normal = normals.get(i);
			Vec3f uv = uvs.get(i);
			consumer.vertex(entry.getModel(), vertex.getX(), vertex.getY(), vertex.getZ())
				.color(255, 255, 255, 255)
				//.texture(uv.getX(), uv.getY())
				.texture(((sprite.getMaxU() - sprite.getMinU()) * uv.getX()) + sprite.getMinU(), ((sprite.getMaxV() - sprite.getMinV()) * uv.getY()) + sprite.getMinV())
				.overlay(overlay)
				.light(lightAbove)
				.normal(entry.getNormal(), normal.getX(), normal.getY(), normal.getZ())
				.next();
		}

		entry = matrices.peek();
		for (int i = 0; i < vertices.size(); i+= 4) {
			for (int j = 3; j > -1; j--) {
				Vec3f vertex = vertices.get(i+j);
				Vec3f normal = normals.get(i+j);
				Vec3f uv = uvs.get(i+j);
				consumer.vertex(entry.getModel(), vertex.getX(), vertex.getY(), vertex.getZ())
					.color(255, 255, 255, 255)
					//.texture(uv.getX(), uv.getY())
					.texture(((sprite.getMaxU() - sprite.getMinU()) * uv.getX()) + sprite.getMinU(), ((sprite.getMaxV() - sprite.getMinV()) * uv.getY()) + sprite.getMinV())
					.overlay(overlay)
					.light(lightAbove)
					.normal(entry.getNormal(), normal.getX(), normal.getY(), normal.getZ())
					.next();
			}

		}

		matrices.pop();
	}

	private static List<Vec3f> vertices = new ArrayList<>();
	private static List<Vec3f> normals = new ArrayList<>();
	private static List<Vec3f> uvs = new ArrayList<>();

	private static void calcVertices(int sectorCount, int stackCount) {
		vertices.clear();
		normals.clear();
		uvs.clear();

		float sectorStep = 2 * (float)Math.PI / sectorCount;
		float stackStep = (float)Math.PI / stackCount;
		for (int i = 0; i <= stackCount - 1; i++) {
			float stackAngle = (float)Math.PI / 2 - i * stackStep;
			float xy = (float) Math.cos(stackAngle);
			float z = (float) Math.sin(stackAngle);
			float stackAngle2 = (float)Math.PI / 2 - (i + 1) * stackStep;
			float xy2 = (float) Math.cos(stackAngle2);
			float z2 = (float) Math.sin(stackAngle2);

			for (int j = 0; j <= sectorCount - 1; j++) {
				float sectorAngle = j * sectorStep;

				Vec3f vertex = new Vec3f(
					xy * (float)Math.cos(sectorAngle),
					xy * (float)Math.sin(sectorAngle),
					z
				);
				vertices.add(vertex);
				// TODO: normals == vertices?
				normals.add(vertex);
				//uvs.add(new Vec3f((float)j / sectorCount, (float)i / stackCount, 0));
				uvs.add(new Vec3f(0, 0, 0));

				Vec3f vertex2 = new Vec3f(
					xy * (float)Math.cos(sectorAngle + sectorStep),
					xy * (float)Math.sin(sectorAngle + sectorStep),
					z
				);
				vertices.add(vertex2);
				// TODO: normals == vertices?
				normals.add(vertex2);
				//uvs.add(new Vec3f((float)(j + 1) / sectorCount, (float)i / stackCount, 0));
				uvs.add(new Vec3f(1, 0, 0));

				Vec3f vertex3 = new Vec3f(
					xy2 * (float)Math.cos(sectorAngle + sectorStep),
					xy2 * (float)Math.sin(sectorAngle + sectorStep),
					z2
				);
				vertices.add(vertex3);
				// TODO: normals == vertices?
				normals.add(vertex3);

				//uvs.add(new Vec3f((float)(j + 1) / sectorCount, (float)(i + 1) / stackCount, 0));
				uvs.add(new Vec3f(1, 1, 0));

				Vec3f vertex4 = new Vec3f(
					xy2 * (float)Math.cos(sectorAngle),
					xy2 * (float)Math.sin(sectorAngle),
					z2
				);
				vertices.add(vertex4);
				// TODO: normals == vertices?
				normals.add(vertex4);

				//uvs.add(new Vec3f((float)j / sectorCount, (float)(i + 1) / stackCount, 0));
				uvs.add(new Vec3f(0, 1, 0));
			}
		}
	}
}
