package ru.magnat.sfs.bom.creditrequest;

import java.util.ArrayList;

import ru.magnat.sfs.bom.GenericEntity;
import ru.magnat.sfs.bom.SqlCriteria;
import ru.magnat.sfs.bom.generic.DocGeneric;
import ru.magnat.sfs.bom.ref.contractor.RefContractorEntity;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.IBO;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.doc.creditrequest.DocRequestOnChangeCreditListView;
import ru.magnat.sfs.ui.android.doc.creditrequest.DocRequestOnChangeCreditListViewItem;
import ru.magnat.sfs.ui.android.doc.creditrequest.DocRequestOnChangeCreditView;
import android.content.Context;
import android.widget.ListView;

public final class DocCreditRequestJournal extends
		DocGeneric<DocCreditRequestEntity, DocCreditRequestLineEntity>
		implements
		IBO<DocCreditRequestJournal, DocCreditRequestEntity, TaskWorkdayEntity> {

	public DocCreditRequestJournal(Context context) {
		super(context, DocCreditRequestEntity.class);
	}

	public GenericListView<DocCreditRequestJournal, DocCreditRequestEntity, TaskWorkdayEntity> GetSelectView(
			TaskWorkdayEntity owner) {
		return new DocRequestOnChangeCreditListView(_context, owner);
	}

	public SfsContentView GetViewView(DocCreditRequestEntity entity) {
		return new DocRequestOnChangeCreditView(_context, this, entity);
	}

	public GenericListView<DocCreditRequestJournal, DocCreditRequestEntity, TaskWorkdayEntity> GetListView(
			TaskWorkdayEntity owner) {
		return new DocRequestOnChangeCreditListView(_context, owner);
	}

	public SfsContentView GetEditView(DocCreditRequestEntity entity) {
		return new DocRequestOnChangeCreditView(_context, this, entity);
	}

	@Override
	public SfsContentView GetExtendedListItemView(ListView lv) {
		return new DocRequestOnChangeCreditListViewItem(_context, this, lv,
				Current());
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
