package moriyashiine.aylyth.common.entity.mob;

import moriyashiine.aylyth.common.registry.ModBlocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AylythianEntity extends HostileEntity implements IAnimatable {
	private final AnimationFactory factory = new AnimationFactory(this);
	
	public AylythianEntity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.setCanPickUpLoot(true);
	}
	
	public static DefaultAttributeContainer.Builder createAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 35).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5).add(EntityAttributes.GENERIC_ARMOR, 2).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
	}
	
	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		float limbSwingAmount = Math.abs(event.getLimbSwingAmount());
		AnimationBuilder builder = new AnimationBuilder();
		if (limbSwingAmount > 0.01F) {
			MoveState state = limbSwingAmount > 0.3F ? limbSwingAmount > 0.6F ? MoveState.RUN : MoveState.WALK : MoveState.STALK;
			builder = switch (state) {
				case RUN -> builder.addAnimation("run", true);
				case WALK -> builder.addAnimation("walk", true);
				case STALK -> builder.addAnimation("stalk", true);
			};
		}
		else {
			builder.addAnimation("idle", true);
		}
		event.getController().setAnimation(builder);
		return PlayState.CONTINUE;
	}
	
	private <E extends IAnimatable> PlayState armPredicate(AnimationEvent<E> event) {
		AnimationBuilder builder = new AnimationBuilder();
		if (handSwingTicks > 0 && !isDead()) {
			event.getController().setAnimation(builder.addAnimation(getMainArm() == Arm.RIGHT ? "clawswipe_right" : "clawswipe_left", true));
			return PlayState.CONTINUE;
		}
		return PlayState.STOP;
	}
	
	@Override
	public void registerControllers(AnimationData animationData) {
		animationData.addAnimationController(new AnimationController<>(this, "controller", 10, this::predicate));
		animationData.addAnimationController(new AnimationController<>(this, "arms", 0, this::armPredicate));
	}
	
	@Override
	public AnimationFactory getFactory() {
		return factory;
	}
	
	@Override
	protected void initGoals() {
		super.initGoals();
		goalSelector.add(0, new SwimGoal(this));
		goalSelector.add(1, new MeleeAttackGoal(this, 1.2F, false));
		goalSelector.add(2, new WanderAroundFarGoal(this, 0.5F));
		goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8));
		goalSelector.add(3, new LookAroundGoal(this));
		targetSelector.add(0, new RevengeGoal(this));
		targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}
	
	@Override
	public void tick() {
		super.tick();
		if (age % 200 == 0) {
			heal(1);
		}
	}
	
	@Override
	public boolean damage(DamageSource source, float amount) {
		return super.damage(source, source.isFire() ? amount * 2 : amount);
	}
	
	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
		if (!world.isClient && world.getBlockState(getBlockPos()).getMaterial().isReplaceable() && ModBlocks.YMPE_SAPLING.getDefaultState().canPlaceAt(world, getBlockPos())) {
			world.setBlockState(getBlockPos(), ModBlocks.YMPE_SAPLING.getDefaultState());
			playSound(SoundEvents.BLOCK_GRASS_PLACE, getSoundVolume(), getSoundPitch());
		}
	}
	
	enum MoveState {
		WALK, RUN, STALK
	}
}