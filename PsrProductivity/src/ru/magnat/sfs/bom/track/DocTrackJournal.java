package ru.magnat.sfs.bom.track;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.SfsContentView;
import android.content.Context;
import android.view.View;
import android.widget.ListView;

@SuppressWarnings("rawtypes")
public final class DocTrackJournal extends
		DocGeneric<DocTrackEntity, DocTrackLineEntity> {

	public DocTrackJournal(Context context) {
		super(context, DocTrackEntity.class);
		
	}

	public DocTrackEntity Current() {
		DocTrackEntity entity = (DocTrackEntity) super.Current();

		return entity;
	}

	public void selectByWorkday(TaskWorkdayEntity entity) {

		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("MasterTask", entity.Id));
		super.Select(criteria, "ORDER BY ID DESC");

	}

	public View GetSelectView(GenericEntity owner) {
		
		return null;
	}

	public View GetViewView(GenericEntity entity) {
		
		return null;
	}

	public View GetListView(GenericEntity owner) {
		
		return null;
	}

	public View GetEditView(GenericEntity entity) {
		
		return null;
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		
		return null;
	}

	@Override
	protected GenericEntity<?> getGenericTaskEntity(String class_name, int id) {
		if (class_name.equals("MasterTask")) {
			return searchGenericEntity(TaskWorkdayEntity.class, id);
		}
		return null;
	}

}
