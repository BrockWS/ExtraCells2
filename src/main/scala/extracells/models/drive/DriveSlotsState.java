package extracells.models.drive;

public class DriveSlotsState {

	private final DriveSlotState[] slots;

	private DriveSlotsState(DriveSlotState[] slots) {
		this.slots = slots;
	}

	public DriveSlotState getState(int index) {
		if (index >= slots.length) {
			return DriveSlotState.EMPTY;
		}
		return slots[index];
	}

	public int getSlotCount() {
		return slots.length;
	}

	/**
	 * Retrieve an array that describes the state of each slot in this drive or chest.
	 */
	public static DriveSlotsState fromChestOrDrive(IDrive drive) {
		DriveSlotState[] slots = new DriveSlotState[drive.getCellCount()];
		for (int i = 0; i < drive.getCellCount(); i++) {
			if (!drive.isPowered()) {
				if (drive.getCellStatus(i) != 0) {
					slots[i] = DriveSlotState.OFFLINE;
				} else {
					slots[i] = DriveSlotState.EMPTY;
				}
			} else {
				slots[i] = DriveSlotState.fromCellStatus(drive.getCellStatus(i));
			}
		}
		return new DriveSlotsState(slots);
	}
}
