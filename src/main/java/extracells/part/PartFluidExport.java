package extracells.part;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.parts.IPartCollsionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.data.IAEFluidStack;
import extracells.render.TextureManager;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class PartFluidExport extends PartFluidIO
{

	@Override
	public void renderInventory(IPartRenderHelper rh, RenderBlocks renderer)
	{
		rh.setTexture(TextureManager.BUS_SIDE.getTexture());
		rh.setBounds(6, 6, 12, 10, 10, 13);
		rh.renderInventoryBox(renderer);

		rh.setBounds(4, 4, 13, 12, 12, 14);
		rh.renderInventoryBox(renderer);

		rh.setBounds(5, 5, 1, 11, 11, 15);
		rh.renderInventoryBox(renderer);

		IIcon side = TextureManager.BUS_SIDE.getTexture();
		rh.setTexture(side, side, side, TextureManager.EXPORT_FRONT.getTexture(), side, side);
		rh.setBounds(6, 6, 15, 10, 10, 16);
		rh.renderInventoryBox(renderer);

		rh.setBounds(6, 6, 11, 10, 10, 12);
		renderInventoryBusLights(rh, renderer);
	}

	@Override
	public void renderStatic(int x, int y, int z, IPartRenderHelper rh, RenderBlocks renderer)
	{
		rh.setTexture(TextureManager.BUS_SIDE.getTexture());
		rh.setBounds(6, 6, 12, 10, 10, 13);
		rh.renderBlock(x, y, z, renderer);

		rh.setBounds(4, 4, 13, 12, 12, 14);
		rh.renderBlock(x, y, z, renderer);

		rh.setBounds(5, 5, 14, 11, 11, 15);
		rh.renderBlock(x, y, z, renderer);

		IIcon side = TextureManager.BUS_SIDE.getTexture();
		rh.setTexture(side, side, side, TextureManager.EXPORT_FRONT.getTexture(), side, side);
		rh.setBounds(6, 6, 15, 10, 10, 16);
		rh.renderBlock(x, y, z, renderer);

		rh.setBounds(6, 6, 11, 10, 10, 12);
		renderStaticBusLights(x, y, z, rh, renderer);
	}

	@Override
	public void getBoxes(IPartCollsionHelper bch)
	{
		bch.addBox(6, 6, 12, 10, 10, 13);
		bch.addBox(4, 4, 13, 12, 12, 14);
		bch.addBox(5, 5, 14, 11, 11, 15);
		bch.addBox(6, 6, 15, 10, 10, 16);
		bch.addBox(6, 6, 11, 10, 10, 12);
	}

	@Override
	public int cableConnectionRenderTo()
	{
		return 5;
	}

	@Override
	public boolean doWork(int rate, int TicksSinceLastCall)
	{
		if (facingTank == null)
			return false;
		List<Fluid> filter = new ArrayList<Fluid>();
		filter.add(filterFluids[4]);

		if (filterSize >= 1)
		{
			for (byte i = 1; i < 9; i += 2)
			{
				if (i != 4)
				{
					filter.add(filterFluids[i]);
				}
			}
		}

		if (filterSize >= 2)
		{
			for (byte i = 0; i < 9; i += 2)
			{
				if (i != 4)
				{
					filter.add(filterFluids[i]);
				}
			}
		}

		for (Fluid fluid : filter)
		{
			if (fluid != null)
			{
				IAEFluidStack stack = extractFluid(AEApi.instance().storage().createFluidStack(new FluidStack(fluid, rate * TicksSinceLastCall)), Actionable.SIMULATE);

				if (stack == null)
					continue;
				int filled = facingTank.fill(side.getOpposite(), stack.getFluidStack(), true);

				if (filled > 0)
				{
					extractFluid(AEApi.instance().storage().createFluidStack(new FluidStack(fluid, filled)), Actionable.MODULATE);
					return true;
				} else
				{
					continue;
				}
			} else
			{
				continue;
			}
		}
		return false;
	}
}
