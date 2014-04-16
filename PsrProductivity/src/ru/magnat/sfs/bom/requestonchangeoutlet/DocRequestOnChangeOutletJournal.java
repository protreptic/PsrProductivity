package ru.magnat.sfs.bom.requestonchangeoutlet;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.ref.outlet.RefOutletEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.requestonchangeoutlet.DocRequestOnChangeOutletListView;
import ru.magnat.sfs.ui.android.doc.requestonchangeoutlet.DocRequestOnChangeOutletListViewItem;
import ru.magnat.sfs.ui.android.doc.requestonchangeoutlet.DocRequestOnChangeOutletView;
import android.content.Context;
import android.widget.ListView;

public final class DocRequestOnChangeOutletJournal
		extends
		DocGeneric<DocRequestOnChangeOutletEntity, DocRequestOnChangeOutletLineEntity>
		implements
		IBO<DocRequestOnChangeOutletJournal, DocRequestOnChangeOutletEntity, TaskWorkdayEntity> {
	public DocRequestOnChangeOutletJournal(Context context) {
		super(context, DocRequestOnChangeOutletEntity.class);
	}

	public GenericListView<DocRequestOnChangeOutletJournal, DocRequestOnChangeOutletEntity, TaskWorkdayEntity> GetSelectView(
			TaskWorkdayEntity owner) {

		return new DocRequestOnChangeOutletListView(_context, owner);
	}

	public SfsContentView GetViewView(DocRequestOnChangeOutletEntity entity) {
		return new DocRequestOnChangeOutletView(_context, this, entity);
	}

	public GenericListView<DocRequestOnChangeOutletJournal, DocRequestOnChangeOutletEntity, TaskWorkdayEntity> GetListView(
			TaskWorkdayEntity owner) {

		return new DocRequestOnChangeOutletListView(_context, owner);
	}

	public SfsContentView GetEditView(DocRequestOnChangeOutletEntity entity) {

		return new DocRequestOnChangeOutletView(_context, this, entity);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {

		return new DocRequestOnChangeOutletListViewItem(_context, this, lv,
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