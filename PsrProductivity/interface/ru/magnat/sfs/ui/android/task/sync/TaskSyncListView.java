package ru.magnat.sfs.ui.android.task.sync;

import ru.magnat.sfs.bom.task.sync.TaskSyncEntity;
import ru.magnat.sfs.bom.task.sync.TaskSyncJournal;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import android.content.Context;

@Deprecated
public class TaskSyncListView extends GenericListView<TaskSyncJournal, TaskSyncEntity, TaskWorkdayEntity> {

	public TaskSyncListView(Context context, TaskWorkdayEntity owner) {
		super(context, null, owner);
	}

	@Override
	protected void createEntity() {} 

}
