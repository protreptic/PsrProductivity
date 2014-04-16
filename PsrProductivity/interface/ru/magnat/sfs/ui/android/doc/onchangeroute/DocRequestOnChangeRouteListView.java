package ru.magnat.sfs.ui.android.doc.onchangeroute;

import ru.magnat.sfs.android.Dialogs;
import ru.magnat.sfs.android.R;
import ru.magnat.sfs.bom.requestonchangeroute.DocRequestOnChangeRouteEntity;
import ru.magnat.sfs.bom.requestonchangeroute.DocRequestOnChangeRouteJournal;
import ru.magnat.sfs.bom.task.workday.TaskWorkdayEntity;
import ru.magnat.sfs.ui.android.GenericListView;
import ru.magnat.sfs.ui.android.SfsContentView;
import ru.magnat.sfs.ui.android.SfsListType;
import android.content.Context;

public class DocRequestOnChangeRouteListView
		extends
		GenericListView<DocRequestOnChangeRouteJournal, DocRequestOnChangeRouteEntity, TaskWorkdayEntity>

{

	public DocRequestOnChangeRouteListView(Context context,
			TaskWorkdayEntity Workday) {
		super(context, null, Workday);

	}

	@Override
	public SfsContentView inflate() {
		requery();
		super.сatalogInflate(R.layout.doc_order_list_view,
				"Запросы на изменение маршрута");
		showAddButton();
		return this;
	}

	protected void CreateRequest(TaskWorkdayEntity Workday) {

		DocRequestOnChangeRouteJournal docs = new DocRequestOnChangeRouteJournal(
				getContext());
		docs.NewEntity();
		DocRequestOnChangeRouteEntity doc = docs.Current();

		if (Workday == null) {
			Dialogs.MessageBox("Запрос на добавление торговой точки в маршрут можно создавать только в рамках рабочего дня");
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
			this._catalog = new DocRequestOnChangeRouteJournal(getContext());
		this._catalog.SetListType(SfsListType.EXTENDED_LIST);
		// if (this._owner!=null)
		// this._catalog.selectByContractor(this.);
		// else
		this._catalog.Select(null, "ORDER BY Id DESC");
		this._catalog.notifyDataSetChanged();

	}

}
