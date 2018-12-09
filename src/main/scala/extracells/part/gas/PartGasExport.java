package extracells.part.gas;

import extracells.api.gas.IAEGasStack;
import extracells.integration.Integration;
import extracells.integration.mekanism.gas.Capabilities;
import extracells.part.fluid.PartFluidExport;
import extracells.util.StorageChannels;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.ITubeConnection;

import java.util.ArrayList;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;

import appeng.api.config.Actionable;

/**
 * @author BrockWS
 */
@Optional.Interface(iface = "mekanism.api.gas.ITubeConnection", modid = "MekanismAPI|gas", striprefs = true)
public class PartGasExport extends PartFluidExport implements ITubeConnection {

    @Override
    public boolean doWork(int rate, int ticksSinceLastCall) {
        return Integration.Mods.MEKANISMGAS.isEnabled() && this.work(rate, ticksSinceLastCall);
    }

    @Optional.Method(modid = "MekanismAPI|gas")
    protected boolean work(int rate, int ticksSinceLastCall) {
        IGasHandler facingTank = this.getFacingGasTank();
        if (facingTank == null || !this.isActive())
            return false;
        ArrayList<Fluid> filter = new ArrayList<>();
        filter.add(this.filterFluids[4]);
        if (this.filterSize > 0) {
            filter.add(this.filterFluids[1]);
            filter.add(this.filterFluids[3]);
            filter.add(this.filterFluids[5]);
            filter.add(this.filterFluids[7]);
        }
        if (this.filterSize > 1) {
            filter.add(this.filterFluids[0]);
            filter.add(this.filterFluids[2]);
            filter.add(this.filterFluids[6]);
            filter.add(this.filterFluids[8]);
        }

        for (Fluid fluid : filter) {
            if (fluid == null)
                continue;
            IAEGasStack stack = this.extractGas(StorageChannels.GAS().createStack(new FluidStack(fluid, rate * ticksSinceLastCall)), Actionable.SIMULATE);
            if (stack == null)
                continue;
            GasStack gasStack = (GasStack) stack.getGasStack();
            if (gasStack != null && facingTank.canReceiveGas(this.getFacing().getOpposite(), gasStack.getGas())) {
                int filled = facingTank.receiveGas(this.getFacing().getOpposite(), gasStack, true);
                if (filled > 0) {
                    this.extractGas(StorageChannels.GAS().createStack(new FluidStack(fluid, filled)), Actionable.MODULATE);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public boolean hasCapability(Capability<?> capability) {
        return capability == Capabilities.TUBE_CONNECTION_CAPABILITY;
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public <T> T getCapability(Capability<T> capability) {
        if (capability == Capabilities.TUBE_CONNECTION_CAPABILITY)
            return Capabilities.TUBE_CONNECTION_CAPABILITY.cast(this);
        return super.getCapability(capability);
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public boolean canTubeConnect(EnumFacing facing) {
        return this.getFacing() == facing;
    }
}
