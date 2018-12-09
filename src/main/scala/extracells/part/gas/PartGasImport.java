package extracells.part.gas;

import extracells.api.gas.IAEGasStack;
import extracells.integration.Integration;
import extracells.integration.mekanism.gas.Capabilities;
import extracells.integration.mekanism.gas.MekanismGas;
import extracells.part.fluid.PartFluidImport;
import extracells.util.GasUtil;
import extracells.util.MachineSource;
import extracells.util.StorageChannels;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.ITubeConnection;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;

import appeng.api.config.Actionable;
import appeng.api.storage.IMEMonitor;

/**
 * @author BrockWS
 */
@Optional.InterfaceList({
        @Optional.Interface(iface = "mekanism.api.gas.IGasHandler", modid = "MekanismAPI|gas", striprefs = true),
        @Optional.Interface(iface = "mekanism.api.gas.ITubeConnection", modid = "MekanismAPI|gas", striprefs = true)
})
public class PartGasImport extends PartFluidImport implements IGasHandler, ITubeConnection {

    @Override
    public boolean doWork(int rate, int ticksSinceLastCall) {
        if (!Integration.Mods.MEKANISMGAS.isEnabled() || this.getFacingGasTank() == null || !this.isActive())
            return false;
        boolean empty = true;
        List<Fluid> filter = new ArrayList<>();
        filter.add(this.filterFluids[4]);
        if (this.filterSize > 0) {
            filter.add(this.filterFluids[1]);
            filter.add(this.filterFluids[3]);
            filter.add(this.filterFluids[5]);
            filter.add(this.filterFluids[7]);
            if (this.filterSize > 1) {
                filter.add(this.filterFluids[0]);
                filter.add(this.filterFluids[2]);
                filter.add(this.filterFluids[6]);
                filter.add(this.filterFluids[8]);
            }
        }

        for (Fluid fluid : filter) {
            if (fluid == null)
                continue;
            empty = false;
            if (fillToNetwork(fluid, rate * ticksSinceLastCall))
                return true;
        }
        return empty && fillToNetwork(null, rate * ticksSinceLastCall);
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    protected boolean fillToNetwork(Fluid fluid, int toDrain) {
        GasStack drained = null;
        IGasHandler facingTank = this.getFacingGasTank();
        EnumFacing side = this.getFacing();
        Gas gasType = null;
        if (fluid != null) {
            GasStack gasStack = GasUtil.getGasStack(new FluidStack(fluid, toDrain));
            if (gasStack != null)
                gasType = gasStack.getGas();
        }
        if (gasType == null)
            drained = facingTank.drawGas(side.getOpposite(), toDrain, false);
        else if (facingTank.canDrawGas(side.getOpposite(), gasType))
            drained = facingTank.drawGas(side.getOpposite(), toDrain, false);
        if (drained == null || drained.amount < 1 || drained.getGas() == null)
            return false;
        IAEGasStack toFill = StorageChannels.GAS().createStack(drained);
        IAEGasStack notInjected = this.injectGas(toFill, Actionable.MODULATE);
        if (notInjected != null) {
            int amount = Math.toIntExact(toFill.getStackSize() - notInjected.getStackSize());
            if (amount > 0) {
                facingTank.drawGas(side.getOpposite(), amount, true);
                return true;
            } else {
                return false;
            }
        } else {
            facingTank.drawGas(side.getOpposite(), ((GasStack) toFill.getGasStack()).amount, true);
            return true;
        }
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        if (stack == null || stack.amount < 1 || !canReceiveGas(side, stack.getGas()))
            return 0;
        int amount = Math.min(stack.amount, 125 + this.speedState * 125);
        IAEGasStack gasStack = StorageChannels.GAS().createStack(new GasStack(stack.getGas(), amount));
        IAEGasStack notInjected;
        if (this.getGridBlock() == null) {
            notInjected = gasStack;
        } else {
            IMEMonitor<IAEGasStack> monitor = this.getGridBlock().getGasMonitor();
            if (monitor == null)
                notInjected = gasStack;
            else
                notInjected = monitor.injectItems(gasStack, Actionable.MODULATE, new MachineSource(this));
        }
        if (notInjected == null)
            return amount;
        else
            return amount - Math.toIntExact(notInjected.getStackSize());
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public boolean canDrawGas(EnumFacing enumFacing, Gas gas) {
        return false;
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public GasStack drawGas(EnumFacing enumFacing, int i, boolean b) {
        return null;
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public boolean canReceiveGas(EnumFacing enumFacing, Gas gasType) {
        Fluid fluid = MekanismGas.getFluidGasMap().get(gasType);
        boolean isEmpty = true;
        for (Fluid filter : this.filterFluids) {
            if (filter == null)
                continue;
            isEmpty = false;
            if (filter == fluid)
                return true;
        }
        return isEmpty;
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public boolean hasCapability(Capability<?> capability) {
        return capability == Capabilities.GAS_HANDLER_CAPABILITY || capability == Capabilities.TUBE_CONNECTION_CAPABILITY;
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public <T> T getCapability(Capability<T> capability) {
        if (capability == Capabilities.GAS_HANDLER_CAPABILITY)
            return Capabilities.GAS_HANDLER_CAPABILITY.cast(this);
        else if (capability == Capabilities.TUBE_CONNECTION_CAPABILITY)
            return Capabilities.TUBE_CONNECTION_CAPABILITY.cast(this);
        else
            return super.getCapability(capability);
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    public boolean canTubeConnect(EnumFacing facing) {
        return this.getFacing() == facing;
    }
}
