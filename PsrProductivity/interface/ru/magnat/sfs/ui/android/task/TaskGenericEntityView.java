package ru.magnat.sfs.ui.android.task;

import ru.magnat.sfs.bom.task.generic.TaskGeneric;
import ru.magnat.sfs.bom.task.generic.TaskGenericEntity;
import ru.magnat.sfs.ui.android.GenericEntityView;
import android.content.Context;

@SuppressWarnings("rawtypes")
public class TaskGenericEntityView extends GenericEntityView {

	@SuppressWarnings("unchecked")
	public TaskGenericEntityView(Context context, TaskGeneric tasks,
			TaskGenericEntity task) {
		super(context, tasks, task);
	}

	public Boolean onBackPressed() {
		
		return null;
	}

	@Override
	public void moveTaskToBack(Boolean handled) {
		

	}

}
