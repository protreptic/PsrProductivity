package ru.magnat.sfs.ui.android.doc.requestonchangeoutlet;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.requestonchangeoutlet.DocRequestOnChangeOutletEntity;
import ru.magnat.sfs.bom.requestonchangeoutlet.DocRequestOnChangeOutletJournal;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;

public class DocRequestOnChangeOutletListView
		extends
		GenericListView<DocRequestOnChangeOutletJournal, DocRequestOnChangeOutletEntity, TaskWorkdayEntity>

{

	public DocRequestOnChangeOutletListView(Context context,
			TaskWorkdayEntity Workday) {
		super(context, null, Workday);
	}

	@Override
	public SfsContentView inflate() {
		requery();
		super.сatalogInflate(R.layout.doc_order_list_view,
				"Запросы на изменение торговой точки");
		showAddButton();
		return this;
	}

	protected void CreateRequest(TaskWorkdayEntity Workday) {
		// DocOrderJournal docs = this._catalog;
		DocRequestOnChangeOutletJournal docs = new DocRequestOnChangeOutletJournal(
				getContext());
		docs.NewEntity();
		DocRequestOnChangeOutletEntity doc = docs.Current();

		if (Workday == null) {
			Dialogs.MessageBox("Запрос на добавление торговой точки можно создавать только в рамках рабочего дня");
			return;
		}

		doc.setDefaults(getContext(), Workday);

		if (!docs.save()) {
			Dialogs.MessageBox("Не удалось записать созданный запрос");
			return;
		}
		docs.close();
		OpenEntity(doc);
	}

	@Override
	protected void createEntity() {
		CreateRequest(_owner);
	}

	@Override
	protected void requery() {
		if (this._catalog == null)
			this._catalog = new DocRequestOnChangeOutletJournal(getContext());
		this._catalog.SetListType(SfsListType.EXTENDED_LIST);
		// if (this._owner!=null)
		// this._catalog.selectByContractor(this.);
		// else
		this._catalog.Select(null, "ORDER BY Id DESC");
		this._catalog.notifyDataSetChanged();

	}

}
