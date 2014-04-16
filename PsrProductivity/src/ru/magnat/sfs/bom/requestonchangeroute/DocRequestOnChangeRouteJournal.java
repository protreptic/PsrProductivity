package ru.magnat.sfs.bom.requestonchangeroute;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.onchangeroute.DocRequestOnChangeRouteListView;
import ru.magnat.sfs.ui.android.doc.onchangeroute.DocRequestOnChangeRouteListViewItem;
import ru.magnat.sfs.ui.android.doc.onchangeroute.DocRequestOnChangeRouteView;
import android.content.Context;
import android.widget.ListView;

public final class DocRequestOnChangeRouteJournal
		extends
		DocGeneric<DocRequestOnChangeRouteEntity, DocRequestOnChangeRouteLineEntity>
		implements
		IBO<DocRequestOnChangeRouteJournal, DocRequestOnChangeRouteEntity, TaskWorkdayEntity> {
	public DocRequestOnChangeRouteJournal(Context context) {
		super(context, DocRequestOnChangeRouteEntity.class);
	}

	public GenericListView<DocRequestOnChangeRouteJournal, DocRequestOnChangeRouteEntity, TaskWorkdayEntity> GetSelectView(
			TaskWorkdayEntity owner) {

		return new DocRequestOnChangeRouteListView(_context, owner);
	}

	public SfsContentView GetViewView(DocRequestOnChangeRouteEntity entity) {
		return new DocRequestOnChangeRouteView(_context, this, entity);
	}

	public GenericListView<DocRequestOnChangeRouteJournal, DocRequestOnChangeRouteEntity, TaskWorkdayEntity> GetListView(
			TaskWorkdayEntity owner) {

		return new DocRequestOnChangeRouteListView(_context, owner);
	}

	public SfsContentView GetEditView(DocRequestOnChangeRouteEntity entity) {

		return new DocRequestOnChangeRouteView(_context, this, entity);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return new DocRequestOnChangeRouteListViewItem(_context, this, lv,
				Current());
	}

	public void selectByOutlet(RefOutletEntity outlet) {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Outlet", outlet.Id));
		super.Select(criteria, "ORDER BY ID DESC");

	}

	@Override
	protected GenericEntity<?> getGenericTaskEntity(String class_name, int id) {
		if (class_name.equals("MasterTask")) {
			return searchGenericEntity(TaskWorkdayEntity.class, id);
		}
		return null;
	}
}