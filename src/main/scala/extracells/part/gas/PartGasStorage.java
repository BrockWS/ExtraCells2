package extracells.part.gas;

import com.google.common.collect.ImmutableMap;
import extracells.api.gas.IAEGasStack;
import extracells.integration.Integration;
import extracells.inventory.cell.HandlerPartStorageGas;
import extracells.part.fluid.PartFluidStorage;
import extracells.util.StorageChannels;
import mekanism.api.gas.GasStack;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fml.common.Optional;

import appeng.api.config.AccessRestriction;

/**
 * @author BrockWS
 */
public class PartGasStorage extends PartFluidStorage {

    private Map<Object, Integer> gasList = new HashMap<>();

    public PartGasStorage() {
        this.handler = new HandlerPartStorageGas(this);
        this.channel = StorageChannels.GAS();
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    protected void updateNeighbor() {
        if (!Integration.Mods.MEKANISMGAS.isEnabled())
            return;
        gasList = new HashMap<>();
        if (this.access != AccessRestriction.READ && access != AccessRestriction.READ_WRITE)
            return;
        this.gasList = this.getAvailableItems();
    }

    @Override
    @Optional.Method(modid = "MekanismAPI|gas")
    protected boolean wasChanged() {
        return this.getAvailableItems().equals(this.gasList);
    }

    @Optional.Method(modid = "MekanismAPI|gas")
    private Map<Object, Integer> getAvailableItems() {
        Map<Object, Integer> map = new HashMap<>();
        HandlerPartStorageGas handler = (HandlerPartStorageGas) this.handler;
        for (IAEGasStack stack : handler.getAvailableItems(StorageChannels.GAS().createList())) {
            GasStack s = ((GasStack) stack.getGasStack()).copy();
            map.put(s, s.amount);
        }
        return map;
    }
}
