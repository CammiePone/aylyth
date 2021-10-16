package moriyashiine.aylyth.client.particle;

import moriyashiine.aylyth.common.registry.ModParticles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PilotLightParticle extends AbstractSlowingParticle {
	protected boolean fadeIn = true;

	protected PilotLightParticle(ClientWorld clientWorld, double x, double y, double z, float r, float g, float b) {
		super(clientWorld, x, y, z, 0, Math.max(0.01F, clientWorld.random.nextFloat() / 50), 0);
		colorAlpha = 0;
		colorRed = r;
		colorGreen = g;
		colorBlue = b;
		maxAge = 40 + clientWorld.random.nextInt(40);
	}

	public ParticleTextureSheet getType() {
		return ModParticles.ParticleTextureSheets.GLOWING;
	}

	public void move(double dx, double dy, double dz) {
		this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
		this.repositionFromBoundingBox();
	}

	@Override
	public void tick() {
		super.tick();
		float lifeRatio = (float) this.age / (float) this.maxAge;
		this.colorAlpha = fadeIn ? (lifeRatio >= 0.5F ? 1 - (lifeRatio - 0.5F) * 2 : lifeRatio * 2) : 1 - lifeRatio;
		if (lifeRatio >= 1) {
			markDead();
		}
	}

	public int getBrightness(float tint) {
		float f = ((float) this.age + tint) / (float) this.maxAge;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = super.getBrightness(tint);
		int j = i & 255;
		int k = i >> 16 & 255;
		j += (int) (f * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void setSprite(SpriteProvider spriteProvider) {
		super.setSprite(spriteProvider);
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double x, double y, double z, double velX, double velY, double velZ) {
			PilotLightParticle particle = new PilotLightParticle(clientWorld, x, y, z, MathHelper.nextFloat(clientWorld.random, 0.8F, 1F), MathHelper.nextFloat(clientWorld.random, 0.5F, 0.6F), MathHelper.nextFloat(clientWorld.random, 0.05F, 0.1F));
			particle.scale(MathHelper.nextFloat(clientWorld.random, 0.5F, 0.75F));
			particle.velocityX = velX;
			particle.velocityY = velY;
			particle.velocityZ = velZ;
			particle.fadeIn = false;
			particle.setSpriteForAge(this.spriteProvider);
			return particle;
		}
	}
}