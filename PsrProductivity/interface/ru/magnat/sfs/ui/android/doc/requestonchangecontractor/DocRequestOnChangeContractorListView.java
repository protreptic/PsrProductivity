package ru.magnat.sfs.ui.android.doc.requestonchangecontractor;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.requestonchangecontractor.DocRequestOnChangeContractorEntity;
import ru.magnat.sfs.bom.requestonchangecontractor.DocRequestOnChangeContractorJournal;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;

public class DocRequestOnChangeContractorListView
		extends
		GenericListView<DocRequestOnChangeContractorJournal, DocRequestOnChangeContractorEntity, TaskWorkdayEntity>

{

	public DocRequestOnChangeContractorListView(Context context,
			TaskWorkdayEntity Workday) {
		super(context, null, Workday);

	}

	@Override
	public SfsContentView inflate() {
		requery();
		super.сatalogInflate(R.layout.doc_order_list_view, "Запросы на изменение контрагента");
		showAddButton();
		return this;
	}

	protected void CreateOrder(TaskWorkdayEntity Workday) {

		// DocOrderJournal docs = this._catalog;
		DocRequestOnChangeContractorJournal docs = new DocRequestOnChangeContractorJournal(
				getContext());
		docs.NewEntity();
		DocRequestOnChangeContractorEntity doc = docs.Current();

		if (Workday == null) {
			Dialogs.MessageBox("Запрос на изменение контрагента можно создавать только в рамках рабочего дня");
			return;
		}

		doc.setDefaults(getContext(), Workday);

		if (!docs.save()) {
			Dialogs.MessageBox("Не удалось записать созданного контрагента");
			return;
		}
		docs.close();
		OpenEntity(doc);

	}

	@Override
	protected void createEntity() {
		CreateOrder(_owner);
	}

	@Override
	protected void requery() {
		if (this._catalog == null)
			this._catalog = new DocRequestOnChangeContractorJournal(
					getContext());
		this._catalog.SetListType(SfsListType.EXTENDED_LIST);
		// if (this._owner!=null)
		// this._catalog.selectByContractor(this.);
		// else
		this._catalog.Select(null, "ORDER BY Id DESC");
		this._catalog.notifyDataSetChanged();

	}

}
