package ru.magnat.sfs.bom.task.workday;

import android.content.Context;
import android.widget.ListView;
import ru.magnat.sfs.bom.Globals;
import ru.magnat.sfs.bom.ref.user.RefUserEntity;
import ru.magnat.sfs.bom.task.generic.TaskGeneric;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.task.workday.TaskWorkdayEntityView;
import ru.magnat.sfs.view.WorkdayView;

public final class TaskWorkdayJournal extends TaskGeneric<TaskWorkdayEntity> implements IBO<TaskWorkdayJournal, TaskWorkdayEntity, RefUserEntity> {

	public TaskWorkdayJournal(Context context) {
		super(context, TaskWorkdayEntity.class);
	}

	public TaskWorkdayEntity Current() {
		return (TaskWorkdayEntity) super.Current();
	}

	public WorkdayView GetViewView(TaskWorkdayEntity entity) {
		return new WorkdayView(_context);
	} 

	public TaskWorkdayEntityView GetEditView(TaskWorkdayEntity entity) {
		return null;
	}

	public GenericListView<TaskWorkdayJournal, TaskWorkdayEntity, RefUserEntity> GetSelectView(RefUserEntity owner) {
		return null;
	}

	public GenericListView<TaskWorkdayJournal, TaskWorkdayEntity, RefUserEntity> GetListView(RefUserEntity owner) {
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
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
