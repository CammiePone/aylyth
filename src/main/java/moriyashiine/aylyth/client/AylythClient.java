package moriyashiine.aylyth.client;

import com.terraformersmc.terraform.boat.api.client.TerraformBoatClientHelper;
import com.terraformersmc.terraform.sign.SpriteIdentifierRegistry;
import moriyashiine.aylyth.common.Aylyth;
import moriyashiine.aylyth.common.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AylythClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SpriteIdentifierRegistry.INSTANCE.addIdentifier(new SpriteIdentifier(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, ModBlocks.YMPE_SIGN.getTexture()));
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.YMPE_SAPLING, ModBlocks.POTTED_YMPE_SAPLING, ModBlocks.YMPE_DOOR, ModBlocks.YMPE_TRAPDOOR);
		TerraformBoatClientHelper.registerModelLayer(new Identifier(Aylyth.MOD_ID, "ympe"));
	}
}