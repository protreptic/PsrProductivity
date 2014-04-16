package ru.magnat.sfs.bom.task.workday;

import ru.magnat.sfs.bom.IEventListener;
import ru.magnat.sfs.bom.task.visit.TaskVisitEntity;

public interface OnOpenVisitRequest extends IEventListener {
	public void onOpenVisitRequest(TaskVisitEntity visit);
}
