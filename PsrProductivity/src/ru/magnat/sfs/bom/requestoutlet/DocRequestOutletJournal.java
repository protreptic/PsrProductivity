package ru.magnat.sfs.bom.requestoutlet;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.requestoutlet.DocRequestOutletListView;
import ru.magnat.sfs.ui.android.doc.requestoutlet.DocRequestOutletListViewItem;
import ru.magnat.sfs.ui.android.doc.requestoutlet.DocRequestOutletView;
import android.content.Context;
import android.widget.ListView;

public final class DocRequestOutletJournal
		extends
		DocGeneric<DocRequestOutletEntity, DocRequestOutletLineEntity>
		implements
		IBO<DocRequestOutletJournal, DocRequestOutletEntity, TaskWorkdayEntity> {
	public DocRequestOutletJournal(Context context) {
		super(context, DocRequestOutletEntity.class);
	}

	public GenericListView<DocRequestOutletJournal, DocRequestOutletEntity, TaskWorkdayEntity> GetSelectView(
			TaskWorkdayEntity owner) {

		return new DocRequestOutletListView(_context, owner);
	}

	public SfsContentView GetViewView(DocRequestOutletEntity entity) {
		return new DocRequestOutletView(_context, this, entity);
	}

	public GenericListView<DocRequestOutletJournal, DocRequestOutletEntity, TaskWorkdayEntity> GetListView(
			TaskWorkdayEntity owner) {

		return new DocRequestOutletListView(_context, owner);
	}

	public SfsContentView GetEditView(DocRequestOutletEntity entity) {

		return new DocRequestOutletView(_context, this, entity);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {

		return new DocRequestOutletListViewItem(_context, this, lv,
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