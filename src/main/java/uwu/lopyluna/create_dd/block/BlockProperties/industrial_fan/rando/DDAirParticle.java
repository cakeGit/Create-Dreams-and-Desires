package uwu.lopyluna.create_dd.block.BlockProperties.industrial_fan.rando;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings({"deprecation"})
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DDAirParticle extends SimpleAnimatedParticle {

    private float originX, originY, originZ;
    private float targetX, targetY, targetZ;
    private float drag;

    private float twirlRadius, twirlAngleOffset;
    private Direction.Axis twirlAxis;

    protected DDAirParticle(ClientLevel world, DDAirParticleData data, double x, double y, double z, double dx, double dy,
                          double dz, SpriteSet sprite) {
        super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
        quadSize *= 0.75F;
        hasPhysics = false;

        setPos(x, y, z);
        originX = (float) (xo = x);
        originY = (float) (yo = y);
        originZ = (float) (zo = z);
        targetX = (float) (x + dx);
        targetY = (float) (y + dy);
        targetZ = (float) (z + dz);
        drag = data.drag;

        twirlRadius = Create.RANDOM.nextFloat() / 6;
        twirlAngleOffset = Create.RANDOM.nextFloat() * 360;
        twirlAxis = Create.RANDOM.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;

        // speed in m/ticks
        double length = new Vec3(dx, dy, dz).length();
        lifetime = Math.min((int) (length / data.speed), 60);
        selectSprite(7);
        setAlpha(.25f);

        if (length == 0) {
            remove();
            setAlpha(0);
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float progress = (float) Math.pow(((float) age) / lifetime, drag);
        float angle = (progress * 2 * 360 + twirlAngleOffset) % 360;
        Vec3 twirl = VecHelper.rotate(new Vec3(0, twirlRadius, 0), angle, twirlAxis);

        float x = (float) (Mth.lerp(progress, originX, targetX) + twirl.x);
        float y = (float) (Mth.lerp(progress, originY, targetY) + twirl.y);
        float z = (float) (Mth.lerp(progress, originZ, targetZ) + twirl.z);

        xd = x - this.x;
        yd = y - this.y;
        zd = z - this.z;

        setSpriteFromAge(sprites);
        this.move(this.xd, this.yd, this.zd);
    }

    public int getLightColor(float partialTick) {
        BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor(level, blockpos) : 0;
    }

    private void selectSprite(int index) {
        setSprite(sprites.get(index, 8));
    }

    public static class Factory implements ParticleProvider<DDAirParticleData> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet animatedSprite) {
            this.spriteSet = animatedSprite;
        }

        public Particle createParticle(DDAirParticleData data, ClientLevel worldIn, double x, double y, double z, double xSpeed,
                                       double ySpeed, double zSpeed) {
            return new DDAirParticle(worldIn, data, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

}
