package ru.magnat.sfs.bom.task.visit;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.task.generic.TaskGeneric;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayJournal;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.task.visit.TaskVisitEntityView;
import android.content.Context;
import android.widget.ListView;

public final class TaskVisitJournal extends TaskGeneric<TaskVisitEntity>
		implements IBO<TaskVisitJournal, TaskVisitEntity, TaskWorkdayEntity> {

	public TaskVisitJournal(Context context) {
		super(context, TaskVisitEntity.class);
	}

	public GenericListView<TaskVisitJournal,TaskVisitEntity,TaskWorkdayEntity> GetSelectView(TaskWorkdayEntity owner) {
		return null;
	}

	public GenericListView<TaskVisitJournal,TaskVisitEntity,TaskWorkdayEntity> GetListView(TaskWorkdayEntity owner) {
		return null;
	}
	public SfsContentView getCustomListView(TaskWorkdayEntity owner){
		return null;
	}
	public TaskVisitEntityView GetViewView(TaskVisitEntity task) {
		return new TaskVisitEntityView(_context, this, task);
	}

	public TaskVisitEntityView GetEditView(TaskVisitEntity task) {
		return new TaskVisitEntityView(_context, this, task);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return null;//new TaskVisitExtendedListItemView(_context, this, lv);
	}

	@Override
	protected GenericEntity<?> getGenericTaskEntity(String class_name, int id) {
		if (class_name.equals("MasterTask")) {
			return searchGenericEntity(TaskWorkdayJournal.class, id);
		}
		return null;
	}
}
