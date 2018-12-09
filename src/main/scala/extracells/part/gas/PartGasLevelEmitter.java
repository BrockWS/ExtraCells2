package extracells.part.gas;

import extracells.api.gas.IAEGasStack;
import extracells.part.fluid.PartFluidLevelEmitter;
import extracells.util.GasUtil;

import appeng.api.networking.IGridNode;

/**
 * @author BrockWS
 */
public class PartGasLevelEmitter extends PartFluidLevelEmitter {

    public PartGasLevelEmitter() {
        this.isGas = true;
    }

    @Override
    protected void onStackChangeGas(IAEGasStack fullStack, IAEGasStack diffStack) {
        if (diffStack == null || diffStack.getGas() != GasUtil.getGas(this.selectedFluid))
            return;
        this.currentAmount = fullStack != null ? fullStack.getStackSize() : 0;
        IGridNode node = this.getGridNode();
        if (node == null)
            return;
        this.setActive(node.isActive());
        this.getHost().markForUpdate();
        this.notifyTargetBlock(this.getHostTile(), this.getFacing());
    }
}
