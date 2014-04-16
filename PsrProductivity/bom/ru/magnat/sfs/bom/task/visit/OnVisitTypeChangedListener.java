package ru.magnat.sfs.bom.task.visit;

import ru.magnat.sfs.bom.IEventListener;

public interface OnVisitTypeChangedListener extends IEventListener {
	public void onVisitTypeChanged(TaskVisitEntity sender, int type);
}
