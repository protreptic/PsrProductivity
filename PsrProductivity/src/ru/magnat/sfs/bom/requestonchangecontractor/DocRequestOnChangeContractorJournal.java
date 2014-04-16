package ru.magnat.sfs.bom.requestonchangecontractor;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.requestonchangecontractor.DocRequestOnChangeContractorListView;
import ru.magnat.sfs.ui.android.doc.requestonchangecontractor.DocRequestOnChangeContractorListViewItem;
import ru.magnat.sfs.ui.android.doc.requestonchangecontractor.DocRequestOnChangeContractorView;
import android.content.Context;
import android.widget.ListView;

public final class DocRequestOnChangeContractorJournal
		extends
		DocGeneric<DocRequestOnChangeContractorEntity, DocRequestOnChangeContractorLineEntity>
		implements
		IBO<DocRequestOnChangeContractorJournal, DocRequestOnChangeContractorEntity, TaskWorkdayEntity> {
	public DocRequestOnChangeContractorJournal(Context context) {
		super(context, DocRequestOnChangeContractorEntity.class);
	}

	public GenericListView<DocRequestOnChangeContractorJournal, DocRequestOnChangeContractorEntity, TaskWorkdayEntity> GetSelectView(
			TaskWorkdayEntity owner) {
		return new DocRequestOnChangeContractorListView(_context, owner);
	}

	public SfsContentView GetViewView(DocRequestOnChangeContractorEntity entity) {
		return new DocRequestOnChangeContractorView(_context, this, entity);
	}

	public GenericListView<DocRequestOnChangeContractorJournal, DocRequestOnChangeContractorEntity, TaskWorkdayEntity> GetListView(
			TaskWorkdayEntity owner) {
		return new DocRequestOnChangeContractorListView(_context, owner);
	}

	public SfsContentView GetEditView(DocRequestOnChangeContractorEntity entity) {
		return new DocRequestOnChangeContractorView(_context, this, entity);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return new DocRequestOnChangeContractorListViewItem(_context, this, lv,
				this.Current());
	}

	public void selectByContractor(RefContractorEntity entity) {
		ArrayList<SqlCriteria> criteria = new ArrayList<SqlCriteria>();
		criteria.add(new SqlCriteria("Contractor", entity.Id));
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