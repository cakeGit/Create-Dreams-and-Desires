package uwu.lopyluna.create_dd.mixins;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.gantry.GantryShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uwu.lopyluna.create_dd.content.blocks.kinetics.giant_gear.GiantGearBlockEntity;
import uwu.lopyluna.create_dd.registry.DesiresBlockEntityTypes;
import uwu.lopyluna.create_dd.registry.DesiresBlocks;

import java.util.ArrayList;
import java.util.List;

//FOR LARGE COG TO WORM GEAR

@Mixin(value = KineticBlockEntity.class, remap = false)
public abstract class MixinKineticBlockEntity extends SmartBlockEntity {
    
    public MixinKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    //@Shadow public abstract float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs);*
//
//    @Inject(method = "propagateRotationTo(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;ZZ)F",*
//            at=@At("HEAD"), cancellable = true)
//    public void propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff,*
//                                    boolean connectedViaAxes, boolean connectedViaCogs, CallbackInfoReturnable<Float> cir) {
//        if (AllBlocks.LARGE_COGWHEEL.has(stateFrom)) {
//            float defaultModifier = propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
//
//            if (connectedViaAxes)
//                cir.setReturnValue(defaultModifier);
//            if (!DesiresBlocks.WORM_GEAR.has(stateTo))
//                cir.setReturnValue(defaultModifier);
//
//            Direction direction = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ());
//            if (stateFrom.getValue(CogWheelBlock.AXIS) != direction.getOpposite().getAxis())
//                cir.setReturnValue(defaultModifier);
//
//            cir.setReturnValue(getLargeCogModifier(stateTo.getValue(GantryShaftBlock.FACING), stateFrom.getValue(CogWheelBlock.AXIS)));
//        }
//
//    }


    @Inject(method = "propagateRotationTo(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;ZZ)F", at = @At("HEAD"), cancellable = true)
    public void propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff,
                                    boolean connectedViaAxes, boolean connectedViaCogs, CallbackInfoReturnable<Float> cir) {
        if (!DesiresBlockEntityTypes.GIANT_GEAR.is(target)) return;
        
        if (connectedViaAxes)
            cir.setReturnValue(1f);
        else if (ICogWheel.isSmallCog(stateFrom))
            cir.setReturnValue(-1/8f);
        else if (ICogWheel.isLargeCog(stateFrom))
            cir.setReturnValue(-1/4f);
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
