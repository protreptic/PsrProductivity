package ru.magnat.sfs.ui.android.doc.creditrequest;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.creditrequest.DocCreditRequestEntity;
import ru.magnat.sfs.bom.creditrequest.DocCreditRequestJournal;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;

public class DocRequestOnChangeCreditListView extends GenericListView<DocCreditRequestJournal, DocCreditRequestEntity, TaskWorkdayEntity> {

	public DocRequestOnChangeCreditListView(Context context, TaskWorkdayEntity Workday) {
		super(context, null, Workday);
	}

	@Override
	public SfsContentView inflate() {
		requery();
		super.сatalogInflate(R.layout.doc_order_list_view, "Запрос на изменение типа кредита");
		showAddButton();
		
		return this;
	}

	protected void createRequest(TaskWorkdayEntity Workday) {
		DocCreditRequestJournal docs = new DocCreditRequestJournal(getContext());
		docs.NewEntity();
		DocCreditRequestEntity doc = docs.Current();

		if (Workday == null) {
			Dialogs.MessageBox("Запрос на кредитные условия можно создавать только в рамках рабочего дня");
			return;
		}

		doc.setDefaults(getContext(), Workday);

		if (!docs.save()) {
			Dialogs.MessageBox("Не удалось записать созданный запрос на изменение кредитных условий");
			return;
		}
		docs.close();
		OpenEntity(doc);
	}

	@Override
	protected void createEntity() {
		createRequest(_owner);
	}

	@Override
	protected void requery() {
		if (_catalog == null) {
			_catalog = new DocCreditRequestJournal(getContext());
		}
		_catalog.SetListType(SfsListType.EXTENDED_LIST);
		_catalog.Select(null, "ORDER BY Id DESC");
		_catalog.notifyDataSetChanged();
	}

}
