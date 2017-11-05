package extracells.container.fluid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import extracells.container.slot.SlotRespective;

public class ContainerFluidCrafter extends Container {
	IInventory tileentity;

	public ContainerFluidCrafter(InventoryPlayer player, IInventory tileentity) {
		this.tileentity = tileentity;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				addSlotToContainer(new SlotRespective(tileentity, j + i * 3,
					62 + j * 18, 17 + i * 18));
			}
		}
		bindPlayerInventory(player);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
					8 + j * 18, i * 18 + 84));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {
		super.onContainerClosed(entityplayer);
	}

//	@Override
//	protected void retrySlotClick(int par1, int par2, boolean par3,
//		EntityPlayer par4EntityPlayer) {
//		// DON'T DO ANYTHING, YOU SHITTY METHOD!
//	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotnumber) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotnumber);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (this.tileentity.isItemValidForSlot(0, itemstack1)) {
				if (slotnumber < 10) {
					if (!mergeItemStack(itemstack1, 10, 36, false)) {
						return ItemStack.EMPTY;
					}
				} else if (slotnumber >= 10 && slotnumber <= 36) {
					if (!mergeItemStack(itemstack1, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				}
				if (itemstack1.getCount() == 0) {
					slot.putStack(ItemStack.EMPTY);
				} else {
					slot.onSlotChanged();
				}
			} else {
				return ItemStack.EMPTY;
			}
		}
		return itemstack;
	}
}
