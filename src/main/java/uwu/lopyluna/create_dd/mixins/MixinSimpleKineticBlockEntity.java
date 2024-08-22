package uwu.lopyluna.create_dd.mixins;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uwu.lopyluna.create_dd.content.blocks.kinetics.giant_gear.GiantGearBlockEntity;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = SimpleKineticBlockEntity.class, remap = false)
public abstract class MixinSimpleKineticBlockEntity extends KineticBlockEntity {
    
    public MixinSimpleKineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }
    
    @Inject(method = "addPropagationLocations", at = @At("RETURN"), cancellable = true)
    private void addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours, CallbackInfoReturnable<List<BlockPos>> cir) {
        ArrayList<BlockPos> extendedConnectionPositions = new ArrayList<>(neighbours);
        Direction.Axis axis = ((IRotate) getBlockState().getBlock()).getRotationAxis(getBlockState());
        
        if (ICogWheel.isLargeCog(state))
            extendedConnectionPositions.addAll(
                GiantGearBlockEntity.collectConnectionPositions(
                    getBlockPos(), axis,
                    false, true
                )
            );
        
        if (ICogWheel.isSmallCog(state))
            extendedConnectionPositions.addAll(
                GiantGearBlockEntity.collectConnectionPositions(
                    getBlockPos(), axis,
                    true, false
                )
            );
        
        cir.setReturnValue(extendedConnectionPositions);
    }
    
}
