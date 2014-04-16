package ru.magnat.sfs.bom.task.sync;

import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.task.generic.TaskGeneric;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.task.sync.TaskSyncListView;
import android.content.Context;
import android.widget.ListView;

public final class TaskSyncJournal extends TaskGeneric<TaskSyncEntity> implements IBO<TaskSyncJournal, TaskSyncEntity, TaskWorkdayEntity> {

	public TaskSyncJournal(Context context) {
		super(context, TaskSyncEntity.class);
	}

	public TaskSyncEntity Current() {
		return (TaskSyncEntity) super.Current();
	}

	public TaskSyncListView GetListView(TaskWorkdayEntity owner) {
		return null;
	}

	public SfsContentView GetViewView(TaskSyncEntity task) {
		return null;
	}

	public SfsContentView GetEditView(TaskSyncEntity task) {
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;
	}

	public TaskSyncListView GetSelectView(TaskWorkdayEntity owner) {
		return null;
	}

	@Override
	public Boolean save() {
		Boolean result = super.save();
		if (result) {
			Globals.setOutletTargetUpdated(false);
			Globals.setEmployeeTargetUpdated(false);
		}
		return result;
	}
}
